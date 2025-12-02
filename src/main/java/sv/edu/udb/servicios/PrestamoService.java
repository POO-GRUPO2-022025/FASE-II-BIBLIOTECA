package sv.edu.udb.servicios;

import sv.edu.udb.beans.DetallePrestamoDTO;
import sv.edu.udb.beans.Prestamo;
import sv.edu.udb.beans.Usuario;
import sv.edu.udb.beans.Material;
import sv.edu.udb.beans.Mora;
import sv.edu.udb.model.PrestamoModel;
import sv.edu.udb.model.UsuarioModel;
import sv.edu.udb.model.MaterialModel;
import sv.edu.udb.model.MoraModel;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio para gestionar todas las operaciones de préstamos de la biblioteca
 * Incluye: solicitud, aprobación, denegación, devolución, cálculo de moras y abonos
 */
public class PrestamoService {
    
    private final PrestamoModel prestamoModel;
    private final UsuarioModel usuarioModel;
    private final MaterialModel materialModel;
    private final MoraModel moraModel;
    
    // Límites de préstamos por tipo de usuario
    private static final int LIMITE_PRESTAMOS_ALUMNO = 3;
    private static final int LIMITE_PRESTAMOS_PROFESOR = 6;
    private static final int DIAS_PRESTAMO_DEFAULT = 15;
    
    public PrestamoService() {
        this.prestamoModel = new PrestamoModel();
        this.usuarioModel = new UsuarioModel();
        this.materialModel = new MaterialModel();
        this.moraModel = new MoraModel();
    }
    
    /**
     * Solicita un nuevo préstamo (estado: Pendiente)
     * Valida que el usuario no haya excedido su límite de préstamos activos
     */
    public Map<String, Object> solicitarPrestamo(int idUsuario, int idMaterial) {
        Map<String, Object> resultado = new HashMap<>();
        
        try {
            // Validar usuario
            Usuario usuario = usuarioModel.obtenerPorId(idUsuario);
            if (usuario == null) {
                resultado.put("success", false);
                resultado.put("mensaje", "Usuario no encontrado");
                return resultado;
            }
            
            // Validar material
            Material material = materialModel.obtenerPorId(idMaterial);
            if (material == null) {
                resultado.put("success", false);
                resultado.put("mensaje", "Material no encontrado");
                return resultado;
            }
            
            // Verificar disponibilidad del material
            if (material.getCantidadDisponible() <= 0) {
                resultado.put("success", false);
                resultado.put("mensaje", "Material no disponible. No hay unidades en existencia.");
                return resultado;
            }
            
            // Validar si puede solicitar préstamos (en una sola consulta SQL)
            PrestamoModel.ValidacionSolicitud validacion = prestamoModel.validarPuedeSolicitarPrestamo(idUsuario);
            
            // Verificar si tiene mora pendiente
            if (validacion.isTieneMora()) {
                resultado.put("success", false);
                resultado.put("mensaje", "Tiene mora pendiente. " +
                    "Debe pagar la mora antes de solicitar nuevos préstamos.");
                return resultado;
            }
            
            // Verificar si tiene devoluciones con retraso
            if (validacion.isTieneRetraso()) {
                resultado.put("success", false);
                resultado.put("mensaje", "Tiene préstamos con fecha de devolución vencida. " +
                    "Debe devolver los materiales atrasados antes de solicitar nuevos préstamos.");
                return resultado;
            }
            
            // Verificar límite de préstamos activos
            int prestamosActivos = validacion.getPrestamosActivos();
            int limiteMaximo = obtenerLimitePrestamos(usuario.getTipoUsuario());
            
            if (prestamosActivos >= limiteMaximo) {
                resultado.put("success", false);
                resultado.put("mensaje", "Ha alcanzado el límite de " + limiteMaximo + 
                    " préstamos activos. No puede solicitar más préstamos.");
                resultado.put("prestamosActivos", prestamosActivos);
                resultado.put("limiteMaximo", limiteMaximo);
                return resultado;
            }
            
            // Obtener la tarifa de mora correspondiente al tipo de usuario
            int anioActual = LocalDate.now().getYear();
            Mora tarifaMora = moraModel.selectByTipoUsuarioYAnio(
                usuario.getTipoUsuario().name(), anioActual);
            
            if (tarifaMora == null) {
                resultado.put("success", false);
                resultado.put("mensaje", "No se encontró tarifa de mora para el tipo de usuario en el año actual");
                return resultado;
            }
            
            // Crear nuevo préstamo
            Prestamo nuevoPrestamo = new Prestamo();
            nuevoPrestamo.setIdUsuario(idUsuario);
            nuevoPrestamo.setIdMaterial(idMaterial);
            nuevoPrestamo.setFechaPrestamo(Date.valueOf(LocalDate.now()));
            nuevoPrestamo.setIdMora(tarifaMora.getIdMora());
            nuevoPrestamo.setEstado(Prestamo.Estado.Pendiente);
            nuevoPrestamo.setMoraTotal(BigDecimal.ZERO);
            nuevoPrestamo.setFechaEstimada(null); // Se asignará al aprobar
            nuevoPrestamo.setFechaDevolucion(null);
            
            // Insertar en la base de datos
            Prestamo prestamoCreado = prestamoModel.insert(nuevoPrestamo);
            
            if (prestamoCreado != null) {
                // Reducir cantidad disponible del material
                material.setCantidadDisponible(material.getCantidadDisponible() - 1);
                boolean materialActualizado = materialModel.actualizar(material);
                
                if (!materialActualizado) {
                    // Si falla la actualización del material, intentar revertir el préstamo
                    prestamoModel.delete(prestamoCreado.getIdPrestamo());
                    resultado.put("success", false);
                    resultado.put("mensaje", "Error al actualizar la disponibilidad del material");
                    return resultado;
                }
                
                resultado.put("success", true);
                resultado.put("mensaje", "Préstamo solicitado correctamente. Espere la aprobación del administrador.");
                resultado.put("prestamo", prestamoCreado);
            } else {
                resultado.put("success", false);
                resultado.put("mensaje", "Error al crear el préstamo en la base de datos");
            }
            
        } catch (Exception e) {
            resultado.put("success", false);
            resultado.put("mensaje", "Error al procesar la solicitud: " + e.getMessage());
            e.printStackTrace();
        }
        
        return resultado;
    }
    
    /**
     * Aprueba un préstamo pendiente (cambia estado a En_Curso)
     * Establece la fecha de devolución estimada
     */
    public Map<String, Object> aprobarPrestamo(int idPrestamo, LocalDate fechaEstimadaDevolucion) {
        Map<String, Object> resultado = new HashMap<>();
        
        try {
            // Obtener el préstamo
            Prestamo prestamo = prestamoModel.select(idPrestamo);
            
            if (prestamo == null) {
                resultado.put("success", false);
                resultado.put("mensaje", "Préstamo no encontrado");
                return resultado;
            }
            
            // Validar que el préstamo esté en estado Pendiente
            if (prestamo.getEstado() != Prestamo.Estado.Pendiente) {
                resultado.put("success", false);
                resultado.put("mensaje", "Solo se pueden aprobar préstamos en estado Pendiente. Estado actual: " + 
                    prestamo.getEstado());
                return resultado;
            }
            
            // Establecer fecha de préstamo actual
            LocalDate fechaActual = LocalDate.now();
            prestamo.setFechaPrestamo(Date.valueOf(fechaActual));
            
            // Validar fecha de devolución
            if (fechaEstimadaDevolucion == null) {
                // Si no se proporciona fecha, usar default (5 días desde hoy)
                fechaEstimadaDevolucion = fechaActual.plusDays(5);
            }
            
            if (fechaEstimadaDevolucion.isBefore(fechaActual) || 
                fechaEstimadaDevolucion.isEqual(fechaActual)) {
                resultado.put("success", false);
                resultado.put("mensaje", "La fecha de devolución debe ser posterior a la fecha actual");
                return resultado;
            }
            
            // Actualizar el préstamo
            prestamo.setEstado(Prestamo.Estado.En_Curso);
            prestamo.setFechaEstimada(Date.valueOf(fechaEstimadaDevolucion));
            
            // Guardar en la base de datos
            if (prestamoModel.update(prestamo)) {
                resultado.put("success", true);
                resultado.put("mensaje", "Préstamo aprobado exitosamente");
                resultado.put("prestamo", prestamo);
                resultado.put("fechaEstimada", fechaEstimadaDevolucion);
            } else {
                resultado.put("success", false);
                resultado.put("mensaje", "Error al actualizar el préstamo en la base de datos");
            }
            
        } catch (Exception e) {
            resultado.put("success", false);
            resultado.put("mensaje", "Error al aprobar el préstamo: " + e.getMessage());
            e.printStackTrace();
        }
        
        return resultado;
    }
    
    /**
     * Deniega un préstamo pendiente (cambia estado a Denegado)
     * 
     * @param idPrestamo ID del préstamo a denegar
     * @return Map con resultado
     */
    public Map<String, Object> denegarPrestamo(int idPrestamo) {
        Map<String, Object> resultado = new HashMap<>();
        
        try {
            // Obtener el préstamo
            Prestamo prestamo = prestamoModel.select(idPrestamo);
            
            if (prestamo == null) {
                resultado.put("success", false);
                resultado.put("mensaje", "Préstamo no encontrado");
                return resultado;
            }
            
            // Validar que el préstamo esté en estado Pendiente
            if (prestamo.getEstado() != Prestamo.Estado.Pendiente) {
                resultado.put("success", false);
                resultado.put("mensaje", "Solo se pueden denegar préstamos en estado Pendiente. Estado actual: " + 
                    prestamo.getEstado());
                return resultado;
            }
            
            // Actualizar el préstamo
            prestamo.setEstado(Prestamo.Estado.Denegado);
            
            // Guardar en la base de datos
            if (prestamoModel.update(prestamo)) {
                // Devolver la unidad disponible al material
                Material material = materialModel.obtenerPorId(prestamo.getIdMaterial());
                if (material != null) {
                    material.setCantidadDisponible(material.getCantidadDisponible() + 1);
                    materialModel.actualizar(material);
                }
                
                resultado.put("success", true);
                resultado.put("mensaje", "Préstamo denegado exitosamente");
                resultado.put("prestamo", prestamo);
            } else {
                resultado.put("success", false);
                resultado.put("mensaje", "Error al actualizar el préstamo en la base de datos");
            }
            
        } catch (Exception e) {
            resultado.put("success", false);
            resultado.put("mensaje", "Error al denegar el préstamo: " + e.getMessage());
            e.printStackTrace();
        }
        
        return resultado;
    }
    
    /**
     * Registra la devolución de un préstamo (cambia estado a Devuelto)
     * Calcula automáticamente la mora si hay retraso
     */
    public Map<String, Object> registrarDevolucion(int idPrestamo) {
        Map<String, Object> resultado = new HashMap<>();
        
        try {
            // Obtener el préstamo
            Prestamo prestamo = prestamoModel.select(idPrestamo);
            
            if (prestamo == null) {
                resultado.put("success", false);
                resultado.put("mensaje", "Préstamo no encontrado");
                return resultado;
            }
            
            // Validar que el préstamo esté en estado En_Curso
            if (prestamo.getEstado() != Prestamo.Estado.En_Curso) {
                resultado.put("success", false);
                resultado.put("mensaje", "Solo se pueden devolver préstamos en estado En Curso. Estado actual: " + 
                    prestamo.getEstado());
                return resultado;
            }
            
            // Calcular mora si hay retraso
            LocalDate fechaDevolucion = LocalDate.now();
            LocalDate fechaEstimada = prestamo.getFechaEstimada().toLocalDate();
            BigDecimal moraTotal = BigDecimal.ZERO;
            int diasRetraso = 0;
            
            if (fechaDevolucion.isAfter(fechaEstimada)) {
                diasRetraso = (int) ChronoUnit.DAYS.between(fechaEstimada, fechaDevolucion);
                
                // Obtener la tarifa de mora
                Mora tarifaMora = moraModel.obtenerPorId(prestamo.getIdMora());
                
                if (tarifaMora != null && diasRetraso > 0) {
                    moraTotal = tarifaMora.getTarifaDiaria()
                        .multiply(BigDecimal.valueOf(diasRetraso));
                }
            }
            
            // Actualizar el préstamo
            prestamo.setEstado(Prestamo.Estado.Devuelto);
            prestamo.setFechaDevolucion(Date.valueOf(fechaDevolucion));
            prestamo.setMoraTotal(moraTotal);
            
            // Guardar en la base de datos
            if (prestamoModel.update(prestamo)) {
                // Incrementar cantidad disponible del material
                Material material = materialModel.obtenerPorId(prestamo.getIdMaterial());
                if (material != null) {
                    material.setCantidadDisponible(material.getCantidadDisponible() + 1);
                    materialModel.actualizar(material);
                }
                
                resultado.put("success", true);
                resultado.put("mensaje", "Devolución registrada exitosamente");
                resultado.put("prestamo", prestamo);
                resultado.put("fechaDevolucion", fechaDevolucion);
                resultado.put("diasRetraso", diasRetraso);
                resultado.put("moraTotal", moraTotal);
            } else {
                resultado.put("success", false);
                resultado.put("mensaje", "Error al actualizar el préstamo en la base de datos");
            }
            
        } catch (Exception e) {
            resultado.put("success", false);
            resultado.put("mensaje", "Error al registrar la devolución: " + e.getMessage());
            e.printStackTrace();
        }
        
        return resultado;
    }
    
    /**
     * Registra un abono a la mora de un préstamo
     */
    public Map<String, Object> abonarMora(int idPrestamo, BigDecimal montoAbonar) {
        Map<String, Object> resultado = new HashMap<>();
        
        try {
            // Validar monto
            if (montoAbonar == null || montoAbonar.compareTo(BigDecimal.ZERO) <= 0) {
                resultado.put("success", false);
                resultado.put("mensaje", "El monto debe ser mayor a cero");
                return resultado;
            }
            
            // Obtener el préstamo
            Prestamo prestamo = prestamoModel.select(idPrestamo);
            
            if (prestamo == null) {
                resultado.put("success", false);
                resultado.put("mensaje", "Préstamo no encontrado");
                return resultado;
            }
            
            // Verificar que el préstamo tenga mora
            if (prestamo.getMoraTotal() == null || 
                prestamo.getMoraTotal().compareTo(BigDecimal.ZERO) <= 0) {
                resultado.put("success", false);
                resultado.put("mensaje", "Este préstamo no tiene mora pendiente");
                return resultado;
            }
            
            // Validar que el monto no sea mayor a la mora actual
            if (montoAbonar.compareTo(prestamo.getMoraTotal()) > 0) {
                resultado.put("success", false);
                resultado.put("mensaje", "El monto a abonar no puede ser mayor a la mora actual ($" + 
                    prestamo.getMoraTotal() + ")");
                resultado.put("moraActual", prestamo.getMoraTotal());
                return resultado;
            }
            
            // Calcular nueva mora
            BigDecimal moraAnterior = prestamo.getMoraTotal();
            BigDecimal nuevaMora = moraAnterior.subtract(montoAbonar);
            
            // Actualizar la mora
            prestamo.setMoraTotal(nuevaMora);
            
            // Guardar en la base de datos
            if (prestamoModel.update(prestamo)) {
                resultado.put("success", true);
                resultado.put("mensaje", "Abono registrado exitosamente");
                resultado.put("prestamo", prestamo);
                resultado.put("moraAnterior", moraAnterior);
                resultado.put("montoAbonado", montoAbonar);
                resultado.put("nuevaMora", nuevaMora);
                resultado.put("moraSaldada", nuevaMora.compareTo(BigDecimal.ZERO) == 0);
            } else {
                resultado.put("success", false);
                resultado.put("mensaje", "Error al actualizar el préstamo en la base de datos");
            }
            
        } catch (Exception e) {
            resultado.put("success", false);
            resultado.put("mensaje", "Error al registrar el abono: " + e.getMessage());
            e.printStackTrace();
        }
        
        return resultado;
    }
    
    /**
     * Verifica si un usuario puede solicitar más préstamos
     */
    public Map<String, Object> verificarDisponibilidadPrestamo(int idUsuario) {
        Map<String, Object> resultado = new HashMap<>();
        
        try {
            // Obtener usuario
            Usuario usuario = usuarioModel.obtenerPorId(idUsuario);
            
            if (usuario == null) {
                resultado.put("puedeSolicitar", false);
                resultado.put("mensaje", "Usuario no encontrado");
                return resultado;
            }
            
            // Validar usando la consulta optimizada
            PrestamoModel.ValidacionSolicitud validacion = prestamoModel.validarPuedeSolicitarPrestamo(idUsuario);
            int prestamosActivos = validacion.getPrestamosActivos();
            int limiteMaximo = obtenerLimitePrestamos(usuario.getTipoUsuario());
            
            boolean puedeSolicitar = !validacion.isTieneMora() && !validacion.isTieneRetraso() && prestamosActivos < limiteMaximo;
            
            resultado.put("puedeSolicitar", puedeSolicitar);
            resultado.put("prestamosActivos", prestamosActivos);
            resultado.put("limiteMaximo", limiteMaximo);
            resultado.put("tipoUsuario", usuario.getTipoUsuario().name());
            
            if (validacion.isTieneMora()) {
                resultado.put("mensaje", "Tiene mora pendiente. Debe pagar antes de solicitar préstamos.");
            } else if (validacion.isTieneRetraso()) {
                resultado.put("mensaje", "Tiene préstamos con fecha de devolución vencida");
            } else if (!puedeSolicitar) {
                resultado.put("mensaje", "Ha alcanzado el límite de " + limiteMaximo + 
                    " préstamos activos");
            }
            
        } catch (Exception e) {
            resultado.put("puedeSolicitar", false);
            resultado.put("mensaje", "Error al verificar disponibilidad: " + e.getMessage());
            e.printStackTrace();
        }
        
        return resultado;
    }
    
    /**
     * Obtiene información completa y detallada de un préstamo para revisión
     * Incluye: datos del préstamo, usuario, material, tarifa de mora aplicable,
     * días de retraso actuales (si aplica), mora calculada, etc.
     */
    public DetallePrestamoDTO obtenerDetallePrestamo(int idPrestamo) {
        DetallePrestamoDTO detalle = new DetallePrestamoDTO();
        
        try {
            // 1. Obtener préstamo
            Prestamo prestamo = prestamoModel.select(idPrestamo);
            
            if (prestamo == null) {
                return null;
            }
            
            detalle.setPrestamo(prestamo);
            
            // 2. Obtener usuario
            Usuario usuario = usuarioModel.obtenerPorId(prestamo.getIdUsuario());
            detalle.setUsuario(usuario);
            
            // 3. Obtener material
            Material material = materialModel.obtenerPorId(prestamo.getIdMaterial());
            detalle.setMaterial(material);
            
            // 4. Obtener tarifa de mora
            Mora tarifaMora = moraModel.obtenerPorId(prestamo.getIdMora());
            detalle.setTarifa(tarifaMora);
            
            // 5. Calcular días de retraso y mora actual
            int diasRetraso = 0;
            BigDecimal moraCalculada = BigDecimal.ZERO;
            BigDecimal moraOriginal = BigDecimal.ZERO;
            boolean tieneRetraso = false;
            
            if (prestamo.getEstado() == Prestamo.Estado.En_Curso && prestamo.getFechaEstimada() != null) {
                LocalDate hoy = LocalDate.now();
                LocalDate fechaEstimada = prestamo.getFechaEstimada().toLocalDate();
                
                if (hoy.isAfter(fechaEstimada)) {
                    diasRetraso = (int) ChronoUnit.DAYS.between(fechaEstimada, hoy);
                    tieneRetraso = true;
                    
                    if (tarifaMora != null) {
                        moraCalculada = tarifaMora.getTarifaDiaria()
                            .multiply(BigDecimal.valueOf(diasRetraso));
                        moraOriginal = moraCalculada; // En curso, la original es la misma
                    }
                }
            } else if (prestamo.getEstado() == Prestamo.Estado.Devuelto) {
                // Si ya está devuelto, calcular la mora original y la actual
                if (prestamo.getFechaDevolucion() != null && prestamo.getFechaEstimada() != null) {
                    LocalDate fechaDevolucion = prestamo.getFechaDevolucion().toLocalDate();
                    LocalDate fechaEstimada = prestamo.getFechaEstimada().toLocalDate();
                    
                    if (fechaDevolucion.isAfter(fechaEstimada)) {
                        diasRetraso = (int) ChronoUnit.DAYS.between(fechaEstimada, fechaDevolucion);
                        tieneRetraso = true;
                        
                        // Calcular la mora original basada en los días de retraso
                        if (tarifaMora != null) {
                            moraOriginal = tarifaMora.getTarifaDiaria()
                                .multiply(BigDecimal.valueOf(diasRetraso));
                        }
                    }
                }
                
                // La mora actual es lo que está registrado en el sistema (después de abonos)
                moraCalculada = prestamo.getMoraTotal() != null ? prestamo.getMoraTotal() : BigDecimal.ZERO;
            }
            
            detalle.setDiasRetraso(diasRetraso);
            detalle.setMoraCalculada(moraCalculada);
            detalle.setMoraOriginal(moraOriginal);
            detalle.setTieneRetraso(tieneRetraso);
            
            // 6. Información adicional del usuario
            if (usuario != null) {
                PrestamoModel.ValidacionSolicitud validacion = prestamoModel.validarPuedeSolicitarPrestamo(usuario.getIdUsuario());
                int prestamosActivos = validacion.getPrestamosActivos();
                int limiteMaximo = obtenerLimitePrestamos(usuario.getTipoUsuario());
                detalle.setPrestamosActivosUsuario(prestamosActivos);
                detalle.setLimiteMaximoUsuario(limiteMaximo);
            }
            
            // 7. Determinar permisos de acciones según el estado
            if (prestamo.getEstado() == Prestamo.Estado.Pendiente) {
                detalle.setPuedeAprobar(true);
                detalle.setPuedeDenegar(true);
                detalle.setPuedeDevolver(false);
                detalle.setDiasPrestamoDefault(DIAS_PRESTAMO_DEFAULT);
            } else if (prestamo.getEstado() == Prestamo.Estado.En_Curso) {
                detalle.setPuedeAprobar(false);
                detalle.setPuedeDenegar(false);
                detalle.setPuedeDevolver(true);
            } else {
                // Devuelto o Denegado
                detalle.setPuedeAprobar(false);
                detalle.setPuedeDenegar(false);
                detalle.setPuedeDevolver(false);
            }
            
            // Puede abonar mora si hay mora pendiente
            BigDecimal moraPendiente = prestamo.getMoraTotal() != null ? prestamo.getMoraTotal() : BigDecimal.ZERO;
            detalle.setPuedeAbonarMora(moraPendiente.compareTo(BigDecimal.ZERO) > 0);
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
        return detalle;
    }
    
    /**
     * Obtiene el límite de préstamos según el tipo de usuario
     */
    private int obtenerLimitePrestamos(Usuario.TipoUsuario tipoUsuario) {
        switch (tipoUsuario) {
            case Profesor:
                return LIMITE_PRESTAMOS_PROFESOR;
            case Alumno:
            default:
                return LIMITE_PRESTAMOS_ALUMNO;
        }
    }
}

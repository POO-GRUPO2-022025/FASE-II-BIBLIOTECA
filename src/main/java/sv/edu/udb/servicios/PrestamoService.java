package sv.edu.udb.servicios;

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
     * 
     * @param idUsuario ID del usuario que solicita el préstamo
     * @param idMaterial ID del material a prestar
     * @return Map con resultado (success: boolean, mensaje: String, prestamo: Prestamo)
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
            
            // Verificar límite de préstamos activos del usuario
            int prestamosActivos = prestamoModel.contarPrestamosActivosPorUsuario(idUsuario);
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
     * 
     * @param idPrestamo ID del préstamo a aprobar
     * @param fechaEstimadaDevolucion Fecha estimada de devolución
     * @return Map con resultado
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
            
            // Validar fecha de devolución
            LocalDate fechaPrestamo = prestamo.getFechaPrestamo().toLocalDate();
            
            if (fechaEstimadaDevolucion == null) {
                // Si no se proporciona fecha, usar default (15 días)
                fechaEstimadaDevolucion = fechaPrestamo.plusDays(DIAS_PRESTAMO_DEFAULT);
            }
            
            if (fechaEstimadaDevolucion.isBefore(fechaPrestamo) || 
                fechaEstimadaDevolucion.isEqual(fechaPrestamo)) {
                resultado.put("success", false);
                resultado.put("mensaje", "La fecha de devolución debe ser posterior a la fecha de préstamo");
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
     * 
     * @param idPrestamo ID del préstamo a devolver
     * @return Map con resultado incluyendo mora calculada
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
     * 
     * @param idPrestamo ID del préstamo
     * @param montoAbonar Monto a abonar a la mora
     * @return Map con resultado
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
     * 
     * @param idUsuario ID del usuario
     * @return Map con resultado (puedeSolicitar: boolean, prestamosActivos: int, limiteMaximo: int)
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
            
            // Contar préstamos activos
            int prestamosActivos = prestamoModel.contarPrestamosActivosPorUsuario(idUsuario);
            int limiteMaximo = obtenerLimitePrestamos(usuario.getTipoUsuario());
            
            boolean puedeSolicitar = prestamosActivos < limiteMaximo;
            
            resultado.put("puedeSolicitar", puedeSolicitar);
            resultado.put("prestamosActivos", prestamosActivos);
            resultado.put("limiteMaximo", limiteMaximo);
            resultado.put("tipoUsuario", usuario.getTipoUsuario().name());
            
            if (!puedeSolicitar) {
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
     * 
     * IDEAL PARA: Mostrar antes de aprobar, denegar o devolver un préstamo
     * 
     * @param idPrestamo ID del préstamo a revisar
     * @return Map con toda la información del préstamo
     */
    public Map<String, Object> obtenerDetallePrestamo(int idPrestamo) {
        Map<String, Object> detalle = new HashMap<>();
        
        try {
            // 1. Obtener préstamo
            Prestamo prestamo = prestamoModel.select(idPrestamo);
            
            if (prestamo == null) {
                detalle.put("success", false);
                detalle.put("mensaje", "Préstamo no encontrado");
                return detalle;
            }
            
            // 2. Obtener usuario
            Usuario usuario = usuarioModel.obtenerPorId(prestamo.getIdUsuario());
            
            // 3. Obtener material
            Material material = materialModel.obtenerPorId(prestamo.getIdMaterial());
            
            // 4. Obtener tarifa de mora
            Mora tarifaMora = moraModel.obtenerPorId(prestamo.getIdMora());
            
            // 5. Calcular días de retraso y mora actual (si el préstamo está en curso)
            int diasRetraso = 0;
            BigDecimal moraCalculada = BigDecimal.ZERO;
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
                    }
                }
            } else if (prestamo.getEstado() == Prestamo.Estado.Devuelto) {
                // Si ya está devuelto, mostrar la mora que se cobró
                moraCalculada = prestamo.getMoraTotal() != null ? prestamo.getMoraTotal() : BigDecimal.ZERO;
                
                if (prestamo.getFechaDevolucion() != null && prestamo.getFechaEstimada() != null) {
                    LocalDate fechaDevolucion = prestamo.getFechaDevolucion().toLocalDate();
                    LocalDate fechaEstimada = prestamo.getFechaEstimada().toLocalDate();
                    
                    if (fechaDevolucion.isAfter(fechaEstimada)) {
                        diasRetraso = (int) ChronoUnit.DAYS.between(fechaEstimada, fechaDevolucion);
                        tieneRetraso = true;
                    }
                }
            }
            
            // 6. Calcular mora estimada si se aprueba hoy con 15 días
            BigDecimal moraEstimadaSiAprueba = BigDecimal.ZERO;
            if (prestamo.getEstado() == Prestamo.Estado.Pendiente && tarifaMora != null) {
                // Esta es solo una referencia, el administrador puede cambiar la fecha
                moraEstimadaSiAprueba = tarifaMora.getTarifaDiaria();
            }
            
            // 7. Construir respuesta detallada
            detalle.put("success", true);
            
            // Información del préstamo
            detalle.put("prestamo", prestamo);
            detalle.put("idPrestamo", prestamo.getIdPrestamo());
            detalle.put("estado", prestamo.getEstado().name());
            detalle.put("fechaPrestamo", prestamo.getFechaPrestamo());
            detalle.put("fechaEstimada", prestamo.getFechaEstimada());
            detalle.put("fechaDevolucion", prestamo.getFechaDevolucion());
            detalle.put("moraTotal", prestamo.getMoraTotal() != null ? prestamo.getMoraTotal() : BigDecimal.ZERO);
            
            // Información del usuario
            if (usuario != null) {
                detalle.put("usuario", usuario);
                detalle.put("nombreUsuario", usuario.getNombre());
                detalle.put("emailUsuario", usuario.getCorreo());
                detalle.put("tipoUsuario", usuario.getTipoUsuario().name());
                
                // Contar préstamos activos del usuario
                int prestamosActivos = prestamoModel.contarPrestamosActivosPorUsuario(usuario.getIdUsuario());
                int limiteMaximo = obtenerLimitePrestamos(usuario.getTipoUsuario());
                detalle.put("prestamosActivosUsuario", prestamosActivos);
                detalle.put("limiteMaximoUsuario", limiteMaximo);
            }
            
            // Información del material
            if (material != null) {
                detalle.put("material", material);
                detalle.put("tituloMaterial", material.getTitulo());
                detalle.put("tipoMaterial", material.getTipoMaterial().name());
                detalle.put("ubicacionMaterial", material.getUbicacion());
                detalle.put("cantidadDisponible", material.getCantidadDisponible());
            }
            
            // Información de la tarifa de mora
            if (tarifaMora != null) {
                detalle.put("tarifaMora", tarifaMora);
                detalle.put("tarifaDiaria", tarifaMora.getTarifaDiaria());
                detalle.put("anioTarifa", tarifaMora.getAnioAplicable());
                detalle.put("moraEstimadaPorDia", tarifaMora.getTarifaDiaria());
            }
            
            // Cálculos de retraso y mora
            detalle.put("diasRetraso", diasRetraso);
            detalle.put("moraCalculada", moraCalculada);
            detalle.put("tieneRetraso", tieneRetraso);
            
            // Información adicional según el estado
            if (prestamo.getEstado() == Prestamo.Estado.Pendiente) {
                detalle.put("puedeAprobar", true);
                detalle.put("puedeDenegar", true);
                detalle.put("puedeDevolver", false);
                detalle.put("moraEstimadaSiAprueba", moraEstimadaSiAprueba);
                detalle.put("diasPrestamoDefault", DIAS_PRESTAMO_DEFAULT);
            } else if (prestamo.getEstado() == Prestamo.Estado.En_Curso) {
                detalle.put("puedeAprobar", false);
                detalle.put("puedeDenegar", false);
                detalle.put("puedeDevolver", true);
                
                // Calcular mora si se devuelve hoy
                if (tarifaMora != null && tieneRetraso) {
                    detalle.put("moraAlDevolver", moraCalculada);
                } else {
                    detalle.put("moraAlDevolver", BigDecimal.ZERO);
                }
            } else if (prestamo.getEstado() == Prestamo.Estado.Devuelto) {
                detalle.put("puedeAprobar", false);
                detalle.put("puedeDenegar", false);
                detalle.put("puedeDevolver", false);
                detalle.put("yaDevuelto", true);
            } else if (prestamo.getEstado() == Prestamo.Estado.Denegado) {
                detalle.put("puedeAprobar", false);
                detalle.put("puedeDenegar", false);
                detalle.put("puedeDevolver", false);
                detalle.put("yaDenegado", true);
            }
            
            // Puede abonar mora si hay mora pendiente
            BigDecimal moraPendiente = prestamo.getMoraTotal() != null ? prestamo.getMoraTotal() : BigDecimal.ZERO;
            detalle.put("puedeAbonarMora", moraPendiente.compareTo(BigDecimal.ZERO) > 0);
            detalle.put("moraPendiente", moraPendiente);
            
        } catch (Exception e) {
            detalle.put("success", false);
            detalle.put("mensaje", "Error al obtener detalle del préstamo: " + e.getMessage());
            e.printStackTrace();
        }
        
        return detalle;
    }
    
    /**
     * Obtiene el límite de préstamos según el tipo de usuario
     * 
     * @param tipoUsuario Tipo de usuario (Alumno, Profesor, Administrador)
     * @return Límite de préstamos simultáneos
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

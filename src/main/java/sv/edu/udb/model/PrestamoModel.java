package sv.edu.udb.model;

import sv.edu.udb.beans.Prestamo;
import sv.edu.udb.beans.Prestamo.Estado;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrestamoModel {
    
    /**
     * Clase interna para retornar el resultado de la validación
     */
    public static class ValidacionSolicitud {
        private final int prestamosActivos;
        private final boolean tieneRetraso;
        private final boolean tieneMora;
        
        public ValidacionSolicitud(int prestamosActivos, boolean tieneRetraso, boolean tieneMora) {
            this.prestamosActivos = prestamosActivos;
            this.tieneRetraso = tieneRetraso;
            this.tieneMora = tieneMora;
        }
        
        public int getPrestamosActivos() {
            return prestamosActivos;
        }
        
        public boolean isTieneRetraso() {
            return tieneRetraso;
        }
        
        public boolean isTieneMora() {
            return tieneMora;
        }
    }
    
    private static final String SQL_INSERT = "INSERT INTO prestamos(id_usuario, id_material, id_mora, fecha_prestamo, fecha_estimada, fecha_devolucion, mora_total, estado) VALUES(?,?,?,?,?,?,?,?)";
    private static final String SQL_UPDATE = "UPDATE prestamos SET id_usuario=?, id_material=?, id_mora=?, fecha_prestamo=?, fecha_estimada=?, fecha_devolucion=?, mora_total=?, estado=? WHERE id_prestamo=?";
    private static final String SQL_UPDATE_ESTADO = "UPDATE prestamos SET estado=?, fecha_devolucion=?, mora_total=? WHERE id_prestamo=?";
    private static final String SQL_DELETE = "DELETE FROM prestamos WHERE id_prestamo=?";
    private static final String SQL_SELECT = "SELECT * FROM prestamos WHERE id_prestamo=?";
    
    // SQL optimizada para validar si un usuario puede solicitar préstamos (una sola consulta)
    // Retorna: prestamos_activos (count), tiene_retraso (0 o 1), tiene_mora (0 o 1)
    private static final String SQL_VALIDAR_PUEDE_SOLICITAR = 
        "SELECT " +
        "  (SELECT COUNT(*) FROM prestamos WHERE id_usuario=? AND estado IN ('Pendiente', 'En_Curso')) as prestamos_activos, " +
        "  (SELECT COUNT(*) FROM prestamos WHERE id_usuario=? AND estado='En_Curso' AND fecha_estimada < CURDATE()) as tiene_retraso, " +
        "  (SELECT COUNT(*) FROM prestamos WHERE id_usuario=? AND mora_total > 0) as tiene_mora";
    
    // SQL con JOINs para obtener información completa
    private static final String SQL_SELECT_ALL_WITH_INFO = 
        "SELECT p.*, u.nombre as nombre_usuario, m.titulo as titulo_material, m.tipo_material as tipo_material, " +
        "g.nombre as genero_nombre " +
        "FROM prestamos p " +
        "LEFT JOIN usuarios u ON p.id_usuario = u.id_usuario " +
        "LEFT JOIN materiales m ON p.id_material = m.id_material " +
        "LEFT JOIN libros l ON m.id_material = l.id_material " +
        "LEFT JOIN genero g ON l.id_genero = g.id_genero " +
        "ORDER BY p.id_prestamo DESC";
    
    private static final String SQL_SELECT_BY_USER_WITH_INFO = 
        "SELECT p.*, u.nombre as nombre_usuario, m.titulo as titulo_material, m.tipo_material as tipo_material, " +
        "g.nombre as genero_nombre " +
        "FROM prestamos p " +
        "LEFT JOIN usuarios u ON p.id_usuario = u.id_usuario " +
        "LEFT JOIN materiales m ON p.id_material = m.id_material " +
        "LEFT JOIN libros l ON m.id_material = l.id_material " +
        "LEFT JOIN genero g ON l.id_genero = g.id_genero " +
        "WHERE p.id_usuario = ? " +
        "ORDER BY p.id_prestamo DESC";
    
    // SQL base para filtros dinámicos (similar a selectPrestamosDetalladoFiltrado)
    private static final String SQL_SELECT_FILTRADO_BASE = 
        "SELECT p.*, u.nombre as nombre_usuario, m.titulo as titulo_material, m.tipo_material as tipo_material, " +
        "g.nombre as genero_nombre " +
        "FROM prestamos p " +
        "LEFT JOIN usuarios u ON p.id_usuario = u.id_usuario " +
        "LEFT JOIN materiales m ON p.id_material = m.id_material " +
        "LEFT JOIN libros l ON m.id_material = l.id_material " +
        "LEFT JOIN genero g ON l.id_genero = g.id_genero ";

    /**
     * Inserta un nuevo préstamo y retorna el préstamo con su ID generado
     * OPTIMIZADO: Retorna el objeto completo sin hacer SELECT adicional (patrón PrestamosDB)
     */
    public Prestamo insert(Prestamo prestamo) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Prestamo nuevoPrestamo = null;
        
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, prestamo.getIdUsuario());
            stmt.setInt(2, prestamo.getIdMaterial());
            stmt.setInt(3, prestamo.getIdMora());
            
            // Validar null para fecha_prestamo
            if (prestamo.getFechaPrestamo() != null) {
                stmt.setDate(4, prestamo.getFechaPrestamo());
            } else {
                stmt.setNull(4, java.sql.Types.DATE);
            }
            
            // Validar null para fecha_estimada
            if (prestamo.getFechaEstimada() != null) {
                stmt.setDate(5, prestamo.getFechaEstimada());
            } else {
                stmt.setNull(5, java.sql.Types.DATE);
            }
            
            // Validar null para fecha_devolucion
            if (prestamo.getFechaDevolucion() != null) {
                stmt.setDate(6, prestamo.getFechaDevolucion());
            } else {
                stmt.setNull(6, java.sql.Types.DATE);
            }
            
            stmt.setBigDecimal(7, prestamo.getMoraTotal());
            stmt.setString(8, prestamo.getEstado().name());
            
            stmt.executeUpdate();
            
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                // Retornar el mismo préstamo con el ID generado (sin SELECT adicional)
                nuevoPrestamo = new Prestamo();
                nuevoPrestamo.setIdPrestamo(rs.getInt(1));
                nuevoPrestamo.setIdUsuario(prestamo.getIdUsuario());
                nuevoPrestamo.setIdMaterial(prestamo.getIdMaterial());
                nuevoPrestamo.setIdMora(prestamo.getIdMora());
                nuevoPrestamo.setFechaPrestamo(prestamo.getFechaPrestamo());
                nuevoPrestamo.setFechaEstimada(prestamo.getFechaEstimada());
                nuevoPrestamo.setFechaDevolucion(prestamo.getFechaDevolucion());
                nuevoPrestamo.setMoraTotal(prestamo.getMoraTotal());
                nuevoPrestamo.setEstado(prestamo.getEstado());
            }
            
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al insertar préstamo", e);
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        
        return nuevoPrestamo;
    }

    /**
     * Actualiza un préstamo completo
     * Maneja fechas NULL explícitamente
     */
    public boolean update(Prestamo prestamo) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean retorno = false;
        
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_UPDATE);
            stmt.setInt(1, prestamo.getIdUsuario());
            stmt.setInt(2, prestamo.getIdMaterial());
            stmt.setInt(3, prestamo.getIdMora());
            
            // Validar null para fecha_prestamo
            if (prestamo.getFechaPrestamo() != null) {
                stmt.setDate(4, prestamo.getFechaPrestamo());
            } else {
                stmt.setNull(4, java.sql.Types.DATE);
            }
            
            // Validar null para fecha_estimada
            if (prestamo.getFechaEstimada() != null) {
                stmt.setDate(5, prestamo.getFechaEstimada());
            } else {
                stmt.setNull(5, java.sql.Types.DATE);
            }
            
            // Validar null para fecha_devolucion
            if (prestamo.getFechaDevolucion() != null) {
                stmt.setDate(6, prestamo.getFechaDevolucion());
            } else {
                stmt.setNull(6, java.sql.Types.DATE);
            }
            
            stmt.setBigDecimal(7, prestamo.getMoraTotal());
            stmt.setString(8, prestamo.getEstado().name());
            stmt.setInt(9, prestamo.getIdPrestamo());
            
            int filas = stmt.executeUpdate();
            retorno = filas > 0;
            
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al actualizar préstamo", e);
        } finally {
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        
        return retorno;
    }

    /**
     * Actualiza el estado de un préstamo
     */
    public boolean actualizarEstado(int idPrestamo, Estado nuevoEstado, Date fechaDevolucion, BigDecimal moraTotal) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean retorno = false;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_UPDATE_ESTADO);
            stmt.setString(1, nuevoEstado.name());
            stmt.setDate(2, fechaDevolucion);
            stmt.setBigDecimal(3, moraTotal);
            stmt.setInt(4, idPrestamo);
            
            int filas = stmt.executeUpdate();
            retorno = filas > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return retorno;
    }

    /**
     * Elimina un préstamo
     */
    public boolean delete(int idPrestamo) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean retorno = false;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_DELETE);
            stmt.setInt(1, idPrestamo);
            int filas = stmt.executeUpdate();
            retorno = filas > 0;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return retorno;
    }

    /**
     * Obtiene un préstamo por su ID
     */
    public Prestamo select(int idPrestamo) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Prestamo prestamo = null;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_SELECT);
            stmt.setInt(1, idPrestamo);
            rs = stmt.executeQuery();
            if (rs.next()) {
                prestamo = new Prestamo();
                prestamo.setIdPrestamo(rs.getInt("id_prestamo"));
                prestamo.setIdUsuario(rs.getInt("id_usuario"));
                prestamo.setIdMaterial(rs.getInt("id_material"));
                prestamo.setIdMora(rs.getInt("id_mora"));
                prestamo.setFechaPrestamo(rs.getDate("fecha_prestamo"));
                prestamo.setFechaEstimada(rs.getDate("fecha_estimada"));
                prestamo.setFechaDevolucion(rs.getDate("fecha_devolucion"));
                prestamo.setMoraTotal(rs.getBigDecimal("mora_total"));
                prestamo.setEstado(Estado.valueOf(rs.getString("estado")));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return prestamo;
    }

    /**
     * Valida si un usuario puede solicitar un nuevo préstamo
     * Verifica en una sola consulta SQL:
     * 1. Cantidad de préstamos activos (Pendiente + En_Curso)
     * 2. Si tiene préstamos con retraso (fecha_estimada vencida)
     * 3. Si tiene mora pendiente (mora_total > 0)
     */
    public ValidacionSolicitud validarPuedeSolicitarPrestamo(int idUsuario) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_VALIDAR_PUEDE_SOLICITAR);
            stmt.setInt(1, idUsuario); // Para contar préstamos activos
            stmt.setInt(2, idUsuario); // Para verificar retrasos
            stmt.setInt(3, idUsuario); // Para verificar mora pendiente
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                int prestamosActivos = rs.getInt("prestamos_activos");
                int countRetraso = rs.getInt("tiene_retraso");
                int countMora = rs.getInt("tiene_mora");
                return new ValidacionSolicitud(prestamosActivos, countRetraso > 0, countMora > 0);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        
        return new ValidacionSolicitud(0, false, false);
    }

    /**
     * Filtra préstamos con información completa usando query dinámica
     */
    public List<Prestamo> selectPrestamosFiltrados(String estado, String tipoMaterial, boolean conMora) {
        List<Prestamo> prestamos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        // Construir query dinámicamente según filtros
        StringBuilder sql = new StringBuilder(SQL_SELECT_FILTRADO_BASE);
        
        boolean hayFiltros = false;
        
        // Filtro por tipo de material
        if (tipoMaterial != null && !tipoMaterial.isEmpty() && !tipoMaterial.equals("Todos")) {
            sql.append("WHERE m.tipo_material = ? ");
            hayFiltros = true;
        }
        
        // Filtro por estado
        if (estado != null && !estado.isEmpty() && !estado.equals("Todos")) {
            if (hayFiltros) {
                sql.append("AND p.estado = ? ");
            } else {
                sql.append("WHERE p.estado = ? ");
                hayFiltros = true;
            }
        }
        
        // Filtro por mora
        if (conMora) {
            if (hayFiltros) {
                sql.append("AND (p.mora_total > 0 OR (p.estado = 'En_Curso' AND p.fecha_estimada < CURDATE())) ");
            } else {
                sql.append("WHERE (p.mora_total > 0 OR (p.estado = 'En_Curso' AND p.fecha_estimada < CURDATE())) ");
            }
        }
        
        sql.append("ORDER BY p.id_prestamo DESC");
        
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(sql.toString());
            
            // Establecer parámetros según filtros activos
            int paramIndex = 1;
            if (tipoMaterial != null && !tipoMaterial.isEmpty() && !tipoMaterial.equals("Todos")) {
                stmt.setString(paramIndex++, tipoMaterial);
            }
            if (estado != null && !estado.isEmpty() && !estado.equals("Todos")) {
                stmt.setString(paramIndex, estado);
            }
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Prestamo prestamo = new Prestamo();
                prestamo.setIdPrestamo(rs.getInt("id_prestamo"));
                prestamo.setIdUsuario(rs.getInt("id_usuario"));
                prestamo.setIdMaterial(rs.getInt("id_material"));
                prestamo.setIdMora(rs.getInt("id_mora"));
                prestamo.setFechaPrestamo(rs.getDate("fecha_prestamo"));
                prestamo.setFechaEstimada(rs.getDate("fecha_estimada"));
                prestamo.setFechaDevolucion(rs.getDate("fecha_devolucion"));
                prestamo.setMoraTotal(rs.getBigDecimal("mora_total"));
                prestamo.setEstado(Estado.valueOf(rs.getString("estado")));
                
                // Campos adicionales del JOIN
                prestamo.setNombreUsuario(rs.getString("nombre_usuario"));
                prestamo.setTituloMaterial(rs.getString("titulo_material"));
                prestamo.setTipoMaterial(rs.getString("tipo_material"));
                prestamo.setGeneroNombre(rs.getString("genero_nombre"));
                
                prestamos.add(prestamo);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        
        return prestamos;
    }
    
    /**
     * Obtiene todos los préstamos con información del usuario y material (mediante JOINs)
     */
    public List<Prestamo> selectAllWithInfo() {
        List<Prestamo> prestamos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_SELECT_ALL_WITH_INFO);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Prestamo prestamo = new Prestamo();
                prestamo.setIdPrestamo(rs.getInt("id_prestamo"));
                prestamo.setIdUsuario(rs.getInt("id_usuario"));
                prestamo.setIdMaterial(rs.getInt("id_material"));
                prestamo.setIdMora(rs.getInt("id_mora"));
                prestamo.setFechaPrestamo(rs.getDate("fecha_prestamo"));
                prestamo.setFechaEstimada(rs.getDate("fecha_estimada"));
                prestamo.setFechaDevolucion(rs.getDate("fecha_devolucion"));
                prestamo.setMoraTotal(rs.getBigDecimal("mora_total"));
                prestamo.setEstado(Estado.valueOf(rs.getString("estado")));
                // Información adicional del JOIN
                prestamo.setNombreUsuario(rs.getString("nombre_usuario"));
                prestamo.setTituloMaterial(rs.getString("titulo_material"));
                prestamo.setTipoMaterial(rs.getString("tipo_material"));
                prestamo.setGeneroNombre(rs.getString("genero_nombre"));
                prestamos.add(prestamo);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return prestamos;
    }
    
    /**
     * Obtiene préstamos de un usuario con información del material (mediante JOINs)
     */
    public List<Prestamo> selectByUsuarioWithInfo(int idUsuario) {
        List<Prestamo> prestamos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_SELECT_BY_USER_WITH_INFO);
            stmt.setInt(1, idUsuario);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Prestamo prestamo = new Prestamo();
                prestamo.setIdPrestamo(rs.getInt("id_prestamo"));
                prestamo.setIdUsuario(rs.getInt("id_usuario"));
                prestamo.setIdMaterial(rs.getInt("id_material"));
                prestamo.setIdMora(rs.getInt("id_mora"));
                prestamo.setFechaPrestamo(rs.getDate("fecha_prestamo"));
                prestamo.setFechaEstimada(rs.getDate("fecha_estimada"));
                prestamo.setFechaDevolucion(rs.getDate("fecha_devolucion"));
                prestamo.setMoraTotal(rs.getBigDecimal("mora_total"));
                prestamo.setEstado(Estado.valueOf(rs.getString("estado")));
                // Información adicional del JOIN
                prestamo.setNombreUsuario(rs.getString("nombre_usuario"));
                prestamo.setTituloMaterial(rs.getString("titulo_material"));
                prestamo.setTipoMaterial(rs.getString("tipo_material"));
                prestamo.setGeneroNombre(rs.getString("genero_nombre"));
                prestamos.add(prestamo);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return prestamos;
    }
}

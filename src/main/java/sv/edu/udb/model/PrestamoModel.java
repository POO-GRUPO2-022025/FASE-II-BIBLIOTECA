package sv.edu.udb.model;

import sv.edu.udb.beans.Prestamo;
import sv.edu.udb.beans.Prestamo.Estado;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrestamoModel {
    
    private static final String SQL_INSERT = "INSERT INTO prestamos(id_usuario, id_material, id_mora, fecha_prestamo, fecha_estimada, fecha_devolucion, mora_total, estado) VALUES(?,?,?,?,?,?,?,?)";
    private static final String SQL_UPDATE = "UPDATE prestamos SET id_usuario=?, id_material=?, id_mora=?, fecha_prestamo=?, fecha_estimada=?, fecha_devolucion=?, mora_total=?, estado=? WHERE id_prestamo=?";
    private static final String SQL_UPDATE_ESTADO = "UPDATE prestamos SET estado=?, fecha_devolucion=?, mora_total=? WHERE id_prestamo=?";
    private static final String SQL_DELETE = "DELETE FROM prestamos WHERE id_prestamo=?";
    private static final String SQL_SELECT = "SELECT * FROM prestamos WHERE id_prestamo=?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM prestamos ORDER BY id_prestamo";
    private static final String SQL_SELECT_BY_USER = "SELECT * FROM prestamos WHERE id_usuario=? ORDER BY id_prestamo DESC";
    private static final String SQL_SELECT_BY_STATE = "SELECT * FROM prestamos WHERE estado=? ORDER BY id_prestamo DESC";
    private static final String SQL_SELECT_ACTIVE_BY_USER = "SELECT * FROM prestamos WHERE id_usuario=? AND estado IN ('Pendiente', 'En_Curso') ORDER BY id_prestamo DESC";
    private static final String SQL_COUNT_ACTIVE_BY_USER = "SELECT COUNT(*) FROM prestamos WHERE id_usuario=? AND estado IN ('Pendiente', 'En_Curso')";
    private static final String SQL_SELECT_WITH_MORA = "SELECT * FROM prestamos WHERE mora_total > 0 ORDER BY id_prestamo DESC";
    
    // SQL con JOINs para obtener información completa
    private static final String SQL_SELECT_ALL_WITH_INFO = 
        "SELECT p.*, u.nombre as nombre_usuario, m.titulo as titulo_material, m.tipo_material as tipo_material " +
        "FROM prestamos p " +
        "LEFT JOIN usuarios u ON p.id_usuario = u.id_usuario " +
        "LEFT JOIN materiales m ON p.id_material = m.id_material " +
        "ORDER BY p.id_prestamo DESC";
    
    private static final String SQL_SELECT_BY_USER_WITH_INFO = 
        "SELECT p.*, u.nombre as nombre_usuario, m.titulo as titulo_material, m.tipo_material as tipo_material " +
        "FROM prestamos p " +
        "LEFT JOIN usuarios u ON p.id_usuario = u.id_usuario " +
        "LEFT JOIN materiales m ON p.id_material = m.id_material " +
        "WHERE p.id_usuario = ? " +
        "ORDER BY p.id_prestamo DESC";

    /**
     * Inserta un nuevo préstamo y retorna el préstamo con su ID generado
     */
    public Prestamo insert(Prestamo prestamo) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Prestamo resultado = null;
        
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, prestamo.getIdUsuario());
            stmt.setInt(2, prestamo.getIdMaterial());
            stmt.setInt(3, prestamo.getIdMora());
            stmt.setDate(4, prestamo.getFechaPrestamo());
            stmt.setDate(5, prestamo.getFechaEstimada());
            stmt.setDate(6, prestamo.getFechaDevolucion());
            stmt.setBigDecimal(7, prestamo.getMoraTotal());
            stmt.setString(8, prestamo.getEstado().name());
            
            int rows = stmt.executeUpdate();
            
            if (rows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    resultado = select(idGenerado);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return resultado;
    }

    /**
     * Actualiza un préstamo completo
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
            stmt.setDate(4, prestamo.getFechaPrestamo());
            stmt.setDate(5, prestamo.getFechaEstimada());
            stmt.setDate(6, prestamo.getFechaDevolucion());
            stmt.setBigDecimal(7, prestamo.getMoraTotal());
            stmt.setString(8, prestamo.getEstado().name());
            stmt.setInt(9, prestamo.getIdPrestamo());
            
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
     * Obtiene todos los préstamos
     */
    public List<Prestamo> selectAll() {
        List<Prestamo> prestamos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_SELECT_ALL);
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
     * Obtiene préstamos por usuario
     */
    public List<Prestamo> selectByUsuario(int idUsuario) {
        List<Prestamo> prestamos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_SELECT_BY_USER);
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
     * Obtiene préstamos activos (Pendiente o En_Curso) por usuario
     */
    public List<Prestamo> selectPrestamosActivosPorUsuario(int idUsuario) {
        List<Prestamo> prestamos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_SELECT_ACTIVE_BY_USER);
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
     * Obtiene préstamos por estado
     */
    public List<Prestamo> selectPrestamosPorEstado(String estado) {
        List<Prestamo> prestamos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_SELECT_BY_STATE);
            stmt.setString(1, estado);  
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
     * Cuenta los préstamos activos (Pendiente o En_Curso) de un usuario
     */
    public int contarPrestamosActivosPorUsuario(int idUsuario) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int count = 0;
        
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_COUNT_ACTIVE_BY_USER);
            stmt.setInt(1, idUsuario);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return count;
    }

    /**
     * Obtiene todos los préstamos con mora pendiente
     */
    public List<Prestamo> selectPrestamosConMora() {
        List<Prestamo> prestamos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_SELECT_WITH_MORA);
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

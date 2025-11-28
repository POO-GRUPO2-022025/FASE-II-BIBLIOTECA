package sv.edu.udb.model;

import sv.edu.udb.beans.Editorial;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EditorialModel {
    
    private static final String SQL_INSERT = "INSERT INTO editoriales(nombre, pais) VALUES(?,?)";
    private static final String SQL_UPDATE = "UPDATE editoriales SET nombre=?, pais=? WHERE id_editorial=?";
    private static final String SQL_DELETE = "DELETE FROM editoriales WHERE id_editorial = ?";
    private static final String SQL_SELECT = "SELECT id_editorial, nombre, pais FROM editoriales ORDER BY id_editorial";
    private static final String SQL_SELECT_BY_ID = "SELECT id_editorial, nombre, pais FROM editoriales WHERE id_editorial = ?";

    /**
     * Inserta una nueva editorial
     */
    public int insertar(Editorial editorial) {
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_INSERT);
            stmt.setString(1, editorial.getNombre());
            stmt.setString(2, editorial.getPais());
            rows = stmt.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return rows;
    }

    /**
     * Actualiza una editorial existente
     */
    public int actualizar(Editorial editorial) {
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_UPDATE);
            stmt.setString(1, editorial.getNombre());
            stmt.setString(2, editorial.getPais());
            stmt.setInt(3, editorial.getIdEditorial());
            rows = stmt.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return rows;
    }

    /**
     * Elimina una editorial
     */
    public int eliminar(int idEditorial) {
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_DELETE);
            stmt.setInt(1, idEditorial);
            rows = stmt.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return rows;
    }

    /**
     * Obtiene una editorial por su ID
     */
    public Editorial obtenerPorId(int idEditorial) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Editorial editorial = null;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_SELECT_BY_ID);
            stmt.setInt(1, idEditorial);
            rs = stmt.executeQuery();
            if (rs.next()) {
                editorial = new Editorial();
                editorial.setIdEditorial(rs.getInt("id_editorial"));
                editorial.setNombre(rs.getString("nombre"));
                editorial.setPais(rs.getString("pais"));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return editorial;
    }

    /**
     * Obtiene todas las editoriales
     */
    public List<Editorial> listarTodas() {
        List<Editorial> editoriales = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_SELECT);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Editorial editorial = new Editorial();
                editorial.setIdEditorial(rs.getInt("id_editorial"));
                editorial.setNombre(rs.getString("nombre"));
                editorial.setPais(rs.getString("pais"));
                editoriales.add(editorial);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return editoriales;
    }
}

package sv.edu.udb.model;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import sv.edu.udb.beans.Genero;

public class GeneroModel {
    
    private static final String SQL_INSERT = "INSERT INTO genero(nombre, descripcion) VALUES(?,?)";
    private static final String SQL_UPDATE = "UPDATE genero SET nombre=?, descripcion=? WHERE id_genero=?";
    private static final String SQL_DELETE = "DELETE FROM genero WHERE id_genero = ?";
    private static final String SQL_SELECT = "SELECT id_genero, nombre, descripcion FROM genero ORDER BY id_genero";
    private static final String SQL_SELECT_BY_ID = "SELECT id_genero, nombre, descripcion FROM genero WHERE id_genero = ?";
    
    /**
     * 
     */
    public int insertar(Genero genero) {
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_INSERT);
            stmt.setString(1, genero.getNombre());
            stmt.setString(2, genero.getDescripcion());
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
     * 
     */
    public int actualizar(Genero genero) {
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_UPDATE);
            stmt.setString(1, genero.getNombre());
            stmt.setString(2, genero.getDescripcion());
            stmt.setInt(3, genero.getIdGenero());
            
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
     * 
     */
    public int eliminar(int idGenero) {
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_DELETE);
            stmt.setInt(1, idGenero);
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
     * 
     */
    public Genero obtenerPorId(int idGenero) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Genero genero = null;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_SELECT_BY_ID);
            stmt.setInt(1, idGenero);
            rs = stmt.executeQuery();
            if (rs.next()) {
                genero = new Genero();
                genero.setIdGenero(rs.getInt("id_genero"));
                genero.setNombre(rs.getString("nombre"));
                genero.setDescripcion(rs.getString("descripcion"));
                
            
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return genero;
    }

    /**

     */
    public List<Genero> listarTodas() {
        List<Genero> ListaGenero = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_SELECT);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Genero genero = new Genero();
                genero.setIdGenero(rs.getInt("id_genero"));
                genero.setNombre(rs.getString("nombre"));
                genero.setDescripcion(rs.getString("descripcion"));
                
                ListaGenero.add(genero);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return ListaGenero;
    }
}

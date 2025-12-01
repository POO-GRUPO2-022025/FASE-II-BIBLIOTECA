package sv.edu.udb.model.materiales;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import sv.edu.udb.model.Conexion;

/**
 * Modelo para gestionar la relación muchos a muchos entre Libros y Autores
 * Tabla: libro_autor
 */
public class LibroAutorModel {
    
    private final String SQL_INSERT = "INSERT INTO libro_autor(id_material, id_autor) VALUES(?,?)";
    private final String SQL_DELETE_BY_LIBRO = "DELETE FROM libro_autor WHERE id_material=?";
    private final String SQL_DELETE_BY_AUTOR = "DELETE FROM libro_autor WHERE id_autor=?";
    private final String SQL_DELETE_RELATION = "DELETE FROM libro_autor WHERE id_material=? AND id_autor=?";
    private final String SQL_SELECT_BY_LIBRO = "SELECT id_autor FROM libro_autor WHERE id_material=?";
    private final String SQL_SELECT_BY_AUTOR = "SELECT id_material FROM libro_autor WHERE id_autor=?";

    /**
     * Inserta una relación entre libro y autor
     */
    public boolean insertRelacion(int idMaterial, int idAutor) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean resultado = false;
        
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_INSERT);
            stmt.setInt(1, idMaterial);
            stmt.setInt(2, idAutor);
            int rows = stmt.executeUpdate();
            resultado = rows > 0;
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        
        return resultado;
    }

    /**
     * Inserta múltiples relaciones libro-autor en conjunto
     */
    public boolean insertRelaciones(int idMaterial, List<Integer> idsAutores) {
        if (idsAutores == null || idsAutores.isEmpty()) {
            return true; // No hay autores que insertar
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        boolean resultado = false;
        
        try {
            conn = Conexion.getConexion();
            conn.setAutoCommit(false); // Iniciar transacción
            
            stmt = conn.prepareStatement(SQL_INSERT);
            
            for (Integer idAutor : idsAutores) {
                stmt.setInt(1, idMaterial);
                stmt.setInt(2, idAutor);
                stmt.addBatch();
            }
            
            stmt.executeBatch();
            conn.commit();
            resultado = true;
            
        } catch (SQLException | ClassNotFoundException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Revertir cambios si hay error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Restaurar auto-commit
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        
        return resultado;
    }

    /**
     * Elimina todas las relaciones de un libro
     */
    public boolean deleteRelacionesByLibro(int idMaterial) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean resultado = false;
        
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_DELETE_BY_LIBRO);
            stmt.setInt(1, idMaterial);
            stmt.executeUpdate();
            resultado = true;
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        
        return resultado;
    }

    /**
     * Elimina todas las relaciones de un autor
     */
    public boolean deleteRelacionesByAutor(int idAutor) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean resultado = false;
        
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_DELETE_BY_AUTOR);
            stmt.setInt(1, idAutor);
            stmt.executeUpdate();
            resultado = true;
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        
        return resultado;
    }

    /**
     * Elimina una relación específica libro-autor
     */
    public boolean deleteRelacion(int idMaterial, int idAutor) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean resultado = false;
        
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_DELETE_RELATION);
            stmt.setInt(1, idMaterial);
            stmt.setInt(2, idAutor);
            int rows = stmt.executeUpdate();
            resultado = rows > 0;
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        
        return resultado;
    }

    /**
     * Obtiene los IDs de autores de un libro
     */
    public List<Integer> selectAutoresByLibro(int idMaterial) {
        List<Integer> autores = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_SELECT_BY_LIBRO);
            stmt.setInt(1, idMaterial);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                autores.add(rs.getInt("id_autor"));
            }
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        
        return autores;
    }

    /**
     * Obtiene los IDs de libros de un autor
     */
    public List<Integer> selectLibrosByAutor(int idAutor) {
        List<Integer> libros = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_SELECT_BY_AUTOR);
            stmt.setInt(1, idAutor);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                libros.add(rs.getInt("id_material"));
            }
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        
        return libros;
    }
}

package sv.edu.udb.model;

import sv.edu.udb.beans.Autor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AutorModel {
    
    private static final String SQL_INSERT = "INSERT INTO autores(id_autor, nombre, apellidos, pais) VALUES(?,?,?,?)";
    private static final String SQL_UPDATE = "UPDATE autores SET nombre=?, apellidos=?, pais=? WHERE id_autor=?";
    private static final String SQL_DELETE = "DELETE FROM autores WHERE id_autor=?";
    private static final String SQL_SELECT = "SELECT id_autor, nombre, apellidos, pais FROM autores WHERE id_autor=?";
    private static final String SQL_SELECT_ALL = "SELECT id_autor, nombre, apellidos, pais FROM autores ORDER BY id_autor";
    private static final String SQL_SELECT_BY_LIBRO = "SELECT a.id_autor, a.nombre, a.apellidos, a.pais " +
            "FROM autores a INNER JOIN libro_autor la ON a.id_autor = la.id_autor " +
            "WHERE la.id_material = ?";

    /**
     * Inserta un nuevo autor en la base de datos
     */
    public int insertar(Autor autor) {
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_INSERT);
            stmt.setInt(1, autor.getIdAutor());
            stmt.setString(2, autor.getNombre());
            stmt.setString(3, autor.getApellidos());
            stmt.setString(4, autor.getPais());
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
     * Actualiza un autor existente
     */
    public int actualizar(Autor autor) {
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_UPDATE);
            stmt.setString(1, autor.getNombre());
            stmt.setString(2, autor.getApellidos());
            stmt.setString(3, autor.getPais());
            stmt.setInt(4, autor.getIdAutor());
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
     * Elimina un autor
     */
    public int eliminar(int idAutor) {
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_DELETE);
            stmt.setInt(1, idAutor);
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
     * Obtiene un autor por su ID
     */
    public Autor obtenerPorId(int idAutor) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Autor autor = null;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_SELECT);
            stmt.setInt(1, idAutor);
            rs = stmt.executeQuery();
            if (rs.next()) {
                autor = new Autor(
                        rs.getInt("id_autor"),
                        rs.getString("nombre"),
                        rs.getString("apellidos"),
                        rs.getString("pais")
                );
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return autor;
    }

    /**
     * Obtiene todos los autores
     */
    public List<Autor> listarTodos() {
        List<Autor> autores = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_SELECT_ALL);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Autor autor = new Autor(
                    rs.getInt("id_autor"),
                    rs.getString("nombre"),
                    rs.getString("apellidos"),
                    rs.getString("pais")
                );
                autores.add(autor);
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
     * Obtiene los autores de un libro específico
     */
    public List<Autor> obtenerAutoresPorLibro(int idMaterial) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Autor> autores = new ArrayList<>();
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_SELECT_BY_LIBRO);
            stmt.setInt(1, idMaterial);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Autor autor = new Autor(
                        rs.getInt("id_autor"),
                        rs.getString("nombre"),
                        rs.getString("apellidos"),
                        rs.getString("pais")
                );
                autores.add(autor);
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
}

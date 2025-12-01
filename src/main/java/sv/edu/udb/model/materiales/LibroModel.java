package sv.edu.udb.model.materiales;

import sv.edu.udb.beans.Libro;
import sv.edu.udb.beans.Material;
import sv.edu.udb.model.Conexion;
import sv.edu.udb.model.MaterialModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo para gestionar operaciones específicas de Libros
 * Coordina con MaterialModel para operaciones base y LibroAutorModel para relaciones
 */
public class LibroModel {
    
    private final String SQL_INSERT = "INSERT INTO libros(id_material, id_editorial, isbn) VALUES(?,?,?)";
    private final String SQL_UPDATE = "UPDATE libros SET id_editorial=?, isbn=? WHERE id_material=?";
    private final String SQL_DELETE = "DELETE FROM libros WHERE id_material=?";
    private final String SQL_SELECT = "SELECT * FROM libros WHERE id_material=?";
    private final String SQL_SELECT_ALL = "SELECT l.id_material, l.id_editorial, l.isbn, e.nombre AS editorial_nombre, " +
            "m.tipo_material, m.titulo, m.ubicacion, m.cantidad_total, m.cantidad_disponible, " +
            "m.cantidad_prestados, m.cantidad_daniado " +
            "FROM libros l " +
            "INNER JOIN materiales m ON l.id_material = m.id_material " +
            "LEFT JOIN editoriales e ON l.id_editorial = e.id_editorial " +
            "ORDER BY l.id_material";

    /**
     * Inserta un libro completo (material + libro + autores)
     */
    public Libro insert(Libro libro) {
        // Paso 1: Insertar en materiales
        MaterialModel materialModel = new MaterialModel();
        Material baseMaterial = materialModel.insertar(libro);
        
        if (baseMaterial == null) return null;
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Libro nuevoLibro = null;
        
        try {
            conn = Conexion.getConexion();
            
            // Paso 2: Insertar datos específicos del libro
            stmt = conn.prepareStatement(SQL_INSERT);
            stmt.setInt(1, baseMaterial.getIdMaterial());
            stmt.setInt(2, libro.getIdEditorial());
            stmt.setString(3, libro.getIsbn());
            stmt.executeUpdate();
            
            // Paso 3: Insertar relaciones libro-autor
            LibroAutorModel libroAutorModel = new LibroAutorModel();
            if (libro.getIdsAutores() != null && !libro.getIdsAutores().isEmpty()) {
                libroAutorModel.insertRelaciones(baseMaterial.getIdMaterial(), libro.getIdsAutores());
            }
            
            // Paso 4: Recuperar el libro completo
            stmt = conn.prepareStatement(SQL_SELECT);
            stmt.setInt(1, baseMaterial.getIdMaterial());
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                nuevoLibro = new Libro();
                nuevoLibro.setIdMaterial(baseMaterial.getIdMaterial());
                nuevoLibro.setTipoMaterial(baseMaterial.getTipoMaterial());
                nuevoLibro.setTitulo(baseMaterial.getTitulo());
                nuevoLibro.setUbicacion(baseMaterial.getUbicacion());
                nuevoLibro.setCantidadTotal(baseMaterial.getCantidadTotal());
                nuevoLibro.setCantidadDisponible(baseMaterial.getCantidadDisponible());
                nuevoLibro.setCantidadPrestada(baseMaterial.getCantidadPrestada());
                nuevoLibro.setCantidadDaniada(baseMaterial.getCantidadDaniada());
                nuevoLibro.setIdEditorial(rs.getInt("id_editorial"));
                nuevoLibro.setIsbn(rs.getString("isbn"));
                nuevoLibro.setIdsAutores(libro.getIdsAutores());
            }
            
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al insertar libro", e);
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        
        return nuevoLibro;
    }

    /**
     * Actualiza solo los datos específicos del libro
     */
    public boolean update(Libro libro) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean resultado = false;
        
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_UPDATE);
            stmt.setInt(1, libro.getIdEditorial());
            stmt.setString(2, libro.getIsbn());
            stmt.setInt(3, libro.getIdMaterial());
            
            int filas = stmt.executeUpdate();
            resultado = filas > 0;
            
            // Actualizar relaciones libro-autor si se proporcionan
            if (resultado && libro.getIdsAutores() != null) {
                LibroAutorModel libroAutorModel = new LibroAutorModel();
                libroAutorModel.deleteRelacionesByLibro(libro.getIdMaterial());
                if (!libro.getIdsAutores().isEmpty()) {
                    libroAutorModel.insertRelaciones(libro.getIdMaterial(), libro.getIdsAutores());
                }
            }
            
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al actualizar libro", e);
        } finally {
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        
        return resultado;
    }

    /**
     * Elimina solo la parte específica del libro
     */
    public boolean delete(int idMaterial) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean resultado = false;
        
        try {
            conn = Conexion.getConexion();
            
            // Primero eliminar relaciones libro-autor
            LibroAutorModel libroAutorModel = new LibroAutorModel();
            libroAutorModel.deleteRelacionesByLibro(idMaterial);
            
            // Luego eliminar el libro
            stmt = conn.prepareStatement(SQL_DELETE);
            stmt.setInt(1, idMaterial);
            int filas = stmt.executeUpdate();
            resultado = filas > 0;
            
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al eliminar libro", e);
        } finally {
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        
        return resultado;
    }

    /**
     * Consulta un libro completo con sus autores
     */
    public Libro obtenerPorId(int idMaterial) {
        MaterialModel materialModel = new MaterialModel();
        Material baseMaterial = materialModel.obtenerPorId(idMaterial);
        
        if (baseMaterial == null) return null;
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Libro libro = null;
        
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_SELECT);
            stmt.setInt(1, idMaterial);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                libro = new Libro();
                libro.setIdMaterial(baseMaterial.getIdMaterial());
                libro.setTipoMaterial(baseMaterial.getTipoMaterial());
                libro.setTitulo(baseMaterial.getTitulo());
                libro.setUbicacion(baseMaterial.getUbicacion());
                libro.setCantidadTotal(baseMaterial.getCantidadTotal());
                libro.setCantidadDisponible(baseMaterial.getCantidadDisponible());
                libro.setCantidadPrestada(baseMaterial.getCantidadPrestada());
                libro.setCantidadDaniada(baseMaterial.getCantidadDaniada());
                libro.setIdEditorial(rs.getInt("id_editorial"));
                libro.setIsbn(rs.getString("isbn"));
                
                // Cargar autores
                LibroAutorModel libroAutorModel = new LibroAutorModel();
                List<Integer> idsAutores = libroAutorModel.selectAutoresByLibro(idMaterial);
                libro.setIdsAutores(idsAutores);
            }
            
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al consultar libro", e);
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        
        return libro;
    }

    /**
     * Obtiene todos los libros con información completa
     */
    public List<Libro> selectAll() {
        List<Libro> libros = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_SELECT_ALL);
            rs = stmt.executeQuery();
            
            LibroAutorModel libroAutorModel = new LibroAutorModel();
            
            while (rs.next()) {
                Libro libro = new Libro();
                libro.setIdMaterial(rs.getInt("id_material"));
                libro.setTipoMaterial(Material.TipoMaterial.valueOf(rs.getString("tipo_material")));
                libro.setTitulo(rs.getString("titulo"));
                libro.setUbicacion(rs.getString("ubicacion"));
                libro.setCantidadTotal(rs.getInt("cantidad_total"));
                libro.setCantidadDisponible(rs.getInt("cantidad_disponible"));
                libro.setCantidadPrestada(rs.getInt("cantidad_prestados"));
                libro.setCantidadDaniada(rs.getInt("cantidad_daniado"));
                libro.setIdEditorial(rs.getInt("id_editorial"));
                libro.setIsbn(rs.getString("isbn"));
                
                // Cargar autores
                List<Integer> idsAutores = libroAutorModel.selectAutoresByLibro(libro.getIdMaterial());
                libro.setIdsAutores(idsAutores);
                
                libros.add(libro);
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

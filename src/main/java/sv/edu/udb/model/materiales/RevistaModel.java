package sv.edu.udb.model.materiales;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import sv.edu.udb.beans.Material;
import sv.edu.udb.beans.Revista;
import sv.edu.udb.model.Conexion;
import sv.edu.udb.model.MaterialModel;

/**
 * Modelo para gestionar operaciones específicas de Revistas
 * Coordina con MaterialModel para operaciones base
 */
public class RevistaModel {
    
    private final String SQL_INSERT = "INSERT INTO revistas(id_material, volumen, numero, fecha_publicacion) VALUES(?,?,?,?)";
    private final String SQL_UPDATE = "UPDATE revistas SET volumen=?, numero=?, fecha_publicacion=? WHERE id_material=?";
    private final String SQL_DELETE = "DELETE FROM revistas WHERE id_material=?";
    private final String SQL_SELECT = "SELECT * FROM revistas WHERE id_material=?";
    private final String SQL_SELECT_ALL = "SELECT r.id_material, r.volumen, r.numero, r.fecha_publicacion, " +
            "m.tipo_material, m.titulo, m.ubicacion, m.cantidad_total, m.cantidad_disponible, " +
            "m.cantidad_prestados, m.cantidad_daniado " +
            "FROM revistas r " +
            "INNER JOIN materiales m ON r.id_material = m.id_material " +
            "ORDER BY r.id_material";

    /**
     * Inserta una revista completa (material + revista)
     */
    public Revista insert(Revista revista) {
        // Paso 1: Insertar en materiales
        MaterialModel materialModel = new MaterialModel();
        Material baseMaterial = materialModel.insertar(revista);
        
        if (baseMaterial == null) return null;
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Revista nuevaRevista = null;
        
        try {
            conn = Conexion.getConexion();
            
            // Paso 2: Insertar datos específicos de la revista
            stmt = conn.prepareStatement(SQL_INSERT);
            stmt.setInt(1, baseMaterial.getIdMaterial());
            stmt.setString(2, revista.getVolumen());
            stmt.setString(3, revista.getNumero());
            stmt.setDate(4, Date.valueOf(revista.getFechaPublicacion()));
            stmt.executeUpdate();
            
            // Paso 3: Recuperar la revista completa
            stmt = conn.prepareStatement(SQL_SELECT);
            stmt.setInt(1, baseMaterial.getIdMaterial());
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                nuevaRevista = new Revista();
                nuevaRevista.setIdMaterial(baseMaterial.getIdMaterial());
                nuevaRevista.setTipoMaterial(baseMaterial.getTipoMaterial());
                nuevaRevista.setTitulo(baseMaterial.getTitulo());
                nuevaRevista.setUbicacion(baseMaterial.getUbicacion());
                nuevaRevista.setCantidadTotal(baseMaterial.getCantidadTotal());
                nuevaRevista.setCantidadDisponible(baseMaterial.getCantidadDisponible());
                nuevaRevista.setCantidadPrestada(baseMaterial.getCantidadPrestada());
                nuevaRevista.setCantidadDaniada(baseMaterial.getCantidadDaniada());
                nuevaRevista.setVolumen(rs.getString("volumen"));
                nuevaRevista.setNumero(rs.getString("numero"));
                nuevaRevista.setFechaPublicacion(rs.getDate("fecha_publicacion").toLocalDate());
            }
            
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al insertar revista", e);
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        
        return nuevaRevista;
    }

    /**
     * Actualiza solo los datos específicos de la revista
     */
    public boolean update(Revista revista) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean resultado = false;
        
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_UPDATE);
            stmt.setString(1, revista.getVolumen());
            stmt.setString(2, revista.getNumero());
            stmt.setDate(3, Date.valueOf(revista.getFechaPublicacion()));
            stmt.setInt(4, revista.getIdMaterial());
            
            int filas = stmt.executeUpdate();
            resultado = filas > 0;
            
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al actualizar revista", e);
        } finally {
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        
        return resultado;
    }

    /**
     * Elimina solo la parte específica de la revista
     */
    public boolean delete(int idMaterial) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean resultado = false;
        
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_DELETE);
            stmt.setInt(1, idMaterial);
            int filas = stmt.executeUpdate();
            resultado = filas > 0;
            
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al eliminar revista", e);
        } finally {
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        
        return resultado;
    }

    /**
     * Consulta una revista completa
     */
    public Revista obtenerPorId(int idMaterial) {
        MaterialModel materialModel = new MaterialModel();
        Material baseMaterial = materialModel.obtenerPorId(idMaterial);
        
        if (baseMaterial == null) return null;
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Revista revista = null;
        
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_SELECT);
            stmt.setInt(1, idMaterial);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                revista = new Revista();
                revista.setIdMaterial(baseMaterial.getIdMaterial());
                revista.setTipoMaterial(baseMaterial.getTipoMaterial());
                revista.setTitulo(baseMaterial.getTitulo());
                revista.setUbicacion(baseMaterial.getUbicacion());
                revista.setCantidadTotal(baseMaterial.getCantidadTotal());
                revista.setCantidadDisponible(baseMaterial.getCantidadDisponible());
                revista.setCantidadPrestada(baseMaterial.getCantidadPrestada());
                revista.setCantidadDaniada(baseMaterial.getCantidadDaniada());
                revista.setVolumen(rs.getString("volumen"));
                revista.setNumero(rs.getString("numero"));
                revista.setFechaPublicacion(rs.getDate("fecha_publicacion").toLocalDate());
            }
            
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al consultar revista", e);
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        
        return revista;
    }

    /**
     * Obtiene todas las revistas con información completa
     */
    public List<Revista> selectRevistas() {
        List<Revista> revistas = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_SELECT_ALL);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Revista revista = new Revista();
                revista.setIdMaterial(rs.getInt("id_material"));
                revista.setTipoMaterial(Material.TipoMaterial.valueOf(rs.getString("tipo_material")));
                revista.setTitulo(rs.getString("titulo"));
                revista.setUbicacion(rs.getString("ubicacion"));
                revista.setCantidadTotal(rs.getInt("cantidad_total"));
                revista.setCantidadDisponible(rs.getInt("cantidad_disponible"));
                revista.setCantidadPrestada(rs.getInt("cantidad_prestados"));
                revista.setCantidadDaniada(rs.getInt("cantidad_daniado"));
                revista.setVolumen(rs.getString("volumen"));
                revista.setNumero(rs.getString("numero"));
                revista.setFechaPublicacion(rs.getDate("fecha_publicacion").toLocalDate());
                
                revistas.add(revista);
            }
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        
        return revistas;
    }
}

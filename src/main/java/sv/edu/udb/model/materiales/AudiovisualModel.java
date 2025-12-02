package sv.edu.udb.model.materiales;

import sv.edu.udb.beans.Audiovisual;
import sv.edu.udb.beans.Material;
import sv.edu.udb.model.Conexion;
import sv.edu.udb.model.MaterialModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo para gestionar operaciones específicas de Audiovisuales
 * Coordina con MaterialModel para operaciones base
 */
public class AudiovisualModel {
    
    private final String SQL_INSERT = "INSERT INTO audiovisuales(id_material, formato, duracion) VALUES(?,?,?)";
    private final String SQL_UPDATE = "UPDATE audiovisuales SET formato=?, duracion=? WHERE id_material=?";
    private final String SQL_DELETE = "DELETE FROM audiovisuales WHERE id_material=?";
    private final String SQL_SELECT = "SELECT * FROM audiovisuales WHERE id_material=?";
    private final String SQL_SELECT_ALL = "SELECT av.id_material, av.formato, av.duracion, " +
            "m.tipo_material, m.titulo, m.ubicacion, m.cantidad_total, m.cantidad_disponible, " +
            "m.cantidad_prestados, m.cantidad_daniado " +
            "FROM audiovisuales av " +
            "INNER JOIN materiales m ON av.id_material = m.id_material " +
            "ORDER BY av.id_material";

    /**
     * Inserta un audiovisual completo (material + audiovisual)
     */
    public Audiovisual insert(Audiovisual audiovisual) {
        // Paso 1: Insertar en materiales
        MaterialModel materialModel = new MaterialModel();
        Material baseMaterial = materialModel.insertar(audiovisual);
        
        if (baseMaterial == null) return null;
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Audiovisual nuevoAudiovisual = null;
        
        try {
            conn = Conexion.getConexion();
            
            // Paso 2: Insertar datos específicos del audiovisual
            stmt = conn.prepareStatement(SQL_INSERT);
            stmt.setInt(1, baseMaterial.getIdMaterial());
            stmt.setString(2, audiovisual.getFormato());
            stmt.setInt(3, audiovisual.getDuracion());
            stmt.executeUpdate();
            
            // Paso 3: Recuperar el audiovisual completo
            stmt = conn.prepareStatement(SQL_SELECT);
            stmt.setInt(1, baseMaterial.getIdMaterial());
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                nuevoAudiovisual = new Audiovisual();
                nuevoAudiovisual.setIdMaterial(baseMaterial.getIdMaterial());
                nuevoAudiovisual.setTipoMaterial(baseMaterial.getTipoMaterial());
                nuevoAudiovisual.setTitulo(baseMaterial.getTitulo());
                nuevoAudiovisual.setUbicacion(baseMaterial.getUbicacion());
                nuevoAudiovisual.setCantidadTotal(baseMaterial.getCantidadTotal());
                nuevoAudiovisual.setCantidadDisponible(baseMaterial.getCantidadDisponible());
                nuevoAudiovisual.setCantidadPrestada(baseMaterial.getCantidadPrestada());
                nuevoAudiovisual.setCantidadDaniada(baseMaterial.getCantidadDaniada());
                nuevoAudiovisual.setFormato(rs.getString("formato"));
                nuevoAudiovisual.setDuracion(rs.getInt("duracion"));
            }
            
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al insertar audiovisual", e);
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        
        return nuevoAudiovisual;
    }

    /**
     * Actualiza solo los datos específicos del audiovisual
     */
    public boolean update(Audiovisual audiovisual) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean resultado = false;
        
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_UPDATE);
            stmt.setString(1, audiovisual.getFormato());
            stmt.setInt(2, audiovisual.getDuracion());
            stmt.setInt(3, audiovisual.getIdMaterial());
            
            int filas = stmt.executeUpdate();
            resultado = filas > 0;
            
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al actualizar audiovisual", e);
        } finally {
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        
        return resultado;
    }

    /**
     * Elimina solo la parte específica del audiovisual
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
            throw new RuntimeException("Error al eliminar audiovisual", e);
        } finally {
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        
        return resultado;
    }

    /**
     * Consulta un audiovisual completo
     */
    public Audiovisual obtenerPorId(int idMaterial) {
        MaterialModel materialModel = new MaterialModel();
        Material baseMaterial = materialModel.obtenerPorId(idMaterial);
        
        if (baseMaterial == null) return null;
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Audiovisual audiovisual = null;
        
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_SELECT);
            stmt.setInt(1, idMaterial);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                audiovisual = new Audiovisual();
                audiovisual.setIdMaterial(baseMaterial.getIdMaterial());
                audiovisual.setTipoMaterial(baseMaterial.getTipoMaterial());
                audiovisual.setTitulo(baseMaterial.getTitulo());
                audiovisual.setUbicacion(baseMaterial.getUbicacion());
                audiovisual.setCantidadTotal(baseMaterial.getCantidadTotal());
                audiovisual.setCantidadDisponible(baseMaterial.getCantidadDisponible());
                audiovisual.setCantidadPrestada(baseMaterial.getCantidadPrestada());
                audiovisual.setCantidadDaniada(baseMaterial.getCantidadDaniada());
                audiovisual.setFormato(rs.getString("formato"));
                audiovisual.setDuracion(rs.getInt("duracion"));
            }
            
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al consultar audiovisual", e);
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        
        return audiovisual;
    }

    /**
     * Obtiene todos los audiovisuales con información completa
     */
    public List<Audiovisual> selectAll() {
        List<Audiovisual> audiovisuales = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_SELECT_ALL);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Audiovisual audiovisual = new Audiovisual();
                audiovisual.setIdMaterial(rs.getInt("id_material"));
                audiovisual.setTipoMaterial(Material.TipoMaterial.valueOf(rs.getString("tipo_material")));
                audiovisual.setTitulo(rs.getString("titulo"));
                audiovisual.setUbicacion(rs.getString("ubicacion"));
                audiovisual.setCantidadTotal(rs.getInt("cantidad_total"));
                audiovisual.setCantidadDisponible(rs.getInt("cantidad_disponible"));
                audiovisual.setCantidadPrestada(rs.getInt("cantidad_prestados"));
                audiovisual.setCantidadDaniada(rs.getInt("cantidad_daniado"));
                audiovisual.setFormato(rs.getString("formato"));
                audiovisual.setDuracion(rs.getInt("duracion"));
                
                audiovisuales.add(audiovisual);
            }
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        
        return audiovisuales;
    }
}

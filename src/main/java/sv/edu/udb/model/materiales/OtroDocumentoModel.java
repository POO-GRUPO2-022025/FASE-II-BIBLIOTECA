package sv.edu.udb.model.materiales;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import sv.edu.udb.beans.Material;
import sv.edu.udb.beans.OtroDocumento;
import sv.edu.udb.model.Conexion;
import sv.edu.udb.model.MaterialModel;

/**
 * Modelo para gestionar operaciones específicas de Otros Documentos
 * Coordina con MaterialModel para operaciones base
 */
public class OtroDocumentoModel {
    
    private final String SQL_INSERT = "INSERT INTO otros_documentos(id_material, descripcion) VALUES(?,?)";
    private final String SQL_UPDATE = "UPDATE otros_documentos SET descripcion=? WHERE id_material=?";
    private final String SQL_DELETE = "DELETE FROM otros_documentos WHERE id_material=?";
    private final String SQL_SELECT = "SELECT * FROM otros_documentos WHERE id_material=?";
    private final String SQL_SELECT_ALL = "SELECT od.id_material, od.descripcion, " +
            "m.tipo_material, m.titulo, m.ubicacion, m.cantidad_total, m.cantidad_disponible, " +
            "m.cantidad_prestados, m.cantidad_daniado " +
            "FROM otros_documentos od " +
            "INNER JOIN materiales m ON od.id_material = m.id_material " +
            "ORDER BY od.id_material";

    /**
     * Inserta un otro documento completo (material + otro_documento)
     */
    public OtroDocumento insert(OtroDocumento otroDocumento) {
        // Paso 1: Insertar en materiales
        MaterialModel materialModel = new MaterialModel();
        Material baseMaterial = materialModel.insertar(otroDocumento);
        
        if (baseMaterial == null) return null;
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        OtroDocumento nuevoDocumento = null;
        
        try {
            conn = Conexion.getConexion();
            
            // Paso 2: Insertar datos específicos del otro documento
            stmt = conn.prepareStatement(SQL_INSERT);
            stmt.setInt(1, baseMaterial.getIdMaterial());
            stmt.setString(2, otroDocumento.getDescripcion());
            stmt.executeUpdate();
            
            // Paso 3: Recuperar el otro documento completo
            stmt = conn.prepareStatement(SQL_SELECT);
            stmt.setInt(1, baseMaterial.getIdMaterial());
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                nuevoDocumento = new OtroDocumento();
                nuevoDocumento.setIdMaterial(baseMaterial.getIdMaterial());
                nuevoDocumento.setTipoMaterial(baseMaterial.getTipoMaterial());
                nuevoDocumento.setTitulo(baseMaterial.getTitulo());
                nuevoDocumento.setUbicacion(baseMaterial.getUbicacion());
                nuevoDocumento.setCantidadTotal(baseMaterial.getCantidadTotal());
                nuevoDocumento.setCantidadDisponible(baseMaterial.getCantidadDisponible());
                nuevoDocumento.setCantidadPrestada(baseMaterial.getCantidadPrestada());
                nuevoDocumento.setCantidadDaniada(baseMaterial.getCantidadDaniada());
                nuevoDocumento.setDescripcion(rs.getString("descripcion"));
            }
            
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al insertar otro documento", e);
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        
        return nuevoDocumento;
    }

    /**
     * Actualiza solo los datos específicos del otro documento
     */
    public boolean update(OtroDocumento otroDocumento) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean resultado = false;
        
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_UPDATE);
            stmt.setString(1, otroDocumento.getDescripcion());
            stmt.setInt(2, otroDocumento.getIdMaterial());
            
            int filas = stmt.executeUpdate();
            resultado = filas > 0;
            
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al actualizar otro documento", e);
        } finally {
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        
        return resultado;
    }

    /**
     * Elimina solo la parte específica del otro documento
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
            throw new RuntimeException("Error al eliminar otro documento", e);
        } finally {
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        
        return resultado;
    }

    /**
     * Consulta un otro documento completo
     */
    public OtroDocumento obtenerPorId(int idMaterial) {
        MaterialModel materialModel = new MaterialModel();
        Material baseMaterial = materialModel.obtenerPorId(idMaterial);
        
        if (baseMaterial == null) return null;
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        OtroDocumento otroDocumento = null;
        
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_SELECT);
            stmt.setInt(1, idMaterial);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                otroDocumento = new OtroDocumento();
                otroDocumento.setIdMaterial(baseMaterial.getIdMaterial());
                otroDocumento.setTipoMaterial(baseMaterial.getTipoMaterial());
                otroDocumento.setTitulo(baseMaterial.getTitulo());
                otroDocumento.setUbicacion(baseMaterial.getUbicacion());
                otroDocumento.setCantidadTotal(baseMaterial.getCantidadTotal());
                otroDocumento.setCantidadDisponible(baseMaterial.getCantidadDisponible());
                otroDocumento.setCantidadPrestada(baseMaterial.getCantidadPrestada());
                otroDocumento.setCantidadDaniada(baseMaterial.getCantidadDaniada());
                otroDocumento.setDescripcion(rs.getString("descripcion"));
            }
            
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error al consultar otro documento", e);
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        
        return otroDocumento;
    }

    /**
     * Obtiene todos los otros documentos con información completa
     */
    public List<OtroDocumento> selectAll() {
        List<OtroDocumento> otrosDocumentos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_SELECT_ALL);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                OtroDocumento otroDocumento = new OtroDocumento();
                otroDocumento.setIdMaterial(rs.getInt("id_material"));
                otroDocumento.setTipoMaterial(Material.TipoMaterial.valueOf(rs.getString("tipo_material")));
                otroDocumento.setTitulo(rs.getString("titulo"));
                otroDocumento.setUbicacion(rs.getString("ubicacion"));
                otroDocumento.setCantidadTotal(rs.getInt("cantidad_total"));
                otroDocumento.setCantidadDisponible(rs.getInt("cantidad_disponible"));
                otroDocumento.setCantidadPrestada(rs.getInt("cantidad_prestados"));
                otroDocumento.setCantidadDaniada(rs.getInt("cantidad_daniado"));
                otroDocumento.setDescripcion(rs.getString("descripcion"));
                
                otrosDocumentos.add(otroDocumento);
            }
            
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        
        return otrosDocumentos;
    }
}

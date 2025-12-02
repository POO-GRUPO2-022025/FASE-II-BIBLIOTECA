package sv.edu.udb.model;

import sv.edu.udb.beans.Material;
import sv.edu.udb.beans.Material.TipoMaterial;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaterialModel {
    
    private static final String SQL_INSERT = "INSERT INTO materiales(id_material, tipo_material, titulo, ubicacion, cantidad_total, cantidad_disponible, cantidad_prestados, cantidad_daniado) VALUES(?,?,?,?,?,?,?,?)";
    private static final String SQL_UPDATE = "UPDATE materiales SET tipo_material=?, titulo=?, ubicacion=?, cantidad_total=?, cantidad_disponible=?, cantidad_prestados=?, cantidad_daniado=? WHERE id_material=?";
    private static final String SQL_DELETE = "DELETE FROM materiales WHERE id_material=?";
    private static final String SQL_SELECT = "SELECT * FROM materiales WHERE id_material=?";
    private static final String SQL_SELECT_ALL = "SELECT id_material, tipo_material, titulo, ubicacion, cantidad_total, cantidad_disponible, cantidad_prestados, cantidad_daniado FROM materiales ORDER BY id_material";
    private static final String SQL_SELECT_BY_TYPE = "SELECT id_material, tipo_material, titulo, ubicacion, cantidad_total, cantidad_disponible, cantidad_prestados, cantidad_daniado FROM materiales WHERE tipo_material=? ORDER BY id_material";
    
    // SQL base para filtros dinámicos
    private static final String SQL_SELECT_FILTRADO_BASE = 
        "SELECT id_material, tipo_material, titulo, ubicacion, cantidad_total, cantidad_disponible, cantidad_prestados, cantidad_daniado " +
        "FROM materiales ";

    /**
     * Inserta un nuevo material y retorna el material con su ID generado
     */
    public Material insertar(Material material) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet IdGenerado = null;
        ResultSet rs = null;
        Material nuevoMaterial = null;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, material.getIdMaterial());
            stmt.setString(2, material.getTipoMaterial().toString());
            stmt.setString(3, material.getTitulo());
            stmt.setString(4, material.getUbicacion());
            stmt.setInt(5, material.getCantidadTotal());
            stmt.setInt(6, material.getCantidadDisponible());
            stmt.setInt(7, material.getCantidadPrestada());
            stmt.setInt(8, material.getCantidadDaniada());
            stmt.executeUpdate();

            IdGenerado = stmt.getGeneratedKeys();

            if (IdGenerado.next()) {
                stmt = conn.prepareStatement(SQL_SELECT);
                stmt.setInt(1, IdGenerado.getInt(1));
                rs = stmt.executeQuery();
                if (rs.next()) {
                    nuevoMaterial = new Material();
                    nuevoMaterial.setIdMaterial(rs.getInt("id_material"));
                    nuevoMaterial.setTipoMaterial(TipoMaterial.valueOf(rs.getString("tipo_material")));
                    nuevoMaterial.setTitulo(rs.getString("titulo"));
                    nuevoMaterial.setUbicacion(rs.getString("ubicacion"));
                    nuevoMaterial.setCantidadTotal(rs.getInt("cantidad_total"));
                    nuevoMaterial.setCantidadDisponible(rs.getInt("cantidad_disponible"));
                    nuevoMaterial.setCantidadPrestada(rs.getInt("cantidad_prestados"));
                    nuevoMaterial.setCantidadDaniada(rs.getInt("cantidad_daniado"));
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return nuevoMaterial;
    }

    /**
     * Actualiza un material existente
     */
    public boolean actualizar(Material material) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean retorno = false;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_UPDATE);
            stmt.setString(1, material.getTipoMaterial().toString());
            stmt.setString(2, material.getTitulo());
            stmt.setString(3, material.getUbicacion());
            stmt.setInt(4, material.getCantidadTotal());
            stmt.setInt(5, material.getCantidadDisponible());
            stmt.setInt(6, material.getCantidadPrestada());
            stmt.setInt(7, material.getCantidadDaniada());
            stmt.setInt(8, material.getIdMaterial());

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
     * Elimina un material
     */
    public boolean eliminar(int idMaterial) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean retorno = false;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_DELETE);
            stmt.setInt(1, idMaterial);
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
     * Obtiene un material por su ID
     */
    public Material obtenerPorId(int idMaterial) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Material material = null;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_SELECT);
            stmt.setInt(1, idMaterial);
            rs = stmt.executeQuery();
            if (rs.next()) {
                material = new Material();
                material.setIdMaterial(rs.getInt("id_material"));
                material.setTipoMaterial(TipoMaterial.valueOf(rs.getString("tipo_material")));
                material.setTitulo(rs.getString("titulo"));
                material.setUbicacion(rs.getString("ubicacion"));
                material.setCantidadTotal(rs.getInt("cantidad_total"));
                material.setCantidadDisponible(rs.getInt("cantidad_disponible"));
                material.setCantidadPrestada(rs.getInt("cantidad_prestados"));
                material.setCantidadDaniada(rs.getInt("cantidad_daniado"));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return material;
    }

    /**
     * Obtiene todos los materiales
     */
    public List<Material> listarTodos() {
        List<Material> materiales = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_SELECT_ALL);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Material material = new Material();
                material.setIdMaterial(rs.getInt("id_material"));
                material.setTipoMaterial(TipoMaterial.valueOf(rs.getString("tipo_material")));
                material.setTitulo(rs.getString("titulo"));
                material.setUbicacion(rs.getString("ubicacion"));
                material.setCantidadTotal(rs.getInt("cantidad_total"));
                material.setCantidadDisponible(rs.getInt("cantidad_disponible"));
                material.setCantidadPrestada(rs.getInt("cantidad_prestados"));
                material.setCantidadDaniada(rs.getInt("cantidad_daniado"));
                materiales.add(material);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return materiales;
    }

    /**
     * Obtiene materiales por tipo
     */
    public List<Material> listarPorTipo(TipoMaterial tipo) {
        List<Material> materiales = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_SELECT_BY_TYPE);
            stmt.setString(1, tipo.toString());
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Material material = new Material();
                material.setIdMaterial(rs.getInt("id_material"));
                material.setTipoMaterial(TipoMaterial.valueOf(rs.getString("tipo_material")));
                material.setTitulo(rs.getString("titulo"));
                material.setUbicacion(rs.getString("ubicacion"));
                material.setCantidadTotal(rs.getInt("cantidad_total"));
                material.setCantidadDisponible(rs.getInt("cantidad_disponible"));
                material.setCantidadPrestada(rs.getInt("cantidad_prestados"));
                material.setCantidadDaniada(rs.getInt("cantidad_daniado"));
                materiales.add(material);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return materiales;
    }

    /**
     * Filtra materiales con query dinámica
     * @param titulo Título a buscar (null o vacío para todos)
     * @param tipo Tipo de material (null o vacío para todos)
     * @return Lista de materiales filtrados
     */
    public List<Material> buscarMaterialesFiltrados(String titulo, String tipo) {
        List<Material> materiales = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        StringBuilder sql = new StringBuilder(SQL_SELECT_FILTRADO_BASE);
        boolean hayFiltros = false;
        
        // Filtro por tipo
        if (tipo != null && !tipo.isEmpty()) {
            sql.append("WHERE tipo_material = ? ");
            hayFiltros = true;
        }
        
        // Filtro por título
        if (titulo != null && !titulo.isEmpty()) {
            if (hayFiltros) {
                sql.append("AND titulo LIKE ? ");
            } else {
                sql.append("WHERE titulo LIKE ? ");
                hayFiltros = true;
            }
        }
        
        sql.append("ORDER BY id_material");
        
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(sql.toString());
            
            int paramIndex = 1;
            if (tipo != null && !tipo.isEmpty()) {
                stmt.setString(paramIndex++, tipo);
            }
            if (titulo != null && !titulo.isEmpty()) {
                stmt.setString(paramIndex, "%" + titulo + "%");
            }
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Material material = new Material();
                material.setIdMaterial(rs.getInt("id_material"));
                material.setTipoMaterial(TipoMaterial.valueOf(rs.getString("tipo_material")));
                material.setTitulo(rs.getString("titulo"));
                material.setUbicacion(rs.getString("ubicacion"));
                material.setCantidadTotal(rs.getInt("cantidad_total"));
                material.setCantidadDisponible(rs.getInt("cantidad_disponible"));
                material.setCantidadPrestada(rs.getInt("cantidad_prestados"));
                material.setCantidadDaniada(rs.getInt("cantidad_daniado"));
                materiales.add(material);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return materiales;
    }
}

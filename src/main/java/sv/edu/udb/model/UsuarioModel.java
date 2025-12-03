package sv.edu.udb.model;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import sv.edu.udb.beans.Usuario;
import sv.edu.udb.beans.Usuario.TipoUsuario;

public class UsuarioModel {
    
   

private static final String SQL_INSERT = "INSERT INTO usuario(nombre, correo, password, tipo_usuario) VALUES(?,?,?,?)";
private static final String SQL_UPDATE = "UPDATE usuario SET nombre=?, correo=?, tipo_usuario=? WHERE id_usuario=?";
private static final String SQL_UPDATE_PASSWORD = "UPDATE usuario SET password=? WHERE id_usuario=?";
private static final String SQL_DELETE = "DELETE FROM usuario WHERE id_usuario=?";
private static final String SQL_SELECT = "SELECT id_usuario, nombre, correo, password, tipo_usuario FROM usuario WHERE id_usuario=?";
private static final String SQL_SELECT_ALL = "SELECT id_usuario, nombre, correo, password, tipo_usuario FROM usuario ORDER BY id_usuario";
private static final String SQL_SELECT_BY_EMAIL = "SELECT id_usuario, nombre, correo, password, tipo_usuario FROM usuario WHERE correo=?";

 




    public int insertar(Usuario usuario) {
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_INSERT);
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getCorreo());
            stmt.setString(3, usuario.getPassword());
            stmt.setString(4, usuario.getTipoUsuario().name());
            rows = stmt.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return rows;
    }

   
   
    public int actualizar(Usuario usuario) {
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_UPDATE);
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getCorreo());
            stmt.setString(3, usuario.getTipoUsuario().name());
            stmt.setInt(4, usuario.getIdUsuario());
            rows = stmt.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return rows;
    }

    
    public int actualizarPassword(int idUsuario, String nuevoPassword) {
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_UPDATE_PASSWORD);
            Usuario temp = new Usuario();
            stmt.setString(1, temp.encriptarPass(nuevoPassword));
            stmt.setInt(2, idUsuario);
            rows = stmt.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return rows;
    }

   
    public int eliminar(int idUsuario) {
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_DELETE);
            stmt.setInt(1, idUsuario);
            rows = stmt.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return rows;
    }

  
    public Usuario obtenerPorId(int idUsuario) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Usuario usuario = null;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_SELECT);
            stmt.setInt(1, idUsuario);
            rs = stmt.executeQuery();
            if (rs.next()) {
                usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("id_usuario"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setCorreo(rs.getString("correo"));
               
             
                usuario.setTipoUsuario(TipoUsuario.valueOf(rs.getString("tipo_usuario")));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return usuario;
    }

    
    public Usuario obtenerPorCorreo(String correo) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Usuario usuario = null;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_SELECT_BY_EMAIL);
            stmt.setString(1, correo);
            rs = stmt.executeQuery();
            if (rs.next()) {
                usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("id_usuario"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setCorreo(rs.getString("correo"));
                usuario.setTipoUsuario(TipoUsuario.valueOf(rs.getString("tipo_usuario")));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return usuario;
    }

 
    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_SELECT_ALL);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("id_usuario"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setCorreo(rs.getString("correo"));
                usuario.setTipoUsuario(TipoUsuario.valueOf(rs.getString("tipo_usuario")));
                usuarios.add(usuario);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return usuarios;
    }

    public Usuario validarCredenciales(String correo, String password) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Usuario usuario = null;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_SELECT_BY_EMAIL);
            stmt.setString(1, correo);
            rs = stmt.executeQuery();
            if (rs.next()) {
                String passwordHash = rs.getString("password");
                Usuario temp = new Usuario();
                temp.setPassword(password);
                
                if (org.mindrot.jbcrypt.BCrypt.checkpw(password, passwordHash)) {
                    usuario = new Usuario();
                    usuario.setIdUsuario(rs.getInt("id_usuario"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setCorreo(rs.getString("correo"));
                    usuario.setTipoUsuario(TipoUsuario.valueOf(rs.getString("tipo_usuario")));
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return usuario;
    }
}

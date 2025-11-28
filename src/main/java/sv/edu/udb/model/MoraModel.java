package sv.edu.udb.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import sv.edu.udb.beans.Mora;
import sv.edu.udb.beans.Usuario.TipoUsuario;

public class MoraModel {
    
    private static final String SQL_INSERT = "INSERT INTO moras(anio_aplicable, tipo_usuario, tarifa_diaria) VALUES(?,?,?)";
    private static final String SQL_UPDATE = "UPDATE moras SET anio_aplicable=?, tipo_usuario=?, tarifa_diaria=? WHERE id_mora=?";
    private static final String SQL_DELETE = "DELETE FROM moras WHERE id_mora=?";
    private static final String SQL_SELECT = "SELECT * FROM moras WHERE id_mora=?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM moras ORDER BY anio_aplicable DESC, tipo_usuario";
    private static final String SQL_SELECT_BY_YEAR_AND_TYPE = "SELECT * FROM moras WHERE anio_aplicable=? AND tipo_usuario=?";

    /**
     * Inserta una nueva mora/tarifa
     */
    public int insertar(Mora mora) {
        Connection conn = null;
        PreparedStatement stmt = null;
        int rows = 0;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_INSERT);
            stmt.setInt(1, mora.getAnioAplicable());
            stmt.setString(2, mora.getTipoUsuario().name());
            stmt.setBigDecimal(3, mora.getTarifaDiaria());
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
     * Actualiza una mora/tarifa existente
     */
    public boolean actualizar(Mora mora) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean retorno = false;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_UPDATE);
            stmt.setInt(1, mora.getAnioAplicable());
            stmt.setString(2, mora.getTipoUsuario().name());
            stmt.setBigDecimal(3, mora.getTarifaDiaria());
            stmt.setInt(4, mora.getIdMora());
            
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
     * Elimina una mora/tarifa
     */
    public boolean eliminar(int idMora) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean retorno = false;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_DELETE);
            stmt.setInt(1, idMora);
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
     * Obtiene una mora por su ID
     */
    public Mora obtenerPorId(int idMora) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Mora mora = null;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_SELECT);
            stmt.setInt(1, idMora);
            rs = stmt.executeQuery();
            if (rs.next()) {
                mora = new Mora();
                mora.setIdMora(rs.getInt("id_mora"));
                mora.setAnioAplicable(rs.getInt("anio_aplicable"));
                mora.setTipoUsuario(TipoUsuario.valueOf(rs.getString("tipo_usuario")));
                mora.setTarifaDiaria(rs.getBigDecimal("tarifa_diaria"));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return mora;
    }

    /**
     * Obtiene todas las moras/tarifas
     */
    public List<Mora> listarTodas() {
        List<Mora> moras = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_SELECT_ALL);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Mora mora = new Mora();
                mora.setIdMora(rs.getInt("id_mora"));
                mora.setAnioAplicable(rs.getInt("anio_aplicable"));
                mora.setTipoUsuario(TipoUsuario.valueOf(rs.getString("tipo_usuario")));
                mora.setTarifaDiaria(rs.getBigDecimal("tarifa_diaria"));
                moras.add(mora);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return moras;
    }

    /**
     * Obtiene la tarifa de mora para un año y tipo de usuario específico
     */
    public Mora obtenerPorAnioYTipo(int anio, TipoUsuario tipoUsuario) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Mora mora = null;
        try {
            conn = Conexion.getConexion();
            stmt = conn.prepareStatement(SQL_SELECT_BY_YEAR_AND_TYPE);
            stmt.setInt(1, anio);
            stmt.setString(2, tipoUsuario.name());
            rs = stmt.executeQuery();
            if (rs.next()) {
                mora = new Mora();
                mora.setIdMora(rs.getInt("id_mora"));
                mora.setAnioAplicable(rs.getInt("anio_aplicable"));
                mora.setTipoUsuario(TipoUsuario.valueOf(rs.getString("tipo_usuario")));
                mora.setTarifaDiaria(rs.getBigDecimal("tarifa_diaria"));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Conexion.close(rs);
            Conexion.close(stmt);
            Conexion.close(conn);
        }
        return mora;
    }
}

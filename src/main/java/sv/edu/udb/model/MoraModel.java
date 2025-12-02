package sv.edu.udb.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import sv.edu.udb.beans.Mora;
import sv.edu.udb.beans.Usuario.TipoUsuario;

public class MoraModel {
    
    // Consultas SQL usadas
    private static final String SQL_SELECT = "SELECT * FROM moras WHERE id_mora=?";
    private static final String SQL_SELECT_BY_YEAR_AND_TYPE = "SELECT * FROM moras WHERE anio_aplicable=? AND tipo_usuario=?";

    /**
     * Obtiene una mora por su ID
     * USADO EN: PrestamoService.devolverPrestamo(), PrestamoService.obtenerDetallePrestamo()
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
     * Obtiene la tarifa de mora para un año y tipo de usuario específico
     * USADO EN: selectByTipoUsuarioYAnio() (método interno)
     */
    private Mora obtenerPorAnioYTipo(int anio, TipoUsuario tipoUsuario) {
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

    /**
     * Obtiene la tarifa de mora para un año y tipo de usuario específico (String)
     * USADO EN: PrestamoService.solicitarPrestamo()
     */
    public Mora selectByTipoUsuarioYAnio(String tipoUsuarioStr, int anio) {
        try {
            TipoUsuario tipoUsuario = TipoUsuario.valueOf(tipoUsuarioStr);
            return obtenerPorAnioYTipo(anio, tipoUsuario);
        } catch (IllegalArgumentException e) {
            System.err.println("Tipo de usuario inválido: " + tipoUsuarioStr);
            return null;
        }
    }
}

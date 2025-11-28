package sv.edu.udb.model;

import java.sql.*;

public class Conexion {

    // Atributos para la conexión
    private static final String DRIVER_DB = "com.mysql.cj.jdbc.Driver";
    private static final String URL_DB = "jdbc:mysql://127.0.0.1:3306/biblioteca_db";
    private static final String USUARIO_DB = "root";
    private static final String PASS_DB = "rootpassword";

    /**
     * Obtiene una conexión a la base de datos
     * @return Connection objeto de conexión
     * @throws SQLException si ocurre un error de SQL
     * @throws ClassNotFoundException si no encuentra el driver
     */
    public static Connection getConexion() throws SQLException, ClassNotFoundException {
        Class.forName(DRIVER_DB);
        try {
            Connection conn = DriverManager.getConnection(URL_DB, USUARIO_DB, PASS_DB);
            return conn;
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Cierra un ResultSet
     */
    public static void close(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            System.err.println("Error cerrando ResultSet: " + e.getMessage());
        }
    }

    /**
     * Cierra un Statement
     */
    public static void close(Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            System.err.println("Error cerrando Statement: " + e.getMessage());
        }
    }

    /**
     * Cierra una Connection
     */
    public static void close(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Error cerrando Connection: " + e.getMessage());
        }
    }
}

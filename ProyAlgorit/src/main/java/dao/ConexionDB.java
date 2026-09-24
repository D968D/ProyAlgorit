package dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionDB {

    private static String url = "jdbc:postgresql://restaurante-db.canoy6a4yl8c.us-east-1.rds.amazonaws.com:5432/cordonrosadb";
    private static String user = "postgres";
    private static String pass = "algortimos123";

    static {
        try (InputStream in = ConexionDB.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (in != null) {
                Properties p = new Properties();
                p.load(in);
                url = p.getProperty("db.url", url);
                user = p.getProperty("db.user", user);
                pass = p.getProperty("db.password", pass);
            }
        } catch (Exception e) {
            System.err.println("No se pudo leer db.properties, se usan valores por defecto.");
        }
    }

    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }

    public static void closeConexion(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexion: " + e.getMessage());
            }
        }
    }

    public static boolean probarConexion() {
        try (Connection conn = getConexion()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Error de conexion: " + e.getMessage());
            return false;
        }
    }
}
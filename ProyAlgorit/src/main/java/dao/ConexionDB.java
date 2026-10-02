package dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionDB {

    public static Connection getConexion() throws SQLException {
        Connection conn = null;
        Properties config = new Properties();
        try (FileInputStream fis = new FileInputStream("ProyAlgorit/.env")) {
            
            config.load(fis);
            
            String url = config.getProperty("DB_URL");
            String user = config.getProperty("DB_USER");
            String pass = config.getProperty("DB_PASSWORD");

            conn = DriverManager.getConnection(url, user, pass);
            
        } catch (IOException e) {
            System.err.println("Error: No se encontró el archivo .env en la raíz del proyecto.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error de SQL: Credenciales incorrectas o la base de datos está apagada.");
            e.printStackTrace();
        }
        return conn;
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
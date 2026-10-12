package dao;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionDB {

    public static Connection getConexion() throws SQLException {
        Connection conn = null;
        Properties config = new Properties();

        Path[] candidatos = {
                Path.of(".env"),
                Path.of("ProyAlgorit/.env"),
                Path.of("src/.env")
        };

        Path rutaEnv = null;
        for (Path candidato : candidatos) {
            if (Files.exists(candidato)) {
                rutaEnv = candidato;
                break;
            }
        }

        if (rutaEnv == null) {
            throw new SQLException("No se encontró el archivo .env en la raíz del proyecto.");
        }

        try (InputStream input = Files.newInputStream(rutaEnv)) {
            config.load(input);

            String url = config.getProperty("DB_URL");
            String user = config.getProperty("DB_USER");
            String pass = config.getProperty("DB_PASSWORD");

            conn = DriverManager.getConnection(url, user, pass);
        } catch (IOException e) {
            throw new SQLException("No se pudo leer el archivo .env: " + rutaEnv, e);
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


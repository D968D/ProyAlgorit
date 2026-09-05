package dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConexionDB {
    private static final String URL = "jdbc:postgresql://localhost:5432/CordonRosaDB";
    private static final String USER = "postgres";
    private static final String PASS = "admin";

    public static Connection getConexion() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(URL, USER, PASS);

        } catch (Exception e) {
            System.out.println("Error de conexion: " + e.getMessage());
        }
        return con;
    }
}

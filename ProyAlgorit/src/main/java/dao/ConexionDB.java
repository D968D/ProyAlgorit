package dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConexionDB {
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=CordonRosaDB;encrypt=true;trustServerCertificate=true;";
    private static final String USER = "sa";
    private static final String PASS = "Contraseña de la base de datos";

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

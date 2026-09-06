package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class LoginDAO {

    public boolean validar(String usuario, String contra) {
        String sql = "SELECT * FROM usuarios WHERE username = ? AND \"contraseña\" = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ps.setString(2, contra);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.out.println("Error al iniciar sesion: " + e.getMessage());
            return false;
        }
    }
}


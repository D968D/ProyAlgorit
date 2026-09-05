import dao.ConexionDB;

import java.sql.*;

public class Login {

    public boolean validar(String usuario, String contra) {
        try {
            Connection con = ConexionDB.getConexion();
            String sql = "SELECT * FROM usuarios WHERE username = ? AND contraseña = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, usuario);
            ps.setString(2, contra);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.out.println("Error al iniciar sesion: " + e.getMessage());
            return false;
        }
    }
}
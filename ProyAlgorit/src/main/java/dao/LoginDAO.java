package dao;

import util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDAO {

    /**
     * Valida usuario y contraseña. Las contraseñas se guardan con hash (PasswordUtil).
     * Si el usuario todavía tiene la contraseña antigua en texto plano, se acepta una
     * sola vez y se reemplaza de inmediato por su hash (migración automática).
     */
    public boolean validar(String usuario, String contra) {
        String sql = "SELECT idusuario, \"contraseña\" FROM usuarios WHERE username = ?";

        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }
                int id = rs.getInt(1);
                String guardado = rs.getString(2);

                if (PasswordUtil.esHash(guardado)) {
                    return PasswordUtil.verificar(contra, guardado);
                }
                if (guardado != null && guardado.equals(contra)) {
                    migrarAHash(con, id, contra);
                    return true;
                }
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Error al iniciar sesion: " + e.getMessage());
            return false;
        }
    }

    private void migrarAHash(Connection con, int idUsuario, String contra) {
        String sql = "UPDATE usuarios SET \"contraseña\" = ? WHERE idusuario = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, PasswordUtil.hashear(contra));
            ps.setInt(2, idUsuario);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("No se pudo migrar la contraseña a hash: " + e.getMessage());
        }
    }
}

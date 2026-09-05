package dao;
import java.sql.*;
public class MesaDAO {
    public boolean actualizar(){
        String sql = "UPDATE mesas SET estado = ? WHERE numero_mesa= ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pst = conn.prepareStatement(sql)
        ) {
            pst.setString(1, );
            pst.setInt(2, );

            pst.executeUpdate();
            return true;

        } catch (Exception e) {
            System.out.println("Error al Actualizar Mesa: " + e.getMessage());
            return false;
        }
    }
}

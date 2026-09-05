package dao;
import java.sql.*;
public class PedidoDAO {
    public boolean agregar(){
        String sql = "INSERT INTO pedidos (id_pedido, id_mesa, estado) VALUES (?,?,?)";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pst = conn.prepareStatement(sql)
        ) {
            pst.setInt(1, 1);
            pst.setInt(2, 2);
            pst.setString(3, "e");
            pst.executeUpdate();
            return true;
        } catch (Exception e) {
            System.out.println("Error al agregar Pedido: " + e.getMessage());
            return false;
        }
    }
}
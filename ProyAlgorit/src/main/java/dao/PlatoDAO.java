package dao;
import model.Plato;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PlatoDAO {
    public boolean insertar(Plato plato) {
        String sql = "INSERT INTO platos (nombre, precio, categoria) VALUES (?, ?, ?)";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, plato.getNombre());
            pst.setDouble(2, plato.getPrecio());
            pst.setString(3, plato.getCategoria());

            int filasAfectadas = pst.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al insertar plato: " + e.getMessage());
            return false;
        }
    }
}
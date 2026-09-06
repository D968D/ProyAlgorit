package dao;

import model.Mesa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MesaDAO {

    public List<Mesa> listarTodas() {
        List<Mesa> lista = new ArrayList<>();
        String sql = "SELECT numero_mesa, capacidad, estado FROM mesas ORDER BY numero_mesa";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Mesa mesa = new Mesa(
                        rs.getInt("numero_mesa"),
                        rs.getInt("capacidad"),
                        rs.getString("estado")
                );
                lista.add(mesa);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar mesas: " + e.getMessage());
        }
        return lista;
    }

 
    public boolean actualizarEstado(int numeroMesa, String nuevoEstado) {
        String sql = "UPDATE mesas SET estado = ? WHERE numero_mesa = ?";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, nuevoEstado);
            pst.setInt(2, numeroMesa);

            int filasAfectadas = pst.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar mesa: " + e.getMessage());
            return false;
        }
    }

    public boolean insertar(Mesa mesa) {
        String sql = "INSERT INTO mesas (numero_mesa, capacidad, estado) VALUES (?, ?, ?)";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, mesa.getNumeroMesa());
            pst.setInt(2, mesa.getCapacidad());
            pst.setString(3, mesa.getEstado());

            pst.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al insertar mesa: " + e.getMessage());
            return false;
        }
    }
}

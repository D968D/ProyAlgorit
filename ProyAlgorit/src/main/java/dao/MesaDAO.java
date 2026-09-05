package dao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Mesa;
public class MesaDAO {
    public boolean actualizar(Mesa mesa){
        String sql = "UPDATE mesas SET estado = ? WHERE numero_mesa = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pst = conn.prepareStatement(sql)
        ) {
            pst.setString(1, mesa.getEstado());
            pst.setInt(2, mesa.getNumMesa());

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al Actualizar Mesa: " + e.getMessage());
            return false;
        }
    }
    public List<Mesa> listarTodas(){
        List<Mesa> mesas = new ArrayList<>();
        String sql = "SELECT * FROM mesas";

        try (
            Connection conn = ConexionDB.getConexion();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)
        ) {
            while (rs.next()) {
                Mesa m = new Mesa(
                    rs.getInt("numero_mesa"),
                    rs.getInt("capacidad"),
                    rs.getString("estado")
                );
                mesas.add(m);
                
            }
        } catch (Exception e) {
            System.out.println("Error al listar mesas: " + e.getMessage());
            return mesas;
        }
        return mesas;
    }
    public Mesa buscarPorNumero(int numero){
        String sql = "SELECT numero_mesa, capacidad, estado FROM mesas WHERE numero_mesa = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, numero);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Mesa(
                        rs.getInt("numero_mesa"),
                        rs.getInt("capacidad"),
                        rs.getString("estado")
                    );
                }
            }
        } catch (Exception e) {
            System.out.println("Error al buscar mesa: " + e.getMessage());
        }
        return null;
    }
    public boolean insertar(Mesa mesa){
        String sql = "INSERT INTO mesas (numero_mesa, capacidad, estado) VALUES (?, ?, ?)";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, mesa.getNumMesa());
            pst.setInt(2, mesa.getCapacidad());
            pst.setString(3, mesa.getEstado());

            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error al insertar mesa: " + e.getMessage());
            return false;
        }
    }
}
package dao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Mesa;
public class MesaDAO {
    public boolean actualizar(Mesa mesa){
        String sql = "UPDATE mesas SET estado = ? WHERE numero_mesa= ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pst = conn.prepareStatement(sql)
        ) {
            pst.setString(1, "a");
            pst.setInt(2, 3);

            pst.executeUpdate();
            return true;

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
            return null;
        }
        return mesas;
    }
    public Mesa buscarPorNumero(int numero){
        return null;
    }
    public boolean insertar(Mesa mesa){
        return false;//cambiar
    }
}
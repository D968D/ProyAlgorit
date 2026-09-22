package dao;

import model.Plato;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PlatoDAO {

    public List<Plato> listarTodos() {
        List<Plato> lista = new ArrayList<>();
        String sql = "SELECT idplato, nombre, precio, categoria FROM platos ORDER BY idplato";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Plato plato = new Plato(
                        rs.getInt("idplato"),
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getString("categoria")
                );
                lista.add(plato);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar platos: " + e.getMessage());
        }
        return lista;
    }

    public boolean insertar(Plato plato) {
        String sql = "INSERT INTO platos (nombre, precio, categoria) VALUES (?, ?, ?)";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, plato.getNombre());
            pst.setDouble(2, plato.getPrecio());
            pst.setString(3, plato.getCategoria());

            int filas = pst.executeUpdate();
            if (filas > 0) {
                ResultSet keys = pst.getGeneratedKeys();
                if (keys.next()) {
                    plato.setIdPlato(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error al insertar plato: " + e.getMessage());
        }
        return false;
    }

    /** Actualiza solo el precio (como en la imagen del diálogo de Actualizar). */
    public boolean actualizarPrecio(int idPlato, double nuevoPrecio) {
        String sql = "UPDATE platos SET precio = ? WHERE idplato = ?";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setDouble(1, nuevoPrecio);
            pst.setInt(2, idPlato);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar precio del plato: " + e.getMessage());
            return false;
        }
    }

    /** Actualiza todos los campos (por si se necesita más adelante). */
    public boolean actualizar(Plato plato) {
        String sql = "UPDATE platos SET nombre = ?, precio = ?, categoria = ? WHERE idplato = ?";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, plato.getNombre());
            pst.setDouble(2, plato.getPrecio());
            pst.setString(3, plato.getCategoria());
            pst.setInt(4, plato.getIdPlato());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar plato: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int idPlato) {
        String sql = "DELETE FROM platos WHERE idplato = ?";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, idPlato);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar plato: " + e.getMessage());
            return false;
        }
    }

    public Plato buscarPorId(int idPlato) {
        String sql = "SELECT idplato, nombre, precio, categoria FROM platos WHERE idplato = ?";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, idPlato);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Plato(
                        rs.getInt("idplato"),
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getString("categoria")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar plato: " + e.getMessage());
        }
        return null;
    }
}

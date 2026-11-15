package dao;

import model.DetallePedido;
import model.Pedido;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Acceso a las tablas "pedidos" y "detalle_pedido".
 * Los métodos propagan SQLException para que el controlador informe los errores.
 */
public class PedidoDAO {

    private static final DateTimeFormatter FECHA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public List<Pedido> listarTodos() throws SQLException {
        return listarPedidos(true);
    }

    public List<Pedido> listarTodosLosPedidos() throws SQLException {
        return listarPedidos(false);
    }

    private List<Pedido> listarPedidos(boolean soloPendientes) throws SQLException {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT idpedido, numero_mesa, dni, fecha_hora, estado, total, metodo_pago "
                + "FROM pedidos "
                + (soloPendientes ? "WHERE UPPER(TRIM(estado)) = 'PENDIENTE' " : "")
                + "ORDER BY fecha_hora DESC NULLS LAST, idpedido DESC";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Pedido pedido = new Pedido();
                pedido.setIdPedido(rs.getInt("idpedido"));
                pedido.setNumMesa(rs.getInt("numero_mesa"));
                pedido.setDni(rs.getString("dni"));

                Timestamp fechaHora = rs.getTimestamp("fecha_hora");
                pedido.setFechaHora(fechaHora == null
                        ? ""
                        : fechaHora.toLocalDateTime().format(FECHA_HORA));

                pedido.setEstado(rs.getString("estado"));
                pedido.setTotal(rs.getDouble("total"));
                pedido.setMetodoPago(rs.getString("metodo_pago"));
                lista.add(pedido);
            }
        }

        return lista;
    }

    public boolean insertarConDetalles(Pedido pedido) throws SQLException {
        String sqlPedido = "INSERT INTO pedidos "
                + "(numero_mesa, dni, estado, total, metodo_pago) VALUES (?, ?, ?, ?, ?)";
        String sqlDetalle = "INSERT INTO detalle_pedido "
                + "(idpedido, idplato, cantidad, precio, subtotal) VALUES (?, ?, ?, ?, ?)";
        String estado = normalizarEstado(pedido.getEstado());

        try (Connection conn = ConexionDB.getConexion()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement pst = conn.prepareStatement(
                        "SELECT pg_advisory_xact_lock(?, ?)")) {
                    pst.setInt(1, 18003);
                    pst.setInt(2, pedido.getNumMesa());
                    pst.execute();
                }

                try (PreparedStatement pst = conn.prepareStatement(
                        "SELECT 1 FROM mesas m WHERE m.numero_mesa = ? "
                                + "AND UPPER(TRIM(m.estado)) = 'LIBRE' "
                                + "AND NOT EXISTS (SELECT 1 FROM pedidos p "
                                + "WHERE p.numero_mesa = m.numero_mesa "
                                + "AND UPPER(TRIM(p.estado)) = 'PENDIENTE') FOR UPDATE")) {
                    pst.setInt(1, pedido.getNumMesa());
                    try (ResultSet rs = pst.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            return false;
                        }
                    }
                }

                try (PreparedStatement pst = conn.prepareStatement(
                        sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                    pst.setInt(1, pedido.getNumMesa());
                    pst.setString(2, pedido.getDni());
                    pst.setString(3, estado);
                    pst.setDouble(4, pedido.getTotal());
                    pst.setString(5, pedido.getMetodoPago());
                    pst.executeUpdate();

                    try (ResultSet keys = pst.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("No se pudo obtener el ID del pedido creado.");
                        }
                        pedido.setIdPedido(keys.getInt(1));
                    }
                }

                if (pedido.getDetalles() != null && !pedido.getDetalles().isEmpty()) {
                    try (PreparedStatement pst = conn.prepareStatement(sqlDetalle)) {
                        for (DetallePedido detalle : pedido.getDetalles()) {
                            detalle.setIdPedido(pedido.getIdPedido());
                            pst.setInt(1, pedido.getIdPedido());
                            pst.setInt(2, detalle.getIdPlato());
                            pst.setInt(3, detalle.getCantidad());
                            pst.setDouble(4, detalle.getPrecioUnitario());
                            pst.setDouble(5, detalle.getSubtotal());
                            pst.addBatch();
                        }
                        pst.executeBatch();
                    }
                }

                conn.commit();
                return true;
            } catch (SQLException ex) {
                rollback(conn, ex);
                throw ex;
            }
        }
    }

    public boolean agregarDetalles(int idPedido, List<DetallePedido> detalles) throws SQLException {
        if (detalles == null || detalles.isEmpty()) {
            throw new SQLException("Agrega al menos un plato al pedido.");
        }

        String sqlDetalle = "INSERT INTO detalle_pedido "
                + "(idpedido, idplato, cantidad, precio, subtotal) VALUES (?, ?, ?, ?, ?)";
        double incrementoTotal = detalles.stream().mapToDouble(DetallePedido::getSubtotal).sum();

        try (Connection conn = ConexionDB.getConexion()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement pst = conn.prepareStatement(
                        "SELECT 1 FROM pedidos WHERE idpedido = ? "
                                + "AND UPPER(TRIM(estado)) = 'PENDIENTE' FOR UPDATE")) {
                    pst.setInt(1, idPedido);
                    try (ResultSet rs = pst.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            return false;
                        }
                    }
                }

                try (PreparedStatement pst = conn.prepareStatement(sqlDetalle)) {
                    for (DetallePedido detalle : detalles) {
                        detalle.setIdPedido(idPedido);
                        pst.setInt(1, idPedido);
                        pst.setInt(2, detalle.getIdPlato());
                        pst.setInt(3, detalle.getCantidad());
                        pst.setDouble(4, detalle.getPrecioUnitario());
                        pst.setDouble(5, detalle.getSubtotal());
                        pst.addBatch();
                    }
                    pst.executeBatch();
                }

                try (PreparedStatement pst = conn.prepareStatement(
                        "UPDATE pedidos SET total = COALESCE(total, 0) + ? "
                                + "WHERE idpedido = ? AND UPPER(TRIM(estado)) = 'PENDIENTE'")) {
                    pst.setDouble(1, incrementoTotal);
                    pst.setInt(2, idPedido);
                    if (pst.executeUpdate() == 0) {
                        conn.rollback();
                        return false;
                    }
                }

                conn.commit();
                return true;
            } catch (SQLException ex) {
                rollback(conn, ex);
                throw ex;
            }
        }
    }

    public List<DetallePedido> listarDetallesPorPedido(int idPedido) throws SQLException {
        List<DetallePedido> lista = new ArrayList<>();
        String sql = "SELECT dp.iddetalle, dp.idpedido, dp.idplato, pl.nombre AS nombre_plato, "
                + "dp.cantidad, dp.precio, dp.subtotal "
                + "FROM detalle_pedido dp "
                + "LEFT JOIN platos pl ON dp.idplato = pl.idplato "
                + "WHERE dp.idpedido = ? ORDER BY dp.iddetalle";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, idPedido);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    DetallePedido detalle = new DetallePedido();
                    detalle.setIdDetalle(rs.getInt("iddetalle"));
                    detalle.setIdPedido(rs.getInt("idpedido"));
                    detalle.setIdPlato(rs.getInt("idplato"));
                    detalle.setNombrePlato(rs.getString("nombre_plato"));
                    detalle.setCantidad(rs.getInt("cantidad"));
                    detalle.setPrecioUnitario(rs.getDouble("precio"));
                    detalle.setSubtotal(rs.getDouble("subtotal"));
                    lista.add(detalle);
                }
            }
        }

        return lista;
    }

    public boolean actualizar(Pedido pedido) throws SQLException {
        String sql = "UPDATE pedidos SET numero_mesa = ?, dni = ?, estado = ?, "
                + "total = ?, metodo_pago = ? WHERE idpedido = ?";
        String estado = normalizarEstado(pedido.getEstado());

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, pedido.getNumMesa());
            pst.setString(2, pedido.getDni());
            pst.setString(3, estado);
            pst.setDouble(4, pedido.getTotal());
            pst.setString(5, pedido.getMetodoPago());
            pst.setInt(6, pedido.getIdPedido());
            return pst.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int idPedido) throws SQLException {
        try (Connection conn = ConexionDB.getConexion()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement pst = conn.prepareStatement(
                        "SELECT 1 FROM pedidos WHERE idpedido = ? FOR UPDATE")) {
                    pst.setInt(1, idPedido);
                    try (ResultSet rs = pst.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            return false;
                        }
                    }
                }

                try (PreparedStatement pst = conn.prepareStatement(
                        "DELETE FROM detalle_pedido WHERE idpedido = ?")) {
                    pst.setInt(1, idPedido);
                    pst.executeUpdate();
                }

                try (PreparedStatement pst = conn.prepareStatement(
                        "DELETE FROM pedidos WHERE idpedido = ?")) {
                    pst.setInt(1, idPedido);
                    boolean eliminado = pst.executeUpdate() > 0;
                    conn.commit();
                    return eliminado;
                }
            } catch (SQLException ex) {
                rollback(conn, ex);
                throw ex;
            }
        }
    }

    private String normalizarEstado(String estado) throws SQLException {
        if (estado == null) {
            throw new SQLException("El estado del pedido debe ser PENDIENTE o ATENDIDO.");
        }
        String normalizado = estado.trim().toUpperCase(Locale.ROOT);
        if (!"PENDIENTE".equals(normalizado) && !"ATENDIDO".equals(normalizado)) {
            throw new SQLException("El estado del pedido debe ser PENDIENTE o ATENDIDO.");
        }
        return normalizado;
    }

    private void rollback(Connection conn, SQLException ex) {
        try {
            conn.rollback();
        } catch (SQLException rollbackError) {
            ex.addSuppressed(rollbackError);
        }
    }
}

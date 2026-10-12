package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import model.Reserva;

/**
 * Acceso a la tabla "reservas" de PostgreSQL.
 * La tabla se crea sola la primera vez si todavía no existe (requiere que exista "mesas").
 * Los métodos lanzan SQLException para que la vista pueda avisar al usuario si algo falla.
 */
public class ReservaDAO {

    private static final DateTimeFormatter FECHA =
            DateTimeFormatter.ofPattern("d/M/yyyy");

    private static final DateTimeFormatter HORA =
            DateTimeFormatter.ofPattern("H:mm");

    private static final DateTimeFormatter HORA_VISTA =
            DateTimeFormatter.ofPattern("HH:mm");

    // Listar todas las reservas
    public List<Reserva> listarTodas() throws SQLException {
        List<Reserva> lista = new ArrayList<>();

        String sql = "SELECT idreserva, cliente, fecha, hora, "
                + "cantidad_personas, numero_mesa, estado "
                + "FROM reservas ORDER BY idreserva";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                lista.add(new Reserva(
                        rs.getInt("idreserva"),
                        rs.getString("cliente"),
                        rs.getDate("fecha").toLocalDate().format(FECHA),
                        rs.getTime("hora").toLocalTime().format(HORA_VISTA),
                        rs.getInt("cantidad_personas"),
                        rs.getInt("numero_mesa"),
                        rs.getString("estado")
                ));
            }
        }

        return lista;
    }

    // Insertar una reserva
    public Reserva insertar(
            String cliente,
            String fecha,
            String hora,
            int personas,
            int mesa,
            String estado
    ) throws SQLException {

        String sql = "INSERT INTO reservas "
                + "(cliente, fecha, hora, cantidad_personas, "
                + "numero_mesa, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pst = conn.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, cliente);

            pst.setDate(2, java.sql.Date.valueOf(
                    LocalDate.parse(fecha, FECHA)));

            pst.setTime(3, java.sql.Time.valueOf(
                    LocalTime.parse(hora, HORA)));

            pst.setInt(4, personas);
            pst.setInt(5, mesa);
            pst.setString(6, estado);

            pst.executeUpdate();

            try (ResultSet keys = pst.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Reserva(
                            keys.getInt(1),
                            cliente,
                            fecha,
                            LocalTime.parse(hora, HORA)
                                    .format(HORA_VISTA),
                            personas,
                            mesa,
                            estado
                    );
                }
            }

            throw new SQLException(
                    "No se pudo obtener el ID de la reserva creada."
            );
        }
    }

    // Eliminar una reserva
    public boolean eliminar(int idReserva) throws SQLException {
        String sql =
                "DELETE FROM reservas WHERE idreserva = ?";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, idReserva);

            return pst.executeUpdate() > 0;
        }
    }

    // Actualizar el estado de una reserva
    public boolean actualizarEstado(
            int idReserva,
            String estado
    ) throws SQLException {

        String sql =
                "UPDATE reservas SET estado = ? WHERE idreserva = ?";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, estado);
            pst.setInt(2, idReserva);

            return pst.executeUpdate() > 0;
        }
    }
}
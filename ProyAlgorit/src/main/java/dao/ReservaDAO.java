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

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("d/M/yyyy");
    private static final DateTimeFormatter HORA  = DateTimeFormatter.ofPattern("H:mm");
    private static final DateTimeFormatter HORA_VISTA = DateTimeFormatter.ofPattern("HH:mm");

    public List<Reserva> listarTodas() throws SQLException {
        List<Reserva> lista = new ArrayList<>();
        String sql = "SELECT idreserva, cliente, fecha, hora, cantidad_personas, numero_mesa, estado "
                + "FROM reservas ORDER BY idreserva";

        try (Connection conn = ConexionDB.getConexion()) {
            try (PreparedStatement pst = conn.prepareStatement(sql);
                 ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Reserva(
                            rs.getInt("idreserva"),
                            rs.getString("cliente"),
                            rs.getDate("fecha").toLocalDate().format(FECHA),
                            rs.getTime("hora").toLocalTime().format(HORA_VISTA),
                            rs.getInt("cantidad_personas"),
                            rs.getInt("numero_mesa"),
                            rs.getString("estado")));
                }
            }
        }
        return lista;
    }

    /** Inserta la reserva y le asigna el ID que generó la base de datos. */
    public Reserva insertar(String cliente, String fecha, String hora,
                               int personas, int mesa, String estado) throws SQLException {
        String sql = "INSERT INTO reservas (cliente, fecha, hora, cantidad_personas, numero_mesa, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.getConexion()) {
            try (PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pst.setString(1, cliente);
                pst.setDate(2, java.sql.Date.valueOf(LocalDate.parse(fecha, FECHA)));
                pst.setTime(3, java.sql.Time.valueOf(LocalTime.parse(hora, HORA)));
                pst.setInt(4, personas);
                pst.setInt(5, mesa);
                pst.setString(6, estado);
                pst.executeUpdate();

                try (ResultSet keys = pst.getGeneratedKeys()) {
                    keys.next();
                    return new Reserva(keys.getInt(1), cliente, fecha,
                            LocalTime.parse(hora, HORA).format(HORA_VISTA), personas, mesa, estado);
                }
            }
        }
    }

    public boolean eliminar(int idReserva) throws SQLException {
        try (Connection conn = ConexionDB.getConexion()) {
            try (PreparedStatement pst = conn.prepareStatement("DELETE FROM reservas WHERE idreserva = ?")) {
                pst.setInt(1, idReserva);
                return pst.executeUpdate() > 0;
            }
        }
    }
}

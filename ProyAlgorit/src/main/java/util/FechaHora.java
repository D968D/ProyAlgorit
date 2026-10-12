package util;

import model.Reserva;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/** Convierte la fecha ("d/M/aaaa") y la hora ("H:mm") de una reserva en un solo valor comparable. */
public final class FechaHora {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("d/M/yyyy");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("H:mm");

    private FechaHora() { }

    /** Si el texto no se puede leer, la reserva se considera la más lejana (queda al final). */
    public static LocalDateTime de(Reserva r) {
        try {
            return java.time.LocalDate.parse(r.getFecha(), FECHA)
                    .atTime(java.time.LocalTime.parse(r.getHora(), HORA));
        } catch (DateTimeParseException | NullPointerException e) {
            return LocalDateTime.MAX;
        }
    }
}

package estructura;

import model.Reserva;
import util.FechaHora;

/**
 * Cola con prioridad de reservas pendientes (Capítulo 7).
 *
 * Prioridad: la reserva con fecha y hora MÁS PRÓXIMA se atiende primero.
 * Si dos reservas tienen la misma fecha y hora, se respeta el orden de llegada (FIFO).
 *
 * Implementación: lista enlazada que se mantiene ordenada al encolar (inserción ordenada),
 * así desencolar y consultar el primero son O(1) y encolar es O(n).
 */
public class ColaPrioridadReservas {

    private NodoColaReserva frente;
    private int tamanio;

    public boolean estaVacia() {
        return frente == null;
    }

    public int tamanio() {
        return tamanio;
    }

    /** ENQUEUE con prioridad: se coloca antes de la primera reserva más lejana en el tiempo. */
    public void encolar(Reserva reserva) {
        if (reserva == null) {
            throw new IllegalArgumentException("La reserva no puede ser nula");
        }
        NodoColaReserva nuevo = new NodoColaReserva(reserva);
        tamanio++;

        if (frente == null || FechaHora.de(reserva).isBefore(FechaHora.de(frente.getReserva()))) {
            nuevo.setSiguiente(frente);
            frente = nuevo;
            return;
        }
        NodoColaReserva actual = frente;
        // avanza mientras el siguiente tenga igual o mayor prioridad (empate = llegó antes)
        while (actual.getSiguiente() != null
                && !FechaHora.de(reserva).isBefore(FechaHora.de(actual.getSiguiente().getReserva()))) {
            actual = actual.getSiguiente();
        }
        nuevo.setSiguiente(actual.getSiguiente());
        actual.setSiguiente(nuevo);
    }

    /** DEQUEUE: retira la reserva más próxima en el tiempo. Devuelve null si la cola está vacía. */
    public Reserva desencolar() {
        if (estaVacia()) {
            return null;
        }
        Reserva r = frente.getReserva();
        frente = frente.getSiguiente();
        tamanio--;
        return r;
    }

    public Reserva primero() {
        return estaVacia() ? null : frente.getReserva();
    }

    public boolean eliminarPorId(int id) {
        NodoColaReserva actual = frente;
        NodoColaReserva anterior = null;
        while (actual != null) {
            if (actual.getReserva().getIdReserva() == id) {
                if (anterior == null) {
                    frente = actual.getSiguiente();
                } else {
                    anterior.setSiguiente(actual.getSiguiente());
                }
                tamanio--;
                return true;
            }
            anterior = actual;
            actual = actual.getSiguiente();
        }
        return false;
    }

    public String recorrer() {
        if (estaVacia()) {
            return "No hay reservas pendientes.\n";
        }
        StringBuilder sb = new StringBuilder();
        int posicion = 1;
        for (NodoColaReserva a = frente; a != null; a = a.getSiguiente()) {
            Reserva r = a.getReserva();
            sb.append(posicion++).append(". ").append(r.getFecha()).append(' ').append(r.getHora())
                    .append(" - Reserva N° ").append(r.getIdReserva())
                    .append(" - ").append(r.getCliente()).append('\n');
        }
        return sb.toString();
    }
}


package estructura;

import model.Reserva;

public class ColaReservas {

    private NodoColaReserva frente;
    private NodoColaReserva fin;

    public ColaReservas() {
        frente = null;
        fin = null;
    }

    // Validar si la cola está vacía
    public boolean estaVacia() {
        return frente == null;
    }

    // ENQUEUE: agregar al final
    public void encolar(Reserva reserva) {
        if (reserva == null) {
            throw new IllegalArgumentException(
                    "La reserva no puede ser nula"
            );
        }

        NodoColaReserva nuevo =
                new NodoColaReserva(reserva);

        if (estaVacia()) {
            frente = nuevo;
        } else {
            fin.setSiguiente(nuevo);
        }

        fin = nuevo;
    }

    // DEQUEUE: retirar el primer elemento
    public Reserva desencolar() {
        if (estaVacia()) {
            return null;
        }

        Reserva reserva = frente.getReserva();
        frente = frente.getSiguiente();

        if (frente == null) {
            fin = null;
        }

        return reserva;
    }

    // Consultar el primero sin eliminarlo
    public Reserva primero() {
        if (estaVacia()) {
            return null;
        }

        return frente.getReserva();
    }

    // Recorrer todos los elementos
    public String recorrer() {
        if (estaVacia()) {
            return "No hay reservas pendientes.";
        }

        StringBuilder resultado = new StringBuilder();
        NodoColaReserva actual = frente;

        while (actual != null) {
            Reserva r = actual.getReserva();

            resultado.append("Reserva N° ")
                    .append(r.getIdReserva())
                    .append(" - Cliente: ")
                    .append(r.getCliente())
                    .append("\n");

            actual = actual.getSiguiente();
        }

        return resultado.toString();
    }

    // Eliminar una reserva por su ID
    public void eliminarPorId(int id) {
        NodoColaReserva actual = frente;
        NodoColaReserva anterior = null;

        while (actual != null) {
            if (actual.getReserva().getIdReserva() == id) {

                if (anterior == null) {
                    frente = actual.getSiguiente();
                } else {
                    anterior.setSiguiente(
                            actual.getSiguiente()
                    );
                }

                if (actual == fin) {
                    fin = anterior;
                }

                return;
            }

            anterior = actual;
            actual = actual.getSiguiente();
        }
    }
}

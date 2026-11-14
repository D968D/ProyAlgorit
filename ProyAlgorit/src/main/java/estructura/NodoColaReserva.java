package estructura;

import model.Reserva;

public class NodoColaReserva {

    private Reserva reserva;
    private NodoColaReserva siguiente;

    public NodoColaReserva(Reserva reserva) {
        this.reserva = reserva;
        this.siguiente = null;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public NodoColaReserva getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoColaReserva siguiente) {
        this.siguiente = siguiente;
    }
}

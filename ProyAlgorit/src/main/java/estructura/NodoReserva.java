package estructura;

import model.Reserva;

public class NodoReserva {

    private Reserva reserva;
    private NodoReserva siguiente;

    public NodoReserva(Reserva reserva) {
        this.reserva = reserva;
        this.siguiente = null;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public NodoReserva getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoReserva siguiente) {
        this.siguiente = siguiente;
    }
}
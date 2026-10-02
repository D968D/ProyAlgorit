package estructura;

import tad.TADReserva;

public class NodoReserva {

    private TADReserva reserva;
    private NodoReserva siguiente;

    public NodoReserva(TADReserva reserva) {
        this.reserva = reserva;
        this.siguiente = null;
    }

    public TADReserva getReserva() {
        return reserva;
    }

    public void setReserva(TADReserva reserva) {
        this.reserva = reserva;
    }

    public NodoReserva getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoReserva siguiente) {
        this.siguiente = siguiente;
    }
}
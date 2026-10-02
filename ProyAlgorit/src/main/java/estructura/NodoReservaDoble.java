package estructura;

import tad.TADReserva;

public class NodoReservaDoble {

    private TADReserva reserva;
    private NodoReservaDoble anterior;
    private NodoReservaDoble siguiente;

    public NodoReservaDoble(TADReserva reserva) {
        this.reserva = reserva;
        this.anterior = null;
        this.siguiente = null;
    }

    public TADReserva getReserva() {
        return reserva;
    }

    public void setReserva(TADReserva reserva) {
        this.reserva = reserva;
    }

    public NodoReservaDoble getAnterior() {
        return anterior;
    }

    public void setAnterior(NodoReservaDoble anterior) {
        this.anterior = anterior;
    }

    public NodoReservaDoble getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoReservaDoble siguiente) {
        this.siguiente = siguiente;
    }
}
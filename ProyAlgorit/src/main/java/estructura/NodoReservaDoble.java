package estructura;

import model.Reserva;

public class NodoReservaDoble {

    private Reserva reserva;
    private NodoReservaDoble anterior;
    private NodoReservaDoble siguiente;

    public NodoReservaDoble(Reserva reserva) {
        this.reserva = reserva;
        this.anterior = null;
        this.siguiente = null;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
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
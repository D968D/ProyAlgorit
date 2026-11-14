package estructura;

import model.Reserva;

/** Nodo del árbol AVL de reservas: guarda la reserva, su altura y dos hijos. */
public class NodoAVL {

    private Reserva reserva;
    private int altura;
    private NodoAVL izquierdo;
    private NodoAVL derecho;

    public NodoAVL(Reserva reserva) {
        this.reserva = reserva;
        this.altura = 1; // una hoja tiene altura 1
        this.izquierdo = null;
        this.derecho = null;
    }

    public Reserva getReserva() { return reserva; }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }
    public int getAltura() { return altura; }
    public void setAltura(int altura) { this.altura = altura; }
    public NodoAVL getIzquierdo() { return izquierdo; }
    public void setIzquierdo(NodoAVL izquierdo) { this.izquierdo = izquierdo; }
    public NodoAVL getDerecho() { return derecho; }
    public void setDerecho(NodoAVL derecho) { this.derecho = derecho; }
}

package estructura;

import model.Plato;

/** Nodo del árbol binario de la carta: guarda un plato y dos hijos. */
public class NodoPlato {

    private Plato plato;
    private NodoPlato izquierdo;
    private NodoPlato derecho;

    public NodoPlato(Plato plato) {
        this.plato = plato;
        this.izquierdo = null;
        this.derecho = null;
    }

    public Plato getPlato() {
        return plato;
    }

    public void setPlato(Plato plato) {
        this.plato = plato;
    }

    public NodoPlato getIzquierdo() {
        return izquierdo;
    }

    public void setIzquierdo(NodoPlato izquierdo) {
        this.izquierdo = izquierdo;
    }

    public NodoPlato getDerecho() {
        return derecho;
    }

    public void setDerecho(NodoPlato derecho) {
        this.derecho = derecho;
    }
}

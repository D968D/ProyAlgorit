package estructura;

import model.Plato;

/**
 * Árbol binario de la carta (Capítulo 8).
 *
 * Es un árbol binario COMPLETO: los platos se insertan en el primer lugar libre
 * recorriendo por niveles (de arriba hacia abajo y de izquierda a derecha).
 * No ordena por clave (eso lo hace el ABB del Capítulo 9); aquí el orden lo da
 * la llegada de los platos, y sirve para mostrar la carta por niveles y
 * recorrerla en preorden, inorden y postorden.
 *
 * La posición de cada nodo se numera 1, 2, 3...: los hijos del nodo p son 2p y 2p+1.
 * Con los bits de esa posición se llega a cualquier nodo sin usar colas ni colecciones.
 */
public class ArbolBinarioPlatos {

    private NodoPlato raiz;
    private int cantidad;

    public ArbolBinarioPlatos() {
        raiz = null;
        cantidad = 0;
    }

    public boolean estaVacio() {
        return raiz == null;
    }

    public int getCantidad() {
        return cantidad;
    }

    public NodoPlato getRaiz() {
        return raiz;
    }

    /** Camino desde la raíz hasta la posición indicada: devuelve el nodo en esa posición. */
    private NodoPlato nodoEnPosicion(int posicion) {
        NodoPlato actual = raiz;
        int bits = 31 - Integer.numberOfLeadingZeros(posicion); // niveles por bajar
        for (int i = bits - 1; i >= 0 && actual != null; i--) {
            actual = ((posicion >> i) & 1) == 0 ? actual.getIzquierdo() : actual.getDerecho();
        }
        return actual;
    }

    // ---------- INSERCIÓN ----------
    public void insertar(Plato plato) {
        if (plato == null) {
            throw new IllegalArgumentException("El plato no puede ser nulo");
        }
        NodoPlato nuevo = new NodoPlato(plato);
        cantidad++;
        if (raiz == null) {
            raiz = nuevo;
            return;
        }
        NodoPlato padre = nodoEnPosicion(cantidad / 2);
        if (cantidad % 2 == 0) {
            padre.setIzquierdo(nuevo);
        } else {
            padre.setDerecho(nuevo);
        }
    }

    // ---------- BÚSQUEDA (por id, recorriendo en preorden) ----------
    public Plato buscar(int idPlato) {
        return buscar(raiz, idPlato);
    }

    private Plato buscar(NodoPlato nodo, int idPlato) {
        if (nodo == null) {
            return null;
        }
        if (nodo.getPlato().getIdPlato() == idPlato) {
            return nodo.getPlato();
        }
        Plato enIzq = buscar(nodo.getIzquierdo(), idPlato);
        if (enIzq != null) {
            return enIzq;
        }
        return buscar(nodo.getDerecho(), idPlato);
    }

    // ---------- ELIMINACIÓN ----------
    /**
     * Elimina el plato con ese id: el nodo toma el plato del ÚLTIMO nodo del árbol
     * (el más profundo y a la derecha) y ese último nodo se desconecta.
     * Así el árbol sigue siendo completo.
     */
    public boolean eliminar(int idPlato) {
        NodoPlato objetivo = buscarNodo(raiz, idPlato);
        if (objetivo == null) {
            return false;
        }
        if (cantidad == 1) {
            raiz = null;
            cantidad = 0;
            return true;
        }
        NodoPlato padreUltimo = nodoEnPosicion(cantidad / 2);
        NodoPlato ultimo;
        if (cantidad % 2 == 0) {
            ultimo = padreUltimo.getIzquierdo();
            padreUltimo.setIzquierdo(null);
        } else {
            ultimo = padreUltimo.getDerecho();
            padreUltimo.setDerecho(null);
        }
        cantidad--;
        if (objetivo != ultimo) {
            objetivo.setPlato(ultimo.getPlato());
        }
        return true;
    }

    private NodoPlato buscarNodo(NodoPlato nodo, int idPlato) {
        if (nodo == null) {
            return null;
        }
        if (nodo.getPlato().getIdPlato() == idPlato) {
            return nodo;
        }
        NodoPlato enIzq = buscarNodo(nodo.getIzquierdo(), idPlato);
        return enIzq != null ? enIzq : buscarNodo(nodo.getDerecho(), idPlato);
    }

    // ---------- ALTURA ----------
    public int altura() {
        return altura(raiz);
    }

    private int altura(NodoPlato nodo) {
        if (nodo == null) {
            return 0;
        }
        return 1 + Math.max(altura(nodo.getIzquierdo()), altura(nodo.getDerecho()));
    }

    // ---------- RECORRIDOS ----------
    /** Raíz → izquierdo → derecho */
    public String preorden() {
        StringBuilder sb = new StringBuilder();
        preorden(raiz, sb);
        return sb.toString().trim();
    }

    private void preorden(NodoPlato nodo, StringBuilder sb) {
        if (nodo == null) {
            return;
        }
        sb.append(etiqueta(nodo)).append("  ");
        preorden(nodo.getIzquierdo(), sb);
        preorden(nodo.getDerecho(), sb);
    }

    /** Izquierdo → raíz → derecho */
    public String inorden() {
        StringBuilder sb = new StringBuilder();
        inorden(raiz, sb);
        return sb.toString().trim();
    }

    private void inorden(NodoPlato nodo, StringBuilder sb) {
        if (nodo == null) {
            return;
        }
        inorden(nodo.getIzquierdo(), sb);
        sb.append(etiqueta(nodo)).append("  ");
        inorden(nodo.getDerecho(), sb);
    }

    /** Izquierdo → derecho → raíz */
    public String postorden() {
        StringBuilder sb = new StringBuilder();
        postorden(raiz, sb);
        return sb.toString().trim();
    }

    private void postorden(NodoPlato nodo, StringBuilder sb) {
        if (nodo == null) {
            return;
        }
        postorden(nodo.getIzquierdo(), sb);
        postorden(nodo.getDerecho(), sb);
        sb.append(etiqueta(nodo)).append("  ");
    }

    private String etiqueta(NodoPlato nodo) {
        return nodo.getPlato().getIdPlato() + "-" + nodo.getPlato().getNombre();
    }
}

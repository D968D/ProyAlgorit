package estructura;

import model.Plato;

/**
 * Árbol Binario de Búsqueda de platos (Capítulo 9). La clave es el NOMBRE del plato:
 * a la izquierda van los nombres menores (alfabéticamente) y a la derecha los mayores,
 * así buscar un plato por nombre no recorre toda la carta, y el recorrido inorden
 * entrega la carta ordenada de la A a la Z.
 * No se balancea: según el orden de inserción puede quedar muy inclinado (el AVL lo corrige).
 */
public class ArbolABBPlatos {

    private NodoPlato raiz;
    private int cantidad;
    private int comparaciones;      // comparaciones de la última búsqueda
    private boolean huboCambio;

    public boolean estaVacio() { return raiz == null; }
    public int getCantidad() { return cantidad; }
    public NodoPlato getRaiz() { return raiz; }
    public int getComparaciones() { return comparaciones; }
    public int altura() { return altura(raiz); }

    private int comparar(String a, String b) {
        return a.compareToIgnoreCase(b);
    }

    // ---------- INSERCIÓN ----------
    /** Inserta el plato. Devuelve false si ya existe un plato con ese nombre. */
    public boolean insertar(Plato plato) {
        if (plato == null) {
            throw new IllegalArgumentException("El plato no puede ser nulo");
        }
        huboCambio = false;
        raiz = insertar(raiz, plato);
        if (huboCambio) {
            cantidad++;
        }
        return huboCambio;
    }

    private NodoPlato insertar(NodoPlato nodo, Plato plato) {
        if (nodo == null) {
            huboCambio = true;
            return new NodoPlato(plato);
        }
        int c = comparar(plato.getNombre(), nodo.getPlato().getNombre());
        if (c < 0) {
            nodo.setIzquierdo(insertar(nodo.getIzquierdo(), plato));
        } else if (c > 0) {
            nodo.setDerecho(insertar(nodo.getDerecho(), plato));
        }
        return nodo;
    }

    // ---------- BÚSQUEDA ----------
    public Plato buscar(String nombre) {
        comparaciones = 0;
        NodoPlato actual = raiz;
        while (actual != null) {
            comparaciones++;
            int c = comparar(nombre, actual.getPlato().getNombre());
            if (c == 0) {
                return actual.getPlato();
            }
            actual = c < 0 ? actual.getIzquierdo() : actual.getDerecho();
        }
        return null;
    }

    // ---------- ELIMINACIÓN ----------
    /** Elimina el plato con ese nombre. Casos: hoja, un hijo o dos hijos (sucesor inorden). */
    public boolean eliminar(String nombre) {
        huboCambio = false;
        raiz = eliminar(raiz, nombre);
        if (huboCambio) {
            cantidad--;
        }
        return huboCambio;
    }

    private NodoPlato eliminar(NodoPlato nodo, String nombre) {
        if (nodo == null) {
            return null;
        }
        int c = comparar(nombre, nodo.getPlato().getNombre());
        if (c < 0) {
            nodo.setIzquierdo(eliminar(nodo.getIzquierdo(), nombre));
        } else if (c > 0) {
            nodo.setDerecho(eliminar(nodo.getDerecho(), nombre));
        } else {
            huboCambio = true;
            if (nodo.getIzquierdo() == null) {
                return nodo.getDerecho();
            }
            if (nodo.getDerecho() == null) {
                return nodo.getIzquierdo();
            }
            NodoPlato sucesor = nodo.getDerecho();
            while (sucesor.getIzquierdo() != null) {
                sucesor = sucesor.getIzquierdo();
            }
            nodo.setPlato(sucesor.getPlato());
            boolean marca = huboCambio;
            nodo.setDerecho(eliminar(nodo.getDerecho(), sucesor.getPlato().getNombre()));
            huboCambio = marca;
        }
        return nodo;
    }

    private int altura(NodoPlato n) {
        return n == null ? 0 : 1 + Math.max(altura(n.getIzquierdo()), altura(n.getDerecho()));
    }

    // ---------- RECORRIDOS ----------
    public String preorden() {
        StringBuilder sb = new StringBuilder();
        preorden(raiz, sb);
        return sb.toString().trim();
    }

    private void preorden(NodoPlato n, StringBuilder sb) {
        if (n == null) return;
        sb.append(n.getPlato().getNombre()).append("  ");
        preorden(n.getIzquierdo(), sb);
        preorden(n.getDerecho(), sb);
    }

    /** Carta ordenada alfabéticamente. */
    public String inorden() {
        StringBuilder sb = new StringBuilder();
        inorden(raiz, sb);
        return sb.toString().trim();
    }

    private void inorden(NodoPlato n, StringBuilder sb) {
        if (n == null) return;
        inorden(n.getIzquierdo(), sb);
        sb.append(n.getPlato().getNombre()).append("  ");
        inorden(n.getDerecho(), sb);
    }

    public String postorden() {
        StringBuilder sb = new StringBuilder();
        postorden(raiz, sb);
        return sb.toString().trim();
    }

    private void postorden(NodoPlato n, StringBuilder sb) {
        if (n == null) return;
        postorden(n.getIzquierdo(), sb);
        postorden(n.getDerecho(), sb);
        sb.append(n.getPlato().getNombre()).append("  ");
    }
}

package estructura;

import model.Reserva;

/**
 * Árbol AVL de reservas (Capítulo 10). La clave es el ID de la reserva.
 *
 * Un AVL es un ABB que se mantiene balanceado: en cada nodo la diferencia de
 * alturas entre el subárbol izquierdo y el derecho (factor de equilibrio) es -1, 0 o 1.
 * Si al insertar o eliminar el factor sale de ese rango, se rota:
 *   - LL: rotación simple a la derecha
 *   - RR: rotación simple a la izquierda
 *   - LR: rotación doble (izquierda sobre el hijo izquierdo, luego derecha)
 *   - RL: rotación doble (derecha sobre el hijo derecho, luego izquierda)
 * Cada rotación se anota en un registro (getRegistro) para mostrarlo en pantalla.
 */
public class ArbolAVLReservas {

    private NodoAVL raiz;
    private int cantidad;
    private int rotaciones;           // rotaciones de la última operación
    private boolean huboCambio;       // resultado interno de insertar/eliminar recursivos
    private StringBuilder registro = new StringBuilder();

    public boolean estaVacio() { return raiz == null; }
    public int getCantidad() { return cantidad; }
    public NodoAVL getRaiz() { return raiz; }
    public int getRotaciones() { return rotaciones; }
    public int altura() { return altura(raiz); }

    /** Texto con las rotaciones de la última inserción o eliminación. */
    public String getRegistro() {
        return registro.length() == 0
                ? "Sin rotaciones: el árbol siguió balanceado."
                : registro.toString().trim();
    }

    private void iniciarOperacion() {
        registro = new StringBuilder();
        rotaciones = 0;
    }

    // ---------- utilidades de altura y equilibrio ----------
    private int altura(NodoAVL n) {
        return n == null ? 0 : n.getAltura();
    }

    private void actualizarAltura(NodoAVL n) {
        n.setAltura(1 + Math.max(altura(n.getIzquierdo()), altura(n.getDerecho())));
    }

    /** Factor de equilibrio = altura(izquierdo) - altura(derecho). */
    public int factorEquilibrio(NodoAVL n) {
        return n == null ? 0 : altura(n.getIzquierdo()) - altura(n.getDerecho());
    }

    private int idDe(NodoAVL n) {
        return n.getReserva().getIdReserva();
    }

    // ---------- rotaciones ----------
    /** Rotación simple a la derecha: el hijo izquierdo pasa a ser la raíz del subárbol. */
    private NodoAVL rotarDerecha(NodoAVL y) {
        NodoAVL x = y.getIzquierdo();
        y.setIzquierdo(x.getDerecho());
        x.setDerecho(y);
        actualizarAltura(y);
        actualizarAltura(x);
        rotaciones++;
        return x;
    }

    /** Rotación simple a la izquierda: el hijo derecho pasa a ser la raíz del subárbol. */
    private NodoAVL rotarIzquierda(NodoAVL x) {
        NodoAVL y = x.getDerecho();
        x.setDerecho(y.getIzquierdo());
        y.setIzquierdo(x);
        actualizarAltura(x);
        actualizarAltura(y);
        rotaciones++;
        return y;
    }

    /** Revisa el nodo y, si está desbalanceado, aplica la rotación que corresponda. */
    private NodoAVL balancear(NodoAVL n) {
        actualizarAltura(n);
        int fe = factorEquilibrio(n);

        if (fe > 1) { // cargado a la izquierda
            if (factorEquilibrio(n.getIzquierdo()) < 0) {
                registro.append("Nodo ").append(idDe(n)).append(" (FE=").append(fe)
                        .append("): rotación doble izquierda-derecha (LR)\n");
                n.setIzquierdo(rotarIzquierda(n.getIzquierdo()));
            } else {
                registro.append("Nodo ").append(idDe(n)).append(" (FE=").append(fe)
                        .append("): rotación simple a la derecha (LL)\n");
            }
            return rotarDerecha(n);
        }
        if (fe < -1) { // cargado a la derecha
            if (factorEquilibrio(n.getDerecho()) > 0) {
                registro.append("Nodo ").append(idDe(n)).append(" (FE=").append(fe)
                        .append("): rotación doble derecha-izquierda (RL)\n");
                n.setDerecho(rotarDerecha(n.getDerecho()));
            } else {
                registro.append("Nodo ").append(idDe(n)).append(" (FE=").append(fe)
                        .append("): rotación simple a la izquierda (RR)\n");
            }
            return rotarIzquierda(n);
        }
        return n;
    }

    // ---------- INSERCIÓN ----------
    /** Inserta la reserva. Devuelve false si ya existe una con ese ID. */
    public boolean insertar(Reserva reserva) {
        if (reserva == null) {
            throw new IllegalArgumentException("La reserva no puede ser nula");
        }
        iniciarOperacion();
        huboCambio = false;
        raiz = insertar(raiz, reserva);
        if (huboCambio) {
            cantidad++;
        }
        return huboCambio;
    }

    private NodoAVL insertar(NodoAVL nodo, Reserva reserva) {
        if (nodo == null) {
            huboCambio = true;
            return new NodoAVL(reserva);
        }
        int id = reserva.getIdReserva();
        if (id < idDe(nodo)) {
            nodo.setIzquierdo(insertar(nodo.getIzquierdo(), reserva));
        } else if (id > idDe(nodo)) {
            nodo.setDerecho(insertar(nodo.getDerecho(), reserva));
        } else {
            return nodo; // clave repetida: no se inserta
        }
        return balancear(nodo);
    }

    // ---------- BÚSQUEDA ----------
    public Reserva buscar(int idReserva) {
        NodoAVL actual = raiz;
        while (actual != null) {
            if (idReserva == idDe(actual)) {
                return actual.getReserva();
            }
            actual = idReserva < idDe(actual) ? actual.getIzquierdo() : actual.getDerecho();
        }
        return null;
    }

    // ---------- ELIMINACIÓN ----------
    /** Elimina la reserva con ese ID y rebalancea. Devuelve false si no existe. */
    public boolean eliminar(int idReserva) {
        iniciarOperacion();
        huboCambio = false;
        raiz = eliminar(raiz, idReserva);
        if (huboCambio) {
            cantidad--;
        }
        return huboCambio;
    }

    private NodoAVL eliminar(NodoAVL nodo, int id) {
        if (nodo == null) {
            return null;
        }
        if (id < idDe(nodo)) {
            nodo.setIzquierdo(eliminar(nodo.getIzquierdo(), id));
        } else if (id > idDe(nodo)) {
            nodo.setDerecho(eliminar(nodo.getDerecho(), id));
        } else {
            huboCambio = true;
            if (nodo.getIzquierdo() == null) {
                return nodo.getDerecho();
            }
            if (nodo.getDerecho() == null) {
                return nodo.getIzquierdo();
            }
            // dos hijos: se reemplaza por el sucesor inorden (menor del subárbol derecho)
            NodoAVL sucesor = nodo.getDerecho();
            while (sucesor.getIzquierdo() != null) {
                sucesor = sucesor.getIzquierdo();
            }
            nodo.setReserva(sucesor.getReserva());
            boolean marca = huboCambio;
            nodo.setDerecho(eliminar(nodo.getDerecho(), idDe(sucesor)));
            huboCambio = marca;
        }
        return balancear(nodo);
    }

    // ---------- RECORRIDOS ----------
    public String preorden() {
        StringBuilder sb = new StringBuilder();
        preorden(raiz, sb);
        return sb.toString().trim();
    }

    private void preorden(NodoAVL n, StringBuilder sb) {
        if (n == null) return;
        sb.append(idDe(n)).append("  ");
        preorden(n.getIzquierdo(), sb);
        preorden(n.getDerecho(), sb);
    }

    /** Devuelve los IDs ordenados de menor a mayor. */
    public String inorden() {
        StringBuilder sb = new StringBuilder();
        inorden(raiz, sb);
        return sb.toString().trim();
    }

    private void inorden(NodoAVL n, StringBuilder sb) {
        if (n == null) return;
        inorden(n.getIzquierdo(), sb);
        sb.append(idDe(n)).append("  ");
        inorden(n.getDerecho(), sb);
    }

    public String postorden() {
        StringBuilder sb = new StringBuilder();
        postorden(raiz, sb);
        return sb.toString().trim();
    }

    private void postorden(NodoAVL n, StringBuilder sb) {
        if (n == null) return;
        postorden(n.getIzquierdo(), sb);
        postorden(n.getDerecho(), sb);
        sb.append(idDe(n)).append("  ");
    }
}

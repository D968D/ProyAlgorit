package estructura;

import model.Pedido;
import java.util.ArrayList;
import java.util.List;

public class ArbolABBPedidos {

    public static class NodoPedido {
        public Pedido dato;
        public NodoPedido izquierdo;
        public NodoPedido derecho;

        public NodoPedido(Pedido dato) {
            this.dato = dato;
            this.izquierdo = null;
            this.derecho = null;
        }
    }

    private NodoPedido raiz;

    public ArbolABBPedidos() {
        this.raiz = null;
    }

    public NodoPedido getRaiz() {
        return raiz;
    }

    public boolean estaVacio() {
        return raiz == null;
    }

    public void limpiar() {
        raiz = null;
    }

    /**
     * Inserta un pedido en el árbol ordenado por idPedido.
     * Si el idPedido ya existe, actualiza sus datos en el nodo.
     */
    public void insertar(Pedido pedido) {
        if (pedido == null) return;
        raiz = insertarRecursivo(raiz, pedido);
    }

    private NodoPedido insertarRecursivo(NodoPedido actual, Pedido pedido) {
        if (actual == null) {
            return new NodoPedido(pedido);
        }

        if (pedido.getIdPedido() < actual.dato.getIdPedido()) {
            actual.izquierdo = insertarRecursivo(actual.izquierdo, pedido);
        } else if (pedido.getIdPedido() > actual.dato.getIdPedido()) {
            actual.derecho = insertarRecursivo(actual.derecho, pedido);
        } else {
            actual.dato = pedido;
        }
        return actual;
    }

    /**
     * Busca un pedido por su idPedido en O(h).
     */
    public Pedido buscar(int idPedido) {
        NodoPedido actual = raiz;
        while (actual != null) {
            if (idPedido == actual.dato.getIdPedido()) {
                return actual.dato;
            }
            if (idPedido < actual.dato.getIdPedido()) {
                actual = actual.izquierdo;
            } else {
                actual = actual.derecho;
            }
        }
        return null;
    }

    /**
     * Elimina un nodo del árbol por su idPedido manteniendo la propiedad ABB.
     */
    public void eliminar(int idPedido) {
        raiz = eliminarRecursivo(raiz, idPedido);
    }

    private NodoPedido eliminarRecursivo(NodoPedido actual, int idPedido) {
        if (actual == null) {
            return null;
        }

        if (idPedido < actual.dato.getIdPedido()) {
            actual.izquierdo = eliminarRecursivo(actual.izquierdo, idPedido);
        } else if (idPedido > actual.dato.getIdPedido()) {
            actual.derecho = eliminarRecursivo(actual.derecho, idPedido);
        } else {
            // Caso 1: Nodo hoja o con un solo hijo
            if (actual.izquierdo == null) {
                return actual.derecho;
            }
            if (actual.derecho == null) {
                return actual.izquierdo;
            }

            // Caso 2: Nodo con dos hijos (reemplazar por el sucesor inorden)
            NodoPedido sucesor = obtenerMinimo(actual.derecho);
            actual.dato = sucesor.dato;
            actual.derecho = eliminarRecursivo(actual.derecho, sucesor.dato.getIdPedido());
        }
        return actual;
    }

    private NodoPedido obtenerMinimo(NodoPedido nodo) {
        while (nodo.izquierdo != null) {
            nodo = nodo.izquierdo;
        }
        return nodo;
    }

    // --- Recorridos para poblar la JTable ---

    public List<Pedido> recorridoInOrden() {
        List<Pedido> resultado = new ArrayList<>();
        inOrdenRec(raiz, resultado);
        return resultado;
    }

    private void inOrdenRec(NodoPedido nodo, List<Pedido> resultado) {
        if (nodo == null) return;
        inOrdenRec(nodo.izquierdo, resultado);
        resultado.add(nodo.dato);
        inOrdenRec(nodo.derecho, resultado);
    }

    public List<Pedido> recorridoPreOrden() {
        List<Pedido> resultado = new ArrayList<>();
        preOrdenRec(raiz, resultado);
        return resultado;
    }

    private void preOrdenRec(NodoPedido nodo, List<Pedido> resultado) {
        if (nodo == null) return;
        resultado.add(nodo.dato);
        preOrdenRec(nodo.izquierdo, resultado);
        preOrdenRec(nodo.derecho, resultado);
    }

    public List<Pedido> recorridoPostOrden() {
        List<Pedido> resultado = new ArrayList<>();
        postOrdenRec(raiz, resultado);
        return resultado;
    }

    private void postOrdenRec(NodoPedido nodo, List<Pedido> resultado) {
        if (nodo == null) return;
        postOrdenRec(nodo.izquierdo, resultado);
        postOrdenRec(nodo.derecho, resultado);
        resultado.add(nodo.dato);
    }

    // --- Métodos auxiliares de métricas del árbol ---

    public int contarNodos() {
        return contarNodosRec(raiz);
    }

    private int contarNodosRec(NodoPedido nodo) {
        if (nodo == null) return 0;
        return 1 + contarNodosRec(nodo.izquierdo) + contarNodosRec(nodo.derecho);
    }

    public int altura() {
        return alturaRec(raiz);
    }

    private int alturaRec(NodoPedido nodo) {
        if (nodo == null) return 0;
        return 1 + Math.max(alturaRec(nodo.izquierdo), alturaRec(nodo.derecho));
    }
}
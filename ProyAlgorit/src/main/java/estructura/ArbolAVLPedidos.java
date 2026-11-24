package estructura;

import model.Pedido;
import java.util.ArrayList;
import java.util.List;

public class ArbolAVLPedidos {

    public static class NodoPedido {
        public Pedido dato;
        public NodoPedido izquierdo;
        public NodoPedido derecho;
        public int altura;

        public NodoPedido(Pedido dato) {
            this.dato = dato;
            this.izquierdo = null;
            this.derecho = null;
            this.altura = 1;
        }
    }

    private NodoPedido raiz;
    private int cantidad;
    private int rotaciones;

    public ArbolAVLPedidos() {
        raiz = null;
        cantidad = 0;
        rotaciones = 0;
    }

    public NodoPedido getRaiz() {
        return raiz;
    }

    public boolean estaVacio() {
        return raiz == null;
    }

    public int contarNodos() {
        return cantidad;
    }

    public int getRotaciones() {
        return rotaciones;
    }

    public void limpiar() {
        raiz = null;
        cantidad = 0;
        rotaciones = 0;
    }

    public void insertar(Pedido pedido) {
        if (pedido == null) {
            return;
        }
        boolean[] cambio = {false};
        rotaciones = 0;
        raiz = insertarRecursivo(raiz, pedido, cambio);
        if (cambio[0]) {
            cantidad++;
        }
    }

    private NodoPedido insertarRecursivo(NodoPedido actual, Pedido pedido, boolean[] cambio) {
        if (actual == null) {
            cambio[0] = true;
            return new NodoPedido(pedido);
        }

        if (pedido.getIdPedido() < actual.dato.getIdPedido()) {
            actual.izquierdo = insertarRecursivo(actual.izquierdo, pedido, cambio);
        } else if (pedido.getIdPedido() > actual.dato.getIdPedido()) {
            actual.derecho = insertarRecursivo(actual.derecho, pedido, cambio);
        } else {
            actual.dato = pedido;
            return actual;
        }
        return balancear(actual);
    }

    public Pedido buscar(int idPedido) {
        NodoPedido actual = raiz;
        while (actual != null) {
            if (idPedido == actual.dato.getIdPedido()) {
                return actual.dato;
            }
            actual = idPedido < actual.dato.getIdPedido()
                    ? actual.izquierdo
                    : actual.derecho;
        }
        return null;
    }

    public void eliminar(int idPedido) {
        boolean[] cambio = {false};
        rotaciones = 0;
        raiz = eliminarRecursivo(raiz, idPedido, cambio);
        if (cambio[0]) {
            cantidad--;
        }
    }

    private NodoPedido eliminarRecursivo(NodoPedido actual, int idPedido, boolean[] cambio) {
        if (actual == null) {
            return null;
        }

        if (idPedido < actual.dato.getIdPedido()) {
            actual.izquierdo = eliminarRecursivo(actual.izquierdo, idPedido, cambio);
        } else if (idPedido > actual.dato.getIdPedido()) {
            actual.derecho = eliminarRecursivo(actual.derecho, idPedido, cambio);
        } else {
            cambio[0] = true;
            if (actual.izquierdo == null) {
                return actual.derecho;
            }
            if (actual.derecho == null) {
                return actual.izquierdo;
            }

            NodoPedido sucesor = obtenerMinimo(actual.derecho);
            actual.dato = sucesor.dato;
            boolean[] eliminacionSucesor = {false};
            actual.derecho = eliminarRecursivo(actual.derecho,
                    sucesor.dato.getIdPedido(), eliminacionSucesor);
        }
        return balancear(actual);
    }

    private NodoPedido obtenerMinimo(NodoPedido nodo) {
        while (nodo.izquierdo != null) {
            nodo = nodo.izquierdo;
        }
        return nodo;
    }

    private int altura(NodoPedido nodo) {
        return nodo == null ? 0 : nodo.altura;
    }

    private void actualizarAltura(NodoPedido nodo) {
        nodo.altura = 1 + Math.max(altura(nodo.izquierdo), altura(nodo.derecho));
    }

    public int factorEquilibrio(NodoPedido nodo) {
        return nodo == null ? 0 : altura(nodo.izquierdo) - altura(nodo.derecho);
    }

    private NodoPedido balancear(NodoPedido nodo) {
        actualizarAltura(nodo);
        int factor = factorEquilibrio(nodo);

        if (factor > 1) {
            if (factorEquilibrio(nodo.izquierdo) < 0) {
                nodo.izquierdo = rotarIzquierda(nodo.izquierdo);
            }
            return rotarDerecha(nodo);
        }
        if (factor < -1) {
            if (factorEquilibrio(nodo.derecho) > 0) {
                nodo.derecho = rotarDerecha(nodo.derecho);
            }
            return rotarIzquierda(nodo);
        }
        return nodo;
    }

    private NodoPedido rotarDerecha(NodoPedido y) {
        NodoPedido x = y.izquierdo;
        NodoPedido temporal = x.derecho;
        x.derecho = y;
        y.izquierdo = temporal;
        actualizarAltura(y);
        actualizarAltura(x);
        rotaciones++;
        return x;
    }

    private NodoPedido rotarIzquierda(NodoPedido x) {
        NodoPedido y = x.derecho;
        NodoPedido temporal = y.izquierdo;
        y.izquierdo = x;
        x.derecho = temporal;
        actualizarAltura(x);
        actualizarAltura(y);
        rotaciones++;
        return y;
    }

    public int altura() {
        return altura(raiz);
    }

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
}

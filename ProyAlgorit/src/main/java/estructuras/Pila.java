package estructuras;

public class Pila<T> {
    private Nodo<T> cima;
    private int tamaño;

    public Pila() {
        cima = null;
        tamaño = 0;
    }

    public void push(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.setSiguiente(cima);
        cima = nuevo;
        tamaño++;
    }

    public T pop() {
        if (estaVacia()) return null;
        T dato = cima.getDato();
        cima = cima.getSiguiente();
        tamaño--;
        return dato;
    }

    public T peek() {
        return estaVacia() ? null : cima.getDato();
    }

    public boolean estaVacia() {
        return cima == null;
    }

    public int getTamaño() { return tamaño; }

    public void mostrar() {
        Nodo<T> actual = cima;
        while (actual != null) {
            System.out.println(actual.getDato());
            actual = actual.getSiguiente();
        }
    }
}

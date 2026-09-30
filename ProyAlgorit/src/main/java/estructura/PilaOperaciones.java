package estructura;

import model.Operacion;

public class PilaOperaciones {

    private final Operacion[] elementos;
    private int cima;                 // índice de la cima (-1 = vacía)
    private final int capacidadMaxima;

    public PilaOperaciones(int capacidadMaxima) {
        if (capacidadMaxima <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser positiva");
        }
        this.capacidadMaxima = capacidadMaxima;
        this.elementos = new Operacion[capacidadMaxima];
        this.cima = -1;
    }

    /** Inserta una operación en la cima (push). */
    public boolean push(Operacion operacion) {
        if (estaLlena()) {
            return false; // pila llena
        }
        cima++;
        elementos[cima] = operacion;
        return true;
    }

    /** Extrae y devuelve la operación de la cima (pop). */
    public Operacion pop() {
        if (estaVacia()) {
            return null;
        }
        Operacion op = elementos[cima];
        elementos[cima] = null; // ayuda al GC
        cima--;
        return op;
    }

    /** Devuelve la operación de la cima sin extraerla (peek). */
    public Operacion peek() {
        if (estaVacia()) {
            return null;
        }
        return elementos[cima];
    }

    /** Validación de pila vacía. */
    public boolean estaVacia() {
        return cima == -1;
    }

    public boolean estaLlena() {
        return cima == capacidadMaxima - 1;
    }

    public int tamanio() {
        return cima + 1;
    }


    public String mostrar() {
        if (estaVacia()) {
            return "Historial vacío (ninguna operación pendiente de deshacer).";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("=== Historial de operaciones (cima → base) ===").append(System.lineSeparator());
        sb.append("La primera de la lista es la última realizada (LIFO).").append(System.lineSeparator());
        sb.append("----------------------------------------------").append(System.lineSeparator());
        for (int i = cima; i >= 0; i--) {
            int posicionDesdeCima = cima - i + 1;
            sb.append(posicionDesdeCima)
              .append(". ")
              .append(elementos[i].getDescripcion())
              .append(System.lineSeparator());
        }
        sb.append("----------------------------------------------").append(System.lineSeparator());
        sb.append("Total: ").append(tamanio()).append(" operación(es).");
        return sb.toString();
    }

    public void limpiar() {
        while (!estaVacia()) {
            pop();
        }
    }
}
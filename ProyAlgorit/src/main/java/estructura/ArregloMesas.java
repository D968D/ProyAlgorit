package estructura;

import model.Mesa;
import java.util.Arrays;

public class ArregloMesas {

    private final Mesa[] mesas;
    private int cantidad;
    private final int capacidadMaxima;

    public ArregloMesas(int capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
        this.mesas = new Mesa[capacidadMaxima];
        this.cantidad = 0;
    }

    /** Insercion */
    public boolean insertar(Mesa mesa) {
        if (cantidad >= capacidadMaxima) {
            return false; // arreglo lleno
        }
        if (buscarPorNumero(mesa.getNumeroMesa()) != null) {
            return false; // ya existe esa mesa
        }
        mesas[cantidad] = mesa;
        cantidad++;
        return true;
    }

    /** Busqueda lineal */
    public Mesa buscarPorNumero(int numeroMesa) {
        for (int i = 0; i < cantidad; i++) {
            if (mesas[i] != null && mesas[i].getNumeroMesa() == numeroMesa) {
                return mesas[i];
            }
        }
        return null;
    }

    /** Actualizacion */
    public boolean actualizarEstado(int numeroMesa, String nuevoEstado) {
        Mesa mesa = buscarPorNumero(numeroMesa);
        if (mesa == null) {
            return false;
        }
        mesa.setEstado(nuevoEstado);
        return true;
    }

    /** Eliminacion logica: no se borra el dato, se marca como INACTIVA */
    public boolean eliminarLogico(int numeroMesa) {
        Mesa mesa = buscarPorNumero(numeroMesa);
        if (mesa == null) {
            return false;
        }
        mesa.setEstado("INACTIVA");
        return true;
    }

    /** Recorrido: devuelve solo las mesas activas (no eliminadas logicamente) */
    public Mesa[] recorrer() {
        Mesa[] activas = new Mesa[cantidad];
        int idx = 0;
        for (int i = 0; i < cantidad; i++) {
            if (mesas[i] != null && !"INACTIVA".equalsIgnoreCase(mesas[i].getEstado())) {
                activas[idx] = mesas[i];
                idx++;
            }
        }
        return Arrays.copyOf(activas, idx);
    }

    /** Copia: genera un arreglo independiente con los mismos datos */
    public ArregloMesas copiar() {
        ArregloMesas copia = new ArregloMesas(this.capacidadMaxima);
        for (int i = 0; i < this.cantidad; i++) {
            Mesa original = this.mesas[i];
            Mesa nueva = new Mesa(original.getNumeroMesa(), original.getCapacidad(), original.getEstado());
            copia.insertar(nueva);
        }
        return copia;
    }

    /** Comparacion: compara dos arreglos de mesas y dice si tienen la misma configuracion */
    public boolean compararConfiguracion(ArregloMesas otro) {
        if (this.cantidad != otro.cantidad) {
            return false;
        }
        for (int i = 0; i < this.cantidad; i++) {
            Mesa propia = this.mesas[i];
            Mesa ajena = otro.buscarPorNumero(propia.getNumeroMesa());
            if (ajena == null) {
                return false;
            }
            if (propia.getCapacidad() != ajena.getCapacidad()
                    || !propia.getEstado().equalsIgnoreCase(ajena.getEstado())) {
                return false;
            }
        }
        return true;
    }

    public int getCantidad() {
        return cantidad;
    }
}

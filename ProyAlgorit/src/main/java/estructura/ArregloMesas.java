package estructura;

import model.Mesa;

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
            return false;
        }
        if (buscarPorNumero(mesa.getNumeroMesa()) != null) {
            return false;
        }
        mesas[cantidad] = mesa;
        cantidad++;
        return true;
    }

    public Mesa buscarPorNumero(int numeroMesa) {
        for (int i = 0; i < cantidad; i++) {
            if (mesas[i] != null && mesas[i].getNumeroMesa() == numeroMesa) {
                return mesas[i];
            }
        }
        return null;
    }


    public boolean actualizarEstado(int numeroMesa, String nuevoEstado) {
        Mesa mesa = buscarPorNumero(numeroMesa);
        if (mesa == null) {
            return false;
        }
        mesa.setEstado(nuevoEstado);
        return true;
    }


    public boolean eliminarLogico(int numeroMesa) {
        Mesa mesa = buscarPorNumero(numeroMesa);
        if (mesa == null) {
            return false;
        }
        mesa.setEstado("INACTIVA");
        return true;
    }


    /** Eliminación real: quita la mesa y corre hacia la izquierda las que estaban después. */
    public boolean eliminar(int numeroMesa) {
        for (int i = 0; i < cantidad; i++) {
            if (mesas[i] != null && mesas[i].getNumeroMesa() == numeroMesa) {
                for (int j = i; j < cantidad - 1; j++) {
                    mesas[j] = mesas[j + 1];
                }
                mesas[cantidad - 1] = null;
                cantidad--;
                return true;
            }
        }
        return false;
    }

    public Mesa[] recorrer() {
        int n = 0;
        for (int i = 0; i < cantidad; i++) {
            if (mesas[i] != null && !"INACTIVA".equalsIgnoreCase(mesas[i].getEstado())) {
                n++;
            }
        }
        Mesa[] activas = new Mesa[n];
        int idx = 0;
        for (int i = 0; i < cantidad; i++) {
            if (mesas[i] != null && !"INACTIVA".equalsIgnoreCase(mesas[i].getEstado())) {
                activas[idx] = mesas[i];
                idx++;
            }
        }
        return activas;
    }


    public ArregloMesas copiar() {
        ArregloMesas copia = new ArregloMesas(this.capacidadMaxima);
        for (int i = 0; i < this.cantidad; i++) {
            Mesa original = this.mesas[i];
            Mesa nueva = new Mesa(original.getNumeroMesa(), original.getCapacidad(), original.getEstado());
            copia.insertar(nueva);
        }
        return copia;
    }


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
    public boolean existe(int numeroMesa){
        for(Mesa m:mesas){
            if(m!=null && numeroMesa==m.getNumeroMesa()){
                return true;
            }
        }
        return false;
    }

    public int getCantidad() {
        return cantidad;
    }
}

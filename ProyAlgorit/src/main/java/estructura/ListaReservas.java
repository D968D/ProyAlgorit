package estructura;

import model.Reserva;
import util.FechaHora;

public class ListaReservas {

    private NodoReserva cabeza;

    public ListaReservas() {
        cabeza = null;
    }

    // INSERTAR
    public void insertar(Reserva reserva) {
        NodoReserva nuevo = new NodoReserva(reserva);

        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            NodoReserva actual = cabeza;

            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }

            actual.setSiguiente(nuevo);
        }
    }

    // ELIMINAR
    public boolean eliminar(int idReserva) {
        if (cabeza == null) {
            return false;
        }

        if (cabeza.getReserva().getIdReserva() == idReserva) {
            cabeza = cabeza.getSiguiente();
            return true;
        }

        NodoReserva actual = cabeza;

        while (actual.getSiguiente() != null) {

            if (actual.getSiguiente()
                    .getReserva()
                    .getIdReserva() == idReserva) {

                actual.setSiguiente(
                        actual.getSiguiente().getSiguiente()
                );

                return true;
            }

            actual = actual.getSiguiente();
        }

        return false;
    }

    // RECORRER
    public void recorrer() {
        NodoReserva actual = cabeza;

        while (actual != null) {
            actual.getReserva().mostrarReserva();
            System.out.println("--------------------");

            actual = actual.getSiguiente();
        }
    }

    // BUSCAR
    public Reserva buscar(int idReserva) {
        NodoReserva actual = cabeza;

        while (actual != null) {

            if (actual.getReserva().getIdReserva() == idReserva) {
                return actual.getReserva();
            }

            actual = actual.getSiguiente();
        }

        return null;
    }

    /** Criterios de ordenamiento disponibles. */
    public enum Criterio { ID, CLIENTE, FECHA_HORA }

    /** ORDENAR por ID (criterio por defecto). */
    public void ordenar() {
        ordenar(Criterio.ID);
    }

    /** ORDENAR por el criterio indicado (intercambia los datos de los nodos, O(n²)). */
    public void ordenar(Criterio criterio) {

        if (cabeza == null || cabeza.getSiguiente() == null) {
            return;
        }

        NodoReserva actual = cabeza;

        while (actual != null) {

            NodoReserva siguiente = actual.getSiguiente();

            while (siguiente != null) {

                if (esMayor(actual.getReserva(), siguiente.getReserva(), criterio)) {

                    Reserva temporal = actual.getReserva();
                    actual.setReserva(siguiente.getReserva());
                    siguiente.setReserva(temporal);
                }

                siguiente = siguiente.getSiguiente();
            }

            actual = actual.getSiguiente();
        }
    }

    private boolean esMayor(Reserva a, Reserva b, Criterio criterio) {
        switch (criterio) {
            case CLIENTE:
                return a.getCliente().compareToIgnoreCase(b.getCliente()) > 0;
            case FECHA_HORA:
                return FechaHora.de(a).isAfter(FechaHora.de(b));
            default:
                return a.getIdReserva() > b.getIdReserva();
        }
    }

    // CONVERTIR A ARREGLO (para que la vista arme su tabla sin que la lista dependa de Swing)
    public Reserva[] aArreglo() {
        int n = 0;
        for (NodoReserva a = cabeza; a != null; a = a.getSiguiente()) {
            n++;
        }
        Reserva[] resultado = new Reserva[n];
        int i = 0;
        for (NodoReserva a = cabeza; a != null; a = a.getSiguiente()) {
            resultado[i++] = a.getReserva();
        }
        return resultado;
    }
}
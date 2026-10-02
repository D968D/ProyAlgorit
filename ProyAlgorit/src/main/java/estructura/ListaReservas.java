package estructura;

import tad.TADReserva;
import javax.swing.table.DefaultTableModel;

public class ListaReservas {

    private NodoReserva cabeza;

    public ListaReservas() {
        cabeza = null;
    }

    // INSERTAR
    public void insertar(TADReserva reserva) {
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
    public TADReserva buscar(int idReserva) {
        NodoReserva actual = cabeza;

        while (actual != null) {

            if (actual.getReserva().getIdReserva() == idReserva) {
                return actual.getReserva();
            }

            actual = actual.getSiguiente();
        }

        return null;
    }

    // ORDENAR POR ID DE RESERVA
    public void ordenar() {

        if (cabeza == null || cabeza.getSiguiente() == null) {
            return;
        }

        NodoReserva actual = cabeza;

        while (actual != null) {

            NodoReserva siguiente = actual.getSiguiente();

            while (siguiente != null) {

                if (actual.getReserva().getIdReserva()
                        > siguiente.getReserva().getIdReserva()) {

                    TADReserva temporal = actual.getReserva();

                    actual.setReserva(
                            siguiente.getReserva()
                    );

                    siguiente.setReserva(temporal);
                }

                siguiente = siguiente.getSiguiente();
            }

            actual = actual.getSiguiente();
        }
    }

    // CARGAR LAS RESERVAS EN LA TABLA
    public void cargarEnTabla(DefaultTableModel modelo) {

        modelo.setRowCount(0);

        NodoReserva actual = cabeza;

        while (actual != null) {

            TADReserva reserva = actual.getReserva();

            modelo.addRow(new Object[]{
                    reserva.getIdReserva(),
                    reserva.getCliente(),
                    reserva.getFecha(),
                    reserva.getHora(),
                    reserva.getCantidadPersonas(),
                    reserva.getIdMesa(),
                    reserva.getEstado()
            });

            actual = actual.getSiguiente();
        }
    }
}
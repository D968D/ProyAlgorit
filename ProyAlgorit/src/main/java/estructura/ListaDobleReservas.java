package estructura;

import model.Reserva;

public class ListaDobleReservas {

    private NodoReservaDoble cabeza;
    private NodoReservaDoble cola;

    public ListaDobleReservas() {
        cabeza = null;
        cola = null;
    }


    public void insertar(Reserva reserva) {

        NodoReservaDoble nuevo = new NodoReservaDoble(reserva);

        if (cabeza == null) {
            cabeza = nuevo;
            cola = nuevo;
        } else {
            cola.setSiguiente(nuevo);
            nuevo.setAnterior(cola);
            cola = nuevo;
        }
    }


    public void recorrerAdelante() {

        NodoReservaDoble actual = cabeza;

        while (actual != null) {

            actual.getReserva().mostrarReserva();
            System.out.println("--------------------");

            actual = actual.getSiguiente();
        }
    }


    public void recorrerAtras() {

        NodoReservaDoble actual = cola;

        while (actual != null) {

            actual.getReserva().mostrarReserva();
            System.out.println("--------------------");

            actual = actual.getAnterior();
        }
    }

    // BUSCAR
    public Reserva buscar(int idReserva) {

        NodoReservaDoble actual = cabeza;

        while (actual != null) {

            if (actual.getReserva().getIdReserva() == idReserva) {
                return actual.getReserva();
            }

            actual = actual.getSiguiente();
        }

        return null;
    }

    // ELIMINAR
    public boolean eliminar(int idReserva) {

        NodoReservaDoble actual = cabeza;

        while (actual != null) {

            if (actual.getReserva().getIdReserva() == idReserva) {

                if (actual.getAnterior() != null) {
                    actual.getAnterior()
                            .setSiguiente(actual.getSiguiente());
                } else {
                    cabeza = actual.getSiguiente();
                }

                if (actual.getSiguiente() != null) {
                    actual.getSiguiente()
                            .setAnterior(actual.getAnterior());
                } else {
                    cola = actual.getAnterior();
                }

                return true;
            }

            actual = actual.getSiguiente();
        }

        return false;
    }

    // OBTENER RECORRIDO HACIA ADELANTE
    public String obtenerRecorridoAdelante() {

        StringBuilder resultado = new StringBuilder();

        NodoReservaDoble actual = cabeza;

        while (actual != null) {

            Reserva r = actual.getReserva();

            resultado.append("ID: ")
                    .append(r.getIdReserva())
                    .append(" | Cliente: ")
                    .append(r.getCliente())
                    .append(" | Fecha: ")
                    .append(r.getFecha())
                    .append(" | Hora: ")
                    .append(r.getHora())
                    .append("\n");

            actual = actual.getSiguiente();
        }

        return resultado.toString();
    }

    // OBTENER RECORRIDO HACIA ATRÁS
    public String obtenerRecorridoAtras() {

        StringBuilder resultado = new StringBuilder();

        NodoReservaDoble actual = cola;

        while (actual != null) {

            Reserva r = actual.getReserva();

            resultado.append("ID: ")
                    .append(r.getIdReserva())
                    .append(" | Cliente: ")
                    .append(r.getCliente())
                    .append(" | Fecha: ")
                    .append(r.getFecha())
                    .append(" | Hora: ")
                    .append(r.getHora())
                    .append("\n");

            actual = actual.getAnterior();
        }

        return resultado.toString();
    }
}
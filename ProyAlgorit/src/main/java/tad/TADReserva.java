package tad;

/**
 * TAD Reserva (Tipo Abstracto de Datos).
 *
 * Datos que administra: id, cliente, fecha, hora, cantidad de personas, mesa y estado.
 * Operaciones: consultar y modificar cada dato y mostrar la reserva.
 * Implementación: la clase model.Reserva. Las listas, la cola y el AVL trabajan
 * con reservas solo a través de estas operaciones, sin depender de cómo se guardan.
 */
public interface TADReserva {

    int getIdReserva();
    String getCliente();
    String getFecha();
    String getHora();
    int getCantidadPersonas();
    int getIdMesa();
    String getEstado();

    void setCliente(String cliente);
    void setFecha(String fecha);
    void setHora(String hora);
    void setCantidadPersonas(int cantidadPersonas);
    void setIdMesa(int idMesa);
    void setEstado(String estado);

    void mostrarReserva();
}

package model;

public class Reserva {

    private int idReserva;
    private String cliente;
    private String fecha;
    private String hora;
    private int cantidadPersonas;
    private int idMesa;
    private String estado;

    /**Constructor*/
    public Reserva(int idReserva, String cliente, String fecha,
                      String hora, int cantidadPersonas,
                      int idMesa, String estado) {

        this.idReserva = idReserva;
        this.cliente = cliente;
        this.fecha = fecha;
        this.hora = hora;
        this.cantidadPersonas = cantidadPersonas;
        this.idMesa = idMesa;
        this.estado = estado;
    }

    /**Getters*/
    public int getIdReserva() {
        return idReserva;
    }

    public String getCliente() {
        return cliente;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }

    public int getCantidadPersonas() {
        return cantidadPersonas;
    }

    public int getIdMesa() {
        return idMesa;
    }

    public String getEstado() {
        return estado;
    }

    /**Setters*/
    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public void setCantidadPersonas(int cantidadPersonas) {
        this.cantidadPersonas = cantidadPersonas;
    }

    public void setIdMesa(int idMesa) {
        this.idMesa = idMesa;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**Mostrar información de la reserva*/
    public void mostrarReserva() {
        System.out.println("ID Reserva: " + idReserva);
        System.out.println("Cliente: " + cliente);
        System.out.println("Fecha: " + fecha);
        System.out.println("Hora: " + hora);
        System.out.println("Cantidad de personas: " + cantidadPersonas);
        System.out.println("Mesa: " + idMesa);
        System.out.println("Estado: " + estado);
    }
}
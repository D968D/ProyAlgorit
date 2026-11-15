package model;

import java.util.List;
public class Pedido {
    private int idPedido;
    private int numMesa;
    private String dni;
    private String fechaHora;
    private double total;
    private String estado;
    private String metodoPago;
    private List<DetallePedido> detalles;

    public Pedido(){};

    public Pedido(int idPedido, int numMesa, String dni, String fechaHora, double total, String estado,
            String metodoPago) {
        this.idPedido = idPedido;
        this.numMesa = numMesa;
        this.dni = dni;
        this.fechaHora = fechaHora;
        this.total = total;
        this.estado = estado;
        this.metodoPago = metodoPago;
    }

    public Pedido(int idPedido, int numMesa, String dni, String fechaHora, double total,
            String metodoPago) {
        this.idPedido = idPedido;
        this.numMesa = numMesa;
        this.dni = dni;
        this.fechaHora = fechaHora;
        this.total = total;
        this.estado = "PENDIENTE";
        this.metodoPago = metodoPago;
    }

    public int getIdPedido() {
        return idPedido;
    }
    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }
    public int getNumMesa() {
        return numMesa;
    }
    public void setNumMesa(int numMesa) {
        this.numMesa = numMesa;
    }
    public String getDni() {
        return dni;
    }
    public void setDni(String dni) {
        this.dni = dni;
    }
    public String getFechaHora() {
        return fechaHora;
    }
    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }
    public double getTotal() {
        return total;
    }
    public void setTotal(double total) {
        this.total = total;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public String getMetodoPago() {
        return metodoPago;
    }
    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public List<DetallePedido> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedido> detalles) {
        this.detalles = detalles;
    }
    
}

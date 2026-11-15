package model;

public class DetallePedido {
    private int idDetalle;
    private int idPedido;
    private int idPlato;
    private String nombrePlato;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    public DetallePedido() {}

    public DetallePedido(int idPlato, String nombrePlato, int cantidad, double precioUnitario) {
        this.idPlato = idPlato;
        this.nombrePlato = nombrePlato;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = cantidad * precioUnitario;
    }

    // Getters y Setters
    public int getIdDetalle() { return idDetalle; }
    public void setIdDetalle(int idDetalle) { this.idDetalle = idDetalle; }

    public int getIdPedido() { return idPedido; }
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }

    public int getIdPlato() { return idPlato; }
    public void setIdPlato(int idPlato) { this.idPlato = idPlato; }

    public String getNombrePlato() { return nombrePlato; }
    public void setNombrePlato(String nombrePlato) { this.nombrePlato = nombrePlato; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        this.subtotal = this.cantidad * this.precioUnitario;
    }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
        this.subtotal = this.cantidad * this.precioUnitario;
    }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}
package model;
public class Pedido {
    private int IDPedido;
    private int numMesa;
    private int IDUsuario;
    private String fecha;
    private double total;
    private String estado;
    private String metodoPago;
    
    public Pedido(int IDPedido, int numMesa, int IDUsuario, String fecha, double total, String estado, String metodoPago){
        this.IDPedido = IDPedido;
        this.numMesa = numMesa;
        this.IDUsuario = IDUsuario;
        this.fecha = fecha;
        this.total = total;
        this.estado = estado;
        this.metodoPago = metodoPago;
    }

    public int getIDPedido() {
        return IDPedido;
    }

    public void setIDPedido(int IDPedido) {
        this.IDPedido = IDPedido;
    }

    public int getNumMesa() {
        return numMesa;
    }

    public void setNumMesa(int numMesa) {
        this.numMesa = numMesa;
    }

    public int getIDUsuario() {
        return IDUsuario;
    }

    public void setIDUsuario(int IDUsuario) {
        this.IDUsuario = IDUsuario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
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
    
    public String toString() {
        return "Pedido{" +
                "IDPedido=" + IDPedido +
                ", numMesa=" + numMesa +
                ", IDUsuario=" + IDUsuario +
                ", fecha='" + fecha + '\'' +
                ", total=" + total +
                ", estado='" + estado + '\'' +
                ", metodoPago='" + metodoPago + '\'' +
                '}';
    }
}
package model;

public class Plato {
    private String codigoPlato;
    private String nombre;
    private double precio;
    private String categoria;
    private int stock;

    public Plato() {}

    public Plato(String codigoPlato, String nombre, double precio, String categoria, int stock) {
        this.codigoPlato = codigoPlato;
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
        this.stock = stock;
    }

    // Getters y Setters
    public String getCodigoPlato() { return codigoPlato; }
    public void setCodigoPlato(String codigoPlato) { this.codigoPlato = codigoPlato; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    @Override
    public String toString() {
        return codigoPlato + " - " + nombre + " S/ " + precio;
    }
}
}

package model;

public class Plato {

    private int idPlato;
    private String nombre;
    private double precio;
    private String categoria;

    public Plato(int idPlato, String nombre, double precio, String categoria) {
        this.idPlato = idPlato;
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
    }

    public int getIdPlato() {
        return idPlato;
    }

    public void setIdPlato(int idPlato) {
        this.idPlato = idPlato;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return nombre + " (S/ " + precio + ") - " + categoria;
    }
}

package model;
public class Mesa {
    private int capacidad;
    private String estado;
    public Mesa(int capacidad, String estado){
        this.capacidad=capacidad;
        this.estado=estado;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}

package model;
public class Mesa {
    private int numMesa;
    private int capacidad;
    private String estado;
    public Mesa(int numMesa, int capacidad, String estado){
        this.numMesa = numMesa;
        this.capacidad = capacidad;
        this.estado = estado;
    }

    public int getNumMesa() {
        return numMesa;
    }

    public void setNumMesa(int numMesa) {
        this.numMesa = numMesa;
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

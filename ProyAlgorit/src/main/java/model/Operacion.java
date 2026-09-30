package model;

public class Operacion {

    public enum Tipo {
        AGREGAR,
        ACTUALIZAR,
        ELIMINAR
    }

    private final Tipo tipo;
    private final Plato platoAntes;   // estado anterior (para ACTUALIZAR y ELIMINAR)
    private final int idAfectado;     // id del plato afectado
    private final String descripcion; // texto legible para el historial

    public Operacion(Tipo tipo, Plato platoAntes, int idAfectado, String descripcion) {
        this.tipo = tipo;
        this.platoAntes = platoAntes;
        this.idAfectado = idAfectado;
        this.descripcion = descripcion;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public Plato getPlatoAntes() {
        return platoAntes;
    }

    public int getIdAfectado() {
        return idAfectado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}



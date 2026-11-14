package estructura;

import model.Mesa;

public class MatrizOcupacion {

    private final String[] zonas;
    private final String[][] estados;
    private final int mesasPorZona;

    public MatrizOcupacion(String[] zonas, int mesasPorZona) {
        this.zonas = zonas;
        this.mesasPorZona = mesasPorZona;
        this.estados = new String[zonas.length][mesasPorZona];
        limpiar();
    }

    /**
     * Recarga la matriz a partir del arreglo de mesas.
     * La mesa numero N se ubica en zona = (N-1)/mesasPorZona, columna = (N-1)%mesasPorZona
     */
    public void cargarDesdeMesas(Mesa[] mesas) {
        for (Mesa mesa : mesas) {
            if (mesa == null) {
                continue;
            }
            int idx = mesa.getNumeroMesa() - 1;
            int zonaIdx = idx / mesasPorZona;
            int columna = idx % mesasPorZona;
            if (idx >= 0 && zonaIdx >= 0 && zonaIdx < zonas.length) {
                estados[zonaIdx][columna] = mesa.getEstado();
            }
        }
    }

    /** Deja todas las celdas en LIBRE antes de volver a cargar. */
    public void limpiar() {
        for (int i = 0; i < estados.length; i++) {
            for (int j = 0; j < estados[i].length; j++) {
                estados[i][j] = "LIBRE";
            }
        }
    }

    public String[] getZonas() {
        return zonas;
    }

    public String[][] getEstados() {
        return estados;
    }

    /** Representacion en texto de la matriz, util para depurar o mostrar en consola */
    public String mostrar() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < zonas.length; i++) {
            sb.append(zonas[i]).append(": ");
            for (int j = 0; j < estados[i].length; j++) {
                int numeroMesa = i * mesasPorZona + j + 1;
                sb.append("[M").append(numeroMesa).append(":").append(estados[i][j]).append("] ");
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }
}

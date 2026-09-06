package estructura;

import model.Mesa;
import java.util.Arrays;


public class MatrizOcupacion {

    private final String[] zonas;
    private final String[][] estados;
    private final int mesasPorZona;

    public MatrizOcupacion(String[] zonas, int mesasPorZona) {
        this.zonas = zonas;
        this.mesasPorZona = mesasPorZona;
        this.estados = new String[zonas.length][mesasPorZona];
        for (String[] fila : estados) {
            Arrays.fill(fila, "LIBRE");
        }
    }

 
    public void cargarDesdeMesas(Mesa[] mesas) {
        for (Mesa mesa : mesas) {
            if (mesa == null) {
                continue;
            }
            int idx = mesa.getNumeroMesa() - 1;
            int zonaIdx = idx / mesasPorZona;
            int columna = idx % mesasPorZona;
            if (zonaIdx >= 0 && zonaIdx < zonas.length) {
                estados[zonaIdx][columna] = mesa.getEstado();
            }
        }
    }

    public String[] getZonas() {
        return zonas;
    }

    public String[][] getEstados() {
        return estados;
    }

   
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

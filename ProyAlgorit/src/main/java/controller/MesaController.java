package controller;

import dao.MesaDAO;
import estructura.ArregloMesas;
import estructura.MatrizOcupacion;
import model.Mesa;
import view.EstadoDialog;
import view.MesaView;

import javax.swing.*;
import java.util.List;

// ANTES: el metodo actualizar() estaba vacio ("private void actualizar(){ }")
// y la clase no cargaba ninguna mesa a la tabla ni usaba las estructuras
// de datos del Capitulo 4. Se completo todo el flujo:
// Base de datos -> Arreglo (ArregloMesas) -> Matriz de zonas -> Tabla (interfaz).
public class MesaController {

    private static final int CAPACIDAD_MAXIMA = 20;
    private static final int MESAS_POR_ZONA = 7;
    private static final String[] ZONAS = {"Salon", "Terraza", "VIP"};

    private final MesaView view;
    private final MesaDAO mesaDAO;
    private final ArregloMesas arregloMesas;
    private final MatrizOcupacion matrizOcupacion;

    public MesaController(MesaView view) {
        this.view = view;
        this.mesaDAO = new MesaDAO();
        this.arregloMesas = new ArregloMesas(CAPACIDAD_MAXIMA);
        this.matrizOcupacion = new MatrizOcupacion(ZONAS, MESAS_POR_ZONA);

        cargarMesas();

        view.btnActualizar.addActionListener(e -> actualizarEstado());
        view.setVisible(true);
    }

    /** Trae las mesas de la BD, las guarda en el arreglo propio y llena la tabla */
    private void cargarMesas() {
        List<Mesa> mesasBD = mesaDAO.listarTodas();
        view.modeloTabla.setRowCount(0);

        for (Mesa mesa : mesasBD) {
            arregloMesas.insertar(mesa);
            view.modeloTabla.addRow(new Object[]{mesa.getNumeroMesa(), mesa.getCapacidad(), mesa.getEstado()});
        }

        matrizOcupacion.cargarDesdeMesas(arregloMesas.recorrer());
    }

    /** Se ejecuta al presionar "Actualizar": abre LIBRE/OCUPADO y guarda el cambio */
    private void actualizarEstado() {
        int fila = view.getFilaSeleccionada();
        if (fila == -1) {
            JOptionPane.showMessageDialog(view, "Selecciona una mesa de la tabla primero.");
            return;
        }

        int numeroMesa = (int) view.modeloTabla.getValueAt(fila, 0);

        EstadoDialog dialog = new EstadoDialog(view);
        dialog.setVisible(true); // se detiene aqui hasta que el usuario elija LIBRE u OCUPADO

        String nuevoEstado = dialog.getEstadoSeleccionado();
        if (nuevoEstado == null) {
            return; // el usuario cerro la ventana sin elegir
        }

        boolean okBD = mesaDAO.actualizarEstado(numeroMesa, nuevoEstado);
        boolean okArreglo = arregloMesas.actualizarEstado(numeroMesa, nuevoEstado);

        if (okBD && okArreglo) {
            view.modeloTabla.setValueAt(nuevoEstado, fila, 2);
            matrizOcupacion.cargarDesdeMesas(arregloMesas.recorrer());
            JOptionPane.showMessageDialog(view, "Mesa " + numeroMesa + " actualizada a " + nuevoEstado + ".");
        } else {
            JOptionPane.showMessageDialog(view, "No se pudo actualizar la mesa.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

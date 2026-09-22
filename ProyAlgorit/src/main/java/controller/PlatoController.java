package controller;

import dao.PlatoDAO;
import model.Plato;
import view.ActualizarPlatoDialog;
import view.AgregarPlatoDialog;
import view.PlatoView;

import javax.swing.*;
import java.util.List;

public class PlatoController {

    private final PlatoView view;
    private final PlatoDAO platoDAO;

    public PlatoController(PlatoView view) {
        this.view = view;
        this.platoDAO = new PlatoDAO();

        cargarPlatos();

        view.btnAgregar.addActionListener(e -> agregar());
        view.btnActualizar.addActionListener(e -> actualizar());
        view.btnEliminar.addActionListener(e -> eliminar());

        view.setVisible(true);
    }

    /** Llena la tabla con todos los platos de la BD */
    private void cargarPlatos() {
        view.modeloTabla.setRowCount(0);
        List<Plato> lista = platoDAO.listarTodos();
        for (Plato p : lista) {
            view.modeloTabla.addRow(new Object[]{
                    p.getIdPlato(),
                    p.getNombre(),
                    p.getPrecio(),
                    p.getCategoria()
            });
        }
    }

    /** Botón Agregar → abre diálogo nombre/precio/categoría */
    private void agregar() {
        AgregarPlatoDialog dialog = new AgregarPlatoDialog(view);
        dialog.setVisible(true);

        if (!dialog.isConfirmado()) {
            return;
        }

        Plato nuevo = new Plato(0, dialog.getNombre(), dialog.getPrecio(), dialog.getCategoria());
        if (platoDAO.insertar(nuevo)) {
            JOptionPane.showMessageDialog(view, "Plato agregado correctamente.");
            cargarPlatos();
        } else {
            JOptionPane.showMessageDialog(view, "No se pudo agregar el plato.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Botón Actualizar → abre diálogo solo de precio (como en la imagen) */
    private void actualizar() {
        int fila = view.getFilaSeleccionada();
        if (fila == -1) {
            JOptionPane.showMessageDialog(view, "Seleccione un plato de la tabla primero.");
            return;
        }

        int id = (int) view.modeloTabla.getValueAt(fila, 0);
        double precioActual = ((Number) view.modeloTabla.getValueAt(fila, 2)).doubleValue();

        ActualizarPlatoDialog dialog = new ActualizarPlatoDialog(view, precioActual);
        dialog.setVisible(true);

        if (!dialog.isConfirmado()) {
            return;
        }

        double nuevoPrecio = dialog.getNuevoPrecio();
        if (platoDAO.actualizarPrecio(id, nuevoPrecio)) {
            view.modeloTabla.setValueAt(nuevoPrecio, fila, 2);
            JOptionPane.showMessageDialog(view, "Precio actualizado correctamente.");
        } else {
            JOptionPane.showMessageDialog(view, "No se pudo actualizar el precio.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Botón Eliminar → pide confirmación y borra el registro */
    private void eliminar() {
        int fila = view.getFilaSeleccionada();
        if (fila == -1) {
            JOptionPane.showMessageDialog(view, "Seleccione un plato de la tabla primero.");
            return;
        }

        int id = (int) view.modeloTabla.getValueAt(fila, 0);
        String nombre = view.modeloTabla.getValueAt(fila, 1).toString();

        int conf = JOptionPane.showConfirmDialog(
                view,
                "¿Desea eliminar el plato \"" + nombre + "\"?",
                "Confirmar",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (conf != JOptionPane.OK_OPTION) {
            return;
        }

        if (platoDAO.eliminar(id)) {
            JOptionPane.showMessageDialog(view, "Plato eliminado.");
            cargarPlatos();
        } else {
            JOptionPane.showMessageDialog(view, "No se pudo eliminar el plato.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

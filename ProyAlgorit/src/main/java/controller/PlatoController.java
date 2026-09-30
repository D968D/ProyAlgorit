package controller;

import dao.PlatoDAO;
import estructura.PilaOperaciones;
import model.Operacion;
import model.Plato;
import view.ActualizarPlatoDialog;
import view.AgregarPlatoDialog;
import view.PlatoView;

import javax.swing.*;
import java.awt.Font;
import java.util.List;


public class PlatoController {

    private static final int CAPACIDAD_PILA = 50;

    private final PlatoView view;
    private final PlatoDAO platoDAO;
    private final PilaOperaciones pilaHistorial;

    public PlatoController(PlatoView view) {
        this.view = view;
        this.platoDAO = new PlatoDAO();
        this.pilaHistorial = new PilaOperaciones(CAPACIDAD_PILA);

        cargarPlatos();

        view.btnAgregar.addActionListener(e -> agregar());
        view.btnActualizar.addActionListener(e -> actualizar());
        view.btnEliminar.addActionListener(e -> eliminar());
        view.btnDeshacer.addActionListener(e -> deshacer());
        view.btnHistorial.addActionListener(e -> mostrarHistorial());

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

    /** Botón Agregar → abre diálogo nombre/precio/categoría y apila la operación */
    private void agregar() {
        AgregarPlatoDialog dialog = new AgregarPlatoDialog(view);
        dialog.setVisible(true);

        if (!dialog.isConfirmado()) {
            return;
        }

        Plato nuevo = new Plato(0, dialog.getNombre(), dialog.getPrecio(), dialog.getCategoria());
        if (platoDAO.insertar(nuevo)) {
            // Push: guardar operación para poder deshacer (eliminar el plato recién creado)
            Operacion op = new Operacion(
                    Operacion.Tipo.AGREGAR,
                    null,
                    nuevo.getIdPlato(),
                    "AGREGAR: \"" + nuevo.getNombre() + "\" (id=" + nuevo.getIdPlato() + ")"
            );
            if (!pilaHistorial.push(op)) {
                JOptionPane.showMessageDialog(view,
                        "Historial lleno; no se pudo registrar la operación para deshacer.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
            }

            JOptionPane.showMessageDialog(view, "Plato agregado correctamente.");
            cargarPlatos();
        } else {
            JOptionPane.showMessageDialog(view, "No se pudo agregar el plato.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Botón Actualizar → abre diálogo de precio y apila el estado anterior */
    private void actualizar() {
        int fila = view.getFilaSeleccionada();
        if (fila == -1) {
            JOptionPane.showMessageDialog(view, "Seleccione un plato de la tabla primero.");
            return;
        }

        int id = (int) view.modeloTabla.getValueAt(fila, 0);
        String nombre = view.modeloTabla.getValueAt(fila, 1).toString();
        double precioActual = ((Number) view.modeloTabla.getValueAt(fila, 2)).doubleValue();
        String categoria = view.modeloTabla.getValueAt(fila, 3).toString();

        ActualizarPlatoDialog dialog = new ActualizarPlatoDialog(view, precioActual);
        dialog.setVisible(true);

        if (!dialog.isConfirmado()) {
            return;
        }

        double nuevoPrecio = dialog.getNuevoPrecio();
        if (platoDAO.actualizarPrecio(id, nuevoPrecio)) {
            // Push: guardar plato con el precio anterior para poder restaurarlo
            Plato estadoAnterior = new Plato(id, nombre, precioActual, categoria);
            Operacion op = new Operacion(
                    Operacion.Tipo.ACTUALIZAR,
                    estadoAnterior,
                    id,
                    "ACTUALIZAR precio: \"" + nombre + "\" de S/ " + precioActual + " a S/ " + nuevoPrecio
            );
            if (!pilaHistorial.push(op)) {
                JOptionPane.showMessageDialog(view,
                        "Historial lleno; no se pudo registrar la operación para deshacer.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
            }

            view.modeloTabla.setValueAt(nuevoPrecio, fila, 2);
            JOptionPane.showMessageDialog(view, "Precio actualizado correctamente.");
        } else {
            JOptionPane.showMessageDialog(view, "No se pudo actualizar el precio.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Botón Eliminar → pide confirmación, borra y apila el plato completo para restaurarlo */
    private void eliminar() {
        int fila = view.getFilaSeleccionada();
        if (fila == -1) {
            JOptionPane.showMessageDialog(view, "Seleccione un plato de la tabla primero.");
            return;
        }

        int id = (int) view.modeloTabla.getValueAt(fila, 0);
        String nombre = view.modeloTabla.getValueAt(fila, 1).toString();
        double precio = ((Number) view.modeloTabla.getValueAt(fila, 2)).doubleValue();
        String categoria = view.modeloTabla.getValueAt(fila, 3).toString();

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
            // Push: guardar el plato completo para poder reinsertarlo al deshacer
            Plato eliminado = new Plato(id, nombre, precio, categoria);
            Operacion op = new Operacion(
                    Operacion.Tipo.ELIMINAR,
                    eliminado,
                    id,
                    "ELIMINAR: \"" + nombre + "\" (id=" + id + ")"
            );
            if (!pilaHistorial.push(op)) {
                JOptionPane.showMessageDialog(view,
                        "Historial lleno; no se pudo registrar la operación para deshacer.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
            }

            JOptionPane.showMessageDialog(view, "Plato eliminado.");
            cargarPlatos();
        } else {
            JOptionPane.showMessageDialog(view, "No se pudo eliminar el plato.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Deshacer: hace POP de la pila y revierte la última operación.
     * Comportamiento LIFO: siempre se deshace la más reciente primero.
     */
    private void deshacer() {
        // Validación de pila vacía
        if (pilaHistorial.estaVacia()) {
            JOptionPane.showMessageDialog(view,
                    "No hay operaciones para deshacer.\nLa pila está vacía.",
                    "Deshacer", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Peek: mostrar qué se va a deshacer
        Operacion cima = pilaHistorial.peek();
        int conf = JOptionPane.showConfirmDialog(
                view,
                "¿Deshacer la siguiente operación?\n\n" + cima.getDescripcion(),
                "Confirmar deshacer (LIFO)",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (conf != JOptionPane.OK_OPTION) {
            return;
        }

        // Pop: extraer de la cima
        Operacion op = pilaHistorial.pop();
        boolean ok = false;

        switch (op.getTipo()) {
            case AGREGAR:
                // Revertir agregar = eliminar el plato que se insertó
                ok = platoDAO.eliminar(op.getIdAfectado());
                break;
            case ACTUALIZAR:
                // Revertir actualizar = restaurar el precio anterior
                Plato anterior = op.getPlatoAntes();
                ok = platoDAO.actualizarPrecio(anterior.getIdPlato(), anterior.getPrecio());
                break;
            case ELIMINAR:
                // Revertir eliminar = volver a insertar el plato
                Plato aRestaurar = op.getPlatoAntes();
                Plato copia = new Plato(0, aRestaurar.getNombre(), aRestaurar.getPrecio(), aRestaurar.getCategoria());
                ok = platoDAO.insertar(copia);
                break;
        }

        if (ok) {
            JOptionPane.showMessageDialog(view,
                    "Operación deshecha:\n" + op.getDescripcion(),
                    "Deshacer", JOptionPane.INFORMATION_MESSAGE);
            cargarPlatos();
        } else {
            JOptionPane.showMessageDialog(view,
                    "No se pudo deshacer la operación en la base de datos.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Visualización del contenido de la pila (de cima a base).
     * Demuestra el orden LIFO: la primera línea es la última operación realizada.
     */
    private void mostrarHistorial() {
        String contenido = pilaHistorial.mostrar();
        JTextArea area = new JTextArea(contenido);
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new java.awt.Dimension(480, 280));

        JOptionPane.showMessageDialog(view, scroll,
                "Historial de operaciones (Pila LIFO)",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
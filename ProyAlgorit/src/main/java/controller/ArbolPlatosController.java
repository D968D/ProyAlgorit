package controller;

import dao.PlatoDAO;
import estructura.ArbolBinarioPlatos;
import model.Plato;
import view.AgregarPlatoDialog;
import view.ArbolPlatosView;

import javax.swing.*;

/**
 * Controla el árbol binario de la carta (Capítulo 8).
 * Al abrir, carga los platos de la BD y los inserta en el árbol propio;
 * insertar y eliminar actualizan la BD y el árbol a la vez.
 */
public class ArbolPlatosController {

    private final ArbolPlatosView view;
    private final PlatoDAO platoDAO;
    private final ArbolBinarioPlatos arbol;
    private final Runnable alCambiarPlatos; // para refrescar la tabla de PlatoView

    public ArbolPlatosController(ArbolPlatosView view, Runnable alCambiarPlatos) {
        this.view = view;
        this.platoDAO = new PlatoDAO();
        this.arbol = new ArbolBinarioPlatos();
        this.alCambiarPlatos = alCambiarPlatos;

        cargarDesdeBD();

        view.btnInsertar.addActionListener(e -> insertar());
        view.btnBuscar.addActionListener(e -> buscar());
        view.btnEliminar.addActionListener(e -> eliminar());
        view.btnPreorden.addActionListener(e -> mostrarRecorrido("PREORDEN (raíz, izquierdo, derecho)", arbol.preorden()));
        view.btnInorden.addActionListener(e -> mostrarRecorrido("INORDEN (izquierdo, raíz, derecho)", arbol.inorden()));
        view.btnPostorden.addActionListener(e -> mostrarRecorrido("POSTORDEN (izquierdo, derecho, raíz)", arbol.postorden()));
        view.btnVolver.addActionListener(e -> view.dispose());

        view.setVisible(true);
    }

    private void cargarDesdeBD() {
        for (Plato p : platoDAO.listarTodos()) {
            arbol.insertar(p);
        }
        refrescar(-1);
        view.areaResultados.setText("Árbol cargado: " + arbol.getCantidad() + " plato(s), altura " + arbol.altura() + ".");
    }

    private void refrescar(int idResaltado) {
        view.mostrarArbol(arbol, idResaltado);
    }

    private void insertar() {
        AgregarPlatoDialog dialog = new AgregarPlatoDialog(view);
        dialog.setVisible(true);
        if (!dialog.isConfirmado()) {
            return;
        }
        if (platoDAO.existeNombre(dialog.getNombre())) {
            JOptionPane.showMessageDialog(view, "Ya existe un plato con ese nombre.\nCada plato debe tener un nombre único.");
            return;
        }
        Plato nuevo = new Plato(0, dialog.getNombre(), dialog.getPrecio(), dialog.getCategoria());
        if (platoDAO.insertar(nuevo)) {
            arbol.insertar(nuevo);
            refrescar(nuevo.getIdPlato());
            view.areaResultados.setText("Insertado: " + nuevo + " (id=" + nuevo.getIdPlato()
                    + "). Nodos: " + arbol.getCantidad() + ", altura: " + arbol.altura() + ".");
            alCambiarPlatos.run();
        } else {
            JOptionPane.showMessageDialog(view, "No se pudo agregar el plato.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscar() {
        if (arbol.estaVacio()) {
            JOptionPane.showMessageDialog(view, "El árbol está vacío.");
            return;
        }
        Integer id = pedirId("Ingrese el ID del plato a buscar:");
        if (id == null) {
            return;
        }
        Plato p = arbol.buscar(id);
        if (p == null) {
            refrescar(-1);
            view.areaResultados.setText("No existe un plato con ID " + id + " en el árbol.");
        } else {
            refrescar(id);
            view.areaResultados.setText("Encontrado: " + p);
        }
    }

    private void eliminar() {
        if (arbol.estaVacio()) {
            JOptionPane.showMessageDialog(view, "El árbol está vacío; no hay nada que eliminar.");
            return;
        }
        Integer id = pedirId("Ingrese el ID del plato a eliminar:");
        if (id == null) {
            return;
        }
        Plato p = arbol.buscar(id);
        if (p == null) {
            view.areaResultados.setText("No existe un plato con ID " + id + " en el árbol.");
            return;
        }
        int conf = JOptionPane.showConfirmDialog(view, "¿Eliminar el plato \"" + p.getNombre() + "\"?",
                "Confirmar", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (conf != JOptionPane.OK_OPTION) {
            return;
        }
        if (platoDAO.eliminar(id) && arbol.eliminar(id)) {
            refrescar(-1);
            view.areaResultados.setText("Eliminado: " + p.getNombre() + ". Nodos: " + arbol.getCantidad()
                    + ", altura: " + arbol.altura() + ".");
            alCambiarPlatos.run();
        } else {
            JOptionPane.showMessageDialog(view, "No se pudo eliminar el plato.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarRecorrido(String titulo, String resultado) {
        if (arbol.estaVacio()) {
            view.areaResultados.setText("El árbol está vacío; no hay recorrido que mostrar.");
            return;
        }
        view.areaResultados.setText(titulo + ":\n" + resultado);
    }

    private Integer pedirId(String mensaje) {
        String texto = JOptionPane.showInputDialog(view, mensaje);
        if (texto == null) {
            return null;
        }
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "Ingrese un ID válido (número entero).");
            return null;
        }
    }
}

package controller;

import dao.PlatoDAO;
import estructura.ArbolABBPlatos;
import estructura.NodoPlato;
import model.Plato;
import view.AgregarPlatoDialog;
import view.PanelArbolGenerico.Dibujable;
import view.VisorArbolView;

import javax.swing.*;

/**
 * ABB de platos por nombre (Capítulo 9).
 * Al abrir, carga los platos de la BD en el árbol propio; insertar y eliminar
 * actualizan la BD y el árbol a la vez.
 */
public class ArbolABBController {

    private final VisorArbolView view;
    private final PlatoDAO platoDAO = new PlatoDAO();
    private final ArbolABBPlatos arbol = new ArbolABBPlatos();
    private final Runnable alCambiarPlatos;

    public ArbolABBController(VisorArbolView view, Runnable alCambiarPlatos) {
        this.view = view;
        this.alCambiarPlatos = alCambiarPlatos;

        int repetidos = 0;
        for (Plato p : platoDAO.listarTodos()) {
            if (!arbol.insertar(p)) {
                repetidos++;
            }
        }
        refrescar(null);
        view.areaResultados.setText("ABB cargado: " + arbol.getCantidad() + " plato(s), altura " + arbol.altura()
                + (repetidos > 0 ? ". Se omitieron " + repetidos + " con nombre repetido." : "."));

        view.btnInsertar.addActionListener(e -> insertar());
        view.btnBuscar.addActionListener(e -> buscar());
        view.btnEliminar.addActionListener(e -> eliminar());
        view.btnPreorden.addActionListener(e -> recorrido("PREORDEN (raíz, izquierdo, derecho)", arbol.preorden()));
        view.btnInorden.addActionListener(e -> recorrido("INORDEN (carta ordenada A-Z)", arbol.inorden()));
        view.btnPostorden.addActionListener(e -> recorrido("POSTORDEN (izquierdo, derecho, raíz)", arbol.postorden()));
        view.btnVolver.addActionListener(e -> view.dispose());
        view.setVisible(true);
    }

    private static Dibujable envolver(NodoPlato n) {
        if (n == null) return null;
        final Dibujable izq = envolver(n.getIzquierdo());
        final Dibujable der = envolver(n.getDerecho());
        final Plato p = n.getPlato();
        return new Dibujable() {
            public String id() { return String.valueOf(p.getIdPlato()); }
            public String nombre() { return p.getNombre(); }
            public String extra() { return null; }
            public Dibujable izquierdo() { return izq; }
            public Dibujable derecho() { return der; }
        };
    }

    private void refrescar(String idResaltado) {
        view.mostrar(envolver(arbol.getRaiz()), idResaltado);
    }

    private void insertar() {
        AgregarPlatoDialog dialog = new AgregarPlatoDialog(view);
        dialog.setVisible(true);
        if (!dialog.isConfirmado()) {
            return;
        }
        if (arbol.buscar(dialog.getNombre()) != null) {
            JOptionPane.showMessageDialog(view, "Ya existe un plato con ese nombre en el ABB.\nLa clave no puede repetirse.");
            return;
        }
        Plato nuevo = new Plato(0, dialog.getNombre(), dialog.getPrecio(), dialog.getCategoria());
        if (platoDAO.insertar(nuevo)) {
            arbol.insertar(nuevo);
            refrescar(String.valueOf(nuevo.getIdPlato()));
            view.areaResultados.setText("Insertado: " + nuevo.getNombre() + ". Nodos: " + arbol.getCantidad()
                    + ", altura: " + arbol.altura() + ".");
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
        String nombre = JOptionPane.showInputDialog(view, "Nombre del plato a buscar:");
        if (nombre == null || nombre.trim().isEmpty()) {
            return;
        }
        Plato p = arbol.buscar(nombre.trim());
        int comp = arbol.getComparaciones();
        if (p == null) {
            refrescar(null);
            view.areaResultados.setText("No existe un plato llamado \"" + nombre.trim() + "\". Comparaciones: " + comp
                    + " (de " + arbol.getCantidad() + " platos).");
        } else {
            refrescar(String.valueOf(p.getIdPlato()));
            view.areaResultados.setText("Encontrado: " + p + "\nComparaciones: " + comp + " (de " + arbol.getCantidad()
                    + " platos). Una búsqueda secuencial habría revisado hasta " + arbol.getCantidad() + ".");
        }
    }

    private void eliminar() {
        if (arbol.estaVacio()) {
            JOptionPane.showMessageDialog(view, "El árbol está vacío; no hay nada que eliminar.");
            return;
        }
        String nombre = JOptionPane.showInputDialog(view, "Nombre del plato a eliminar:");
        if (nombre == null || nombre.trim().isEmpty()) {
            return;
        }
        Plato p = arbol.buscar(nombre.trim());
        if (p == null) {
            view.areaResultados.setText("No existe un plato llamado \"" + nombre.trim() + "\".");
            return;
        }
        int conf = JOptionPane.showConfirmDialog(view, "¿Eliminar el plato \"" + p.getNombre() + "\"?",
                "Confirmar", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (conf != JOptionPane.OK_OPTION) {
            return;
        }
        if (platoDAO.eliminar(p.getIdPlato()) && arbol.eliminar(p.getNombre())) {
            refrescar(null);
            view.areaResultados.setText("Eliminado: " + p.getNombre() + ". Nodos: " + arbol.getCantidad()
                    + ", altura: " + arbol.altura() + ".");
            alCambiarPlatos.run();
        } else {
            JOptionPane.showMessageDialog(view, "No se pudo eliminar el plato.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void recorrido(String titulo, String resultado) {
        view.areaResultados.setText(arbol.estaVacio()
                ? "El árbol está vacío; no hay recorrido que mostrar."
                : titulo + ":\n" + resultado);
    }
}

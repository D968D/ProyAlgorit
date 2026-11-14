package controller;

import dao.MesaDAO;
import dao.ReservaDAO;
import estructura.ArbolAVLReservas;
import estructura.NodoAVL;
import model.Reserva;
import view.AgregarReservaDialog;
import view.PanelArbolGenerico.Dibujable;
import view.VisorArbolView;

import javax.swing.*;
import java.sql.SQLException;

/**
 * AVL de reservas por ID (Capítulo 10).
 * Modo BD: el árbol se arma con las reservas de la base y los cambios se guardan.
 * Modo DEMO: árbol aparte, solo en memoria, para probar secuencias de inserción
 * (por ejemplo 10,20,30 provoca una rotación) sin tocar la base de datos.
 */
public class ArbolAVLController {

    private final VisorArbolView view;
    private final ReservaDAO reservaDAO = new ReservaDAO();
    private final MesaDAO mesaDAO = new MesaDAO();
    private final Runnable alCambiarReservas;
    private ArbolAVLReservas arbol = new ArbolAVLReservas();
    private boolean modoDemo = false;

    public ArbolAVLController(VisorArbolView view, Runnable alCambiarReservas) {
        this.view = view;
        this.alCambiarReservas = alCambiarReservas;

        cargarDesdeBD();

        view.btnInsertar.addActionListener(e -> insertar());
        view.btnBuscar.addActionListener(e -> buscar());
        view.btnEliminar.addActionListener(e -> eliminar());
        view.btnPreorden.addActionListener(e -> recorrido("PREORDEN (raíz, izquierdo, derecho)", arbol.preorden()));
        view.btnInorden.addActionListener(e -> recorrido("INORDEN (IDs de menor a mayor)", arbol.inorden()));
        view.btnPostorden.addActionListener(e -> recorrido("POSTORDEN (izquierdo, derecho, raíz)", arbol.postorden()));
        view.btnDemo.addActionListener(e -> demo());
        view.btnReal.addActionListener(e -> cargarDesdeBD());
        view.btnVolver.addActionListener(e -> view.dispose());
        view.setVisible(true);
    }

    private Dibujable envolver(NodoAVL n) {
        if (n == null) return null;
        final Dibujable izq = envolver(n.getIzquierdo());
        final Dibujable der = envolver(n.getDerecho());
        final Reserva r = n.getReserva();
        final String fe = "FE=" + arbol.factorEquilibrio(n);
        return new Dibujable() {
            public String id() { return String.valueOf(r.getIdReserva()); }
            public String nombre() { return r.getCliente(); }
            public String extra() { return fe; }
            public Dibujable izquierdo() { return izq; }
            public Dibujable derecho() { return der; }
        };
    }

    private void refrescar(String idResaltado) {
        view.mostrar(envolver(arbol.getRaiz()), idResaltado);
    }

    private void cargarDesdeBD() {
        modoDemo = false;
        arbol = new ArbolAVLReservas();
        int rotaciones = 0;
        try {
            for (Reserva r : reservaDAO.listarTodas()) {
                arbol.insertar(r);
                rotaciones += arbol.getRotaciones();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "No se pudieron cargar las reservas:\n" + ex.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
        refrescar(null);
        view.areaResultados.setText("AVL con datos de la BD: " + arbol.getCantidad() + " reserva(s), altura "
                + arbol.altura() + ", " + rotaciones + " rotación(es) al cargar.");
    }

    private void insertar() {
        if (modoDemo) {
            Integer id = pedirEntero("ID (número) a insertar en el árbol demo:");
            if (id == null) return;
            Reserva r = new Reserva(id, "Demo " + id, "1/1/2026", "12:00", 1, 1, "Pendiente");
            if (!arbol.insertar(r)) {
                view.areaResultados.setText("El ID " + id + " ya existe; el AVL no admite claves repetidas.");
                return;
            }
            refrescar(String.valueOf(id));
            view.areaResultados.setText("Insertado " + id + " (demo).\n" + arbol.getRegistro());
            return;
        }

        AgregarReservaDialog dialog = new AgregarReservaDialog(view, mesaDAO.listarTodas());
        dialog.setVisible(true);
        if (!dialog.isConfirmado()) {
            return;
        }
        try {
            Reserva r = reservaDAO.insertar(dialog.getCliente(), dialog.getFecha(), dialog.getHora(),
                    dialog.getPersonas(), dialog.getMesa(), "Pendiente");
            arbol.insertar(r);
            refrescar(String.valueOf(r.getIdReserva()));
            view.areaResultados.setText("Insertada la reserva " + r.getIdReserva() + " de " + r.getCliente()
                    + ". Altura: " + arbol.altura() + ".\n" + arbol.getRegistro());
            alCambiarReservas.run();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "No se pudo guardar la reserva:\n" + ex.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscar() {
        if (arbol.estaVacio()) {
            JOptionPane.showMessageDialog(view, "El árbol está vacío.");
            return;
        }
        Integer id = pedirEntero("ID de la reserva a buscar:");
        if (id == null) return;
        Reserva r = arbol.buscar(id);
        if (r == null) {
            refrescar(null);
            view.areaResultados.setText("No existe una reserva con ID " + id + ".");
        } else {
            refrescar(String.valueOf(id));
            view.areaResultados.setText("Encontrada: ID " + r.getIdReserva() + " | " + r.getCliente() + " | "
                    + r.getFecha() + " " + r.getHora() + " | Mesa " + r.getIdMesa() + " | " + r.getEstado());
        }
    }

    private void eliminar() {
        if (arbol.estaVacio()) {
            JOptionPane.showMessageDialog(view, "El árbol está vacío; no hay nada que eliminar.");
            return;
        }
        Integer id = pedirEntero("ID de la reserva a eliminar:");
        if (id == null) return;
        if (arbol.buscar(id) == null) {
            view.areaResultados.setText("No existe una reserva con ID " + id + ".");
            return;
        }
        int conf = JOptionPane.showConfirmDialog(view, "¿Eliminar la reserva " + id + "?",
                "Confirmar", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (conf != JOptionPane.OK_OPTION) return;

        try {
            if (!modoDemo) {
                reservaDAO.eliminar(id);
            }
            arbol.eliminar(id);
            refrescar(null);
            view.areaResultados.setText("Eliminada la reserva " + id + ". Altura: " + arbol.altura() + ".\n"
                    + arbol.getRegistro());
            if (!modoDemo) {
                alCambiarReservas.run();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "No se pudo eliminar la reserva:\n" + ex.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Pide una secuencia de IDs y los inserta uno a uno mostrando qué rotaciones ocurren. */
    private void demo() {
        String texto = JOptionPane.showInputDialog(view,
                "Secuencia de IDs separados por coma.\nEjemplos: 10,20,30   |   30,10,20   |   10,30,20   |   10,20,30,40,50,25",
                "10,20,30,40,50,25");
        if (texto == null || texto.trim().isEmpty()) return;

        ArbolAVLReservas nuevo = new ArbolAVLReservas();
        StringBuilder log = new StringBuilder("DEMO (solo en memoria, no se guarda en la BD)\n");
        try {
            for (String parte : texto.split(",")) {
                int id = Integer.parseInt(parte.trim());
                Reserva r = new Reserva(id, "Demo " + id, "1/1/2026", "12:00", 1, 1, "Pendiente");
                if (!nuevo.insertar(r)) {
                    log.append("Insertar ").append(id).append(": repetido, se ignora\n");
                    continue;
                }
                log.append("Insertar ").append(id).append(": ");
                log.append(nuevo.getRotaciones() == 0 ? "sin rotaciones" : nuevo.getRegistro().replace("\n", " / "));
                log.append("\n");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "La secuencia debe contener solo números enteros separados por coma.");
            return;
        }
        arbol = nuevo;
        modoDemo = true;
        refrescar(null);
        log.append("Resultado: altura ").append(arbol.altura()).append(", inorden: ").append(arbol.inorden());
        view.areaResultados.setText(log.toString());
    }

    private void recorrido(String titulo, String resultado) {
        view.areaResultados.setText(arbol.estaVacio()
                ? "El árbol está vacío; no hay recorrido que mostrar."
                : titulo + ":\n" + resultado);
    }

    private Integer pedirEntero(String mensaje) {
        String texto = JOptionPane.showInputDialog(view, mensaje);
        if (texto == null) return null;
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "Ingrese un número entero válido.");
            return null;
        }
    }
}

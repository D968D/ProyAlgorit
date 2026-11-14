package controller;

import dao.MesaDAO;
import dao.ReservaDAO;
import estructura.ColaPrioridadReservas;
import estructura.ColaReservas;
import estructura.ListaDobleReservas;
import estructura.ListaReservas;
import model.Mesa;
import model.Reserva;
import view.AgregarReservaDialog;
import view.ReservaView;
import view.VisorArbolView;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.SQLException;
import java.util.List;

/**
 * Controla la pantalla de reservas. Aquí viven las estructuras de datos y el acceso a la BD:
 *   - ListaReservas (simple): tabla, búsqueda y ordenamiento.
 *   - ListaDobleReservas: historial hacia adelante y hacia atrás.
 *   - ColaReservas (FIFO): orden de llegada de las reservas pendientes.
 *   - ColaPrioridadReservas: orden de atención (fecha y hora más próximas primero).
 *   - ArbolAVLReservas: se abre desde el botón ÁRBOL AVL.
 */
public class ReservaController {

    private final ReservaView view;
    private final ReservaDAO reservaDAO = new ReservaDAO();
    private final MesaDAO mesaDAO = new MesaDAO();

    private ListaReservas lista = new ListaReservas();
    private ListaDobleReservas listaDoble = new ListaDobleReservas();
    private ColaReservas colaLlegada = new ColaReservas();
    private ColaPrioridadReservas colaPrioridad = new ColaPrioridadReservas();

    public ReservaController(ReservaView view) {
        this.view = view;

        cargarDesdeBD();

        view.btnAgregar.addActionListener(e -> agregar());
        view.btnEliminar.addActionListener(e -> eliminar());
        view.btnBuscar.addActionListener(e -> buscar());
        view.btnOrdenar.addActionListener(e -> ordenar());
        view.btnHistorial.addActionListener(e -> mostrarHistorial());
        view.btnVerCola.addActionListener(e -> verCola());
        view.btnAtenderCola.addActionListener(e -> atenderSiguiente());
        view.btnAVL.addActionListener(e -> new ArbolAVLController(
                new VisorArbolView("Árbol AVL de Reservas",
                        "Reservas por ID, factor de equilibrio y rotaciones", true),
                this::cargarDesdeBD));
        view.btnVolver.addActionListener(e -> view.dispose());

        view.setVisible(true);
    }

    /** Reconstruye listas y colas desde la BD; solo las reservas "Pendiente" entran a las colas. */
    private void cargarDesdeBD() {
        lista = new ListaReservas();
        listaDoble = new ListaDobleReservas();
        colaLlegada = new ColaReservas();
        colaPrioridad = new ColaPrioridadReservas();

        try {
            for (Reserva r : reservaDAO.listarTodas()) {
                lista.insertar(r);
                listaDoble.insertar(r);
                if ("Pendiente".equalsIgnoreCase(r.getEstado())) {
                    colaLlegada.encolar(r);
                    colaPrioridad.encolar(r);
                }
            }
        } catch (SQLException ex) {
            errorBD("No se pudieron cargar las reservas", ex);
        }
        cargarTabla();
    }

    private void cargarTabla() {
        view.modeloTabla.setRowCount(0);
        for (Reserva r : lista.aArreglo()) {
            view.modeloTabla.addRow(new Object[]{
                    r.getIdReserva(), r.getCliente(), r.getFecha(), r.getHora(),
                    r.getCantidadPersonas(), r.getIdMesa(), r.getEstado()
            });
        }
    }

    private void errorBD(String mensaje, SQLException ex) {
        JOptionPane.showMessageDialog(view, mensaje + ":\n" + ex.getMessage(),
                "Error de base de datos", JOptionPane.ERROR_MESSAGE);
    }

    private void agregar() {
        List<Mesa> mesas = mesaDAO.listarTodas();
        AgregarReservaDialog dialog = new AgregarReservaDialog(view, mesas);
        dialog.setVisible(true);
        if (!dialog.isConfirmado()) {
            return;
        }
        try {
            Reserva reserva = reservaDAO.insertar(dialog.getCliente(), dialog.getFecha(), dialog.getHora(),
                    dialog.getPersonas(), dialog.getMesa(), "Pendiente");
            lista.insertar(reserva);
            listaDoble.insertar(reserva);
            colaLlegada.encolar(reserva);
            colaPrioridad.encolar(reserva);
            cargarTabla();
        } catch (SQLException ex) {
            errorBD("No se pudo guardar la reserva", ex);
        }
    }

    private void eliminar() {
        int fila = view.getFilaSeleccionada();
        if (fila == -1) {
            JOptionPane.showMessageDialog(view, "Seleccione una reserva de la tabla.");
            return;
        }
        int id = (int) view.modeloTabla.getValueAt(fila, 0);
        int resp = JOptionPane.showConfirmDialog(view, "¿Eliminar la reserva N° " + id + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (resp != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            reservaDAO.eliminar(id);
            lista.eliminar(id);
            listaDoble.eliminar(id);
            colaLlegada.eliminarPorId(id);
            colaPrioridad.eliminarPorId(id);
            cargarTabla();
        } catch (SQLException ex) {
            errorBD("No se pudo eliminar la reserva", ex);
        }
    }

    private void buscar() {
        String texto = JOptionPane.showInputDialog(view, "Ingrese el ID de la reserva:");
        if (texto == null) {
            return;
        }
        try {
            int id = Integer.parseInt(texto.trim());
            Reserva r = lista.buscar(id);
            if (r == null) {
                JOptionPane.showMessageDialog(view, "No se encontró una reserva con ese ID.");
                return;
            }
            JOptionPane.showMessageDialog(view,
                    "ID: " + r.getIdReserva() + "\n" + "Cliente: " + r.getCliente() + "\n"
                            + "Fecha: " + r.getFecha() + "\n" + "Hora: " + r.getHora() + "\n"
                            + "Personas: " + r.getCantidadPersonas() + "\n" + "Mesa: " + r.getIdMesa() + "\n"
                            + "Estado: " + r.getEstado(),
                    "Reserva encontrada", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "Ingrese un ID válido.");
        }
    }

    /** Ordena la lista enlazada por el criterio elegido y muestra el resultado en la tabla. */
    private void ordenar() {
        String[] opciones = {"ID", "Cliente (A-Z)", "Fecha y hora"};
        int eleccion = JOptionPane.showOptionDialog(view, "¿Por qué criterio desea ordenar las reservas?",
                "Ordenar reservas", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, opciones, opciones[0]);
        if (eleccion < 0) {
            return;
        }
        ListaReservas.Criterio[] criterios = ListaReservas.Criterio.values();
        lista.ordenar(criterios[eleccion]);
        cargarTabla();
    }

    private void mostrarHistorial() {
        String adelante = listaDoble.obtenerRecorridoAdelante();
        String atras = listaDoble.obtenerRecorridoAtras();
        if (adelante.isEmpty()) {
            JOptionPane.showMessageDialog(view, "No hay reservas registradas.");
            return;
        }
        String mensaje = "RECORRIDO HACIA ADELANTE\n========================\n" + adelante
                + "\nRECORRIDO HACIA ATRÁS\n=====================\n" + atras;
        mostrarTexto(mensaje, "Historial de Reservas", 650, 300);
    }

    /** Muestra las dos colas: la de llegada (FIFO) y la de atención (prioridad por fecha y hora). */
    private void verCola() {
        String texto = "ORDEN DE LLEGADA (cola FIFO)\n============================\n" + colaLlegada.recorrer()
                + "\nORDEN DE ATENCIÓN (cola con prioridad: fecha y hora más próximas)\n"
                + "=================================================================\n"
                + colaPrioridad.recorrer();
        mostrarTexto(texto, "Cola de reservas pendientes", 600, 320);
    }

    /** Atiende la reserva de mayor prioridad (la más próxima en el tiempo). */
    private void atenderSiguiente() {
        Reserva primera = colaPrioridad.primero();
        if (primera == null) {
            JOptionPane.showMessageDialog(view, "No hay reservas pendientes.");
            return;
        }
        int opcion = JOptionPane.showConfirmDialog(view,
                "¿Atender la reserva de " + primera.getCliente() + " (N° " + primera.getIdReserva() + ")?\n"
                        + "Es la más próxima: " + primera.getFecha() + " " + primera.getHora(),
                "Confirmar atención", JOptionPane.YES_NO_OPTION);
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            reservaDAO.actualizarEstado(primera.getIdReserva(), "Atendida");
            colaPrioridad.desencolar();
            colaLlegada.eliminarPorId(primera.getIdReserva());
            cargarDesdeBD();
            JOptionPane.showMessageDialog(view, "La reserva fue atendida correctamente.");
        } catch (SQLException ex) {
            errorBD("No se pudo atender la reserva", ex);
        }
    }

    private void mostrarTexto(String texto, String titulo, int ancho, int alto) {
        JTextArea area = new JTextArea(texto);
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(ancho, alto));
        JOptionPane.showMessageDialog(view, scroll, titulo, JOptionPane.INFORMATION_MESSAGE);
    }
}

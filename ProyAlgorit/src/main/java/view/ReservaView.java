package view;

import dao.MesaDAO;
import dao.ReservaDAO;
import estructura.ListaDobleReservas;
import estructura.ListaReservas;
import tad.TADReserva;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

import static view.UIStyle.*;

public class ReservaView extends JFrame {

    private ListaReservas listaReservas = new ListaReservas();
    private ListaDobleReservas listaDobleReservas = new ListaDobleReservas();
    private final ReservaDAO reservaDAO = new ReservaDAO();
    private final MesaDAO mesaDAO = new MesaDAO();

    private final DefaultTableModel modeloTabla;
    private final JTable tabla;

    public ReservaView() {
        setTitle("Gestión de Reservas - C&R OrderManager");
        setSize(900, 460);
        setMinimumSize(new Dimension(860, 400));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(FONDO_EXTERNO);
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        RoundedPanel card = new RoundedPanel(22, CREMA);
        card.setLayout(new BorderLayout(0, 12));
        card.add(headerBar("Gestión de Reservas", "Reservas del salón, búsqueda y orden por ID"), BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(
                new String[]{"ID", "Cliente", "Fecha", "Hora", "Personas", "Mesa", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        styleTable(tabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(45);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(170);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));
        scroll.setBackground(CREMA);
        scroll.getViewport().setBackground(Color.WHITE);
        card.add(scroll, BorderLayout.CENTER);

        RoundedButton btnAgregar   = button("AGREGAR", IconoVector.Tipo.AGREGAR, VERDE_OLIVA, VERDE_HOVER);
        RoundedButton btnEliminar  = button("ELIMINAR", IconoVector.Tipo.ELIMINAR, TERRACOTA, TERRACOTA_HOVER);
        RoundedButton btnBuscar    = button("BUSCAR", IconoVector.Tipo.BUSCAR, NARANJA, NARANJA_HOVER);
        RoundedButton btnOrdenar   = button("ORDENAR", IconoVector.Tipo.ORDENAR, MOSTAZA, MOSTAZA_HOVER);
        RoundedButton btnHistorial = button("HISTORIAL", IconoVector.Tipo.HISTORIAL, TAUPE, TAUPE_HOVER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 16));
        panelBotones.setBackground(CREMA);
        for (JButton b : new JButton[]{btnAgregar, btnEliminar, btnBuscar, btnOrdenar, btnHistorial}) {
            b.setPreferredSize(new Dimension(150, 40));
            panelBotones.add(b);
        }
        card.add(panelBotones, BorderLayout.SOUTH);

        btnAgregar.addActionListener(e -> agregarReserva());
        btnEliminar.addActionListener(e -> eliminarReserva());
        btnBuscar.addActionListener(e -> buscarReserva());
        btnOrdenar.addActionListener(e -> {
            listaReservas.ordenar();
            cargarTabla();
        });
        btnHistorial.addActionListener(e -> mostrarHistorial());

        root.add(card, BorderLayout.CENTER);
        setContentPane(root);
        activarEscalado(this);

        cargarDesdeBD();
    }

    private void cargarDesdeBD() {
        listaReservas = new ListaReservas();
        listaDobleReservas = new ListaDobleReservas();
        try {
            for (TADReserva r : reservaDAO.listarTodas()) {
                listaReservas.insertar(r);
                listaDobleReservas.insertar(r);
            }
        } catch (SQLException ex) {
            errorBD("No se pudieron cargar las reservas", ex);
        }
        cargarTabla();
    }

    private void errorBD(String mensaje, SQLException ex) {
        JOptionPane.showMessageDialog(this, mensaje + ":\n" + ex.getMessage(),
                "Error de base de datos", JOptionPane.ERROR_MESSAGE);
    }


    private void agregarReserva() {
        List<model.Mesa> mesas = mesaDAO.listarTodas();
        AgregarReservaDialog dialog = new AgregarReservaDialog(this, mesas);
        dialog.setVisible(true);
        if (!dialog.isConfirmado()) {
            return;
        }

        try {

            TADReserva reserva = reservaDAO.insertar(
                    dialog.getCliente(), dialog.getFecha(), dialog.getHora(),
                    dialog.getPersonas(), dialog.getMesa(), "Pendiente");

            listaReservas.insertar(reserva);
            listaDobleReservas.insertar(reserva);
            cargarTabla();
        } catch (SQLException ex) {
            errorBD("No se pudo guardar la reserva", ex);
        }
    }


    private void eliminarReserva() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una reserva de la tabla.");
            return;
        }

        int id = (int) modeloTabla.getValueAt(fila, 0);
        int resp = JOptionPane.showConfirmDialog(this,
                "¿Eliminar la reserva N° " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (resp != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            reservaDAO.eliminar(id);
            listaReservas.eliminar(id);
            listaDobleReservas.eliminar(id);
            cargarTabla();
        } catch (SQLException ex) {
            errorBD("No se pudo eliminar la reserva", ex);
        }
    }


    private void buscarReserva() {
        String texto = JOptionPane.showInputDialog(this, "Ingrese el ID de la reserva:");
        if (texto == null) {
            return;
        }

        try {
            int id = Integer.parseInt(texto.trim());
            TADReserva r = listaReservas.buscar(id);

            if (r == null) {
                JOptionPane.showMessageDialog(this, "No se encontró una reserva con ese ID.");
                return;
            }
            JOptionPane.showMessageDialog(this,
                    "ID: " + r.getIdReserva() + "\n" +
                            "Cliente: " + r.getCliente() + "\n" +
                            "Fecha: " + r.getFecha() + "\n" +
                            "Hora: " + r.getHora() + "\n" +
                            "Personas: " + r.getCantidadPersonas() + "\n" +
                            "Mesa: " + r.getIdMesa() + "\n" +
                            "Estado: " + r.getEstado(),
                    "Reserva encontrada", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un ID válido.");
        }
    }


    private void mostrarHistorial() {
        String adelante = listaDobleReservas.obtenerRecorridoAdelante();
        String atras = listaDobleReservas.obtenerRecorridoAtras();

        if (adelante.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay reservas registradas.");
            return;
        }

        String mensaje =
                "RECORRIDO HACIA ADELANTE\n" +
                        "========================\n" +
                        adelante +
                        "\nRECORRIDO HACIA ATRÁS\n" +
                        "=====================\n" +
                        atras;

        JTextArea area = new JTextArea(mensaje);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(650, 300));

        JOptionPane.showMessageDialog(this, scroll, "Historial de Reservas", JOptionPane.INFORMATION_MESSAGE);
    }

    private void cargarTabla() {
        listaReservas.cargarEnTabla(modeloTabla);
    }
}

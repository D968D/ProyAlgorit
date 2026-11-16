package controller;

import dao.PedidoDAO;
import dao.MesaDAO;
import estructura.ArbolABBPedidos;
import model.DetallePedido;
import model.Mesa;
import model.Pedido;
import view.AgregarPedidoDialog;
import view.IconoVector;
import view.PanelArbolGenerico;
import view.PanelArbolGenerico.Dibujable;
import view.PedidoView;
import view.UIStyle;
import util.BoletaPDF;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PedidoController {

    private final PedidoView view;
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final MesaDAO mesaDAO = new MesaDAO();
    private ArbolABBPedidos arbolPedidos = new ArbolABBPedidos();
    private List<Pedido> pedidosVisibles = new ArrayList<>();

    public PedidoController(PedidoView view) {
        this.view = view;

        cargarDesdeBD();

        view.btnAgregar.addActionListener(e -> agregar());
        view.btnAgregarPlatos.addActionListener(e -> agregarPlatos());
        view.btnActualizar.addActionListener(e -> actualizarEstado());
        view.btnEliminar.addActionListener(e -> eliminar());
        view.btnBuscar.addActionListener(e -> buscar());
        view.btnBoleta.addActionListener(e -> generarBoleta());
        view.btnVerTodos.addActionListener(e -> mostrarTodosLosPedidos());
        view.btnArbol.addActionListener(e -> abrirVisorArbol());
        view.addPedidoDoubleClickListener(this::mostrarDetallesPedido);
        view.btnVolver.addActionListener(e -> view.dispose());

        view.setVisible(true);
    }

    private void cargarDesdeBD() {
        arbolPedidos = new ArbolABBPedidos();
        try {
            pedidosVisibles = pedidoDAO.listarTodos();
            for (Pedido pedido : pedidosVisibles) {
                arbolPedidos.insertar(pedido);
            }
        } catch (SQLException ex) {
            pedidosVisibles = new ArrayList<>();
            errorBD("No se pudieron cargar los pedidos", ex);
        }
        cargarTabla();
    }

    private void cargarTabla() {
        view.modeloTabla.setRowCount(0);
        for (Pedido pedido : pedidosVisibles) {
            view.modeloTabla.addRow(new Object[]{
                    pedido.getIdPedido(), pedido.getNumMesa(), pedido.getDni(), pedido.getFechaHora(),
                    pedido.getEstado(), pedido.getTotal(), pedido.getMetodoPago()
            });
        }
    }

    private void errorBD(String mensaje, SQLException ex) {
        JOptionPane.showMessageDialog(view, mensaje + ":\n" + ex.getMessage(),
                "Error de base de datos", JOptionPane.ERROR_MESSAGE);
    }

    private void agregar() {
        try {
            List<Mesa> mesasDisponibles = mesaDAO.listarDisponibles();
            if (mesasDisponibles.isEmpty()) {
                JOptionPane.showMessageDialog(view, "No hay mesas libres disponibles.");
                return;
            }
            AgregarPedidoDialog dialog = new AgregarPedidoDialog(view, this, mesasDisponibles);
            dialog.setVisible(true);
        } catch (SQLException ex) {
            errorBD("No se pudieron cargar las mesas disponibles", ex);
        }
    }

    public boolean registrarPedido(Pedido pedido) {
        try {
            if (pedidoDAO.insertarConDetalles(pedido)) {
                cargarDesdeBD();
                return true;
            }
            JOptionPane.showMessageDialog(view,
                    "La mesa seleccionada ya no está disponible o tiene un pedido pendiente.",
                    "Mesa no disponible", JOptionPane.WARNING_MESSAGE);
            return false;
        } catch (SQLException ex) {
            errorBD("No se pudo guardar el pedido", ex);
            return false;
        }
    }

    private void actualizarEstado() {
        int fila = view.getFilaSeleccionada();
        if (fila == -1) {
            JOptionPane.showMessageDialog(view, "Seleccione un pedido de la tabla.");
            return;
        }

        int id = (int) view.modeloTabla.getValueAt(fila, 0);
        Pedido pedido = buscarPorIdEnArbol(id);
        if (pedido == null) {
            cargarDesdeBD();
            return;
        }
        String[] estados = {"PENDIENTE", "ATENDIDO"};
        int estadoActual = "ATENDIDO".equalsIgnoreCase(pedido.getEstado()) ? 1 : 0;
        int eleccion = JOptionPane.showOptionDialog(view, "Seleccione el estado del pedido:",
                "Actualizar estado", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, estados, estados[estadoActual]);
        if (eleccion < 0) {
            return;
        }

        pedido.setEstado(estados[eleccion]);
        actualizarPedido(pedido);
    }

    public boolean actualizarPedido(Pedido pedido) {
        try {
            if (!pedidoDAO.actualizar(pedido)) {
                JOptionPane.showMessageDialog(view, "No se encontró el pedido para actualizar.");
                return false;
            }
            cargarDesdeBD();
            return true;
        } catch (SQLException ex) {
            errorBD("No se pudo actualizar el pedido", ex);
            return false;
        }
    }

    private void eliminar() {
        int fila = view.getFilaSeleccionada();
        if (fila == -1) {
            JOptionPane.showMessageDialog(view, "Seleccione un pedido de la tabla.");
            return;
        }

        int id = (int) view.modeloTabla.getValueAt(fila, 0);
        int respuesta = JOptionPane.showConfirmDialog(view, "¿Eliminar el pedido N° " + id + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        eliminarPedido(id);
    }

    public boolean eliminarPedido(int idPedido) {
        try {
            if (!pedidoDAO.eliminar(idPedido)) {
                JOptionPane.showMessageDialog(view, "No se encontró el pedido para eliminar.");
                return false;
            }
            cargarDesdeBD();
            return true;
        } catch (SQLException ex) {
            errorBD("No se pudo eliminar el pedido", ex);
            return false;
        }
    }

    private void buscar() {
        String texto = JOptionPane.showInputDialog(view, "Ingrese el ID del pedido:");
        if (texto == null) {
            return;
        }

        try {
            int id = Integer.parseInt(texto.trim());
            Pedido pedido = buscarPorIdEnArbol(id);
            if (pedido == null) {
                JOptionPane.showMessageDialog(view, "No se encontró un pedido con ese ID.");
                return;
            }
            JOptionPane.showMessageDialog(view,
                    "ID: " + pedido.getIdPedido() + "\nCliente / DNI: " + pedido.getDni() + "\n"
                            + "Mesa: " + pedido.getNumMesa() + "\nFecha y hora: " + pedido.getFechaHora() + "\n"
                            + "Estado: " + pedido.getEstado() + "\nTotal: " + pedido.getTotal(),
                    "Pedido encontrado", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "Ingrese un ID válido.");
        }
    }

    private void generarBoleta() {
        int fila = view.getFilaSeleccionada();
        if (fila == -1) {
            JOptionPane.showMessageDialog(view, "Seleccione un pedido de la tabla.");
            return;
        }

        int id = (int) view.modeloTabla.getValueAt(fila, 0);
        Pedido pedido = buscarPorIdEnArbol(id);
        if (pedido == null) {
            cargarDesdeBD();
            return;
        }

        try {
            List<DetallePedido> detalles = obtenerDetallesPorPedido(id);
            if (detalles.isEmpty()) {
                JOptionPane.showMessageDialog(view,
                        "El pedido N° " + id + " aún no tiene platos. Agrégalos para emitir la boleta.");
                return;
            }

            File carpeta = carpetaBoletas();
            File destino = new File(carpeta, "Boleta_" + BoletaPDF.numeroBoleta(id) + ".pdf");
            BoletaPDF.generar(pedido, detalles, destino);

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(destino);
            } else {
                JOptionPane.showMessageDialog(view, "Boleta guardada en:\n" + destino.getAbsolutePath());
            }
        } catch (SQLException ex) {
            errorBD("No se pudieron cargar los platos del pedido", ex);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(view, "No se pudo generar la boleta:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Carpeta donde se guardan las boletas: Documentos/Boletas (se crea si no existe). */
    private File carpetaBoletas() throws IOException {
        File documentos = new File(System.getProperty("user.home"), "Documents");
        if (!documentos.isDirectory()) {
            documentos = new File(System.getProperty("user.home"), "Documentos");
        }
        File base = documentos.isDirectory() ? documentos : new File(System.getProperty("user.home"));
        File carpeta = new File(base, "Boletas");
        if (!carpeta.isDirectory() && !carpeta.mkdirs()) {
            throw new IOException("No se pudo crear la carpeta " + carpeta.getAbsolutePath());
        }
        return carpeta;
    }

    public Pedido buscarPorIdEnArbol(int idPedido) {
        return arbolPedidos.buscar(idPedido);
    }

    private void agregarPlatos() {
        int fila = view.getFilaSeleccionada();
        if (fila == -1) {
            JOptionPane.showMessageDialog(view, "Seleccione un pedido pendiente.");
            return;
        }

        int idPedido = (int) view.modeloTabla.getValueAt(fila, 0);
        Pedido pedido = buscarPorIdEnArbol(idPedido);
        if (pedido == null) {
            cargarDesdeBD();
            return;
        }
        new AgregarPedidoDialog(view, this, pedido).setVisible(true);
    }

    public boolean agregarPlatosAlPedido(int idPedido, List<DetallePedido> detalles) {
        try {
            if (!pedidoDAO.agregarDetalles(idPedido, detalles)) {
                JOptionPane.showMessageDialog(view,
                        "No se pudieron agregar platos. El pedido ya no está pendiente.");
                cargarDesdeBD();
                return false;
            }
            cargarDesdeBD();
            return true;
        } catch (SQLException ex) {
            errorBD("No se pudieron agregar platos al pedido", ex);
            return false;
        }
    }

    public List<DetallePedido> obtenerDetallesPorPedido(int idPedido) throws SQLException {
        return pedidoDAO.listarDetallesPorPedido(idPedido);
    }

    private void mostrarTodosLosPedidos() {
        try {
            List<Pedido> pedidos = pedidoDAO.listarTodosLosPedidos();
            DefaultTableModel modelo = new DefaultTableModel(
                    new String[]{"ID", "Mesa", "Cliente / DNI", "Fecha y hora", "Estado", "Total", "Método de pago"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            for (Pedido pedido : pedidos) {
                modelo.addRow(new Object[]{
                        pedido.getIdPedido(), pedido.getNumMesa(), pedido.getDni(), pedido.getFechaHora(),
                        pedido.getEstado(), pedido.getTotal(), pedido.getMetodoPago()
                });
            }

            JTable tabla = UIStyle.crearTabla(modelo, "Aún no hay pedidos registrados.");
            tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane scroll = new JScrollPane(tabla);
            scroll.setPreferredSize(new Dimension(900, 360));

            UIStyle.RoundedPanel tarjeta = UIStyle.tarjeta();
            tarjeta.add(scroll, BorderLayout.CENTER);

            JPanel contenido = UIStyle.panelDialogo(
                    "Todos los pedidos", "Historial de pedidos pendientes y atendidos");
            contenido.add(tarjeta, BorderLayout.CENTER);
            UIStyle.RoundedButton cerrar = UIStyle.secundario("Cerrar", null);
            contenido.add(UIStyle.pieDialogo(cerrar), BorderLayout.SOUTH);

            JDialog dialog = new JDialog(view, "Historial de pedidos", true);
            dialog.setContentPane(contenido);
            cerrar.addActionListener(e -> dialog.dispose());
            UIStyle.ajustarDialogo(dialog, view, 980);
            dialog.setVisible(true);
        } catch (SQLException ex) {
            errorBD("No se pudieron cargar todos los pedidos", ex);
        }
    }

    private void mostrarDetallesPedido(int idPedido) {
        try {
            List<DetallePedido> detalles = obtenerDetallesPorPedido(idPedido);
            if (detalles.isEmpty()) {
                JOptionPane.showMessageDialog(view, "El pedido #" + idPedido + " no tiene detalles registrados.");
                return;
            }

            DefaultTableModel modelo = new DefaultTableModel(
                    new String[]{"ID Plato", "Plato", "Cantidad", "Precio unitario", "Subtotal"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            double total = 0;
            for (DetallePedido detalle : detalles) {
                modelo.addRow(new Object[]{
                        detalle.getIdPlato(),
                        detalle.getNombrePlato(),
                        detalle.getCantidad(),
                        String.format("%.2f", detalle.getPrecioUnitario()),
                        String.format("%.2f", detalle.getSubtotal())
                });
                total += detalle.getSubtotal();
            }

            JTable tabla = new JTable(modelo);
            tabla.setRowHeight(26);
            JScrollPane scroll = new JScrollPane(tabla);
            scroll.setPreferredSize(new Dimension(620, Math.min(280, 40 + detalles.size() * 26)));
            JPanel contenido = new JPanel(new BorderLayout(0, 8));
            contenido.add(scroll, BorderLayout.CENTER);
            contenido.add(new JLabel(String.format("Total del pedido: S/. %.2f", total)), BorderLayout.SOUTH);
            JOptionPane.showMessageDialog(view, contenido, "Detalles del pedido #" + idPedido,
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            errorBD("No se pudieron cargar los detalles del pedido", ex);
        }
    }

    private void abrirVisorArbol() {
        JFrame visor = new JFrame("Árbol ABB de Pedidos - C&R OrderManager");
        visor.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel root = UIStyle.raiz();
        JPanel superior = new JPanel();
        superior.setOpaque(false);
        superior.setLayout(new BoxLayout(superior, BoxLayout.Y_AXIS));
        UIStyle.RoundedButton volver = UIStyle.botonVolver();
        volver.addActionListener(e -> visor.dispose());
        superior.add(UIStyle.encabezado(
                "Árbol ABB de Pedidos", "Pedidos pendientes ordenados por ID", volver));

        JTextArea salidaRecorrido = new JTextArea(5, 40);
        salidaRecorrido.setEditable(false);
        salidaRecorrido.setLineWrap(true);
        salidaRecorrido.setWrapStyleWord(true);
        salidaRecorrido.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        salidaRecorrido.setForeground(UIStyle.TINTA);
        salidaRecorrido.setBackground(UIStyle.SUPERFICIE);
        salidaRecorrido.setBorder(new EmptyBorder(12, 16, 12, 16));
        salidaRecorrido.setText("ABB cargado: " + arbolPedidos.contarNodos() + " pedido(s), altura "
                + arbolPedidos.altura() + ".");

        UIStyle.RoundedButton btnPreOrden = UIStyle.discreto("Preorden", IconoVector.Tipo.ARBOL);
        UIStyle.RoundedButton btnInOrden = UIStyle.discreto("Inorden", IconoVector.Tipo.ARBOL);
        UIStyle.RoundedButton btnPostOrden = UIStyle.discreto("Postorden", IconoVector.Tipo.ARBOL);
        btnPreOrden.addActionListener(e -> mostrarRecorrido(
                "Preorden", arbolPedidos.recorridoPreOrden(), salidaRecorrido));
        btnInOrden.addActionListener(e -> mostrarRecorrido(
                "Inorden", arbolPedidos.recorridoInOrden(), salidaRecorrido));
        btnPostOrden.addActionListener(e -> mostrarRecorrido(
                "Postorden", arbolPedidos.recorridoPostOrden(), salidaRecorrido));
        superior.add(UIStyle.barraAcciones(
                new JComponent[]{}, new JComponent[]{btnPreOrden, btnInOrden, btnPostOrden}));
        root.add(superior, BorderLayout.NORTH);

        PanelArbolGenerico panelArbol = new PanelArbolGenerico();
        panelArbol.actualizar(envolverPedido(arbolPedidos.getRaiz()), null);
        JScrollPane arbolScroll = new JScrollPane(panelArbol);
        arbolScroll.setBorder(BorderFactory.createEmptyBorder());
        arbolScroll.getViewport().setBackground(UIStyle.SUPERFICIE);
        UIStyle.RoundedPanel tarjetaArbol = UIStyle.tarjeta();
        tarjetaArbol.add(arbolScroll, BorderLayout.CENTER);
        root.add(tarjetaArbol, BorderLayout.CENTER);

        JScrollPane salidaScroll = new JScrollPane(salidaRecorrido);
        salidaScroll.setBorder(BorderFactory.createEmptyBorder());
        salidaScroll.getViewport().setBackground(UIStyle.SUPERFICIE);
        UIStyle.RoundedPanel tarjetaSalida = UIStyle.tarjeta();
        tarjetaSalida.add(salidaScroll, BorderLayout.CENTER);
        JPanel sur = new JPanel(new BorderLayout());
        sur.setOpaque(false);
        sur.setBorder(new EmptyBorder(16, 0, 0, 0));
        sur.add(tarjetaSalida, BorderLayout.CENTER);
        root.add(sur, BorderLayout.SOUTH);

        visor.setContentPane(root);
        visor.setSize(940, 740);
        visor.setMinimumSize(new Dimension(900, 620));
        visor.setLocationRelativeTo(null);
        UIStyle.activarEscalado(visor);
        visor.setVisible(true);
    }

    private Dibujable envolverPedido(ArbolABBPedidos.NodoPedido nodo) {
        if (nodo == null) {
            return null;
        }
        Dibujable izquierdo = envolverPedido(nodo.izquierdo);
        Dibujable derecho = envolverPedido(nodo.derecho);
        Pedido pedido = nodo.dato;
        return new Dibujable() {
            @Override
            public String id() {
                return String.valueOf(pedido.getIdPedido());
            }

            @Override
            public String nombre() {
                return "Mesa " + pedido.getNumMesa();
            }

            @Override
            public String extra() {
                return null;
            }

            @Override
            public Dibujable izquierdo() {
                return izquierdo;
            }

            @Override
            public Dibujable derecho() {
                return derecho;
            }
        };
    }

    private void mostrarRecorrido(String nombre, List<Pedido> recorrido, JTextArea salida) {
        if (recorrido.isEmpty()) {
            salida.setText(nombre + ": el árbol está vacío.");
            return;
        }
        StringBuilder texto = new StringBuilder(nombre).append(": ");
        for (int i = 0; i < recorrido.size(); i++) {
            if (i > 0) {
                texto.append(" -> ");
            }
            texto.append("#").append(recorrido.get(i).getIdPedido());
        }
        salida.setText(texto.toString());
        salida.setCaretPosition(0);
    }
}

package controller;

import dao.PedidoDAO;
import dao.MesaDAO;
import estructura.ArbolABBPedidos;
import model.DetallePedido;
import model.Mesa;
import model.Pedido;
import view.AgregarPedidoDialog;
import view.PedidoView;
import view.UIStyle;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
        JDialog visor = new JDialog(view, "Árbol ABB de Pedidos (por ID)", true);
        JPanel root = UIStyle.raiz(24, 28, 24, 28);
        JPanel superior = new JPanel();
        superior.setOpaque(false);
        superior.setLayout(new BoxLayout(superior, BoxLayout.Y_AXIS));
        UIStyle.RoundedButton cerrar = UIStyle.secundario("Cerrar", null);
        cerrar.addActionListener(e -> visor.dispose());
        superior.add(UIStyle.encabezado(
                "Árbol ABB de Pedidos", "Pedidos pendientes ordenados por ID", cerrar));

        JTextArea salidaRecorrido = new JTextArea(3, 60);
        salidaRecorrido.setEditable(false);
        salidaRecorrido.setLineWrap(true);
        salidaRecorrido.setWrapStyleWord(true);
        salidaRecorrido.setFont(UIStyle.FONT_CAMPO);
        salidaRecorrido.setForeground(UIStyle.TINTA);
        salidaRecorrido.setBackground(UIStyle.SUPERFICIE);
        salidaRecorrido.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        UIStyle.RoundedButton btnPreOrden = UIStyle.secundario("Preorden", null);
        UIStyle.RoundedButton btnInOrden = UIStyle.secundario("Inorden", null);
        UIStyle.RoundedButton btnPostOrden = UIStyle.secundario("Postorden", null);
        btnPreOrden.addActionListener(e -> mostrarRecorrido(
                "Preorden", arbolPedidos.recorridoPreOrden(), salidaRecorrido));
        btnInOrden.addActionListener(e -> mostrarRecorrido(
                "Inorden", arbolPedidos.recorridoInOrden(), salidaRecorrido));
        btnPostOrden.addActionListener(e -> mostrarRecorrido(
                "Postorden", arbolPedidos.recorridoPostOrden(), salidaRecorrido));
        superior.add(UIStyle.barraAcciones(
                new JComponent[]{btnPreOrden, btnInOrden, btnPostOrden}, new JComponent[]{}));
        root.add(superior, BorderLayout.NORTH);

        JPanel lienzo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (arbolPedidos.estaVacio()) {
                    g2.setColor(UIStyle.TINTA_SUAVE);
                    g2.setFont(UIStyle.FONT_SUB);
                    g2.drawString("No hay pedidos pendientes.", 24, 32);
                } else {
                    dibujarNodo(g2, arbolPedidos.getRaiz(), getWidth() / 2, 45, getWidth() / 4);
                }
                g2.dispose();
            }

            private void dibujarNodo(Graphics2D g2, ArbolABBPedidos.NodoPedido nodo,
                                     int x, int y, int offset) {
                if (nodo == null) {
                    return;
                }

                int radio = 24;
                int separacionVertical = 75;
                if (nodo.izquierdo != null) {
                    int xIzquierdo = x - Math.max(offset, 35);
                    int yHijo = y + separacionVertical;
                    g2.setColor(new Color(120, 130, 140));
                    g2.drawLine(x, y, xIzquierdo, yHijo);
                    dibujarNodo(g2, nodo.izquierdo, xIzquierdo, yHijo, offset / 2);
                }
                if (nodo.derecho != null) {
                    int xDerecho = x + Math.max(offset, 35);
                    int yHijo = y + separacionVertical;
                    g2.setColor(new Color(120, 130, 140));
                    g2.drawLine(x, y, xDerecho, yHijo);
                    dibujarNodo(g2, nodo.derecho, xDerecho, yHijo, offset / 2);
                }

                g2.setColor(new Color(44, 62, 80));
                g2.fillOval(x - radio, y - radio, radio * 2, radio * 2);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                String texto = "#" + nodo.dato.getIdPedido();
                FontMetrics metricas = g2.getFontMetrics();
                g2.drawString(texto, x - metricas.stringWidth(texto) / 2, y + 4);

                g2.setColor(new Color(52, 73, 94));
                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                String mesa = "Mesa " + nodo.dato.getNumMesa();
                g2.drawString(mesa, x - g2.getFontMetrics().stringWidth(mesa) / 2, y + radio + 14);
            }
        };

        lienzo.setBackground(Color.WHITE);
        lienzo.setPreferredSize(new Dimension(820, 360));
        JScrollPane arbolScroll = new JScrollPane(lienzo);
        arbolScroll.setBorder(BorderFactory.createEmptyBorder());
        arbolScroll.getViewport().setBackground(UIStyle.SUPERFICIE);
        UIStyle.RoundedPanel tarjetaArbol = UIStyle.tarjeta();
        tarjetaArbol.add(arbolScroll, BorderLayout.CENTER);
        root.add(tarjetaArbol, BorderLayout.CENTER);

        JPanel panelSalida = new JPanel(new BorderLayout(0, 7));
        panelSalida.setOpaque(false);
        panelSalida.add(UIStyle.etiqueta("Recorrido seleccionado"), BorderLayout.NORTH);
        JScrollPane salidaScroll = new JScrollPane(salidaRecorrido);
        salidaScroll.setPreferredSize(new Dimension(820, 70));
        salidaScroll.setBorder(BorderFactory.createEmptyBorder());
        salidaScroll.getViewport().setBackground(UIStyle.SUPERFICIE);
        UIStyle.RoundedPanel tarjetaSalida = UIStyle.tarjeta();
        tarjetaSalida.add(salidaScroll, BorderLayout.CENTER);
        panelSalida.add(tarjetaSalida, BorderLayout.CENTER);
        panelSalida.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));
        root.add(panelSalida, BorderLayout.SOUTH);

        visor.setContentPane(root);
        visor.setSize(900, 650);
        visor.setMinimumSize(new Dimension(780, 560));
        visor.setLocationRelativeTo(view);
        visor.setVisible(true);
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

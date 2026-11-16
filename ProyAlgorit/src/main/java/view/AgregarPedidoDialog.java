package view;

import controller.PedidoController;
import dao.PlatoDAO;
import model.DetallePedido;
import model.Mesa;
import model.Pedido;
import model.Plato;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static view.UIStyle.*;

public class AgregarPedidoDialog extends JDialog {

    private static final String[] METODOS_PAGO = {
            "Tarjeta débito", "Tarjeta crédito", "Yape/Plin", "Efectivo"
    };

    private final PedidoController controller;
    private final Pedido pedidoExistente;
    private final List<DetallePedido> listaDetalles = new ArrayList<>();

    private final JLabel lblError = mensajeError();
    private JTextField txtCliente;
    private JComboBox<Mesa> cbMesas;
    private JComboBox<String> cbMetodoPago;
    private JComboBox<Plato> cbPlatos;
    private JSpinner spCantidad;
    private DefaultTableModel modeloDetalles;
    private JTable tablaDetalles;
    private JLabel lblTotal;

    public AgregarPedidoDialog(Frame owner, PedidoController controller, List<Mesa> mesas) {
        this(owner, controller, null, mesas);
    }

    public AgregarPedidoDialog(Frame owner, PedidoController controller, Pedido pedidoExistente) {
        this(owner, controller, pedidoExistente, List.of());
    }

    private AgregarPedidoDialog(Frame owner, PedidoController controller, Pedido pedidoExistente,
                                List<Mesa> mesas) {
        super(owner, pedidoExistente == null ? "Nuevo pedido"
                : "Agregar platos al pedido #" + pedidoExistente.getIdPedido(), true);
        this.controller = controller;
        this.pedidoExistente = pedidoExistente;
        initUI(mesas, owner);
    }

    private void initUI(List<Mesa> mesas, Frame owner) {
        JPanel root = panelDialogo(
                pedidoExistente == null ? "Nuevo pedido" : "Agregar platos",
                pedidoExistente == null
                        ? "Elige la mesa, los platos y el método de pago"
                        : "Añade platos al pedido #" + pedidoExistente.getIdPedido());

        JPanel cuerpo = new JPanel();
        cuerpo.setOpaque(false);
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));

        if (pedidoExistente == null) {
            cbMesas = new JComboBox<>();
            for (Mesa mesa : mesas) {
                cbMesas.addItem(mesa);
            }
            txtCliente = new JTextField();
            cbMetodoPago = new JComboBox<>(METODOS_PAGO);
            estilarCampo(cbMesas);
            estilarCampo(txtCliente);
            estilarCampo(cbMetodoPago);
            cbMesas.setPreferredSize(new Dimension(210, ALTO_CAMPO));
            txtCliente.setPreferredSize(new Dimension(270, ALTO_CAMPO));
            cbMetodoPago.setPreferredSize(new Dimension(240, ALTO_CAMPO));
            cuerpo.add(formulario(
                    new Object[]{"Mesa disponible", cbMesas, "Cliente / DNI", txtCliente},
                    new Object[]{"Método de pago", cbMetodoPago}));
            cuerpo.add(Box.createVerticalStrut(12));
        }

        cbPlatos = new JComboBox<>();
        for (Plato plato : new PlatoDAO().listarTodos()) {
            cbPlatos.addItem(plato);
        }
        spCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
        estilarCampo(cbPlatos);
        estilarCampo(spCantidad);
        cbPlatos.setPreferredSize(new Dimension(430, ALTO_CAMPO));
        spCantidad.setPreferredSize(new Dimension(120, ALTO_CAMPO));
        cuerpo.add(formulario(new Object[]{"Plato", cbPlatos, "Cantidad", spCantidad}));
        cuerpo.add(Box.createVerticalStrut(8));

        RoundedButton btnAgregarDetalle = secundario("Añadir a la lista", IconoVector.Tipo.AGREGAR);
        RoundedButton btnQuitarDetalle = discreto("Quitar seleccionado", IconoVector.Tipo.ELIMINAR);
        cuerpo.add(pieDialogo(btnAgregarDetalle, btnQuitarDetalle));
        cuerpo.add(Box.createVerticalStrut(10));

        modeloDetalles = new DefaultTableModel(
                new String[]{"ID Plato", "Plato", "Cantidad", "Precio unitario", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaDetalles = crearTabla(modeloDetalles, "Aún no se han añadido platos.");
        tablaDetalles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tablaDetalles);
        scroll.setPreferredSize(new Dimension(820, 190));
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(SUPERFICIE);
        RoundedPanel tarjeta = tarjeta();
        tarjeta.add(scroll, BorderLayout.CENTER);

        JPanel panelDetalles = new JPanel(new BorderLayout(0, 7));
        panelDetalles.setOpaque(false);
        panelDetalles.add(etiqueta("Platos del pedido"), BorderLayout.NORTH);
        panelDetalles.add(tarjeta, BorderLayout.CENTER);
        cuerpo.add(panelDetalles);

        lblTotal = new JLabel("Total de nuevos platos: S/. 0.00");
        lblTotal.setFont(FONT_ETIQUETA);
        lblTotal.setForeground(TINTA);
        lblTotal.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        cuerpo.add(lblTotal);
        cuerpo.add(lblError);
        root.add(cuerpo, BorderLayout.CENTER);

        RoundedButton btnCancelar = secundario("Cancelar", null);
        RoundedButton btnGuardar = primario(
                pedidoExistente == null ? "Guardar pedido" : "Agregar platos", null);
        root.add(pieDialogo(btnCancelar, btnGuardar), BorderLayout.SOUTH);
        setContentPane(root);

        btnAgregarDetalle.addActionListener(e -> agregarPlatoATabla());
        btnQuitarDetalle.addActionListener(e -> quitarFilaSeleccionada());
        btnCancelar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> guardarPedido());
        getRootPane().setDefaultButton(btnGuardar);
        ajustarDialogo(this, owner, 900);
    }

    private void agregarPlatoATabla() {
        Plato plato = (Plato) cbPlatos.getSelectedItem();
        if (plato == null) {
            lblError.setText("No hay platos disponibles para agregar.");
            return;
        }

        lblError.setText(" ");
        int cantidad = (int) spCantidad.getValue();
        for (DetallePedido detalle : listaDetalles) {
            if (detalle.getIdPlato() == plato.getIdPlato()) {
                detalle.setCantidad(detalle.getCantidad() + cantidad);
                refrescarTablaDetalles();
                return;
            }
        }

        listaDetalles.add(new DetallePedido(
                plato.getIdPlato(), plato.getNombre(), cantidad, plato.getPrecio()));
        refrescarTablaDetalles();
    }

    private void quitarFilaSeleccionada() {
        int fila = tablaDetalles.getSelectedRow();
        if (fila >= 0) {
            listaDetalles.remove(fila);
            refrescarTablaDetalles();
        }
    }

    private void refrescarTablaDetalles() {
        modeloDetalles.setRowCount(0);
        double total = 0;
        for (DetallePedido detalle : listaDetalles) {
            modeloDetalles.addRow(new Object[]{
                    detalle.getIdPlato(),
                    detalle.getNombrePlato(),
                    detalle.getCantidad(),
                    String.format("%.2f", detalle.getPrecioUnitario()),
                    String.format("%.2f", detalle.getSubtotal())
            });
            total += detalle.getSubtotal();
        }
        lblTotal.setText(String.format("Total de nuevos platos: S/. %.2f", total));
    }

    private void guardarPedido() {
        lblError.setText(" ");
        if (listaDetalles.isEmpty()) {
            lblError.setText("Agrega al menos un plato a la lista.");
            return;
        }

        if (pedidoExistente != null) {
            if (controller.agregarPlatosAlPedido(pedidoExistente.getIdPedido(), listaDetalles)) {
                dispose();
            }
            return;
        }

        if (txtCliente.getText().trim().isEmpty()) {
            lblError.setText("Ingresa el DNI del cliente.");
            txtCliente.requestFocusInWindow();
            return;
        }

        Mesa mesa = (Mesa) cbMesas.getSelectedItem();
        if (mesa == null) {
            lblError.setText("No hay mesas disponibles para el pedido.");
            return;
        }

        Pedido pedido = new Pedido();
        pedido.setNumMesa(mesa.getNumeroMesa());
        pedido.setDni(txtCliente.getText().trim());
        pedido.setEstado("PENDIENTE");
        pedido.setMetodoPago((String) cbMetodoPago.getSelectedItem());
        pedido.setDetalles(listaDetalles);
        pedido.setTotal(listaDetalles.stream().mapToDouble(DetallePedido::getSubtotal).sum());

        if (controller.registrarPedido(pedido)) {
            dispose();
        } else {
            lblError.setText("No se guardó el pedido. Verifica la disponibilidad de la mesa e inténtalo otra vez.");
        }
    }
}

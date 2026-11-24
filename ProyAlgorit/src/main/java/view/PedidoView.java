package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.IntConsumer;

import static view.UIStyle.*;

public class PedidoView extends JFrame {

    public final JButton btnAgregar = primario("Agregar", IconoVector.Tipo.AGREGAR);
    public final JButton btnAgregarPlatos = secundario("Agregar platos", IconoVector.Tipo.AGREGAR);
    public final JButton btnActualizar = secundario("Actualizar estado", IconoVector.Tipo.EDITAR);
    public final JButton btnEliminar = peligro("Eliminar", IconoVector.Tipo.ELIMINAR);
    public final JButton btnBoleta = secundario("Boleta PDF", IconoVector.Tipo.BOLETA);
    public final JButton btnBuscar = secundario("Buscar", IconoVector.Tipo.BUSCAR);
    public final JButton btnArbol = discreto("Árbol AVL", IconoVector.Tipo.ARBOL);
    public final JButton btnVerTodos = discreto("Todos los pedidos", IconoVector.Tipo.HISTORIAL);
    public final JButton btnVolver = botonVolver();

    public final DefaultTableModel modeloTabla;
    private final JTable tabla;

    public PedidoView() {
        setTitle("Gestión de Pedidos - C&R OrderManager");
        setSize(1200, 600);
        setMinimumSize(new Dimension(1200, 580));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = raiz();

        JPanel superior = new JPanel();
        superior.setOpaque(false);
        superior.setLayout(new BoxLayout(superior, BoxLayout.Y_AXIS));
        superior.add(encabezado("Pedidos", "Pedidos registrados y sus estados", btnVolver));
        superior.add(barraAcciones(
                new JComponent[]{btnAgregar, btnAgregarPlatos, btnActualizar, btnEliminar, btnBuscar, btnBoleta},
                new JComponent[]{btnVerTodos, btnArbol}));
        root.add(superior, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(
                new String[]{"ID", "Mesa", "Cliente / DNI", "Fecha y hora", "Estado", "Total", "Método de pago"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = crearTabla(modeloTabla, "Aún no hay pedidos. Usa «Agregar» para registrar el primero.");
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(180);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(SUPERFICIE);
        RoundedPanel card = tarjeta();
        card.add(scroll, BorderLayout.CENTER);
        root.add(card, BorderLayout.CENTER);

        setContentPane(root);
        activarEscalado(this);
    }

    public int getFilaSeleccionada() {
        return tabla.getSelectedRow();
    }

    public void addPedidoDoubleClickListener(IntConsumer listener) {
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(event)) {
                    int fila = tabla.rowAtPoint(event.getPoint());
                    if (fila >= 0) {
                        int filaModelo = tabla.convertRowIndexToModel(fila);
                        listener.accept((int) modeloTabla.getValueAt(filaModelo, 0));
                    }
                }
            }
        });
    }
}

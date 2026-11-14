package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import static view.UIStyle.*;

public class PlatoView extends JFrame {

    public final JButton btnAgregar    = primario("Agregar", IconoVector.Tipo.AGREGAR);
    public final JButton btnActualizar = secundario("Actualizar", IconoVector.Tipo.EDITAR);
    public final JButton btnEliminar   = peligro("Eliminar", IconoVector.Tipo.ELIMINAR);
    public final JButton btnDeshacer   = discreto("Deshacer", IconoVector.Tipo.DESHACER);
    public final JButton btnHistorial  = discreto("Historial", IconoVector.Tipo.HISTORIAL);
    public final JButton btnArbol      = discreto("Árbol", IconoVector.Tipo.ARBOL);
    public final JButton btnABB        = discreto("ABB", IconoVector.Tipo.ARBOL);
    public final JButton btnVolver     = botonVolver();

    public final DefaultTableModel modeloTabla;
    public final JTable tabla;

    public PlatoView() {
        setTitle("Gestión de Platos - C&R OrderManager");
        setSize(1060, 560);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(980, 480));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = raiz();

        btnDeshacer.setToolTipText("Deshace la última operación (LIFO)");
        btnHistorial.setToolTipText("Muestra el historial de operaciones en la pila");
        btnABB.setToolTipText("Árbol Binario de Búsqueda de platos por nombre");
        btnArbol.setToolTipText("Muestra la carta como árbol binario y sus recorridos");

        JPanel superior = new JPanel();
        superior.setOpaque(false);
        superior.setLayout(new BoxLayout(superior, BoxLayout.Y_AXIS));
        superior.add(encabezado("Platos", "Carta, precios y categorías del restaurante", btnVolver));
        superior.add(barraAcciones(
                new JComponent[]{btnAgregar, btnActualizar, btnEliminar},
                new JComponent[]{btnDeshacer, btnHistorial, btnArbol, btnABB}));
        root.add(superior, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(
                new String[]{"ID", "Nombre", "Precio", "Categoría"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = crearTabla(modeloTabla, "Aún no hay platos. Usa «Agregar» para crear la carta.");
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(240);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(140);

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
}

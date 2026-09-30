package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import static view.UIStyle.*;

public class PlatoView extends JFrame {

    public final JButton btnAgregar    = button("AGREGAR", VERDE_OLIVA, VERDE_HOVER);
    public final JButton btnActualizar = button("ACTUALIZAR", NARANJA, NARANJA_HOVER);
    public final JButton btnEliminar   = button("ELIMINAR", TERRACOTA, TERRACOTA_HOVER);
    public final JButton btnDeshacer   = button("DESHACER", MOSTAZA, MOSTAZA_HOVER);
    public final JButton btnHistorial  = button("HISTORIAL", TAUPE, TAUPE_HOVER);

    public final DefaultTableModel modeloTabla;
    public final JTable tabla;

    public PlatoView() {
        setTitle("Gestión de Platos - C&R OrderManager");
        setSize(760, 440);
        setMinimumSize(new Dimension(640, 380));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(FONDO_EXTERNO);
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        RoundedPanel card = new RoundedPanel(22, CREMA);
        card.setLayout(new BorderLayout(0, 12));
        card.add(headerBar("Gestión de Platos", "Carta, precios y categorías del restaurante"), BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(
                new String[]{"ID", "Nombre", "Precio", "Categoría"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // solo se edita con los botones
            }
        };

        tabla = new JTable(modeloTabla);
        styleTable(tabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(220);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(90);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(120);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));
        scroll.getViewport().setBackground(Color.WHITE);
        card.add(scroll, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 16));
        panelBotones.setBackground(CREMA);

        for (JButton b : new JButton[]{btnAgregar, btnActualizar, btnEliminar, btnDeshacer, btnHistorial}) {
            b.setPreferredSize(new Dimension(120, 40));
            panelBotones.add(b);
        }

        btnDeshacer.setToolTipText("Deshace la última operación (LIFO)");
        btnHistorial.setToolTipText("Muestra el historial de operaciones en la pila");

        card.add(panelBotones, BorderLayout.SOUTH);
        root.add(card, BorderLayout.CENTER);
        setContentPane(root);
    }

    public int getFilaSeleccionada() {
        return tabla.getSelectedRow();
    }
}
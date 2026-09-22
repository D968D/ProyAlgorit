package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PlatoView extends JFrame {

    public final JButton btnAgregar    = new JButton("Agregar");
    public final JButton btnActualizar = new JButton("Actualizar");
    public final JButton btnEliminar   = new JButton("Eliminar");

    public final DefaultTableModel modeloTabla;
    public final JTable tabla;

    public PlatoView() {
        setTitle("GESTIÓN DE PLATOS - C&R OrderManager");
        setSize(620, 380);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        modeloTabla = new DefaultTableModel(
                new String[]{"ID", "Nombre", "Precio", "Categoría"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // solo se edita con los botones
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(26);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(220);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(90);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(120);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.add(btnAgregar);
        btnAgregar.setBackground(new Color(0,255,0));
        btnAgregar.setForeground(new Color(0,0,0));

        panelBotones.add(btnActualizar);
        btnActualizar.setBackground(new Color(178,255,255));
        btnActualizar.setForeground(new Color(0,0,0));

        panelBotones.add(btnEliminar);
        btnEliminar.setBackground(new Color(255,0,0));
        btnEliminar.setForeground(new Color(0,0,0));

        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    public int getFilaSeleccionada() {
        return tabla.getSelectedRow();
    }
}

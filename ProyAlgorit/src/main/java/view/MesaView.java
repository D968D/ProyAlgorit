package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;


public class MesaView extends JFrame {

    public final JButton btnActualizar = new JButton("Actualizar");
    public final DefaultTableModel modeloTabla;
    public final JTable tabla;

    public MesaView() {
        setTitle("GESTION DE MESAS - C&R OrderManager");
        setSize(500, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        modeloTabla = new DefaultTableModel(new String[]{"Numero mesa", "Capacidad", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(24);

        JPanel panelBotones = new JPanel();
        panelBotones.add(btnActualizar);

        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    public int getFilaSeleccionada() {
        return tabla.getSelectedRow();
    }
}

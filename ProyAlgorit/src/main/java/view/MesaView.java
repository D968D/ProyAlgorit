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
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        modeloTabla = new DefaultTableModel(new String[]{"Numero mesa", "Capacidad", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // la tabla es solo de lectura, se edita con el boton Actualizar
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(24);

        JPanel panelBotones = new JPanel();
        panelBotones.add(btnActualizar);
        btnActualizar.setBackground(new Color(178,255,255));
        btnActualizar.setForeground(new Color(0,0,0));

        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    public int getFilaSeleccionada() {
        return tabla.getSelectedRow();
    }
}

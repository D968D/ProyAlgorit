package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import static view.UIStyle.*;

public class MesaView extends JFrame {

    public final JButton btnActualizar = button("ACTUALIZAR", NARANJA, NARANJA_HOVER);
    public final DefaultTableModel modeloTabla;
    public final JTable tabla;

    public MesaView() {
        setTitle("Gestión de Mesas - C&R OrderManager");
        setSize(560, 420);
        setMinimumSize(new Dimension(480, 360));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(FONDO_EXTERNO);
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        RoundedPanel card = new RoundedPanel(22, CREMA);
        card.setLayout(new BorderLayout(0, 12));
        card.add(headerBar("Gestión de Mesas", "Disponibilidad y capacidad del salón"), BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(new String[]{"N° Mesa", "Capacidad", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // la tabla es solo de lectura, se edita con el botón Actualizar
            }
        };
        tabla = new JTable(modeloTabla);
        styleTable(tabla);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));
        scroll.getViewport().setBackground(Color.WHITE);
        card.add(scroll, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 16));
        panelBotones.setBackground(CREMA);
        btnActualizar.setPreferredSize(new Dimension(170, 40));
        panelBotones.add(btnActualizar);
        card.add(panelBotones, BorderLayout.SOUTH);

        root.add(card, BorderLayout.CENTER);
        setContentPane(root);
        activarEscalado(this);
    }

    public int getFilaSeleccionada() {
        return tabla.getSelectedRow();
    }
}
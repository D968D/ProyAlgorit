package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import view.UIStyle.RoundedPanel;

import java.awt.*;

import static view.UIStyle.*;

public class MesaView extends JFrame {

    public final JButton btnAgregar    = button("AGREGAR", VERDE_OLIVA, VERDE_HOVER);
    public final JButton btnActualizar = button("ACTUALIZAR", NARANJA, NARANJA_HOVER);
    public final JButton btnEliminar   = button("ELIMINAR", TERRACOTA, TERRACOTA_HOVER);
    public final JButton btnVolver     = button("X", TERRACOTA, TERRACOTA_HOVER);

    public final DefaultTableModel modeloTabla;
    public final JTable tabla;

    public MesaView() {
        setTitle("Gestión de Mesas - C&R OrderManager");
        setSize(560,420);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(480, 360));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(FONDO_EXTERNO);
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        RoundedPanel card = new RoundedPanel(22, CREMA);
        card.setLayout(new BorderLayout(0, 12));
        JPanel headerOriginal = headerBar("Gestión de Mesas", "Disponibilidad y capacidad del salón");
        
        JPanel headerWrapper = new JPanel(new BorderLayout());
        headerWrapper.setBackground(headerOriginal.getBackground()); // Hereda el verde oscuro automáticamente
        
        headerWrapper.add(headerOriginal, BorderLayout.WEST);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10,10 ));
        rightPanel.setOpaque(false);
        
        btnVolver.setPreferredSize(new Dimension(60, 60));
        rightPanel.add(btnVolver);
        
        headerWrapper.add(rightPanel, BorderLayout.EAST);
        
        card.add(headerWrapper, BorderLayout.NORTH);

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

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 16));
        panelBotones.setBackground(CREMA);
        for (JButton b : new JButton[]{btnAgregar, btnActualizar, btnEliminar}) {
            b.setPreferredSize(new Dimension(120, 40));
            panelBotones.add(b);
        }
        
        card.add(panelBotones, BorderLayout.SOUTH);

        root.add(card, BorderLayout.CENTER);
        setContentPane(root);
        activarEscalado(this);
    }

    public int getFilaSeleccionada() {
        return tabla.getSelectedRow();
    }
}
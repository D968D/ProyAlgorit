
package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import static view.UIStyle.*;

public class ReservaView extends JFrame {

    public final JButton btnAgregar =
            button("AGREGAR", IconoVector.Tipo.AGREGAR,
                    VERDE_OLIVA, VERDE_HOVER);

    public final JButton btnEliminar =
            button("ELIMINAR", IconoVector.Tipo.ELIMINAR,
                    TERRACOTA, TERRACOTA_HOVER);

    public final JButton btnBuscar =
            button("BUSCAR", IconoVector.Tipo.BUSCAR,
                    NARANJA, NARANJA_HOVER);

    public final JButton btnOrdenar =
            button("ORDENAR", IconoVector.Tipo.ORDENAR,
                    MOSTAZA, MOSTAZA_HOVER);

    public final JButton btnHistorial =
            button("HISTORIAL", IconoVector.Tipo.HISTORIAL,
                    TAUPE, TAUPE_HOVER);

    public final JButton btnVerCola =
            button("VER COLA", IconoVector.Tipo.HISTORIAL,
                    TAUPE, TAUPE_HOVER);

    public final JButton btnAtenderCola =
            button("ATENDER SIGUIENTE", IconoVector.Tipo.AGREGAR,
                    VERDE_OLIVA, VERDE_HOVER);

    public final JButton btnAVL =
            button("ÁRBOL AVL", ROYAL_BLUE, ROYAL_BLUE_HOVER);

    public final JButton btnVolver =
            button("X", TERRACOTA, TERRACOTA_HOVER);

    public final DefaultTableModel modeloTabla;
    private final JTable tabla;

    public ReservaView() {
        setTitle("Gestión de Reservas - C&R OrderManager");
        setSize(1000, 560);
        setMinimumSize(new Dimension(900, 500));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(FONDO_EXTERNO);
        root.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        RoundedPanel card = new RoundedPanel(22, CREMA);
        card.setLayout(new BorderLayout(0, 12));

        JPanel headerOriginal = headerBar(
                "Gestión de Reservas",
                "Reservas del salón, cola de atención por fecha y hora"
        );

        JPanel headerWrapper = new JPanel(new BorderLayout());
        headerWrapper.setBackground(
                headerOriginal.getBackground()
        );

        headerWrapper.add(headerOriginal, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 10)
        );
        rightPanel.setOpaque(false);

        btnVolver.setPreferredSize(new Dimension(60, 60));
        rightPanel.add(btnVolver);
        headerWrapper.add(rightPanel, BorderLayout.EAST);

        card.add(headerWrapper, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(
                new String[]{
                        "ID", "Cliente", "Fecha", "Hora",
                        "Personas", "Mesa", "Estado"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        styleTable(tabla);
        tabla.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
        tabla.getColumnModel().getColumn(0).setPreferredWidth(45);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(170);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(
                BorderFactory.createEmptyBorder(0, 24, 0, 24)
        );
        scroll.setBackground(CREMA);
        scroll.getViewport().setBackground(Color.WHITE);

        card.add(scroll, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(
                new GridLayout(2, 4, 10, 10)
        );
        panelBotones.setBackground(CREMA);
        panelBotones.setBorder(
                BorderFactory.createEmptyBorder(5, 20, 5, 20)
        );

        for (JButton b : new JButton[]{
                btnAgregar, btnEliminar, btnBuscar,
                btnOrdenar, btnHistorial,
                btnVerCola, btnAtenderCola, btnAVL
        }) {
            b.setPreferredSize(new Dimension(150, 40));
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

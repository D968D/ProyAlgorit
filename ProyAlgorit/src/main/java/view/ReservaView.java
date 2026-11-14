package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import static view.UIStyle.*;

public class ReservaView extends JFrame {

    public final JButton btnAgregar      = primario("Agregar", IconoVector.Tipo.AGREGAR);
    public final JButton btnEliminar     = peligro("Eliminar", IconoVector.Tipo.ELIMINAR);
    public final JButton btnBuscar       = secundario("Buscar", IconoVector.Tipo.BUSCAR);
    public final JButton btnOrdenar      = secundario("Ordenar", IconoVector.Tipo.ORDENAR);
    public final JButton btnHistorial    = discreto("Historial", IconoVector.Tipo.HISTORIAL);
    public final JButton btnVerCola      = discreto("Ver cola", IconoVector.Tipo.COLA);
    public final JButton btnAtenderCola  = secundario("Atender siguiente", IconoVector.Tipo.ATENDER);
    public final JButton btnAVL          = discreto("Árbol AVL", IconoVector.Tipo.ARBOL);
    public final JButton btnVolver       = botonVolver();

    public final DefaultTableModel modeloTabla;
    private final JTable tabla;

    public ReservaView() {
        setTitle("Gestión de Reservas - C&R OrderManager");
        setSize(1180, 600);
        setMinimumSize(new Dimension(1100, 520));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = raiz();

        JPanel superior = new JPanel();
        superior.setOpaque(false);
        superior.setLayout(new BoxLayout(superior, BoxLayout.Y_AXIS));
        superior.add(encabezado("Reservas", "Reservas del salón, cola de atención por fecha y hora", btnVolver));
        superior.add(barraAcciones(
                new JComponent[]{btnAgregar, btnEliminar, btnBuscar, btnOrdenar, btnAtenderCola},
                new JComponent[]{btnVerCola, btnHistorial, btnAVL}));
        root.add(superior, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(
                new String[]{"ID", "Cliente", "Fecha", "Hora", "Personas", "Mesa", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = crearTabla(modeloTabla, "Aún no hay reservas. Usa «Agregar» para registrar la primera.");
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(200);

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

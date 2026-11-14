package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import static view.UIStyle.*;

public class MesaView extends JFrame {

    public final JButton btnAgregar    = primario("Agregar", IconoVector.Tipo.AGREGAR);
    public final JButton btnActualizar = secundario("Actualizar", IconoVector.Tipo.EDITAR);
    public final JButton btnEliminar   = peligro("Eliminar", IconoVector.Tipo.ELIMINAR);
    public final JButton btnOcupacion  = discreto("Ocupación", IconoVector.Tipo.OCUPACION);
    public final JButton btnVolver     = botonVolver();

    public final DefaultTableModel modeloTabla;
    public final JTable tabla;

    public MesaView() {
        setTitle("Gestión de Mesas - C&R OrderManager");
        setSize(820, 540);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(720, 460));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = raiz();

        JPanel superior = new JPanel();
        superior.setOpaque(false);
        superior.setLayout(new BoxLayout(superior, BoxLayout.Y_AXIS));
        superior.add(encabezado("Mesas", "Disponibilidad y capacidad del salón", btnVolver));
        superior.add(barraAcciones(
                new JComponent[]{btnAgregar, btnActualizar, btnEliminar},
                new JComponent[]{btnOcupacion}));
        root.add(superior, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(new String[]{"N° Mesa", "Capacidad", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // la tabla es solo de lectura, se edita con el botón Actualizar
            }
        };
        tabla = crearTabla(modeloTabla, "Aún no hay mesas. Usa «Agregar» para registrar la primera.");
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

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

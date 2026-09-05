package view;

import controller.MesaController;
import model.Mesa;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FrmMesas extends JFrame {

    private JTable tabla;
    private DefaultTableModel modelo;
    private MesaController cont = new MesaController();
    private int filaSeleccionada = -1;

    public FrmMesas() {
        setTitle("Gestión de Mesas - Cordon y la Rosa");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Tabla
        modelo = new DefaultTableModel(new Object[]{"Numero mesa", "Capacidad", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(28);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Resaltar fila seleccionada
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                filaSeleccionada = tabla.getSelectedRow();
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);

        // Botón Actualizar
        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.setPreferredSize(new Dimension(120, 35));
        btnActualizar.addActionListener(e -> abrirDialogoEstado());

        JPanel panelSur = new JPanel();
        panelSur.add(btnActualizar);

        // Botón para agregar mesa (opcional)
        JButton btnNueva = new JButton("Nueva Mesa");
        btnNueva.addActionListener(e -> agregarMesa());

        JPanel panelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelNorte.add(btnNueva);

        setLayout(new BorderLayout(10, 10));
        add(panelNorte, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(panelSur, BorderLayout.SOUTH);

        cargarTabla();
    }

    private void cargarTabla() {
        modelo.setRowCount(0);
        List<Mesa> lista = cont.listarTodas();
        for (Mesa m : lista) {
            modelo.addRow(new Object[]{
                m.getNumMesa(),
                m.getCapacidad(),
                m.getEstado()
            });
        }
    }

    private void abrirDialogoEstado() {
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una mesa primero");
            return;
        }

        int numero = Integer.parseInt(modelo.getValueAt(filaSeleccionada, 0).toString());
        Mesa mesa = cont.buscarPorNumero(numero);

        // Diálogo estilo de la imagen
        JDialog dialogo = new JDialog(this, "Cambiar Estado", true);
        dialogo.setSize(280, 150);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 30));

        JButton btnLibre = new JButton("LIBRE");
        JButton btnOcupado = new JButton("OCUPADO");

        btnLibre.setPreferredSize(new Dimension(100, 40));
        btnOcupado.setPreferredSize(new Dimension(100, 40));

        btnLibre.addActionListener(e -> {
            mesa.setEstado("Libre");
            if (cont.actualizar(mesa)) {
                cargarTabla();
                dialogo.dispose();
            }
        });

        btnOcupado.addActionListener(e -> {
            mesa.setEstado("Ocupada");
            if (cont.actualizar(mesa)) {
                cargarTabla();
                dialogo.dispose();
            }
        });

        dialogo.add(btnLibre);
        dialogo.add(btnOcupado);
        dialogo.setVisible(true);
    }

    private void agregarMesa() {
        JTextField txtNumero = new JTextField(5);
        JTextField txtCapacidad = new JTextField(5);

        JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));
        panel.add(new JLabel("Número de mesa:"));
        panel.add(txtNumero);
        panel.add(new JLabel("Capacidad:"));
        panel.add(txtCapacidad);

        int op = JOptionPane.showConfirmDialog(this, panel, "Nueva Mesa", JOptionPane.OK_CANCEL_OPTION);
        if (op == JOptionPane.OK_OPTION) {
            try {
                Mesa m = new Mesa(
                    Integer.parseInt(txtNumero.getText()),
                    Integer.parseInt(txtCapacidad.getText()),
                    "Libre"
                );
                if (cont.insertar(m)) {
                    JOptionPane.showMessageDialog(this, "Mesa registrada");
                    cargarTabla();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingrese valores numéricos válidos");
            }
        }
    }
}

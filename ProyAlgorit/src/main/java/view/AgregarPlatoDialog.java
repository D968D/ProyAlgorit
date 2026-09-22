package view;

import javax.swing.*;
import java.awt.*;

public class AgregarPlatoDialog extends JDialog {

    private final JTextField txtNombre    = new JTextField();
    private final JTextField txtPrecio    = new JTextField();
    private final JTextField txtCategoria = new JTextField();
    private final JButton btnAgregar      = new JButton("agregar");

    private boolean confirmado = false;

    public AgregarPlatoDialog(JFrame parent) {
        super(parent, "Agregar plato", true);
        setSize(360, 240);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));

        // Tamaño fijo de los campos para que se vean y se pueda escribir
        Dimension tamanoCampo = new Dimension(200, 28);
        txtNombre.setPreferredSize(tamanoCampo);
        txtPrecio.setPreferredSize(tamanoCampo);
        txtCategoria.setPreferredSize(tamanoCampo);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Fila nombre
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        form.add(new JLabel("nombre"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        form.add(txtNombre, gbc);

        // Fila precio
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        form.add(new JLabel("precio"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        form.add(txtPrecio, gbc);

        // Fila categoría
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        form.add(new JLabel("categoría"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        form.add(txtCategoria, gbc);

        JPanel panelBoton = new JPanel();
        panelBoton.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));
        panelBoton.add(btnAgregar);

        add(form, BorderLayout.CENTER);
        add(panelBoton, BorderLayout.SOUTH);

        btnAgregar.addActionListener(e -> {
            if (validar()) {
                confirmado = true;
                dispose();
            }
        });

        // Enfocar el primer campo al abrir
        SwingUtilities.invokeLater(() -> txtNombre.requestFocusInWindow());
    }

    private boolean validar() {
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el nombre del plato.");
            txtNombre.requestFocusInWindow();
            return false;
        }
        try {
            double p = Double.parseDouble(txtPrecio.getText().trim().replace(',', '.'));
            if (p <= 0) {
                JOptionPane.showMessageDialog(this, "El precio debe ser mayor a 0.");
                txtPrecio.requestFocusInWindow();
                return false;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un precio numérico válido (ej: 25.50).");
            txtPrecio.requestFocusInWindow();
            return false;
        }
        if (txtCategoria.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la categoría.");
            txtCategoria.requestFocusInWindow();
            return false;
        }
        return true;
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public String getNombre() {
        return txtNombre.getText().trim();
    }

    public double getPrecio() {
        return Double.parseDouble(txtPrecio.getText().trim().replace(',', '.'));
    }

    public String getCategoria() {
        return txtCategoria.getText().trim();
    }
}

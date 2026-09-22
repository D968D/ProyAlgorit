package view;

import javax.swing.*;
import java.awt.*;


public class ActualizarPlatoDialog extends JDialog {

    private final JTextField txtPrecio = new JTextField();
    private final JButton btnActualizar = new JButton("Actualizar");

    private boolean confirmado = false;

    public ActualizarPlatoDialog(JFrame parent, double precioActual) {
        super(parent, "Actualizar precio", true);
        setSize(300, 160);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));

        txtPrecio.setPreferredSize(new Dimension(160, 28));
        txtPrecio.setText(String.valueOf(precioActual));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(25, 20, 10, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        form.add(new JLabel("precio"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        form.add(txtPrecio, gbc);

        JPanel panelBoton = new JPanel();
        panelBoton.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));
        panelBoton.add(btnActualizar);

        add(form, BorderLayout.CENTER);
        add(panelBoton, BorderLayout.SOUTH);

        btnActualizar.addActionListener(e -> {
            if (validar()) {
                confirmado = true;
                dispose();
            }
        });

        SwingUtilities.invokeLater(() -> {
            txtPrecio.requestFocusInWindow();
            txtPrecio.selectAll();
        });
    }

    private boolean validar() {
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
        return true;
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public double getNuevoPrecio() {
        return Double.parseDouble(txtPrecio.getText().trim().replace(',', '.'));
    }
}

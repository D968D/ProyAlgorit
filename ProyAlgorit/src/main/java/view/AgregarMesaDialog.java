package view;

import javax.swing.*;

import estructura.ArregloMesas;

import static view.UIStyle.CREMA;
import static view.UIStyle.VERDE_HOVER;
import static view.UIStyle.VERDE_OLIVA;
import static view.UIStyle.button;

import java.awt.*;

public class AgregarMesaDialog extends JDialog{
    
    private final JTextField txtNumMesa    = new JTextField();
    private final JTextField txtCapacidad    = new JTextField();
    private final JButton btnAgregar      = button("AGREGAR", VERDE_OLIVA, VERDE_HOVER);
    private final ArregloMesas mesas;

    private boolean confirmado = false;

    public AgregarMesaDialog(JFrame parent, ArregloMesas mesas) {
        super(parent, "Agregar mesa", true);
        this.mesas=mesas;
        setSize(360, 240);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(CREMA);
        // Tamaño fijo de los campos para que se vean y se pueda escribir
        Dimension tamanoCampo = new Dimension(200, 28);
        txtNumMesa.setPreferredSize(tamanoCampo);
        txtCapacidad.setPreferredSize(tamanoCampo);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        form.setBackground(CREMA);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Fila numero de mesa
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        form.add(new JLabel("Número De Mesa:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        form.add(txtNumMesa, gbc);

        // Fila capacidad
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        form.add(new JLabel("Capacidad:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        form.add(txtCapacidad, gbc);

        JPanel panelBoton = new JPanel();
        panelBoton.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));
        panelBoton.add(btnAgregar);
        panelBoton.setBackground(CREMA);

        add(form, BorderLayout.CENTER);
        add(panelBoton, BorderLayout.SOUTH);

        btnAgregar.addActionListener(e -> {
            if (validar()) {
                confirmado = true;
                dispose();
            }
        });
        // Enfocar el primer campo al abrir
        SwingUtilities.invokeLater(() -> txtNumMesa.requestFocusInWindow());
    }

    private boolean validar() {
        try {
            int p = Integer.parseInt(txtNumMesa.getText().trim());
            if (p <= 0) {
                JOptionPane.showMessageDialog(this, "El número de la mesa debe ser mayor a 0.");
                txtNumMesa.requestFocusInWindow();
                return false;
            }
            if (mesas.existe(p)) {
                JOptionPane.showMessageDialog(this, "La mesa Nº " + p + " ya existe. Elija otro número.");
                txtNumMesa.requestFocusInWindow();
                return false;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un número entero válido.");
            txtNumMesa.requestFocusInWindow();
            return false;
        }
        try {
            int p = Integer.parseInt(txtCapacidad.getText().trim());
            if (p <= 0) {
                JOptionPane.showMessageDialog(this, "La capacidad de la mesa debe ser mayor a 0.");
                txtCapacidad.requestFocusInWindow();
                return false;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un número entero válido.");
            txtCapacidad.requestFocusInWindow();
            return false;
        }
        return true;
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public int getNumMesa() {
        return Integer.parseInt(txtNumMesa.getText().trim());
    }

    public int getCapacidad() {
        return Integer.parseInt(txtCapacidad.getText().trim());
    }
}

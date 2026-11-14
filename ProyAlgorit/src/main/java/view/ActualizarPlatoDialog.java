package view;

import javax.swing.*;
import java.awt.*;

import static view.UIStyle.*;

public class ActualizarPlatoDialog extends JDialog {

    private final JTextField txtPrecio = new JTextField();
    private final JLabel lblError = mensajeError();

    private boolean confirmado = false;

    public ActualizarPlatoDialog(JFrame parent, double precioActual) {
        super(parent, "Actualizar precio", true);

        estilarCampo(txtPrecio);
        txtPrecio.setText(String.valueOf(precioActual));

        JPanel root = panelDialogo("Actualizar precio", "Escribe el nuevo precio del plato");
        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setOpaque(false);
        centro.add(formulario(new Object[]{"Precio (S/)", txtPrecio}), BorderLayout.CENTER);
        centro.add(lblError, BorderLayout.SOUTH);
        root.add(centro, BorderLayout.CENTER);

        RoundedButton btnCancelar = secundario("Cancelar", null);
        RoundedButton btnActualizar = primario("Actualizar", null);
        root.add(pieDialogo(btnCancelar, btnActualizar), BorderLayout.SOUTH);
        setContentPane(root);

        btnCancelar.addActionListener(e -> dispose());
        btnActualizar.addActionListener(e -> {
            if (validar()) {
                confirmado = true;
                dispose();
            }
        });
        getRootPane().setDefaultButton(btnActualizar);
        ajustarDialogo(this, parent, 380);

        SwingUtilities.invokeLater(() -> {
            txtPrecio.requestFocusInWindow();
            txtPrecio.selectAll();
        });
    }

    private boolean validar() {
        lblError.setText(" ");
        try {
            double p = Double.parseDouble(txtPrecio.getText().trim().replace(',', '.'));
            if (p <= 0) {
                lblError.setText("El precio debe ser mayor a 0.");
                txtPrecio.requestFocusInWindow();
                return false;
            }
        } catch (NumberFormatException ex) {
            lblError.setText("Ingresa un precio numérico válido (ej: 25.50).");
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

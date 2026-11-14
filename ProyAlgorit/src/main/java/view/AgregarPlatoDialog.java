package view;

import javax.swing.*;
import java.awt.*;

import static view.UIStyle.*;

public class AgregarPlatoDialog extends JDialog {

    private final JTextField txtNombre    = new JTextField();
    private final JTextField txtPrecio    = new JTextField();
    private final JTextField txtCategoria = new JTextField();
    private final JLabel lblError = mensajeError();

    private boolean confirmado = false;

    public AgregarPlatoDialog(JFrame parent) {
        super(parent, "Agregar plato", true);

        estilarCampo(txtNombre);
        estilarCampo(txtPrecio);
        estilarCampo(txtCategoria);

        JPanel root = panelDialogo("Nuevo plato", "Se añadirá a la carta");
        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setOpaque(false);
        centro.add(formulario(
                new Object[]{"Nombre", txtNombre},
                new Object[]{"Precio (S/)", txtPrecio, "Categoría", txtCategoria}), BorderLayout.CENTER);
        centro.add(lblError, BorderLayout.SOUTH);
        root.add(centro, BorderLayout.CENTER);

        RoundedButton btnCancelar = secundario("Cancelar", null);
        RoundedButton btnAgregar = primario("Agregar", null);
        root.add(pieDialogo(btnCancelar, btnAgregar), BorderLayout.SOUTH);
        setContentPane(root);

        btnCancelar.addActionListener(e -> dispose());
        btnAgregar.addActionListener(e -> {
            if (validar()) {
                confirmado = true;
                dispose();
            }
        });
        getRootPane().setDefaultButton(btnAgregar);
        ajustarDialogo(this, parent, 460);
        SwingUtilities.invokeLater(() -> txtNombre.requestFocusInWindow());
    }

    private void error(String mensaje, JComponent foco) {
        lblError.setText(mensaje);
        foco.requestFocusInWindow();
    }

    private boolean validar() {
        lblError.setText(" ");
        if (txtNombre.getText().trim().isEmpty()) {
            error("Ingresa el nombre del plato.", txtNombre);
            return false;
        }
        try {
            double p = Double.parseDouble(txtPrecio.getText().trim().replace(',', '.'));
            if (p <= 0) {
                error("El precio debe ser mayor a 0.", txtPrecio);
                return false;
            }
        } catch (NumberFormatException ex) {
            error("Ingresa un precio numérico válido (ej: 25.50).", txtPrecio);
            return false;
        }
        if (txtCategoria.getText().trim().isEmpty()) {
            error("Ingresa la categoría.", txtCategoria);
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

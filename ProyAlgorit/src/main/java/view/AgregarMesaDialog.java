package view;

import estructura.ArregloMesas;

import javax.swing.*;
import java.awt.*;

import static view.UIStyle.*;

public class AgregarMesaDialog extends JDialog {

    private final JTextField txtNumMesa  = new JTextField();
    private final JTextField txtCapacidad = new JTextField();
    private final JLabel lblError = mensajeError();
    private final ArregloMesas mesas;

    private boolean confirmado = false;

    public AgregarMesaDialog(JFrame parent, ArregloMesas mesas) {
        super(parent, "Agregar mesa", true);
        this.mesas = mesas;

        estilarCampo(txtNumMesa);
        estilarCampo(txtCapacidad);

        JPanel root = panelDialogo("Nueva mesa", "Indica el número y cuántas personas caben");
        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setOpaque(false);
        centro.add(formulario(
                new Object[]{"Número de mesa", txtNumMesa, "Capacidad", txtCapacidad}), BorderLayout.CENTER);
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
        ajustarDialogo(this, parent, 420);
        SwingUtilities.invokeLater(() -> txtNumMesa.requestFocusInWindow());
    }

    private void error(String mensaje, JComponent foco) {
        lblError.setText(mensaje);
        foco.requestFocusInWindow();
    }

    private boolean validar() {
        lblError.setText(" ");
        try {
            int p = Integer.parseInt(txtNumMesa.getText().trim());
            if (p <= 0) {
                error("El número de la mesa debe ser mayor a 0.", txtNumMesa);
                return false;
            }
            if (mesas.existe(p)) {
                error("La mesa Nº " + p + " ya existe. Elige otro número.", txtNumMesa);
                return false;
            }
        } catch (NumberFormatException ex) {
            error("Ingresa un número de mesa entero.", txtNumMesa);
            return false;
        }
        try {
            int p = Integer.parseInt(txtCapacidad.getText().trim());
            if (p <= 0) {
                error("La capacidad debe ser mayor a 0.", txtCapacidad);
                return false;
            }
        } catch (NumberFormatException ex) {
            error("Ingresa una capacidad entera.", txtCapacidad);
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

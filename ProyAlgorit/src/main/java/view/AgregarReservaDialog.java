package view;

import model.Mesa;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import static view.UIStyle.*;

/** Formulario único para registrar una reserva. */
public class AgregarReservaDialog extends JDialog {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("d/M/yyyy");
    private static final DateTimeFormatter HORA  = DateTimeFormatter.ofPattern("HH:mm");

    private final JTextField txtCliente = new JTextField();
    private final JTextField txtFecha   = new JTextField(LocalDate.now().format(FECHA));
    private final JTextField txtHora    = new JTextField("20:00");
    private final JSpinner spPersonas   = new JSpinner(new SpinnerNumberModel(2, 1, 50, 1));
    private final JComboBox<Mesa> cbMesa   = new JComboBox<>();

    private boolean confirmado = false;

    private final JLabel lblError = mensajeError();

    public AgregarReservaDialog(JFrame parent, List<Mesa> mesas) {
        super(parent, "Nueva reserva", true);

        for (Mesa m : mesas) {
            cbMesa.addItem(m);
        }
        cbMesa.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean sel, boolean foco) {
                super.getListCellRendererComponent(list, value, index, sel, foco);
                if (value instanceof Mesa m) {
                    setText("Mesa " + m.getNumeroMesa() + "  (capacidad " + m.getCapacidad() + ")");
                }
                setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                setBackground(sel ? OLIVA_TINTE : SUPERFICIE);
                setForeground(TINTA);
                return this;
            }
        });

        estilarCampo(txtCliente);
        estilarCampo(txtFecha);
        estilarCampo(txtHora);
        estilarCampo(spPersonas);
        estilarCampo(cbMesa);

        JPanel root = panelDialogo("Nueva reserva", "Datos del cliente y de la mesa");
        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setOpaque(false);
        centro.add(formulario(
                new Object[]{"Cliente", txtCliente},
                new Object[]{"Fecha (d/m/aaaa)", txtFecha, "Hora (HH:mm)", txtHora},
                new Object[]{"Personas", spPersonas, "Mesa", cbMesa}), BorderLayout.CENTER);
        centro.add(lblError, BorderLayout.SOUTH);
        root.add(centro, BorderLayout.CENTER);

        RoundedButton btnCancelar = secundario("Cancelar", null);
        RoundedButton btnGuardar  = primario("Guardar", null);
        root.add(pieDialogo(btnCancelar, btnGuardar), BorderLayout.SOUTH);
        setContentPane(root);

        btnGuardar.addActionListener(e -> {
            if (validar()) {
                confirmado = true;
                dispose();
            }
        });
        btnCancelar.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(btnGuardar);
        ajustarDialogo(this, parent, 500);
        SwingUtilities.invokeLater(() -> txtCliente.requestFocusInWindow());
    }

    private boolean validar() {
        lblError.setText(" ");
        if (txtCliente.getText().trim().isEmpty()) {
            aviso("Ingresa el nombre del cliente.", txtCliente);
            return false;
        }
        try {
            LocalDate.parse(txtFecha.getText().trim(), FECHA);
        } catch (DateTimeParseException ex) {
            aviso("Fecha inválida. Usa el formato d/m/aaaa (ej: 15/10/2026).", txtFecha);
            return false;
        }
        try {
            LocalTime.parse(txtHora.getText().trim(), DateTimeFormatter.ofPattern("H:mm"));
        } catch (DateTimeParseException ex) {
            aviso("Hora inválida. Usa el formato HH:mm (ej: 20:30).", txtHora);
            return false;
        }
        Mesa mesa = (Mesa) cbMesa.getSelectedItem();
        if (mesa == null) {
            aviso("No hay mesas registradas. Registra mesas antes de crear una reserva.", cbMesa);
            return false;
        }
        if (getPersonas() > mesa.getCapacidad()) {
            aviso("La mesa " + mesa.getNumeroMesa() + " tiene capacidad para " + mesa.getCapacidad()
                    + " personas. Elige otra mesa o reduce la cantidad.", cbMesa);
            return false;
        }
        return true;
    }

    private void aviso(String mensaje, JComponent foco) {
        lblError.setText(mensaje);
        foco.requestFocusInWindow();
    }

    public boolean isConfirmado()  { return confirmado; }
    public String getCliente()     { return txtCliente.getText().trim(); }
    public String getFecha()       { return txtFecha.getText().trim(); }
    public String getHora()        {
        return LocalTime.parse(txtHora.getText().trim(), DateTimeFormatter.ofPattern("H:mm")).format(HORA);
    }
    public int getPersonas()       { return (Integer) spPersonas.getValue(); }
    public int getMesa()           { return ((Mesa) cbMesa.getSelectedItem()).getNumeroMesa(); }
}

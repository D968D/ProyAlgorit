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

/** Formulario único para registrar una reserva (reemplaza la cadena de cuadros de diálogo). */
public class AgregarReservaDialog extends JDialog {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("d/M/yyyy");
    private static final DateTimeFormatter HORA  = DateTimeFormatter.ofPattern("HH:mm");

    private final JTextField txtCliente = new JTextField();
    private final JTextField txtFecha   = new JTextField(LocalDate.now().format(FECHA));
    private final JTextField txtHora    = new JTextField("20:00");
    private final JSpinner spPersonas   = new JSpinner(new SpinnerNumberModel(2, 1, 50, 1));
    private final JComboBox<Mesa> cbMesa   = new JComboBox<>();

    private boolean confirmado = false;

    public AgregarReservaDialog(JFrame parent, List<Mesa> mesas) {
        super(parent, "Nueva reserva", true);
        setSize(420, 430);
        setResizable(false);
        setLocationRelativeTo(parent);

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
                return this;
            }
        });

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(FONDO_EXTERNO);
        root.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        RoundedPanel card = new RoundedPanel(22, CREMA);
        card.setLayout(new BorderLayout());
        card.add(headerBar("Nueva reserva", "Datos del cliente y la mesa"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(14, 24, 4, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.anchor = GridBagConstraints.WEST;

        agregarFila(form, gbc, 0, "Cliente", txtCliente);
        agregarFila(form, gbc, 1, "Fecha (d/m/aaaa)", txtFecha);
        agregarFila(form, gbc, 2, "Hora (HH:mm)", txtHora);
        agregarFila(form, gbc, 3, "Personas", spPersonas);
        agregarFila(form, gbc, 4, "Mesa", cbMesa);
        card.add(form, BorderLayout.CENTER);

        RoundedButton btnGuardar  = button("GUARDAR", IconoVector.Tipo.AGREGAR, VERDE_OLIVA, VERDE_HOVER);
        RoundedButton btnCancelar = button("CANCELAR", TAUPE, TAUPE_HOVER);
        btnGuardar.setPreferredSize(new Dimension(140, 38));
        btnCancelar.setPreferredSize(new Dimension(120, 38));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        botones.setOpaque(false);
        botones.add(btnGuardar);
        botones.add(btnCancelar);
        card.add(botones, BorderLayout.SOUTH);

        btnGuardar.addActionListener(e -> {
            if (validar()) {
                confirmado = true;
                dispose();
            }
        });
        btnCancelar.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(btnGuardar);

        root.add(card, BorderLayout.CENTER);
        setContentPane(root);
        SwingUtilities.invokeLater(() -> txtCliente.requestFocusInWindow());
    }

    private void agregarFila(JPanel form, GridBagConstraints gbc, int fila, String etiqueta, JComponent campo) {
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(FONT_HEADER);
        lbl.setForeground(TEXTO_OSCURO);
        campo.setFont(FONT_TABLA);
        campo.setPreferredSize(new Dimension(170, 30));

        gbc.gridy = fila;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        form.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(campo, gbc);
    }

    private boolean validar() {
        if (txtCliente.getText().trim().isEmpty()) {
            aviso("Ingrese el nombre del cliente.", txtCliente);
            return false;
        }
        try {
            LocalDate.parse(txtFecha.getText().trim(), FECHA);
        } catch (DateTimeParseException ex) {
            aviso("Fecha inválida. Use el formato d/m/aaaa (ej: 15/10/2026).", txtFecha);
            return false;
        }
        try {
            LocalTime.parse(txtHora.getText().trim(), DateTimeFormatter.ofPattern("H:mm"));
        } catch (DateTimeParseException ex) {
            aviso("Hora inválida. Use el formato HH:mm (ej: 20:30).", txtHora);
            return false;
        }
        Mesa mesa = (Mesa) cbMesa.getSelectedItem();
        if (mesa == null) {
            aviso("No hay mesas registradas. Registre mesas antes de crear una reserva.", cbMesa);
            return false;
        }
        if (getPersonas() > mesa.getCapacidad()) {
            aviso("La mesa " + mesa.getNumeroMesa() + " tiene capacidad para " + mesa.getCapacidad()
                    + " personas. Elija otra mesa o reduzca la cantidad.", cbMesa);
            return false;
        }
        return true;
    }

    private void aviso(String mensaje, JComponent foco) {
        JOptionPane.showMessageDialog(this, mensaje, "Datos incompletos", JOptionPane.WARNING_MESSAGE);
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

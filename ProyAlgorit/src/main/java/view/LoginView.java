package view;

import dao.LoginDAO;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;


public class LoginView extends JFrame {

    // ---------- Paleta de marca -------------------------------------------------
    private static final Color VERDE_OLIVA   = new Color(0x6E, 0x7C, 0x3B);
    private static final Color VERDE_OSCURO  = new Color(0x3E, 0x47, 0x22);
    private static final Color NARANJA       = new Color(0xD9, 0x62, 0x2B);
    private static final Color NARANJA_HOVER = new Color(0xC2, 0x54, 0x22);
    private static final Color CREMA         = new Color(0xF8, 0xF3, 0xE6);
    private static final Color FONDO_EXTERNO = new Color(0xEA, 0xE6, 0xD8);
    private static final Color TEXTO_OSCURO  = new Color(0x30, 0x2C, 0x20);
    private static final Color GRIS_TEXTO    = new Color(0x8D, 0x89, 0x7A);
    private static final Color LINEA_CAMPO   = new Color(0xCB, 0xC5, 0xB2);

    private final JTextField txtUsuario = new JTextField();
    private final JPasswordField txtContrasena = new JPasswordField();
    private final RoundedButton btnIngresar = new RoundedButton("INGRESAR", NARANJA, NARANJA_HOVER);
    private final LoginDAO loginDAO = new LoginDAO();

    public LoginView() {
        setTitle("Iniciar sesión - C&R OrderManager");
        setSize(780, 480);
        setMinimumSize(new Dimension(680, 440));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(FONDO_EXTERNO);
        outer.setBorder(new EmptyBorder(28, 28, 28, 28));

        JPanel card = new RoundedPanel(26, CREMA);
        card.setLayout(new GridLayout(1, 2));
        card.add(buildFormPanel());
        card.add(buildBrandPanel());

        outer.add(card, BorderLayout.CENTER);
        setContentPane(outer);

        btnIngresar.addActionListener(e -> intentarLogin());
        getRootPane().setDefaultButton(btnIngresar);
    }

    // ---------- Panel izquierdo: formulario -------------------------------------
    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CREMA);
        panel.setBorder(new EmptyBorder(40, 46, 40, 46));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblTitulo = new JLabel("BIENVENIDO");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblTitulo.setForeground(VERDE_OLIVA);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 4, 0);
        panel.add(lblTitulo, gbc);

        JLabel lblSub = new JLabel("El Cordón y la Rosa · Ica, Perú");
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblSub.setForeground(GRIS_TEXTO);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 30, 0);
        panel.add(lblSub, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(6, 0, 6, 0);
        panel.add(fieldLabel("USUARIO"), gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 22, 0);
        styleUnderlineField(txtUsuario);
        panel.add(txtUsuario, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(6, 0, 6, 0);
        panel.add(fieldLabel("CONTRASEÑA"), gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 8, 0);
        styleUnderlineField(txtContrasena);
        panel.add(txtContrasena, gbc);

        JCheckBox chkRecordar = new JCheckBox("Recordarme");
        chkRecordar.setBackground(CREMA);
        chkRecordar.setForeground(GRIS_TEXTO);
        chkRecordar.setFocusPainted(false);
        chkRecordar.setFont(new Font("SansSerif", Font.PLAIN, 12));
        gbc.gridy = 6;
        gbc.insets = new Insets(4, 0, 26, 0);
        panel.add(chkRecordar, gbc);

        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 0, 0);
        btnIngresar.setPreferredSize(new Dimension(10, 44));
        panel.add(btnIngresar, gbc);

        return panel;
    }

    private JLabel fieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        lbl.setForeground(VERDE_OLIVA);
        return lbl;
    }

    private void styleUnderlineField(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 15));
        field.setForeground(TEXTO_OSCURO);
        field.setBackground(CREMA);
        field.setCaretColor(NARANJA);
        field.setBorder(new UnderlineBorder(LINEA_CAMPO));
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(new UnderlineBorder(NARANJA));
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(new UnderlineBorder(LINEA_CAMPO));
            }
        });
    }

    // ---------- Panel derecho: identidad de marca --------------------------------
    private JPanel buildBrandPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 18));
                g2.fillOval(-60, -60, 220, 220);
                g2.setColor(new Color(217, 98, 43, 40));
                g2.fillOval(getWidth() - 140, getHeight() - 140, 200, 200);
                g2.dispose();
            }
        };
        panel.setBackground(VERDE_OSCURO);
        panel.setOpaque(true);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(6, 24, 6, 24);

        JLabel lockIcon = new JLabel("\uD83D\uDD12");
        lockIcon.setFont(new Font("SansSerif", Font.PLAIN, 40));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 24, 18, 24);
        panel.add(lockIcon, gbc);

        JLabel lblNombre = new JLabel("EL CORDÓN Y LA ROSA");
        lblNombre.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblNombre.setForeground(Color.WHITE);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 24, 8, 24);
        panel.add(lblNombre, gbc);

        JLabel lblTag = new JLabel("Sistema de Gestión de Pedidos");
        lblTag.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblTag.setForeground(new Color(230, 226, 210));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 24, 0, 24);
        panel.add(lblTag, gbc);

        return panel;
    }

    private void intentarLogin() {
        String usuario = txtUsuario.getText().trim();
        String contra = new String(txtContrasena.getPassword());

        if (usuario.isEmpty() || contra.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar usuario y contraseña.",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean valido = loginDAO.validar(usuario, contra);

        if (valido) {
            dispose();
            new MenuPrincipal().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Usuario o contraseña incorrectos.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- Componentes auxiliares reutilizables -----------------------------

    /** Panel con esquinas redondeadas y color de fondo sólido. */
    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bg;

        RoundedPanel(int radius, Color bg) {
            this.radius = radius;
            this.bg = bg;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Botón sólido con esquinas redondeadas y color al pasar el mouse. */
    private static class RoundedButton extends JButton {
        private final Color base;
        private final Color hover;
        private boolean isHover = false;

        RoundedButton(String text, Color base, Color hover) {
            super(text);
            this.base = base;
            this.hover = hover;
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("SansSerif", Font.BOLD, 13));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHover = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isHover ? hover : base);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Borde inferior simple (subrayado) para campos de texto, estilo "material". */
    private static class UnderlineBorder extends AbstractBorder {
        private final Color color;

        UnderlineBorder(Color color) {
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2f));
            g2.drawLine(x, y + height - 3, x + width, y + height - 3);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(6, 2, 8, 2);
        }
    }
}
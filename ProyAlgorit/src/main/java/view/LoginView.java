package view;

import controller.LoginController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;

import static view.UIStyle.*;

public class LoginView extends JFrame {

    private final JTextField txtUsuario = new JTextField();
    private final JPasswordField txtContrasena = new JPasswordField();
    private final JLabel lblError = mensajeError();
    private final RoundedButton btnIngresar = primario("Ingresar", null);
    private final LoginController loginController = new LoginController();

    public LoginView() {
        setTitle("Iniciar sesión - C&R OrderManager");
        setSize(820, 500);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(720, 460));
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel contenido = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 0.55;
        contenido.add(buildFormPanel(), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.45;
        contenido.add(buildBrandPanel(), gbc);
        setContentPane(contenido);

        btnIngresar.addActionListener(e -> intentarLogin());
        getRootPane().setDefaultButton(btnIngresar);
        KeyAdapter limpiar = new KeyAdapter() {
            @Override public void keyTyped(KeyEvent e) {
                lblError.setText(" ");
            }
        };
        txtUsuario.addKeyListener(limpiar);
        txtContrasena.addKeyListener(limpiar);
        activarEscalado(this);
    }

    // ---------- Panel izquierdo: formulario -------------------------------------
    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(SUPERFICIE);
        panel.setBorder(new EmptyBorder(40, 56, 40, 56));

        estilarCampo(txtUsuario);
        estilarCampo(txtContrasena);
        txtUsuario.setPreferredSize(new Dimension(280, 42));
        txtContrasena.setPreferredSize(new Dimension(280, 42));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1;

        JLabel lblTitulo = new JLabel("Bienvenido");
        lblTitulo.setFont(new Font(FAMILIA, Font.BOLD, 28));
        lblTitulo.setForeground(TINTA);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 6, 0);
        panel.add(lblTitulo, gbc);

        JLabel lblSub = new JLabel("Ingresa tus datos para continuar");
        lblSub.setFont(FONT_SUB);
        lblSub.setForeground(TINTA_SUAVE);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 32, 0);
        panel.add(lblSub, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 6, 0);
        panel.add(etiqueta("Usuario"), gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 18, 0);
        panel.add(txtUsuario, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 6, 0);
        panel.add(etiqueta("Contraseña"), gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 8, 0);
        panel.add(txtContrasena, gbc);

        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 14, 0);
        panel.add(lblError, gbc);

        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(btnIngresar, gbc);

        return panel;
    }

    // ---------- Panel derecho: identidad de marca --------------------------------
    private JPanel buildBrandPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // dos anillos finos como único adorno
                g2.setColor(new Color(255, 255, 255, 22));
                g2.setStroke(new BasicStroke(1.2f));
                int d = Math.round(getWidth() * 0.9f);
                g2.draw(new Ellipse2D.Float(getWidth() - d * 0.55f, getHeight() - d * 0.55f, d, d));
                g2.draw(new Ellipse2D.Float(getWidth() - d * 0.40f, getHeight() - d * 0.40f, d * 0.7f, d * 0.7f));
                g2.dispose();
            }
        };
        panel.setBackground(OLIVA_OSCURO);
        panel.setOpaque(true);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 24, 20, 24);
        panel.add(new Logo(), gbc);

        JLabel lblTag = new JLabel("Sistema de gestión de pedidos");
        lblTag.setFont(FONT_SUB);
        lblTag.setForeground(new Color(0xD5, 0xDA, 0xC4));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 24, 0, 24);
        panel.add(lblTag, gbc);

        return panel;
    }

    /** Logo del restaurante (src/main/resources/logo.png); su tamaño sigue la escala de la ventana. */
    private static class Logo extends JComponent {
        private final Image img;

        Logo() {
            java.net.URL url = LoginView.class.getResource("/logo.png");
            img = url == null ? null : new ImageIcon(url).getImage();
            setFont(new Font(FAMILIA, Font.PLAIN, 13));
        }

        private int alto() {
            return Math.round(getFont().getSize2D() * 15);
        }

        private int ancho() {
            if (img == null) return alto();
            return Math.round(alto() * img.getWidth(null) / (float) img.getHeight(null));
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(ancho(), alto());
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (img == null) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.drawImage(img, 0, 0, ancho(), alto(), null);
            g2.dispose();
        }
    }

    /** Monograma de la marca dentro de un anillo fino (reemplaza al emoji del candado). */
    private static class Monograma extends JComponent {
        private final String texto;

        Monograma(String texto) {
            this.texto = texto;
            setFont(new Font(FAMILIA, Font.BOLD, 24));
        }

        @Override
        public Dimension getPreferredSize() {
            int d = Math.round(getFont().getSize2D() * 3.6f);
            return new Dimension(d, d);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(new Color(255, 255, 255, 120));
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(new Ellipse2D.Float(1, 1, getWidth() - 3, getHeight() - 3));
            g2.setColor(Color.WHITE);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(texto, (getWidth() - fm.stringWidth(texto)) / 2,
                    (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }

    private void intentarLogin() {
        String usuario = txtUsuario.getText().trim();
        String contra = new String(txtContrasena.getPassword());

        if (usuario.isEmpty() || contra.isEmpty()) {
            lblError.setText("Ingresa tu usuario y tu contraseña.");
            (usuario.isEmpty() ? txtUsuario : txtContrasena).requestFocusInWindow();
            return;
        }

        LoginController.Resultado resultado = loginController.iniciarSesion(usuario, contra);

        if (resultado == LoginController.Resultado.OK) {
            dispose();
            new MenuPrincipal().setVisible(true);
        } else if (resultado == LoginController.Resultado.SIN_CONEXION) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo conectar con la base de datos.\nRevise db.properties (o las variables DB_URL, DB_USER, DB_PASSWORD) y que el servidor esté activo.",
                    "Sin conexión", JOptionPane.ERROR_MESSAGE);
        } else {
            lblError.setText("Usuario o contraseña incorrectos.");
            txtContrasena.selectAll();
            txtContrasena.requestFocusInWindow();
        }
    }
}

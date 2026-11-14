package view;

import controller.MesaController;
import controller.PlatoController;
import controller.ReservaController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

import static view.UIStyle.*;

public class MenuPrincipal extends JFrame {

    public MenuPrincipal() {
        setTitle("C&R OrderManager - Menú Principal");
        setSize(920, 520);
        setMinimumSize(new Dimension(800, 460));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = raiz(36, 44, 40, 44);

        RoundedButton btnSalir = discreto("Salir", IconoVector.Tipo.SALIR);
        btnSalir.addActionListener(e -> System.exit(0));
        root.add(encabezado("El Cordón y la Rosa", "Sistema de gestión de pedidos · Ica, Perú", btnSalir),
                BorderLayout.NORTH);

        TarjetaMenu mesas = new TarjetaMenu("Mesas", "Disponibilidad y capacidad del salón", IconoVector.Tipo.MESA);
        TarjetaMenu platos = new TarjetaMenu("Platos", "Carta, precios y categorías", IconoVector.Tipo.PLATO);
        TarjetaMenu reservas = new TarjetaMenu("Reservas", "Reservas del salón y cola de atención", IconoVector.Tipo.RESERVA);

        mesas.addActionListener(e -> {
            MesaView mesaView = new MesaView();
            new MesaController(mesaView);
        });
        platos.addActionListener(e -> {
            PlatoView platoView = new PlatoView();
            new PlatoController(platoView);
        });
        reservas.addActionListener(e -> new ReservaController(new ReservaView()));

        JPanel rejilla = new JPanel(new GridLayout(1, 3, 20, 0));
        rejilla.setOpaque(false);
        rejilla.add(mesas);
        rejilla.add(platos);
        rejilla.add(reservas);

        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        centro.add(rejilla, gbc);
        root.add(centro, BorderLayout.CENTER);

        setContentPane(root);
        activarEscalado(this);
    }

    /** Acceso a un módulo: icono, nombre y una línea de descripción. Se puede usar con teclado. */
    private static class TarjetaMenu extends JButton {
        private final String titulo;
        private final String descripcion;
        private final IconoVector.Tipo icono;
        private boolean hover = false;
        private boolean anillo = false;

        TarjetaMenu(String titulo, String descripcion, IconoVector.Tipo icono) {
            this.titulo = titulo;
            this.descripcion = descripcion;
            this.icono = icono;
            setFont(new Font(FAMILIA, Font.PLAIN, 15));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            getAccessibleContext().setAccessibleName(titulo);
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
            });
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) {
                    FocusEvent.Cause c = e.getCause();
                    anillo = c == FocusEvent.Cause.TRAVERSAL || c == FocusEvent.Cause.TRAVERSAL_FORWARD
                            || c == FocusEvent.Cause.TRAVERSAL_BACKWARD;
                    repaint();
                }
                @Override public void focusLost(FocusEvent e) { anillo = false; repaint(); }
            });
        }

        @Override
        public Dimension getPreferredSize() {
            float f = getFont().getSize2D();
            return new Dimension((int) (f * 13), (int) (f * 13));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            float f = getFont().getSize2D();
            int w = getWidth(), h = getHeight();

            RoundRectangle2D forma = new RoundRectangle2D.Float(0.5f, 0.5f, w - 1, h - 1, 18, 18);
            g2.setColor(hover ? OLIVA_TINTE : SUPERFICIE);
            g2.fill(forma);
            g2.setColor(hover ? OLIVA : BORDE);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(forma);
            if (anillo) {
                g2.setColor(ACENTO);
                g2.setStroke(new BasicStroke(2f));
                g2.draw(new RoundRectangle2D.Float(2, 2, w - 4, h - 4, 16, 16));
            }

            int pad = Math.round(f * 1.7f);
            int disco = Math.round(f * 3.3f);
            g2.setColor(hover ? SUPERFICIE : OLIVA_TINTE);
            g2.fill(new Ellipse2D.Float(pad, pad, disco, disco));
            int lado = Math.round(disco * 0.5f);
            new IconoVector(icono, lado, OLIVA).paintIcon(this, g2, pad + (disco - lado) / 2, pad + (disco - lado) / 2);

            Font fTitulo = getFont().deriveFont(Font.BOLD, f * 1.3f);
            g2.setFont(fTitulo);
            g2.setColor(TINTA);
            int y = pad + disco + Math.round(f * 2.0f);
            g2.drawString(titulo, pad, y);

            Font fDesc = getFont().deriveFont(Font.PLAIN, f * 0.9f);
            g2.setFont(fDesc);
            g2.setColor(TINTA_SUAVE);
            FontMetrics fm = g2.getFontMetrics();
            int ancho = w - pad * 2;
            y += Math.round(f * 1.5f);
            StringBuilder linea = new StringBuilder();
            for (String palabra : descripcion.split(" ")) {
                String prueba = linea.length() == 0 ? palabra : linea + " " + palabra;
                if (fm.stringWidth(prueba) > ancho && linea.length() > 0) {
                    g2.drawString(linea.toString(), pad, y);
                    y += Math.round(fm.getHeight() * 1.15f);
                    linea = new StringBuilder(palabra);
                } else {
                    linea = new StringBuilder(prueba);
                }
            }
            g2.drawString(linea.toString(), pad, y);
            g2.dispose();
        }
    }
}

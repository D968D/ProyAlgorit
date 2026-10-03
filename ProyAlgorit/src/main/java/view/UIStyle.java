package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;


public final class UIStyle {

    // ---------- Paleta de marca --------------------------------------------------
    public static final Color VERDE_OLIVA     = new Color(0x6E, 0x7C, 0x3B);
    public static final Color VERDE_HOVER     = new Color(0x5C, 0x68, 0x30);
    public static final Color VERDE_OSCURO    = new Color(0x3E, 0x47, 0x22);

    public static final Color NARANJA         = new Color(0xD9, 0x62, 0x2B);
    public static final Color NARANJA_HOVER   = new Color(0xC2, 0x54, 0x22);

    public static final Color TERRACOTA       = new Color(0xB5, 0x48, 0x2F);
    public static final Color TERRACOTA_HOVER = new Color(0x9E, 0x3D, 0x28);

    public static final Color MOSTAZA         = new Color(0xC9, 0x97, 0x1B);
    public static final Color MOSTAZA_HOVER   = new Color(0xB1, 0x84, 0x17);

    public static final Color TAUPE           = new Color(0x6B, 0x64, 0x55);
    public static final Color TAUPE_HOVER     = new Color(0x59, 0x53, 0x46);

    public static final Color ROYAL_BLUE      = new Color(0x00, 0x23, 0x66);
    public static final Color ROYAL_BLUE_HOVER= new Color(0x03, 0x3E, 0xAD);


    public static final Color CREMA           = new Color(0xF8, 0xF3, 0xE6);
    public static final Color FONDO_EXTERNO   = new Color(0xEA, 0xE6, 0xD8);
    public static final Color TEXTO_OSCURO    = new Color(0x30, 0x2C, 0x20);
    public static final Color GRIS_TEXTO      = new Color(0x8D, 0x89, 0x7A);
    public static final Color LINEA           = new Color(0xDD, 0xD7, 0xC5);
    public static final Color LINEA_CAMPO     = new Color(0xCB, 0xC5, 0xB2);
    public static final Color FILA_ALTERNA    = new Color(0xF1, 0xEC, 0xDC);
    public static final Color SELECCION       = new Color(0xE4, 0xE8, 0xD2);
    public static final Color DESHABILITADO   = new Color(0xC9, 0xC4, 0xB4);

    // ---------- Tipografía ---------------------------------------------------------
    public static final Font FONT_TITULO = new Font("SansSerif", Font.BOLD, 22);
    public static final Font FONT_SUB    = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_BOTON  = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONT_TABLA  = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_HEADER = new Font("SansSerif", Font.BOLD, 13);

    private UIStyle() {
    }

    public static RoundedButton button(String text, Color base, Color hover) {
        return new RoundedButton(text, base, hover);
    }

    public static RoundedButton button(String text, IconoVector.Tipo icono, Color base, Color hover) {
        RoundedButton b = new RoundedButton(text, base, hover);
        b.setIcon(new IconoVector(icono, 20));
        b.setIconTextGap(10);
        b.setHorizontalTextPosition(SwingConstants.RIGHT);
        return b;
    }

    public static void activarEscalado(JFrame frame) {
        final int baseW = Math.max(1, frame.getWidth());
        final int baseH = Math.max(1, frame.getHeight());
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                double rw = frame.getWidth() / (double) baseW;
                double rh = frame.getHeight() / (double) baseH;

                double escala = Math.min(Math.sqrt(rw * rh), rw);
                escala = Math.max(1.0, Math.min(escala, 2.5));
                escalar(frame.getContentPane(), escala);
                frame.getContentPane().revalidate();
                frame.getContentPane().repaint();
            }
        });
    }

    private static void escalar(Component c, double escala) {
        if (c instanceof JComponent jc) {
            if (jc.getClientProperty("baseFont") == null && jc.getFont() != null) {
                jc.putClientProperty("baseFont", jc.getFont());
            }
            Font base = (Font) jc.getClientProperty("baseFont");
            if (base != null && !(jc instanceof JScrollBar)) {
                jc.setFont(base.deriveFont((float) (base.getSize2D() * escala)));
            }

            if (jc instanceof JButton) {
                if (jc.getClientProperty("basePref") == null && jc.isPreferredSizeSet()) {
                    jc.putClientProperty("basePref", jc.getPreferredSize());
                }
                Dimension bp = (Dimension) jc.getClientProperty("basePref");
                if (bp != null) {
                    jc.setPreferredSize(new Dimension((int) (bp.width * escala), (int) (bp.height * escala)));
                }
                JButton b = (JButton) jc;
                if (b.getIcon() instanceof IconoVector iv) {
                    if (jc.getClientProperty("baseIconSize") == null) {
                        jc.putClientProperty("baseIconSize", iv.getIconWidth());
                    }
                    int tam = (int) Math.round((Integer) jc.getClientProperty("baseIconSize") * escala);
                    b.setIcon(new IconoVector(iv.getTipo(), tam, iv.getColor()));
                    b.setIconTextGap((int) Math.round(10 * escala));
                }
            }

            if (jc instanceof JTable t) {
                t.setRowHeight((int) Math.round(28 * escala));
                JTableHeader h = t.getTableHeader();
                h.setPreferredSize(new Dimension(h.getWidth(), (int) Math.round(34 * escala)));
            }
        }
        if (c instanceof Container cont) {
            for (Component hijo : cont.getComponents()) {
                escalar(hijo, escala);
            }
        }
    }


    public static void styleTable(JTable tabla) {
        tabla.setFont(FONT_TABLA);
        tabla.setRowHeight(28);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setSelectionBackground(SELECCION);
        tabla.setSelectionForeground(TEXTO_OSCURO);
        tabla.setFillsViewportHeight(true);
        tabla.setGridColor(LINEA);

        JTableHeader header = tabla.getTableHeader();
        header.setFont(FONT_HEADER);
        header.setBackground(VERDE_OLIVA);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 34));
        header.setReorderingAllowed(false);

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                             boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : FILA_ALTERNA);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        });
    }

    public static JPanel headerBar(String titulo, String subtitulo) {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(VERDE_OSCURO);
        header.setBorder(BorderFactory.createEmptyBorder(18, 24, 16, 24));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(FONT_TITULO);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(lblTitulo);

        if (subtitulo != null) {
            JLabel lblSub = new JLabel(subtitulo);
            lblSub.setFont(FONT_SUB);
            lblSub.setForeground(new Color(230, 226, 210));
            lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);
            lblSub.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
            header.add(lblSub);
        }
        return header;
    }


    public static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bg;

        public RoundedPanel(int radius, Color bg) {
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


    public static class RoundedButton extends JButton {
        private final Color base;
        private final Color hover;
        private boolean isHover = false;

        public RoundedButton(String text, Color base, Color hover) {
            super(text);
            this.base = base;
            this.hover = hover;
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setForeground(Color.WHITE);
            setFont(FONT_BOTON);
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
            g2.setColor(isEnabled() ? (isHover ? hover : base) : DESHABILITADO);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
            g2.dispose();
            super.paintComponent(g);
        }
    }
}


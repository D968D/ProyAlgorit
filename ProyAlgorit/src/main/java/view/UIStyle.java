package view;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.BasicSpinnerUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Sistema de diseño de C&amp;R OrderManager.
 *
 * Principios: un solo color de acción (oliva), fondos neutros, mucho espacio en blanco,
 * jerarquía por peso y no por color, y bordes finos en lugar de bloques de color.
 * Todo el aspecto de la aplicación se controla desde este archivo.
 */
public final class UIStyle {

    // ---------- Paleta ----------------------------------------------------------------
    /** Fondo de las ventanas. */
    public static final Color LIENZO         = new Color(0xF6, 0xF6, 0xF1);
    /** Tarjetas, tablas, campos y diálogos. */
    public static final Color SUPERFICIE     = Color.WHITE;
    /** Texto principal. */
    public static final Color TINTA          = new Color(0x23, 0x29, 0x1B);
    /** Texto secundario (subtítulos, etiquetas, encabezados de tabla). */
    public static final Color TINTA_SUAVE    = new Color(0x6C, 0x71, 0x63);
    /** Líneas finas y bordes de tarjetas. */
    public static final Color BORDE          = new Color(0xE2, 0xE3, 0xD9);
    /** Bordes de campos y botones secundarios. */
    public static final Color BORDE_FUERTE   = new Color(0xCD, 0xD0, 0xC0);
    /** Color de marca y de la acción principal. */
    public static final Color OLIVA          = new Color(0x5B, 0x6A, 0x33);
    public static final Color OLIVA_HOVER    = new Color(0x4A, 0x58, 0x29);
    public static final Color OLIVA_OSCURO   = new Color(0x34, 0x3D, 0x1E);
    /** Fondo suave para selección y hover. */
    public static final Color OLIVA_TINTE    = new Color(0xEC, 0xEF, 0xE0);
    /** Acento puntual: resaltados y estado "ocupado". */
    public static final Color ACENTO         = new Color(0xD9, 0x62, 0x2B);
    public static final Color PELIGRO        = new Color(0xB2, 0x3A, 0x2B);
    public static final Color PELIGRO_TINTE  = new Color(0xFB, 0xEC, 0xE9);
    public static final Color EXITO          = new Color(0x4E, 0x8B, 0x3A);
    public static final Color PENDIENTE      = new Color(0xC5, 0x8A, 0x1B);
    public static final Color DESHABILITADO  = new Color(0xB4, 0xB7, 0xA8);
    /** Franja superior de las ventanas: oliva del logo. */
    public static final Color FRANJA         = new Color(0x79, 0x80, 0x27);
    /** Velo muy tenue bajo la franja. */
    public static final Color FRANJA_VELO    = new Color(0xE6, 0xE9, 0xCC);
    private static final Color HOVER_DISCRETO = new Color(0xEB, 0xEC, 0xE1);
    private static final Color LINEA_FILA    = new Color(0xEF, 0xF0, 0xE8);

    // ---------- Tipografía ------------------------------------------------------------
    /** Primera fuente moderna disponible en el equipo; en Windows será Segoe UI. */
    public static final String FAMILIA = elegirFamilia();
    public static final Font FONT_BASE     = new Font(FAMILIA, Font.PLAIN, 13);
    public static final Font FONT_TITULO   = new Font(FAMILIA, Font.BOLD, 24);
    public static final Font FONT_SUB      = new Font(FAMILIA, Font.PLAIN, 13);
    public static final Font FONT_BOTON    = new Font(FAMILIA, Font.PLAIN, 13);
    public static final Font FONT_BOTON_B  = new Font(FAMILIA, Font.BOLD, 13);
    public static final Font FONT_TABLA    = new Font(FAMILIA, Font.PLAIN, 14);
    public static final Font FONT_HEADER   = new Font(FAMILIA, Font.BOLD, 12);
    public static final Font FONT_ETIQUETA = new Font(FAMILIA, Font.BOLD, 12);
    public static final Font FONT_CAMPO    = new Font(FAMILIA, Font.PLAIN, 14);

    public static final int FILA_ALTO = 38;
    public static final int HEADER_ALTO = 40;
    public static final int ALTO_CAMPO = 40;

    private UIStyle() {
    }

    private static String elegirFamilia() {
        Set<String> disponibles = new HashSet<>(Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        for (String f : new String[]{"Segoe UI", "Inter", "SF Pro Text", "Helvetica Neue", "Roboto", "Open Sans"}) {
            if (disponibles.contains(f)) {
                return f;
            }
        }
        return Font.SANS_SERIF;
    }

    // ---------- Tema global ------------------------------------------------------------
    /** Llamar una sola vez, antes de crear cualquier ventana (ver Main). */
    public static void instalarTema() {
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignorada) {
            // se mantiene el aspecto por defecto
        }

        for (Object clave : Collections.list(UIManager.getDefaults().keys())) {
            if (UIManager.get(clave) instanceof FontUIResource) {
                UIManager.put(clave, new FontUIResource(FONT_BASE));
            }
        }

        UIManager.put("ButtonUI", BotonUI.class.getName());
        UIManager.put("ScrollBarUI", BarraUI.class.getName());
        UIManager.put("SpinnerUI", SpinnerPlanoUI.class.getName());
        UIManager.put("ComboBoxUI", ComboPlanoUI.class.getName());

        UIManager.put("Panel.background", SUPERFICIE);
        UIManager.put("OptionPane.background", SUPERFICIE);
        UIManager.put("OptionPane.messageForeground", TINTA);
        UIManager.put("OptionPane.messageFont", new FontUIResource(FONT_CAMPO));
        UIManager.put("OptionPane.buttonFont", new FontUIResource(FONT_BOTON));
        UIManager.put("OptionPane.border", new EmptyBorder(20, 20, 8, 20));
        UIManager.put("OptionPane.buttonAreaBorder", new EmptyBorder(8, 16, 16, 16));
        UIManager.put("Label.foreground", TINTA);
        UIManager.put("ScrollBar.background", SUPERFICIE);
        UIManager.put("ScrollPane.background", SUPERFICIE);
        UIManager.put("ToolTip.background", TINTA);
        UIManager.put("ToolTip.foreground", Color.WHITE);
        UIManager.put("ToolTip.border", new EmptyBorder(5, 9, 5, 9));
        UIManager.put("ComboBox.squareButton", Boolean.FALSE);
        UIManager.put("ComboBox.background", SUPERFICIE);
        UIManager.put("ComboBox.selectionBackground", OLIVA_TINTE);
        UIManager.put("ComboBox.selectionForeground", TINTA);
        UIManager.put("List.selectionBackground", OLIVA_TINTE);
        UIManager.put("List.selectionForeground", TINTA);
        UIManager.put("TextField.selectionBackground", OLIVA_TINTE);
        UIManager.put("TextField.selectionForeground", TINTA);
    }

    // ---------- Botones ----------------------------------------------------------------
    public enum Variante { PRIMARIO, SECUNDARIO, PELIGRO, DISCRETO }

    /** Acción principal de la pantalla: una sola por ventana. */
    public static RoundedButton primario(String texto, IconoVector.Tipo icono) {
        return new RoundedButton(texto, icono, Variante.PRIMARIO);
    }

    /** Acción habitual: borde fino, sin relleno. */
    public static RoundedButton secundario(String texto, IconoVector.Tipo icono) {
        return new RoundedButton(texto, icono, Variante.SECUNDARIO);
    }

    /** Acción destructiva: solo el texto es rojo. */
    public static RoundedButton peligro(String texto, IconoVector.Tipo icono) {
        return new RoundedButton(texto, icono, Variante.PELIGRO);
    }

    /** Herramientas y navegación: sin borde ni relleno hasta pasar el mouse. */
    public static RoundedButton discreto(String texto, IconoVector.Tipo icono) {
        return new RoundedButton(texto, icono, Variante.DISCRETO);
    }

    public static class RoundedButton extends JButton {
        private final Variante variante;
        private boolean hover = false;
        private boolean presionado = false;
        private boolean anillo = false;

        public RoundedButton(String texto, IconoVector.Tipo icono, Variante variante) {
            super(texto);
            this.variante = variante;
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setFont(variante == Variante.PRIMARIO ? FONT_BOTON_B : FONT_BOTON);
            setForeground(colorTexto());
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            if (icono != null) {
                setIcon(new IconoVector(icono, 18, colorTexto()));
                setIconTextGap(8);
                setHorizontalTextPosition(SwingConstants.RIGHT);
            }
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    hover = true;
                    if (variante == Variante.DISCRETO) setForeground(TINTA);
                    repaint();
                }
                @Override public void mouseExited(MouseEvent e) {
                    hover = false;
                    presionado = false;
                    if (variante == Variante.DISCRETO) setForeground(TINTA_SUAVE);
                    repaint();
                }
                @Override public void mousePressed(MouseEvent e) {
                    presionado = true;
                    repaint();
                }
                @Override public void mouseReleased(MouseEvent e) {
                    presionado = false;
                    repaint();
                }
            });
            // el anillo de foco solo se muestra al navegar con el teclado
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) {
                    FocusEvent.Cause c = e.getCause();
                    anillo = c == FocusEvent.Cause.TRAVERSAL || c == FocusEvent.Cause.TRAVERSAL_FORWARD
                            || c == FocusEvent.Cause.TRAVERSAL_BACKWARD;
                    repaint();
                }
                @Override public void focusLost(FocusEvent e) {
                    anillo = false;
                    repaint();
                }
            });
        }

        private Color colorTexto() {
            return switch (variante) {
                case PRIMARIO -> Color.WHITE;
                case SECUNDARIO -> TINTA;
                case PELIGRO -> PELIGRO;
                case DISCRETO -> TINTA_SUAVE;
            };
        }

        /** El relleno interior crece con la tipografía, así el botón escala junto con la ventana. */
        @Override
        public void setFont(Font f) {
            super.setFont(f);
            if (f != null) {
                int v = Math.round(f.getSize2D() * 0.72f);
                int h = Math.round(f.getSize2D() * 1.4f);
                setBorder(new EmptyBorder(v, h, v, h));
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            float w = getWidth(), h = getHeight();
            Color relleno = null;
            Color linea = null;
            if (!isEnabled()) {
                relleno = variante == Variante.DISCRETO ? null : new Color(0xEE, 0xEF, 0xE7);
            } else {
                switch (variante) {
                    case PRIMARIO -> relleno = presionado ? OLIVA_OSCURO : hover ? OLIVA_HOVER : OLIVA;
                    case SECUNDARIO -> {
                        relleno = hover ? OLIVA_TINTE : SUPERFICIE;
                        linea = BORDE_FUERTE;
                    }
                    case PELIGRO -> {
                        relleno = hover ? PELIGRO_TINTE : SUPERFICIE;
                        linea = hover ? PELIGRO : BORDE_FUERTE;
                    }
                    case DISCRETO -> relleno = hover ? HOVER_DISCRETO : null;
                }
            }
            RoundRectangle2D forma = new RoundRectangle2D.Float(0.5f, 0.5f, w - 1, h - 1, 10, 10);
            if (relleno != null) {
                g2.setColor(relleno);
                g2.fill(forma);
            }
            if (linea != null) {
                g2.setColor(linea);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(forma);
            }
            if (anillo) {
                g2.setColor(ACENTO);
                g2.setStroke(new BasicStroke(2f));
                g2.draw(new RoundRectangle2D.Float(1.5f, 1.5f, w - 3, h - 3, 9, 9));
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Aspecto plano para TODOS los botones que no son RoundedButton (p. ej. los de JOptionPane). */
    public static class BotonUI extends BasicButtonUI {
        public static ComponentUI createUI(JComponent c) {
            return new BotonUI();
        }

        @Override
        protected void installDefaults(AbstractButton b) {
            super.installDefaults(b);
            b.setRolloverEnabled(true);
            b.setOpaque(false);
            b.setFont(FONT_BOTON);
            b.setForeground(TINTA);
            b.setBorder(new EmptyBorder(9, 20, 9, 20));
            b.setFocusPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        private boolean esPrincipal(AbstractButton b) {
            return b instanceof JButton jb && jb.isDefaultButton();
        }

        @Override
        public void update(Graphics g, JComponent c) {
            AbstractButton b = (AbstractButton) c;
            if (b.isContentAreaFilled()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                ButtonModel m = b.getModel();
                boolean activo = m.isRollover() || m.isArmed();
                boolean principal = esPrincipal(b);
                RoundRectangle2D forma = new RoundRectangle2D.Float(0.5f, 0.5f, c.getWidth() - 1, c.getHeight() - 1, 10, 10);
                g2.setColor(principal ? (activo ? OLIVA_HOVER : OLIVA) : (activo ? OLIVA_TINTE : SUPERFICIE));
                g2.fill(forma);
                if (!principal) {
                    g2.setColor(BORDE_FUERTE);
                    g2.draw(forma);
                }
                g2.dispose();
            }
            paint(g, c);
        }

        @Override
        protected void paintText(Graphics g, AbstractButton b, Rectangle textRect, String text) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(b.getFont());
            FontMetrics fm = g2.getFontMetrics();
            Color color = b.getForeground();
            if (!b.isEnabled()) {
                color = DESHABILITADO;
            } else if (b.isContentAreaFilled() && esPrincipal(b)) {
                color = Color.WHITE;
            }
            g2.setColor(color);
            g2.drawString(text, textRect.x, textRect.y + fm.getAscent());
        }
    }

    // ---------- Tarjetas y paneles -----------------------------------------------------
    public static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bg;
        private final Color borde;

        public RoundedPanel(int radius, Color bg) {
            this(radius, bg, null);
        }

        public RoundedPanel(int radius, Color bg, Color borde) {
            this.radius = radius;
            this.bg = bg;
            this.borde = borde;
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

        @Override
        protected void paintChildren(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.clip(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
            super.paintChildren(g2);
            g2.dispose();
            if (borde != null) {
                Graphics2D gb = (Graphics2D) g.create();
                gb.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                gb.setColor(borde);
                gb.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, radius, radius));
                gb.dispose();
            }
        }
    }

    /** Tarjeta blanca con borde fino: contenedor estándar de tablas, árboles y resultados. */
    public static RoundedPanel tarjeta() {
        RoundedPanel t = new RoundedPanel(14, SUPERFICIE, BORDE);
        t.setLayout(new BorderLayout());
        t.setBorder(new EmptyBorder(1, 1, 1, 1));
        return t;
    }

    /** Raíz estándar de una ventana secundaria: fondo liso, franja verde superior y márgenes amplios. */
    public static JPanel raiz() {
        return raiz(28, 32, 28, 32);
    }

    /** Igual que {@link #raiz()} pero con márgenes propios (la pantalla de menú usa otros). */
    public static JPanel raiz(int arriba, int izquierda, int abajo, int derecha) {
        FondoConFranja root = new FondoConFranja();
        root.setBorder(new EmptyBorder(arriba, izquierda, abajo, derecha));
        return root;
    }

    /**
     * Fondo de ventana con una franja verde sutil en la parte superior: una línea de verde
     * hoja (distinto del oliva de las acciones, para que contraste) y un velo verde muy tenue
     * que se desvanece hacia el lienzo.
     */
    public static class FondoConFranja extends JPanel {
        private static final int ALTO_LINEA = 6;
        private static final int ALTO_VELO = 130;

        public FondoConFranja() {
            super(new BorderLayout());
            setBackground(LIENZO);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth();
            g2.setPaint(new GradientPaint(0, 0, FRANJA_VELO, 0, ALTO_VELO,
                    new Color(FRANJA_VELO.getRed(), FRANJA_VELO.getGreen(), FRANJA_VELO.getBlue(), 0)));
            g2.fillRect(0, 0, w, ALTO_VELO);
            g2.setColor(FRANJA);
            g2.fillRect(0, 0, w, ALTO_LINEA);
            g2.dispose();
        }
    }

    /** Título, subtítulo y, a la derecha, el botón de volver. */
    public static JPanel encabezado(String titulo, String subtitulo, JComponent accionDerecha) {
        JPanel fila = new JPanel(new BorderLayout());
        fila.setOpaque(false);
        fila.setBorder(new EmptyBorder(0, 0, 22, 0));

        JPanel bloque = new JPanel();
        bloque.setOpaque(false);
        bloque.setLayout(new BoxLayout(bloque, BoxLayout.Y_AXIS));
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(FONT_TITULO);
        lblTitulo.setForeground(TINTA);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        bloque.add(lblTitulo);
        if (subtitulo != null) {
            JLabel lblSub = new JLabel(subtitulo);
            lblSub.setFont(FONT_SUB);
            lblSub.setForeground(TINTA_SUAVE);
            lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);
            lblSub.setBorder(new EmptyBorder(3, 0, 0, 0));
            bloque.add(lblSub);
        }
        fila.add(bloque, BorderLayout.WEST);

        if (accionDerecha != null) {
            JPanel der = new JPanel(new GridBagLayout());
            der.setOpaque(false);
            der.add(accionDerecha);
            fila.add(der, BorderLayout.EAST);
        }
        return fila;
    }

    /** Botón "Volver" estándar de las ventanas secundarias. */
    public static RoundedButton botonVolver() {
        RoundedButton b = discreto("Volver", IconoVector.Tipo.VOLVER);
        b.setToolTipText("Volver al menú");
        return b;
    }

    /** Barra de acciones: grupo principal a la izquierda y herramientas a la derecha. */
    public static JPanel barraAcciones(JComponent[] izquierda, JComponent[] derecha) {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setOpaque(false);
        barra.setBorder(new EmptyBorder(0, 0, 16, 0));
        barra.add(grupo(izquierda), BorderLayout.WEST);
        barra.add(grupo(derecha), BorderLayout.EAST);
        return barra;
    }

    private static JPanel grupo(JComponent[] componentes) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        for (int i = 0; i < componentes.length; i++) {
            if (i > 0) {
                p.add(Box.createHorizontalStrut(8));
            }
            p.add(componentes[i]);
        }
        return p;
    }

    // ---------- Tablas -----------------------------------------------------------------
    /** Crea una tabla con el estilo estándar y un mensaje cuando no hay filas. */
    public static JTable crearTabla(TableModel modelo, String textoVacio) {
        JTable tabla = new JTable(modelo) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getRowCount() == 0 && textoVacio != null) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2.setFont(getFont());
                    g2.setColor(TINTA_SUAVE);
                    FontMetrics fm = g2.getFontMetrics();
                    Rectangle vis = getVisibleRect();
                    int x = vis.x + (vis.width - fm.stringWidth(textoVacio)) / 2;
                    int y = vis.y + vis.height / 2 + fm.getAscent() / 2;
                    g2.drawString(textoVacio, Math.max(vis.x + 16, x), y);
                    g2.dispose();
                }
            }
        };
        styleTable(tabla);
        return tabla;
    }

    /** Punto de color que acompaña a los estados (Libre, Ocupado, Pendiente, Atendida). */
    private static class Punto implements Icon {
        private final Color color;

        Punto(Color color) {
            this.color = color;
        }

        @Override public int getIconWidth()  { return 10; }
        @Override public int getIconHeight() { return 10; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillOval(x + 1, y + 1, 8, 8);
            g2.dispose();
        }
    }

    private static Color colorEstado(String estado) {
        return switch (estado.trim().toLowerCase(Locale.ROOT)) {
            case "libre", "atendida" -> EXITO;
            case "ocupado", "ocupada" -> ACENTO;
            case "pendiente" -> PENDIENTE;
            default -> TINTA_SUAVE;
        };
    }

    public static void styleTable(JTable tabla) {
        tabla.setFont(FONT_TABLA);
        tabla.setForeground(TINTA);
        tabla.setBackground(SUPERFICIE);
        tabla.setRowHeight(FILA_ALTO);
        tabla.setShowGrid(false);
        tabla.setShowHorizontalLines(true);
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(LINEA_FILA);
        tabla.setIntercellSpacing(new Dimension(0, 1));
        tabla.setSelectionBackground(OLIVA_TINTE);
        tabla.setSelectionForeground(TINTA);
        tabla.setFillsViewportHeight(true);

        JTableHeader header = tabla.getTableHeader();
        header.setFont(FONT_HEADER);
        header.setPreferredSize(new Dimension(header.getWidth(), HEADER_ALTO));
        header.setReorderingAllowed(false);
        header.setBackground(SUPERFICIE);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                             boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, false, false, row, column);
                setBackground(SUPERFICIE);
                setForeground(TINTA_SUAVE);
                setFont(table.getTableHeader().getFont());
                setHorizontalAlignment(SwingConstants.LEFT);
                setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, BORDE), new EmptyBorder(0, 16, 0, 16)));
                return this;
            }
        });

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                             boolean hasFocus, int row, int column) {
                Object mostrar = value;
                if (value instanceof Double || value instanceof Float) {
                    mostrar = String.format(Locale.US, "S/ %.2f", ((Number) value).doubleValue());
                }
                super.getTableCellRendererComponent(table, mostrar, isSelected, false, row, column);
                setBackground(isSelected ? OLIVA_TINTE : SUPERFICIE);
                setForeground(TINTA);
                setBorder(new EmptyBorder(0, 16, 0, 16));
                if (value != null && "Estado".equals(table.getColumnName(column))) {
                    String e = value.toString().trim();
                    if (!e.isEmpty()) {
                        setText(e.substring(0, 1).toUpperCase(Locale.ROOT) + e.substring(1).toLowerCase(Locale.ROOT));
                    }
                    setIcon(new Punto(colorEstado(value.toString())));
                    setIconTextGap(8);
                } else {
                    setIcon(null);
                }
                return this;
            }
        });
    }

    // ---------- Scroll -----------------------------------------------------------------
    /** Barra de desplazamiento delgada, sin flechas. */
    public static class BarraUI extends BasicScrollBarUI {
        public static ComponentUI createUI(JComponent c) {
            return new BarraUI();
        }

        private JButton sinBoton() {
            JButton b = new JButton();
            Dimension cero = new Dimension(0, 0);
            b.setPreferredSize(cero);
            b.setMinimumSize(cero);
            b.setMaximumSize(cero);
            return b;
        }

        @Override protected JButton createDecreaseButton(int orientation) { return sinBoton(); }
        @Override protected JButton createIncreaseButton(int orientation) { return sinBoton(); }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            // sin pista visible
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            if (r.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isThumbRollover() || isDragging ? TINTA_SUAVE : BORDE_FUERTE);
            g2.fill(new RoundRectangle2D.Float(r.x + 3, r.y + 3, r.width - 6, r.height - 6, 6, 6));
            g2.dispose();
        }

        @Override
        public Dimension getPreferredSize(JComponent c) {
            return new Dimension(12, 12);
        }
    }

    // ---------- Combo y spinner planos --------------------------------------------------
    private static BasicArrowButton flecha(int direccion, int ancho) {
        BasicArrowButton b = new BasicArrowButton(direccion, SUPERFICIE, SUPERFICIE, TINTA_SUAVE, SUPERFICIE);
        b.setBorder(new EmptyBorder(0, 0, 0, 0));
        b.setPreferredSize(new Dimension(ancho, 12));
        return b;
    }

    public static class ComboPlanoUI extends BasicComboBoxUI {
        public static ComponentUI createUI(JComponent c) {
            return new ComboPlanoUI();
        }

        @Override
        protected JButton createArrowButton() {
            return flecha(SwingConstants.SOUTH, 30);
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            g.setColor(SUPERFICIE);
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    public static class SpinnerPlanoUI extends BasicSpinnerUI {
        public static ComponentUI createUI(JComponent c) {
            return new SpinnerPlanoUI();
        }

        @Override
        protected Component createNextButton() {
            JComponent b = flecha(SwingConstants.NORTH, 20);
            installNextButtonListeners(b);
            return b;
        }

        @Override
        protected Component createPreviousButton() {
            JComponent b = flecha(SwingConstants.SOUTH, 20);
            installPreviousButtonListeners(b);
            return b;
        }
    }

    // ---------- Campos y formularios ---------------------------------------------------
    /** Borde redondeado de los campos; más grueso y oliva cuando el campo tiene el foco. */
    public static class CampoBorde extends AbstractBorder {
        private final Color color;
        private final float grosor;

        public CampoBorde(Color color, float grosor) {
            this.color = color;
            this.grosor = grosor;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(grosor));
            float d = grosor / 2f;
            g2.draw(new RoundRectangle2D.Float(x + d, y + d, width - grosor, height - grosor, 10, 10));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(9, 12, 9, 12);
        }
    }

    public static void estilarCampo(JTextField campo) {
        campo.setFont(FONT_CAMPO);
        campo.setForeground(TINTA);
        campo.setBackground(SUPERFICIE);
        campo.setCaretColor(OLIVA);
        campo.setBorder(new CampoBorde(BORDE_FUERTE, 1f));
        campo.setPreferredSize(new Dimension(campo.getPreferredSize().width, ALTO_CAMPO));
        campo.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                campo.setBorder(new CampoBorde(OLIVA, 2f));
            }
            @Override public void focusLost(FocusEvent e) {
                campo.setBorder(new CampoBorde(BORDE_FUERTE, 1f));
            }
        });
    }

    public static void estilarCampo(JSpinner spinner) {
        spinner.setFont(FONT_CAMPO);
        spinner.setBackground(SUPERFICIE);
        spinner.setBorder(new CampoBorde(BORDE_FUERTE, 1f));
        spinner.setPreferredSize(new Dimension(spinner.getPreferredSize().width, ALTO_CAMPO));
        if (spinner.getEditor() instanceof JSpinner.DefaultEditor ed) {
            ed.getTextField().setBorder(new EmptyBorder(0, 0, 0, 4));
            ed.getTextField().setHorizontalAlignment(SwingConstants.LEFT);
            ed.getTextField().setBackground(SUPERFICIE);
            ed.getTextField().setFont(FONT_CAMPO);
            ed.getTextField().setForeground(TINTA);
        }
    }

    public static void estilarCampo(JComboBox<?> combo) {
        combo.setFont(FONT_CAMPO);
        combo.setBackground(SUPERFICIE);
        combo.setForeground(TINTA);
        combo.setBorder(new CampoBorde(BORDE_FUERTE, 1f));
        combo.setPreferredSize(new Dimension(combo.getPreferredSize().width, ALTO_CAMPO));
    }

    public static JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(FONT_ETIQUETA);
        l.setForeground(TINTA_SUAVE);
        return l;
    }

    /** Mensaje de error en línea, bajo el formulario; reemplaza a las ventanas emergentes. */
    public static JLabel mensajeError() {
        JLabel l = new JLabel(" ");
        l.setFont(FONT_SUB);
        l.setForeground(PELIGRO);
        return l;
    }

    /**
     * Formulario con la etiqueta encima de cada campo. Cada fila es
     * {etiqueta, campo} o {etiqueta1, campo1, etiqueta2, campo2} para dos columnas.
     */
    public static JPanel formulario(Object[]... filas) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        int maxColumnas = 1;
        for (Object[] fila : filas) {
            maxColumnas = Math.max(maxColumnas, fila.length / 2);
        }
        for (int r = 0; r < filas.length; r++) {
            Object[] fila = filas[r];
            int columnas = fila.length / 2;
            for (int c = 0; c < columnas; c++) {
                gbc.gridx = c;
                gbc.gridwidth = (c == columnas - 1) ? maxColumnas - c : 1;
                gbc.insets = new Insets(r == 0 ? 0 : 16, c == 0 ? 0 : 12, 6, 0);
                gbc.gridy = r * 2;
                p.add(etiqueta((String) fila[c * 2]), gbc);
                gbc.insets = new Insets(0, c == 0 ? 0 : 12, 0, 0);
                gbc.gridy = r * 2 + 1;
                p.add((JComponent) fila[c * 2 + 1], gbc);
            }
        }
        return p;
    }

    /** Contenedor estándar de un diálogo: título arriba, contenido al centro, botones abajo. */
    public static JPanel panelDialogo(String titulo, String subtitulo) {
        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(SUPERFICIE);
        root.setBorder(new EmptyBorder(26, 30, 22, 30));
        JPanel bloque = new JPanel();
        bloque.setOpaque(false);
        bloque.setLayout(new BoxLayout(bloque, BoxLayout.Y_AXIS));
        JLabel t = new JLabel(titulo);
        t.setFont(new Font(FAMILIA, Font.BOLD, 19));
        t.setForeground(TINTA);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        bloque.add(t);
        if (subtitulo != null) {
            JLabel s = new JLabel(subtitulo);
            s.setFont(FONT_SUB);
            s.setForeground(TINTA_SUAVE);
            s.setAlignmentX(Component.LEFT_ALIGNMENT);
            s.setBorder(new EmptyBorder(3, 0, 0, 0));
            bloque.add(s);
        }
        root.add(bloque, BorderLayout.NORTH);
        return root;
    }

    /** Botones del pie de un diálogo, alineados a la derecha. */
    public static JPanel pieDialogo(JComponent... botones) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(Box.createHorizontalGlue());
        for (int i = 0; i < botones.length; i++) {
            if (i > 0) {
                p.add(Box.createHorizontalStrut(10));
            }
            p.add(botones[i]);
        }
        return p;
    }

    /** Ajusta el tamaño al contenido y lo centra sobre la ventana padre. */
    public static void ajustarDialogo(JDialog d, Component padre, int anchoMinimo) {
        d.setResizable(false);
        d.pack();
        d.setSize(Math.max(d.getWidth(), anchoMinimo), d.getHeight());
        d.setLocationRelativeTo(padre);
        d.getRootPane().registerKeyboardAction(e -> d.dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    // ---------- Escalado -----------------------------------------------------------------
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
                    b.setIconTextGap((int) Math.round(8 * escala));
                }
            }

            if (jc instanceof JTable t) {
                t.setRowHeight((int) Math.round(FILA_ALTO * escala));
                JTableHeader h = t.getTableHeader();
                h.setPreferredSize(new Dimension(h.getWidth(), (int) Math.round(HEADER_ALTO * escala)));
            }
        }
        if (c instanceof Container cont) {
            for (Component hijo : cont.getComponents()) {
                escalar(hijo, escala);
            }
        }
    }
}

package view;

import estructura.ArbolBinarioPlatos;
import estructura.NodoPlato;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import static view.UIStyle.*;

/** Ventana del árbol binario de la carta (Capítulo 8): dibujo del árbol y resultados de recorridos. */
public class ArbolPlatosView extends JFrame {

    public final JButton btnInsertar  = primario("Insertar", IconoVector.Tipo.AGREGAR);
    public final JButton btnBuscar    = secundario("Buscar", IconoVector.Tipo.BUSCAR);
    public final JButton btnEliminar  = peligro("Eliminar", IconoVector.Tipo.ELIMINAR);
    public final JButton btnPreorden  = discreto("Preorden", IconoVector.Tipo.ARBOL);
    public final JButton btnInorden   = discreto("Inorden", IconoVector.Tipo.ARBOL);
    public final JButton btnPostorden = discreto("Postorden", IconoVector.Tipo.ARBOL);
    public final JButton btnVolver    = botonVolver();

    public final JTextArea areaResultados = new JTextArea(5, 40);
    private final PanelArbol panelArbol = new PanelArbol();

    public ArbolPlatosView() {
        setTitle("Árbol Binario de la Carta - C&R OrderManager");
        setSize(940, 700);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = raiz();

        JPanel superior = new JPanel();
        superior.setOpaque(false);
        superior.setLayout(new BoxLayout(superior, BoxLayout.Y_AXIS));
        superior.add(encabezado("Árbol de la carta",
                "Platos por niveles y recorridos preorden, inorden y postorden", btnVolver));
        superior.add(barraAcciones(
                new JComponent[]{btnInsertar, btnBuscar, btnEliminar},
                new JComponent[]{btnPreorden, btnInorden, btnPostorden}));
        root.add(superior, BorderLayout.NORTH);

        JScrollPane scrollArbol = new JScrollPane(panelArbol);
        scrollArbol.setBorder(BorderFactory.createEmptyBorder());
        scrollArbol.getViewport().setBackground(SUPERFICIE);
        RoundedPanel cardArbol = tarjeta();
        cardArbol.add(scrollArbol, BorderLayout.CENTER);
        root.add(cardArbol, BorderLayout.CENTER);

        areaResultados.setEditable(false);
        areaResultados.setLineWrap(true);
        areaResultados.setWrapStyleWord(true);
        areaResultados.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        areaResultados.setForeground(TINTA);
        areaResultados.setBackground(SUPERFICIE);
        areaResultados.setBorder(new EmptyBorder(12, 16, 12, 16));
        JScrollPane scrollRes = new JScrollPane(areaResultados);
        scrollRes.setBorder(BorderFactory.createEmptyBorder());
        scrollRes.getViewport().setBackground(SUPERFICIE);
        RoundedPanel cardRes = tarjeta();
        cardRes.add(scrollRes, BorderLayout.CENTER);
        JPanel sur = new JPanel(new BorderLayout());
        sur.setOpaque(false);
        sur.setBorder(new EmptyBorder(16, 0, 0, 0));
        sur.add(cardRes, BorderLayout.CENTER);
        root.add(sur, BorderLayout.SOUTH);

        setContentPane(root);
        activarEscalado(this);
    }

    /** Redibuja el árbol; idResaltado = id del plato a destacar (o -1 si ninguno). */
    public void mostrarArbol(ArbolBinarioPlatos arbol, int idResaltado) {
        panelArbol.actualizar(arbol, idResaltado);
    }

    /** Panel que dibuja el árbol con nodos circulares y líneas hacia los hijos. */
    private static class PanelArbol extends JPanel {

        private static final int RADIO = 22;
        private static final int SEP_X = 64;
        private static final int SEP_Y = 78;

        private ArbolBinarioPlatos arbol;
        private int idResaltado = -1;
        private int contadorX;

        PanelArbol() {
            setBackground(Color.WHITE);
        }

        void actualizar(ArbolBinarioPlatos arbol, int idResaltado) {
            this.arbol = arbol;
            this.idResaltado = idResaltado;
            int ancho = Math.max(400, arbol.getCantidad() * SEP_X + 80);
            int alto = Math.max(200, arbol.altura() * SEP_Y + 60);
            setPreferredSize(new Dimension(ancho, alto));
            revalidate();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            if (arbol == null || arbol.estaVacio()) {
                g2.setColor(TINTA_SUAVE);
                g2.setFont(FONT_SUB);
                g2.drawString("El árbol está vacío.", 30, 40);
                g2.dispose();
                return;
            }
            contadorX = 0;
            dibujar(g2, arbol.getRaiz(), 0);
            g2.dispose();
        }

        /** Devuelve la x del nodo; la x se reparte en orden (inorden) para que no se crucen. */
        private int dibujar(Graphics2D g2, NodoPlato nodo, int nivel) {
            int xIzq = -1, xDer = -1;
            if (nodo.getIzquierdo() != null) {
                xIzq = dibujar(g2, nodo.getIzquierdo(), nivel + 1);
            }
            int x = 40 + contadorX * SEP_X;
            contadorX++;
            int y = 40 + nivel * SEP_Y;
            if (nodo.getDerecho() != null) {
                xDer = dibujar(g2, nodo.getDerecho(), nivel + 1);
            }

            g2.setColor(BORDE_FUERTE);
            g2.setStroke(new BasicStroke(1.6f));
            if (xIzq >= 0) {
                g2.drawLine(x, y, xIzq, y + SEP_Y);
            }
            if (xDer >= 0) {
                g2.drawLine(x, y, xDer, y + SEP_Y);
            }

            boolean resaltado = nodo.getPlato().getIdPlato() == idResaltado;
            g2.setColor(resaltado ? ACENTO : OLIVA);
            g2.fillOval(x - RADIO, y - RADIO, RADIO * 2, RADIO * 2);
            g2.setColor(Color.WHITE);
            g2.setFont(FONT_BOTON_B);
            String id = String.valueOf(nodo.getPlato().getIdPlato());
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(id, x - fm.stringWidth(id) / 2, y + fm.getAscent() / 2 - 1);

            g2.setColor(TINTA);
            g2.setFont(new Font(FAMILIA, Font.PLAIN, 11));
            String nombre = nodo.getPlato().getNombre();
            if (nombre.length() > 11) {
                nombre = nombre.substring(0, 10) + "…";
            }
            fm = g2.getFontMetrics();
            g2.drawString(nombre, x - fm.stringWidth(nombre) / 2, y + RADIO + 14);

            return x;
        }
    }
}

package view;

import estructura.ArbolBinarioPlatos;
import estructura.NodoPlato;

import javax.swing.*;
import java.awt.*;

import static view.UIStyle.*;

/** Ventana del árbol binario de la carta (Capítulo 8): dibujo del árbol y resultados de recorridos. */
public class ArbolPlatosView extends JFrame {

    public final JButton btnInsertar  = button("INSERTAR", VERDE_OLIVA, VERDE_HOVER);
    public final JButton btnBuscar    = button("BUSCAR", NARANJA, NARANJA_HOVER);
    public final JButton btnEliminar  = button("ELIMINAR", TERRACOTA, TERRACOTA_HOVER);
    public final JButton btnPreorden  = button("PREORDEN", MOSTAZA, MOSTAZA_HOVER);
    public final JButton btnInorden   = button("INORDEN", MOSTAZA, MOSTAZA_HOVER);
    public final JButton btnPostorden = button("POSTORDEN", MOSTAZA, MOSTAZA_HOVER);
    public final JButton btnVolver    = button("X", TERRACOTA, TERRACOTA_HOVER);

    public final JTextArea areaResultados = new JTextArea(5, 40);
    private final PanelArbol panelArbol = new PanelArbol();

    public ArbolPlatosView() {
        setTitle("Árbol Binario de la Carta - C&R OrderManager");
        setSize(900, 640);
        setMinimumSize(new Dimension(780, 560));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(FONDO_EXTERNO);
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        RoundedPanel card = new RoundedPanel(22, CREMA);
        card.setLayout(new BorderLayout(0, 12));

        JPanel headerOriginal = headerBar("Árbol Binario de la Carta",
                "Platos por niveles y recorridos preorden, inorden y postorden");
        JPanel headerWrapper = new JPanel(new BorderLayout());
        headerWrapper.setBackground(headerOriginal.getBackground());
        headerWrapper.add(headerOriginal, BorderLayout.WEST);
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rightPanel.setOpaque(false);
        btnVolver.setPreferredSize(new Dimension(60, 60));
        rightPanel.add(btnVolver);
        headerWrapper.add(rightPanel, BorderLayout.EAST);
        card.add(headerWrapper, BorderLayout.NORTH);

        JScrollPane scrollArbol = new JScrollPane(panelArbol);
        scrollArbol.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));
        scrollArbol.getViewport().setBackground(Color.WHITE);
        card.add(scrollArbol, BorderLayout.CENTER);

        areaResultados.setEditable(false);
        areaResultados.setLineWrap(true);
        areaResultados.setWrapStyleWord(true);
        areaResultados.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollRes = new JScrollPane(areaResultados);
        scrollRes.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        panelBotones.setBackground(CREMA);
        for (JButton b : new JButton[]{btnInsertar, btnBuscar, btnEliminar, btnPreorden, btnInorden, btnPostorden}) {
            b.setPreferredSize(new Dimension(125, 40));
            panelBotones.add(b);
        }

        JPanel sur = new JPanel(new BorderLayout(0, 8));
        sur.setBackground(CREMA);
        sur.add(scrollRes, BorderLayout.CENTER);
        sur.add(panelBotones, BorderLayout.SOUTH);
        card.add(sur, BorderLayout.SOUTH);

        root.add(card, BorderLayout.CENTER);
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

            if (arbol == null || arbol.estaVacio()) {
                g2.setColor(GRIS_TEXTO);
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

            g2.setColor(LINEA_CAMPO);
            g2.setStroke(new BasicStroke(1.6f));
            if (xIzq >= 0) {
                g2.drawLine(x, y, xIzq, y + SEP_Y);
            }
            if (xDer >= 0) {
                g2.drawLine(x, y, xDer, y + SEP_Y);
            }

            boolean resaltado = nodo.getPlato().getIdPlato() == idResaltado;
            g2.setColor(resaltado ? NARANJA : VERDE_OLIVA);
            g2.fillOval(x - RADIO, y - RADIO, RADIO * 2, RADIO * 2);
            g2.setColor(Color.WHITE);
            g2.setFont(FONT_BOTON);
            String id = String.valueOf(nodo.getPlato().getIdPlato());
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(id, x - fm.stringWidth(id) / 2, y + fm.getAscent() / 2 - 1);

            g2.setColor(TEXTO_OSCURO);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
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

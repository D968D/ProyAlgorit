package view;

import javax.swing.*;
import java.awt.*;

import static view.UIStyle.*;

/** Panel reutilizable que dibuja cualquier árbol binario (nodos circulares y líneas a los hijos). */
public class PanelArbolGenerico extends JPanel {

    /** Lo que el panel necesita saber de cada nodo para dibujarlo. */
    public interface Dibujable {
        String id();          // texto dentro del círculo
        String nombre();      // texto debajo del círculo
        String extra();       // segunda línea debajo (puede ser null)
        Dibujable izquierdo();
        Dibujable derecho();
    }

    private static final int RADIO = 22;
    private static final int SEP_X = 64;
    private static final int SEP_Y = 82;

    private Dibujable raiz;
    private String idResaltado = null;
    private int contadorX;

    public PanelArbolGenerico() {
        setBackground(SUPERFICIE);
    }

    public void actualizar(Dibujable raiz, String idResaltado) {
        this.raiz = raiz;
        this.idResaltado = idResaltado;
        int ancho = Math.max(400, contar(raiz) * SEP_X + 80);
        int alto = Math.max(200, altura(raiz) * SEP_Y + 60);
        setPreferredSize(new Dimension(ancho, alto));
        revalidate();
        repaint();
    }

    private int contar(Dibujable n) {
        return n == null ? 0 : 1 + contar(n.izquierdo()) + contar(n.derecho());
    }

    private int altura(Dibujable n) {
        return n == null ? 0 : 1 + Math.max(altura(n.izquierdo()), altura(n.derecho()));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        if (raiz == null) {
            g2.setColor(TINTA_SUAVE);
            g2.setFont(FONT_SUB);
            g2.drawString("El árbol está vacío.", 30, 40);
            g2.dispose();
            return;
        }
        contadorX = 0;
        dibujar(g2, raiz, 0);
        g2.dispose();
    }

    private int dibujar(Graphics2D g2, Dibujable nodo, int nivel) {
        int xIzq = -1, xDer = -1;
        if (nodo.izquierdo() != null) {
            xIzq = dibujar(g2, nodo.izquierdo(), nivel + 1);
        }
        int x = 40 + contadorX * SEP_X;
        contadorX++;
        int y = 40 + nivel * SEP_Y;
        if (nodo.derecho() != null) {
            xDer = dibujar(g2, nodo.derecho(), nivel + 1);
        }

        g2.setColor(BORDE_FUERTE);
        g2.setStroke(new BasicStroke(1.6f));
        if (xIzq >= 0) g2.drawLine(x, y, xIzq, y + SEP_Y);
        if (xDer >= 0) g2.drawLine(x, y, xDer, y + SEP_Y);

        boolean resaltado = idResaltado != null && idResaltado.equals(nodo.id());
        g2.setColor(resaltado ? ACENTO : OLIVA);
        g2.fillOval(x - RADIO, y - RADIO, RADIO * 2, RADIO * 2);
        g2.setColor(Color.WHITE);
        g2.setFont(FONT_BOTON_B);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(nodo.id(), x - fm.stringWidth(nodo.id()) / 2, y + fm.getAscent() / 2 - 1);

        g2.setColor(TINTA);
        g2.setFont(new Font(FAMILIA, Font.PLAIN, 11));
        fm = g2.getFontMetrics();
        String nombre = nodo.nombre();
        if (nombre.length() > 11) nombre = nombre.substring(0, 10) + "…";
        g2.drawString(nombre, x - fm.stringWidth(nombre) / 2, y + RADIO + 14);
        if (nodo.extra() != null) {
            g2.setColor(TINTA_SUAVE);
            g2.drawString(nodo.extra(), x - fm.stringWidth(nodo.extra()) / 2, y + RADIO + 27);
        }
        return x;
    }
}

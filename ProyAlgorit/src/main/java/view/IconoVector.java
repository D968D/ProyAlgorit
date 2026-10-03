package view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;


public class IconoVector implements Icon {

    public enum Tipo {
        MESA, PLATO, RESERVA, SALIR,
        AGREGAR, ELIMINAR, BUSCAR, ORDENAR, HISTORIAL
    }

    private final Tipo tipo;
    private final int size;
    private final Color color;

    public IconoVector(Tipo tipo, int size, Color color) {
        this.tipo = tipo;
        this.size = size;
        this.color = color;
    }

    public IconoVector(Tipo tipo, int size) {
        this(tipo, size, Color.WHITE);
    }

    public Tipo getTipo()   { return tipo; }
    public Color getColor() { return color; }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.translate(x, y);
        g2.scale(size / 24.0, size / 24.0);   // todos los dibujos están pensados en una grilla de 24x24
        g2.setColor(color);
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        switch (tipo) {
            case MESA -> {
                g2.draw(new RoundRectangle2D.Float(2, 6, 20, 5, 3, 3));   // tablero
                g2.draw(new Line2D.Float(5, 11, 5, 20));                  // patas
                g2.draw(new Line2D.Float(19, 11, 19, 20));
            }
            case PLATO -> {
                g2.draw(new Ellipse2D.Float(2.5f, 2.5f, 19, 19));         // plato
                g2.draw(new Ellipse2D.Float(7, 7, 10, 10));               // centro
            }
            case RESERVA -> {
                g2.draw(new RoundRectangle2D.Float(3, 5, 18, 16, 4, 4));  // calendario
                g2.draw(new Line2D.Float(3, 10, 21, 10));
                g2.draw(new Line2D.Float(8, 2.5f, 8, 7));                 // anillas
                g2.draw(new Line2D.Float(16, 2.5f, 16, 7));
                Path2D check = new Path2D.Float();                        // visto
                check.moveTo(8, 15.5);
                check.lineTo(11, 18);
                check.lineTo(16, 13);
                g2.draw(check);
            }
            case SALIR -> {
                Path2D puerta = new Path2D.Float();                       // marco de la puerta
                puerta.moveTo(10, 3);
                puerta.lineTo(4, 3);
                puerta.lineTo(4, 21);
                puerta.lineTo(10, 21);
                g2.draw(puerta);
                g2.draw(new Line2D.Float(10, 12, 21, 12));                // flecha
                Path2D punta = new Path2D.Float();
                punta.moveTo(16.5, 7.5);
                punta.lineTo(21, 12);
                punta.lineTo(16.5, 16.5);
                g2.draw(punta);
            }
            case AGREGAR -> {
                g2.draw(new Line2D.Float(12, 4, 12, 20));
                g2.draw(new Line2D.Float(4, 12, 20, 12));
            }
            case ELIMINAR -> {
                g2.draw(new Line2D.Float(3, 6, 21, 6));                   // tapa
                g2.draw(new Line2D.Float(9, 3, 15, 3));
                Path2D tacho = new Path2D.Float();
                tacho.moveTo(5.5, 6);
                tacho.lineTo(7, 21);
                tacho.lineTo(17, 21);
                tacho.lineTo(18.5, 6);
                g2.draw(tacho);
                g2.draw(new Line2D.Float(10, 10, 10, 17));
                g2.draw(new Line2D.Float(14, 10, 14, 17));
            }
            case BUSCAR -> {
                g2.draw(new Ellipse2D.Float(3, 3, 14, 14));               // lupa
                g2.draw(new Line2D.Float(15, 15, 21, 21));
            }
            case ORDENAR -> {
                g2.draw(new Line2D.Float(3, 6, 21, 6));                   // barras de distinta longitud
                g2.draw(new Line2D.Float(3, 12, 16, 12));
                g2.draw(new Line2D.Float(3, 18, 11, 18));
            }
            case HISTORIAL -> {
                g2.draw(new Ellipse2D.Float(3, 3, 18, 18));               // reloj
                Path2D manecillas = new Path2D.Float();
                manecillas.moveTo(12, 7);
                manecillas.lineTo(12, 12);
                manecillas.lineTo(15.5, 14);
                g2.draw(manecillas);
            }
        }
        g2.dispose();
    }
}

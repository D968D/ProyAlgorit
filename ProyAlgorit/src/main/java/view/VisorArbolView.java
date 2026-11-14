package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import static view.UIStyle.*;

/** Ventana común para el ABB y el AVL: árbol dibujado, operaciones y área de resultados. */
public class VisorArbolView extends JFrame {

    public final JButton btnInsertar  = primario("Insertar", IconoVector.Tipo.AGREGAR);
    public final JButton btnBuscar    = secundario("Buscar", IconoVector.Tipo.BUSCAR);
    public final JButton btnEliminar  = peligro("Eliminar", IconoVector.Tipo.ELIMINAR);
    public final JButton btnPreorden  = discreto("Preorden", IconoVector.Tipo.ARBOL);
    public final JButton btnInorden   = discreto("Inorden", IconoVector.Tipo.ARBOL);
    public final JButton btnPostorden = discreto("Postorden", IconoVector.Tipo.ARBOL);
    public final JButton btnDemo      = discreto("Demo", IconoVector.Tipo.DEMO);
    public final JButton btnReal      = discreto("Datos BD", IconoVector.Tipo.DATOS);
    public final JButton btnVolver    = botonVolver();

    public final JTextArea areaResultados = new JTextArea(5, 40);
    private final PanelArbolGenerico panel = new PanelArbolGenerico();

    /** conDemo = true agrega los botones Demo y Datos BD (se usan en el AVL). */
    public VisorArbolView(String titulo, String subtitulo, boolean conDemo) {
        setTitle(titulo + " - C&R OrderManager");
        setSize(conDemo ? 1120 : 940, 740);
        setMinimumSize(new Dimension(conDemo ? 1060 : 900, 620));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = raiz();

        java.util.List<JComponent> izquierda = new java.util.ArrayList<>(java.util.List.of(
                btnInsertar, btnBuscar, btnEliminar));
        if (conDemo) {
            izquierda.add(btnDemo);
            izquierda.add(btnReal);
        }
        JPanel superior = new JPanel();
        superior.setOpaque(false);
        superior.setLayout(new BoxLayout(superior, BoxLayout.Y_AXIS));
        superior.add(encabezado(titulo, subtitulo, btnVolver));
        superior.add(barraAcciones(
                izquierda.toArray(new JComponent[0]),
                new JComponent[]{btnPreorden, btnInorden, btnPostorden}));
        root.add(superior, BorderLayout.NORTH);

        JScrollPane scrollArbol = new JScrollPane(panel);
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

    public void mostrar(PanelArbolGenerico.Dibujable raiz, String idResaltado) {
        panel.actualizar(raiz, idResaltado);
    }
}

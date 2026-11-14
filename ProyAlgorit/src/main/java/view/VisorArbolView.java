package view;

import javax.swing.*;
import java.awt.*;

import static view.UIStyle.*;

/** Ventana común para el ABB y el AVL: árbol dibujado, botones de operaciones y área de resultados. */
public class VisorArbolView extends JFrame {

    public final JButton btnInsertar  = button("INSERTAR", VERDE_OLIVA, VERDE_HOVER);
    public final JButton btnBuscar    = button("BUSCAR", NARANJA, NARANJA_HOVER);
    public final JButton btnEliminar  = button("ELIMINAR", TERRACOTA, TERRACOTA_HOVER);
    public final JButton btnPreorden  = button("PREORDEN", MOSTAZA, MOSTAZA_HOVER);
    public final JButton btnInorden   = button("INORDEN", MOSTAZA, MOSTAZA_HOVER);
    public final JButton btnPostorden = button("POSTORDEN", MOSTAZA, MOSTAZA_HOVER);
    public final JButton btnDemo      = button("DEMO", ROYAL_BLUE, ROYAL_BLUE_HOVER);
    public final JButton btnReal      = button("DATOS BD", TAUPE, TAUPE_HOVER);
    public final JButton btnVolver    = button("X", TERRACOTA, TERRACOTA_HOVER);

    public final JTextArea areaResultados = new JTextArea(6, 40);
    private final PanelArbolGenerico panel = new PanelArbolGenerico();

    /** conDemo = true agrega los botones DEMO y DATOS BD (se usan en el AVL). */
    public VisorArbolView(String titulo, String subtitulo, boolean conDemo) {
        setTitle(titulo + " - C&R OrderManager");
        setSize(960, 700);
        setMinimumSize(new Dimension(820, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(FONDO_EXTERNO);
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        RoundedPanel card = new RoundedPanel(22, CREMA);
        card.setLayout(new BorderLayout(0, 12));

        JPanel headerOriginal = headerBar(titulo, subtitulo);
        JPanel headerWrapper = new JPanel(new BorderLayout());
        headerWrapper.setBackground(headerOriginal.getBackground());
        headerWrapper.add(headerOriginal, BorderLayout.WEST);
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rightPanel.setOpaque(false);
        btnVolver.setPreferredSize(new Dimension(60, 60));
        rightPanel.add(btnVolver);
        headerWrapper.add(rightPanel, BorderLayout.EAST);
        card.add(headerWrapper, BorderLayout.NORTH);

        JScrollPane scrollArbol = new JScrollPane(panel);
        scrollArbol.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));
        scrollArbol.getViewport().setBackground(Color.WHITE);
        card.add(scrollArbol, BorderLayout.CENTER);

        areaResultados.setEditable(false);
        areaResultados.setLineWrap(true);
        areaResultados.setWrapStyleWord(true);
        areaResultados.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollRes = new JScrollPane(areaResultados);
        scrollRes.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        botones.setBackground(CREMA);
        java.util.List<JButton> lista = new java.util.ArrayList<>(java.util.List.of(
                btnInsertar, btnBuscar, btnEliminar, btnPreorden, btnInorden, btnPostorden));
        if (conDemo) {
            lista.add(btnDemo);
            lista.add(btnReal);
        }
        for (JButton b : lista) {
            b.setPreferredSize(new Dimension(115, 40));
            botones.add(b);
        }

        JPanel sur = new JPanel(new BorderLayout(0, 8));
        sur.setBackground(CREMA);
        sur.add(scrollRes, BorderLayout.CENTER);
        sur.add(botones, BorderLayout.SOUTH);
        card.add(sur, BorderLayout.SOUTH);

        root.add(card, BorderLayout.CENTER);
        setContentPane(root);
        activarEscalado(this);
    }

    public void mostrar(PanelArbolGenerico.Dibujable raiz, String idResaltado) {
        panel.actualizar(raiz, idResaltado);
    }
}

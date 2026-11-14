package view;

import javax.swing.*;
import java.awt.*;

import static view.UIStyle.*;

public class EstadoDialog extends JDialog {

    public final JButton btnLibre   = secundario("Libre", null);
    public final JButton btnOcupado = secundario("Ocupado", null);
    private String estadoSeleccionado = null;

    public EstadoDialog(JFrame padre) {
        super(padre, "Actualizar estado de mesa", true); // true = modal

        JPanel root = panelDialogo("Estado de la mesa", "Elige cómo queda la mesa ahora");
        JPanel opciones = new JPanel(new GridLayout(1, 2, 12, 0));
        opciones.setOpaque(false);
        opciones.add(btnLibre);
        opciones.add(btnOcupado);
        root.add(opciones, BorderLayout.CENTER);
        setContentPane(root);

        btnLibre.addActionListener(e -> {
            estadoSeleccionado = "LIBRE";
            dispose();
        });
        btnOcupado.addActionListener(e -> {
            estadoSeleccionado = "OCUPADO";
            dispose();
        });
        ajustarDialogo(this, padre, 380);
    }

    // Devuelve "LIBRE", "OCUPADO", o null si se cerro sin elegir
    public String getEstadoSeleccionado() {
        return estadoSeleccionado;
    }
}

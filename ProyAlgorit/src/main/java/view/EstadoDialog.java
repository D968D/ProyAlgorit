package view;

import javax.swing.*;

import static view.UIStyle.CREMA;
import static view.UIStyle.ROYAL_BLUE;
import static view.UIStyle.ROYAL_BLUE_HOVER;
import static view.UIStyle.button;

import java.awt.*;

public class EstadoDialog extends JDialog {

    public final JButton btnLibre     = button("LIBRE", ROYAL_BLUE_HOVER, ROYAL_BLUE);
    public final JButton btnOcupado   = button("OCUPADO", ROYAL_BLUE_HOVER, ROYAL_BLUE);
    private String estadoSeleccionado = null;

    public EstadoDialog(JFrame padre) {
        super(padre, "Actualizar estado de mesa", true); // true = modal
        setSize(300, 150);
        setLocationRelativeTo(padre);
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 40));
        getContentPane().setBackground(CREMA);
        add(btnLibre);
        add(btnOcupado);

        btnLibre.addActionListener(e -> {
            estadoSeleccionado = "LIBRE";
            dispose();
        });
        btnOcupado.addActionListener(e -> {
            estadoSeleccionado = "OCUPADO";
            dispose();
        });
    }

    // Devuelve "LIBRE", "OCUPADO", o null si se cerro sin elegir
    public String getEstadoSeleccionado() {
        return estadoSeleccionado;
    }
}

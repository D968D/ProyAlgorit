package view;

import javax.swing.*;
import java.awt.*;

public class EstadoDialog extends JDialog {

    public final JButton btnLibre = new JButton("LIBRE");
    public final JButton btnOcupado = new JButton("OCUPADO");
    private String estadoSeleccionado = null;

    public EstadoDialog(JFrame padre) {
        super(padre, "Actualizar estado de mesa", true); // true = modal
        setSize(300, 150);
        setLocationRelativeTo(padre);
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 40));

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

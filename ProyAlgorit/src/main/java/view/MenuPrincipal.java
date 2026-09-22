package view;

import controller.MesaController;
import controller.PlatoController;

import javax.swing.*;
import java.awt.*;

public class MenuPrincipal extends JFrame {

    public MenuPrincipal() {
        setTitle("C&R OrderManager - Menú Principal");
        setSize(420, 260);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel titulo = new JLabel("Cordon y la Rosa", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        JPanel panelBotones = new JPanel(new GridLayout(3, 1, 10, 12));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 60, 30, 60));

        JButton btnMesas  = new JButton("Gestión de Mesas");
        JButton btnPlatos = new JButton("Gestión de Platos");
        JButton btnSalir  = new JButton("Salir");

        btnMesas.setPreferredSize(new Dimension(200, 40));
        btnPlatos.setPreferredSize(new Dimension(200, 40));

        btnMesas.addActionListener(e -> {
            MesaView mesaView = new MesaView();
            new MesaController(mesaView);
        });

        btnPlatos.addActionListener(e -> {
            PlatoView platoView = new PlatoView();
            new PlatoController(platoView);
        });

        btnSalir.addActionListener(e -> System.exit(0));

        panelBotones.add(btnMesas);
        panelBotones.add(btnPlatos);
        panelBotones.add(btnSalir);

        add(titulo, BorderLayout.NORTH);
        add(panelBotones, BorderLayout.CENTER);
    }
}

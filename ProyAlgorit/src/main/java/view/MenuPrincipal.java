package view;

import controller.MesaController;
import controller.PlatoController;

import javax.swing.*;
import java.awt.*;

import static view.UIStyle.*;

public class MenuPrincipal extends JFrame {

    public MenuPrincipal() {
        setTitle("C&R OrderManager - Menú Principal");
        setSize(440, 420);
        setMinimumSize(new Dimension(400, 380));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(FONDO_EXTERNO);
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        RoundedPanel card = new RoundedPanel(22, CREMA);
        card.setLayout(new BorderLayout());
        card.add(headerBar("EL CORDÓN Y LA ROSA", "Sistema de Gestión de Pedidos · Ica, Perú"), BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new GridLayout(3, 1, 0, 16));
        panelBotones.setBackground(CREMA);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(32, 50, 34, 50));

        RoundedButton btnMesas  = button("GESTIÓN DE MESAS", VERDE_OLIVA, VERDE_HOVER);
        RoundedButton btnPlatos = button("GESTIÓN DE PLATOS", NARANJA, NARANJA_HOVER);
        RoundedButton btnSalir  = button("SALIR", TAUPE, TAUPE_HOVER);

        for (JButton b : new JButton[]{btnMesas, btnPlatos, btnSalir}) {
            b.setPreferredSize(new Dimension(220, 46));
        }

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

        card.add(panelBotones, BorderLayout.CENTER);
        root.add(card, BorderLayout.CENTER);
        setContentPane(root);
    }
}
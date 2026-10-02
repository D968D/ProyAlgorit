package view;

import controller.MesaController;
import controller.PlatoController;

import javax.swing.*;
import java.awt.*;

import static view.UIStyle.*;

public class MenuPrincipal extends JFrame {

    public MenuPrincipal() {
        setTitle("C&R OrderManager - Menú Principal");
        setSize(440, 500);
        setMinimumSize(new Dimension(400, 460));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(FONDO_EXTERNO);
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        RoundedPanel card = new RoundedPanel(22, CREMA);
        card.setLayout(new BorderLayout());
        card.add(headerBar("EL CORDÓN Y LA ROSA", "Sistema de Gestión de Pedidos · Ica, Perú"), BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new GridLayout(4, 1, 0, 16));
        panelBotones.setBackground(CREMA);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(32, 50, 34, 50));

        RoundedButton btnMesas    = button("GESTIÓN DE MESAS", IconoVector.Tipo.MESA, VERDE_OLIVA, VERDE_HOVER);
        RoundedButton btnPlatos   = button("GESTIÓN DE PLATOS", IconoVector.Tipo.PLATO, NARANJA, NARANJA_HOVER);
        RoundedButton btnReservas = button("GESTIÓN DE RESERVAS", IconoVector.Tipo.RESERVA, TERRACOTA, TERRACOTA_HOVER);
        RoundedButton btnSalir    = button("SALIR", IconoVector.Tipo.SALIR, TAUPE, TAUPE_HOVER);

        for (JButton b : new JButton[]{btnMesas, btnPlatos, btnReservas, btnSalir}) {
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

        btnReservas.addActionListener(e -> new ReservaView().setVisible(true));

        btnSalir.addActionListener(e -> System.exit(0));

        panelBotones.add(btnMesas);
        panelBotones.add(btnPlatos);
        panelBotones.add(btnReservas);
        panelBotones.add(btnSalir);

        card.add(panelBotones, BorderLayout.CENTER);
        root.add(card, BorderLayout.CENTER);
        setContentPane(root);
        activarEscalado(this);
    }
}
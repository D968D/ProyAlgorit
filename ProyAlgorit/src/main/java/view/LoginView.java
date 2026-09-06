package view;

import controller.MesaController;
import dao.LoginDAO;

import javax.swing.*;
import java.awt.*;


public class LoginView extends JFrame {

    private final JTextField txtUsuario = new JTextField(15);
    private final JPasswordField txtContraseña = new JPasswordField(15);
    private final JButton btnIngresar = new JButton("Ingresar");
    private final LoginDAO loginDAO = new LoginDAO();

    public LoginView() {
        setTitle("Iniciar sesion - C&R OrderManager");
        setSize(340, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1;
        add(txtUsuario, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1;
        add(txtContraseña, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(btnIngresar, gbc);

        btnIngresar.addActionListener(e -> intentarLogin());
    }

    private void intentarLogin() {
        String usuario = txtUsuario.getText().trim();
        String contra = new String(txtContraseña.getPassword());

        if (usuario.isEmpty() || contra.isEmpty()) {
            JOptionPane.showMessageDialog(this, "administrador 1234.");
            return;
        }

        boolean valido = loginDAO.validar(usuario, contra);

        if (valido) {
            dispose();
            MesaView mesaView = new MesaView();
            new MesaController(mesaView);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Usuario o contraseña incorrectos.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

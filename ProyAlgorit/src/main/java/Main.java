import view.LoginView;
import view.UIStyle;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            UIStyle.instalarTema();
            LoginView login = new LoginView();
            login.setVisible(true);
        });
    }
}

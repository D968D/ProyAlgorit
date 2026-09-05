import view.MesaView;
import controller.MesaController;
public class Main {
    public static void main(String[] args) {
        MesaView mesaView = new MesaView();
        new MesaController(mesaView);
    }
}
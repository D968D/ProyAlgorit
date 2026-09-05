import view.FrmMesas;
import controller.MesaController;
public class Main {
    public static void main(String[] args) {
        FrmMesas mesaView = new FrmMesas();
        new MesaController(mesaView);
    }
}
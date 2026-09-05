package controller;
import java.util.List;
import model.Mesa;
import view.FrmMesas;
import view.FrmMesas;
import dao.MesaDAO;
public class MesaController {
    private final Mesa[] mesas = new Mesa[10];
    private MesaDAO dao = new MesaDAO();
    private int cantidad=0;
    private FrmMesas view;

    public MesaController() {
    }
    public MesaController(FrmMesas view){
        this.view = view;
        view.setVisible(true);
    }

    public boolean actualizar(Mesa mesa){
        return false;//cambiar
    }

    public boolean insertar(Mesa mesa){
        return false;//cambiar
    }
    public List<Mesa> listarTodas(){

        return null;
    }
    public Mesa buscarPorNumero(int numero){
        return null;
    }
}

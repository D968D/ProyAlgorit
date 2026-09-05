package controller;
import java.util.List;
import model.Mesa;
import dao.MesaDAO;
public class MesaController {
    private final MesaDAO dao = new MesaDAO();

    public MesaController() {
    }

    public boolean actualizar(Mesa mesa){
        return dao.actualizar(mesa);
    }

    public boolean insertar(Mesa mesa){
        return dao.insertar(mesa);
    }
    public List<Mesa> listarTodas(){
        return dao.listarTodas();
    }
    public Mesa buscarPorNumero(int numero){
        return dao.buscarPorNumero(numero);
    }
}

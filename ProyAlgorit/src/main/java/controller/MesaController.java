package controller;

import model.Mesa;
import view.FrmMesas;
import view.FrmMesas;

public class MesaController {
    private final Mesa[] mesas = new Mesa[10];
    private int cantidad=0;
    private FrmMesas view;

    public MesaController(FrmMesas view){
        this.view = view;
        view.setVisible(true);
    }

    private void actualizar(){

    }

    private void libre(){

    }

    private void ocupado(){

    }
}

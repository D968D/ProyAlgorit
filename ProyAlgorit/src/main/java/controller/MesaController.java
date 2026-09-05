package controller;

import model.Mesa;
import view.MesaView;

public class MesaController {
    private final Mesa[] mesas = new Mesa[10];
    private int cantidad=0;
    private MesaView view;

    public MesaController(MesaView view){
        this.view = view;
        view.btnActualizar.addActionListener(e->actualizar());
        view.btnLibre.addActionListener(e->libre());
        view.btnOcupado.addActionListener(e->ocupado());
        view.setVisible(true);
    }

    private void actualizar(){

    }

    private void libre(){

    }

    private void ocupado(){

    }
}

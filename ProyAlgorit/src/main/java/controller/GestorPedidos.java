package controller;

import estructuras.ListaEnlazadaSimple;
import estructuras.Pila;
import model.Pedido;

public class GestorPedidos {
    private ListaEnlazadaSimple<Pedido> pedidosActivos;
    private Pila<Pedido> historialOperaciones;            

    public GestorPedidos() {
        pedidosActivos = new ListaEnlazadaSimple<>();
        historialOperaciones = new Pila<>();
    }

    public void registrarPedido(Pedido p) {
        pedidosActivos.insertarFinal(p);
        historialOperaciones.push(p); 
    }


    public Pedido deshacerUltimaOperacion() {
        return historialOperaciones.pop();
    }

    public ListaEnlazadaSimple<Pedido> getPedidosActivos() {
        return pedidosActivos;
    }

    
}

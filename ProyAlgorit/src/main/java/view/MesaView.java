package view;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
public class MesaView extends JFrame {

    public JButton btnActualizar = new JButton();
    public JButton btnLibre = new JButton();
    public JButton btnOcupado = new JButton();

    public DefaultTableModel modeloTabla = new DefaultTableModel(new String[]{"ID","Capacidad","Estado"},0);

    public JTable tabla = new JTable();
    public MesaView(){
        setTitle("MESAS");
        setSize(600,400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel botones = new JPanel();

        botones.add(btnActualizar);

        add(new JScrollPane(tabla),BorderLayout.NORTH);
        add(botones,BorderLayout.SOUTH);
    }
    public MesaView(int id){
        setTitle("ACTUALIZAR MESA");
        setSize(600,400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel botones = new JPanel();

        botones.add(btnLibre);
        botones.add(btnOcupado);
        add(botones,BorderLayout.CENTER);
    }
}

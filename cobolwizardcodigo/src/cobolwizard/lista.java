package cobolwizard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

public class lista extends JFrame{
	JButton aceptar;
	JButton cancelar;
	JButton elegir;
	JButton deselegir;
	selectfile ventana1;
	JList ficheros_seleccionados;
	JList ficheros_disponibles;
	JScrollPane listapanel_disponibles;
	JScrollPane listapanel_seleccionados;
	JPanel botones;
	JPanel botones_seleccion;
	DefaultListModel listmodel;
	
	public lista(String[] ls, selectfile ventana){
		ventana1=ventana;
		initcomponentes(ls);
	}
	
	public void initcomponentes(String[] list){
		setTitle("Ficheros disponibles");
		setResizable(false);
		setBounds(200,200,500,430);
		setLayout(null);
		listapanel_disponibles = new JScrollPane();
		listapanel_seleccionados = new JScrollPane();
		botones = new JPanel();
		botones_seleccion = new JPanel();
		aceptar = new JButton("Aceptar");
		cancelar = new JButton("Cancelar");
		elegir = new JButton(">>");
		deselegir = new JButton("<<");
		listmodel = new DefaultListModel();
		for(int x=0;x<list.length;x++){
			listmodel.addElement(list[x]);;
		}
		
		ficheros_disponibles = new JList(listmodel);
		ficheros_seleccionados = new JList();
		
		//listapanel_disponibles.setBorder(BorderFactory.createLineBorder(Color.black));
		listapanel_disponibles.setBounds(10, 10, 215, 290);
		listapanel_disponibles.setViewportView(ficheros_disponibles);
		getContentPane().add(listapanel_disponibles);
		
		//listapanel_seleccionados.setBorder(BorderFactory.createLineBorder(Color.blue));
		listapanel_seleccionados.setBounds(275, 10, 215, 290);
		listapanel_seleccionados.setViewportView(ficheros_seleccionados);
		getContentPane().add(listapanel_seleccionados);
		
		//botones_seleccion.setBorder(BorderFactory.createLineBorder(Color.yellow));
		botones_seleccion.setLayout(new FlowLayout());
		botones_seleccion.setBounds(225, 80, 50, 150);
		botones_seleccion.add(elegir);
		botones_seleccion.add(deselegir);
		getContentPane().add(botones_seleccion);
		
		//botones.setBorder(BorderFactory.createLineBorder(Color.red));
		botones.setLayout(new FlowLayout());
		botones.add(aceptar);
		botones.add(cancelar);
		botones.setBounds(250,350,180 ,50 );
		getContentPane().add(botones);
		
		ControlaEventosLista controlaEventosLista =new ControlaEventosLista (this,ventana1);
		aceptar.addActionListener(controlaEventosLista);
		cancelar.addActionListener(controlaEventosLista);
		elegir.addActionListener(controlaEventosLista);
		deselegir.addActionListener(controlaEventosLista);	
	}
}

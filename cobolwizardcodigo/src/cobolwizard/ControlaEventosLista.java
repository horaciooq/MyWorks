package cobolwizard;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

public class ControlaEventosLista implements ActionListener{
	
	lista ventana;
	selectfile ventana1;
	DefaultListModel listData;
	
		public  ControlaEventosLista(lista objeto, selectfile ventanauno){
		ventana = objeto;
		ventana1=ventanauno;
		listData = new DefaultListModel();
	}
	
	@Override
	public void actionPerformed(ActionEvent evento) {
		
		if (evento.getSource()==ventana.elegir){
			List elemento = ventana.ficheros_disponibles.getSelectedValuesList();
			if( elemento != null )
			{
				for(int x=0;x<elemento.size();x++){
					listData.addElement( elemento.get(x) );
					ventana.ficheros_seleccionados.setModel(listData);
				}
				int[] indices= ventana.ficheros_disponibles.getSelectedIndices();
				for(int x=indices.length-1;x>-1;x--){
					ventana.listmodel.remove(indices[x]);
				}
				ventana.ficheros_disponibles.setModel(ventana.listmodel);
			}
		}
		if(evento.getSource()==ventana.deselegir){
			List elemento1 = ventana.ficheros_seleccionados.getSelectedValuesList();
			if(elemento1!=null){
				for(int x=0;x<elemento1.size();x++){
					ventana.listmodel.addElement(elemento1.get(x));
					ventana.ficheros_disponibles.setModel(ventana.listmodel);
				}
				int[] indices1 = ventana.ficheros_seleccionados.getSelectedIndices();
				for(int x=indices1.length-1;x>-1;x--){
					listData.remove(indices1[x]);
				}
				ventana.ficheros_seleccionados.setModel(listData);	
			}
		}
			
		if(evento.getSource()==ventana.aceptar){
			if(ventana.ficheros_seleccionados.getModel().getSize()>0){
				ventana1.modelolista.clear();
				
				for(int x=0;x<ventana.ficheros_seleccionados.getModel().getSize();x++){
					Object archivo= ventana.ficheros_seleccionados.getModel().getElementAt(x);
					ventana1.modelolista.addElement(archivo);
				}
				ventana1.files.setModel(ventana1.modelolista);
				ventana.setVisible(false);
				ventana1.setSize(400, 580);
				ventana1.getContentPane().add(ventana1.panelista);
			}else{
				JOptionPane.showMessageDialog(null, "No se a elegido ningun archivo a escanear", 
		    		   "ERROR", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		if(evento.getSource()==ventana.cancelar){
			ventana.dispose();
			
		}
			
	}
		// TODO Auto-generated method stub
		
	

}

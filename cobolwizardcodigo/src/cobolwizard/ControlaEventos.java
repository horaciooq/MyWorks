package cobolwizard;

import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;
public class ControlaEventos implements ActionListener{
	selectfile ventana;
	Generar generar;
	String memory;
	File dir,dir1;
	lista l;
	
		public ControlaEventos(selectfile objeto){
		ventana = objeto;
	}
	public void actionPerformed(ActionEvent evento)
    {
		/*controlamos eventos en jchkmemoria*/
		if (evento.getSource()== ventana.jchkmemoria){/*escuchamos el evento en auriculares*/
            if (ventana.jchkmemoria.isSelected()){/*nos aseguramos q esta chekeado*/            
                ventana.jcbmemoria.setEnabled(true);
                memory="512M";
              }
            else{
            	ventana.jcbmemoria.setEnabled(false);
            }
		}
		
		/*controlamos eventos en jchkdebug*/
		if (evento.getSource()== ventana.jchkdebug){/*escuchamos el evento en auriculares*/
            if (ventana.jchkdebug.isSelected()){/*nos aseguramos q esta chekeado*/            
                ventana.debug=true;
              }
            else{
            	ventana.debug=false;
            }
		}
		
		/*controlamos eventos en jchknoextension*/
		if (evento.getSource()== ventana.jchknoextension){/*escuchamos el evento en auriculares*/
            if (ventana.jchknoextension.isSelected()){/*nos aseguramos q esta chekeado*/            
                ventana.noextension=true;
              }
            else{
            	ventana.noextension=false;
            }
		}
		
		/*controlamos eventos en jchkfixmode*/
		if (evento.getSource()== ventana.jchkfixmode){/*escuchamos el evento en auriculares*/
            if (ventana.jchkfixmode.isSelected()){/*nos aseguramos q esta chekeado*/            
                ventana.fixmode=true;
              }
            else{
            	ventana.noextension=false;
            }
		}
		
		if (evento.getSource()==ventana.jtfRutaEntrada){
			
			
		}
		/*controlamos los eventos en los botones de busqueda para el directorio*/
		if (evento.getSource()==ventana.jbFuente){//si hay evento en el boton examinar
			
			  dir1 = ventana.jdirectorioFuente.showDialog(ventana);
			    if (dir1 != null) {
			        // Real application would handle user's selection here.
			    	ventana.jtfRutaEntrada.setText(dir1.getPath());
				    	String[] fich = dir1.list();
				    if(fich!=null){
				    	l = new lista(fich,ventana);
				    	l.setVisible(true);
			    	}else{
			    		JOptionPane.showMessageDialog(null, "No hay archivos en el directorio", 
				    			   "ERROR", JOptionPane.ERROR_MESSAGE);
			    		ventana.jtfRutaEntrada.setText("");
			    	}
			    } 
		}
		
		if (evento.getSource()==ventana.jbCopies){//si hay evento en el boton examinar
			  dir = ventana.jdirectorioFuente.showDialog(ventana);
			    if (dir != null) {
			    	ventana.jtfRutaEntradaC.setText(dir.getPath());
			    } 
		    }
		
		/*capturamos el valor de memoria*/
		if (evento.getSource()==ventana.jcbmemoria) {
            memory=(String)ventana.jcbmemoria.getSelectedItem();
        }
		
		/*controlamos los eventos en el boton crear*/
		if (evento.getSource()==ventana.jbNext){//si hay evento en el boton examinar
			String cadena=ventana.jtfproyecto.getText(); 
			String root=ventana.jtfRutaEntrada.getText();
			String copy=ventana.jtfRutaEntradaC.getText();
			List archivos = new ArrayList();
			for(int x=0;x<ventana.files.getModel().getSize();x++){
				archivos.add(ventana.files.getModel().getElementAt(x));
			}
			if(cadena.length()>0 && root.length()>0 && copy.length()>0 && ventana.files.getModel().getSize()>0)
		       {
				if(cadena.matches("[a-zA-Z0-9]*")){
					if(dir.exists() && dir1.exists()){
						if(!ventana.jchkmemoria.isSelected()){
							generar = new Generar(ventana.jtfproyecto.getText(),ventana.jtfRutaEntrada.getText(),ventana.jtfRutaEntradaC.getText(),ventana.noextension,ventana.debug,ventana.fixmode,archivos);
							ventana.dispose();
						}
						else{
							generar = new Generar(ventana.jtfproyecto.getText(),ventana.jtfRutaEntrada.getText(),ventana.jtfRutaEntradaC.getText(),ventana.noextension,ventana.debug,ventana.fixmode,memory,archivos);
							ventana.dispose();
						}
					}else{
						JOptionPane.showMessageDialog(null, "Alguno de los directorios especificados no existe", 
				    			   "ERROR", JOptionPane.ERROR_MESSAGE);
					}
		       }else{
		    	   JOptionPane.showMessageDialog(null, "El nombre del proyecto no puede contener caracteres especiales o espacios en blanco", 
		    			   "ERROR", JOptionPane.ERROR_MESSAGE);
		    	   }
		       }else{
		    	   JOptionPane.showMessageDialog(null, "ninguno de los siguientes campos puede estar vacio:\nNombre del Proyecto\nDirectorio del Codigo Fuente\nDirectorio de copybooks", 
		    			   "ERROR", JOptionPane.ERROR_MESSAGE);
		       }	
		}
		
		/*controla los eventos en el boton cancel*/
		if (evento.getSource()==ventana.jbCancel){//si hay evento en el boton examinar
			ventana.dispose();
		}
    }
		
}
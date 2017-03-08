package connector;

import java.sql.Connection;
import javax.swing.JOptionPane;
public class pruebaconexion {
	public void prueba(){
	Connection miConexion;
    miConexion=conexion.GetConnection();
  
    if(miConexion!=null)
    {
        JOptionPane.showMessageDialog(null, "Conexión Realizada Correctamente");
    }
	}
}

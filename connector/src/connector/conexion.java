package connector;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JOptionPane;
public class conexion {
	@SuppressWarnings("finally")
	public static Connection GetConnection()
	    {
	        Connection con=null;
	      
	        try
	        {
	            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	            String url = "jdbc:sqlserver://15.128.27.56:1433;databaseName=SSCDB;user=ugfortify;password=a$u74g2i;";   
	            con= DriverManager.getConnection(url);
	        }
	        catch(ClassNotFoundException ex)
	        {
	            JOptionPane.showMessageDialog(null, ex, "Error1 en la Conexión con la BD "+ex.getMessage(), JOptionPane.ERROR_MESSAGE);
	            con=null;
	        }
	        catch(SQLException ex)
	        {
	            JOptionPane.showMessageDialog(null, ex, "Error2 en la Conexión con la BD "+ex.getMessage(), JOptionPane.ERROR_MESSAGE);
	            con=null;
	        }
	        catch(Exception ex)
	        {
	            JOptionPane.showMessageDialog(null, ex, "Error3 en la Conexión con la BD "+ex.getMessage(), JOptionPane.ERROR_MESSAGE);
	            con=null;
	        }finally{
	       
	            return con;
	        }
	    }
	
}


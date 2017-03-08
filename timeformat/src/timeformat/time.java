package timeformat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;



public class time {
		  public static void main(String[] argv) throws Exception {
			  Formatter fmt = new Formatter();
			    Calendar cal = Calendar.getInstance();
			    // Display 12-hour time format.
			     //fmt.format("%td/%tm/%tY %2$tl:%1$tM:%1$tS %1$tp \n", cal,cal,cal,cal);
			  //String orig=fmt.toString();
			    String orig=new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(new Date()); 
			    
			    String result = orig.substring(0,21) + "." + orig.substring(21,22)+".";
			    System.out.println(result.toLowerCase());
			    System.out.println(orig.toLowerCase());
			    
			    /*
			    final Entity.Fields.Field creationTime = new Entity.Fields.Field();
		        creationTime.setName(CREATION_TIME_FIELD_NAME);
		        creationTime.getValue().add(
		                new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		        fields.getField().add(creationTime); 
		        
		        String orig=new SimpleDateFormat("dd/MM/yyyy KK:mm:ss a").format(new Date()); 	    
			    String result = orig.substring(0,21) + "." + orig.substring(21,22)+".";	    
		        final Entity.Fields.Field fechaalta = new Entity.Fields.Field();
		        fechaalta.setName(FECHA_ALTA_FIELD_NAME);
		        fechaalta.getValue().add(result.toLowerCase());       
		        fields.getField().add(fechaalta);*/
		  }
		}
		// TODO Auto-generated method stub

	



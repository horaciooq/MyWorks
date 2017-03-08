 import java.io.*;
 
public class TestData{
	public static void main(String[] args){
		
		int[] 		objInts = {0,1,2,3,4,5,6,7,8,9};
		String[]	objStrings = {
			        "Lola","Lulu","Lili","Lina","Luna",
			        "Lana","Leda","Lala","Luna","Lucy"
		};		
		Object[] 	objects ={objInts, objStrings};
		
		ObjectArchive.saveObject(objects, "ooFile.datoo");
		
		Object[] objs = (Object[])ObjectArchive.loadObject("ooFile.datoo");
		
		int[] ints = (int[])objs[0];
		String[] words = (String[])objs[1];
		
		for(int i = 0; i< words.length; i++){
			System.out.println(
								ints[i] +
								".-" +     
								words[i]
			);
		}
			
	}
}

     //////////////////////////////////////////////////////////////////////
     
class ObjectArchive{         
     
     public static void saveObject(Object object, String archiveName){
          ObjectOutputStream oos = null;
          try {
               oos = new ObjectOutputStream(new FileOutputStream(archiveName));       
               oos.writeObject(object);
	       if (oos != null) oos.close();	
          }
	  catch(Exception e){
		System.out.println("saveObject fails " + e);
	  }          
     }

     //////////////////////////////////////////////////////////////////////

     public static Object loadObject(String archiveName) {
          ObjectInputStream ois = null;
          Object object = null;
          try {
               ois = new ObjectInputStream(new FileInputStream(archiveName));                    
               object = ois.readObject();    
	       if (ois != null) ois.close();           
          }    
	  catch(Exception e){
		System.out.println("loadObject fails " + e);
	  }                    
          return object;
    }   
}
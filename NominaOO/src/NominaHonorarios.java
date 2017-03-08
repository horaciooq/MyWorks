import java.io.*;
public class NominaHonorarios implements Serializable{
     public Empleado [] coleccionEmpleados;

     public NominaHonorarios(){
            coleccionEmpleados = null;
     }
     public void addColeccionEmpleados(int numeroEmpleadosEnColeccion){
            coleccionEmpleados = new Empleado[numeroEmpleadosEnColeccion];
     } 
     public void saveNomina(String nombreArchivoNomina) throws IOException{
          ObjectOutputStream oos = null;
          try {
               oos = new ObjectOutputStream(new FileOutputStream(nombreArchivoNomina));       
               oos.writeObject(this);
          }
          finally {
               if (oos != null) oos.close();
          }  
     }
     public NominaHonorarios loadNomina(String nombreArchivoNomina) throws Exception {
          ObjectInputStream ois = null;
          NominaHonorarios nomina = null;
          try {
               ois = new ObjectInputStream(new FileInputStream(nombreArchivoNomina));                    
               nomina = (NominaHonorarios)ois.readObject();               
          }              
          finally {
               if (ois != null) ois.close();
          }
          return nomina;
      }
}
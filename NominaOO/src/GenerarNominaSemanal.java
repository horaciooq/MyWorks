import java.io.*;

public class GenerarNominaSemanal{

     public static void main(String args[]){
          
          //Crear el Objeto Compuesto Nomina:
     
          NominaHonorarios miNominaSemanal = new NominaHonorarios();
          miNominaSemanal.addColeccionEmpleados(3);

          miNominaSemanal.coleccionEmpleados[0] = new Empleado();
          miNominaSemanal.coleccionEmpleados[0].setRfcEmpleado("abcd112233");
          miNominaSemanal.coleccionEmpleados[0].setHorasLaboradas(40.00);
          miNominaSemanal.coleccionEmpleados[0].setCuotaPorHora(100.00);            
          miNominaSemanal.coleccionEmpleados[0].addPagoEmpleado();

          miNominaSemanal.coleccionEmpleados[1] = new Empleado();
          miNominaSemanal.coleccionEmpleados[1].setRfcEmpleado("efgh445566");
          miNominaSemanal.coleccionEmpleados[1].setHorasLaboradas(25.00);
          miNominaSemanal.coleccionEmpleados[1].setCuotaPorHora(100.00);            
          miNominaSemanal.coleccionEmpleados[1].addPagoEmpleado();
          
          miNominaSemanal.coleccionEmpleados[2] = new Empleado();
          miNominaSemanal.coleccionEmpleados[2].setRfcEmpleado("ijkl778899");
          miNominaSemanal.coleccionEmpleados[2].setHorasLaboradas(40.00);
          miNominaSemanal.coleccionEmpleados[2].setCuotaPorHora(50.00);            
          miNominaSemanal.coleccionEmpleados[2].addPagoEmpleado();
          
          //Almacenar el objeto compuesto Nomina en un archivo:
 
          try {
               miNominaSemanal.saveNomina("Nomina.dat");
               System.out.println("Archivo Generado -->> Nomina.dat");
          }
          catch(IOException e)
          {
               System.out.println("Error: " + e.getMessage());
          }  
     }                              
}
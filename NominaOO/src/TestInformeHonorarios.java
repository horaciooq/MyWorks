import java.io.*;

public class TestInformeHonorarios{

     public static void main(String args[]){
          
          //Crear el Objeto Nomina:

          NominaHonorarios miNominaSemanal = new NominaHonorarios();
          
          //Recuperar el objeto compuesto Nomina de un archivo:
          
          try {
               miNominaSemanal = miNominaSemanal.loadNomina("Nomina.dat");
          }
          catch(Exception e)
          {
               System.out.println("Error: " + e.getMessage());
          }

          //Desplegar el Objeto Nomina:

          System.out.println("Test Nomina \n");

          for(int i = 0; i <=2; i++){
             Percepcion percepcionesEmpleado[];
             Deduccion  deduccionesEmpleado[];

             percepcionesEmpleado = miNominaSemanal.coleccionEmpleados[i].getPagoEmpleado().getPercepcionesEmpleado();
             deduccionesEmpleado  = miNominaSemanal.coleccionEmpleados[i].getPagoEmpleado().getDeduccionesEmpleado();             
          
             System.out.println("Empleado        : "   + miNominaSemanal.coleccionEmpleados[i].getRfcEmpleado() );          
             System.out.println("Horas Laboradas : "   + miNominaSemanal.coleccionEmpleados[i].getHorasLaboradas()); 
             System.out.println("Cuota por Hora  : $ " + miNominaSemanal.coleccionEmpleados[i].getCuotaPorHora());  
             System.out.println("Salario         : $ " + percepcionesEmpleado[0].calcTotalPercepcion(miNominaSemanal.coleccionEmpleados[i]) );
             System.out.println("+IVA 15%        : $ " + percepcionesEmpleado[1].calcTotalPercepcion(miNominaSemanal.coleccionEmpleados[i]) );
             System.out.println("-ISR 10%        : $ " + deduccionesEmpleado[1].calcTotalDeduccion(miNominaSemanal.coleccionEmpleados[i]) );
             System.out.println("-IVA 10%        : $ " + deduccionesEmpleado[0].calcTotalDeduccion(miNominaSemanal.coleccionEmpleados[i]) );
             System.out.println("Pago Total      : $ " + miNominaSemanal.coleccionEmpleados[i].getPagoEmpleado().calcTotalPago());
              System.out.println();
          }  
     }     
}
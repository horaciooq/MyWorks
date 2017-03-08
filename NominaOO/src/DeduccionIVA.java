import java.io.*;
public class DeduccionIVA extends Deduccion implements Serializable{ 
     public DeduccionIVA(){

     }
     public double calcTotalDeduccion(Empleado empleado){
          return ( (empleado.getCuotaPorHora() * empleado.getHorasLaboradas()) * 0.10 );
     }          
}
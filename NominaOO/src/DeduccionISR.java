import java.io.*;
public class DeduccionISR extends Deduccion implements Serializable{ 
     public DeduccionISR(){

     }
     public double calcTotalDeduccion(Empleado empleado){
          return ( (empleado.getCuotaPorHora() * empleado.getHorasLaboradas()) * 0.10 );
     }          
}
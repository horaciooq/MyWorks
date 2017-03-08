import java.io.*;
public class PercepcionIVA extends Percepcion implements Serializable{ 
     public PercepcionIVA(){

     }
     public double calcTotalPercepcion(Empleado empleado){
          return ( (empleado.getCuotaPorHora() * empleado.getHorasLaboradas()) * 0.15 );
     }          
}
import java.io.*;
public class PercepcionHonorarios extends Percepcion implements Serializable { 
     public PercepcionHonorarios(){

     }
     public double calcTotalPercepcion(Empleado empleado){
          return ( empleado.getCuotaPorHora() * empleado.getHorasLaboradas() );
     }          
}
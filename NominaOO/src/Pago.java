import java.io.*;
public class Pago implements Serializable{ 
     private Empleado empleado;
     private Percepcion percepcionesEmpleado[];
     private Deduccion  deduccionesEmpleado[];
    
     public Pago(Empleado empleado){
          this.empleado = empleado;
          this.addPercepcionesEmpleado();
          this.addDeduccionesEmpleado();
     }
     public Empleado getEmpleado(){
          return this.empleado;
     } 
     public Percepcion [] getPercepcionesEmpleado(){
          return percepcionesEmpleado;
     }
     public Deduccion [] getDeduccionesEmpleado() {
          return deduccionesEmpleado;
     }    
     private void addPercepcionesEmpleado(){
          percepcionesEmpleado = new Percepcion[2]; 
          percepcionesEmpleado[0] = new PercepcionHonorarios();
          percepcionesEmpleado[1] = new PercepcionIVA();      
     }
     private void addDeduccionesEmpleado(){
          deduccionesEmpleado = new Deduccion[2]; 
          deduccionesEmpleado[0] = new DeduccionIVA();
          deduccionesEmpleado[1] = new DeduccionISR();      
     }
     public double calcTotalPago(){          
          return    percepcionesEmpleado[0].calcTotalPercepcion(empleado) 
                  + percepcionesEmpleado[1].calcTotalPercepcion(empleado) 
                  - deduccionesEmpleado[0].calcTotalDeduccion(empleado)
                  - deduccionesEmpleado[1].calcTotalDeduccion(empleado);           
     } 
}
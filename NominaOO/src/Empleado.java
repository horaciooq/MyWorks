import java.io.*;
public class Empleado implements Serializable{
     private String rfcEmpleado;
     private double horasLaboradas;
     private double cuotaPorHora;
     Pago   pagoEmpleado;

     public Empleado(){
          this.rfcEmpleado = "aaaa000000";
          this.horasLaboradas = 0.0;
          this.cuotaPorHora = 0.0;
          this.pagoEmpleado = null;
     }
     public String getRfcEmpleado(){
          return this.rfcEmpleado;
     }
     public double getHorasLaboradas(){
          return horasLaboradas;
     }
     public double getCuotaPorHora(){
          return cuotaPorHora;
     }
     public Pago getPagoEmpleado(){
          return pagoEmpleado;
     }
     public void setRfcEmpleado(String rfcEmpleado){
          this.rfcEmpleado = rfcEmpleado;     
     }
     public void setHorasLaboradas(double horasLaboradas){
          this.horasLaboradas = horasLaboradas;
     }
     public void setCuotaPorHora(double cuotaPorHora){
          this.cuotaPorHora = cuotaPorHora; 
     }
     public void addPagoEmpleado(){
          pagoEmpleado = new Pago(this);          
     }
}
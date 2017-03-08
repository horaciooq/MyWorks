package com.serviceexample;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class MyService {

	   /**
	    * Single static instance of the service class
	    */
	   private static MyService 
	       serviceInstance = new MyService();
		
	   /**
	    * Static method called by prunsrv to start/stop
	    * the service.  Pass the argument "start"
	    * to start the service, and pass "stop" to
	    * stop the service.
	    */
	   public static void main(String args[]) {
		   System.out.println(args[0]);
	      String cmd = "start";
	      if(args.length > 0) {
	         cmd = args[0];
	      }
		
	      if("start".equals(cmd)) {
	         serviceInstance.start();
	      }
	      else {
	         serviceInstance.stop();
	      }
	   }

	   /**
	    * Flag to know if this service
	    * instance has been stopped.
	    */
	   private boolean stopped = false;
		
		
	   /**
	    * Start this service instance
	    */
	   public void start() {
		   stopped = false;
		   File archivo =new File("C:\\MyService\\MyService.txt");
			FileWriter fw;
			try {
				fw = new FileWriter(archivo);
				PrintWriter bw = new PrintWriter(fw,true);
				bw.println("Hello");
				int x=0;
				synchronized(this){ 
				while(!stopped){
					try{
					Thread.sleep(1000);
					System.out.println("segundo "+ x);
					x++;
					bw.println(x);
					}catch (InterruptedException e){}
					
				}}
				bw.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	   
		
	   /**
	    * Stop this service instance
	    */
	   public void stop() {
	      stopped = true;
		  synchronized(this) {
	         this.notify();
	      }
	      
	   }
	}
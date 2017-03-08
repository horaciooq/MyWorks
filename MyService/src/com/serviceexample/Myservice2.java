package com.serviceexample;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Myservice2 {

	public static void main(String[] args) {
		Myservice2 exe = new Myservice2();
		exe.ejecucion();
	}
	
	public void ejecucion(){
		File archivo =new File("C:\\MyService\\MyService.txt");
		FileWriter fw;
		try {
			fw = new FileWriter(archivo);
			PrintWriter bw = new PrintWriter(fw,true);
			bw.println("Hello");
			int x=0;
			synchronized(this){ 
			while(x<10){
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
}
	

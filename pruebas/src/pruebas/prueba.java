package pruebas;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class prueba {

	public static void main(String[] args) {
		String val = "veinte";
		try {
		int value = Integer.parseInt(val);
		}
		catch (NumberFormatException nfe) {
		log.Info(lista_de_errores(nfe));
		}
	}
	
	public String lista_de_errores(Exception e){
		
		return "";
	}

}

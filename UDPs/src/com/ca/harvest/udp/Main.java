package com.ca.harvest.udp;

import java.io.IOException;

import com.ca.harvest.UDPException;
import com.ca.harvest.jhsdk.hutils.JCaHarvestException;
/**
 * Clase principal que invoca a las UDPs
 * 
 * @author Ernesto Peña
 * 
 */
public class Main {
	
	/**
	 * Método estático de ayuda para hacer un corrimiento en un arreglo.
	 * @param args Arreglo al que se le va a hacer el correimiento
	 * @return
	 */
	private static String[] shiftArgs(String[] args){
		String[] salida = new String[args.length -1];
		for(int i = 1; i< args.length; i++){
			salida[i-1] = args[i];
		}
		return salida;
	}
	
	/**
	 * Método main.
	 * @param args Arreglo de entrada. Varia dependiendo la UDP que se invocará, tomando en cuenta que la primer entrada es el identificador de la UDP
	 */
	public static void main (String[] args){
		if(args.length <= 1 ){
			System.err.println("Numero de parámetros incorrecto");
			System.exit(-1);
			return;
		}
		Udps udp = null;
		try{
			udp = Udps.valueOf(args[0]);
		}catch(IllegalArgumentException iae){
			System.err.println("Primer parámetro de UDP incorrecto, el primer parámetro debe ser ActualizaForma, CreaLineaBase, CrearPaquete, NotificaConcurrencia, NotificaIntegracion o ValidaPromote");
			System.exit(-1);
			return;
		}
		
		try{
			
			switch(udp){
				case ActualizaForma:
					ActualizaForma._main(shiftArgs(args));
					break;
				case CreaLineaBase:
					CreaLineaBaseFinal._main(shiftArgs(args));
					break;
				case CrearPaquete:
					CrearPaquete._main(shiftArgs(args));
					break;
				case NotificaConcurrencia:
					NotificaionConcurrencia._main(shiftArgs(args));
					break;
				case NotificaIntegracion:
					NotificacionIntegracion._main(shiftArgs(args));
					break;
				case ValidaPromote:
					ValidaPromote._main(shiftArgs(args));
					break;
				case ValidaVersionesEnteras:
					ValidaVersionesEnteras._main(shiftArgs(args));
					break;
				case ValidaCamposReq:
					ValidaCamposRequeridos._main(shiftArgs(args));
					break;
				case ValidaPromoIncidencia:
					ValidaPromocionIncidencia._main(shiftArgs(args));
					break;
				case CampoCajaBlanca:  //UDP para el campo de pruebas de caja blanca
					CampoCajaBlanca._main(shiftArgs(args)); 
					break;
				default:
					System.err.println("Primer parámetro de UDP incorrecto, el primer parámetro debe ser ActualizaForma, CreaLineaBase, CrearPaquete, NotificaConcurrencia, NotificaIntegracion o ValidaPromote");
					System.exit(-1);
					break;
			}
		} catch (JCaHarvestException e) {
			System.err.println("Se encontro un error en el proceso");
			System.err.println(e.getMessage());
			
			System.exit(1);
		}catch(IOException ioe){
			System.err.println("Se encontro un error de IO en el proceso");
			System.err.println(ioe.getMessage());
			System.exit(2);
		}catch(InterruptedException ie){
			System.err.println("El proceso de envio de correos fue interrumpido");
			System.err.println(ie.getMessage());
			System.exit(3);
		
		}catch(IllegalArgumentException iae){
			System.err.println(iae.getMessage());
			System.exit(4);
		
		} catch (UDPException e) {
			System.err.println(e.getMessage());
			System.exit(5);
		
		}
		
	}
	
	private enum Udps{

	    ActualizaForma,
	    CreaLineaBase,
	    CrearPaquete,
	    NotificaIntegracion,
	    NotificaConcurrencia,
	    ValidaPromote,
	    ValidaVersionesEnteras,
	    ValidaCamposReq,
	    ValidaPromoIncidencia,
	    CampoCajaBlanca
	  }
}

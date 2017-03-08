package com.ca.harvest;
/**
 * Excepción para ser enviada dentro de las UDPs
 * @author Ernesto Peña
 * 
 *
 */
public class UDPException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1123L;
	
	/**
	 * Contructor
	 */
	public UDPException(){
		super();
	}
	
	/**
	 * Contructor
	 * @param mensaje Mensaje a enviar con la excepción.
	 */
	public UDPException(String mensaje){
		super(mensaje);
	}
}

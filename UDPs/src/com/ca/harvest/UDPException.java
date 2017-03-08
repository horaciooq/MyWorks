package com.ca.harvest;
/**
 * Excepci�n para ser enviada dentro de las UDPs
 * @author Ernesto Pe�a
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
	 * @param mensaje Mensaje a enviar con la excepci�n.
	 */
	public UDPException(String mensaje){
		super(mensaje);
	}
}

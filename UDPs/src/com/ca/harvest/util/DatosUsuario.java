package com.ca.harvest.util;

/**
 * Clase de apoyo para almacenar los datos de usuario
 * @author Ernesto Peña
 *
 */
public class DatosUsuario {
	private String _usuario;
	private String _correo;
	private String _nombre;
	private int _uid;
	
	/**
	 * Constructor
	 * @param usuario Nombre de usuario
	 * @param nombre Nombre completo
	 * @param correo Correo electrónico
	 * @param uid Id del usuario
	 */
	public DatosUsuario(String usuario, String nombre, String correo,int uid){
		_usuario = usuario;
		_nombre = nombre;
		_correo = correo;
		_uid = uid;
		
	}
	
	/**
	 * 
	 * @return El Nombre de usuario
	 */
	public String get_usuario() {
		return _usuario;
	}
	/**
	 * 
	 * @return El correco electrónico
	 */
	public String get_correo() {
		return _correo;
	}
	
	/**
	 * 
	 * @return El nombre completo
	 */
	public String get_nombre() {
		return _nombre;
	}
	
	/**
	 * 
	 * @return El Id del usuario
	 */
	public int getUid() {
		return _uid;
	}
}

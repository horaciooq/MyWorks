package com.ca.harvest.util;

import java.util.HashMap;

import com.ca.harvest.jhsdk.JCaContext;
import com.ca.harvest.jhsdk.JCaForm;
import com.ca.harvest.jhsdk.JCaPackage;
import com.ca.harvest.jhsdk.JCaSQL;
import com.ca.harvest.jhsdk.hutils.JCaContainer;
import com.ca.harvest.jhsdk.hutils.JCaHarvestException;

/**
 * Clase de Apoyo que nos ayuda a hacer consultas que se usan en varios lados
 * @author Ernesto Peña
 *
 */
public class ConsultaRegistros {
	private JCaContext _contexto;
	
	/**
	 * Constructor
	 * @param contexto Objeto JCaContext con el contexto debidamente configurado
	 */
	public ConsultaRegistros(JCaContext contexto){
		_contexto = contexto;
	}
	
	/**
	 * Método que nos regresa los correos electrónicos de los usuarios con rol Gestor de Versiones asignados en el proyecto de Harvest 
	 * @param idProyecto Id del proyecto
	 * @return Una cadena de correos electrónicos separados por comas
	 * @throws JCaHarvestException en caso de error en la consulta.
	 */
	public String consultaCorreosGestorVersiones(int idProyecto) throws JCaHarvestException{
		JCaSQL sql = _contexto.getSQL();
		String consulta = "select EMAIL from HARENVIRONMENTACCESS a " +
				"join HARUSERSINGROUP ug on a.USRGRPOBJID = ug.USRGRPOBJID " +
				"join HARUSERSINGROUP ug2 on ug.USROBJID = ug2.USROBJID " +
				"join HARUSERGROUP g on ug2.USRGRPOBJID = g.USRGRPOBJID " +
				"join haruser u on ug.USROBJID = u.USROBJID " +
				"where ENVOBJID = " + idProyecto + " AND EXECUTEACCESS = 'Y' and g.USERGROUPNAME = 'Gestor de Versiones (GV)'";
		sql.setSQLStatement(consulta);
		int error = sql.execute();
		if(error == 0){
			JCaContainer res = sql.getSQLResult();
			if(res.isEmpty()){
				return "";
			}else{
				int cuantos = res.getKeyElementCount("EMAIL");
				String salida = "";
				for(int i = 0; i< cuantos; i++){
					salida += " " + res.getString("EMAIL", i);
				}
				return salida;
			}
		}
		
		throw new JCaHarvestException("Hubo un error con la consulta: codigo de error: " + error);
	}
	
	/**
	 * Método que nos regresa los datos del usuario a través de su nombre de usuario.
	 * @param userName Nombre de usuario
	 * @return un objeto del tipo {@link DatosUsuario} con los datos del usuario
	 * @throws JCaHarvestException
	 */
	public DatosUsuario getUsuario(String userName) throws JCaHarvestException{
		JCaSQL sql = _contexto.getSQL();
		sql.setSQLStatement("SELECT USROBJID, USERNAME, EMAIL, REALNAME FROM HARUSER WHERE USERNAME = '" + userName + "'");
		int error = sql.execute();
		if(error == 0){
			JCaContainer contenedor = sql.getSQLResult();
			return new DatosUsuario(contenedor.getString("USERNAME"), 
					contenedor.getString("REALNAME"), 
					contenedor.getString("EMAIL"), 
					contenedor.getInt("USROBJID"));
		}else{
			throw new IllegalArgumentException("El usuario no existe");
		}
	}
	/**
	 * Método que nos regresa los datos del usuario a través de su Id.
	 * @param usrobjid Id del usuario
	 * @return un objeto del tipo {@link DatosUsuario} con los datos del usuario
	 * @throws JCaHarvestException
	 */
	public DatosUsuario getUsuario(int usrobjid) throws JCaHarvestException{
		JCaSQL sql = _contexto.getSQL();
		sql.setSQLStatement("SELECT USROBJID, USERNAME, EMAIL, REALNAME FROM HARUSER WHERE USROBJID = '" + usrobjid + "'");
		int error = sql.execute();
		if(error == 0){
			JCaContainer contenedor = sql.getSQLResult();
			return new DatosUsuario(contenedor.getString("USERNAME"), 
					contenedor.getString("REALNAME"), 
					contenedor.getString("EMAIL"), 
					contenedor.getInt("USROBJID"));
		}else{
			throw new IllegalArgumentException("El usuario no existe");
		}
	}

	
	/**
	 * Método que nos regresa algunos datos del formulario
	 * @param paquete Objeto JCaPackage con el contexto.
	 * @return un objeto del tipo {@link HashMap} con los datos del formulario
	 * @throws JCaHarvestException
	 */
	public HashMap<String, String> formaPaquete(JCaPackage paquete) throws JCaHarvestException{

		JCaForm[] formularios = paquete.getFormListAsList(true);
		HashMap<String, String> salida = new HashMap<String, String>(); 
		for(int i = 0; i< formularios.length; i++){
			if("Descripcion del paquete".equals(formularios[i].getFormType().getName() )){
				formularios[i].get(formularios[i].getFormObjID());
				salida.put("FECHACREACION", formularios[i].getColumnValue("FECHACREACION").getTimeStamp().toString());
				salida.put("NOMBREAPLICACION", formularios[i].getColumnValue("NOMBREAPLICACION").getString());
				salida.put("IDPROYECTO", formularios[i].getColumnValue("IDPROYECTO").getString());
				salida.put("LIDERVERSION", formularios[i].getColumnValue("LIDERVERSION").getString());
				salida.put("DESARROLLADOR", formularios[i].getColumnValue("DESARROLLADOR").getString());
				salida.put("DESCREQUERIMIENTO", formularios[i].getColumnValue("DESCREQUERIMIENTO").getString());
				salida.put("PRUEBASUNITARIAS", formularios[i].getColumnValue("PRUEBASUNITARIAS").getString());
				salida.put("FABRICADESARROLLADOR", formularios[i].getColumnValue("FABRICADESARROLLADOR").getString());
				salida.put("ANALISTAPRUEBAS", formularios[i].getColumnValue("ANALISTAPRUEBAS").getString());
				salida.put("CONTROLCAMBIOS", formularios[i].getColumnValue("CONTROLCAMBIOS").getString());
				salida.put("TIPOREQUERIMIENTO", formularios[i].getColumnValue("TIPOREQUERIMIENTO").getString());
				return salida;
			}
		}
		throw new IllegalArgumentException("El paquete no tiene ninguna forma del tipo \"Descripcion del paquete\"");
	}
	
	/**
	 * Metodo que nos regresa el tipo de requerimiento del paquete
	 * @param paquete Nombre del paquete
	 * @return El tipo de requerimiento
	 * @throws JCaHarvestException en caso de error en la consulta
	 */
	public String tipoRequerimientoPaquete(JCaPackage paquete) throws JCaHarvestException{
		JCaSQL sql = _contexto.getSQL();
		//DatosUsuario[] salida  = new DatosUsuario[2];   
		JCaForm[] formularios = paquete.getFormListAsList(true);
		for(int i = 0; i< formularios.length; i++){
			if("Descripcion del paquete".equals(formularios[i].getFormType().getName() )){
				sql.setSQLStatement("SELECT TIPOREQUERIMIENTO FROM hardescpaquete where formobjid = " + formularios[i].getFormObjID());
				int error = sql.execute();
				if(error == 0){
					JCaContainer cont = sql.getSQLResult();
					return cont.getString("TIPOREQUERIMIENTO");
				} 
			}
		}
		throw new IllegalArgumentException("El paquete no tiene ninguna forma del tipo \"Descripcion del paquete\"");
	}
	
	
	/**
	 * Obtiene un arreglo de objetos con los datos de los desarrolladores asignados en el formulario, este método considera que un paquete puede tener dos o más forma del mismo tipo en un paquete
	 * @param paquete Nombre del paquete
	 * @return un arreglo de objetos de tipo {@link DatosUsuario} con los datos de los desarrolladores en los formularios del paquete.
	 * @throws JCaHarvestException en caso de error al traer los datos de los formularios.
	 */
	public DatosUsuario[] getUsersInForm(JCaPackage paquete) throws JCaHarvestException{
		
		JCaForm[] formularios = paquete.getFormListAsList(true);
		
		for(int i = 0; i< formularios.length; i++){
			if("Descripcion del paquete".equals(formularios[i].getFormType().getName() )){
				return getUsersInForm(formularios[i].getFormObjID());
			}
		}
		
		throw new IllegalArgumentException("El paquete no tiene ninguna forma del tipo \"Descripcion del paquete\"");
		
	}
	/**
	 * Metodo que sirve para obtener el desarrollador y el lider ingresados en el formulario
	 * @param formobjid id del formulario
	 * @return El resultado es un arreglo con dos elementos del tipo DatosUsuario en donde la primer entrada es el lider y la segunda es el desarrollador
	 * @throws JCaHarvestException en caso de error al traer los datos del formulario.
	 */
	public DatosUsuario[] getUsersInForm(int formobjid) throws JCaHarvestException{
		JCaSQL sql = _contexto.getSQL();
		DatosUsuario[] salida  = new DatosUsuario[2];   
		
		sql.setSQLStatement("SELECT ud.usrobjid DESAUID, ud.username as DESA, ud.realname NOMBDESA, ud.email DESAMAIL,ud.usrobjid LIDERUID, ul.username as LIDER, ul.realname NOMBLIDER, ul.email LIDERMAIL FROM hardescpaquete f LEFT JOIN haruser ud ON ud.username = f.desarrollador LEFT JOIN haruser ul on ul.username = f.liderversion where f.formobjid = " + formobjid);
		int error = sql.execute();
		if(error == 0){
				JCaContainer cont = sql.getSQLResult();
				salida[0] = new DatosUsuario(cont.getString("LIDER"), cont.getString("NOMBLIDER"), cont.getString("LIDERMAIL"), cont.getInt("LIDERUID"));
				salida[1] = new DatosUsuario(cont.getString("DESA"), cont.getString("NOMBDESA"), cont.getString("DESAMAIL"), cont.getInt("DESAUID"));
				
			return salida;
		}else{
			throw new IllegalArgumentException("El usuario no existe");
			
		}
	}
	
}

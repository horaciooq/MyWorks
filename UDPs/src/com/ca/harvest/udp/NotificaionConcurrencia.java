package com.ca.harvest.udp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import com.ca.harvest.jhsdk.JCaContext;
import com.ca.harvest.jhsdk.JCaPackage;
import com.ca.harvest.jhsdk.JCaSQL;
import com.ca.harvest.jhsdk.hutils.JCaAttrKey;
import com.ca.harvest.jhsdk.hutils.JCaContainer;
import com.ca.harvest.jhsdk.hutils.JCaHarvestException;
import com.ca.harvest.util.ConsultaRegistros;
import com.ca.harvest.util.DatosUsuario;
import com.ca.harvest.util.EnviaNotificacion;
/**
 * Clase para la notificación de concurrencia.
 * @author Ernesto Peña
 *
 */
public class NotificaionConcurrencia {
	JCaContext contexto;
	/**
	 * Constructor
	 * @param broker Broker de harvest
	 * @param rutaDfo Ruta completa al archivo dfo de credenciales
	 * @param proyecto Proyecto de Harvest
	 * @param estado Estado de Harvest
	 * @throws JCaHarvestException
	 */
	public NotificaionConcurrencia (String broker, String rutaDfo, String proyecto, String estado) throws JCaHarvestException{
		com.ca.harvest.conexion.Contexto con = new com.ca.harvest.conexion.Contexto(broker, rutaDfo);
		con.setContexto(proyecto, estado);
		contexto = con.getContext();
	}
	
	/**
	 * Método que envía notificación en caso de encontrar elementos en concurrencia a todos los involucrados
	 * @param paquete Paquete inical
	 * @param estadoProd Nombre del estado de Producción. Este estado se omite en la consulta.
	 * @param cuerpo Cuerpo de la notificación
	 * @param asunto Asunto de la notificación
	 * @throws JCaHarvestException En caso de error al obtener los elementos en concurrencia
	 * @throws IOException En caso de error al leer el cuerpo de la notificación
	 * @throws InterruptedException En caso de error con el proceso de notificación
	 */
	public void revisaConcurrencia(String paquete, String estadoProd, String cuerpo, String asunto) throws JCaHarvestException, IOException, InterruptedException{
		
		JCaPackage _pack  = contexto.getPackageByName(paquete);
		JCaSQL sql = contexto.getSQL();
		int error ;
		
		String desarrolladorAsignado = "";
		String liderVersionAsignado = "";
		String mailLiderVersion = "";
		String mailDesaAsignado = "";
		ConsultaRegistros cr = new ConsultaRegistros(contexto);
		
		
		DatosUsuario[] usuarios = cr.getUsersInForm(_pack);
		desarrolladorAsignado = usuarios[1].get_usuario() +" - " + usuarios[1].get_nombre();
		liderVersionAsignado = usuarios[0].get_usuario() +" - " + usuarios[0].get_nombre();
		mailLiderVersion = usuarios[0].get_correo();
		mailDesaAsignado = usuarios[1].get_correo();

		int _idPack = _pack.getObjId();
		int idproy = contexto.getProjectId();
		
	 	JCaContainer listaEstados = contexto.getStateList();
	 	int[] _edoProd = listaEstados.find(JCaAttrKey.CA_ATTRKEY_NAME, estadoProd);
	 	int idEdoProd = listaEstados.getInt(JCaAttrKey.CA_ATTRKEY_OBJID, _edoProd[0]); //suponemos que siempre habra, a menos de que le cambien el nombre al estado de produccion cosa que se tendria que adecuar en la invocacion de la udp
	    
	    String consulta = "select distinct hf.PATHFULLNAME || '\\' || hi.itemname as item,   " +
				    		"hp.PACKAGENAME, DESARROLLADOR, desa.REALNAME nombredesarrollador,  " +
				    		"desa.EMAIL emaildesarrollador, LIDERVERSION, lv.REALNAME  " +
				    		"nombrelider, lv.EMAIL emaillider ,  hs.STATENAME " +
				    		"FROM harpackage hp " +
				    		"JOIN HARVERSIONS hv2 on hp.PACKAGEOBJID = hv2.PACKAGEOBJID and  hv2.PACKAGEOBJID <> " + _idPack + " " +
				    		"JOIN HARVERSIONS hv on hv.ITEMOBJID = hv2.ITEMOBJID AND hv.PACKAGEOBJID = " + _idPack + " " +
				    		"JOIN HARITEMS hi on hv.ITEMOBJID = hi.ITEMOBJID " +
				    		"JOIN HARPATHFULLNAME hf ON hi.PARENTOBJID = hf.ITEMOBJID " +
				    		"JOIN HARASSOCPKG haf on haf.ASSOCPKGID = hp.PACKAGEOBJID " +
				    		"JOIN HARFORM hfo on haf.FORMOBJID  = hfo.FORMOBJID " +
				    		"JOIN HARSTATE hs on hs.STATEOBJID = hp.STATEOBJID " +
				    		"left JOIN hardescpaquete hde on hde.FORMOBJID = hfo.FORMOBJID " +
				    		"left JOIN HARUSER desa on desa.USERNAME = hde.DESARROLLADOR " +
				    		"left JOIN HARUSER lv on lv.USERNAME = hde.LIDERVERSION " +
				    		"WHERE hs.envobjid = " + idproy + " and hs.stateobjid <> " + idEdoProd +
				    		" ORDER BY hp.PACKAGENAME";
				    		sql.setSQLStatement(consulta);
	    error = sql.execute();
	    HashMap<String, ContenedorPaquete> paquetes = new HashMap< String, ContenedorPaquete>();
	    LinkedList<String > correos = new LinkedList<String>();
	    if(mailLiderVersion != null && !"".equals(mailLiderVersion)){
	    	correos.add(mailLiderVersion);
	    }
	    if(mailDesaAsignado != null && !"".equals(mailDesaAsignado)){
	    	correos.add(mailDesaAsignado);
	    }
	    if(error == 0){
	    	JCaContainer sqlResult = sql.getSQLResult();
	    	if(sqlResult.getKeyCount() == 0){
	    		System.out.println("No se encontraron elementos en concurrencia");
	    		return;
	    	}
	    	int tot = sqlResult.getKeyElementCount("PACKAGENAME");
	    	String paq, estado, elemento, desarrollador, lider, correoDesarrollador, correoLider;
	    	for(int i = 0; i < tot; i++){
	    		paq = sqlResult.getString("PACKAGENAME", i);
	    		elemento = sqlResult.getString("ITEM", i);
	    		if(paquetes.containsKey(paq)){
	    			paquetes.get(paq).agrega(elemento);
	    		}else{
		    		estado = sqlResult.getString("STATENAME", i);
		    		desarrollador = sqlResult.getString("DESARROLLADOR", i);
		    		desarrollador += " - " + sqlResult.getString("NOMBREDESARROLLADOR", i);
		    		lider = sqlResult.getString("LIDERVERSION", i);
		    		lider += " - " + sqlResult.getString("NOMBRELIDER", i);
		    		correoDesarrollador = sqlResult.getString("EMAILDESARROLLADOR", i);
		    		correoLider = sqlResult.getString("EMAILLIDER", i);
		    		ContenedorPaquete conte = new ContenedorPaquete(desarrollador, lider, estado);
		    		conte.agrega(elemento);
		    		paquetes.put(paq, conte);
		    		if(correoLider != null && !correoLider.equals("") && !correos.contains(correoLider)){
		    			correos.add(correoLider);
		    		}
		    		if(correoDesarrollador!= null && !correoDesarrollador.equals("") && !correos.contains(correoDesarrollador)){
		    			correos.add(correoDesarrollador);
		    		}
	    		}
	    	}
	    	Iterator<String> it = correos.iterator();
	    	String cadenaCorreos = "";
	    	while(it.hasNext()){
	    		cadenaCorreos+=" " + it.next();
	    	}
	    	if("".equals(cadenaCorreos)){
	    		throw new IllegalArgumentException("No se encontraron correos para notificar");
	    	}else{
	    		cadenaCorreos = cadenaCorreos.substring(1) + cr.consultaCorreosGestorVersiones(contexto.getProjectId());
	    	}
	    	StringBuilder paquetesYVersiones = new StringBuilder();
	    	Set<String> paquetesSet = paquetes.keySet();
	    	it = paquetesSet.iterator();
	    	while(it.hasNext()){
	    		String p = it.next();
	    		ContenedorPaquete cp =  paquetes.get(p);
	    		paquetesYVersiones.append("<br><br><br><b>Estado:</b> " + cp.get_estado());
	    		paquetesYVersiones.append("<br><b>Paquete:</b> " + p);
	    		paquetesYVersiones.append("<br><b>Lider de Versi&oacute;n:</b> " + cp.get_lider());
	    		paquetesYVersiones.append("<br><b>Desarrollador:</b> " + cp.get_desarrollador() );

	    		paquetesYVersiones.append("<br><b>Elementos:</b> ");
	    		Iterator<String> it2 = cp.get_versiones().iterator();
	    		while(it2.hasNext()){
	    			paquetesYVersiones.append("<br>" + it2.next().replaceAll("\\\\", "\\\\\\\\"));
	    		}
	    	}
	    	String sPaquetesYVersiones =paquetesYVersiones.toString();
	    	cuerpo = cuerpo.replaceAll("\\[project\\]", contexto.getProjectName());
	    	cuerpo = cuerpo.replaceAll("\\[state\\]", contexto.getStateName());
	    	cuerpo = cuerpo.replaceAll("\\[lista\\]", sPaquetesYVersiones);
	    	cuerpo = cuerpo.replaceAll("\\[package\\]", paquete);
	    	cuerpo = cuerpo.replaceAll("\\[desarrollador\\]", desarrolladorAsignado);
	    	cuerpo = cuerpo.replaceAll("\\[lider\\]", liderVersionAsignado);
	    	
	    	EnviaNotificacion.envia(cadenaCorreos, cuerpo, asunto);
	    	
	    }
		
	}
/**
 * 
 * Clase de apoyo para almacenar la informacion de los paquetes involucrados en la concurrencia
 *
 */
	private class ContenedorPaquete{
		private  LinkedList<String> _versiones;
		private String _desarrollador;
		private String _lider;
		private String _estado; 
		
		/**
		 * 
		 * @return Regresa la lista de versiones
		 */
		public LinkedList<String> get_versiones() {
			return _versiones;
		}
		/**
		 * 
		 * @return Obtiene el desarrollador en concurrencia
		 */
		public String get_desarrollador() {
			return _desarrollador;
		}
		/**
		 * 
		 * @return Obtiene el líder de version que creó el paquete
		 */
		public String get_lider() {
			return _lider;
		}
		/**
		 * 
		 * @return Obtiene el estado en el que se encuentra el paquete
		 */
		public String get_estado() {
			return _estado;
		}
		/**
		 * Constructor
		 * @param Desarrollador Desarrollador en concurrencia
		 * @param lider Líder de version que creó el paquete
		 * @param estado Estado en el que se encuentra el paquete
		 */
		public ContenedorPaquete(String desarrollador, String lider, String estado){
			_versiones = new LinkedList<String>();
			_desarrollador = desarrollador;
			_lider = lider;
			_estado = estado;
			
		}
		/**
		 * Agrega elementos a la lista de elementos en concurrencia
		 * @param elemento Elemento en concurrencia
		 */
		public void agrega(String elemento){
			if(elemento != null && elemento != "" && !_versiones.contains(elemento)){
				_versiones.add(elemento);
			}
		}
	}
	
	
	/**
	 * Invocación principal, se hace a través de la clase {@link Main}
	 * @param args Arreglo de argumentos, el arreglo debe contener el broker, la ruta del archivo .dfo con las credenciales para iniciar sesión en el broker, proyecto, estado, paquete, nombre del estado que se omitirá (Produccion) y el asunto de la notificación 
	 * @throws JCaHarvestException En caso de error con la obtencion de los elementos con concurrencia 
	 * @throws IOException En caso de error al leer el cuerpo del mensaje ya que este se obtiene del Standard Input
	 * @throws InterruptedException En caso de error al invocar el proceso de notificación
	 * 
	 */
	public static void _main(String[] args) throws IOException, JCaHarvestException, InterruptedException{
		
		if(args.length != 7){
			System.err.println("El uso correcto es comando broker archivoDfo proyecto estado paquete estadoProduccion asunto");
			System.exit(-1);
		}
		System.out.println("Iniciando la verificacion de concurrencia");
		String cuerpo = "";
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String input;
		while((input=br.readLine())!=null){
			cuerpo += input;
		}
		
		
		NotificaionConcurrencia nc = new NotificaionConcurrencia(args[0], args[1], args[2], args[3]);
		nc.revisaConcurrencia(args[4], args[5], cuerpo, args[6]);
	
		
	}
	
}

package com.ca.harvest.udp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.ca.harvest.jhsdk.JCaConst;
import com.ca.harvest.jhsdk.JCaContext;
import com.ca.harvest.jhsdk.JCaPackage;
import com.ca.harvest.jhsdk.JCaVersion;
import com.ca.harvest.jhsdk.hutils.JCaHarvestException;
import com.ca.harvest.util.ConsultaRegistros;
import com.ca.harvest.util.DatosUsuario;
import com.ca.harvest.util.EnviaNotificacion;
/**
 * Clase para la UDP de notificación de integración
 * @author gramsusa
 *
 */
public class NotificacionIntegracion {
	JCaContext contexto;
	/**
	 * Constructor
	 * @param broker Broker de harvest
	 * @param rutaDfo Ruta completa al archivo dfo de credenciales
	 * @param proyecto Proyecto de Harvest
	 * @param estado Estado de Harvest
	 * @throws JCaHarvestException
	 */
	public NotificacionIntegracion  (String broker, String rutaDfo, String proyecto, String estado) throws JCaHarvestException{
		com.ca.harvest.conexion.Contexto con = new com.ca.harvest.conexion.Contexto(broker, rutaDfo);
		con.setContexto(proyecto, estado);
		contexto = con.getContext();
	}
	
	/**
	 * Método que lanza la notificación en caso de que se encuentren elementos con etiqueta M
	 * @param paquete Paquete
	 * @param cuerpo Cuerpo de la notificación
	 * @param asunto Asunto de la notificación
	 * @throws JCaHarvestException En caso de error al obtener los elementos
	 * @throws IOException En caso de error al leer el cuerpo del mensaje ya que este se obtiene del Standard Input
	 * @throws InterruptedException En caso de error al invocar el proceso de notificación
	 */
	public void notificaTagM(String paquete, String cuerpo, String asunto) throws JCaHarvestException, IOException, InterruptedException{
		JCaPackage _pack  = contexto.getPackageByName(paquete);
		
		ConsultaRegistros cr = new ConsultaRegistros(contexto);
		DatosUsuario[] usuarios = cr.getUsersInForm(_pack);
		
		JCaVersion[] versiones = _pack.getVersionList(true, JCaConst.VERSION_FILTER_ALL_IN_VIEW, JCaConst.VERSION_FILTER_MERGED_VERSION );
		if(versiones.length == 0){
			System.out.println("No se generaron elementos que necesiten integracion interactiva");
			return;
		}
		String listaVersiones = "";
		for(JCaVersion version : versiones){
			listaVersiones += "<br>" + version.getPathFullName() + "\\" + version.getName();
		}
		cuerpo = cuerpo.replaceAll("\\[package\\]", paquete);
		cuerpo = cuerpo.replaceAll("\\[state\\]", contexto.getStateName());
		cuerpo = cuerpo.replaceAll("\\[project\\]", contexto.getProjectName());
		cuerpo = cuerpo.replaceAll("\\[desarrollador\\]", usuarios[1].get_usuario());
		cuerpo = cuerpo.replaceAll("\\[lider\\]", usuarios[0].get_usuario());
		cuerpo = cuerpo.replaceAll("\\[lista\\]", listaVersiones.replaceAll("\\\\", "\\\\\\\\"));
		String cadenaCorreos = "";
		for(DatosUsuario usr : usuarios){
			if(!usr.get_correo().equals("")){
				cadenaCorreos +=" "+  usr.get_correo(); 
			}
		}
		if("".equals(cadenaCorreos)){
			throw new IllegalArgumentException("El formulario del paquete " + paquete + " no tiene asignado un Desarrollador ni el Lider de Version");
		}else{
			cadenaCorreos = cadenaCorreos.substring(1) + cr.consultaCorreosGestorVersiones(contexto.getProjectId());
		}
		EnviaNotificacion.envia(cadenaCorreos, cuerpo, asunto);
		
	}
	
	/**
	 * Invocación principal, se hace a través de la clase {@link Main}
	 * @param args Arreglo de argumentos, el arreglo debe contener el broker, la ruta del archivo .dfo con las credenciales para iniciar sesión en el broker, proyecto, estado y el prefijo para el nombre del snapshot 
	 * @throws JCaHarvestException En caso de error con la obtencion de los elementos con etiqueta M 
	 * @throws IOException En caso de error al leer el cuerpo del mensaje ya que este se obtiene del Standard Input
	 * @throws InterruptedException En caso de error al invocar el proceso de notificación
	 * 
	 */
	public static void _main(String[] args) throws IOException, JCaHarvestException, InterruptedException{
		if(args.length != 6){
			System.err.println("El uso correcto es comando broker archivoDfo proyecto estado paquete asunto");
			System.exit(-1);
		}
		
		String cuerpo = "";
		
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	 
			String input;
			
			while((input=br.readLine())!=null){
				cuerpo += input;
			}
			NotificacionIntegracion ni = new NotificacionIntegracion(args[0], args[1], args[2], args[3]);

			String pack = args[4].trim();
			String[] packs = pack.split("\t");
			for(int i = 0; i < packs.length; i++){
				ni.notificaTagM(packs[i].trim(), cuerpo, args[5]);
			}
		
	}
}

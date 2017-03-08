package com.ca.harvest.udp;

import com.ca.harvest.UDPException;
import com.ca.harvest.jhsdk.JCaContext;
import com.ca.harvest.jhsdk.JCaPackage;
import com.ca.harvest.jhsdk.JCaVersion;
import com.ca.harvest.jhsdk.hutils.JCaHarvestException;

/**
 * Clase para la UDP que valida que no se tengan versiones enteras al momento de hacer Demote al estado de Incidencias o al estade de Desarrollo
 * @author Ernesto Peña
 *
 */
public class ValidaVersionesEnteras {
	JCaContext contexto;
	/**
	 * Constructor
	 * @param broker Broker de harvest
	 * @param rutaDfo Ruta completa al archivo dfo de credenciales
	 * @param proyecto Proyecto de Harvest
	 * @param estado Estado de Harvest
	 * @throws JCaHarvestException
	 */
	
	public ValidaVersionesEnteras (String broker, String rutaDfo, String proyecto, String estado) throws JCaHarvestException{
		com.ca.harvest.conexion.Contexto con = new com.ca.harvest.conexion.Contexto(broker, rutaDfo);
		con.setContexto(proyecto, estado);
		contexto = con.getContext();
	}
	
	/**
	 * Método que valida que no se tengan versiones enteras al momento de hacer Demote al estado de Incidencias o al estade de Desarrollo
	 * @param paquete Nombre del paquete
	 * @throws JCaHarvestException En caso de error al obtener los elementos del paquete
	 * @throws UDPException En caso de detectar que el paquete que se intenta regresar tiene versiones enteras.
	 */
	public void valida(String paquete) throws JCaHarvestException, UDPException{
		JCaPackage pack = contexto.getPackageByName(paquete);
		JCaVersion[] versiones = pack.getVersionList();
		
		for(int i = 0; i< versiones.length; i++){
			//System.out.println(versiones[i].getMappedVersionName());
			if(versiones[i].getMappedVersionName().indexOf(".") < 0){
				throw new UDPException("El paquete " + paquete + " cuenta con versiones enteras, no es posible regresarlo");
			}
		}
	}
	
	/**
	 * Invocación principal, se hace a través de la clase {@link Main}
	 * @param args Arreglo de argumentos, el arreglo debe contener el broker, la ruta del archivo .dfo con las credenciales para iniciar sesión en el broker, proyecto, estado y paquete
	 * @throws JCaHarvestException En caso de error al obtener los elementos del paquete
	 * @throws UDPException En caso de detectar que el paquete que se intenta regresar tiene versiones enteras.
	 * 
	 */
	public static void _main(String[] args) throws JCaHarvestException, UDPException{
		if(args.length != 5){
			System.err.println("El uso adecuado es comando broker rutaDfo proyecto estado paquete");									
			System.exit(-1); 
		}
		 
		ValidaVersionesEnteras vve = new ValidaVersionesEnteras(args[0], args[1], args[2], args[3]);
		String pack = args[4].trim();
		String[] packs = pack.split("\t");
		for(int i = 0; i < packs.length; i++){ 
			vve.valida(packs[i].trim());
		}
	}
}

package com.ca.harvest.udp;


import com.ca.harvest.UDPException;
import com.ca.harvest.jhsdk.JCaContext;
import com.ca.harvest.jhsdk.JCaPackage;
import com.ca.harvest.jhsdk.JCaSQL;
import com.ca.harvest.jhsdk.hutils.JCaContainer;
import com.ca.harvest.jhsdk.hutils.JCaHarvestException;

/**
 * Clase para la UDP que valida que no se tengan dependencias de paquetes en el estado PreProduccion al promover un paquete del tipo incidencia al estado de Producción
 * @author Ernesto Peña
 *
 */
public class ValidaPromocionIncidencia {
	JCaContext contexto;
	/**
	 * Constructor
	 * @param broker Broker de harvest
	 * @param rutaDfo Ruta completa al archivo dfo de credenciales
	 * @param proyecto Proyecto de Harvest
	 * @param estado Estado de Harvest
	 * @throws JCaHarvestException
	 */
	public ValidaPromocionIncidencia (String broker, String rutaDfo, String proyecto, String estado) throws JCaHarvestException{
		com.ca.harvest.conexion.Contexto con = new com.ca.harvest.conexion.Contexto(broker, rutaDfo);
		con.setContexto(proyecto, estado);
		contexto = con.getContext();
	}
	
	/**
	 * Método que valida que no se tengan dependencias de paquetes en el estado PreProduccion al promover un paquete del tipo incidencia al estado de Producción 
	 * @param paquete Nombre del paquete
	 * @param estadoPreProd Nombre del estado de Pre Produccion
	 * @throws JCaHarvestException En caso de error al obtener los paquetes
	 * @throws UDPException En caso de que se detecten dependencias en el estado de Preproducción
	 */
	public void Valida(String paquete, String estadoPreProd) throws JCaHarvestException, UDPException{

		JCaPackage _pack  = contexto.getPackageByName(paquete);
		
		contexto.setState(estadoPreProd);
		JCaSQL sql = contexto.getSQL();
		int error ;
		int packObjId = _pack.getObjId();
		String consulta = "SELECT DISTINCT p.PACKAGENAME  from harpackage p " + 
				"join HARVERSIONS v on v.PACKAGEOBJID = p.PACKAGEOBJID " + 
				"join HARVERSIONS v2 on v.ITEMOBJID = v2.ITEMOBJID " + 
				"where stateobjid = " + contexto.getStateId() + " and v2.PACKAGEOBJID = " + packObjId + " and v.PACKAGEOBJID <> " + packObjId + " " + 
				"and not v.MAPPEDVERSION like '%.%' and not v2.MAPPEDVERSION like '%.%'   " + 
				"and to_number(v.MAPPEDVERSION) < to_number(v2.MAPPEDVERSION) "; 
				
				
		sql.setSQLStatement(consulta);
		error = sql.execute();
		if(error != 0){
			throw new UDPException("Hubo un error con la consulta. Codigo de error: " + error);
		}
		JCaContainer contenedor = sql.getSQLResult();
		if(contenedor.isEmpty()){
			
			return;
		}else{
			String nombrePaquete = contenedor.getString("PACKAGENAME");
			throw new UDPException("El paquete " + paquete + " tiene dependencias con al menos el paquete " + nombrePaquete + " que se encuentra en el estado " + estadoPreProd);
		}
	}
	
	/**
	 * Invocación principal, se hace a través de la clase {@link Main}
	 * @param args Arreglo de argumentos, el arreglo debe contener el broker, la ruta del archivo .dfo con las credenciales para iniciar sesión en el broker, proyecto, estado, paquete y el nombre del estado de preproduccion
	 * @throws JCaHarvestException En caso de error al obtener los paquetes
	 * @throws UDPException En caso de que se detecten dependencias en el estado de Preproducción
	 * 
	 */
	public static void _main(String[] args) throws JCaHarvestException, UDPException {
		
		if(args.length != 6){
			System.err.println("El uso correcto es comando broker archivoDfo proyecto estado paquete estadoPreProd");
			System.exit(-1);
		}	
		
		ValidaPromocionIncidencia vp = new ValidaPromocionIncidencia(args[0], args[1], args[2], args[3]);
		String pack = args[4].trim();
		String[] packs = pack.split("\t");
		for(int i = 0; i < packs.length; i++){
			vp.Valida(packs[i].trim(), args[5]);
		}
	}
}

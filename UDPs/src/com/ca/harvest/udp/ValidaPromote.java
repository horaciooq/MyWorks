package com.ca.harvest.udp;


import com.ca.harvest.UDPException;
import com.ca.harvest.jhsdk.JCaContext;
import com.ca.harvest.jhsdk.JCaPackage;
import com.ca.harvest.jhsdk.hutils.JCaHarvestException;
import com.ca.harvest.util.ConsultaRegistros;

/**
 * Clase para la UDP que valida las promociones segun el tipo de paquete que sea, si es del tipo Proyecto o Mantenimiento o si es del tipo Incidencia 
 * @author Ernesto Peña
 *
 */
public class ValidaPromote {
	JCaContext contexto;
	/**
	 * Constructor
	 * @param broker Broker de harvest
	 * @param rutaDfo Ruta completa al archivo dfo de credenciales
	 * @param proyecto Proyecto de Harvest
	 * @param estado Estado de Harvest
	 * @throws JCaHarvestException
	 */
	public ValidaPromote (String broker, String rutaDfo, String proyecto, String estado) throws JCaHarvestException{
		com.ca.harvest.conexion.Contexto con = new com.ca.harvest.conexion.Contexto(broker, rutaDfo);
		con.setContexto(proyecto, estado);
		contexto = con.getContext();
	}
	/**
	 * Método que valida el tipo de paquete que es y el estado al que va.
	 * @param paquete Nombre del paquete
	 * @param siguienteEstado Nombre del estado al que va
	 * @throws JCaHarvestException En caso de obtener el tipo de paquete
	 * @throws UDPException En caso de que el Estado destino no sea el indicado
	 */
	public void Valida(String paquete, String siguienteEstado) throws JCaHarvestException, UDPException{
		ConsultaRegistros cons = new ConsultaRegistros(contexto);
		JCaPackage pack = contexto.getPackageByName(paquete);
		String tipo = cons.tipoRequerimientoPaquete(pack);
		if(tipo == null || tipo.equals("")){
			throw new IllegalArgumentException("La forma no tiene asignado el tipo de requerimiento");
		}
		if(tipo.equals("Incidencia") && (siguienteEstado.equals("Produccion") || siguienteEstado.equals("Incidencias") || siguienteEstado.equals("Calidad")) || (!tipo.equals("Incidencia") && (siguienteEstado.equals("Pre Produccion") || siguienteEstado.equals("Desarrollo")))){
			return;
		}else{
			if(siguienteEstado.equals("Produccion") || siguienteEstado.equals("Pre Produccion")){
				throw new UDPException("El paquete " + paquete + " no se puede promover a \"" + siguienteEstado + "\" por ser del tipo \"" + tipo + "\"\nSe debe promover a " + (tipo.equals("Incidencia") ? "Produccion":"Pre Produccion") );
			}
			else{
				if(tipo.equals("Incidencia")){
					
					throw new UDPException("El paquete " + paquete + " no se puede regresar a \"" + siguienteEstado + "\" por ser del tipo \"" + tipo + "\"\nSe debe mover a " +  (siguienteEstado.equals("Pre Produccion") ? "Calidad":"Incidencias") );
				}else{
					throw new UDPException("El paquete " + paquete + " no se puede regresar a \"" + siguienteEstado + "\" por ser del tipo \"" + tipo + "\"\nSe debe mover a " +  (siguienteEstado.equals("Incidencias") ? "Desarrollo":"Pre Produccion") );
				}
				
			}
		}
	}
	/**
	 * Invocación principal, se hace a través de la clase {@link Main}
	 * @param args Arreglo de argumentos, el arreglo debe contener el broker, la ruta del archivo .dfo con las credenciales para iniciar sesión en el broker, proyecto, estado, paquete y el estado destino al que se va a mover el paquete
	 * @throws JCaHarvestException En caso de obtener el tipo de paquete
	 * @throws UDPException En caso de que el Estado destino no sea el indicado 
	 */
	public static void _main(String[] args) throws JCaHarvestException, UDPException {
		
		if(args.length != 6){
			System.err.println("El uso correcto es comando broker archivoDfo proyecto estado paquete siguienteEstado");
			System.exit(-1);
		}	
		
		ValidaPromote vp = new ValidaPromote(args[0], args[1], args[2], args[3]);
		String pack = args[4].trim();
		String[] packs = pack.split("\t");
		for(int i = 0; i < packs.length; i++){
			vp.Valida(packs[i].trim(), args[5]);
		}
	
		
		
	}

}

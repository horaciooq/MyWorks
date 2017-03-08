package com.ca.harvest.udp;


import java.util.HashMap;

import com.ca.harvest.UDPException;
import com.ca.harvest.jhsdk.JCaContext;
import com.ca.harvest.jhsdk.JCaPackage;
import com.ca.harvest.jhsdk.hutils.JCaHarvestException;
import com.ca.harvest.util.ConsultaRegistros;
/**
 * Clase para la UDP que valida los campos requeridos según el estado en el que se encuentre el paquete.
 * @author Ernesto Peña
 *
 */
public class ValidaCamposRequeridos {
	JCaContext contexto;
	/**
	 * Constructor
	 * @param broker Broker de harvest
	 * @param rutaDfo Ruta completa al archivo dfo de credenciales
	 * @param proyecto Proyecto de Harvest
	 * @param estado Estado de Harvest
	 * @throws JCaHarvestException En caso de error al poner el contexto.
	 */
	public ValidaCamposRequeridos (String broker, String rutaDfo, String proyecto, String estado) throws JCaHarvestException{
		com.ca.harvest.conexion.Contexto con = new com.ca.harvest.conexion.Contexto(broker, rutaDfo);
		con.setContexto(proyecto, estado);
		contexto = con.getContext();
	}
	
	/**
	 * Método que hace la validación de los campos requeridos del formulario dependiendo del estado en el que se encuentre el paquete.
	 * @param paquete Nombre del paquete
	 * @param posicion Nos indica el tipo de validación dependiendo del proceso
	 * @throws JCaHarvestException En caso de error al obtener los datos del formulario
	 * @throws UDPException en caso de que falten campos obligatorios en el formulario
	 */
	public void valida(String paquete, String posicion) throws JCaHarvestException, UDPException{
		
		ConsultaRegistros cons = new ConsultaRegistros(contexto); 
		JCaPackage pack = contexto.getPackageByName(paquete);
		HashMap<String, String> forma = cons.formaPaquete(pack);
		boolean conError = false;
		if(posicion.equals("Checkout")){	
			if(forma.get("NOMBREAPLICACION") == null || forma.get("NOMBREAPLICACION").equals("")){
				System.err.println("El campo del formulario \"Nombre de la Aplicacion\" es obligatorio.");
				conError = true;
			}
			if(forma.get("IDPROYECTO") == null || forma.get("IDPROYECTO").equals("")){
				System.err.println("El campo del formulario \"Id del Proyecto\" es obligatorio.");
				conError = true;
			}
	
			if(forma.get("DESARROLLADOR") == null || forma.get("DESARROLLADOR").equals("")){
				System.err.println("El campo del formulario \"Desarrollador\" es obligatorio.");
				conError = true;
			}
	
			if(forma.get("TIPOREQUERIMIENTO") == null || forma.get("TIPOREQUERIMIENTO").equals("")){
				System.err.println("El campo del formulario \"Tipo de Requerimiento\" es obligatorio.");
				conError = true;
			}
			if(forma.get("DESCREQUERIMIENTO") == null || forma.get("DESCREQUERIMIENTO").equals("")){
				System.err.println("El campo del formulario \"Descripcion Breve del Requerimiento\" es obligatorio.");
				conError = true;
			}
			
		}else if(posicion.equals("PromoteCalidad")){
			if(forma.get("PRUEBASUNITARIAS") == null || forma.get("PRUEBASUNITARIAS").equals("")){
				System.err.println("El campo del formulario \"Pruebas Unitarias\" es obligatorio.");
				conError = true;
			}
			//Se valida que el campo PRUEBASCAJABLANCA sea igual a "Aprobado", en cualquier otro caso se marca error
			if(!"Aprobado".equals(forma.get("PRUEBASCAJABLANCA"))){
				System.err.println("Se deben ejecutar las pruebas de caja blanca satisfactoriamente.");
				conError = true;
			}
		}else if(posicion.equals("PromoteProduccion")){
			if(forma.get("CONTROLCAMBIOS") == null || forma.get("CONTROLCAMBIOS").equals("")){
				System.err.println("El campo del formulario \"Control de Cambios Asociado\" es obligatorio.");
				conError = true;
			}	
		}else{
			System.err.println("El uso correcto es comando broker archivoDfo proyecto estado paquete <Checkout|PromoteCalidad|PromoteProduccion>");
			System.exit(-1);
		}
		
		if(conError){
			throw new UDPException("Faltan campos obligatorias en el formulario del paquete " + paquete);
		}
		
	}
	

	/**
	 * Invocación principal, se hace a través de la clase {@link Main}
	 * @param args Arreglo de argumentos, el arreglo debe contener el broker, la ruta del archivo .dfo con las credenciales para iniciar sesión en el broker, proyecto, estado, paquete y el proceso el cual puede ser Checkout, PromoteCalidad o PromoteProduccion 
	 * @throws JCaHarvestException En caso de error con la obtencion de los datos del formulario
	 * @throws UDPException En caso de faltar campos obligatorios
	 * 
	 */
	public static void _main(String[] args) throws JCaHarvestException, UDPException {
		
		if(args.length != 6){
			System.err.println("El uso correcto es comando broker archivoDfo proyecto estado paquete <Checkout|PromoteCalidad|PromoteProduccion");
			System.exit(-1);
		}	
		
		ValidaCamposRequeridos vp = new ValidaCamposRequeridos(args[0], args[1], args[2], args[3]);
		String pack = args[4].trim();
		String[] packs = pack.split("\t");
		for(int i = 0; i < packs.length; i++){
			vp.valida(packs[i].trim(), args[5]);
		}	
	}
}

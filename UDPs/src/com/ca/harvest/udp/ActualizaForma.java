package com.ca.harvest.udp;

import java.util.Calendar;

import com.ca.harvest.jhsdk.JCaContext;
import com.ca.harvest.jhsdk.JCaForm;
import com.ca.harvest.jhsdk.JCaPackage;
import com.ca.harvest.jhsdk.hutils.JCaData;
import com.ca.harvest.jhsdk.hutils.JCaHarvestException;
import com.ca.harvest.jhsdk.hutils.JCaTimeStamp;
/**
 * UDP para la automatización de las formas
 * @author Ernesto Peña
 *
 */
public class ActualizaForma {
	JCaContext contexto;
	
	/**
	 * Constructor
	 * @param broker Broker de harvest
	 * @param rutaDfo Ruta completa al archivo dfo de credenciales
	 * @param proyecto Proyecto de Harvest
	 * @param estado Estado de Harvest
	 * @throws JCaHarvestException
	 */
	public ActualizaForma (String broker, String rutaDfo, String proyecto, String estado) throws JCaHarvestException{
		com.ca.harvest.conexion.Contexto con = new com.ca.harvest.conexion.Contexto(broker, rutaDfo);
		con.setContexto(proyecto, estado);
		contexto = con.getContext();
	}
	
	/**
	 * Actualiza la información del paquete
	 * @param paquete Paquete al que se le actualizará el formulario
	 * @param usuario nombre del usuario que se agregará en el campo Responsable de pase a Producción
	 * @throws JCaHarvestException En caso de error al actualizar el formulario
	 */
	public void actualiza(String paquete, String usuario, String app) throws JCaHarvestException{
		String estado =  contexto.getStateName();
		JCaPackage pack = contexto.getPackageByName(paquete);
		JCaForm[] formas =  pack.getFormListAsList(false);
		Calendar date = Calendar.getInstance();
		JCaTimeStamp ts = new JCaTimeStamp(date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1,date.get(Calendar.DAY_OF_MONTH), date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), date.get(Calendar.SECOND));
		JCaData responsable;
		JCaData fecha;
		for(JCaForm forma : formas){
			if("Descripcion del paquete".equals(forma.getFormTypeName())){
				responsable = new JCaData();
				responsable.setString(usuario);

				forma.get(forma.getFormObjID());
				if("Calidad".equals(estado) && !app.equals("")){
					if(!forma.setColumnValue("ANALISTAPRUEBAS", responsable)){
						System.err.println("Hubo un error al llenar el campo Analista de pruebas en el formulario");
					}
				}else if ("Integracion".equals(estado)){
					JCaData responsable2 = new JCaData();
					responsable2.setString("");
					if(!forma.setColumnValue("ANALISTAPRUEBAS", responsable2)){
						System.err.println("Hubo un error al llenar el campo Analista de pruebas en el formulario");
					}
				}else if ("Pre Produccion".equals(estado) || "Calidad".equals(estado)){
					JCaData responsable2 = new JCaData();
					responsable2.setString("");
					fecha = new JCaData();
					fecha.setTimeStamp(JCaTimeStamp.NULL_JCATIMESTAMP());
					if(!forma.setColumnValue("RESPONSABLEPASOPROD", responsable2)){
						System.err.println("Hubo un error al actualizar el campo del responsable de paso a produccion en el formulario");
					}
					
				}else if ("Produccion".equals(estado)){
					fecha = new JCaData();
					fecha.setTimeStamp(ts);
					if(!forma.setColumnValue("RESPONSABLEPASOPROD", responsable)){
						System.err.println("Hubo un error al llenar el campo del responsable de paso a produccion en la forma");
					}
					if(!forma.setColumnValue("FECHAPRODUCCION", fecha)){
						System.err.println("Hubo un error al llenar el campo del fecha de paso a produccion en la forma");
					}
				}
				forma.update();
			}
		}
	}
	
	/**
	 * Invocación principal, se hace a través de la clase {@link Main}
	 * @param args Arreglo de argumentos, el arreglo debe contener el broker, la ruta del archivo .dfo con las credenciales para iniciar sesión en el broker, proyecto, estado, paquete, usuario involucrado en la actualizacion de la forma y opcionalmente la cadena "approve" la cual nos indica que la actualizacion se hace en el proceso  approve 
	 * @throws JCaHarvestException En caso de error al actualizar el formulario
	 */
	public static void _main(String[] args) throws JCaHarvestException{
		if(args.length != 6 && args.length != 7){
			System.err.println("El uso adecuado es comando broker rutaDfo proyecto estado paquete usuario [approve]");
												
			System.exit(-1); 
			
		}
		 
		ActualizaForma af = new ActualizaForma(args[0], args[1], args[2], args[3]);
		String pack = args[4].trim();
		String[] packs = pack.split("\t");
		String approve = args.length == 7 ? args[6] : "";
		for(int i = 0; i < packs.length; i++){
			af.actualiza(packs[i].trim(), args[5], approve);
		}
		 
	}
}

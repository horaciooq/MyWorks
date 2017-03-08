package com.ca.harvest.udp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.ca.harvest.conexion.Contexto;
import com.ca.harvest.jhsdk.JCaContext;
import com.ca.harvest.jhsdk.JCaCreatePackage;
import com.ca.harvest.jhsdk.JCaForm;
import com.ca.harvest.jhsdk.JCaFormType;
import com.ca.harvest.jhsdk.JCaPackage;
import com.ca.harvest.jhsdk.hutils.JCaAttrKey;
import com.ca.harvest.jhsdk.hutils.JCaContainer;
import com.ca.harvest.jhsdk.hutils.JCaData;
import com.ca.harvest.jhsdk.hutils.JCaHarvestException;
import com.ca.harvest.jhsdk.hutils.JCaTimeStamp;

/**
 * Clase para la UDP que crea el paquete.
 * @author Ernesto Peña
 *
 */
public class CrearPaquete {
	private JCaContext contexto;
	/**
	 * Constructor
	 * @param broker Broker de harvest
	 * @param rutaDfo Ruta completa al archivo dfo de credenciales
	 * @param proyecto Proyecto de Harvest
	 * @param estado Estado de Harvest
	 * @throws JCaHarvestException
	 */
	public CrearPaquete(String broker, String rutaDfo, String proyecto, String estado) throws JCaHarvestException{
		Contexto cont = new Contexto(broker, rutaDfo);
		cont.setContexto(proyecto, estado);
		contexto = cont.getContext();
		
	}
	
	/**
	 * Crea el paquete con el formato PRE.aaaa.mm.dd.hhMM 
	 * @param prefijo Prefijo para el nombre del paquete.
	 * @param userName Nombre de usuario al que se le asignará el paquete
	 * @throws JCaHarvestException En caso de error al crear el paquete
	 */
	public void ejecuta(String prefijo, String userName) throws JCaHarvestException{
		Calendar date = Calendar.getInstance();
		DateFormat dateFormat = new SimpleDateFormat(".yy.MM.dd.HHmm");
		String nombre = prefijo + dateFormat.format(date.getTime());
		JCaContainer procesos = contexto.getProcessList();
		int[] idxProceso = procesos.find(JCaAttrKey.CA_ATTRKEY_PROCTYPE, "CreatePackageProcess");
		if(idxProceso.length == 0){
			throw new IllegalArgumentException("El proyecto seleccionado no tiene un proceso del tipo Create Package, posiblemente se tenga una configuracion erronea");
		}
		//String[] llaves = procesos.getKeyList();
		int processObjId = procesos.getInt(JCaAttrKey.CA_ATTRKEY_OBJID, idxProceso[0]);
		
		
		contexto.setCreatePackage(processObjId);
		JCaCreatePackage cp = contexto.getCreatePackage();
		
		cp.setPackageName(nombre);
		cp.setAssignedUserName(userName);
		JCaFormType[] tipos = contexto.getHarvestSession().getFormTypeList(false);
		JCaFormType tipo = null;
		for (JCaFormType _tipo : tipos){
			if("Descripcion del paquete".equals(_tipo.getName())){
				tipo = _tipo;
				break;
			}
		}
		if(tipo == null){
			throw new IllegalArgumentException("No se pudo obtener la forma");
		}
		int error = cp.execute();
		if(error == 0){
			
			JCaPackage pack = contexto.getPackageByName(nombre);
			JCaForm forma = new JCaForm(tipo, nombre, pack);
			JCaData fecha = new JCaData();
			
			JCaData pruebascajablanca = new JCaData();

			JCaData lider = new JCaData();
			
			lider.setString(userName);
			
			fecha.setTimeStamp(new JCaTimeStamp(date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1,date.get(Calendar.DAY_OF_MONTH), date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), date.get(Calendar.SECOND)));
			pruebascajablanca.setString("No aprobado");
			
			if(!forma.setColumnValue("FECHACREACION", fecha)){
				System.err.println("No se pudo poner la fecha de creacion en la forma");
			}
			if(!forma.setColumnValue("LIDERVERSION", lider)){
				System.err.println("No se pudo poner el lider en la forma");
			}
			//Modificacion para agregar el valor predeterminado del campo PRUEBASCAJABLANCA 
			if(!forma.setColumnValue("PRUEBASCAJABLANCA", pruebascajablanca)){
				System.err.println("No se pudo poner la prueba de caja blanca");
			}
			
			forma.update();
		}
	}
	
	/**
	 * Invocación principal, se hace a través de la clase {@link Main}
	 * @param args Arreglo de argumentos, el arreglo debe contener el broker, la ruta del archivo .dfo con las credenciales para iniciar sesión en el broker, proyecto, estado y el prefijo para el nombre del paquete y el estado donde se generará el paquete.
	 * @throws JCaHarvestException En caso de error al crear el paquete
	 */
	public static void _main(String[] args) throws JCaHarvestException{
		if(args.length != 6){
			System.err.println("El uso adecuado es comando broker rutaDfo proyecto estado prefijo usuario");
			System.exit(-1);
		} 
		CrearPaquete cp = new CrearPaquete(args[0], args[1], args[2], args[3]);
		cp.ejecuta(args[4], args[5]);
	 
	}
}

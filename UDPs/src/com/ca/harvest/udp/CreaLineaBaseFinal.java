package com.ca.harvest.udp;

import java.util.Calendar;


import com.ca.harvest.jhsdk.JCaContext;
import com.ca.harvest.jhsdk.JCaSQL;
import com.ca.harvest.jhsdk.JCaTakeSnapshot;
import com.ca.harvest.jhsdk.hutils.JCaAttrKey;
import com.ca.harvest.jhsdk.hutils.JCaContainer;
import com.ca.harvest.jhsdk.hutils.JCaHarvestException;
import com.ca.harvest.jhsdk.hutils.JCaTimeStamp;

/**
 * UDP para crear el Snapshot para la línea base final
 * @author Ernesto Peña
 *
 */
public class CreaLineaBaseFinal{
	JCaContext contexto;
	/**
	 * Constructor
	 * @param broker Broker de harvest
	 * @param rutaDfo Ruta completa al archivo dfo de credenciales para iniciar sesión
	 * @param proyecto Proyecto de Harvest
	 * @param estado Estado de Harvest
	 * @throws JCaHarvestException En caso de error al poner el contexto
	 */
	public CreaLineaBaseFinal (String broker, String rutaDfo, String proyecto, String estado) throws JCaHarvestException{
		com.ca.harvest.conexion.Contexto con = new com.ca.harvest.conexion.Contexto(broker, rutaDfo);
		con.setContexto(proyecto, estado);
		contexto = con.getContext();
	}
	
	/**
	 * Crea el snapshot contando los paquetes del tipo Proyecto, Mantenimiento e Incidencia que se encuentren en el estado de Producción
	 * @param prefijo Prefijo del snapshot
	 * @throws JCaHarvestException En caso de error al crear el snapshot 
	 * @throws IllegalArgumentException En caso de que el proyecto de Harvest no este debidamente configurado y no cuente con el proceso del tipo Take Snapshot en el estado de Producción.
	 */
	public void ejecuta(String prefijo) throws JCaHarvestException, IllegalArgumentException{ 
		JCaContainer procesos = contexto.getProcessList();
		int[] procesoidx = procesos.find(JCaAttrKey.CA_ATTRKEY_PROCTYPE, "TakeSnapshotProcess");
		if(procesoidx.length ==0 ){
			throw new IllegalArgumentException("El estado no contiene un proceso del tipo Take Snapshot");
		}
		contexto.setTakeSnapshot(procesos.getInt(JCaAttrKey.CA_ATTRKEY_OBJID, procesoidx[0]));
		JCaTakeSnapshot ts = contexto.getTakeSnapshot();
		
		
		int anio = Calendar.getInstance().get(Calendar.YEAR);
		
		///////////////////////////////
		/*
		int idEstado = contexto.getStateId();
		JCaSQL sql = contexto.getSQL();
		String  _incidencias = cuentaPaquetes("Incidencia", idEstado, anio, sql);
		String  _proys = cuentaPaquetes("Proyecto", idEstado, anio, sql);
		String  _mantes = cuentaPaquetes("Mantenimiento", idEstado, anio, sql);
		String nombre =prefijo + "." + anio + "." +  _proys +  "." +  _mantes + "." + _incidencias ;
		ts.setCanViewExternallyFlag(true);
		int error = ts.execute(nombre, JCaTimeStamp.NULL_JCATIMESTAMP(), null);
		if (error != 0){
			
			throw new JCaHarvestException("Codigo de error: " + error);
		}*/
		////////////////////////////////
		String consulta = "SELECT SUM( CASE fd.TIPOREQUERIMIENTO WHEN 'Incidencia' then 1 else 0 end) INCIDENCIAS, " +
			"SUM( CASE fd.TIPOREQUERIMIENTO WHEN 'Proyecto' then 1 else 0 end) PROYECTOS, " +
			"SUM( CASE fd.TIPOREQUERIMIENTO WHEN 'Mantenimiento' then 1 else 0 end) MANTENIMIENTOS " +
			"FROM hardescpaquete fd " +
			"JOIN HARASSOCPKG f ON fd.FORMOBJID = f.FORMOBJID " +
			"JOIN HARPACKAGE p on f.ASSOCPKGID = p.PACKAGEOBJID " +
			"WHERE p.STATEOBJID = " + contexto.getStateId() + " AND to_char(fd.FECHACREACION , 'YYYY') = '" + anio+ "'";
		JCaSQL sql = contexto.getSQL();
		sql.setSQLStatement(consulta);
		int error = sql.execute();
		if(error == 0){
			ts.setCanViewExternallyFlag(true);

			JCaContainer res = sql.getSQLResult();
			String _incidencias =haceCero(res.getInt("INCIDENCIAS"));
			String _mantes =haceCero(res.getInt("MANTENIMIENTOS"));
			String _proys =haceCero(res.getInt("PROYECTOS"));
			
			String nombre =prefijo + "." + anio + "." +  _proys +  "." +  _mantes + "." + _incidencias ;
			error = ts.execute(nombre, JCaTimeStamp.NULL_JCATIMESTAMP(), null);
			if (error != 0){
				
				throw new JCaHarvestException("Codigo de error: " + error);
			}
		}
		
		///////////////////////////////////
		
	}
	
	/*private String cuentaPaquetes(String tipo, int  idEstado, int anio, JCaSQL sql) throws UDPException{
		String consulta = "SELECT COUNT(DISTINCT fd.IDPROYECTO) CONTEO " +
				"FROM hardescpaquete fd " +
				"  JOIN HARASSOCPKG f ON fd.FORMOBJID = f.FORMOBJID " +
				"  JOIN HARPACKAGE p ON f.ASSOCPKGID = p.PACKAGEOBJID " +
				"WHERE p.STATEOBJID = " + idEstado +" " +
				"  AND TO_CHAR(fd.FECHACREACION , 'YYYY') = '" +anio + "' " +
				"  AND fd.TIPOREQUERIMIENTO = '" + tipo + "'";
		 
		sql.setSQLStatement(consulta);
		int error = sql.execute();
		if(error == 0){
			 
			JCaContainer res = sql.getSQLResult();
			return haceCero(res.getInt("CONTEO"));
		
		}
		throw new UDPException("Hubo un error con la consulta para obtener los " + tipo + " error: " + error);
	}*/
	
	private String haceCero(int valor){
		return String.format("%02d", valor < 0 ? 0 : valor);
	}
	
	/**
	 * Invocación principal, se hace a través de la clase {@link Main}
	 * @param args Arreglo de argumentos, el arreglo debe contener el broker, la ruta del archivo .dfo con las credenciales para iniciar sesión en el broker, proyecto, estado y el prefijo para el nombre del snapshot
	 * @throws JCaHarvestException En caso de error al crear el snapshot 
	 * @throws IllegalArgumentException En caso de que el proyecto de Harvest no este debidamente configurado y no cuente con el proceso del tipo Take Snapshot en el estado de Producción.
	 */
	public static void _main(String[] args) throws JCaHarvestException, IllegalArgumentException{
		if(args.length != 5){
			System.err.println("El uso adecuado es comando broker rutaDfo proyecto estado prefijo");
			System.exit(-1);
		}
		
		CreaLineaBaseFinal lbf = new CreaLineaBaseFinal(args[0], args[1], args[2], args[3]);
		lbf.ejecuta(args[4]);
		
	}
}

package com.ca.harvest.conexion;
import com.ca.harvest.jhsdk.JCaContext;
import com.ca.harvest.jhsdk.JCaHarvest;
import com.ca.harvest.jhsdk.hutils.JCaHarvestException;
/**
 * Clase necesaria para generar el contexto de Harvest.
 * El contexto en Harvest nos indica en que Broker, Proyecto, Estado y opcionalmente el Paquete en que nos encontramos.
 * @author Ernesto Pe�a
 *
 */
public class Contexto {
	private JCaContext _contexto;
	private boolean _contextoOk;
	
	/**
	 * Constructor
	 * @param broker Broker en el que se va a iniciar sesi�n
	 * @param rutaDfo Ruta completa del archivo .dfo que almacena las credenciales para iniciar sesi�n en el broker
	 * @throws JCaHarvestException Lanza una excepci�n en caso de error de conexi�n.
	 */
	public Contexto(String broker, String rutaDfo ) throws JCaHarvestException{
		_contextoOk = false;
		JCaHarvest harv = new JCaHarvest();
		int error = harv.loginUsingDfo(broker, rutaDfo);
		if(error  == 0){ //no error
			_contexto = harv.getContext();
			
		}else{ //error de login
			throw new JCaHarvestException("Hubo un error al conectarse, el codigo de error es " + error);
		}
		
	}
	
	/**
	 * En este m�todo se pone el contexto en Harvest una vez que ya se inici� la sesi�n
	 * @param proyecto Proyecto de Harvest
	 * @param estado Estado de Harvest
	 * @throws JCaHarvestException Lanza una excepci�n en caso de que el proyecto o estado no existan
	 */
	public void setContexto(String proyecto, String estado) throws JCaHarvestException{
		_contexto.setProject(proyecto);
		_contexto.setState(estado);
		_contextoOk = true;
	}
	
	/**
	 * M�todo que nos devuelve el objeto JCaContext con el contexto debidamente configurado.
	 * @return Objeto JCaContext con el contexto debidamente configurado.
	 * @throws JCaHarvestException En caso de que el contexto no este debidamente configurado.
	 */
	public JCaContext getContext() throws JCaHarvestException{
		
		if(_contextoOk){
			return _contexto;
		}else{
			throw new JCaHarvestException("El contexto no esta debidamente configurado");
		}
	}
}

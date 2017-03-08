package com.ca.harvest.udp;

import com.ca.harvest.UDPException;
import com.ca.harvest.conexion.Contexto;
import com.ca.harvest.jhsdk.JCaContext;
import com.ca.harvest.jhsdk.JCaForm;
import com.ca.harvest.jhsdk.JCaPackage;
import com.ca.harvest.jhsdk.JCaSQL;
import com.ca.harvest.jhsdk.hutils.JCaContainer;
import com.ca.harvest.jhsdk.hutils.JCaData;
import com.ca.harvest.jhsdk.hutils.JCaHarvestException;


/**
 * Pone el cambpo PRUEBASCAJABLANCA a "No aprobado"
 * @author Ernesto Peña
 *
 */
public class CampoCajaBlanca {

	private JCaContext contexto;
	/**
	 * Constructor
	 * @param broker Broker de harvest
	 * @param rutaDfo Ruta completa al archivo dfo de credenciales
	 * @param proyecto Proyecto de Harvest
	 * @param estado Estado de Harvest
	 * @throws JCaHarvestException
	 */
	public CampoCajaBlanca(String broker, String rutaDfo, String proyecto, String estado) throws JCaHarvestException{
		Contexto cont = new Contexto(broker, rutaDfo);
		cont.setContexto(proyecto, estado);
		contexto = cont.getContext();	
	}
	
	
	/**
	 * Pone el valor "No aprobado" en campo del formulario PRUEBASCAJABLANCA dado el paquete 
	 * @param paquete nombre del paquete a actualizar
	 * @throws JCaHarvestException en caso de problemas al obtener el formulario
	 * @throws UDPException En caso de que el paquete no tenga un formulario de tipo "Descripcion de paquete"
	 */
	public void reiniciaCampo(String paquete) throws JCaHarvestException, UDPException{
		JCaPackage _package =contexto.getPackageByName(paquete);
		JCaForm[] formas = _package.getFormListAsList(false);
		JCaForm forma = null;
		for(JCaForm _forma:formas){
			if(_forma.getFormTypeName().equals("Descripcion del paquete")){
				forma = _forma;
				break;
			}
		}
		
		if(forma == null){
			throw new UDPException("El paquete no contiene un formulario del tipo \"Descripcion del paquete\"");
		}
		
		forma.get(forma.getFormObjID());
		JCaData dato = new JCaData();
		dato.setString("No aprobado");
		forma.setColumnValue("PRUEBASCAJABLANCA", dato);
		forma.update();
	}
	/**
	 * Como en el proceso "Borrar version" no esta disponible la variable [package] se hace esta modificacion
	 * Para obtener el paquete a traves de las versiones que se eliminan
	 * @param arg
	 * @return Arreglo de nombres de paquetes encontrado, en caso del proceso Borrar version siempre es un unico paquete
	 * @throws JCaHarvestException En caso de problemas al obtener el objeto para consultas SQL 
	 * @throws UDPException  en caso de errores al ejecutar la consulta
	 */
	private String[] getPackage(String arg) throws JCaHarvestException, UDPException{
		if(arg.indexOf(";")> 0){
			String[] partes = arg.trim().split("\t");
			partes = partes[0].trim().split(";");
			int indice = partes[0].lastIndexOf("\\");
			String nombre = partes[0].substring(indice + 1);
			String ruta = partes[0].substring(0, indice );
			JCaSQL sql = contexto.getSQL();
			
			String consulta = "select pack.PACKAGENAME from HARVERSIONS v " +
			" join HARPATHFULLNAME p on v.PATHVERSIONID = p.VERSIONOBJID " +
			" join haritems i on i.itemobjid = v.itemobjid " +
			" join HARPACKAGE pack on pack.PACKAGEOBJID = v.PACKAGEOBJID " +
			" where  i.itemname = '" + nombre + "' and p.pathfullname = '" + ruta + "' and v.MAPPEDVERSION = '" + partes[1] + "' ";
			sql.setSQLStatement(consulta);
			int error = sql.execute();
			if(error == 0){
				JCaContainer resultado = sql.getSQLResult();
				if(resultado.isEmpty()){
					System.out.println("No se encontro paquete.");
				//	System.out.println(consulta);
					return new String[]{};
				}
				return new String[]{resultado.getString("PACKAGENAME")};
			}
			throw new UDPException("hubo un error al ejecutar la consulta");
			
		}else{
			return arg.trim().split("\t");
		}
	}
	
	/**
	 * Invocación principal, se hace a través de la clase {@link Main}
	 * @param args Arreglo de argumentos, el arreglo debe contener el broker, la ruta del archivo .dfo con las credenciales para iniciar sesión en el broker, proyecto, estado y el nombre del paquete o en caso de invocarse en el proceso de borrar vesion se debe poner el parametro "[version]".
	 * @throws JCaHarvestException 
	 * @throws UDPException 
	 */
	public static void _main(String[] args) throws JCaHarvestException, UDPException{
		if(args.length != 5){
			System.err.println("El uso adecuado es CampoCajaBlanca broker rutaDfo proyecto estado prefijo usuario");
			System.exit(-1);
		} 
		CampoCajaBlanca cp = new CampoCajaBlanca(args[0], args[1], args[2], args[3]);
		//String pack = args[4].trim();
		String[] packs = cp.getPackage( args[4]);
		for(int i = 0; i < packs.length; i++){
			cp.reiniciaCampo(packs[i].trim());
		}
	}
}

package Aprobador;



	import com.ca.harvest.jhsdk.JCaContext;
	import com.ca.harvest.jhsdk.JCaForm;
	import com.ca.harvest.jhsdk.JCaHarvest;
	import com.ca.harvest.jhsdk.JCaSQL;
	import com.ca.harvest.jhsdk.hutils.JCaContainer;
	import com.ca.harvest.jhsdk.hutils.JCaData;
	import com.ca.harvest.jhsdk.hutils.JCaHarvestException;

	public class harvestform  {
		
		private JCaContext  contexto;
		public harvestform(String broker,credenciales credenciales) throws JCaHarvestException{
			
			
			JCaHarvest harvest = new JCaHarvest();
			
			//Hacemos login
			//int error = harvest.loginUsingDfo("svrtransferad", "C:\\Harvest-FF\\dfo\\harvest.dfo");
			
			int error= harvest.login(broker, credenciales.getusuario(), credenciales.getcontrasena());
			if(error == 0){
				//obtenemos el objeto de contexto
				contexto = harvest.getContext();
			}
		}
			public harvestform(String broker, String dfo) throws JCaHarvestException{
				
				
				JCaHarvest harvest = new JCaHarvest();
				
				//Hacemos login
				int error = harvest.loginUsingDfo(broker, dfo);
				if(error == 0){
					//obtenemos el objeto de contexto
					contexto = harvest.getContext();
				}
			
		}
		
		
		private JCaForm getForm(String idProyecto) throws JCaHarvestException{
			JCaSQL sql = contexto.getSQL();
			String Consultapaquete="SELECT PACKAGEOBJID FROM HARPACKAGE WHERE PACKAGENAME= '"+ 
					idProyecto +"'";
			sql.setSQLStatement(Consultapaquete);
			sql.execute();
			JCaContainer result1 = sql.getSQLResult();
			int idPack =  result1.getInt("PACKAGEOBJID");
			//consulta para obtener el Proyecto/Estado/Paquete dado el id de proyecto
			String consulta = "SELECT p.ENVOBJID, p.STATEOBJID, p.PACKAGEOBJID FROM HARDESCPAQUETE d "+
					" JOIN HARASSOCPKG a ON a.FORMOBJID =d.FORMOBJID  "+
					" JOIN HARPACKAGE p ON p.PACKAGEOBJID = a.ASSOCPKGID   "+
					" WHERE p.PACKAGEOBJID  = '" + idPack + "'";
			sql.setSQLStatement(consulta);
			sql.execute();
			JCaContainer result = sql.getSQLResult();
			if(!result.isEmpty()){
				int idEnv =  result.getInt("ENVOBJID");
				int idState =  result.getInt("STATEOBJID");
				//Seteamos el contexto
				contexto.setProject(idEnv);
				contexto.setState(idState);
				//obtenemos los formularios del paquete, en teoria solo debe ser un solo formulario
				JCaForm[] formas = contexto.getPackageById(idPack).getFormListAsList(true);
				
				//Obtenemos el formulario
				JCaForm forma = formas[0];
				
				//Hacemos esta llamada para que permita ser modidifaco el formulario, si no no permite aplicar los cambios
				int i = forma.get(forma.getFormObjID());
				
				return forma;
			}else{
				//Error en caso de que no obtengamos el formulario con el id de proyecto proporcionado
				throw new JCaHarvestException("No hay formularios con el id de paquete proporcionado");
			}
			
		}
		
		public void modificaForm(String idProyecto,  String bandera) throws JCaHarvestException{
			//obtenemos el formulario
			JCaForm forma = getForm(idProyecto);
			//System.out.println(bandera);
			//se setea el valor que se va a modificar al formulario en un objeto JCaData
			JCaData _bandera = new JCaData();
			
			_bandera.setString(bandera);
			
			//Se setea el valor en el formulario. 
			//TODO: Falta definir bien el campo destino en el formulario que va a recibir el dato
			forma.setColumnValue("PRUEBASCAJABLANCA", _bandera);
			//forma.setColumnValue("", _bandera);
			//Se aplica los cambios al formulario
			forma.update();
		}
		
}

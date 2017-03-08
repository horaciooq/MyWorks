package connector;

public class primera {

	public static void main(String[] args){
		//pruebaconexion p = new pruebaconexion();
		//p.prueba();
		ejecucion b = new ejecucion ();
		//b.escribirharvest(b.getvpinfo("Crisol V Desarrollo Pruebas Caja Blanca","Editor-Fortify"), "PREI.15.02.13.1144");
		b.escribirharvest(b.getvpinfo(args[0],args[1]), args[2]);
	}
}

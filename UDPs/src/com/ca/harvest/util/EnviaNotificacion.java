package com.ca.harvest.util;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Clase encargada de enviar las notificaciones a trav�s del comando hsmtp
 * @author Ernesto Pe�a
 *
 */
public class EnviaNotificacion {
	
	/**
	 * M�todo est�tico que envia la notificaci�n
	 * @param correos Cadena de correos separados por comas a los que se les enviar� la notificacion
	 * @param cuerpo Cuerpo del correo
	 * @param asunto Asunto del correo
	 * @throws IOException En caso de error al enviar el cuerpo al comando ya que se le envia a trav�s del standard input
	 * @throws InterruptedException En caso de problemas al ejecutar el comando
	 */
	public static void envia(String correos, String cuerpo, String asunto) throws IOException, InterruptedException{
		Process p;
		File temp = File.createTempFile("notif_", ".txt");
	    FileOutputStream salida = new FileOutputStream(temp);
	    salida.write(cuerpo.getBytes());
	    salida.flush();
	    salida.close();
	    
		String comando = "cat " +temp.getPath() + " | hsmtp -s \"" + asunto + "\" \"" + correos + "\"";
		//System.out.println(comando);
		String[] cmds = {"/bin/sh",
				"-c",
				comando}; 
		p = Runtime.getRuntime().exec(cmds);
		p.waitFor();
		/*BufferedReader reader =  new BufferedReader(new InputStreamReader(p.getInputStream()));

          			
		while (reader.readLine()!= null) {
		//	output.append(line + "\n");
			//System.out.println(line);
		}
		 */
		temp.deleteOnExit();
	}
}

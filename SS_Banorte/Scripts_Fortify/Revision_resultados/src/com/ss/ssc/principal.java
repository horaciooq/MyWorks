package com.ss.ssc;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.ca.harvest.jhsdk.hutils.JCaHarvestException;


public class principal {

	//private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private static Logger LOGGER = Logger.getLogger(principal.class);
	public static void main(String[] args) {
		int conteovulnerabilidades=0;
		int conteofalsospositivos=0;
		int Criticas=0;
		int Altas=0;
		String cataux="";
		String archivoxml=args[0];
		String resultados=args[4];
		lecturaresultados lr =new lecturaresultados();
		Object [] obj=lr.getResults(archivoxml, args[1], args[2],args[3]);
		ArrayList categorias = (ArrayList) obj[0];
		ArrayList vulnerabilidades = (ArrayList) obj[1];
		  for(int X=0; X<vulnerabilidades.size();X++){
				Vulnerabilidad vul =(Vulnerabilidad) vulnerabilidades.get(X);
				if (vul.getEstado().equals("New")){
					conteovulnerabilidades++;
					if (vul.getProridad().equals("Critical")){
						Criticas++;
					}
					if (vul.getProridad().equals("High")){
						Altas++;
					}
					if(vul.getAudit().equals("Not an Issue") && !vul.getComentario().equals("") && vul.getComentario()!=null){
					conteofalsospositivos++;
					}
					
									
				}
			}
		  escribir_resultado(resultados, conteovulnerabilidades, conteofalsospositivos, Criticas, Altas);
	}
	private static void escribir_resultado(String results, int nuevas, int falsos, int critical, int high) {
		File archivo=new File(results);
		try{
			FileWriter fw= new FileWriter(archivo);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw);
			pw.write("Nuevas:"+nuevas+";Falsos:"+falsos+";Criticas:"+critical+";Altas:"+high);
			pw.close();
			bw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
}

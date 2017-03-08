package cobolwizard;

import java.io.*;

import javax.swing.JOptionPane;

public class Generar {
	String buildID;
	String ProjectRoot;
	String CopiesRoot;
	String Memory;
	String translate;
	String scan;
	Boolean noextensionP;
	Boolean debugP;
	Boolean fixmodeP;
	
	
 public Generar(String build, String root, String copies,Boolean extension, Boolean debug, Boolean fix){
	 buildID=build;
	 ProjectRoot=root;
	 CopiesRoot=copies;
	 noextensionP=extension;
	 debugP=debug;
	 fixmodeP=fix;
	 Memory = "512M";
	 cadena();
 }
 public Generar(String build, String root, String copies,Boolean extension, Boolean debug, Boolean fix, String memoria){
	 buildID=build;
	 ProjectRoot=root;
	 CopiesRoot=copies;
	 noextensionP=extension;
	 debugP=debug;
	 fixmodeP=fix;
	 Memory = memoria;
	 cadena();
 }
 
 private void cadena(){ 
	 
	 if (noextensionP && debugP && fixmodeP){
		 translate="Sourceanalyzer -Xmx"+Memory+" -b "+buildID+" -fixed-format -noextension-type COBOL \"" + ProjectRoot + "\" -copydirs \"" + CopiesRoot +"\" -debug";
		 scan="Sourceanalyzer -Xmx"+Memory+" -debug -b " + buildID + " -scan -fixed-format -f C:\\FortifyConsole\\FPRDir\\" + buildID + ".fpr";
	 }else{
		 if (noextensionP && debugP){
			 translate="Sourceanalyzer -Xmx"+Memory+" -b "+buildID+" -noextension-type COBOL \"" + ProjectRoot + "\" -copydirs \"" + CopiesRoot +"\" -debug";
			 scan="Sourceanalyzer -Xmx"+Memory+" -debug -b " + buildID + " -scan -f C:\\FortifyConsole\\FPRDir\\" + buildID + ".fpr";
		 }else{
			 if(noextensionP && fixmodeP){
				 translate="Sourceanalyzer -Xmx"+Memory+" -b "+buildID+" -fixed-format -noextension-type COBOL \"" + ProjectRoot + "\" -copydirs \"" + CopiesRoot +"\"";
				 scan="Sourceanalyzer -Xmx"+Memory+" -b " + buildID + " -scan -fixed-format -f C:\\FortifyConsole\\FPRDir\\" + buildID + ".fpr";
			 }else{
				 if (fixmodeP && debugP){
					 translate="Sourceanalyzer -Xmx"+Memory+" -debug -b "+buildID+" -fixed-format \"" + ProjectRoot + "\" -copydirs \"" + CopiesRoot +"\"";
					 scan="Sourceanalyzer -Xmx"+Memory+" -debug -b " + buildID + " -scan -fixed-format -f C:\\FortifyConsole\\FPRDir\\" + buildID + ".fpr";
				 }else{
					 if (noextensionP){
						 translate="Sourceanalyzer -Xmx"+Memory+" -b "+buildID+" -noextension-type COBOL \"" + ProjectRoot + "\" -copydirs \"" + CopiesRoot +"\"";
						 scan="Sourceanalyzer -Xmx"+Memory+" -b " + buildID + " -scan -f C:\\FortifyConsole\\FPRDir\\" + buildID + ".fpr";
					 }else{
						 if (fixmodeP){
							 translate="Sourceanalyzer -Xmx"+Memory+" -b "+buildID+" -fixed-format \"" + ProjectRoot + "\" -copydirs \"" + CopiesRoot +"\"";
							 scan="Sourceanalyzer -Xmx"+Memory+" -b " + buildID + " -scan -fixed-format -f C:\\FortifyConsole\\FPRDir\\" + buildID + ".fpr";
						 }else {
							 if (debugP){
								 translate="Sourceanalyzer -Xmx"+Memory+" -debug -b "+buildID+" \"" + ProjectRoot + "\" -copydirs \"" + CopiesRoot +"\"";
								 scan="Sourceanalyzer -Xmx"+Memory+" -debug -b " + buildID + " -scan -f C:\\FortifyConsole\\FPRDir\\" + buildID + ".fpr";
							 }else{
								 translate="Sourceanalyzer -Xmx"+Memory+" -b "+buildID+" \"" + ProjectRoot + "\" -copydirs \"" + CopiesRoot +"\"";
								 scan="Sourceanalyzer -Xmx"+Memory+" -b " + buildID + " -scan -f C:\\FortifyConsole\\FPRDir\\" + buildID + ".fpr";
							
							 }
						 }
					 }
				 }	 
			 }
		 }
	 }
	 
	 
	 
	 
	 escribirbatch(translate,scan);
 }
 
 private void escribirbatch(String comandotrans, String comandoscan){
	 File archivo = null;
	 File directorio = null;
	 FileWriter fichero = null;
     PrintWriter pw = null;
     try
     {
    	 
    	 //archivo=new File("C:\\FortifyConsole\\BatchSource\\"+buildID+".bat");
    	 String user=System.getProperty("user.name");
    	 String dataFolder = "C:"+ File.separator+"FortifyConsole";
    	 directorio=new File(dataFolder);
    	 if (!directorio.exists()){
    		 directorio.mkdir(); 
    	 }
    	dataFolder = "C:"+File.separator+"FortifyConsole"+File.separator+"BatchSource";
    	 directorio=new File(dataFolder);
 if (!directorio.exists()){
    		 directorio.mkdir(); 
    	 }
    	 dataFolder = "C:"+File.separator+"FortifyConsole"+File.separator+"BatchSource"+File.separator+user;
    	 directorio=new File(dataFolder);
    	 if (!directorio.exists()){
    		 directorio.mkdir(); 
    	 }
    	 archivo=new File(dataFolder+File.separator+buildID+".bat");
    	 if(!archivo.exists()){
    		 archivo.createNewFile();
    	 }
         fichero = new FileWriter(archivo);
         pw = new PrintWriter(fichero);
             pw.println("@echo off \n");
             pw.println("echo Cleaning previous scan artifacts \n");
             pw.println("Sourceanalyzer -b "+buildID +" -clean \n");
             pw.println("IF %ERRORLEVEL%==1 ( \n");
             pw.println("echo Sourceanalyzer failed, exiting \n");
             pw.println("GOTO :FINISHED \n");
             pw.println(") \n");
             pw.println("REM ########################################################################### \n");
             pw.println("echo Translating files \n");
             pw.println(comandotrans);
             pw.println("IF %ERRORLEVEL%==1 ( \n");
             pw.println("echo Sourceanalyzer failed, exiting \n");
             pw.println("GOTO :FINISHED \n");
             pw.println(") \n");
             pw.println("REM ########################################################################### \n");
             pw.println("echo Starting scan \n");
             pw.println(comandoscan);
             pw.println("IF %ERRORLEVEL%==1 ( \n");
             pw.println("echo Sourceanalyzer failed, exiting \n");
             pw.println("GOTO :FINISHED \n");
             pw.println(") \n");
             pw.println("REM ########################################################################### \n");
             pw.println("echo Finished \n");
             pw.println("C:\\FortifyConsole\\FPRDir\\"+buildID+".fpr \n");
             pw.println(":FINISHED");
             JOptionPane.showMessageDialog(null, "El Archivo batch "+buildID+".bat se a creado", 
      			   "", JOptionPane.INFORMATION_MESSAGE);
     } catch (Exception e) {
    	 JOptionPane.showMessageDialog(null, "No se pudo generar el batch file", 
  			   "ERROR", JOptionPane.ERROR_MESSAGE);
     } finally {
        try {
        // Nuevamente aprovechamos el finally para 
        // asegurarnos que se cierra el fichero.
        if (null != fichero)
           fichero.close();
        } catch (IOException e2) {
        	JOptionPane.showMessageDialog(null, "Error Cerrando el Archivo", 
       			   "ERROR", JOptionPane.ERROR_MESSAGE);
        }
     }
   
 }
}

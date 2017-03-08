package esapitest;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;



import javax.script.*;

import org.owasp.esapi.ESAPI;


public class esapimain {

	public static void main(String[] args)   {
		Process process;
		try {
			process = Runtime.getRuntime().exec("cmd.exe /K del \"C:\\Users\\SMARTSOL 20\\workspace\\pruebasseguridad\\WebContent\\archivo1\"");
			//process = Runtime.getRuntime().exec("netstat");
			Scanner sc = new Scanner(process.getInputStream()); 
			while (sc.hasNext()){ 
			System.out.println( sc.next());
			//ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
			//ScriptEngine scriptEngine = scriptEngineManager.getEngineByExtension("js");
			//String userOps = "java.lang.Runtime.getRuntime().exec(\"netstat\")";
			//Object result = scriptEngine.eval(userOps);
		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		 
	}
	
}

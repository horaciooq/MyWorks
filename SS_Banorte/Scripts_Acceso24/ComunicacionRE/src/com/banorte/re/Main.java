package com.banorte.re;

import java.io.BufferedReader;
//import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.ProtocolException;
import java.net.URL;
//import java.net.URLEncoder;
import java.net.URLEncoder;

public class Main {
	private String user;
	private String pass;
	private String reurl;
	private String cookie;

	public Main() throws IOException, FileNotFoundException {
		InputStream input = Main.class.getClassLoader().getResourceAsStream("config.properties");
		Properties prop = new Properties();
		prop.load(input);
		user = prop.getProperty("re.user");
		pass = prop.getProperty("re.pass");
		reurl = prop.getProperty("re.url");
		input.close();
	}

	private static JSONObject getJSON(HttpURLConnection conn) throws IOException, ParseException {
		BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
		String output;
		//System.out.println("Output from Server .... \n");
		String response = "";
		while ((output = br.readLine()) != null) {
			response += output + "\n";
		}
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(response);
		if (obj instanceof JSONObject) {
			return (JSONObject) obj;
		} else {
			throw new RuntimeException("Error en la respuesta del servidor RE.");
		}
	}

	public Boolean login() throws UnsupportedEncodingException, IOException, ParseException {
		URL url = new URL(reurl + "/login?user=" + user + "&pass=" + pass);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");

		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Falla en conexion con servidor. ErrorCode: " + conn.getResponseCode());
		}

		JSONObject salida = getJSON(conn);

		Boolean loginStatus = (Boolean) salida.get("success");

		if (loginStatus) { // login correcto
			String headerName = null;

			for (int i = 1; (headerName = conn.getHeaderFieldKey(i)) != null; i++) {
				if ("Set-Cookie".equals(headerName)) {
					// En el cookie viene el ID de sesion
					cookie = conn.getHeaderField(i);
					break;
				}
			}
		} else {
			System.err.println(salida.get("error"));
		}

		conn.disconnect();
		return loginStatus;
	}

	public int deploy(String app, String env, String params, boolean syn) throws IOException, ParseException {
		String _url = reurl + "/deploy/" + app + "?env=" + env + params +(syn ? "&wait=Y" : "");
		int exitCode = -1;
		URL url = new URL(_url
				);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("Cookie", cookie);
		
		if (conn.getResponseCode() != 200) {
			throw new RuntimeException(
					"Falla en conexion con servidor al hacer el deploy. ErrorCode: " + conn.getResponseCode());
		}
		JSONObject salida = getJSON(conn);
		//Boolean resultadoDeploy = (Boolean) salida.get("success");
		if(!(Boolean) salida.get("success")){
			throw new RuntimeException("Error al lanzar el deploy\n" + salida.get("error") );
		}
		Number idDeploy = (Number) salida.get("deploymentid");

		conn.disconnect();
		url = new URL(reurl + "/log?id=" + idDeploy);
		conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("Cookie", cookie);
		if (conn.getResponseCode() != 200) {
			throw new RuntimeException(
					"Falla en conexion con servidor al obtener los logs. ErrorCode: " + conn.getResponseCode());

		}
		salida = getJSON(conn);
		Boolean success = (Boolean) salida.get("success");
		if (success) {
			System.out.println("Application: " + salida.get("application").toString());

			System.out.println("Complete: " + salida.get("complete").toString());
			System.out.println("Environment: " + salida.get("environment").toString());
			System.out.println("ExitCode: " + salida.get("exitcode").toString());
			System.out.println("ExitStatus: " + salida.get("exitstatus").toString());
			System.out.println("DeployID: " + salida.get("id").toString());
			JSONArray logs = (JSONArray) salida.get("logoutput");
			exitCode = Integer.parseInt(salida.get("exitcode").toString());
			@SuppressWarnings("unchecked")
			Iterator<String> it = (Iterator<String>) logs.iterator();
			String nx;
			while (it.hasNext()) {
				nx = it.next();
				if(!nx.startsWith("shell")){
					System.out.println("\t" + nx);
				}
			}
		}else{
			System.err.println("No se pudieron recuperar los logs del deploy numero: " + idDeploy);
		}
		return exitCode;
		
	}

	public static void main(String[] args) {
		try {
			String uso = "Uso: -app <aplicacion> -env <ambiente> [-param nombre=valor[;nombre2=valor2...]] [-version <version>]";
			if(args.length <= 4){
				System.err.println(uso);
				System.exit(1);
			}
			String app = null;
			String env = null;
			String params = null;
			String version = null;
			for(int i = 0; i < args.length; i++){
				if("-app".equals(args[i]) ){
					if(i+1 < args.length){
						app = URLEncoder.encode(args[i+1], "UTF-8");
					}else{
						System.err.println(uso);
						System.exit(1);
					}
				}else if("-env".equals(args[i]) ){
					if(i+1 < args.length){
						env = URLEncoder.encode(args[i+1], "UTF-8");
					}else{
						System.err.println(uso);
						System.exit(1);
					}
				}else if("-version".equals(args[i]) ){
					if(i+1 < args.length){
						version = URLEncoder.encode(args[i+1], "UTF-8");
					}else{
						System.err.println(uso);
						System.exit(1);
					}
				}
				else if("-param".equals(args[i]) ){
					if(i+1 < args.length){
						String[] ps =  args[i+1].split(";");
						params = "";
						for(String p : ps){
							String[] tmp = p.split("=", 1);
							if(tmp.length == 2){
								//El nombre de la variable no debe tener espacios por sentido comun
								p = tmp[0] + "=" + URLEncoder.encode(tmp[1], "UTF-8");
							}else{
								p = tmp[0];
							}
							params += "&" + p;
						}
						
						
					}else{
						System.err.println(uso);
						System.exit(1);
					}
				}
			}
			if(version != null){
				if(params != null){
					params += "&version=" + version;
				}else{
					params = "&version=" + version;
				}
			}
			if(app == null || env == null){
				System.err.println(uso);
				System.exit(1);
			}
			Main m = new Main();
			
			if(m.login()){
				int exitCode = m.deploy(app, env, params, true);
				System.exit(exitCode);
			}else{
				System.err.println("Error en el inicio de sesion");
				
			}
		} catch (FileNotFoundException e) {
			System.err.println("No se encuentra el archivo de propiedades");
		} catch (IOException e) {
			System.err.println("Hay un problema de lectura del archivo de propiedades. " + e.getMessage());

		} catch (ParseException e) {
			System.err.println("Error con RE. " + e.getMessage());
		}catch (Exception e){
			System.err.println("Error con RE. " + e.getMessage());
		}
		System.exit(-10);
	}
}

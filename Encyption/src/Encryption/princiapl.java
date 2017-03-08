package Encryption;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;

import javax.net.ssl.SSLContext;

import org.jasypt.digest.StandardStringDigester;
import org.jasypt.digest.config.DigesterConfig;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.EncryptableProperties;
import org.jasypt.salt.SaltGenerator;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.jasypt.util.text.StrongTextEncryptor;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encryptor;
import org.owasp.esapi.crypto.CipherText;
import org.owasp.esapi.crypto.PlainText;
import org.owasp.esapi.errors.EncryptionException;

public class princiapl {

	public static void main(String[] args) throws NoSuchAlgorithmException {
		SSLContext sc = SSLContext.getInstance("SSL");
		String SSLContext ="hello";
		// TODO Auto-generated method stub
		//encriptar password y checarla
		System.out.println("########### encriptacion de contraseña usando jasypt ##########");
		String inputPassword=null;		
		StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
		String encryptedPassword = passwordEncryptor.encryptPassword("banorte");				
		System.out.println("password:");
		Scanner Scan = new Scanner(System.in);
		inputPassword=Scan.nextLine();
		if (passwordEncryptor.checkPassword(inputPassword, encryptedPassword)) {
		  System.out.println( "correct!" );
		  System.out.println("the encrypted password is: "+encryptedPassword);
		} else {
		  System.out.println(" bad login!");
		}
		Scan.close();
		
		//encriptar y decriptar texto
		System.out.println("########## encriptar decriptar texto jasypt ##########");
		StrongTextEncryptor textEncryptor = new StrongTextEncryptor();
		textEncryptor.setPassword("123");
		String myEncryptedText = textEncryptor.encrypt("Hola mundo");
		String plainText = textEncryptor.decrypt(myEncryptedText);
		System.out.println("Texto encriptado: "+myEncryptedText);
		System.out.println("Texto Desencriptado:" +plainText);
		
		//Archivos de configuracion
		System.out.println("########## Archivos de configuracion para conectar a SQL jasypt ##########");
		Connection con=null;
		InputStream input = null;
		String stru="", strp="";
		StandardPBEStringEncryptor contra =new StandardPBEStringEncryptor();
		contra.setPassword("123");	
		Properties prop = new EncryptableProperties(contra);			
		 try
	        {
			 	input = new FileInputStream("config1.properties");
			 	prop.load(input);	
			 	strp=prop.getProperty("dbpassword");
	            stru=prop.getProperty("dbuser");	          		 	
	            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	            String url = "jdbc:sqlserver://15.128.27.56:1433;databaseName=SSCDB"; 
	            con= DriverManager.getConnection(url, stru, strp);
	            if(con!=null){
	            	System.out.println("connecion exitosa");
	            }else{
	            	System.out.println("not set");
	            }	  
	           
	        }catch(SQLException | ClassNotFoundException | IOException e){
	        	e.printStackTrace();
	        }finally{
	        	try {
	        		if (con!=null){
					con.close();}
	        		if(input!=null){
	        		input.close();}
				} catch (SQLException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
		 
		 //hashing Stings
		 System.out.println("########## Hashing de cadenas de texto jasypt ##########");
		 StandardStringDigester ssd = new StandardStringDigester();
		 ssd.setAlgorithm("SHA-512");   // opcional		
		 ssd.setIterations(50000);  //opcional		 
		 String digestedString=ssd.digest("holamundo");
		 System.out.println("Cadena Hasheada: "+digestedString);
		 
		 //Encryptar y desencryptar Strings con contraseña
		 System.out.println("########## Encriptar con password based encryption ##########");
		 StandardPBEStringEncryptor sse = new StandardPBEStringEncryptor();
		 sse.setPassword("12345678");		 
		 String encryptedString=sse.encrypt("Esta cade es encriptada");
		 System.out.println(encryptedString);
		 String decryptedString =sse.decrypt(encryptedString);
		 System.out.println(decryptedString);
		 
		 /*//Encryptio using esapi
		 System.out.println("########## Encriptar cadenas con esapi fail ##########");
		 PlainText pt = new PlainText("texto");
		 try {
			 CipherText ciphertext =
			            ESAPI.encryptor().encrypt( new PlainText("Holla") );
			//System.out.println(ciphertext.toString());
		} catch (EncryptionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		 
	}
	
	
}

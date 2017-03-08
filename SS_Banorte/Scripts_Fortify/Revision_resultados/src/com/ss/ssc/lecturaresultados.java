package com.ss.ssc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
 
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



public class lecturaresultados {
	//private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	//private static Logger LOGGER = Logger.getLogger(lecturaresultados.class);
	public lecturaresultados(){
		 /*Properties props = new Properties();
		   File i =new File("C:\\Harvest-FF\\config\\log4j.properties");
		   InputStream is = null;
		   
		   try {
			    is= new FileInputStream(i);
			   props.load(is);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
			    try {
			        is.close();
			    }
			    catch (Exception e) {
			        // ignore this exception
			    }
			}
	      
		   PropertyConfigurator.configure(props);*/
	}
	
	
     
	public static Object [] getResults(String file, String namep, String namecom, String namepar){
		//MyLogger.setup();
		FileInputStream archivo = null;
		ArrayList<Categoria> categorias = new ArrayList<Categoria>();
	    ArrayList<Vulnerabilidad> vulnerabilidades = new ArrayList<Vulnerabilidad>();
	 
		try{
		archivo = new FileInputStream(new File(file));
	    
	    DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
	    builderFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
	    builderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
	    builderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
	    DocumentBuilder builder =  builderFactory.newDocumentBuilder();
	   
	    Document xmlDocument = builder.parse(archivo);
	    archivo.close();
	   
	    XPath xPath =  XPathFactory.newInstance().newXPath();
	    
	    
	    String expresion = "/ReportDefinition/ReportSection [@optionalSubsections=\"true\"][@enabled=\"true\"]/SubSection/IssueListing/Chart/GroupingSection";
	  
	    NodeList grupos =(NodeList) xPath.compile(expresion).evaluate(xmlDocument, XPathConstants.NODESET);
	   
	    for(int x=0;x<grupos.getLength();x++){
	    	Node nodo = grupos.item(x);
	    	Categoria cat =new Categoria();	    	
	      	cat.setcount(Integer.parseInt(nodo.getAttributes().getNamedItem("count").getTextContent()));
	    	NodeList gruposinfo =nodo.getChildNodes();
	    	for(int y=0;y<gruposinfo.getLength();y=y+1){
	    		 Node nodoinfo =gruposinfo.item(y);
	    		 if(nodoinfo.getNodeName().equals("groupTitle")){	    			 
	    			 cat.setNombre(nodoinfo.getTextContent());
	    		 }
	    		 if(nodoinfo.getNodeName().equals("MajorAttributeSummary")){
	    			 NodeList metainfos = nodoinfo.getChildNodes();
	    			 for(int z=0;z<metainfos.getLength();z++){
	    				 Node metainfo =metainfos.item(z);
	    				 if(metainfo.getNodeName().equals("MetaInfo")){
	    					 cat.addMetaInfo(metainfo.getTextContent());
	    				 }
	    			 }
	    		 }
	    		 if(nodoinfo.getNodeName().equals("Issue")){
	    			 Vulnerabilidad vul = new Vulnerabilidad();
	    			 NodeList issue =nodoinfo.getChildNodes();
	    			 vul.setInstanceid(nodoinfo.getAttributes().getNamedItem("iid").getTextContent());
	    			 for(int j=0;j<issue.getLength();j++){
	    				 Node issuenodo = issue.item(j);
	    				 if(issuenodo.getNodeName().equals("Category")){vul.setCategoria(issuenodo.getTextContent());}
	    				 if(issuenodo.getNodeName().equals("Abstract")){vul.setAbstracto(issuenodo.getTextContent());}
	    				 if(issuenodo.getNodeName().equals("Friority")){vul.setPrioridad(issuenodo.getTextContent());}
	    				 if(issuenodo.getNodeName().equals("Primary")){
	    					 NodeList sink =issuenodo.getChildNodes();
	    					 for(int k=0;k<sink.getLength();k++){
	    						 Node sinknodo =sink.item(k);
	    						 if(sinknodo.getNodeName().equals("FileName")){vul.setsinkArchivo(sinknodo.getTextContent());}
	    						 if(sinknodo.getNodeName().equals("FilePath")){vul.setsinkLocalizacion(sinknodo.getTextContent());}
	    						 if(sinknodo.getNodeName().equals("LineStart")){vul.setsinkLinea(Integer.parseInt(sinknodo.getTextContent()));}
	    						 if(sinknodo.getNodeName().equals("Snippet")){vul.setsinkSnippet(sinknodo.getTextContent());}
	    					 }
	    				 }
	    				 if(issuenodo.getNodeName().equals("Source")){
	    					 NodeList source =issuenodo.getChildNodes();
	    					 for(int l=0;l<source.getLength();l++){
	    						 Node sourcenodo =source.item(l);
	    						 if(sourcenodo.getNodeName().equals("FileName")){vul.setsourceArchivo(sourcenodo.getTextContent());}
	    						 if(sourcenodo.getNodeName().equals("FilePath")){vul.setsourceLocalizacion(sourcenodo.getTextContent());}
	    						 if(sourcenodo.getNodeName().equals("LineStart")){vul.setsourceLinea(Integer.parseInt(sourcenodo.getTextContent()));}
	    						 if(sourcenodo.getNodeName().equals("Snippet")){vul.setsourceSnippet(sourcenodo.getTextContent());}
	    					 }
	    				 }
	    			 }
	    			 vulnerabilidades.add(vul);	    			 
	    		 }
	    	 }
	    	categorias.add(cat);
	    }
		}catch (IOException e){
			
			//LOGGER.warn(e.getMessage());
		}catch (ParserConfigurationException e){
			//LOGGER.warn(e.getMessage());
		} catch (SAXException e) {			
			//LOGGER.warn(e.getMessage());
		} catch (XPathExpressionException e) {
			//LOGGER.warn(e.getMessage());
		}finally{
			if(archivo!=null){
				safeClose(archivo);
			}
		}
		String normalized = Normalizer.normalize(namepar, Normalizer.Form.NFD);
		int parcial=getProjectVersion(namep,normalized);
		int ProjectVersion=getProjectVersion(namep,namecom);
		Auditoria(vulnerabilidades, parcial);
		Estado(vulnerabilidades, ProjectVersion);
		return new Object [] {categorias, vulnerabilidades};
	}
	public static Connection GetConexion(){
		//MyLogger.setup();
		Connection con=null;
		 try
	        {
	            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	            String url = "jdbc:sqlserver://15.128.27.56:1433;databaseName=SSCDB;user=ugfortify;password=a$u74g2i;";   
	            con= DriverManager.getConnection(url);
	        }catch(SQLException  e){
	        	//LOGGER.warn(e.getMessage());
	        }catch(ClassNotFoundException e){
	        	//LOGGER.warn(e.getMessage());
	        }
		 return con;
	}
	public static ArrayList <Vulnerabilidad> Estado (ArrayList<Vulnerabilidad> array, int ProjectVersion){
		//MyLogger.setup();
		Connection conexion= GetConexion();
		CallableStatement cstmt=null;
		try{			
			for(int x=0;x<array.size();x++){
				cstmt=conexion.prepareCall("{call dbo.IsItNew(?, ?, ?)}");
				cstmt.setString(1, array.get(x).getInstanceid());;
				cstmt.setInt(2, ProjectVersion);
			    cstmt.registerOutParameter(3, java.sql.Types.VARCHAR);
			    cstmt.execute();
			    if(cstmt.getString(3)!=null){
			    array.get(x).setEstado("Updated");
			    }else{
			    	array.get(x).setEstado("New");
			    }
			}			
		}catch (SQLException e){
			//LOGGER.warn(e.getMessage());
		}finally{
			if(cstmt!=null){
				safeClose(cstmt);
			}
			if(conexion!=null){
				safeClose(conexion);
			}
		}
		return array;
	}
	
	public static void safeClose(Connection con) {
		//MyLogger.setup();
		  if (con != null) {
		    try {
		      con.close();
		    } catch (SQLException e) {
		    	//LOGGER.warn(e.getMessage());
		    }
		  }
		}
	public static void safeClose(FileInputStream con) {
		//MyLogger.setup();
		  if (con != null) {
		    try {
		      con.close();
		    } catch (IOException e) {
		    	//LOGGER.warn(e.getMessage());
		    }
		  }
		}
	public static void safeClose(Statement con) {
		//MyLogger.setup();
		  if (con != null) {
		    try {
		      con.close();
		    } catch (SQLException e) {
		    	//LOGGER.warn(e.getMessage());
		    }
		  }
		}
	public static ArrayList <Vulnerabilidad> Auditoria (ArrayList<Vulnerabilidad> array, int ProjectVersion){
		//MyLogger.setup();
		Connection conexion = GetConexion();
		CallableStatement cstmt=null;
	    try {
	    	for(int x=0;x<array.size();x++){
				cstmt = conexion.prepareCall("{call dbo.IsAudited(?, ?, ?)}");
				cstmt.setString(1, array.get(x).getInstanceid());
				cstmt.setInt(2, ProjectVersion);
			    cstmt.registerOutParameter(3, java.sql.Types.VARCHAR);
			    cstmt.execute();
			    if(cstmt.getString(3)!=null){
			    array.get(x).setAudit(cstmt.getString(3));}
			    safeClose(cstmt);
			    cstmt = conexion.prepareCall("{call dbo.GETCOMMENT(?,?,?)}");
			    cstmt.setString(1, array.get(x).getInstanceid());
			    cstmt.setInt(2, ProjectVersion);
			    cstmt.registerOutParameter(3, java.sql.Types.VARCHAR);
			    cstmt.execute();
			    if (cstmt.getString(3)!=null){
			    	array.get(x).setComentario(cstmt.getString(3));
			    }
			    
	    	}	    	
		} catch (SQLException e) {	
			//LOGGER.warn(e.getMessage());
		}finally{
			if(cstmt!=null){
				safeClose(cstmt);
					}
				if(conexion!=null){
				safeClose(conexion);
					}
		}
	    
		return array;
	}
	private static int getProjectVersion (String namep, String namev){
		//MyLogger.setup();
		int ProjectVersionid=0;
		Connection miConexion= GetConexion();		
		PreparedStatement st=null;
		ResultSet rs;
		String query;
		try {
			query="select id from project where name=?";
			st=miConexion.prepareStatement(query);
			st.setString(1, namep);
			rs=st.executeQuery();
			int aux=0;
			
			while(rs.next()){
				aux=rs.getInt("id");
			}
			safeClose(st);
			query="select id from projectversion where name=? AND project_id=?";
			st=miConexion.prepareStatement(query);
			st.setString(1, namev);
			st.setInt(2, aux);
			rs=st.executeQuery();
			while(rs.next()){
				ProjectVersionid=rs.getInt("id");
				
				}
		}catch(SQLException e){
			//LOGGER.warn(e.getMessage());
		}finally{
			if (st!=null){
				safeClose(st);
			}
			if (miConexion!=null){
				safeClose(miConexion);
			}
			
		}	
		return ProjectVersionid;
	}
}

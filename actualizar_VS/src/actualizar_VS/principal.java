package actualizar_VS;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

class principal {

	public void listarDirectorio(File f) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException, TransformerException{
	    File[] ficheros = f.listFiles();
	    String fichero=null;               
	    for (int x=0;x<ficheros.length;x++)
	    {                    
	    	if(ficheros[x].isFile()){
	    		fichero=ficheros[x].getName();
	    		if(fichero.endsWith(".csproj") || fichero.endsWith(".vbproj")){
						actualizarproyecto(ficheros[x].getPath(), "ToolsVersion","4.0");
	    		}
	    		if(fichero.endsWith(".sln")){
	    			actualizarsolucion(ficheros[x].getPath());
	    		}
	    	}
	        if (ficheros[x].isDirectory())
	        {
	            listarDirectorio(ficheros[x]);
	        }
	    }       
	}	
	public void actualizarproyecto(String archivo, String atributo, String atributovalor) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, TransformerException{
	    File file =new File(archivo);
		FileInputStream filestream = new FileInputStream(file);
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder =  builderFactory.newDocumentBuilder();
        Document xmlDocument = builder.parse(filestream);
        XPath xPath =  XPathFactory.newInstance().newXPath();          
        String expression = "/Project";
        final NodeList item=(NodeList)xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET); 
        Element elemento = (Element)item.item(0);
        elemento.setAttribute(atributo, atributovalor);
        Result result = new StreamResult(file);
        Source source = new DOMSource(elemento);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(source, result);        
	}
	public void actualizarsolucion(String ruta) throws IOException{
        File archivo = new File(ruta);
        String linea=null;
        StringBuffer texto = new StringBuffer();
        String version1="Microsoft Visual Studio Solution File, Format Version";
        String version2="# Visual Studio";
		Scanner scan = new Scanner(archivo);
			while (scan.hasNextLine()) {
				linea=scan.nextLine(); 	
				if (linea.contains(version1)){
					texto.append("Microsoft Visual Studio Solution File, Format Version 11.00 \n");
					
				}else if(linea.contains(version2)){
					texto.append("# Visual Studio 2010 \n");
					
				}else
					texto.append(linea+"\n");
			}
			FileWriter fw=new FileWriter(archivo);
			fw.write(texto.toString());
			fw.flush();
			fw.close();
			scan.close();
    }
	

}

package com.SAEwebconfigmodify;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class inicio {

	public static void main(String[] args) {
	 	File file =new File(args[0]);
	 	
		FileInputStream filestream;
		try {
			
			filestream = new FileInputStream(file);
	    DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
	    builderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
	    builderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
	    DocumentBuilder builder =  builderFactory.newDocumentBuilder();
	    Document xmlDocument = builder.parse(filestream);
	    XPath xPath =  XPathFactory.newInstance().newXPath();          
	    String expression = "/configuration/configSections/sectionGroup/sectionGroup";
	    final NodeList item=(NodeList)xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET); 
	    Node ele =item.item(0);
	    NodeList nodos = ele.getChildNodes();
	    for(int x=0;x<nodos.getLength();x++){
		    Node nodo=nodos.item(x);
		    if (nodo.getNodeName().equals("section")){
		    ele.removeChild(nodo);
		    //xmlDocument.normalize();
		    Result result = new StreamResult(file);
	        Source source = new DOMSource(xmlDocument);
	        Transformer transformer = TransformerFactory.newInstance().newTransformer();
	        transformer.transform(source, result); 
		    }
		    if (nodo.hasChildNodes()){
		    	NodeList nod = nodo.getChildNodes();
		    	for(int y=0;y<nod.getLength();y++){
		    		Node n = nod.item(y);
		    		if(n.getNodeName().equals("section")){
		    			nodo.removeChild(n);
		    			Result result = new StreamResult(file);
		    		    Source source = new DOMSource(xmlDocument);
	    		        Transformer transformer = TransformerFactory.newInstance().newTransformer();
    		        transformer.transform(source, result);  
		    		}
		    	}
		    	
		    }
	    }
        
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 		
	 	}


	}



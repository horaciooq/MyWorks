package xpathproject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;



import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthPolicy;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.util.HttpURLConnection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;





public class inicio {	

	 private static InputSource makeInputSource(final InputStream inputStream) throws UnsupportedEncodingException {
	        Reader reader = new InputStreamReader(inputStream,"UTF-8");
	        InputSource result = new InputSource(reader);
	        result.setEncoding("UTF-8");
	        return result;
	    }
			
			public static void main(String[] args) throws IOException, XPathExpressionException, SAXException, ParserConfigurationException {
				GetMethod listQuery = null;
				GetMethod authenticate = null;
				XPathFactory xpathFactory;
				int httpReturnCode=0;
				DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();	             
		        DocumentBuilder docBuilder =  builderFactory.newDocumentBuilder();
				HttpClient client = new HttpClient();
				client.getParams().setParameter(AuthPolicy.AUTH_SCHEME_PRIORITY, new ArrayList<String>(Collections.singletonList(AuthPolicy.BASIC)));
				client.getState().setCredentials(
				new AuthScope("15.128.18.26", 8080),
				new UsernamePasswordCredentials("userfortify", "banorte234"));
				authenticate = new GetMethod("http://15.128.18.26:8080/qcbin/authentication-point/authenticate");
				authenticate.addRequestHeader("Accept", "application/xml");
				authenticate.setDoAuthentication(true);					
				httpReturnCode = client.executeMethod(authenticate);
				authenticate.getResponseBodyAsString();
				switch (httpReturnCode) {
				case HttpURLConnection.HTTP_OK:
					System.out.println("hello");
					// need to get xsrf and qc cookies for ALM 12.  This doesn't seem to have any effect on ALM 11 or 11.5
					PostMethod getSession = new PostMethod("http://15.128.18.26:8080/qcbin/rest/site-session");
					getSession.addRequestHeader("Accept", "application/xml");
					httpReturnCode = client.executeMethod(getSession);
					getSession.getResponseBodyAsString();
					if (!(httpReturnCode == HttpURLConnection.HTTP_CREATED || httpReturnCode == HttpURLConnection.HTTP_OK)) {
						System.out.println("Problem getting QCSession cookie");
					}					
					break;
				default:
					System.out.println("The credentials provided were invalid");
			}
				listQuery = new GetMethod("http://15.128.18.26:8080/qcbin/rest/domains/BANORTE/projects/Mantenimientos2015/release-cycles?page-size=200");
	            listQuery.addRequestHeader("Accept", "application/xml");				
				int listHttpReturnCode = client.executeMethod(listQuery);
			    String listResponse = listQuery.getResponseBodyAsString();			    
			    Document listDoc = docBuilder.parse(listQuery.getResponseBodyAsStream());
			    XPath listXpath = XPathFactory.newInstance().newXPath();
			    if (listDoc==null){
			    System.out.println(listDoc.toString());}
			    switch (listHttpReturnCode) {
			        case HttpURLConnection.HTTP_OK:
			            final NodeList choices = (NodeList) listXpath.compile("Entities/Entity/Fields").evaluate(listDoc, XPathConstants.NODESET);
			                for (int l=0; l<choices.getLength(); l++){
			                	System.out.println(choices.getLength());
			                Element itemElem = (Element)choices.item(l);
		                itemElem.getAttribute("Name"); 			              
			                System.out.println(itemElem.getAttributes().getNamedItem("Name").getTextContent());
			            }
			            //System.out.println(listDoc);
			           
			            break;
			        default:
			            RuntimeException nested = new RuntimeException("Got HTTP return code: "
			                    + listHttpReturnCode + "; Response: " + listResponse);
			           
			    }
			    }
			    
			    
		    }



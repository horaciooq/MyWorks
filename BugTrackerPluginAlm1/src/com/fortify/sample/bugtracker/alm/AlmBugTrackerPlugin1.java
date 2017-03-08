/*
 * (C) Copyright 2012 Hewlett-Packard Development Company, L.P.
 */

package com.fortify.sample.bugtracker.alm;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.methods.multipart.*;
import org.apache.commons.httpclient.util.*;
import org.apache.commons.httpclient.util.HttpURLConnection;
import org.apache.commons.lang.*;
import org.apache.commons.logging.*;
import org.w3c.dom.*;
import org.xml.sax.*;

import com.fortify.pub.bugtracker.plugin.*;
import com.fortify.pub.bugtracker.support.*;
import com.fortify.sample.bugtracker.alm.infra.*;
import com.fortify.sample.bugtracker.alm.infra.Entity;

import static com.fortify.pub.bugtracker.support.BugTrackerPluginConstants.*;

@BugTrackerPluginImplementation
public class AlmBugTrackerPlugin1 extends AbstractBatchBugTrackerPlugin {

    private enum ALMApiVersion{
        VER_11,
        VER_11_52,
    }

    private static final Log LOG = LogFactory.getLog(AlmBugTrackerPlugin1.class);
    
    
    
    private static final String STATUS_NEW = "New";
    private static final String STATUS_OPEN = "Open";
    private static final String STATUS_REOPEN = "Reopen";
    private static final String STATUS_CLOSED = "Closed";
    private static final String STATUS_FIXED = "Fixed";
    private static final String STATUS_REJECTED = "Rejected";

    private static final int DEFAULT_HTTP_PORT = 80;
    private static final int DEFAULT_HTTPS_PORT = 443;
    private static final String SEVERITY_FIELD_NAME = "severity";
    private static final String DEV_COMMENTS_FIELD_NAME = "dev-comments";
    private static final String DESCRIPTION_FIELD_NAME = "description";
    private static final String DETECTED_IN_BUILD_FIELD_NAME = "build-detected";
    private static final String CAUSED_BY_CHANGESET_FIELD_NAME = "changeset";
    private static final String NAME_FIELD_NAME = "name";
    private static final String CREATION_TIME_FIELD_NAME = "creation-time";
    private static final String DETECTED_BY_FIELD_NAME = "detected-by";
    private static final String STATUS_FIELD_NAME = "status";
    private static final String FECHA_ALTA_FIELD_NAME="user-template-02";
    private static final String TIPO_DEFECTO_FIELD_NAME = "user-template-07";
    private static final String CATEGORIA_DEFECTO_FIELD_NAME = "user-09";
    private static final String LIDER_PRUEBAS_FIELD_NAME = "user-template-10";
    private static final String PRIORIDAD_FIELD_NAME = "priority";
    private static final String ETAPA_DE_PRUEBAS_FIELD_NAME = "user-08";
    private static final String ASIGNADO_FIELD_NAME = "owner";
    private static final String APLICATIVO_FIELD_NAME = "user-06";
    private static final String DETECTADO_EN_REL_FIELD_NAME = "detected-in-rel";
    private static final String DETECTADO_EN_RCYC_FIELD_NAME = "detected-in-rcyc";
    private static final String FABRICA_FIELD_NAME = "user-13";
    private static final String FOLIO_FIELD_NAME = "user-template-04";

    private static final String CATEGORY_LABEL_NAME = "Category";

    private static final String DEFECT_ENTITY_TYPE_NAME = "defect";

    private static final String ALM_URL = "almUrl";
    private static final String SEVERITY_PARAM_NAME = "severity";
    private static final String PROJECT_PARAM_NAME = "project";
    private static final String DOMAIN_PARAM_NAME = "domain";
    private static final String SUMMARY_PARAM_NAME = "summary";
    private static final String NAME_PARAM_NAME = "name";
    private static final String DESCRIPTION_PARAM_NAME = "description";

    private static final String SUPPORTED_VERSIONS = "11, 11.5, 12";

    private String almUrlPrefix;
    private boolean isSecure;
    private String almHost;
    private int almPort;

    private static final String ALM_NOT_ACCESSIBLE = "The ALM server is not accessible. Check the plugin configuration and ensure that the server is not down or overloaded.";

    /**
     * This is the maximum number of candidate changesets that will be included
     * in the bug description.
     */
    private static final int MAX_CANDIDATE_CHANGELISTS = 20;

    private final DocumentBuilder docBuilder;

    private final XPathFactory xpathFactory;

    public AlmBugTrackerPlugin1() {

        final DocumentBuilderFactory docFactory = DocumentBuilderFactory
                .newInstance();
        docFactory.setNamespaceAware(true); // never forget this!
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (final ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        xpathFactory = XPathFactory.newInstance();

    }

    private GetMethod createRequestForBugXML(String bugId, UserAuthenticationStore credentials, HttpClient client) {
        GetMethod query = null;
        try {
            authenticateAndCreateSession(client, credentials.getUserName(),
                    credentials.getPassword());

            final String[] bugIdParts = bugId.split(":");
            if (bugIdParts.length != 3) {
                throw new IllegalArgumentException();
            }
            final String domainName = bugIdParts[0];
            final String projectName = bugIdParts[1];
            final String bugNumber = bugIdParts[2];

            query = new GetMethod(almUrlPrefix
                    + "/qcbin/rest/domains/" + domainName + "/projects/"
                    + projectName + "/defects");
            query.setQueryString(URIUtil.encodeQuery("query={id[" + bugNumber + "]}"));

            query.addRequestHeader("Accept", "application/xml");
            return query;
        } catch (URIException urie) {
            throw new RuntimeException(urie);
        }
    }

    public Bug fetchBugDetails(String bugId, UserAuthenticationStore credentials) {
        final HttpClient client = new HttpClient();
        GetMethod query = createRequestForBugXML(bugId, credentials, client);
        try {
            final int httpReturnCode = client.executeMethod(query);
            final String response = query.getResponseBodyAsString();

            final Document doc = docBuilder.parse(makeInputSource(query.getResponseBodyAsStream()));

            final XPath xpath = xpathFactory.newXPath();
            switch (httpReturnCode) {
                case HttpURLConnection.HTTP_OK:

                    final int numResults = Integer.parseInt((String) xpath.compile(
                            "/Entities/@TotalResults").evaluate(doc, XPathConstants.STRING));

                    if (numResults == 0) {
                        return null;
                    }

                    final String bugStatus = (String) xpath.compile(
                            "/Entities/Entity/Fields/Field[@Name='status']/Value/text()")
                            .evaluate(doc, XPathConstants.STRING);
                    return new Bug(bugId, bugStatus);
                default:
                    RuntimeException nested = new RuntimeException("Got HTTP return code: "
                            + httpReturnCode + "; Response: " + response);
                    throw new BugTrackerException("Could not query defects from the ALM server", nested);
            }

        } catch (final IOException e) {
            throw new BugTrackerException(
                    ALM_NOT_ACCESSIBLE,
                    e);
        } catch (final RuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (query != null) {
                query.releaseConnection();
            }
        }

    }

    private String fetchBugComments(String bugId, UserAuthenticationStore credentials) {
        final HttpClient client = new HttpClient();
        GetMethod query = createRequestForBugXML(bugId, credentials, client);
        try {
            final int httpReturnCode = client.executeMethod(query);
            final String response = query.getResponseBodyAsString();

            final Document doc = docBuilder.parse(makeInputSource(query.getResponseBodyAsStream()));
            final XPath xpath = xpathFactory.newXPath();
            switch (httpReturnCode) {
                case HttpURLConnection.HTTP_OK:

                    final int numResults = Integer.parseInt((String) xpath.compile(
                            "/Entities/@TotalResults").evaluate(doc, XPathConstants.STRING));

                    if (numResults == 0) {
                        return null;
                    }

                    final String bugComments = (String) xpath.compile(
                            "/Entities/Entity/Fields/Field[@Name='" + DEV_COMMENTS_FIELD_NAME+"']/Value/text()")
                            .evaluate(doc, XPathConstants.STRING);
                    return bugComments;
                default:
                    RuntimeException nested = new RuntimeException("Got HTTP return code: "
                            + httpReturnCode + "; Response: " + response);
                    throw new BugTrackerException("Could not query comments from the ALM server", nested);
            }

        } catch (final IOException e) {
            throw new BugTrackerException(
                    ALM_NOT_ACCESSIBLE,
                    e);
        } catch (final RuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (query != null) {
                query.releaseConnection();
            }
        }
    }

    public Bug fileBug(BugSubmission bug, UserAuthenticationStore credentials) {

        final HttpClient client = new HttpClient();
        PostMethod createDefect = null;
        try {
            authenticateAndCreateSession(client, credentials.getUserName(),
                    credentials.getPassword());


            final String domainName = bug.getParams().get(DOMAIN_PARAM_NAME);
            final String projectName = bug.getParams().get(PROJECT_PARAM_NAME);

            validateAlmDomainAndProject(domainName, projectName, client);

            String detectedInBuildInstance = null;
            if (bug.getIssueDetail().getDetectedInBuild() != null) {
                try {
                    detectedInBuildInstance = getBuildInstanceIdFromRevision(bug.getIssueDetail().getDetectedInBuild(),
                            client,domainName, projectName);
                } catch (Exception e) {
                    LOG.warn("Skipping identification of build instance where issue was detected.", e);
                }
            }

            List<String> candidateChangesets = null;

            if (bug.getIssueDetail().getLastBuildWithoutIssue() != null &&
                    bug.getIssueDetail().getDetectedInBuild() != null) {

                try {
                    candidateChangesets = queryChangesetsBetween(
                            bug.getIssueDetail().getLastBuildWithoutIssue(),
                            bug.getIssueDetail().getDetectedInBuild(),
                            bug.getIssueDetail().getFileName(),
                            bug.getParams(), credentials);
                } catch (Exception e) {
                    LOG.warn("Skipping changeset discovery", e);
                }

            }


            createDefect = new PostMethod(almUrlPrefix
                    + "/qcbin/rest/domains/" + domainName + "/projects/"
                    + projectName + "/defects");
            createDefect.addRequestHeader("Accept", "application/xml");

            final String defectXmlString = constructDefectXmlString(bug,
                    detectedInBuildInstance,
                    candidateChangesets,
                    credentials.getUserName(),
                    getAttributeNameForEntity(DEFECT_ENTITY_TYPE_NAME,
                            CATEGORY_LABEL_NAME, domainName, projectName, client),credentials,domainName,projectName);

            createDefect.setRequestEntity(new StringRequestEntity(
                    defectXmlString, "application/xml", "UTF-8"));
            createDefect.setDoAuthentication(false);

            final int httpReturnCode = client.executeMethod(createDefect);
            final String response = createDefect.getResponseBodyAsString();
            destroySession(client);

            final Document doc = docBuilder.parse(makeInputSource(createDefect.getResponseBodyAsStream()));
            final XPath xpath = xpathFactory.newXPath();

            switch (httpReturnCode) {
                case HttpURLConnection.HTTP_CREATED:
                    final String bugNumber = (String) xpath.compile(
                            "/Entity/Fields/Field[@Name='id']/Value/text()")
                            .evaluate(doc, XPathConstants.STRING);
                    final String bugId = domainName + ":" + projectName + ":" + bugNumber;

                    final String bugStatus = (String) xpath.compile(
                            "/Entities/Entity/Fields/Field[@Name='status']/Value/text()")
                            .evaluate(doc, XPathConstants.STRING);

                    try {
                        String defectUrl = createDefect.getResponseHeader("Location").getValue();
                        String shortcutFileData = "[InternetShortcut]\r\nURL=" + bug.getIssueDetail().getIssueDeepLink();
                        sendAttachment(client, defectUrl, shortcutFileData.getBytes(), "text/plain", "issueDeepLink.URL", "Deep Link to Issue in SSC");
                    } catch (Exception e) {
                        LOG.warn("Could not upload URL attachment file to defect.", e);
                    }
                    return new Bug(bugId, bugStatus);
                default:
                    RuntimeException nested = new RuntimeException("Got HTTP return code: "
                            + httpReturnCode + "; Response: " + response);
                    throw new BugTrackerException("Could not create a bug on the ALM server.", nested);

            }


        } catch (final IOException e) {
            throw new BugTrackerException(ALM_NOT_ACCESSIBLE, e);
        } catch (final RuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (createDefect != null) {
                createDefect.releaseConnection();
            }
        }

    }

    public List<BugParam> getBugParameters(IssueDetail issueDetail, com.fortify.pub.bugtracker.support.UserAuthenticationStore credentials) {
        return getBugParameters(issueDetail, credentials, null, null);
    }
    private List<BugParam> getBugParameters(IssueDetail issueDetail, com.fortify.pub.bugtracker.support.UserAuthenticationStore credentials,
                                            String domain, String project) {
        final HttpClient client = new HttpClient();
        authenticateAndCreateSession(client, credentials.getUserName(), credentials.getPassword());
        List<BugParam> bugParams = new ArrayList<BugParam>();

        final List<String> domains = getDomains(client);
        final BugParam domainParam = new BugParamChoice()
                .setChoiceList(domains)
                .setHasDependentParams(true)
                .setIdentifier(DOMAIN_PARAM_NAME)
                .setDisplayLabel("ALM Domain")
                .setRequired(true)
                .setDescription("ALM Domain against which bug is to be filed");
        bugParams.add(domainParam);
        final BugParam projectParam = new BugParamChoice()
                .setHasDependentParams(true)
                .setIdentifier(PROJECT_PARAM_NAME)
                .setDisplayLabel("ALM Project")
                .setRequired(true)
                .setDescription("ALM Project against which bug is to be filed");
        bugParams.add(projectParam);
        if (!StringUtils.isEmpty(domain)) {
            if (domains.contains(domain)) {
                final List<String> projects = getProjects(client, domain);
                ((BugParamChoice)projectParam).setChoiceList(projects);
                if (!StringUtils.isEmpty(project)) {
                    if (projects.contains(project)) {
                        GetMethod query = createRequestForBugFieldsXML(domain, project, credentials, client);
                        GetMethod listQuery = null;
                        try {
                            final int httpReturnCode = client.executeMethod(query);
                            InputStream response = query.getResponseBodyAsStream();
                            final Document doc = docBuilder.parse(makeInputSource(response));
                            final XPath xpath = xpathFactory.newXPath();
                            switch (httpReturnCode) {
                                case HttpURLConnection.HTTP_OK:
                                    final NodeList nodes = (NodeList) xpath.compile("/Fields/Field")
                                            .evaluate(doc, XPathConstants.NODESET);
                                    for (int i=0; i<nodes.getLength(); i++){
                                        BugParam bugParam = null;
                                        String type="", listId = "";
                                        boolean required = false;
                                        int size = 0;
                                        Element field = (Element) nodes.item(i);
                                        String identifier =	field.getAttribute("Name");
                                        String displayName =field.getAttribute("Label");
                                        BugParam summaryParam = null;
                                        BugParam descriptionParam = null;

                                        NodeList childNodes = field.getChildNodes();
                                        for (int j=0; j< childNodes.getLength(); j++){
                                            Node childNode = childNodes.item(j);
                                            if (childNode.getNodeName().equals("Type")){
                                                type = childNode.getTextContent();
                                            } else if (childNode.getNodeName().equals("Required")) {
                                                required =Boolean.parseBoolean(childNode.getTextContent());
                                            } else if (childNode.getNodeName().equals("Size")){
                                                size =Integer.parseInt(childNode.getTextContent());
                                            } else if (childNode.getNodeName().equals("List-Id")){
                                                listId =childNode.getTextContent();
                                            } else if ("detected-in-rcyc".equals(identifier) || "user-13".equals(identifier)){
                                            	required = true;
                                            }
                                        }
                                        if (required || "description".equals(identifier)) {
                                            if (type.equals("String") || type.equals("Number") || type.equals("Memo")){
                                                if (size == -1){
                                                    bugParam = new BugParamTextArea();
                                                } else {
                                                    bugParam = new BugParamText();
                                                }
                                                bugParam.setDisplayLabel(displayName);
                                                bugParam.setIdentifier(identifier);
                                                bugParam.setRequired(required);
                                                bugParam.setMaxLength(size);
                                                if ("description".equals(identifier)) {
                                                    if (issueDetail == null) {
                                                        bugParam.setValue("Issue Ids: $ATTRIBUTE_INSTANCE_ID$\n$ISSUE_DEEPLINK$");
                                                    } else {
                                                       bugParam.setValue(pluginHelper.buildDefaultBugDescription(issueDetail, true));                                                    	
                                                    }
                                                    descriptionParam = bugParam;
                                                } else if ("name".equals(identifier)) {
                                                    if (issueDetail == null) {
                                                        bugParam.setValue("Fix $ATTRIBUTE_CATEGORY$ in $ATTRIBUTE_FILE$");
                                                    } else {
                                                        bugParam.setValue(issueDetail.getSummary());
                                                        
                                                    }
                                                    summaryParam = bugParam;
                                                } else {
                                                    // add descriptionParam and summaryParam later
                                                    bugParams.add(bugParam);
                                                }
                                            } else if (type.equalsIgnoreCase("lookuplist")){
                                                if(!"user-template-04".equals(identifier)){
                                                	bugParam = new BugParamChoice();
                                                listQuery = getLookupList(domain, project, listId, credentials, client, ALMApiVersion.VER_11);
                                                int listHttpReturnCode = client.executeMethod(listQuery);
                                                // It's not a very smart solution, but I could not find a way to know the version of ALM
                                                // server. So, I need to check both possible urls for getting the lists.
                                                // If first one is not working, it means that most likly we are working with ALM 11.52.
                                                if (listHttpReturnCode != HttpURLConnection.HTTP_OK) {
                                                    listQuery = getLookupList(domain, project, listId, credentials, client, ALMApiVersion.VER_11_52);
                                                    listHttpReturnCode = client.executeMethod(listQuery);
                                                }
                                                String listResponse = listQuery.getResponseBodyAsString();
                                                final Document listDoc = docBuilder.parse(makeInputSource(listQuery.getResponseBodyAsStream()));
                                                final XPath listXpath = xpathFactory.newXPath();
                                                switch (listHttpReturnCode) {
                                                    case HttpURLConnection.HTTP_OK:
                                                        final NodeList choices = (NodeList) listXpath.compile("/Lists/List/Items/Item").evaluate(listDoc, XPathConstants.NODESET);
                                                        ArrayList<String> choiceList = new ArrayList<String>();
                                                        for (int l=0; l<choices.getLength(); l++){
                                                            Element itemElem = (Element)choices.item(l);
                                                            itemElem.getAttribute("value");
                                                            NamedNodeMap map = itemElem.getAttributes();
                                                            Node valueNode = map.getNamedItem("value");
                                                            String value = itemElem.getAttributes().getNamedItem("value").getTextContent();
                                                            choiceList.add(itemElem.getAttributes().getNamedItem("value").getTextContent());
                                                        }
                                                        bugParam = new BugParamChoice().setChoiceList(choiceList);
                                                        bugParam.setDisplayLabel(displayName);
                                                        bugParam.setIdentifier(identifier);
                                                        bugParam.setRequired(required);
                                                        bugParam.setMaxLength(size);
                                                        bugParams.add(bugParam);
                                                        break;
                                                    default:
                                                        RuntimeException nested = new RuntimeException("Got HTTP return code: "
                                                                + listHttpReturnCode + "; Response: " + listResponse);
                                                        throw new BugTrackerException("Could not query comments from the ALM server", nested);
                                                }
                                            }
                                            }else if (type.equalsIgnoreCase("Reference")){                                            	
                                            	if("detected-in-rcyc".equals(identifier)){                                            		
                                            		bugParam = new BugParamChoice();  
                                            		String nombrecycle=null; int parentcycle=0;
                                            		String nombrerelease=null,nombrefolder=null,  campo=null; int idrelease=0, idfolder=0, parentrelease=0;
                                                    listQuery = getCycles(domain, project, credentials, client);
                                                    int listHttpReturnCode = client.executeMethod(listQuery);                                                     
                                                    String listResponse = listQuery.getResponseBodyAsString();
                                                    //GetMethod listQuery2 = getReleases(domain, project, credentials, client);
                                            		//int listHttpReturnCode2 = client.executeMethod(listQuery2);                                                     
                                                    //String listResponse2 = listQuery2.getResponseBodyAsString();
                                                    //GetMethod listQuery3 = getFolders(domain, project, credentials, client);
                                            		//int listHttpReturnCode3 = client.executeMethod(listQuery3);                                                     
                                                    //String listResponse3 = listQuery3.getResponseBodyAsString();                                                    
                                                    DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();                                                    
                                                    DocumentBuilder builder =  builderFactory.newDocumentBuilder();
                                                    Document xmlDocument = builder.parse(makeInputSource(listQuery.getResponseBodyAsStream()));                                                    
                                                    XPath xPath =  XPathFactory.newInstance().newXPath();
                                                    final Document listDoc = docBuilder.parse(makeInputSource(listQuery.getResponseBodyAsStream()));                                                    
                                                    final XPath listXpath = xpathFactory.newXPath();
                                                    //final Document listDoc2 = docBuilder.parse(makeInputSource(listQuery2.getResponseBodyAsStream()));
                                                    //final XPath listXpath2 = xpathFactory.newXPath();
                                                    //final Document listDoc3 = docBuilder.parse(makeInputSource(listQuery3.getResponseBodyAsStream()));
                                                    //final XPath listXpath3 = xpathFactory.newXPath();
                                                    switch (listHttpReturnCode) {
                                                        case HttpURLConnection.HTTP_OK: 
                                                        	final NodeList cycles = (NodeList)listXpath.compile("Entities/Entity/Fields").evaluate(listDoc, XPathConstants.NODESET);
                                                            //final NodeList releases = (NodeList) listXpath2.compile("Entities/Entity/Fields").evaluate(listDoc2, XPathConstants.NODESET);
                                                            //final NodeList folders = (NodeList) listXpath3.compile("Entities/Entity/Fields").evaluate(listDoc3, XPathConstants.NODESET);
                                                            ArrayList<String> choiceList = new ArrayList<String>();
                                                            for(int x=0;x<cycles.getLength();x++){
                                                            	Element fieldcycles = (Element) cycles.item(x);
                                                            	NodeList childNodesC = fieldcycles.getChildNodes();                                                             
                                                            	for (int j=0; j< childNodesC.getLength(); j++){            		
                                                            		Node childNodeC = childNodesC.item(j);
                                                            		if (childNodeC.getAttributes().getNamedItem("Name").getTextContent().equals("name")){            			
                                                            			NodeList nodo = childNodeC.getChildNodes();
                                                            			nombrecycle=nodo.item(0).getTextContent();                                                            			
                                                                   }
                                                            		
                                                            		if (childNodeC.getAttributes().getNamedItem("Name").getTextContent().equals("parent-id")){            			
                                                            			NodeList nodo1 = childNodeC.getChildNodes(); 
                                                            			parentcycle=Integer.parseInt(nodo1.item(0).getTextContent());                                                                        
                                                                			}                                                            	
                                                                		}
                                                            	
                                                            	//aqui comienza ha sacar los releases
                                                            	GetMethod listQuery2 = getReleases(parentcycle, domain, project, credentials, client);
                                                        		int listHttpReturnCode2 = client.executeMethod(listQuery2);   
                                                        		String listResponse2 = listQuery2.getResponseBodyAsString();
                                                        		final Document listDoc2 = docBuilder.parse(makeInputSource(listQuery2.getResponseBodyAsStream()));
                                                                final XPath listXpath2 = xpathFactory.newXPath();
                                                                final NodeList releases = (NodeList) listXpath2.compile("Entities/Entity/Fields").evaluate(listDoc2, XPathConstants.NODESET);
                                                            	for(int y=0;y<releases.getLength();y++){
                                                    				Element fieldreleases=(Element) releases.item(y);
                                                    				NodeList childNodesR = fieldreleases.getChildNodes();
                                                    				for(int k=0;k<childNodesR.getLength();k++){					
                                                    					Node childNodeR = childNodesR.item(k);
                                                    					if (childNodeR.getAttributes().getNamedItem("Name").getTextContent().equals("name")){                        			
                                                    	        			NodeList nodo = childNodeR.getChildNodes();
                                                    	        			nombrerelease=nodo.item(0).getTextContent();
                                                    	               }
                                                    	        		
                                                    	        		if (childNodeR.getAttributes().getNamedItem("Name").getTextContent().equals("parent-id")){                         			
                                                    	        			NodeList nodo = childNodeR.getChildNodes();
                                                    	        			parentrelease=Integer.parseInt(nodo.item(0).getTextContent());    	                    
                                                    	                }
                                                    	        		
                                                    	        		if (childNodeR.getAttributes().getNamedItem("Name").getTextContent().equals("id")){                        			
                                                    	        			NodeList nodo = childNodeR.getChildNodes();
                                                    	        			idrelease=Integer.parseInt(nodo.item(0).getTextContent());    	                   
                                                    	                } 
                                                    	            }
                                                    	            //aqui comienzan a sacar el folder
                                                    				GetMethod listQuery3 = getFolders(parentrelease, domain, project, credentials, client);
                                                            		int listHttpReturnCode3 = client.executeMethod(listQuery3);                                                     
                                                                    String listResponse3 = listQuery3.getResponseBodyAsString(); 
                                                                    final Document listDoc3 = docBuilder.parse(makeInputSource(listQuery3.getResponseBodyAsStream()));
                                                                    final XPath listXpath3 = xpathFactory.newXPath();
                                                                    final NodeList folders = (NodeList) listXpath3.compile("Entities/Entity/Fields").evaluate(listDoc3, XPathConstants.NODESET);
                                                    				for(int z=0;z<folders.getLength();z++){
                                                        				Element fieldfolder=(Element) folders.item(z);
                                                        				NodeList childNodesF = fieldfolder.getChildNodes();
                                                        				for(int l=0;l<childNodesF.getLength();l++){					
                                                        					Node childNodeF = childNodesF.item(l);
                                                        					if (childNodeF.getAttributes().getNamedItem("Name").getTextContent().equals("name")){                        			
                                                        	        			NodeList nodo = childNodeF.getChildNodes();
                                                        	        			nombrefolder=nodo.item(0).getTextContent();
                                                        	               }
                                                        	        		        	        		
                                                        	        		if (childNodeF.getAttributes().getNamedItem("Name").getTextContent().equals("id")){                        			
                                                        	        			NodeList nodo = childNodeF.getChildNodes();
                                                        	        			idfolder=Integer.parseInt(nodo.item(0).getTextContent());
                                                        	                } 
                                                        	            }
                                                        				if(idfolder==parentrelease){
                                                        					campo=nombrefolder+"#"+nombrerelease;
                                                        					
                                                        				}
                                                        			}
                                                    				if(parentcycle==idrelease){
                                                                		campo=campo+"#"+nombrecycle;
                                                                		choiceList.add(campo);
                                                                		}
                                                    				
                                                    			}//cierre del for de los release
                                                            	
                                                            }//Cierre de for para cycles
                                                            Collections.sort(choiceList);
                                                            bugParam = new BugParamChoice().setChoiceList(choiceList);
                                                            bugParam.setDisplayLabel(displayName);
                                                            bugParam.setIdentifier(identifier);
                                                            bugParam.setRequired(required);
                                                            bugParam.setMaxLength(size);
                                                            bugParams.add(bugParam);
                                                            break;
                                                        default:
                                                            RuntimeException nested = new RuntimeException("Got HTTP return code: "
                                                                    + listHttpReturnCode + "; Response: " + listResponse);
                                                            throw new BugTrackerException("Could not query comments from the ALM server", nested);
                                                   }//cierre del switch
                                            	}//cierre del if detected in cycle
                                            	
                                            	//aqui en caso de que querramos asignar un usuario
                                            /*}else if(type.equalsIgnoreCase("UsersList")){
                                            	if("owner".equals(identifier)){
                                               	 bugParam = new BugParamChoice();                                          
                                                    listQuery = getUsers(domain, project, credentials, client);
                                                    int listHttpReturnCode = client.executeMethod(listQuery);                                                 
                                                    String listResponse = listQuery.getResponseBodyAsString();
                                                    final Document listDoc = docBuilder.parse(makeInputSource(listQuery.getResponseBodyAsStream()));
                                                    final XPath listXpath = xpathFactory.newXPath();
                                                    switch (listHttpReturnCode) {
                                                        case HttpURLConnection.HTTP_OK:
                                                            final NodeList choices = (NodeList) listXpath.compile("/Users/User").evaluate(listDoc, XPathConstants.NODESET);
                                                            List<String> choiceList = new ArrayList<String>();
                                                            for (int l=0; l<choices.getLength(); l++){
                                                                Element itemElem = (Element)choices.item(l);
                                                                itemElem.getAttribute("Name");                                                            
                                                                NamedNodeMap map = itemElem.getAttributes();
                                                                Node valueNode = map.getNamedItem("Name");
                                                                String value = itemElem.getAttributes().getNamedItem("Name").getTextContent();
                                                                choiceList.add(itemElem.getAttributes().getNamedItem("Name").getTextContent());
                                                               
                                                            }
                                                            bugParam = new BugParamChoice().setChoiceList(choiceList);
                                                            bugParam.setDisplayLabel(displayName);
                                                            bugParam.setIdentifier(identifier);
                                                            bugParam.setRequired(required);
                                                            bugParam.setMaxLength(size);
                                                            bugParams.add(bugParam);
                                                            break;
                                                        default:
                                                            RuntimeException nested = new RuntimeException("Got HTTP return code: "
                                                                    + listHttpReturnCode + "; Response: " + listResponse);
                                                            throw new BugTrackerException("Could not query comments from the ALM server", nested);
                                                    }
                                               	}*/	
                                            	
                                            }else {
                                                continue;
                                            }
                                        }
                                        // add summary and description params
                                        // order is domain, project ,summary, description, everything else...
                                        if (descriptionParam != null) {
                                            bugParams.add(2, descriptionParam);
                                        }
                                        if (summaryParam != null) {
                                            bugParams.add(2, summaryParam);
                                        }
                                    }
                                    break;
                                default:
                                    RuntimeException nested = new RuntimeException("Got HTTP return code: "
                                            + httpReturnCode + "; Response: " + response);
                                    throw new BugTrackerException("Could not query comments from the ALM server", nested);
                            }

                        } catch (final IOException e) {
                            throw new BugTrackerException(
                                    ALM_NOT_ACCESSIBLE,
                                    e);
                        } catch (final RuntimeException e) {
                            throw e;
                        } catch (final Exception e) {
                            throw new RuntimeException(e);
                        } finally {
                            if (query != null) {
                                query.releaseConnection();
                            }
                            if (listQuery != null){
                                listQuery.releaseConnection();
                            }
                        }
                    }
                }
            }
        }
        return bugParams;
    }
    
private GetMethod getReleases(final int cycleparent, final String domainName, final String projectName, UserAuthenticationStore credentials, HttpClient client) {
        
        GetMethod query = null;
        try {
            authenticateAndCreateSession(client, credentials.getUserName(),
                    credentials.getPassword());

            query = new GetMethod(almUrlPrefix
                    + "/qcbin/rest/domains/" + domainName + "/projects/"
                    + projectName + "/releases?query=%7Bid%5B"+cycleparent+"%5D%7D" );
            query.addRequestHeader("Accept", "application/xml");
            return query;
        } catch (Exception urie) {
            throw new RuntimeException(urie);
        }
    }

private GetMethod getFolders(final int releaseparent, final String domainName, final String projectName, UserAuthenticationStore credentials, HttpClient client) {
    
    GetMethod query = null;
    try {
        authenticateAndCreateSession(client, credentials.getUserName(),
                credentials.getPassword());

        query = new GetMethod(almUrlPrefix
                + "/qcbin/rest/domains/" + domainName + "/projects/"
                + projectName + "/release-folders?query=%7Bid%5B"+releaseparent+"%5D%7D" );
        query.addRequestHeader("Accept", "application/xml");
        return query;
    } catch (Exception urie) {
        throw new RuntimeException(urie);
    }
}

private GetMethod getCycles(final String domainName, final String projectName, UserAuthenticationStore credentials, HttpClient client) {
    
    GetMethod query = null;
    try {
        authenticateAndCreateSession(client, credentials.getUserName(),
                credentials.getPassword());
        query = new GetMethod(almUrlPrefix
                + "/qcbin/rest/domains/" + domainName + "/projects/"
                + projectName + "/release-cycles?query=%7Bname%5B%27%2AFortify%2A%27%5D%7D" );
        query.addRequestHeader("Accept", "application/xml");
        return query;
    } catch (Exception urie) {
        throw new RuntimeException(urie);
    }
}
    
    private GetMethod getUsers(final String domainName, final String projectName, UserAuthenticationStore credentials, HttpClient client) {
        
        GetMethod query = null;
        try {
            authenticateAndCreateSession(client, credentials.getUserName(),
                    credentials.getPassword());

            query = new GetMethod(almUrlPrefix
                    + "/qcbin/rest/domains/" + domainName + "/projects/"
                    + projectName + "/customization/users" );
            query.addRequestHeader("Accept", "application/xml");
            return query;
        } catch (Exception urie) {
            throw new RuntimeException(urie);
        }
    }
    
    private String getCyclesValues(String cadena,com.fortify.pub.bugtracker.support.UserAuthenticationStore credentials, String domain, String project){    	 
    	GetMethod listQuery = null;
    	String resultado=null;
    	String campos[]=cadena.split("#");
		String nombrecycle=null, nombrerelease=null, nombrefolder=null; 
		int idrelease=0, idfolder=0, parentrelease=0, parentcycle=0, idcycle=0;
		try{	    	
	    	final HttpClient client = new HttpClient();
	        authenticateAndCreateSession(client, credentials.getUserName(), credentials.getPassword());
	        listQuery = getCycles(domain, project, credentials, client);
	        int listHttpReturnCode = client.executeMethod(listQuery);                                                     
	        String listResponse = listQuery.getResponseBodyAsString();
	        //GetMethod listQuery2 = getReleases(domain, project, credentials, client);
			//int listHttpReturnCode2 = client.executeMethod(listQuery2);                                                     
	        //String listResponse2 = listQuery2.getResponseBodyAsString();
	        //GetMethod listQuery3 = getFolders(domain, project, credentials, client);
			//int listHttpReturnCode3 = client.executeMethod(listQuery3);                                                     
	        //String listResponse3 = listQuery3.getResponseBodyAsString();
	        final Document listDoc = docBuilder.parse(makeInputSource(listQuery.getResponseBodyAsStream()));
	        final XPath listXpath = xpathFactory.newXPath();
	        //final Document listDoc2 = docBuilder.parse(makeInputSource(listQuery2.getResponseBodyAsStream()));
	        //final XPath listXpath2 = xpathFactory.newXPath();
	        //final Document listDoc3 = docBuilder.parse(makeInputSource(listQuery3.getResponseBodyAsStream()));
	        //final XPath listXpath3 = xpathFactory.newXPath();
	        switch (listHttpReturnCode) {
	            case HttpURLConnection.HTTP_OK:
	            	final NodeList cycles = (NodeList) listXpath.compile("Entities/Entity/Fields").evaluate(listDoc, XPathConstants.NODESET);
	                //final NodeList releases = (NodeList) listXpath2.compile("Entities/Entity/Fields").evaluate(listDoc2, XPathConstants.NODESET);
	                //final NodeList folders = (NodeList) listXpath3.compile("Entities/Entity/Fields").evaluate(listDoc3, XPathConstants.NODESET);
	                for(int x=0;x<cycles.getLength();x++){
	                	Element fieldcycles = (Element) cycles.item(x);
	                	NodeList childNodesC = fieldcycles.getChildNodes();
	                	for(int i=0;i<childNodesC.getLength();i++){
	                		Node childNodeC = childNodesC.item(i);
	                		if (childNodeC.getAttributes().getNamedItem("Name").getTextContent().equals("name")){            			
	                			NodeList nodo = childNodeC.getChildNodes();
	                			nombrecycle=nodo.item(0).getTextContent();}
	                		if (childNodeC.getAttributes().getNamedItem("Name").getTextContent().equals("parent-id")){            			
	                			NodeList nodo = childNodeC.getChildNodes(); 
	                			parentcycle=Integer.parseInt(nodo.item(0).getTextContent());}
	                		if (childNodeC.getAttributes().getNamedItem("Name").getTextContent().equals("id")){            			
	                			NodeList nodo = childNodeC.getChildNodes(); 
	                			idcycle=Integer.parseInt(nodo.item(0).getTextContent());} 
	                	}
	                	
	                	GetMethod listQuery2 = getReleases(parentcycle, domain, project, credentials, client);
	        			int listHttpReturnCode2 = client.executeMethod(listQuery2);                                                     
	        	        String listResponse2 = listQuery2.getResponseBodyAsString();
	        	        final Document listDoc2 = docBuilder.parse(makeInputSource(listQuery2.getResponseBodyAsStream()));
	        	        final XPath listXpath2 = xpathFactory.newXPath();
	        	        final NodeList releases = (NodeList) listXpath2.compile("Entities/Entity/Fields").evaluate(listDoc2, XPathConstants.NODESET);
	                	if(nombrecycle.equals(campos[2])){
	                		for(int y=0;y<releases.getLength();y++){
	                			Element fieldreleases = (Element) releases.item(y);
	                        	NodeList childNodesR = fieldreleases.getChildNodes();
	                        	for(int j=0;j<childNodesR.getLength();j++){
	                        		Node childNodeR = childNodesR.item(j);
	                        		if (childNodeR.getAttributes().getNamedItem("Name").getTextContent().equals("name")){            			
	                        			NodeList nodo = childNodeR.getChildNodes();
	                        			nombrerelease=nodo.item(0).getTextContent();}
	                        		if (childNodeR.getAttributes().getNamedItem("Name").getTextContent().equals("parent-id")){            			
	                        			NodeList nodo = childNodeR.getChildNodes(); 
	                        			parentrelease=Integer.parseInt(nodo.item(0).getTextContent());}
	                        		if (childNodeR.getAttributes().getNamedItem("Name").getTextContent().equals("id")){            			
	                        			NodeList nodo = childNodeR.getChildNodes(); 
	                        			idrelease=Integer.parseInt(nodo.item(0).getTextContent());}
	                        	}
	                        	if(parentcycle==idrelease){
	                        		if(nombrerelease.equals(campos[1])){
	                        			GetMethod listQuery3 = getFolders(parentrelease, domain, project, credentials, client);
	                        			int listHttpReturnCode3 = client.executeMethod(listQuery3);                                                     
	                        	        String listResponse3 = listQuery3.getResponseBodyAsString();
	                        	        final Document listDoc3 = docBuilder.parse(makeInputSource(listQuery3.getResponseBodyAsStream()));
	                        	        final XPath listXpath3 = xpathFactory.newXPath();
	                        	        final NodeList folders = (NodeList) listXpath3.compile("Entities/Entity/Fields").evaluate(listDoc3, XPathConstants.NODESET);
	                        			for(int z=0;z<folders.getLength();z++){
	                        				Element fieldfolders = (Element) folders.item(z);
	                                    	NodeList childNodesF = fieldfolders.getChildNodes();
	                                    	for(int k=0;k<childNodesF.getLength();k++){
	                                    		Node childNodeF = childNodesF.item(k);
	                                    		if (childNodeF.getAttributes().getNamedItem("Name").getTextContent().equals("name")){            			
	                                    			NodeList nodo = childNodeF.getChildNodes();
	                                    			nombrefolder=nodo.item(0).getTextContent();}
	                                    		if (childNodeF.getAttributes().getNamedItem("Name").getTextContent().equals("id")){            			
	                                    			NodeList nodo = childNodeF.getChildNodes(); 
	                                    			idfolder=Integer.parseInt(nodo.item(0).getTextContent());}
	                                    	}
	                                    	if(parentrelease==idfolder){
	                                    		if(nombrefolder.equals(campos[0])){
	                                    			resultado=idcycle+"#"+idrelease+"#"+nombrefolder;                                			
	                                    		}
	                                    	}
	                        			}
	                        		}
	                        	}
	                		}
	                	}
	                }
	                break;
	            default:
	                RuntimeException nested = new RuntimeException("Got HTTP return code: "
	                        + listHttpReturnCode + "; Response: " + listResponse);
	                throw new BugTrackerException("Could not query comments from the ALM server", nested);
	       }
		}catch (final IOException e) {
            throw new BugTrackerException(
                    ALM_NOT_ACCESSIBLE,
                    e);
        } catch (final RuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            
            if (listQuery != null){
                listQuery.releaseConnection();
            	}
            }
       return resultado;
    }
    
    private GetMethod getLookupList(final String domainName, final String projectName, final String id, UserAuthenticationStore credentials, HttpClient client, ALMApiVersion almApiVersion) {
        String listsUrl;
        if (almApiVersion == ALMApiVersion.VER_11_52) {
            listsUrl = "used-lists";
        } else {
            listsUrl = "lists";
        }
        GetMethod query = null;
        try {
            authenticateAndCreateSession(client, credentials.getUserName(),
                    credentials.getPassword());

            query = new GetMethod(almUrlPrefix
                    + "/qcbin/rest/domains/" + domainName + "/projects/"
                    + projectName + "/customization/" + listsUrl + "?id="+id);

            query.addRequestHeader("Accept", "application/xml");
            return query;
        } catch (Exception urie) {
            throw new RuntimeException(urie);
        }
    }

    private GetMethod createRequestForBugFieldsXML(final String domainName, final String projectName, UserAuthenticationStore credentials, HttpClient client) {
        GetMethod query = null;
        try {
            authenticateAndCreateSession(client, credentials.getUserName(),
                    credentials.getPassword());

            query = new GetMethod(almUrlPrefix
                    + "/qcbin/rest/domains/" + domainName + "/projects/"
                    + projectName + "/customization/entities/defect/fields");

            query.addRequestHeader("Accept", "application/xml");
            return query;
        } catch (Exception urie) {
            throw new RuntimeException(urie);
        }
    }

    /**
     * Proxy fnction with nin array argumante.
     * See comment to the queryChangesetsBetween function for more detailed information.
     */
    private List<String> queryChangesetsBetween(String greaterThanRevision,
                                                String lesserThanOrEqualToRevision,
                                                String touchingFilePath,
                                                Map<String,String> bugParams, UserAuthenticationStore credentials) {
        return queryChangesetsBetween(
                Arrays.asList(greaterThanRevision),
                Arrays.asList(lesserThanOrEqualToRevision),
                touchingFilePath,
                bugParams,
                credentials
        );
    }

    /*
     * This implementation assumes that revision field of changeset-file entity
     * corresponds to the snapshot version of the repository, which is what is
     * tagged for our scans and passed in as parameters for this method. This
     * may not be true for VCSes like CVS and ClearCase which maintain revisions
     * based on file, and not changesets.
     *
     * This was tested successfully with ALM11+ALI1 hooked to a Subversion repository
     *
     */
    private List<String> queryChangesetsBetween(Collection<String> greaterThanRevision,
                                                Collection<String> lesserThanOrEqualToRevision, String touchingFilePath,
                                                Map<String,String> bugParams, UserAuthenticationStore credentials) {

        final HttpClient client = new HttpClient();
        GetMethod query = null;

        try {

            authenticateAndCreateSession(client, credentials.getUserName(),
                    credentials.getPassword());

            final String domainName = bugParams.get(DOMAIN_PARAM_NAME);
            final String projectName = bugParams.get(PROJECT_PARAM_NAME);

            validateAlmDomainAndProject(domainName, projectName, client);

			/*
			 * This information is not available in a straightforward way since
			 * revision field of changeset-file is a string and only
			 * lexicographic ordering can be used. Numeric ordering is not
			 * available.
			 *
			 * Hence we first get the commit times of each of the two boundary
			 * revisions provided by querying the changesets. Then we query for
			 * changesets that touch our concerned file, and also fall between
			 * the two commit times
			 */
            final List<String> startDates = new ArrayList<String>();
            final List<String> endDates = new ArrayList<String>();

            for (final String revision : greaterThanRevision) {
                startDates.add(getChangesetDateFromRevision(revision, client, domainName, projectName));
            }

            for (final String revision : lesserThanOrEqualToRevision) {
                endDates.add(getChangesetDateFromRevision(revision, client, domainName, projectName));
            }

            final String startDate = (startDates.isEmpty() ? null : startDates.get(0));
            final String endDate = (endDates.isEmpty() ? null : endDates.get(endDates.size() - 1));

            query = new GetMethod(almUrlPrefix
                    + "/qcbin/rest/domains/" + domainName + "/projects/"
                    + projectName + "/changesets");

            final StringBuilder filter = new StringBuilder("{date[");
            if (greaterThanRevision != null) {
                filter.append("> '" + startDate + "' AND ");
            }
            filter.append("<= '" + endDate + "']; changeset-file.path['*"
                    + touchingFilePath + "']}");
            final String params = URIUtil.encodeQuery("page-size="
                    + MAX_CANDIDATE_CHANGELISTS + "&query=" + filter
                    + "&order-by={date[DESC]}");
            query.setQueryString(params);
            query.addRequestHeader("Accept", "application/xml");

            final int httpReturnCode = client.executeMethod(query);
            final String response = query.getResponseBodyAsString();
            destroySession(client);

            final Document doc = docBuilder.parse(makeInputSource(query.getResponseBodyAsStream()));
            final XPath xpath = xpathFactory.newXPath();

            switch (httpReturnCode) {

                case HttpURLConnection.HTTP_OK:
                    final List<String> candidateRevisions = new ArrayList<String>();
                    final NodeList result = (NodeList) xpath.compile(
                            "/Entities/Entity/Fields/Field[@Name='id']/Value")
                            .evaluate(doc, XPathConstants.NODESET);
                    final NodeList nodes = result;
                    for (int i = 0; i < nodes.getLength(); i++) {
                        candidateRevisions.add(nodes.item(i).getTextContent());
                    }
                    return candidateRevisions;
                default:
                    RuntimeException nested = new RuntimeException("Got HTTP return code: "
                            + httpReturnCode + "; Response: " + response);
                    throw new BugTrackerException("Could not query changesets from the ALM server", nested);
            }

        } catch (final IOException e) {
            throw new BugTrackerException(ALM_NOT_ACCESSIBLE, e);
        } catch (final RuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (query != null) {
                query.releaseConnection();
            }
        }
    }

    public List<BugTrackerConfig> getConfiguration() {

        final BugTrackerConfig supportedVersions = new BugTrackerConfig()
                .setIdentifier(DISPLAY_ONLY_SUPPORTED_VERSION)
                .setDisplayLabel("Supported Versions")
                .setDescription("Bug Tracker versions supported by the plugin")
                .setValue(SUPPORTED_VERSIONS)
                .setRequired(false);

        final BugTrackerConfig almUrlConfig = new BugTrackerConfig()
                .setIdentifier(ALM_URL)
                .setDisplayLabel("ALM URL")
                .setDescription(
                        "URL at which ALM REST API is accessible. Example: http://w2k3r2sp2:8080")
                .setRequired(true);

        List<BugTrackerConfig> configs = Arrays.asList(supportedVersions, almUrlConfig);
        pluginHelper.populateWithDefaultsIfAvailable(configs);
        return configs;

    }

    public String getLongDisplayName() {
        return "HP ALM at " + almUrlPrefix;
    }

    public String getShortDisplayName() {
        return "HP ALM-12";
    }

    public List<BugParam> onParameterChange(IssueDetail issueDetail,
                                            String changedParamIdentifier,
                                            List<BugParam> currentValues,
                                            com.fortify.pub.bugtracker.support.UserAuthenticationStore credentials) {

        boolean isDomainChanged = DOMAIN_PARAM_NAME.equals(changedParamIdentifier);
        boolean isProjectChanged = PROJECT_PARAM_NAME.equals(changedParamIdentifier);
        if (!(isDomainChanged || isProjectChanged)) {
            throw new IllegalArgumentException(
                    "We should not be getting any other parameter since we didnt mark any other param as having dependent params");
        }
        if (isDomainChanged) {
            BugParamChoice projectParam = (BugParamChoice)pluginHelper.findParam(PROJECT_PARAM_NAME, currentValues);
            final HttpClient client = new HttpClient();
            authenticateAndCreateSession(client, credentials.getUserName(), credentials.getPassword());
            BugParam domainParam = pluginHelper.findParam(DOMAIN_PARAM_NAME, currentValues);
            if (StringUtils.isEmpty(domainParam.getValue())) {
                projectParam.setChoiceList(Arrays.<String>asList());
            } else {
                projectParam.setChoiceList(getProjects(client, domainParam.getValue()));
            }
            projectParam.setValue(null);
        } else if (isProjectChanged) {
            String domain = pluginHelper.findParam(DOMAIN_PARAM_NAME, currentValues).getValue();
            String project = pluginHelper.findParam(PROJECT_PARAM_NAME, currentValues).getValue();
            List<BugParam> newParams = getBugParameters(issueDetail, credentials, domain, project);
            for (BugParam bugParam : newParams) {
                BugParam currentParam = pluginHelper.findParam(bugParam.getIdentifier(), currentValues);
                if (currentParam != null) {
                    bugParam.setValue(currentParam.getValue());
                }
            }
            return newParams;
        }
        return currentValues;
    }

    public boolean requiresAuthentication() {
        return true;
    }

    public void setConfiguration(Map<String, String> config) {

        almUrlPrefix = config.get(ALM_URL);

        if (almUrlPrefix == null) {
            throw new IllegalArgumentException("Invalid configuration passed");
        }

        if (almUrlPrefix.endsWith("/")) {
            almUrlPrefix = almUrlPrefix.substring(0,almUrlPrefix.length()-1);
        }

        if (!almUrlPrefix.startsWith("http://") && !almUrlPrefix.startsWith("https://")) {
            throw new BugTrackerException("ALM URL protocol should be either http or https");
        }

        try {
            URL almUrl = new java.net.URL(almUrlPrefix);
            almHost = almUrl.getHost();
            isSecure = almUrl.getProtocol().equals("https");
            almPort = almUrl.getPort();
            if (almPort == -1) {
				/*  Not specified */
                almPort= isSecure?DEFAULT_HTTPS_PORT:DEFAULT_HTTP_PORT;
            }
        } catch (MalformedURLException e) {
            throw new BugTrackerException("Invalid ALM URL: " + almUrlPrefix);
        }


    }

    public void testConfiguration(
            com.fortify.pub.bugtracker.support.UserAuthenticationStore credentials) {
        validateCredentials(credentials);
    }

    public void validateCredentials(UserAuthenticationStore credentials)
            throws RuntimeException {

        final HttpClient client = new HttpClient();
        authenticateAndCreateSession(client, credentials.getUserName(),
                credentials.getPassword());

        destroySession(client);

    }

    public String getBugDeepLink(String bugId) {

        final String[] bugIdParts = bugId.split(":");
        if (bugIdParts.length != 3) {
            throw new IllegalArgumentException();
        }
        final String domainName = bugIdParts[0];
        final String projectName = bugIdParts[1];
        final String bugNumber = bugIdParts[2];

        final String link = getTdProtocol() +"://"
                + projectName
                + "."
                + domainName
                + "."
                + almHost
                + ":"
                + almPort
                + "/qcbin/DefectsModule-000000004243046514?EntityType=IBug&ShowDetails=Y&EntityID="
                + bugNumber;
        return link;

    }

    private void authenticateAndCreateSession(HttpClient client,
                                              String username, String password) {
    	Properties systemProps = System.getProperties();
		 systemProps.put(
		    "javax.net.ssl.trustStore",
		    "C:/Program Files/Java/jdk1.7.0_45/jre/lib/security/cacerts"
		);
		System.setProperties(systemProps);
        int httpReturnCode = 0;
        GetMethod authenticate = null;

        try {        	
            client.getParams().setParameter(AuthPolicy.AUTH_SCHEME_PRIORITY, new ArrayList<String>(Collections.singletonList(AuthPolicy.BASIC)));
            client.getState().setCredentials(
                    new AuthScope(almHost, almPort),
                    new UsernamePasswordCredentials(username, password));
            
            authenticate = new GetMethod(almUrlPrefix
                    + "/qcbin/authentication-point/authenticate");
         
            authenticate.addRequestHeader("Accept", "application/xml");
            authenticate.setDoAuthentication(true);
            httpReturnCode = client.executeMethod(authenticate);
            authenticate.getResponseBodyAsString();

            switch (httpReturnCode) {
                case HttpURLConnection.HTTP_OK:
                    // need to get xsrf and qc cookies for ALM 12.  This doesn't seem to have any effect on ALM 11 or 11.5
                    PostMethod getSession = new PostMethod(almUrlPrefix + "/qcbin/rest/site-session");
                    getSession.addRequestHeader("Accept", "application/xml");
                    httpReturnCode = client.executeMethod(getSession);
                    getSession.getResponseBodyAsString();
                    if (!(httpReturnCode == HttpURLConnection.HTTP_CREATED || httpReturnCode == HttpURLConnection.HTTP_OK)) {
                    	
                    	throw new BugTrackerAuthenticationException("Problem getting QCSession cookie");
                      
                    }
                   
                    return;
                default:
                    throw new BugTrackerAuthenticationException(
                            "The credentials provided were invalid");
            }

        } catch (final IOException e) {
            throw new BugTrackerException(ALM_NOT_ACCESSIBLE+" ALM fail login", e);
        } catch (final RuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (authenticate != null) {
                authenticate.releaseConnection();
            }
        }
    }

    private String constructDefectXmlString(BugSubmission bug,
                                            String detectedInBuildInstanceId, List<String> candidateChangesets,
                                            String detectedByUser, String categoryAttributeName, UserAuthenticationStore credentials,String domain, String project) throws Exception {

        final Entity defect = new Entity();
        defect.setType(DEFECT_ENTITY_TYPE_NAME);
        final Entity.Fields fields = new Entity.Fields();
        defect.setFields(fields);

        final Entity.Fields.Field detectedBy = new Entity.Fields.Field();
        detectedBy.setName(DETECTED_BY_FIELD_NAME);
        detectedBy.getValue().add(detectedByUser);
        fields.getField().add(detectedBy);

        final Entity.Fields.Field creationTime = new Entity.Fields.Field();
        creationTime.setName(CREATION_TIME_FIELD_NAME);
        creationTime.getValue().add(
                new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        fields.getField().add(creationTime); 
        
        String orig=new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(new Date()); 	    
	    String result = orig.substring(0,21) + "." + orig.substring(21,22)+".";	    
        final Entity.Fields.Field fechaalta = new Entity.Fields.Field();
        fechaalta.setName(FECHA_ALTA_FIELD_NAME);
        fechaalta.getValue().add(result.toLowerCase());       
        fields.getField().add(fechaalta);

        if (categoryAttributeName != null && categoryAttributeName.length() != 0) {
            final Entity.Fields.Field category = new Entity.Fields.Field();
            category.setName(categoryAttributeName);
            category.getValue().add("Fortify - " + bug.getIssueDetail().getAnalysisType());
            fields.getField().add(category);
        }

        final Entity.Fields.Field name = new Entity.Fields.Field();
        name.setName(NAME_FIELD_NAME);
        name.getValue().add(anyNotNull(bug.getParams().get(SUMMARY_PARAM_NAME), bug.getParams().get(NAME_PARAM_NAME)));
        fields.getField().add(name);

        if (detectedInBuildInstanceId != null && detectedInBuildInstanceId.length() != 0) {
            final Entity.Fields.Field detectedInBuild = new Entity.Fields.Field();
            detectedInBuild.setName(DETECTED_IN_BUILD_FIELD_NAME);
            detectedInBuild.getValue().add(detectedInBuildInstanceId);
            fields.getField().add(detectedInBuild);
        }


        if (candidateChangesets != null && candidateChangesets.size() == 1) {
            final Entity.Fields.Field causedByChangeset = new Entity.Fields.Field();
            causedByChangeset.setName(CAUSED_BY_CHANGESET_FIELD_NAME);
            causedByChangeset.getValue().add(candidateChangesets.get(0));
            fields.getField().add(causedByChangeset);
        }

        final Entity.Fields.Field description = new Entity.Fields.Field();
        description.setName(DESCRIPTION_FIELD_NAME);
        description.getValue().add(convertToHtml(massageBugDescription(bug.getParams().get(DESCRIPTION_PARAM_NAME), candidateChangesets)));
        fields.getField().add(description);

        final Entity.Fields.Field comments = new Entity.Fields.Field();
        comments.setName(DEV_COMMENTS_FIELD_NAME);
        final StringBuilder allComments = new StringBuilder();
        for (final IssueComment c : bug.getIssueDetail().getComments()) {
            allComments.append("[").append(c.getUsername())
                    .append(" on " + c.getTimestamp() + "]: " + c.getBody());
            allComments.append("\n\n");
        }
        comments.getValue().add(convertToHtml(allComments.toString()));
        fields.getField().add(comments);

        final String severityValue = bug.getParams().get(SEVERITY_PARAM_NAME);
        final Entity.Fields.Field severity = new Entity.Fields.Field();
        severity.setName(SEVERITY_FIELD_NAME);       
        severity.getValue().add(severityValue);
        fields.getField().add(severity);
        
        final String aplicativovalue = bug.getParams().get(APLICATIVO_FIELD_NAME);
        final Entity.Fields.Field aplicativo = new Entity.Fields.Field();
        aplicativo.setName(APLICATIVO_FIELD_NAME);
        aplicativo.getValue().add(aplicativovalue);
        fields.getField().add(aplicativo);
              
        final Entity.Fields.Field tipo = new Entity.Fields.Field();
        tipo.setName(TIPO_DEFECTO_FIELD_NAME);
        tipo.getValue().add("No Funcional");
        fields.getField().add(tipo);
        
        final Entity.Fields.Field categoria = new Entity.Fields.Field();
        categoria.setName(CATEGORIA_DEFECTO_FIELD_NAME);
        categoria.getValue().add("Ejecucion de Pruebas");
        fields.getField().add(categoria);
        
        final Entity.Fields.Field lider_pruebas = new Entity.Fields.Field();
        lider_pruebas.setName(LIDER_PRUEBAS_FIELD_NAME);
        lider_pruebas.getValue().add(detectedByUser);
        fields.getField().add(lider_pruebas);
        
        final Entity.Fields.Field prioridad = new Entity.Fields.Field();
        prioridad.setName(PRIORIDAD_FIELD_NAME);
        prioridad.getValue().add("2-Alta");
        fields.getField().add(prioridad);
        
        final Entity.Fields.Field etapa = new Entity.Fields.Field();
        etapa.setName(ETAPA_DE_PRUEBAS_FIELD_NAME);
        etapa.getValue().add("Integrales");
        fields.getField().add(etapa);
        
        final Entity.Fields.Field estado = new Entity.Fields.Field();
        estado.setName(STATUS_FIELD_NAME);
        estado.getValue().add("Nuevo");
        fields.getField().add(estado);
        
        final String fabricavalue = bug.getParams().get(FABRICA_FIELD_NAME);
        final Entity.Fields.Field fabrica = new Entity.Fields.Field();
        fabrica.setName(FABRICA_FIELD_NAME);
        fabrica.getValue().add(fabricavalue);
        fields.getField().add(fabrica);
                
        final Entity.Fields.Field owner = new Entity.Fields.Field();
        owner.setName(ASIGNADO_FIELD_NAME);
        owner.getValue().add(detectedByUser);
        fields.getField().add(owner);
                
        final String detected_in_rcycValue = bug.getParams().get(DETECTADO_EN_RCYC_FIELD_NAME);
        
        String valoresdeCRF [] = getCyclesValues(detected_in_rcycValue,credentials,domain,project).split("#");
        
        final Entity.Fields.Field detected_in_rcyc = new Entity.Fields.Field();
        detected_in_rcyc.setName(DETECTADO_EN_RCYC_FIELD_NAME);
        detected_in_rcyc.getValue().add(valoresdeCRF[0]);
        fields.getField().add(detected_in_rcyc);
        
        final Entity.Fields.Field detected_in_rel = new Entity.Fields.Field();
        detected_in_rel.setName(DETECTADO_EN_REL_FIELD_NAME);
        detected_in_rel.getValue().add(valoresdeCRF[1]);
        fields.getField().add(detected_in_rel);
        
        final Entity.Fields.Field folio = new Entity.Fields.Field();
        folio.setName(FOLIO_FIELD_NAME);
        folio.getValue().add(valoresdeCRF[2]);
        fields.getField().add(folio);

        final String defectXmlString = EntityMarshallingUtils.unmarshal(Entity.class,
                defect);
        return defectXmlString;
    }

    private String getBuildInstanceIdFromRevision(String revision, HttpClient client, String domainName,
                                                  String projectName) {
        GetMethod query = null;

        String changesetId = null;
        try {

			/* First find out the changsetId from revision number */

            query = new GetMethod(almUrlPrefix
                    + "/qcbin/rest/domains/" + domainName + "/projects/"
                    + projectName + "/changesets");
            query.addRequestHeader("Accept", "application/xml");
            query.setQueryString(URIUtil
                    .encodeQuery("page-size=1&query={changeset-file.revision['"
                            + revision + "']}"));

            int httpReturnCode = client.executeMethod(query);
            String response = query.getResponseBodyAsString();

            Document doc = docBuilder.parse(makeInputSource(query.getResponseBodyAsStream()));
            XPath xpath = xpathFactory.newXPath();
            switch (httpReturnCode) {
                case HttpURLConnection.HTTP_OK:
                    changesetId = (String) xpath
                            .compile("/Entities/Entity/Fields/Field[@Name='id']/Value/text()")
                            .evaluate(doc, XPathConstants.STRING);
                    if (changesetId == null || changesetId.length() == 0) {
                        LOG.warn("Could not query revisions from ALM. Revision '" +
                                revision + "' does not correspond to a changeset-file revision");
                        return null;
                    }
                    break;
                default:
                    LOG.warn("Could not query changesets from the ALM server. Got HTTP return code: "
                            + httpReturnCode + "; Response: " + response);
                    return null;
            }
        } catch (final IOException e) {
            throw new BugTrackerException(ALM_NOT_ACCESSIBLE, e);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (query != null) {
                query.releaseConnection();
            }
        }

		/* Using the changeset id, get the build instance id*/
        try {
            query = new GetMethod(almUrlPrefix
                    + "/qcbin/rest/domains/" + domainName + "/projects/"
                    + projectName + "/changeset-links");
            query.addRequestHeader("Accept", "application/xml");
            query.setQueryString(URIUtil
                    .encodeQuery("page-size=1&query={to-endpoint-type[build-instance];" +
                            "from-endpoint-type[changeset];from-endpoint-id[" +
                            changesetId + "]}"));

            int httpReturnCode = client.executeMethod(query);
            String response = query.getResponseBodyAsString();

            Document doc = docBuilder.parse(makeInputSource(query.getResponseBodyAsStream()));
            XPath xpath = xpathFactory.newXPath();
            switch (httpReturnCode) {
                case HttpURLConnection.HTTP_OK:
                    final String buildInstanceId = (String) xpath
                            .compile(
                                    "/Entities/Entity/Fields/Field[@Name='to-endpoint-id']/Value/text()")
                            .evaluate(doc, XPathConstants.STRING);
                    if (buildInstanceId == null || buildInstanceId.length() == 0) {
                        LOG.warn("Could not query build-instance from ALM. Changeset Id '" +
                                changesetId + "' does not correspond to a build-instance");
                        return null;
                    }
                    return buildInstanceId;
                default:
                    LOG.warn("Could not query build-instances from the ALM server. Got HTTP return code: "
                            + httpReturnCode + "; Response: " + response);
                    return null;
            }

        } catch (final IOException e) {
            throw new BugTrackerException(ALM_NOT_ACCESSIBLE, e);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (query != null) {
                query.releaseConnection();
            }
        }

    }

    /*
     * Needed because ALM descriptions need to be HTML. Otherwise newline
     * characters dont display correctly
     */
    private String convertToHtml(String description) {

        return "<html><body><p>" + description.replaceAll("[\n\r]+", "</p><p>")
                + "</p></body></html>";
    }

    private String convertCommentToHtml(String comment, String username) {

        return "<html><body><div align=\"left\"><font fact=\"Arial\"><span style=\"font-size:8pt\"><br/></span></font>" +
                "<font face=\"Arial\" color=\"#000080\" size=\"+0\">" +
                "<span style=\"font-size:8pt\"><b>" + username + ", " + new Date() + ":</b></span></font>" +
                "<font face=\"Arial\"><span style=\"font-size:8pt\"><p>"+ comment.replaceAll("[\n\r]+", "</p><p>") +
                "</p></span></font></div></body></html>";
    }

    private void destroySession(HttpClient client) {

        DeleteMethod deleteSession = null;
        try {
            deleteSession = new DeleteMethod(almUrlPrefix
                    + "/qcbin/rest/site-session");
            client.executeMethod(deleteSession);
            deleteSession.getResponseBodyAsString();
        } catch (final IOException e) {
            throw new BugTrackerException(ALM_NOT_ACCESSIBLE, e);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (deleteSession != null) {
                deleteSession.releaseConnection();
            }
        }
    }


    private String getTdProtocol() {
        return isSecure? "tds" : "td";
    }

    private String getChangesetDateFromRevision(String revision,
                                                HttpClient client, String domainName,
                                                String projectName) {

        GetMethod query = null;

        if (revision == null) {
            return null;
        }

        try {

            query = new GetMethod(almUrlPrefix
                    + "/qcbin/rest/domains/" + domainName + "/projects/"
                    + projectName + "/changesets");
            query.addRequestHeader("Accept", "application/xml");
            query.setQueryString(URIUtil
                    .encodeQuery("page-size=1&query={changeset-file.revision['"
                            + revision + "']}"));

            final int httpReturnCode = client.executeMethod(query);
            final String response = query.getResponseBodyAsString();

            final Document doc = docBuilder.parse(makeInputSource(query.getResponseBodyAsStream()));
            final XPath xpath = xpathFactory.newXPath();
            switch (httpReturnCode) {
                case HttpURLConnection.HTTP_OK:
                    final String timestamp = (String) xpath
                            .compile(
                                    "/Entities/Entity/Fields/Field[@Name='date']/Value/text()")
                            .evaluate(doc, XPathConstants.STRING);
                    if (timestamp == null || timestamp.length() == 0) {
                        throw new BugTrackerException("Could not query revisions from ALM. Revision '" +
                                revision + "' does not correspond to a changeset-file revision");
                    }
                    return timestamp;
                default:
                    RuntimeException nested = new RuntimeException("Got HTTP return code: "
                            + httpReturnCode + "; Response: " + response);
                    throw new BugTrackerException("Could not query changesets from the ALM server", nested);
            }

        } catch (final IOException e) {
            throw new BugTrackerException(ALM_NOT_ACCESSIBLE, e);
        } catch (final RuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (query != null) {
                query.releaseConnection();
            }
        }

    }

    private String massageBugDescription(String bugDescription, List<String> candidateChangesets) {

        if (candidateChangesets != null && candidateChangesets.size() > 0) {
            final StringBuilder newDesc = new StringBuilder();
            newDesc.append(bugDescription)
                    .append("\n")
                    .append("This issue could have been introduced in one of the following ALM changesets: ")
                    .append(candidateChangesets);
            return newDesc.toString();
        }
        return bugDescription;

    }

    private String getAttributeNameForEntity(String entity, String attributeLabel,
                                             String domainName,
                                             String projectName, HttpClient client) {
        GetMethod query = null;

        try {

            query = new GetMethod(almUrlPrefix
                    + "/qcbin/rest/domains/" + domainName + "/projects/"
                    + projectName + "/customization/entities/" + entity + "/fields");
            query.addRequestHeader("Accept", "application/xml");

            final int httpReturnCode = client.executeMethod(query);
            final String response = query.getResponseBodyAsString();

            final Document doc = docBuilder.parse(makeInputSource(query.getResponseBodyAsStream()));
            final XPath xpath = xpathFactory.newXPath();
            switch (httpReturnCode) {
                case HttpURLConnection.HTTP_OK:
                    final String attribName = (String) xpath
                            .compile(
                                    "/Fields/Field[@Label='" + attributeLabel + "']/@Name")
                            .evaluate(doc, XPathConstants.STRING);
                    return attribName;

                default:
                    RuntimeException nested = new RuntimeException("Got HTTP return code: "
                            + httpReturnCode + "; Response: " + response);
                    throw new BugTrackerException("Could not query attributes for entities from the ALM server", nested);
            }

        } catch (final IOException e) {
            throw new BugTrackerException(ALM_NOT_ACCESSIBLE, e);
        } catch (final RuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (query != null) {
                query.releaseConnection();
            }
        }
    }

    private void validateAlmDomainAndProject(String domainName,
                                             String projectName, HttpClient client) {

        GetMethod query = null;

        try {

            query = new GetMethod(almUrlPrefix
                    + "/qcbin/rest/domains/" + domainName + "/projects/"
                    + projectName + "/defects");
            query.addRequestHeader("Accept", "application/xml");
            query.setQueryString("page-size=1");

            final int httpReturnCode = client.executeMethod(query);
            final String response = query.getResponseBodyAsString();

            switch (httpReturnCode) {
                case HttpURLConnection.HTTP_OK:
                    return;
                case HttpURLConnection.HTTP_INTERNAL_ERROR:
                case HttpURLConnection.HTTP_NOT_FOUND:
                    final String message = MessageFormat
                            .format("The ALM domain {0} and project {1} combination is invalid. "
                                    + "Please verify your ALM installation and use the right values.",
                                    domainName, projectName);
                    throw new BugTrackerException(message);
                default:
                    RuntimeException nested = new RuntimeException("Got HTTP return code: "
                            + httpReturnCode + "; Response: " + response);
                    throw new BugTrackerException("Could not validate ALM domain and project", nested);

            }
        } catch (final IOException e) {
            throw new BugTrackerException(ALM_NOT_ACCESSIBLE, e);
        } catch (final RuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (query != null) {
                query.releaseConnection();
            }
        }

    }

    private List<String> getDomains(HttpClient client) {

        GetMethod query = null;

        try {

            query = new GetMethod(almUrlPrefix + "/qcbin/rest/domains");
            query.addRequestHeader("Accept", "application/xml");

            final int httpReturnCode = client.executeMethod(query);
            final String response = query.getResponseBodyAsString();

            final Document doc = docBuilder.parse(makeInputSource(query.getResponseBodyAsStream()));
            final XPath xpath = xpathFactory.newXPath();
            switch (httpReturnCode) {
                case HttpURLConnection.HTTP_OK:
                    final List<String> domains = new ArrayList<String>();
                    final NodeList result = (NodeList) xpath.compile("/Domains/Domain/@Name")
                            .evaluate(doc, XPathConstants.NODESET);
                    final NodeList nodes = result;
                    for (int i = 0; i < nodes.getLength(); i++) {
                        domains.add(nodes.item(i).getTextContent());
                    }
                    return domains;

                default:
                    RuntimeException nested = new RuntimeException("Got HTTP return code: "
                            + httpReturnCode + "; Response: " + response);
                    throw new BugTrackerException("Could not query domains from ALM", nested);
            }

        } catch (final IOException e) {
            throw new BugTrackerException(ALM_NOT_ACCESSIBLE, e);
        } catch (final RuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (query != null) {
                query.releaseConnection();
            }
        }
    }

    private List<String> getProjects(HttpClient client, String domain) {

        GetMethod query = null;

        try {

            query = new GetMethod(almUrlPrefix + "/qcbin/rest/domains/" + domain + "/projects");
            query.addRequestHeader("Accept", "application/xml");

            final int httpReturnCode = client.executeMethod(query);
            final String response = query.getResponseBodyAsString();

            final Document doc = docBuilder.parse(makeInputSource(query.getResponseBodyAsStream()));
            final XPath xpath = xpathFactory.newXPath();
            switch (httpReturnCode) {
                case HttpURLConnection.HTTP_OK:
                    final List<String> domains = new ArrayList<String>();
                    final NodeList result = (NodeList) xpath.compile("/Projects/Project/@Name")
                            .evaluate(doc, XPathConstants.NODESET);
                    final NodeList nodes = result;
                    for (int i = 0; i < nodes.getLength(); i++) {
                        domains.add(nodes.item(i).getTextContent());
                    }
                    return domains;

                default:
                    RuntimeException nested = new RuntimeException("Got HTTP return code: "
                            + httpReturnCode + "; Response: " + response);
                    throw new BugTrackerException("Could not query projects from ALM", nested);
            }

        } catch (final IOException e) {
            throw new BugTrackerException(ALM_NOT_ACCESSIBLE, e);
        } catch (final RuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (query != null) {
                query.releaseConnection();
            }
        }

    }


    private String sendAttachment(HttpClient client,
                                  String entityUrl,
                                  byte[] fileData,
                                  String contentType,
                                  String filename,
                                  String description) {

        PostMethod httppost = null;
        try {

            httppost = new PostMethod(entityUrl + "/attachments");

			/* Following code is adapted from the ALM REST API attachment example */

            //This can be pretty much any string - it's used to signify the different mime parts
            String boundary = "exampleboundary";
            //Template to use when sending field data (assuming non-binary data)
            String fieldTemplate =
                    "--%1$s\r\n"
                            + "Content-Disposition: form-data; name=\"%2$s\" \r\n\r\n"
                            + "%3$s"
                            + "\r\n";
            //Template to use when sending file data (binary data still needs to be suffixed)
            String fileDataPrefixTemplate =
                    "--%1$s\r\n"
                            + "Content-Disposition: form-data; name=\"%2$s\"; filename=\"%3$s\"\r\n"
                            + "Content-Type: %4$s\r\n\r\n";
            String filenameData = String.format(fieldTemplate, boundary, "filename", filename);
            String descriptionData = String.format(fieldTemplate, boundary, "description", description);
            String fileDataSuffix = "\r\n--" + boundary + "--";
            String fileDataPrefix =
                    String.format(fileDataPrefixTemplate, boundary, "file", filename, contentType);
            //The order is extremely important: The filename and description come before file data. The name of the file in the file part and in the filename part value MUST MATCH.
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bytes.write(filenameData.getBytes());
            bytes.write(descriptionData.getBytes());
            bytes.write(fileDataPrefix.getBytes());
            bytes.write(fileData);
            bytes.write(fileDataSuffix.getBytes());
            bytes.close();


            Part[] parts = { new StringPart("entity.attachment", bytes.toString()) };

            httppost.setRequestEntity(new MultipartRequestEntity( parts, httppost.getParams()));
            httppost.setRequestHeader("Content-Type", "multipart/form-data; boundary=" + boundary);

            final int httpReturnCode = client.executeMethod(httppost);
            final String response = httppost.getResponseBodyAsString();

            switch (httpReturnCode) {
                case HttpURLConnection.HTTP_CREATED:
                    final String attachmentLocation = httppost.getResponseHeader("Location").getValue();
                    return attachmentLocation;

                default:
                    RuntimeException nested = new RuntimeException("Got HTTP return code: "
                            + httpReturnCode + "; Response: " + response);
                    throw new BugTrackerException("Could not attach file to entity", nested);
            }
        } catch (final IOException e) {
            throw new BugTrackerException(ALM_NOT_ACCESSIBLE, e);
        } catch (final RuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (httppost != null) {
                httppost.releaseConnection();
            }
        }

    }

    public List<BugParam> getBatchBugParameters(UserAuthenticationStore credentials) {
        return getBugParameters(null, credentials);
    }

    public List<BugParam> onBatchBugParameterChange(String changedParamIdentifier, List<BugParam> currentValues, UserAuthenticationStore credentials) {
        return onParameterChange(null, changedParamIdentifier, currentValues, credentials);
    }

    private String constructMultiIssueDefectXmlString(MultiIssueBugSubmission bug,
                                                      String detectedInBuildInstanceId, List<String> candidateChangesets,
                                                      String detectedByUser, String categoryAttributeName,UserAuthenticationStore credentials,String domain, String project ) throws Exception {

        final Entity defect = new Entity();
        defect.setType(DEFECT_ENTITY_TYPE_NAME);
        final Entity.Fields fields = new Entity.Fields();
        defect.setFields(fields);

        final Entity.Fields.Field detectedBy = new Entity.Fields.Field();
        detectedBy.setName(DETECTED_BY_FIELD_NAME);
        detectedBy.getValue().add(detectedByUser);
        fields.getField().add(detectedBy);

        final Entity.Fields.Field creationTime = new Entity.Fields.Field();
        creationTime.setName(CREATION_TIME_FIELD_NAME);
        creationTime.getValue().add(
                new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        fields.getField().add(creationTime);
        
        String orig=new SimpleDateFormat("dd/MM/yyyy KK:mm:ss a").format(new Date()); 	    
	    String result = orig.substring(0,21) + "." + orig.substring(21,22)+".";	    
        final Entity.Fields.Field fechaalta = new Entity.Fields.Field();
        fechaalta.setName(FECHA_ALTA_FIELD_NAME);
        fechaalta.getValue().add(result.toLowerCase());       
        fields.getField().add(fechaalta);

        if (categoryAttributeName != null && categoryAttributeName.length() != 0) {
            final Entity.Fields.Field category = new Entity.Fields.Field();
            category.setName(categoryAttributeName);
            String analysisType = bug.getIssueDetails().get(0).getAnalysisType();
            category.getValue().add("Fortify - " + analysisType);
            boolean allSame = true;
            for (IssueDetail issueDetail : bug.getIssueDetails()) {
                if (!analysisType.equals(issueDetail.getAnalysisType())) {
                    allSame = false;
                    break;
                }
            }
            if (allSame) {
                fields.getField().add(category);
            }
        }

        final Entity.Fields.Field name = new Entity.Fields.Field();
        name.setName(NAME_FIELD_NAME);
        name.getValue().add(anyNotNull(bug.getParams().get(SUMMARY_PARAM_NAME), bug.getParams().get(NAME_PARAM_NAME)));
        fields.getField().add(name);

        if (detectedInBuildInstanceId != null && detectedInBuildInstanceId.length() != 0) {
            final Entity.Fields.Field detectedInBuild = new Entity.Fields.Field();
            detectedInBuild.setName(DETECTED_IN_BUILD_FIELD_NAME);
            detectedInBuild.getValue().add(detectedInBuildInstanceId);
            fields.getField().add(detectedInBuild);
        }


        if (candidateChangesets != null && candidateChangesets.size() == 1) {
            final Entity.Fields.Field causedByChangeset = new Entity.Fields.Field();
            causedByChangeset.setName(CAUSED_BY_CHANGESET_FIELD_NAME);
            causedByChangeset.getValue().add(candidateChangesets.get(0));
            fields.getField().add(causedByChangeset);
        }

        final Entity.Fields.Field description = new Entity.Fields.Field();
        description.setName(DESCRIPTION_FIELD_NAME);
        description.getValue().add(convertToHtml(massageBugDescription(bug.getParams().get(DESCRIPTION_PARAM_NAME), candidateChangesets)));
        fields.getField().add(description);

        if (bug.getIssueDetails().size() == 1) {
            // Would it make sense to just include all comments from all issues?
            final Entity.Fields.Field comments = new Entity.Fields.Field();
            comments.setName(DEV_COMMENTS_FIELD_NAME);
            final StringBuilder allComments = new StringBuilder();
            for (final IssueComment c : bug.getIssueDetails().get(0).getComments()) {
                allComments.append("[").append(c.getUsername())
                        .append(" on " + c.getTimestamp() + "]: " + c.getBody());
                allComments.append("\n\n");
            }
            comments.getValue().add(convertToHtml(allComments.toString()));
            fields.getField().add(comments);
        }

        final String severityValue = bug.getParams().get(SEVERITY_PARAM_NAME);
        final Entity.Fields.Field severity = new Entity.Fields.Field();
        severity.setName(SEVERITY_FIELD_NAME);
        severity.getValue().add(severityValue);
        fields.getField().add(severity);
        
        final String aplicativovalue = bug.getParams().get(APLICATIVO_FIELD_NAME);
        final Entity.Fields.Field aplicativo = new Entity.Fields.Field();
        aplicativo.setName(APLICATIVO_FIELD_NAME);
        aplicativo.getValue().add(aplicativovalue);
        fields.getField().add(aplicativo);
              
        final Entity.Fields.Field tipo = new Entity.Fields.Field();
        tipo.setName(TIPO_DEFECTO_FIELD_NAME);
        tipo.getValue().add("No Funcional");
        fields.getField().add(tipo);
        
	    final Entity.Fields.Field categoria = new Entity.Fields.Field();
        categoria.setName(CATEGORIA_DEFECTO_FIELD_NAME);       
        categoria.getValue().add("Ejecucion de Pruebas");
        fields.getField().add(categoria);
       
        final Entity.Fields.Field lider_pruebas = new Entity.Fields.Field();
        lider_pruebas.setName(LIDER_PRUEBAS_FIELD_NAME);
        lider_pruebas.getValue().add(detectedByUser);
        fields.getField().add(lider_pruebas);
        
        final Entity.Fields.Field prioridad = new Entity.Fields.Field();
        prioridad.setName(PRIORIDAD_FIELD_NAME);
        prioridad.getValue().add("2-Alta");
        fields.getField().add(prioridad);
        
        final Entity.Fields.Field etapa = new Entity.Fields.Field();
        etapa.setName(ETAPA_DE_PRUEBAS_FIELD_NAME);
        etapa.getValue().add("Integrales");
        fields.getField().add(etapa);
        
        final Entity.Fields.Field estado = new Entity.Fields.Field();
        estado.setName(STATUS_FIELD_NAME);
        estado.getValue().add("Nuevo");
        fields.getField().add(estado);
        
        final String fabricavalue = bug.getParams().get(FABRICA_FIELD_NAME);
        final Entity.Fields.Field fabrica = new Entity.Fields.Field();
        fabrica.setName(FABRICA_FIELD_NAME);
        fabrica.getValue().add(fabricavalue);
        fields.getField().add(fabrica);
        
        final Entity.Fields.Field owner = new Entity.Fields.Field();
        owner.setName(ASIGNADO_FIELD_NAME);
        owner.getValue().add(detectedByUser);
        fields.getField().add(owner);
        
        final String detected_in_rcycValue = bug.getParams().get(DETECTADO_EN_RCYC_FIELD_NAME);
        
        String valoresdeCRF [] = getCyclesValues(detected_in_rcycValue,credentials,domain,project).split("#");
        
        final Entity.Fields.Field detected_in_rcyc = new Entity.Fields.Field();
        detected_in_rcyc.setName(DETECTADO_EN_RCYC_FIELD_NAME);
        detected_in_rcyc.getValue().add(valoresdeCRF[0]);
        fields.getField().add(detected_in_rcyc);
        
        final Entity.Fields.Field detected_in_rel = new Entity.Fields.Field();
        detected_in_rel.setName(DETECTADO_EN_REL_FIELD_NAME);
        detected_in_rel.getValue().add(valoresdeCRF[1]);
        fields.getField().add(detected_in_rel);
        
        final Entity.Fields.Field folio = new Entity.Fields.Field();
        folio.setName(FOLIO_FIELD_NAME);
        folio.getValue().add(valoresdeCRF[2]);
        fields.getField().add(folio);

        final String defectXmlString = EntityMarshallingUtils.unmarshal(Entity.class,
                defect);
        return defectXmlString;
    }

    private String anyNotNull(String... strings) {
        for (String str : strings) {
            if (str != null) {
                return str;
            }
        }
        return null;
    }

    public Bug fileMultiIssueBug(MultiIssueBugSubmission bug, UserAuthenticationStore credentials) {
        final HttpClient client = new HttpClient();
        PostMethod createDefect = null;
        try {
            authenticateAndCreateSession(client, credentials.getUserName(),
                    credentials.getPassword());


            final String domainName = bug.getParams().get(DOMAIN_PARAM_NAME);
            final String projectName = bug.getParams().get(PROJECT_PARAM_NAME);

            validateAlmDomainAndProject(domainName, projectName, client);

            String detectedInBuildInstance = null;
            if (bug.getIssueDetails().size() == 1) {
                IssueDetail issueDetail = bug.getIssueDetails().get(0);
                if (issueDetail.getDetectedInBuild() != null) {
                    try {
                        detectedInBuildInstance = getBuildInstanceIdFromRevision(issueDetail.getDetectedInBuild(),
                                client,domainName, projectName);
                    } catch (Exception e) {
                        LOG.warn("Skipping identification of build instance where issue was detected.", e);
                    }
                }
            }
            List<String> candidateChangesets = null;
            if (bug.getIssueDetails().size() > 0) {
                final IssueDetail issueDetail = bug.getIssueDetails().get(0);
                final Set<String> lastBuildWithoutIssueVals = collectLastBuildWithoutIssue(bug);
                final Set<String> detectedInBuildVals = collectDetectedInBuild(bug);
                if (lastBuildWithoutIssueVals.size() > 0 &&	detectedInBuildVals.size() > 0) {
                    try {
                        candidateChangesets = queryChangesetsBetween(
                                lastBuildWithoutIssueVals,
                                detectedInBuildVals,
                                issueDetail.getFileName(), // it's OK to use the file name from the first issue detail since MultiIssueBugSubmission created for one specific file.
                                bug.getParams(), credentials);
                    } catch (Exception e) {
                        LOG.warn("Skipping changeset discovery", e);
                    }

                }
            }

            createDefect = new PostMethod(almUrlPrefix
                    + "/qcbin/rest/domains/" + domainName + "/projects/"
                    + projectName + "/defects");
            createDefect.addRequestHeader("Accept", "application/xml");

            final String defectXmlString = constructMultiIssueDefectXmlString(bug,
                    detectedInBuildInstance,
                    candidateChangesets,
                    credentials.getUserName(),
                    getAttributeNameForEntity(DEFECT_ENTITY_TYPE_NAME,
                            CATEGORY_LABEL_NAME, domainName, projectName, client), credentials,domainName,projectName);

            createDefect.setRequestEntity(new StringRequestEntity(
                    defectXmlString, "application/xml", "UTF-8"));
            createDefect.setDoAuthentication(false);

            final int httpReturnCode = client.executeMethod(createDefect);
            final String response = createDefect.getResponseBodyAsString();
            destroySession(client);

            final Document doc = docBuilder.parse(makeInputSource(createDefect.getResponseBodyAsStream()));
            final XPath xpath = xpathFactory.newXPath();

            switch (httpReturnCode) {
                case HttpURLConnection.HTTP_CREATED:
                    final String bugNumber = (String) xpath.compile(
                            "/Entity/Fields/Field[@Name='id']/Value/text()")
                            .evaluate(doc, XPathConstants.STRING);
                    final String bugId = domainName + ":" + projectName + ":" + bugNumber;

                    final String bugStatus = (String) xpath.compile(
                            "/Entities/Entity/Fields/Field[@Name='status']/Value/text()")
                            .evaluate(doc, XPathConstants.STRING);

                    try {
                        String defectUrl = createDefect.getResponseHeader("Location").getValue();
                        for (IssueDetail issueDetail : bug.getIssueDetails()) {
                            String shortcutFileData = "[InternetShortcut]\r\nURL=" + issueDetail.getIssueDeepLink();
                            sendAttachment(client, defectUrl, shortcutFileData.getBytes(), "text/plain", "issueDeepLink.URL", "Deep Link to Issue " + issueDetail.getIssueInstanceId() + " in SSC");
                        }
                    } catch (Exception e) {
                        LOG.warn("Could not upload URL attachment file to defect.", e);
                    }
                    return new Bug(bugId, bugStatus);

                default:
                    RuntimeException nested = new RuntimeException("Got HTTP return code: "
                            + httpReturnCode + "; Response: " + response);
                    throw new BugTrackerException("Could not create a bug on the ALM server.", nested);

            }


        } catch (final IOException e) {
            throw new BugTrackerException(ALM_NOT_ACCESSIBLE, e);
        } catch (final RuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (createDefect != null) {
                createDefect.releaseConnection();
            }
        }
    }

    private Set<String> collectLastBuildWithoutIssue(MultiIssueBugSubmission bug) {
        final Set<String> result = new LinkedHashSet<String>();
        for (final IssueDetail issueDetail : bug.getIssueDetails()) {
            if (issueDetail.getLastBuildWithoutIssue() != null) {
                result.add(issueDetail.getLastBuildWithoutIssue());
            }
        }
        return result;
    }

    private Set<String> collectDetectedInBuild(MultiIssueBugSubmission bug) {
        final Set<String> result = new LinkedHashSet<String>();
        for (final IssueDetail issueDetail : bug.getIssueDetails()) {
            if (issueDetail.getDetectedInBuild() != null) {
                result.add(issueDetail.getDetectedInBuild());
            }
        }
        return result;
    }

    public boolean isBugOpen(Bug bug, UserAuthenticationStore credentials) {
        return "".equals(bug.getBugStatus()) || STATUS_NEW.equals(bug.getBugStatus()) || STATUS_OPEN.equals(bug.getBugStatus()) || STATUS_REOPEN.equals(bug.getBugStatus());
    }
    public boolean isBugClosed(Bug bug, UserAuthenticationStore credentials) {
        return !isBugOpen(bug, credentials);
    }
    public boolean isBugClosedAndCanReOpen(Bug bug, UserAuthenticationStore credentials) {
        return STATUS_CLOSED.equals(bug.getBugStatus()) || STATUS_FIXED.equals(bug.getBugStatus());
    }

    private Entity.Fields.Field createNewCommentField(Bug bug, String addComment, UserAuthenticationStore credentials) {
        final Entity.Fields.Field comments = new Entity.Fields.Field();
        comments.setName(DEV_COMMENTS_FIELD_NAME);
        String currentComments = fetchBugComments(bug.getBugId(), credentials);
        if (currentComments != null && currentComments.trim().length() > 0) {
            comments.getValue().add(currentComments);
        }
        String newComment;
        newComment = convertCommentToHtml(addComment, credentials.getUserName());
        comments.getValue().add(newComment);
        return comments;
    }
    public void reOpenBug(Bug bug, String comment, UserAuthenticationStore credentials) {
        if (STATUS_REJECTED.equals(bug.getBugStatus())) {
            throw new BugTrackerException("Bug " + bug.getBugId() + " cannot be reopened.");
        }

        final Entity defect = new Entity();
        defect.setType(DEFECT_ENTITY_TYPE_NAME);
        final Entity.Fields fields = new Entity.Fields();
        defect.setFields(fields);

        final Entity.Fields.Field status = new Entity.Fields.Field();
        status.setName(STATUS_FIELD_NAME);
        status.getValue().add(STATUS_REOPEN);
        fields.getField().add(status);

        fields.getField().add(createNewCommentField(bug, comment, credentials));

        updateBug(bug, defect, credentials);
    }

    public void addCommentToBug(Bug bug, String comment, UserAuthenticationStore credentials) {
        final Entity defect = new Entity();
        defect.setType(DEFECT_ENTITY_TYPE_NAME);
        final Entity.Fields fields = new Entity.Fields();
        defect.setFields(fields);

        fields.getField().add(createNewCommentField(bug, comment, credentials));

        updateBug(bug, defect, credentials);
    }

    private void updateBug(Bug bug, Entity defect, UserAuthenticationStore credentials) {

        final HttpClient client = new HttpClient();
        PutMethod updateDefect = null;
        try {
            authenticateAndCreateSession(client, credentials.getUserName(),
                    credentials.getPassword());

            String[] splits = bug.getBugId().split(":");
            if (splits.length != 3) {
                throw new BugTrackerException("External bug id does not contain the 3 expected elements.");
            }

            final String domainName = splits[0];
            final String projectName = splits[1];
            final String defectId = splits[2];

            validateAlmDomainAndProject(domainName, projectName, client);
            updateDefect = new PutMethod(almUrlPrefix
                    + "/qcbin/rest/domains/" + domainName + "/projects/"
                    + projectName + "/defects" + "/" + defectId);
            updateDefect.addRequestHeader("Accept", "application/xml");

            updateDefect.setRequestEntity(new StringRequestEntity(EntityMarshallingUtils.unmarshal(Entity.class,defect),  "application/xml", "UTF-8"));
            updateDefect.setDoAuthentication(false);

            final int httpReturnCode = client.executeMethod(updateDefect);
            final String response = updateDefect.getResponseBodyAsString();
            destroySession(client);

            switch (httpReturnCode) {
                case HttpURLConnection.HTTP_OK:
                    return;
                default:
                    RuntimeException nested = new RuntimeException("Got HTTP return code: " + httpReturnCode + "; Response: " + response);
                    throw new BugTrackerException("Could not update a bug on the ALM server.", nested);
            }
        } catch (final IOException e) {
            throw new BugTrackerException(ALM_NOT_ACCESSIBLE, e);
        } catch (final RuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (updateDefect != null) {
                updateDefect.releaseConnection();
            }
        }
    }

    private InputSource makeInputSource(final InputStream inputStream) throws UnsupportedEncodingException {
        Reader reader = new InputStreamReader(inputStream,"UTF-8");
        InputSource result = new InputSource(reader);
        result.setEncoding("UTF-8");
        return result;
    }
}

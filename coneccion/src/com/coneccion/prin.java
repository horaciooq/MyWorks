package com.coneccion;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
 


















import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthPolicy;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.jsslutils.extra.apachehttpclient.SslContextedSecureProtocolSocketFactory;
import org.jsslutils.sslcontext.X509SSLContextFactory;

import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
public class prin {

	public static void main(String[] args) throws  Exception  {
		Properties systemProps = System.getProperties();
		systemProps.put(
		    "javax.net.ssl.trustStore",
		    "C:/Program Files/Java/jdk1.7.0_25/jre/lib/security/cacerts"
		);
		System.setProperties(systemProps);
		 HttpClient httpclient = new HttpClient();
		  GetMethod httpget = new GetMethod("https://hpalmapp01:8443/qcbin/start_a.jsp"); 
		  try { 
		    httpclient.executeMethod(httpget);
		    System.out.println(httpget.getStatusLine());
		  } finally {
		    httpget.releaseConnection();
		  }
		/*Properties systemProps = System.getProperties();
		 systemProps.put(
		    "javax.net.ssl.trustStore",
		    "C:/Program Files/Java/jdk1.7.0_25/jre/lib/security/cacerts"
		);
		System.setProperties(systemProps);
		  HttpClient client = new HttpClient();
		 int httpReturnCode=0;
		 client.getParams().setParameter(AuthPolicy.AUTH_SCHEME_PRIORITY, new ArrayList<String>(Collections.singletonList(AuthPolicy.BASIC)));
		client.getState().setCredentials(new AuthScope("hpalmapp01", 8443), new UsernamePasswordCredentials("userfortify", "banorte234"));
		  GetMethod httpget = new GetMethod("https://hpalmapp01:8443/qcbin/authentication-point/authenticate"); 
		  try { 
			  httpget.addRequestHeader("Accept", "application/xml");
			  httpget.setDoAuthentication(true);					
			  httpReturnCode = client.executeMethod(httpget);
			  httpget.getResponseBodyAsString();
		    client.executeMethod(httpget);
		    System.out.println(httpget.getStatusLine());
		  } finally {
		    httpget.releaseConnection();
		  }*/
	}
}

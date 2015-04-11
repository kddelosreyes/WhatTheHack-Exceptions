package com.feutech.whatthehack.api;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpVersion;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;

import android.util.Log;

public final class VolleySSLSocketFactory {
	
	public static HttpClient sslHttpClient(){
		try {
			// Create and initialize HTTP parameters
			HttpParams params = new BasicHttpParams();
			HttpClientParams.setRedirecting(params, true );
	
			// Set the timeout in milliseconds until a connection is established.
			HttpConnectionParams.setConnectionTimeout( params, 5000 );
	
			// Set the default socket timeout (SO_TIMEOUT)
			// in milliseconds which is the timeout for waiting for data.
			HttpConnectionParams.setSoTimeout( params, 10000 );
	
			// The params are read in the ctor of the pool constructed by
			// ThreadSafeClientConnManager, and need to be set before constructing it.
			ConnManagerParams.setMaxTotalConnections(params, 15);
			ConnPerRoute cpr = new ConnPerRoute() {
			   @Override
			   public int getMaxForRoute(HttpRoute httpRoute) { return 5; }
			};
	
			ConnManagerParams.setMaxConnectionsPerRoute(params, cpr);
	
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	
			// Create and initialize scheme registry
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register( new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	
			/* Since I'm in a development environment I need to trust self-signed certs */
			SSLSocketFactory sslSocketFactory = null;
			try {
			   X509TrustManager tm = new X509TrustManager() {
			      public void checkClientTrusted(X509Certificate[] xcs, String string)
			         throws CertificateException { }
	
			      public void checkServerTrusted(X509Certificate[] xcs, String string)
			         throws CertificateException { }
	
			      public X509Certificate[] getAcceptedIssuers() { return null; }
	
				
			   };
	
			   SSLContext ctx = SSLContext.getInstance("TLS");
			   ctx.init(null, new TrustManager[]{tm}, null);
	
			   sslSocketFactory = new MySSLSocketFactory(ctx);
			   if (sslSocketFactory != null)
			      sslSocketFactory.setHostnameVerifier(
			          SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	
			} catch (Exception ex) {
			   Log.e("TAG", ex.getMessage(), ex);
			   sslSocketFactory = null;
			}
	
			if (sslSocketFactory == null) {
			   sslSocketFactory = SSLSocketFactory.getSocketFactory();
			   sslSocketFactory.setHostnameVerifier(
			      SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
			}
	
			schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));
	
			// Create an HttpClient with the ThreadSafeClientConnManager.
			// This connection manager must be used if more than one thread will
			// be using the HttpClient.
			ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
	
			DefaultHttpClient client = new DefaultHttpClient(cm, params);
	
			HttpProtocolParams.setUseExpectContinue(client.getParams(), false);
	
			HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler() {
				@Override
				public boolean retryRequest(IOException exception,
						int executionCount, HttpContext context) {
					 if(executionCount >= 5) { return false; }
				      if(exception instanceof NoHttpResponseException){
				         return true;
				      } else if (exception instanceof ClientProtocolException){
				         return true;
				      }
				      return false;
				}
			};
	
			client.setHttpRequestRetryHandler(retryHandler);
	
			/* Cookie Management */
			BasicCookieStore cookieStore = new BasicCookieStore();
			client.setCookieStore(cookieStore);
			return client;
		} catch (Exception ex) {
			return null;
		}
	}
	
	public static HttpsURLConnection sslHttpClient(HttpsURLConnection httpClient){
		try {
			//HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
			SSLContext context = SSLContext.getInstance("SSL");
			
			X509TrustManager tm = new X509TrustManager() {

				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(
						java.security.cert.X509Certificate[] arg0, String arg1)
						throws java.security.cert.CertificateException {

				}

				@Override
				public void checkServerTrusted(
						java.security.cert.X509Certificate[] arg0, String arg1)
						throws java.security.cert.CertificateException {

				}
			};
			
			context.init(null, new TrustManager[] { tm }, new SecureRandom());
//
//			SSLContext ctx = SSLContext.getInstance("SSL");
//			ctx.init(null, new TrustManager[] { tm }, null);
//			javax.net.ssl.SSLSocketFactory ssf = ctx.getSocketFactory();
			//ssf.createSocket("http", 80);
			//ssf.createSocket("https", 443);
			//ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			//ClientConnectionManager ccm = client.getConnectionManager();
//			httpClient.getC
//			SchemeRegistry sr = ccm.getSchemeRegistry();
//			sr.register(new Scheme("http", PlainSocketFactory
//					.getSocketFactory(), 80));
//			sr.register(new Scheme("https", ssf, 443));

			httpClient.setSSLSocketFactory(context.getSocketFactory());
			httpClient.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			
			return httpClient;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public class NullHostNameVerifier implements HostnameVerifier {

	    public boolean verify(String hostname, SSLSession session) {
	        Log.i("RestUtilImpl", "Approving certificate for " + hostname);
	        return true;
	    }
	}
}

package com.chaoxing.document;

import java.io.File;
import java.io.IOException;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import roboguice.inject.SharedPreferencesName;

import android.os.Environment;

import com.chaoxing.httpservice.HttpAsyncClientProvider;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * The constant single object weave
 * 
 * @author <a href="mailto:hb562100@163.com">HeBo</a>
 * @version 2011-5-20
 */
public class ConstantModule extends AbstractModule {

	public static boolean isLibrary  = false; 
	
	public final static File homeFolder = new File(Environment.getExternalStorageDirectory(),
	"ssvideo");
	
	public final static String USER_AGENT = "SSREADER/3.9.4.2010_ANDROID_2.2("+android.os.Build.MODEL + ","
							+ android.os.Build.VERSION.SDK + ","
							+ android.os.Build.VERSION.RELEASE+")";
	
	public final static String SS_VER = "android2.2_SSREADER/4.0.0.0001";
	private HttpClient httpClient ;
	@Override
	protected void configure() {
		if(!homeFolder.exists()){
			homeFolder.mkdirs();
		}				
		
		File nomedia = new File(homeFolder,".nomedia");
		if(!nomedia.exists()){
			try {
				nomedia.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		/*Store video folder*/
		bind(File.class).annotatedWith(Names.named("homeFolder")).toInstance(
				homeFolder);
		bindConstant().annotatedWith(SharedPreferencesName.class).to("ssvideo");


		/*bind HttpAsyncClient*/
		bind(HttpAsyncClientProvider.class);
//		bind(HttpAsyncClient.class).toProvider(httpAsyncClientProvider);
		HttpParams params = new BasicHttpParams();
		ConnManagerParams.setMaxTotalConnections(params, 64);
		ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(32));					
		
		HttpClientParams.setCookiePolicy(params, CookiePolicy.BROWSER_COMPATIBILITY);
		
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setUserAgent(params,ConstantModule.USER_AGENT); 
		
//		HttpConnectionParams.setStaleCheckingEnabled(params, false);
		HttpConnectionParams.setSocketBufferSize(params, 8*1024);
		HttpConnectionParams.setConnectionTimeout(params, 8*1000);
		HttpConnectionParams.setLinger(params, 8*1000);
//		HttpConnectionParams.setTcpNoDelay(params, true);
		HttpConnectionParams.setSoTimeout(params, 12*1000);

		// Create and initialize scheme registry
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

		// Create an HttpClient with the ThreadSafeClientConnManager.
		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(params,
				schemeRegistry);
		/*bind HttpClient*/
		httpClient = new DefaultHttpClient(cm,params);
		
		{
			bind(HttpClient.class).toInstance(httpClient);
		}
	}


}

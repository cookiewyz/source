/**
 * 功能描述：HTTP协议访问服务器
 * Author xin_yueguang
 */

package com.chaoxing.httpservice;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class HttpRequest {
	private static final String TAG = "HttpRequest";
	
	private static final int REQUEST_TIMEOUT = 20*1000;//设置请求超时时间
	private static final int SO_TIMEOUT = 20*1000;  //设置等待数据超时时间

	private int iResponse;
	private String resStr;
	private String resUrl;
	private Bitmap bitmap = null;
	
	
	public HttpRequest(){
		super();
		iResponse = -1;
	}

	public String getStringData()
	{
		return resStr;
	}
	
	public void setRequestUrl(String url)
	{
		this.resUrl = url;
	}
	
	public int getResponse()
	{
		return iResponse;
	}
	
	public Bitmap getBitmapData()
	{
		return bitmap;
	}
	/*private void downloadJson(){
			Thread thread = new Thread(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					//super.run();
					
				}
			};
			thread.start();
			thread = null;
		}*/
	
	public void httpGetFile()
	{
		try {
	            URL url = new URL(resUrl);
	            URLConnection con = url.openConnection();
	            
	            iResponse = ((HttpURLConnection)con).getResponseCode();
	            if(iResponse != 200){
	            	return;
	            }
	            InputStream is = con.getInputStream();
	            BufferedInputStream bis = new BufferedInputStream(is);
	            ByteArrayBuffer bab = new ByteArrayBuffer(32);
	            int current = 0;
	            while ((current = bis.read()) != -1 ){
	                bab.append((byte)current);
	            }
				
	            resStr = EncodingUtils.getString(bab.toByteArray(), HTTP.UTF_8);
	            bis.close();
	            is.close();
		} catch (Exception e) {
	        	e.printStackTrace();
	    }
	    
	    iResponse = 0;
	}
	
	/** 
    * 添加请求超时时间和等待时间 
    * @author yueguang.xin 
    * @return HttpClient对象 
    */  
	public HttpClient getHttpClient(){  
	    BasicHttpParams httpParams = new BasicHttpParams();  
	    HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);  
	    HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);  
	    HttpClient client = new DefaultHttpClient(httpParams);  
	    return client;  
	}  

	public void getRequestFile()
	{
//		DefaultHttpClient httpClient = new DefaultHttpClient(); //取得取得默认的HttpClient实例  
		HttpClient httpClient = getHttpClient();
		HttpGet request = new HttpGet(resUrl); //创建HttpGet实例    
		try {
		    HttpResponse response = httpClient.execute(request); //连接服务器
		    StatusLine sl = response.getStatusLine();
		    iResponse = sl.getStatusCode();
			if(sl.getStatusCode() != HttpStatus.SC_OK){
				return;
			}
		    /*/读取所有头数据
		    Header[] header = response.getAllHeaders();
		    HashMap<String, String> hm = new HashMap<String, String>();
		    for (int i = 0; i < header.length; i++)
		    {
		    	hm.put(header[i].getName(), header[i].getValue());
		    }*/
		    
		    HttpEntity entity = response.getEntity(); //取得数据记录
		    InputStream is = entity.getContent(); //取得数据记录内容
		    BufferedReader bufreader = new BufferedReader(new InputStreamReader(is)); //显示数据记录内容
		    String str = "";
		    StringBuffer sbuff = new StringBuffer("");
		    while((str = bufreader.readLine()) != null){   
		    	sbuff.append(str);
		    }
		    resStr = sbuff.toString();
		    //filenameText.setText(sbuff.toString());
		    /*
		    int length = (int)entity.getContentLength();
			InputStream inputStream =  entity.getContent();
			byte[] resByte = new byte[length];			
			int l = 0,k=0;
			while ((k=inputStream.read(resByte,l,length-l)) !=-1){
				l+=k;
			}*/
			//OutputStream os = new FileOutputStream(catFile);
			//os.write(resByte);
			//entity.consumeContent();
		    
		    httpClient.getConnectionManager().shutdown(); //释放连接
		}
		catch (ClientProtocolException e) 
		{
			e.printStackTrace();
		    //Toast.makeText(getBaseContext(),"ClientProtocolException", Toast.LENGTH_SHORT).show();
		    Log.d(TAG, e.toString());
		    iResponse = 1;
		} 
		catch (IOException ei)
		{
			ei.printStackTrace();
			//Toast.makeText(getBaseContext(),ei.getMessage(), Toast.LENGTH_SHORT).show();
			Log.d(TAG, ei.toString());
			iResponse = 1;
		}

	}// end getRequestFile;
	
	public void getImageRequest() { 
    	URL myFileUrl = null; 
    	 
    	try { 
    		myFileUrl = new URL(resUrl); 
    	} catch (MalformedURLException e) { 
    	e.printStackTrace(); 
    	} 
    	try { 
    		HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection(); 
    		conn.setDoInput(true); 
    		conn.setConnectTimeout(500000);
    		conn.setReadTimeout(10000);
    		conn.connect(); 
    		
    		iResponse = ((HttpURLConnection)conn).getResponseCode();
            if(iResponse != 200){
            	return;
            }
            
    		InputStream is = conn.getInputStream(); 
    		bitmap = BitmapFactory.decodeStream(is); 
    		is.close(); 
    	} catch (IOException e) { 
    		e.printStackTrace();
    		iResponse = 1;
    	} 
 
    } 
	
	public Bitmap getImageUrlRequest(String surl) { 
    	URL myFileUrl = null; 
    	 
    	try { 
    		myFileUrl = new URL(surl); 
    	} catch (MalformedURLException e) { 
    	e.printStackTrace(); 
    	} 
    	Bitmap bmp = null;
    	try { 
    		HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection(); 
//    		conn.setDoInput(true); 
    		conn.setConnectTimeout(500000);
//    		conn.setReadTimeout(20000);
//    		conn.connect(); 
    		
    		iResponse = ((HttpURLConnection)conn).getResponseCode();
            if(iResponse != 200){
            	return null;
            }
            
    		InputStream is = conn.getInputStream(); 
    		bmp = BitmapFactory.decodeStream(is); 
    		is.close(); 
    	} catch (IOException e) { 
    		e.printStackTrace();
    		iResponse = 1;
    	} 
    	return bmp;
    } 
    	
	public List<Bitmap> downLoadImage(PhotoDownloadListener listener) {
		List<String> urls = Arrays.asList(resUrl);
		List<Bitmap> photos = new ArrayList<Bitmap>();
		URL aryURI = null;
		URLConnection conn = null;
		InputStream is = null;
		Bitmap bm = null;
		
		for (String url : urls) {
			try {
				aryURI = new URL(url);
				conn = aryURI.openConnection();
				is = conn.getInputStream();
				bm = BitmapFactory.decodeStream(is);
				Bitmap bmp = bm;
				// update listener
				listener.onPhotoDownloadListener(bmp);
				photos.add(bmp);
//				Log.i("downlaod no: ", "");
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				try {
					if (is != null)
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return photos;
	}
	
	public interface PhotoDownloadListener {
		public void onPhotoDownloadListener(Bitmap photo);
	}
	
}

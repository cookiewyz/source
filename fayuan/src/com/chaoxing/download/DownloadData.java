package com.chaoxing.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Future;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import com.chaoxing.httpservice.HttpAsyncClient;


/*
 * 下载图书目次文件
 */
public class DownloadData {
	private NameValuePair [] headers;
	
	public int download(HttpAsyncClient client,String url,File saveFile)
    {
		HttpUriRequest request;	
		request = handleRequest(new HttpGet(url),headers);
		client.execute(request, new DealDownloadRes(saveFile));
		return 0;
    }
	
	//目次文件下载回调函数
		class DealDownloadRes implements HttpAsyncClient.CompletedCallback{
			private File saveFile;
			
			DealDownloadRes(File saveFile){
				this.saveFile = saveFile;
			}
			
			@Override
			public void callback(HttpUriRequest request, HttpResponse response,
					Object attachment, IOException e) throws IOException {
				if(e!=null){				
					return ;
				}
				
				StatusLine sl = response.getStatusLine();
				if(sl.getStatusCode()!=HttpStatus.SC_OK){
					if(saveFile.exists())
					{
						saveFile.delete();
					}
					return ;
				}
							
				HttpEntity entity= response.getEntity();
				
				int length = (int)entity.getContentLength();			
				if(length<0){
					return ;
				}
				InputStream inputStream =  entity.getContent();
				byte[] resByte = new byte[length];			
				int l = 0,k=0;
				while ((k=inputStream.read(resByte,l,length-l)) !=-1){
					l+=k;
				}
				OutputStream os = new FileOutputStream(saveFile);
				os.write(resByte);
				entity.consumeContent();
			}
		}
		
		public InputStream download(HttpAsyncClient client,String url)
	    {
			HttpUriRequest request;	
			InputStream inputStream = null;
			request = handleRequest(new HttpGet(url),headers);
			Future<HttpResponse> future = client.execute(request);//同步
			try {
				HttpResponse response = future.get();
				HttpEntity entity= response.getEntity();
				
				int length = (int)entity.getContentLength();			
				if(length<0){
					return null;
				}
				inputStream =  entity.getContent();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return inputStream;
	    }
		
		public InputStream download(String url)
	    {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpUriRequest request;	
			InputStream inputStream = null;
			request = handleRequest(new HttpGet(url),headers);
			try {
				HttpResponse response = client.execute(request);
				HttpEntity entity= response.getEntity();
				int statusCode = response.getStatusLine().getStatusCode();
				if(statusCode == 200){
					int length = (int)entity.getContentLength();			
					if(length<0){
						return null;
					}
					inputStream =  entity.getContent();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return inputStream;
	    }
		
		protected HttpUriRequest handleRequest(HttpUriRequest request,NameValuePair [] headers){
			if(headers!=null)
				for(NameValuePair header:headers){
					request.setHeader(header.getName(), header.getValue());
				}
			return request;
		}
	
//	/*
//	 * 生成下载目次文件URL
//	 */
//	public static String getCatUrl(String ssId){
//		//http://img.sslibrary.com/cat/cat2xml.dll?kid=66676791656A6E9169653236373231303135&a=2f87b01817d52f549a5d9644af1809f2
//		String url = "";
//		String domain = "http://img.sslibrary.com/cat/cat2xml.dll?";
//		Security sec = new Security(); 
//		String catDir = ssId.substring(0, 3) + "\\" + ssId.substring(3, 6) + "\\" + ssId.substring(6, 8);
//		String kid = sec.EncodeSSId(catDir);
//		String auth = sec.MakeAuthCodeV1(ssId+"f0^u94*#cD1k");
//		url = domain + "kid=" + kid + "&a=" + auth;
//		return url;
//	}
}

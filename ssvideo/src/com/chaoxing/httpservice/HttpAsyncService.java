package com.chaoxing.httpservice;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;

import com.chaoxing.httpservice.HttpAsyncClient.CompletedCallback;
import com.google.inject.Inject;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import roboguice.service.RoboService;

/**
 * @author <a href="mailto:hb562100@163.com">HeBo</a>
 * @version 2011-6-1
 */
public class HttpAsyncService extends RoboService  {
	private static final String TAG =  HttpAsyncService.class.getSimpleName();

	public final static String ACTION = HttpAsyncService.class.getName();
	
	@Inject
	private ExecutorService executor;
	
	@Inject
	private HttpClient httpClient;

//	private volatile boolean mRUN;

	private Processor processor;

	private IBinder binder = new HttpAsyncClientBinder();
	
	@Override
	public void onCreate() {
		super.onCreate();
	//	mRUN = true;
		processor = new Processor();
	//	processor.start();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onDestroy() {
	//	mRUN = false;

//		synchronized (processor) {
//			processor.notify();
//		}		
		super.onDestroy();
		
//		Log.i(TAG,"HttpAsyncService is destory.");
	}
	

	public class HttpAsyncClientBinder extends Binder {
		public HttpAsyncClient getHttpAsyncClient(){
			return processor;
		}
	}

	private class Processor implements HttpAsyncClient{

		//private LinkedList<HttpRequestDD> jobs = new LinkedList<HttpRequestDD>();
		
		@Override
		public Future<HttpResponse> execute(HttpUriRequest request) {
			return execute(request,(HttpContext)null);
		}		
		
		@Override
		public Future<HttpResponse> execute(
				final HttpUriRequest request,
				final HttpContext context) {
			return executor.submit(
					new Callable<HttpResponse>() {
						public HttpResponse call() throws Exception {
							if(context==null)
								return httpClient.execute(request);
							else{
								return httpClient.execute(request ,context);
							}
						};
					}
			);
		}
		
		
		@Override
		public boolean isReady() {
			return httpClient!=null;
		}

		
		@Override
		public void execute(HttpUriRequest request, HttpContext context,
				Object attachment, CompletedCallback callback) {
//			synchronized (this) {
//				jobs.add(new HttpRequestDD(request,context ,attachment,callback));
//				processor.notify();				
//				executor.execute(new HttpRequestDD(request,context ,attachment,callback));
//			}
			
			executor.execute(new HttpRequestDD(request,context ,attachment,callback));
		}
		
		@Override
		public void execute(HttpUriRequest request, CompletedCallback callback) {
			execute(request,null ,null,callback);
		}
		
		
		
		@Override
		public void execute(HttpUriRequest request, Object attachment,
				CompletedCallback callback) {
			execute(request, null, attachment, callback);
		}
//		@Override
//		public void run() {
//			while(mRUN){
//				HttpRequestDD dd =null;
//				synchronized (this) {
//					if(jobs.size()==0){
//						try {
//							this.wait();					
//						} catch (InterruptedException e) {
//							Log.e(TAG, e.getMessage(), e);
//						}
//					}
//
//					if(!mRUN)
//						return ;
//
//					dd= jobs.poll();					
//				}				
//				executor.execute(dd);
//			}
//		}
	}

	private class HttpRequestDD implements Runnable{
		HttpUriRequest request;
		HttpContext context;
		Object attachment;
		CompletedCallback callback;
		
		HttpRequestDD(HttpUriRequest request, HttpContext context,
				Object attachment, CompletedCallback callback){
			this.request = request;
			this.attachment = attachment;
			this.context = context;
			this.callback = callback;
		}

		@Override
		public void run() {			
			HttpResponse httpResponse = null;
			IOException ioe = null;
			try {
				if(context==null){
					httpResponse = httpClient.execute(request);
				}
				else{
					httpResponse =httpClient.execute(request ,context );
				}
			} catch (IOException e) {
				ioe = e;
			}
			
			//Log.i(TAG,"get httpResponse="+ioe+" request="+request);
			
			try{
				if(callback!=null)
					callback.callback(request, httpResponse , attachment, ioe);
			}catch (Exception e) {
				Log.e(TAG, e.getMessage(),e);
			}finally{
				if(!request.isAborted())
					request.abort();
			}
		}
	}	
}

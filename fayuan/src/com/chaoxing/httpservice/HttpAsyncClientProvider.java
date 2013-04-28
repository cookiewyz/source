package com.chaoxing.httpservice;

import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;

import com.chaoxing.httpservice.HttpAsyncService.HttpAsyncClientBinder;

import android.content.ComponentName;
import android.os.IBinder;
import android.util.Log;


public class HttpAsyncClientProvider extends AbsServiceProvider implements HttpAsyncClient{
	
	private HttpAsyncClient httpAsyncClient;
	
	@Override
	public boolean isReady() {
		return super.isReady() && httpAsyncClient != null && httpAsyncClient.isReady();
	}
	
	@Override
	public void execute(HttpUriRequest request, CompletedCallback callback) {
		httpAsyncClient.execute(request, callback);
	}

	@Override
	public void execute(HttpUriRequest request, Object attachment,
			CompletedCallback callback) {
		httpAsyncClient.execute(request, attachment, callback);
	}

	@Override
	public void execute(HttpUriRequest request, HttpContext context,
			Object attachment, CompletedCallback callback) {
		httpAsyncClient.execute(request, context, attachment, callback);
	}

	@Override
	public Future<HttpResponse> execute(HttpUriRequest request) {
		return httpAsyncClient.execute(request);
	}

	@Override
	public Future<HttpResponse> execute(HttpUriRequest request,
			HttpContext context) {
		return httpAsyncClient.execute(request, context);
	}
	
	@Override
	public void onServiceConnected(ComponentName className, IBinder service) {
		super.onServiceConnected(className, service);		
		HttpAsyncClientBinder binder = (HttpAsyncClientBinder) service;
		httpAsyncClient = binder.getHttpAsyncClient();
	}	
	
	@Override
	protected String getAction() {
		return HttpAsyncService.ACTION;
	}
	
	@Override
	public void onServiceDisconnected(ComponentName arg0) {
		super.onServiceDisconnected(arg0);
	}
}	
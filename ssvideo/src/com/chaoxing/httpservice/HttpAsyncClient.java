package com.chaoxing.httpservice;

import java.io.IOException;
import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;


/**
 * Class HttpAsyncClient
 * 
 * @author <a href="mailto:hb562100@163.com">HeBo</a>
 * @version 2011-6-3
 */
public interface HttpAsyncClient {	
	
	boolean isReady();
	
	void execute(HttpUriRequest request ,CompletedCallback callback);	
	
	void execute(HttpUriRequest request ,Object attachment ,CompletedCallback callback);	
	
	void execute(HttpUriRequest request ,HttpContext context ,Object attachment ,CompletedCallback callback);
	
	Future<HttpResponse> execute(HttpUriRequest request);
	
	Future<HttpResponse> execute(HttpUriRequest request,HttpContext context);
	
	
	interface CompletedCallback{
		void callback(HttpUriRequest request , HttpResponse response, Object attachment , IOException e) throws IOException;
	}
}

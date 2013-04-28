package com.chaoxing.httpservice;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
//test
public abstract class AbsServiceProvider implements ServiceConnection{
	
	private volatile boolean bound;	
	private Context context;
	private final Lock lock = new ReentrantLock();
	private final Condition readyCondition  = lock.newCondition(); 
	
	private final long DEFAULT_LOCK_TIME = 5000;//5s 
	
	public void bridge(Context context){
		if(bound) return;
		
		this.context = context.getApplicationContext();
		Intent intent = new Intent();
		intent.setAction(getAction());
		this.context.bindService(intent, this, Context.BIND_AUTO_CREATE);
	}
	
	public void destroy(){
		if(bound){
			context.unbindService(this);
			bound = false;			
		}
	}
	
	public boolean isReady(){
		return bound;
	}
	
	/**
	 * Block the current thread waiting for bridge() completed 
	 * @param timeoutMillis
	 * @return  if true bridge successfully within the specified time.
	 */
	public boolean awaitForReady(long timeoutMillis){
		long s  = TimeUnit.MILLISECONDS.toNanos(timeoutMillis);	
		
		if(!bound){
			try{
				lock.lock();
					while (!bound) {
						if (s > 0){
							try {
								s = readyCondition.awaitNanos(s);							
							} catch (InterruptedException e) {
								return false;
							}
						}else{
							return false;
						}
					}
			}
			finally{
				lock.unlock();
			}			
		}
		
		return true;
	}

	/**
	 * Block the current thread waiting for bridge() completed ,default out of time is 5s
	 * @return if true bridge successfully within the default time.
	 */
	public boolean awaitForReady(){		
		return awaitForReady(DEFAULT_LOCK_TIME);
	}
	
	
	@Override
	public void onServiceConnected(ComponentName className,
			IBinder service) {
		bound = true;
	}

	@Override
	public void onServiceDisconnected(ComponentName arg0) {
		bound = false;
	}
	
	protected abstract String getAction();	
}

package com.chaoxing.video;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.chaoxing.util.Md5Util;
import com.chaoxing.util.UsersUtil;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;


public class CoverService extends Service {
	public static Context context;
	public static Map<String, Bitmap> coverCache;
	public static ArrayList<String> downQueue;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		context = CoverService.this;
		coverCache = new HashMap<String, Bitmap>();
		downQueue = new ArrayList<String>();
	}
	
	public void downloadCover(final String url, final Message msg) {
		if(url==null || "".equals(url)) return ;
		if(existCover(url) != null) {//modify_by_xin_2013-1-18.
			if(msg != null) msg.sendToTarget();
			return ;
		}
		downQueue.add(Md5Util.strToMd5(url));
		
		new Thread() {
			public void run() {
				Bitmap coverBitmap = null;
				try {
					coverBitmap = UsersUtil.getCoverImage(url);
				}catch (Throwable e) {
					if(e instanceof OutOfMemoryError){
						Log.e("CoverService", "down vodeoCover outOfMemory error.");
						coverCache.clear();
						System.gc();
					}
					e.printStackTrace();
				}
				String key = Md5Util.strToMd5(url);
				if(coverBitmap != null) {
					
					synchronized(coverCache){
						if(!coverCache.containsKey(key)) {
							coverCache.put(key, coverBitmap);
						}
					}
					synchronized(downQueue){
						if(downQueue.contains(key)) {
							downQueue.remove(key);
						}
					}
					if(msg != null) msg.sendToTarget();
				}
				else {
					Log.w("CoverService", "down videoCover is null: "+ url);
					synchronized(downQueue){
						if(downQueue.contains(key)) {
							downQueue.remove(key);
						}
					}
				}
			}
		}.start();
	}
	
	public Bitmap existCover(String url) {//add_by_xin_2013-1-18.
		if(url==null || "".equals(url)) return null;
		final String key = Md5Util.strToMd5(url);
		Bitmap cover = null;
		synchronized(coverCache) {
			cover = coverCache.get(key);
		}
		return cover;
	}
	
	public Bitmap getCover(String url) {
		if(url==null || "".equals(url)) return null;
		final String key = Md5Util.strToMd5(url);
		Bitmap cover = null;
		synchronized(coverCache) {
			cover = coverCache.get(key);
		}
		return cover;
	}
	
	public Bitmap getCover(String url, final Message msg) {//add_by_xin_2013-1-18.
		if(url==null || "".equals(url)) return null;
		final String key = Md5Util.strToMd5(url);
		Bitmap cover = null;
		synchronized(coverCache) {
			cover = coverCache.get(key);
		}
		synchronized(downQueue){
			if(cover == null && !downQueue.contains(key)) {
				downloadCover(url, msg);
			}
		}
		return cover;
	}
	
	public void clearCache() {//add_by_xin_2013-1-18.
		Set<String> keys = new HashSet<String>();
		synchronized(coverCache) {
			keys.addAll(coverCache.keySet());
			for(String key:keys) {
				Bitmap bmp = (Bitmap)coverCache.get(key);
			    if(bmp != null) {
			    	coverCache.remove(key);
			    	bmp.recycle();
			    	bmp = null;
				}
			}
		}
		keys = null;
		downQueue.clear();
		System.gc();
		System.gc();
	}

	@Override
	public void onDestroy() {
		coverCache.clear();
		context = null;
		super.onDestroy();
	}

}

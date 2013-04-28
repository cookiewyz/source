package com.chaoxing.video;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import roboguice.service.RoboService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.chaoxing.document.Global;
import com.chaoxing.httpservice.HttpAsyncClient;
import com.chaoxing.httpservice.HttpAsyncClientProvider;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class UpdateService extends RoboService {
	
	@Inject @Named("homeFolder")
	private File homeFolder;
	
	@Inject
	private HttpAsyncClientProvider clientProvider;
	
	private final static int DOWNLOAD_COMPLETE = 0;
	private final static int DOWNLOAD_FAIL = 1;
	//标题  
//	private int titleId = 0;  
	//文件存储  
	private File updateDir = null;
	private File updateFile = null;  
	private File updateFileTemp = null; 
	//通知栏  
	private NotificationManager updateNotificationManager = null;  
	private Notification updateNotification = null;  
	//通知栏跳转Intent  
	private Intent updateIntent = null;  
	private PendingIntent updatePendingIntent = null; 

	

	@Override
	public void onCreate() {
		super.onCreate();
		clientProvider.bridge(this);
	}



	@Override
	public void onDestroy() {
		super.onDestroy();
		if(clientProvider!=null){
			clientProvider.destroy();
		}
	}



	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
//		Log.i("zzy", "UpdateService onStartCommand-------------intent" + intent);
//		int titleId = intent.getIntExtra("titleId", 0);
//		Log.i("zzy", "titleId=" + titleId);
		updateDir = new File(homeFolder,"update");
		if(!updateDir.exists()){
			updateDir.mkdirs();
		}
		updateFile = new File(updateDir.getPath(),Global.apkName);
		updateFileTemp = new File(updateDir.getPath(),Global.apkName+"_tmp");
		
		this.updateNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		this.updateNotification = new Notification();
		//设置下载过程中，点击通知栏，回到主界面
		updateIntent = new Intent(this,SsvideoActivity.class);
		updatePendingIntent = PendingIntent.getActivity(this, 0, updateIntent, 0);
		//设置通知栏显示内容
		updateNotification.icon = R.drawable.video_dl_notify;
		updateNotification.tickerText = getString(R.string.update_dl_start);
		updateNotification.setLatestEventInfo(this, getString(R.string.app_name), "0%", updatePendingIntent);
		//发出通知
		updateNotificationManager.notify(0, updateNotification);
		if(!updateFile.exists()){
			Toast.makeText(getApplicationContext(), getString(R.string.update_dl_start), Toast.LENGTH_SHORT).show();
		}
		//开启一个新的线程下载
		new Thread(new updateRunnable()).start();
		
		return super.onStartCommand(intent, flags, startId);
	}

	private Handler updateHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
			case DOWNLOAD_COMPLETE:  
				//点击安装
				updateFileTemp.renameTo(updateFile);//
				Uri uri = Uri.fromFile(updateFile);
				Intent installIntent = new Intent(Intent.ACTION_VIEW);
				installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
				updatePendingIntent = PendingIntent.getActivity(UpdateService.this, 0, installIntent, 0);
                updateNotification.defaults = Notification.DEFAULT_SOUND;//铃声提醒   
                updateNotification.setLatestEventInfo(UpdateService.this, getString(R.string.update_dl_complete), getString(R.string.update_dl_install), updatePendingIntent);  
				updateNotificationManager.notify(0, updateNotification); 
				

				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
				intent.setAction(UpdateActivity.ACTION);	
				intent.putExtra("updateFile", updateFile.getAbsolutePath());
				startActivity(intent);
				
				stopService(updateIntent);
				break;
			case DOWNLOAD_FAIL:
                updateNotification.setLatestEventInfo(UpdateService.this, getString(R.string.update_dl_ing), "下载失败", updatePendingIntent);  
				updateNotificationManager.notify(0, updateNotification); 
				stopService(updateIntent);
				break;
			default:
				stopService(updateIntent);
			}
		}
	};
	
	class updateRunnable implements Runnable {
		Message message = updateHandler.obtainMessage();

		@Override
		public void run() {
			message.what = DOWNLOAD_COMPLETE;
			try{
				if(!updateDir.exists()){
					updateDir.mkdirs();
				}
				
				if(updateFile.exists()){
					//判断是否需要重新下载
					File file = new File(updateDir,"apkupdatelog.txt");
					if(file.exists()){
						String verCode = readLog(file);
						if(verCode == null || verCode.length() <= 0)
						{
							updateFile.delete();
							file.delete();
						}else{
							verCode = verCode.replace("verCode=", "");
							verCode = verCode.replace("\n", "");
							verCode = verCode.replace(" ", "");
							int curCode = Integer.parseInt(verCode);//.valueOf(verCode).intValue();
							if(curCode < Global.serverVersion)
							{
								updateFile.delete();
								file.delete();
							}
						}
					}else{
						updateFile.delete();
					}
				}
				
				if(!updateFile.exists()){
					if(!updateFileTemp.exists()){
						updateFileTemp.createNewFile();
					}
					String log = "verCode=" + Global.serverVersion;
					writeLog(log);
					
					boolean finish = false;
					for(int i=0;i<10;i++){
						long downloadSize = downloadUpdateFile(updateFileTemp);
						if(downloadSize > 0){
							finish = true;
							updateHandler.sendMessage(message);
							break;
						}
						Thread.sleep(10000); 
					}
					if(!finish){//下载失败
						message.what = DOWNLOAD_FAIL;
						updateHandler.sendMessage(message);
					}
				}else{
					updateHandler.sendMessage(message);
				}
					
			}catch(Exception e){
				e.printStackTrace();
				Log.d("ReaderView", "e");
				message.what = DOWNLOAD_FAIL;
				updateHandler.sendMessage(message);
			}
		}
		
	}
	
//	private long downloadUpdateFile(HttpAsyncClient client,File saveFile) throws Exception {
//		int downloadCount = 0;
//		int currentSize = 0;
//		int totalSize = 0;
//		int updateTotalSize = 0;
//		return 0;
//	}
	
	private long downloadUpdateFile(File saveFile) throws Exception {
		long retValue = 0;
		int downloadCount = 0;
		long currentSize = 0;
		int totalSize = 0;
		int updateTotalSize = 0;
		String downloadUrl = getString(R.string.update_url) + Global.apkName;
		currentSize = updateFileTemp.length();
		HttpURLConnection httpConnection = null;
		InputStream is = null;
		FileOutputStream fos = null;
		try{
			URL url = new URL(downloadUrl);
			httpConnection = (HttpURLConnection)url.openConnection();
//			httpConnection.setRequestProperty("User-Agent", ConstantModule.USER_AGENT);
			if(currentSize > 0){
				httpConnection.setRequestProperty("RANGE", "bytes=" + currentSize + "-");
			}
			httpConnection.setConnectTimeout(10000);
			httpConnection.setReadTimeout(20000);
			updateTotalSize = httpConnection.getContentLength();
//			Log.d("ReaderView", "updateTotalSize:"+updateTotalSize);
			if(httpConnection.getResponseCode() == 404){
				throw new Exception("fail!");
			}
			is = httpConnection.getInputStream();
			fos = new FileOutputStream(saveFile, true);
			byte buffer[] = new byte[4096];
			int readSize = 0;
			totalSize += currentSize;
			while((readSize = is.read(buffer)) > 0){
				fos.write(buffer, 0, readSize);
				totalSize += readSize;
				if((downloadCount == 0)||(int)(totalSize*100/(updateTotalSize+currentSize))-2>downloadCount){
					downloadCount += 2;
					updateNotification.setLatestEventInfo(UpdateService.this, getString(R.string.update_dl_ing), (int)totalSize*100/(updateTotalSize+currentSize)+"%", updatePendingIntent);
					updateNotificationManager.notify(0, updateNotification);
				}
			}
			
			if(totalSize == (updateTotalSize+currentSize))
			{
				retValue = totalSize;
				updateNotification.setLatestEventInfo(UpdateService.this, getString(R.string.update_dl_ing ), "100%", updatePendingIntent);
				updateNotificationManager.notify(0, updateNotification);
			}else{
				retValue = -1;
			}
		}catch (Exception e){
			retValue = -2;
		}finally{
			if(httpConnection != null){
				httpConnection.disconnect();
			}
			if(is != null){
				is.close();
			}
			if(fos != null){
				fos.close();
			}
		}
		
		return retValue;
	}
	
	public void writeLog(String log){ 
	     File saveFile = new File(updateDir,"apkupdatelog.txt"); 
	     FileOutputStream outStream = null;
		try {
			outStream = new FileOutputStream(saveFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
	     try {
			outStream.write(log.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		} 
		try {
			outStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public String readLog(File file){
		FileInputStream inStream = null;
		try {
			inStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		return readInStream(inStream); 
	}
	
	public String readInStream(FileInputStream inStream){ 
		try
		{ 
		   ByteArrayOutputStream outStream = new ByteArrayOutputStream(); 
		   byte[] buffer = new byte[1024]; 
		   int length = -1; 
		   while((length = inStream.read(buffer)) != -1 ){ 
		    outStream.write(buffer, 0, length); 
		   } 
		   outStream.close(); 
		   inStream.close(); 
		   return outStream.toString(); 
		  } catch (IOException e){ 
			  e.printStackTrace();
		  } 
		  return null; 
	}

	
//	private HttpAsyncClient checksClient(){
//		if(!clientProvider.isReady()){
//			if(clientProvider.awaitForReady(3000))
//				throw new IllegalStateException("Can't connect HttpAsyncService!");
//		}
//		return clientProvider;
//	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}

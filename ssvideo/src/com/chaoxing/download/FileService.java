package com.chaoxing.download;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StatFs;
import android.util.Log;
import android.widget.Toast;

import com.chaoxing.database.SSVideoCategoryBean;
import com.chaoxing.database.SSVideoDatabaseAdapter;
import com.chaoxing.database.SSVideoLocalVideoBean;
import com.chaoxing.util.UsersUtil;
import com.chaoxing.video.SsvideoRoboApplication;

public class FileService extends Service {
	private final static String TAG="fileService";

	SSVideoDatabaseAdapter mDBAdapter = null;
	private SsvideoRoboApplication ssvideo;
	private Vector<FileDownloadThread> threadsVector;
	private String SSvideoPath;
	private String SScoversPath;
	private int errorCode;
	private static final int TIME_OUT = 2;
	
	@Override
	public void onCreate() {
		ssvideo = (SsvideoRoboApplication)getApplication();
		mDBAdapter = new SSVideoDatabaseAdapter(FileService.this, ssvideo, TAG);
		SSvideoPath = UsersUtil.getRootDir() +"/";
		SScoversPath = UsersUtil.getUserCoverDir() +"/";
		threadsVector = new Vector<FileService.FileDownloadThread>();
	}

	@Override
	public void onStart(Intent intent, int startId) {
//		Log.i(TAG, "file service is starting ------------------");
		if(intent != null){
			Bundle bundle = intent.getExtras();
			if(bundle != null){
				int op = bundle.getInt("op");
				
				switch(op){
				case 0: // 该视频第一次开始下载
					createDownloader(bundle);
					break;
				case 1: // 开始一个已暂停下载的视频
					startDownloader(bundle);
					break;
				case 2:  // 暂停一个正在下载的视频
					stopDownloader(bundle);
					break;
				case 3: // 删除一个正在下载的视频
					deleteDownloadingFile(bundle);
					break;
				default:
						break;
				}
			}
		}
	}

	private void createDownloader(Bundle bundle) {
		final SSVideoLocalVideoBean localVideoBean = (SSVideoLocalVideoBean) bundle.getSerializable("localVideoBean");
//		Log.i(TAG, "localVideoBean remoteFileName------------>" + localVideoBean.getStrVideoDownloadRemoteFileUrl());
//		Log.i(TAG, "localVideoBean FileName------------>" + localVideoBean.getStrVideoFileName());
//		Log.i(TAG, "first down-----------");
		localVideoBean.setnVideoDownloadStatus(1);
		
//		SSVideoCategoryBean cateBean = mDBAdapter.fetchCategoryById(localVideoBean.getStrCateId());
		String cateName = localVideoBean.getStrCateName().trim();
		SSVideoCategoryBean cateBean = null;
		if(cateName != "") {//modify_by_xin_2013-3-22.
			cateBean = mDBAdapter.fetchCategoryByName(cateName);
		}
		if(cateBean != null){
			localVideoBean.setStrVideoLocalPath(SSvideoPath + cateBean.getStrCateName() + "/" + localVideoBean.getStrVideoFileName() + ".mp4");
			localVideoBean.setStrCateId(cateBean.getStrCateId());
		}
		else {
			if(cateName != "") {//modify_by_xin_2013-3-22.
				cateBean = new SSVideoCategoryBean();
				cateBean.setStrCateId(localVideoBean.getStrCateId());
				cateBean.setStrCateName(localVideoBean.getStrCateName());
				mDBAdapter.insertIntoCategory(cateBean);
				localVideoBean.setStrVideoLocalPath(SSvideoPath + localVideoBean.getStrCateName() + "/" + localVideoBean.getStrVideoFileName() + ".mp4");
			}
			else {
				localVideoBean.setStrCateId("0");
				localVideoBean.setStrVideoLocalPath(SSvideoPath + "未分类/" + localVideoBean.getStrVideoFileName() + ".mp4");
			}
		}
		
		// 下载视频封面
		downloadCover(localVideoBean.getStrVideoDownloadRemoteCoverUrl(), localVideoBean.getStrCoverName());//add_by_xin_2012-12-28.
		// 下载视频文件
		FileDownloadThread thread = new FileDownloadThread( new DownloadFileLengthListener() {
			@Override
			public long onGetDownloadFileLen(long fileLen) {
				// 将要下载的视频写入local_video表
				if(fileLen <= 0){
					errorCode = -1;
					return -1;
				}
			
				errorCode = 0;
				localVideoBean.setnVideoFileLength((int) fileLen);
//				Log.i(TAG, "create downloader  file length===========================" + localVideoBean.getnVideoFileLength());
				mDBAdapter.insertIntoLocalVideo(localVideoBean);
				return 0;
			}
		});
		if(errorCode == -1){
			// 下载文件长度<=0下载出错
			Intent intent = new Intent().setAction("com.chaoxing.download.DownloadBroadcastReceiver");
			Bundle serviceBundle = new Bundle();
			serviceBundle.putString("msg", "文件不存在");
			serviceBundle.putInt("type", 1);
			intent.putExtras(bundle);
			sendBroadcast(intent);
		}
		thread.setRemoteFilePath(localVideoBean.getStrVideoDownloadRemoteFileUrl());
		thread.setSavePath(SSvideoPath + cateBean.getStrCateName());
		thread.setThreadId(localVideoBean.getStrVideoId());
		thread.setSaveFileName(localVideoBean.getStrVideoFileName() + ".mp4");
		thread.setDownloadSize(0);
		thread.start();
		threadsVector.add(thread);
	}
	
	private void startDownloader(Bundle bundle) {
		SSVideoLocalVideoBean localVideoBean = (SSVideoLocalVideoBean) bundle.getSerializable("localVideoBean");
//		Log.i(TAG, "localVideoBean remoteFileName------------>" + localVideoBean.getStrVideoDownloadRemoteFileUrl());
//		Log.i(TAG, "localVideoBean FileName------------>" + localVideoBean.getStrVideoFileName());
//		String downloadFileMsg;
//		Log.i(TAG, "first down-----------");
		SSVideoLocalVideoBean tempLocalVideoBean = new SSVideoLocalVideoBean();
		tempLocalVideoBean.setnVideoDownloadStatus(1);
		
		mDBAdapter.updateDataInLocalVideo(localVideoBean.getStrVideoId(), tempLocalVideoBean);
		
		// 下载视频文件
		FileDownloadThread thread = new FileDownloadThread(null);
		thread.setRemoteFilePath(localVideoBean.getStrVideoDownloadRemoteFileUrl());
		String localFilePath = localVideoBean.getStrVideoLocalPath();
		thread.setSavePath(localFilePath.substring(0, localFilePath.lastIndexOf('/')));
		thread.setThreadId(localVideoBean.getStrVideoId());
		thread.setSaveFileName(localVideoBean.getStrVideoFileName() + ".mp4");
		thread.setDownloadSize(localVideoBean.getnVideoDownloadProgress());
		thread.start();
		threadsVector.add(thread);
	}

	private void stopDownloader(Bundle bundle) {
		SSVideoLocalVideoBean localVideoBean = (SSVideoLocalVideoBean) bundle.getSerializable("localVideoBean");
//		Log.i(TAG, "localVideoBean remoteFileName------------>" + localVideoBean.getStrVideoDownloadRemoteFileUrl());
//		Log.i(TAG, "localVideoBean FileName------------>" + localVideoBean.getStrVideoFileName());
//		String downloadFileMsg;
		String threadId = localVideoBean.getStrVideoId();
		// 停止下载
		for(FileDownloadThread tempThread : threadsVector){
			if(tempThread.getThreadId().equals(threadId)){
				tempThread.stopDownloader();
				threadsVector.remove(tempThread);
				break;
			}
		}
		// 更新状态
		SSVideoLocalVideoBean tempLocalVideoBean = new SSVideoLocalVideoBean();
		tempLocalVideoBean.setnVideoDownloadStatus(2);
//			Log.i(TAG, "video id --------" + threadId + " video download status ========= " + tempLocalVideoBean.getnVideoDownloadStatus().intValue());
		mDBAdapter.updateDataInLocalVideo(localVideoBean.getStrVideoId(), tempLocalVideoBean);
	}

	private void deleteDownloadingFile(Bundle bundle) {
		SSVideoLocalVideoBean localVideoBean = (SSVideoLocalVideoBean) bundle.getSerializable("localVideoBean");
//		Log.i(TAG, "localVideoBean remoteFileName------------>" + localVideoBean.getStrVideoDownloadRemoteFileUrl());
//		Log.i(TAG, "localVideoBean FileName------------>" + localVideoBean.getStrVideoFileName());
//		String downloadFileMsg;
		String threadId = localVideoBean.getStrVideoId();
		// 停止下载
		for(FileDownloadThread tempThread : threadsVector){
			if(tempThread.getThreadId().equals(threadId)){
				tempThread.stopDownloader();
				threadsVector.remove(tempThread);
				break;
			}
		}
		mDBAdapter.deleteDataInLocalVideoById(threadId);
		File file = new File(localVideoBean.getStrVideoLocalPath());
		file.delete();
		File fCover = new File(localVideoBean.getStrCoverName());
		fCover.delete();
	}
	// 下载视频封面
	private void downloadCover(final String coverUrl, final String coverName) {
		new Thread() {
			public void run() {
				URL url;
				InputStream inputStream;
				RandomAccessFile accessFile;
				try {
					url = new URL(coverUrl);
					HttpURLConnection conn  = (HttpURLConnection)url.openConnection();
					conn.setDoInput(true);
					conn.connect(); 
					inputStream = conn.getInputStream();
					
					File coverImg = new File(SScoversPath + coverName);
//					Log.w(TAG, "cover is in:"+ coverImg.toString());
					accessFile = new RandomAccessFile(coverImg, "rwd");
					byte[] buffer = new byte[1024];
					int len = 0;
					while( (len = inputStream.read(buffer)) != -1){
						accessFile.write(buffer, 0, len);
					}
					inputStream.close();
					accessFile.close();
					/*Message msg = handler.obtainMessage();
					msg.what = 0;
					msg.arg1 = 1;
					handler.sendMessage(msg);*/
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
        }.start();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		mDBAdapter.updateDownloadingToPause();
		
		for(FileDownloadThread thread : threadsVector){
			thread.stopDownloader();
		}
		threadsVector.clear();
//		Log.i(TAG, "file service detoryed---------------------------");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	class FileDownloadThread extends Thread{			
		private String threadId;
		private int fileLen;
		private String remoteFilePath;
		private String savePath;
		private int downloadSize;
		private String saveFileName;
		FileDownloader loader = null;
		private int lastSize = 0;
		private DownloadFileLengthListener fileLenListener;
		
		public FileDownloadThread(DownloadFileLengthListener listener){
			this.fileLenListener = listener;
		}
		
		@Override
		public void run() {
			try {
				loader = new FileDownloader(FileService.this, ssvideo, remoteFilePath, new File(savePath), saveFileName, 1, downloadSize, handler);
			} catch (Exception e) {
				handler.sendEmptyMessage(TIME_OUT);
			}
			
			try {
				fileLen = loader.getFileSize();
				// 通知监听者要下载的文件的长度
				if(this.fileLenListener != null) this.fileLenListener.onGetDownloadFileLen(fileLen);
				long SDcardAvailable = getSDCardAviailSize();
				// SD卡空间不足
				if(SDcardAvailable < fileLen){
					Intent intent = new Intent().setAction("com.chaoxing.download.DownloadBroadcastReceiver");
					Bundle bundle = new Bundle();
					bundle.putString("threadId", threadId);
					bundle.putString("msg", "SD卡空间不足！");
					bundle.putInt("type", 1);
					intent.putExtras(bundle);
					sendBroadcast(intent);
					return ;
				}
				lastSize = downloadSize;
//				Log.i(TAG, "File Service fileSize " + fileLen);
				loader.download(new DownloadProgressListener() {
					@Override
					public void onDownloadSize(int size) {//实时获知文件已经下载的数据长度
//						Log.i(TAG, "file service download size " + size);
						int sizePerS = size - lastSize;
						lastSize = size;
						Intent intent = new Intent().setAction("com.chaoxing.download.DownloadBroadcastReceiver");
						Bundle bundle = new Bundle();
						bundle.putString("threadId", threadId);
						bundle.putInt("progress", size);
						bundle.putInt("fileLen", fileLen);
						bundle.putInt("status", 1);	
						bundle.putInt("speed", sizePerS);
						
						bundle.putInt("type", 0);
//						bundle.putSerializable("downloadList", (Serializable) downloadList);
						intent.putExtras(bundle);
						sendBroadcast(intent);
//						Log.i(TAG, "file service download thread ===" + saveFileName + " ===progress---------------------->" + size);
						SSVideoLocalVideoBean localVideoBean = new SSVideoLocalVideoBean();
						localVideoBean.setStrVideoId(threadId);
						localVideoBean.setnVideoDownloadProgress(size);
//						Log.i(TAG, "file service download theread localVideoBean getnVideoDownloadProgress =======" + localVideoBean.getnVideoDownloadProgress());
						
						mDBAdapter.updateDataInLocalVideo(threadId, localVideoBean);
					}
				});
				if(loader.isFinished() == true){
					SSVideoLocalVideoBean localVideoBean = new SSVideoLocalVideoBean();
					localVideoBean.setnVideoDownloadStatus(0);
					mDBAdapter.updateDataInLocalVideoByRemoteFilePath(remoteFilePath, localVideoBean);
					
					Intent intent = new Intent().setAction("com.chaoxing.download.DownloadBroadcastReceiver");
					Bundle bundle = new Bundle();
					bundle.putString("threadId", threadId);
					bundle.putInt("progress", fileLen);
					bundle.putInt("fileLen", fileLen);
					bundle.putInt("status", 0);	
					bundle.putInt("type", 0);
					intent.putExtras(bundle);
					sendBroadcast(intent);
//					Log.i(TAG, this.threadId + " download finished11111111111111111");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		public void setThreadId(String threadId)
		{
			this.threadId = threadId;
		}
		public String getThreadId()
		{
			return this.threadId;
		}
		public void setRemoteFilePath(String remoteFilePath)
		{
			this.remoteFilePath = remoteFilePath;
		}
		public void setSavePath(String savePath)
		{
			this.savePath = savePath;
		}
		public void setDownloadSize(int downloadSize)
		{
			this.downloadSize = downloadSize;
		}
		public void setSaveFileName(String saveFileName){
			this.saveFileName = saveFileName;
		}
		public void stopDownloader(){
			if(loader != null)
				loader.stopDownload();
		}
		
		public int getFileLen(){
			return this.fileLen;
		}
		
		public long  getSDCardAviailSize() {   
	        String state = Environment.getExternalStorageState();   
	        if (Environment.MEDIA_MOUNTED.equals(state)) {   
	            File sdcardDir = Environment.getExternalStorageDirectory();   
	            StatFs sf = new  StatFs(sdcardDir.getPath());   
	            long  blockSize = sf.getBlockSize(); 
	            long  availCount = sf.getAvailableBlocks();   
//	            Log.i(TAG, "SDCard block size===============" + blockSize);
//	            Log.i(TAG, "SDCard available block count===============" + availCount);
	            return blockSize * availCount;
	        }
			return 0;      
	    }
	}
	
	public final Handler handler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case 0:
				if(msg.arg1 > 0) {
					Log.w(TAG, "cover is download.");
				}
				
				break;
			case 2:
				Toast.makeText(getApplicationContext(), "连接下载服务器失败,请重试", Toast.LENGTH_LONG).show();
				break;
			case 3:
				Toast.makeText(getApplicationContext(), "视频已经开始下载", Toast.LENGTH_LONG).show();
				break;
			}
		}
	};

}

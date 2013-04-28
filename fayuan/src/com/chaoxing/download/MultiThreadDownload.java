package com.chaoxing.download;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Environment;

public class MultiThreadDownload {
	private final static String TAG = "multiThreadDownload";
	/**
	 * 获取要下载的文件的名称
	 * @param path 要下载文件的路径
	 * @return
	 */
	public static String getFileName(String path){
		return path.substring(path.lastIndexOf('/') + 1);
	}
	public void download(String path, int threadSize) throws Exception{
//		Log.i(TAG, path);
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod("GET");
		conn.setReadTimeout(5 * 1000);
		int fileLength = conn.getContentLength(); // 获取要下载文件的长度
		String fileName = getFileName(path); // 获取要下载的文件的文件名
		System.out.println(fileName + " length----- " + fileLength);
		String sdCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ssvideo/";
		File saveFile = new File(sdCardRoot + fileName);
		RandomAccessFile accessFile = new RandomAccessFile(saveFile, "rwd");
		accessFile.setLength(fileLength); // 设置本地文件长度和下载文件相同
		accessFile.close();
//		Log.i(TAG, "before start download thread  " + threadSize);
		int block = fileLength%threadSize==0 ? fileLength/threadSize : fileLength/threadSize+1;
		for (int threadId=0; threadId<threadSize; threadId++){
			new DownloadThread(url, saveFile, block, threadId).start();
		}
	}
	private final class DownloadThread extends Thread{
		private URL url;
		private File saveFile;
		private int block;
		private int threadId;
		public DownloadThread(URL url, File saveFile, int block, int threadId) {
			this.url = url;
			this.saveFile = saveFile;
			this.block = block;   // 每条线程下载的数据长度
			this.threadId = threadId; // 线程id
		}
		@Override
		public void run() {
			// 计算开始位置公式： 线程Id * 每条线程下载的数据长度 = ？
			// 计算结束位置公式：（线程id+1) * 每条线程下载的数据长度 - 1 = ？
			int startPosition = threadId * block;
			int endPosition = (threadId+1) * block -1;
//			Log.i(TAG, "download thread is running......");
			try {
				RandomAccessFile accessFile = new RandomAccessFile(saveFile, "rwd");
				accessFile.seek(startPosition); // 设置从什么位置开始写入数据
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				conn.setRequestMethod("GET");
				conn.setReadTimeout(5 * 1000);
				conn.setRequestProperty("Range", "bytes=" + startPosition + "-" + endPosition);
				InputStream inStream = conn.getInputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while( (len = inStream.read(buffer)) != -1){
					accessFile.write(buffer, 0, len);
//					Log.i(TAG, "下载中…… " + len);
				}
				inStream.close();
				accessFile.close();
				System.out.println("线程id：" + threadId + "下载完成");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}

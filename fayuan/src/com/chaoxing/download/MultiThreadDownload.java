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
	 * ��ȡҪ���ص��ļ�������
	 * @param path Ҫ�����ļ���·��
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
		int fileLength = conn.getContentLength(); // ��ȡҪ�����ļ��ĳ���
		String fileName = getFileName(path); // ��ȡҪ���ص��ļ����ļ���
		System.out.println(fileName + " length----- " + fileLength);
		String sdCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ssvideo/";
		File saveFile = new File(sdCardRoot + fileName);
		RandomAccessFile accessFile = new RandomAccessFile(saveFile, "rwd");
		accessFile.setLength(fileLength); // ���ñ����ļ����Ⱥ������ļ���ͬ
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
			this.block = block;   // ÿ���߳����ص����ݳ���
			this.threadId = threadId; // �߳�id
		}
		@Override
		public void run() {
			// ���㿪ʼλ�ù�ʽ�� �߳�Id * ÿ���߳����ص����ݳ��� = ��
			// �������λ�ù�ʽ�����߳�id+1) * ÿ���߳����ص����ݳ��� - 1 = ��
			int startPosition = threadId * block;
			int endPosition = (threadId+1) * block -1;
//			Log.i(TAG, "download thread is running......");
			try {
				RandomAccessFile accessFile = new RandomAccessFile(saveFile, "rwd");
				accessFile.seek(startPosition); // ���ô�ʲôλ�ÿ�ʼд������
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				conn.setRequestMethod("GET");
				conn.setReadTimeout(5 * 1000);
				conn.setRequestProperty("Range", "bytes=" + startPosition + "-" + endPosition);
				InputStream inStream = conn.getInputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while( (len = inStream.read(buffer)) != -1){
					accessFile.write(buffer, 0, len);
//					Log.i(TAG, "�����С��� " + len);
				}
				inStream.close();
				accessFile.close();
				System.out.println("�߳�id��" + threadId + "�������");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}

package com.chaoxing.download;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.chaoxing.database.SSVideoDatabaseAdapter;
import com.chaoxing.database.SSVideoLocalVideoBean;
import com.chaoxing.video.SsvideoPlayerActivity;
import com.chaoxing.video.SsvideoRoboApplication;
/**
 * �ļ�������
 * FileDownloader loader = new FileDownloader(context, "http://browse.babasport.com/ejb3/ActivePort.exe",
				new File("D:\\androidsoft\\test"), 2);
		loader.getFileSize();//�õ��ļ��ܴ�С
		try {
			loader.download(new DownloadProgressListener(){
				public void onDownloadSize(int size) {
					print("�Ѿ����أ�"+ size);
				}			
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
 */
public class FileDownloader {
	private static final String TAG = "fileDownloader";
	private Context context;
	private SSVideoDatabaseAdapter mDBAdapter = null;

	/* �������ļ����� */
	private int downloadSize = 0;
	/* ԭʼ�ļ����� */
	private int fileSize = 0;
	/* �߳��� */
	private DownloadThread[] threads;
	/* ���ر����ļ� */
	private File saveFile;
	/* ������߳����صĳ���*/
	private Map<Integer, Integer> data = new ConcurrentHashMap<Integer, Integer>();
	/* ÿ���߳����صĳ��� */
	private int block;
	/* ����·��  */
	private String downloadUrl;
	private boolean isStoped = false;
	private boolean isFinished = false;
	
	/**
	 * ��ȡ�߳���
	 */
	public int getThreadSize() {
		return threads.length;
	}
	/**
	 * ��ȡ�ļ���С
	 * @return
	 */
	public int getFileSize() {
		return fileSize;
	}
	/**
	 * �ۼ������ش�С
	 * @param size
	 */
	protected synchronized void append(int size) {
		downloadSize += size;
	}
	/**
	 * ����ָ���߳�������ص�λ��
	 * @param threadId �߳�id
	 * @param pos ������ص�λ��
	 */
	protected synchronized void update(int threadId, int pos) {
		this.data.put(threadId, pos);
		SSVideoLocalVideoBean localVideoBean = new SSVideoLocalVideoBean();
		localVideoBean.setnVideoDownloadProgress(pos);
		mDBAdapter.updateDataInLocalVideoByRemoteFilePath(downloadUrl, localVideoBean);
//		this.fileService.update(this.downloadUrl, this.data);
	}
	/**
	 * �����ļ�������
	 * @param downloadUrl ����·��
	 * @param fileSaveDir �ļ�����Ŀ¼
	 * @param threadNum �����߳���
	 */
	public FileDownloader(Context context, SsvideoRoboApplication ssvideo, String downloadUrl, File fileSaveDir, String saveFileName, int threadNum, int downloadLen, Handler handle) {
		try {
			this.context = context;
			this.downloadUrl = downloadUrl;
			this.downloadSize = downloadLen;
			if(mDBAdapter == null) {
				mDBAdapter = new SSVideoDatabaseAdapter(this.context, ssvideo, TAG);
			}
//			Log.i(TAG, "downloadSize--------------------- " + downloadLen);
			
			URL url = new URL(this.downloadUrl);
			if(!fileSaveDir.exists()) fileSaveDir.mkdirs();
			this.threads = new DownloadThread[threadNum];					
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			int timeOut = 6*5*1000;
			conn.setConnectTimeout(timeOut);
			conn.setReadTimeout(timeOut);//add_by_xin_2013-3-6.
			conn.setRequestMethod("GET");
			int connnum = 0; 
			while(connnum<3) {//���ӷ�����һ������6�Σ�����6����ʾ����ʧ��
				try {
					conn.connect();
					break;
				}catch (Exception e) {
					if(connnum==2)//5
						throw new RuntimeException("don't connection this url " + downloadUrl);
					connnum++;
				}
			}
//			Log.i(TAG, "save dir " + fileSaveDir.toString());
//			int len = conn.getContentLength();
//			Log.i(TAG, "file len" + len);
//			conn.connect();
//			printResponseHeader(conn);
			if (conn.getResponseCode()==200) {
				if(handle != null) {
					handle.sendEmptyMessage(3);//obtainMessage(3, 1, 2).sendToTarget();
				}
//				Toast.makeText(this.context, "��Ƶ�Ѿ���ʼ����", Toast.LENGTH_LONG).show();
				this.fileSize = conn.getContentLength();//������Ӧ��ȡ�ļ���С
				if (this.fileSize <= 0) throw new RuntimeException("Unkown file size ");
//				Log.i(TAG, "File downloader fileSize "+ this.fileSize);		
//				String filename = getFileName(conn);//��ȡ�ļ�����
				this.saveFile = new File(fileSaveDir, saveFileName);//���������ļ�
				//����ÿ���߳����ص����ݳ���
				this.block = (this.fileSize % this.threads.length)==0? this.fileSize / this.threads.length : this.fileSize / this.threads.length + 1;
			}else{
				throw new RuntimeException("server no response ");
			}
		} catch (Exception e) {
			print(e.toString());
			throw new RuntimeException("don't connection this url " + downloadUrl);
		}
	}
	/**
	 * ��ȡ�ļ���
	 */
	private String getFileName(HttpURLConnection conn) {
		String filename = this.downloadUrl.substring(this.downloadUrl.lastIndexOf('/') + 1);
		if(filename==null || "".equals(filename.trim())){//�����ȡ�����ļ�����
			for (int i = 0;; i++) {
				String mine = conn.getHeaderField(i);
				if (mine == null) break;
				if("content-disposition".equals(conn.getHeaderFieldKey(i).toLowerCase())){
					Matcher m = Pattern.compile(".*filename=(.*)").matcher(mine.toLowerCase());
					if(m.find()) return m.group(1);
				}
			}
			filename = UUID.randomUUID()+ ".tmp";//Ĭ��ȡһ���ļ���
		}
		return filename;
	}
	
	/**
	 *  ��ʼ�����ļ�
	 * @param listener �������������ı仯,�������Ҫ�˽�ʵʱ���ص�����,��������Ϊnull
	 * @return �������ļ���С
	 * @throws Exception
	 */
	public int download(DownloadProgressListener listener) throws Exception{
		isStoped = false;
		try {
			RandomAccessFile randOut = new RandomAccessFile(this.saveFile, "rw");
			if(this.fileSize>0) randOut.setLength(this.fileSize);
			randOut.close();
			URL url = new URL(this.downloadUrl);
			if(this.data.size() != this.threads.length){
				this.data.clear();
				for (int i = 0; i < this.threads.length; i++) {
					this.data.put(i+1, 0);//��ʼ��ÿ���߳��Ѿ����ص����ݳ���Ϊ0
				}
			}
			for (int i = 0; i < this.threads.length; i++) {//�����߳̽�������
				int downLength = this.data.get(i+1);
				
				if(downLength < this.block && this.downloadSize<this.fileSize){//�ж��߳��Ƿ��Ѿ��������,�����������	
					this.threads[i] = new DownloadThread(this, url, this.saveFile, this.block, this.downloadSize, i+1, listener);
					this.threads[i].setPriority(7);
					this.threads[i].start();
				}else{
					this.threads[i] = null;
				}
			}
			boolean notFinish = true;//����δ���
			while (notFinish==true && isStoped==false) {// ѭ���ж������߳��Ƿ��������
				Thread.sleep(1000);
				notFinish = false;//�ٶ�ȫ���߳��������
				for (int i = 0; i < this.threads.length; i++){
					if (this.threads[i] != null && !this.threads[i].isFinish()) {//��������߳�δ�������
						notFinish = true;//���ñ�־Ϊ����û�����
						if(this.threads[i].getDownLength() == -1){//�������ʧ��,����������
							this.threads[i] = new DownloadThread(this, url, this.saveFile, this.block, this.downloadSize, i+1, listener);
							this.threads[i].setPriority(7);
							this.threads[i].start();
						}
					}
				}				
				if(listener!=null) listener.onDownloadSize(this.downloadSize);//֪ͨĿǰ�Ѿ�������ɵ����ݳ���
			}
			if(isStoped == false){
				this.isFinished = true;
//				Log.i(TAG, "file downloader finished++++++++++++++++++");
			}
			else this.isFinished = false;
		} catch (Exception e) {
			print(e.toString());
//			throw new Exception("file download fail");
		}
		return this.downloadSize;
	}
	/**
	 * ��ȡHttp��Ӧͷ�ֶ�
	 * @param http
	 * @return
	 */
	public static Map<String, String> getHttpResponseHeader(HttpURLConnection http) {
		Map<String, String> header = new LinkedHashMap<String, String>();
		for (int i = 0;; i++) {
			String mine = http.getHeaderField(i);
			if (mine == null) break;
			header.put(http.getHeaderFieldKey(i), mine);
		}
		return header;
	}
	/**
	 * ��ӡHttpͷ�ֶ�
	 * @param http
	 */
	public static void printResponseHeader(HttpURLConnection http){
		Map<String, String> header = getHttpResponseHeader(http);
		for(Map.Entry<String, String> entry : header.entrySet()){
			String key = entry.getKey()!=null ? entry.getKey()+ ":" : "";
			print(key+ entry.getValue());
		}
	}

	private static void print(String msg){
		Log.i(TAG, msg);
	}
	
	public void stopDownload(){
		this.isStoped = true;
		for(int i=0; i<this.threads.length; i++){
			if(this.threads[i] != null)
				this.threads[i].stopThread();
		}
	}
	
	public boolean isFinished(){
		return this.isFinished;
	}
}

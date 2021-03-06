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
import android.util.Log;
import android.widget.Toast;

import com.chaoxing.database.SSVideoDatabaseAdapter;
import com.chaoxing.database.SSVideoLocalVideoBean;
import com.chaoxing.video.SsvideoPlayerActivity;
import com.chaoxing.video.SsvideoRoboApplication;
/**
 * 文件下载器
 * FileDownloader loader = new FileDownloader(context, "http://browse.babasport.com/ejb3/ActivePort.exe",
				new File("D:\\androidsoft\\test"), 2);
		loader.getFileSize();//得到文件总大小
		try {
			loader.download(new DownloadProgressListener(){
				public void onDownloadSize(int size) {
					print("已经下载："+ size);
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

	/* 已下载文件长度 */
	private int downloadSize = 0;
	/* 原始文件长度 */
	private int fileSize = 0;
	/* 线程数 */
	private DownloadThread[] threads;
	/* 本地保存文件 */
	private File saveFile;
	/* 缓存各线程下载的长度*/
	private Map<Integer, Integer> data = new ConcurrentHashMap<Integer, Integer>();
	/* 每条线程下载的长度 */
	private int block;
	/* 下载路径  */
	private String downloadUrl;
	private boolean isStoped = false;
	private boolean isFinished = false;
	/**
	 * 获取线程数
	 */
	public int getThreadSize() {
		return threads.length;
	}
	/**
	 * 获取文件大小
	 * @return
	 */
	public int getFileSize() {
		return fileSize;
	}
	/**
	 * 累计已下载大小
	 * @param size
	 */
	protected synchronized void append(int size) {
		downloadSize += size;
	}
	/**
	 * 更新指定线程最后下载的位置
	 * @param threadId 线程id
	 * @param pos 最后下载的位置
	 */
	protected synchronized void update(int threadId, int pos) {
		this.data.put(threadId, pos);
		SSVideoLocalVideoBean localVideoBean = new SSVideoLocalVideoBean();
		localVideoBean.setnVideoDownloadProgress(pos);
		mDBAdapter.updateDataInLocalVideoByRemoteFilePath(downloadUrl, localVideoBean);
//		this.fileService.update(this.downloadUrl, this.data);
	}
	/**
	 * 构建文件下载器
	 * @param downloadUrl 下载路径
	 * @param fileSaveDir 文件保存目录
	 * @param threadNum 下载线程数
	 */
	public FileDownloader(Context context, SsvideoRoboApplication ssvideo, String downloadUrl, File fileSaveDir, String saveFileName, int threadNum, int downloadLen) {
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
			conn.setConnectTimeout(5*1000);
			conn.setRequestMethod("GET");
			int connnum = 0; 
			while(connnum<6) {//连接服务器一共尝试6次，超过6次提示连接失败
				try {
					conn.connect();
					break;
				}catch (Exception e) {
					if(connnum==5)
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
				Toast.makeText(this.context, "视频已经开始下载", Toast.LENGTH_LONG).show();
				this.fileSize = conn.getContentLength();//根据响应获取文件大小
				if (this.fileSize <= 0) throw new RuntimeException("Unkown file size ");
//				Log.i(TAG, "File downloader fileSize "+ this.fileSize);		
//				String filename = getFileName(conn);//获取文件名称
				this.saveFile = new File(fileSaveDir, saveFileName);//构建保存文件
				//计算每条线程下载的数据长度
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
	 * 获取文件名
	 */
	private String getFileName(HttpURLConnection conn) {
		String filename = this.downloadUrl.substring(this.downloadUrl.lastIndexOf('/') + 1);
		if(filename==null || "".equals(filename.trim())){//如果获取不到文件名称
			for (int i = 0;; i++) {
				String mine = conn.getHeaderField(i);
				if (mine == null) break;
				if("content-disposition".equals(conn.getHeaderFieldKey(i).toLowerCase())){
					Matcher m = Pattern.compile(".*filename=(.*)").matcher(mine.toLowerCase());
					if(m.find()) return m.group(1);
				}
			}
			filename = UUID.randomUUID()+ ".tmp";//默认取一个文件名
		}
		return filename;
	}
	
	/**
	 *  开始下载文件
	 * @param listener 监听下载数量的变化,如果不需要了解实时下载的数量,可以设置为null
	 * @return 已下载文件大小
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
					this.data.put(i+1, 0);//初始化每条线程已经下载的数据长度为0
				}
			}
			for (int i = 0; i < this.threads.length; i++) {//开启线程进行下载
				int downLength = this.data.get(i+1);
				
				if(downLength < this.block && this.downloadSize<this.fileSize){//判断线程是否已经完成下载,否则继续下载	
					this.threads[i] = new DownloadThread(this, url, this.saveFile, this.block, this.downloadSize, i+1, listener);
					this.threads[i].setPriority(7);
					this.threads[i].start();
				}else{
					this.threads[i] = null;
				}
			}
			boolean notFinish = true;//下载未完成
			while (notFinish==true && isStoped==false) {// 循环判断所有线程是否完成下载
				Thread.sleep(1000);
				notFinish = false;//假定全部线程下载完成
				for (int i = 0; i < this.threads.length; i++){
					if (this.threads[i] != null && !this.threads[i].isFinish()) {//如果发现线程未完成下载
						notFinish = true;//设置标志为下载没有完成
						if(this.threads[i].getDownLength() == -1){//如果下载失败,再重新下载
							this.threads[i] = new DownloadThread(this, url, this.saveFile, this.block, this.downloadSize, i+1, listener);
							this.threads[i].setPriority(7);
							this.threads[i].start();
						}
					}
				}				
				if(listener!=null) listener.onDownloadSize(this.downloadSize);//通知目前已经下载完成的数据长度
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
	 * 获取Http响应头字段
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
	 * 打印Http头字段
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

package com.chaoxing.download;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ListView;
import com.chaoxing.video.R;

public class FileDownloadActivity extends Activity {
	private final static String TAG = "fileDownload";
	private ListView downloadListView;
	private List<Map<String, Object>> downloadList;
	private DownloadBroadcastReceiver receiver = null;
	private DownloadListViewAdapter downloadAdapter = null;
	private IntentFilter filter ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_download);
		//´´½¨²¢×¢²áBroadcastReceiver
		receiver = new DownloadBroadcastReceiver();
		filter = new IntentFilter();
		filter.addAction("com.chaoxing.download.DownloadBroadcastReceiver");
		registerReceiver(receiver, filter);
		
		downloadListView = (ListView) findViewById(R.id.lv_download_file_list);
		downloadList = new ArrayList<Map<String, Object>>();
		
		Intent intent = this.getIntent();
		if(intent != null){
			Bundle bundle = intent.getExtras();
			if(bundle != null){
				bundle.putInt("op", 0);
				download(bundle);
			}
		}
	}
	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}
	private void download(Bundle bundle) {
//		Log.i(TAG, "FileDownloadActivity download-----");
		Intent serviceIntent = new Intent("com.chaoxing.download.FileService");
		serviceIntent.putExtras(bundle);
		startService(serviceIntent);
	}

	public class DownloadBroadcastReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals("com.chaoxing.download.DownloadBroadcastReceiver")){
				Bundle bundle = intent.getExtras();
				if(bundle.getInt("type") == 1){
					downloadList = (List<Map<String, Object>>) bundle.getSerializable("downloadList");
					downloadAdapter = new DownloadListViewAdapter(FileDownloadActivity.this, downloadList);
					downloadListView.setAdapter(downloadAdapter);
				}else if(bundle.getInt("type") == 0){
					int threadId = bundle.getInt("threadId");
					int status = bundle.getInt("status");
					if(status == 0){
						downloadList.remove(threadId);
						if(downloadList.isEmpty() == true){
							Intent serviceIntent = new Intent("com.chaoxing.download.FileService");
							stopService(serviceIntent);
						}
					}
					int progress = bundle.getInt("progress");
					String speed = bundle.getString("speed");
					int fileSize = bundle.getInt("fileSize");
//					Log.i(TAG, "threadId+++++++++++++++++++++++++ " + threadId);
					downloadList.get(threadId).remove("progress");
					downloadList.get(threadId).put("progress", progress);
					downloadList.get(threadId).remove("speed");
					downloadList.get(threadId).put("speed", speed);
					downloadList.get(threadId).remove("fileSize");
					downloadList.get(threadId).put("fileSize", fileSize);
					downloadList.get(threadId).remove("status");
					downloadList.get(threadId).put("status", status);
					downloadAdapter.notifyDataSetChanged();
	//				Log.i(TAG, "download progress " + progress);
	//				Log.i(TAG, "fileSize" + fileSize);
				}
			}			
		}
	}

}

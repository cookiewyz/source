package com.chaoxing.download;

import java.util.List;
import java.util.Map;

import com.chaoxing.video.fayuan.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DownloadListViewAdapter extends BaseAdapter {

	private Context context;
	private List<Map<String, Object>> listItems;
	private LayoutInflater listContainer;
	
	public final class ListItemView{
		public TextView tvVideoName;
		public ProgressBar pbDownloadProgress;
		public TextView tvDownloadProgress;
		public TextView tvDownloadStatus;
		public TextView tvDownloadFileSize;
		public TextView tvDownloadSpeed;
	}
	
	public DownloadListViewAdapter(Context context, List<Map<String, Object>>listItems){
		this.context = context;
		listContainer = LayoutInflater.from(context);
		this.listItems = listItems;
	}
	@Override
	public int getCount() {
		return listItems.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int selectID = position;
		ListItemView listItemView = null;
		if(convertView == null){
			listItemView = new ListItemView();
			convertView = listContainer.inflate(R.layout.file_download_item, null);
			listItemView.tvVideoName = (TextView)convertView.findViewById(R.id.tv_download_item_video_name);
			listItemView.pbDownloadProgress = (ProgressBar)convertView.findViewById(R.id.pb_download_item_progress);
			listItemView.tvDownloadProgress = (TextView)convertView.findViewById(R.id.tv_download_item_progress);
			listItemView.tvDownloadStatus = (TextView)convertView.findViewById(R.id.tv_download_item_status);
			listItemView.tvDownloadFileSize = (TextView)convertView.findViewById(R.id.tv_download_item_file_size);
			listItemView.tvDownloadSpeed = (TextView)convertView.findViewById(R.id.tv_download_item_speed);
			convertView.setTag(listItemView);
		}else{
			listItemView = (ListItemView)convertView.getTag();
		}
		int progress = (Integer) listItems.get(selectID).get("progress");
		int fileSize = (Integer) listItems.get(selectID).get("fileSize");
		double percent = 0;
		if(fileSize != 0){
			percent = (double)progress / (double)fileSize * 100;
			listItemView.tvDownloadProgress.setText(String.format("%.2f%%", percent));
		}else{
			listItemView.tvDownloadProgress.setText("0.00%");
		}
		listItemView.pbDownloadProgress.setProgress((int)(percent*100));
		listItemView.tvVideoName.setText((String) listItems.get(selectID).get("videoName"));
		listItemView.tvDownloadFileSize.setText(String.format("%.2fMB", (double)fileSize/1024/1024));
		int status =  (Integer) listItems.get(selectID).get("status");
		switch(status){
		case 0:
			listItemView.tvDownloadStatus.setText("完成");
			break;
		case 1:
			listItemView.tvDownloadStatus.setText("下载中");
			break;
		case 2:
			listItemView.tvDownloadStatus.setText("暂停");
			break;
		case 3:
			listItemView.tvDownloadStatus.setText("出错");
			break;
		case 4:
			listItemView.tvDownloadStatus.setText("等待");
			break;
		default:
			break;			
		}
		listItemView.tvDownloadSpeed.setText((String) listItems.get(selectID).get("speed"));
		
		return convertView;
	}

}

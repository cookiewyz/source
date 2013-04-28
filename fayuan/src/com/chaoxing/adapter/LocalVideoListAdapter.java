package com.chaoxing.adapter;

import java.util.List;
import java.util.Map;
import com.chaoxing.database.SSVideoLocalVideoBean;
import com.chaoxing.video.fayuan.R;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LocalVideoListAdapter extends BaseAdapter {
	private List<Map<String, Object>> listItems;
	private LayoutInflater listContainer;
	
	public final class ListItemView{
		public ImageView ivLocalVideoCover;
		public ImageView ivLocalVideoDel;
		public ProgressBar pbDownloadProgress;
		public TextView tvDownloadSize;
		public TextView tvLocalVideoTitle;
		public TextView tvDownloadStatus;
		public LinearLayout llDownloadStatus;
//		public TextView tvDownloadSpeed;
		public RelativeLayout downloadLayout;
	}
	
	public LocalVideoListAdapter(Context context, List<Map<String, Object>>listItems){
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
			convertView = listContainer.inflate(R.layout.local_img_text_view, null);
			listItemView.ivLocalVideoCover = (ImageView)convertView.findViewById(R.id.iv_local_video_img);
			listItemView.ivLocalVideoDel = (ImageView) convertView.findViewById(R.id.iv_local_video_img_del);
			listItemView.pbDownloadProgress = (ProgressBar)convertView.findViewById(R.id.pb_download_progress);
			listItemView.tvDownloadSize = (TextView)convertView.findViewById(R.id.tv_download_size);
			listItemView.tvLocalVideoTitle = (TextView)convertView.findViewById(R.id.tv_local_video_title);
			listItemView.downloadLayout = (RelativeLayout) convertView.findViewById(R.id.rl_download_progress);
			listItemView.llDownloadStatus = (LinearLayout) convertView.findViewById(R.id.ll_download_status);
//			listItemView.tvDownloadSpeed = (TextView) convertView.findViewById(R.id.tv_download_speed);
			listItemView.tvDownloadStatus = (TextView) convertView.findViewById(R.id.tv_download_status);
			convertView.setTag(listItemView);
//			Log.i(TAG, "convertView is null......................................");
		}else{
			listItemView = (ListItemView)convertView.getTag();
		}
		SSVideoLocalVideoBean localVideoBean = (SSVideoLocalVideoBean) listItems.get(selectID).get("localVideoBean");
		int progress = 0;
		if(listItems.get(selectID).get("progress") != null)
			progress = (Integer) listItems.get(selectID).get("progress");
		int fileLen = 0;
		if(listItems.get(selectID).get("fileLen") != null)
			fileLen = (Integer) listItems.get(selectID).get("fileLen");
		
		if((String) listItems.get(selectID).get("videoCover") != null)
			listItemView.ivLocalVideoCover.setImageURI(Uri.parse((String) listItems.get(selectID).get("videoCover")));
		if((Integer) listItems.get(selectID).get("delImg") != null){
			listItemView.ivLocalVideoDel.setImageResource( (Integer) listItems.get(selectID).get("delImg"));
		}

		double percent = 0;
		if(fileLen != 0)
			percent = (double)progress / (double)fileLen * 100;
		listItemView.pbDownloadProgress.setProgress((int)(percent*100));
//		listItemView.tvLocalVideoTitle.setText((String) listItems.get(selectID).get("videoTitle"));
		String titleText = localVideoBean.getStrVideoFileName();
		if(localVideoBean.getStrSpeaker() != null){
			titleText += "——" + localVideoBean.getStrSpeaker();
		}
		listItemView.tvLocalVideoTitle.setText(titleText);
		
		// 小于1M按K显示，大于1M按M显示
		if(progress < (2<<19)){
			listItemView.tvDownloadSize.setText(String.format("%.2fKB", (double)progress/1024));
		}else{
			listItemView.tvDownloadSize.setText(String.format("%.2fMB", (double)progress/1024/1024));
		}
		
		int status =  (Integer) listItems.get(selectID).get("status");
		switch(status){
		case 0:
			listItemView.downloadLayout.setVisibility(View.GONE);
			listItemView.llDownloadStatus.setVisibility(View.GONE);
			break;
		case 1:
			listItemView.downloadLayout.setVisibility(View.VISIBLE);
			listItemView.llDownloadStatus.setVisibility(View.VISIBLE);
			int speed = 0;
			
			if(listItems.get(selectID).get("speed") != null)
				speed = (Integer) listItems.get(selectID).get("speed");
			listItemView.tvDownloadStatus.setText(String.format("正在下载中…  %.2fKB/S", (double)speed / 1024));
			break;
		case 2:
			listItemView.downloadLayout.setVisibility(View.VISIBLE);
			listItemView.llDownloadStatus.setVisibility(View.VISIBLE);
			listItemView.tvDownloadStatus.setText("暂停");
			break;
		case 3:
			listItemView.downloadLayout.setVisibility(View.VISIBLE);
			listItemView.llDownloadStatus.setVisibility(View.VISIBLE);
			break;
		case 4:
			listItemView.downloadLayout.setVisibility(View.VISIBLE);
			listItemView.llDownloadStatus.setVisibility(View.VISIBLE);
			break;
		default:
			break;			
		}
//		listItemView.tvDownloadSpeed.setText((String) listItems.get(selectID).get("speed"));
		
		return convertView;
	}
	
	


}

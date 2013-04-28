package com.chaoxing.adapter;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.chaoxing.document.ParseFaYuanData;
import com.chaoxing.document.SeriesInfo;
import com.chaoxing.video.CoverService;
import com.chaoxing.video.fayuan.R;
import com.chaoxing.video.SsvideoActivity.CoverHandler;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * @Author yueguang.xin
 * @version 2013-1-18
 */

public class CategoryVideoAdapter extends BaseAdapter {

	private class VideoCellHolder {
		ImageView videoCover;
		RatingBar videoStars;
		TextView vieoName;
		TextView videoSpeeker;
		TextView videoScore;
		TextView vieoCate;
		TextView videoDetail;
		TextView videoMore;
	}

	private Context context;
	private ParseFaYuanData mData;
	private ArrayList<SeriesInfo> listSer = new ArrayList<SeriesInfo>();
	private LayoutInflater mInflater;
	private SeriesInfo serMore;
	private int iMaxDataCount;
	private int iPerPageCount;
	private CoverHandler handler;

	public CategoryVideoAdapter(Context c) {
		super();
		this.context = c;
		serMore = new SeriesInfo();
		serMore.setTitle("获取更多视频...");
		serMore.setSerid("more");
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		iMaxDataCount = 0;
		iPerPageCount = 20;
		mData = ParseFaYuanData.getInstance();
	}

	public void clearListData() {
		this.listSer.clear();
	}
	
	public ArrayList<SeriesInfo> getListData() {
		return this.listSer;		
	}
	
	public void setList(List<SeriesInfo> list) {
		this.listSer.addAll(list);
		this.listSer.add(serMore);
	}
	
	public void addList(List<SeriesInfo> lst) {
		
		if(listSer != null && listSer.size() > 0){
			int iSize = listSer.size();
			SeriesInfo serinfo = listSer.get(iSize-1);
			if(serinfo.getSerid().equals("more")){
				listSer.remove(iSize-1);
//				Log.d("Delete(serMore) size:", ""+listSer.size());
			}
		}
		this.listSer.addAll(lst);
		this.listSer.add(serMore);
//		Log.d("add(serMore) size:", ""+listSer.size());
	}
	
	public void setHandler(CoverHandler hdr) {
		handler = hdr;
	}
	
	public int getPageCount() {
		int ipage = 1;
		if(listSer != null && listSer.size() > 0){
			ipage = (listSer.size()/iPerPageCount);
		}
		return ipage;
	}
	
	public int getCount() {
		// TODO Auto-generated method stub
		return listSer.size();
	}
	
	public void setPerPageCount(int ict) {
		this.iPerPageCount = ict;
	}
	
	public void setMaxDataCount(int ict) {
		this.iMaxDataCount = ict;
	}

	public int getMaxDataCount() {
		return this.iMaxDataCount;
	}
	
	@Override
	public Object getItem(int index) {

		return listSer.get(index);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}
	
	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		VideoCellHolder vCell = null;
		SeriesInfo info = listSer.get(index);
		if(!info.getSerid().equals("more")){
			
			if (convertView == null) {   
				convertView = mInflater.inflate(R.layout.categorycell, null); //categorycell 
				vCell = new VideoCellHolder();
				vCell.videoCover = (ImageView)convertView.findViewById(R.id.video_cover);
				vCell.videoStars = (RatingBar)convertView.findViewById(R.id.star_Rating_Bar);
				vCell.videoStars.setIsIndicator(true);
				vCell.videoStars.setMax(5);//StepSize(Float.valueOf("0.01"));
				vCell.videoStars.setStepSize(Float.valueOf("0.01"));
				vCell.videoScore = (TextView)convertView.findViewById(R.id.star_score);
				vCell.vieoName = (TextView)convertView.findViewById(R.id.video_name);
				vCell.videoSpeeker = (TextView)convertView.findViewById(R.id.key_speeker);
				vCell.vieoCate = (TextView)convertView.findViewById(R.id.cate_name);
				vCell.videoDetail = (TextView)convertView.findViewById(R.id.video_detail);
							
				convertView.setTag(vCell); 
				convertView.setId(0);
	
			}else{
				if(convertView.getId() == 0){
					vCell = (VideoCellHolder) convertView.getTag();
				}
				else{
					convertView = mInflater.inflate(R.layout.categorycell, null); //categorycell 
					vCell = new VideoCellHolder();
					vCell.videoCover = (ImageView)convertView.findViewById(R.id.video_cover);
					vCell.videoStars = (RatingBar)convertView.findViewById(R.id.star_Rating_Bar);
					vCell.videoStars.setIsIndicator(true);
					vCell.videoStars.setMax(5);//StepSize(Float.valueOf("0.01"));
					vCell.videoStars.setStepSize(Float.valueOf("0.01"));
					vCell.videoScore = (TextView)convertView.findViewById(R.id.star_score);
					vCell.vieoName = (TextView)convertView.findViewById(R.id.video_name);
					vCell.videoSpeeker = (TextView)convertView.findViewById(R.id.key_speeker);
					vCell.vieoCate = (TextView)convertView.findViewById(R.id.cate_name);
					vCell.videoDetail = (TextView)convertView.findViewById(R.id.video_detail);
								
					convertView.setTag(vCell); 
					convertView.setId(0);
				}
			}
			
			if (info != null) {   
				vCell.vieoName.setText(info.getTitle());
				if (info.getScore() != null && info.getScore()!= "") {
					float fScore = Float.valueOf(info.getScore().trim()).floatValue();
					vCell.videoScore.setText(String.format("%.2f分", fScore));
					vCell.videoStars.setRating(Float.valueOf(String.format("%.2f", fScore)));
				}
			
				if (info.getKeySpeaker() != null && !info.getKeySpeaker().equals("")) {
					vCell.videoSpeeker.setText("主讲人：    "+info.getKeySpeaker());
				} else {
					//vCell.videoSpeeker.setText("年代：    "+info.getDate());
					vCell.videoSpeeker.setVisibility(View.GONE);
				}
				      
				
				
				//if (AppConfig.ORDER_ONLINE_VIDEO) {
					vCell.videoDetail.setText("分类编号：  " + info.getSerid());
					vCell.vieoCate.setText("学校名称：  " + info.getSchoolName());
				/*} else {
					vCell.vieoCate.setText("所属分类："+info.getSubject());
					String sAbstract = info.getAbstracts();
					try {
						sAbstract = getPartString(info.getAbstracts(), 485);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//if(sAbstract.length() > 254)
					//	sAbstract = sAbstract.substring(0, 254)+" ...";
					vCell.videoDetail.setText(sAbstract);
				}*/
		
				 Bitmap cover = null;
				 if (CoverService.context != null) {//add_by_xin_2013-1-18.
					 if(handler != null) {
						Message msg = handler.obtainMessage(CoverHandler.COVER_CATEGORY_OK);
					 	cover = (Bitmap) ((CoverService) CoverService.context).getCover(info.getCover(), msg);
					 }
				 }
				 if (cover != null) {
					 vCell.videoCover.setImageBitmap(cover);
				 } 
				 else {
					 vCell.videoCover.setImageResource(R.drawable.all_default_cover);
				 }
				
			}
		}
		else {
//			Log.d("视频分类列表", "more index:"+index);
			vCell = new VideoCellHolder();
			
			convertView = mInflater.inflate(R.layout.categorycellmore, null);  
			vCell.videoMore = (TextView)convertView.findViewById(R.id.txt_more);
			vCell.videoMore.setText("获取更多视频...");//设置TextView的文本信
			//vCell.vieoName.setBackgroundResource(R.drawable.all_category_button_xml);
			convertView.setTag(vCell);
			int iNextPage = (index/iPerPageCount)+1;
			convertView.setId(iNextPage);
			if(iMaxDataCount <= iPerPageCount || index >= iMaxDataCount){
				//vCell.videoMore.setEnabled(false);
				//convertView.setEnabled(false);
				vCell.videoMore.setText("已没有更多视频");
				//vCell.videoMore.setTextColor(color.blue_toolbar);//#F0F0F0,color.gray
				convertView.setId(-1);
			}
				
		}
		return convertView;
	}

    private static String getPartString(String str, int len) throws UnsupportedEncodingException {
    	if(str == null) {
    		return "";
    	}
    	if(str.equals("")) {
    		return str;
    	}
		byte b[];
		int counterOfDoubleByte = 0;
		b = str.getBytes("GBK");
		if (b.length <= len) {
			return str;
		}
		for (int i = 0; i < len; i++) {
			if (b[i] < 0) {
				counterOfDoubleByte++;
			}
		}
		if (counterOfDoubleByte % 2 == 0) {
			return new String(b, 0, len, "GBK") + "...";
		} else {
			return new String(b, 0, len - 1, "GBK") + "...";
		}
	}
    
}

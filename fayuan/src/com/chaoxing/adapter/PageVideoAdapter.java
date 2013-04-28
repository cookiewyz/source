package com.chaoxing.adapter;

import java.io.InputStream;
import java.util.ArrayList;
import com.chaoxing.document.SeriesInfo;
import com.chaoxing.httpservice.HttpRequest;
import com.chaoxing.video.fayuan.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * @Author yueguang.xin
 * @version 2011-8-26
 */
public class PageVideoAdapter extends BaseAdapter {

	private class VideoCellHolder {
		ImageView videoCover;
		RatingBar videoStars;
		TextView vieoName;
		TextView videoScore;
	}

	private Context context;

	private ArrayList<SeriesInfo> list = new ArrayList<SeriesInfo>();
	private ArrayList<Bitmap> listCover;
	private LayoutInflater mInflater;

	public PageVideoAdapter(Context c) {
		super();
		this.context = c;
		listCover = new ArrayList<Bitmap>();
		
	}

	public void setList(ArrayList<SeriesInfo> lst) {
		//this.list = list;
		this.list.addAll(lst);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void setCoverList(ArrayList<Bitmap> lst) {
		//this.listCover = lst;
		this.listCover.addAll(lst);
	}
	
	public void AddCover(Bitmap bmp) {
		this.listCover.add(bmp);
	}
	
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int index) {

		return list.get(index);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}
	
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}
	
	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		VideoCellHolder vCell;
		if (convertView == null) {   
			convertView = mInflater.inflate(R.layout.pagevideocell, null);   
			vCell = new VideoCellHolder();
			vCell.videoCover = (ImageView)convertView.findViewById(R.id.video_cover);
			vCell.videoStars = (RatingBar)convertView.findViewById(R.id.star_Rating_Bar);
			vCell.videoStars.setIsIndicator(true);
			vCell.videoStars.setMax(5);//StepSize(Float.valueOf("0.01"));
			vCell.videoStars.setStepSize(Float.valueOf("0.01"));
			vCell.videoScore = (TextView)convertView.findViewById(R.id.star_score);
			vCell.vieoName = (TextView)convertView.findViewById(R.id.video_name);
			
			// 给ImageView设置资源
			//imageView = new ImageView(mContext);
			// 设置布局 图片120×120显示
			//imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
			// 设置显示比例类型
			//imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
						
			convertView.setTag(vCell);   

		}else{
			vCell = (VideoCellHolder) convertView.getTag();   
		}
		SeriesInfo info = list.get(index);
		if (info != null) {   
			vCell.vieoName.setText(info.getTitle()+" "+info.getKeySpeaker());
			float fScore = Float.valueOf(info.getScore().trim()).floatValue();
			vCell.videoScore.setText(String.format("%.2f分", fScore));
			vCell.videoStars.setRating(Float.valueOf(String.format("%.2f", fScore)));
			
			//new CoverThread(vCell, info.getCover()).start();
			if(listCover != null && listCover.size() > 0){
	        	Bitmap bp = listCover.get(index);	        		        	
	        	if(bp != null)
	        	{
	        		vCell.videoCover.setImageBitmap(bp);
	        		
	        		//new CoverThread2(vCell, info.getCover(), bp).start();
	        	}else {
	        		new CoverThread(vCell, info.getCover()).start();			
				}
	        }
			
		}
		return convertView;
		
	}
	
	//后台线程类   
    class CoverThread extends Thread   
    {   
    	VideoCellHolder activity;
    	String sCoverUrl;
        public CoverThread(VideoCellHolder activity, String surl) {     
            this.activity = activity;
            this.sCoverUrl = surl;
        }
        
        @Override  
        public void run() {     
  
        	activity.videoCover.post(new Runnable() {     

                @Override  
                public void run() {   
                    // TODO Auto-generated method stub   
                	final HttpRequest req = new HttpRequest();
          			req.setRequestUrl(sCoverUrl);
					new Thread() {
						@Override
						public void run() {
							req.getImageRequest();
						}
					}.start();
          			Bitmap bp = req.getBitmapData();
          			if(bp != null){
          				activity.videoCover.setImageBitmap(bp);
          				notifyDataSetChanged();
          			}
                }   
            });     
            super.run();   
        }
    }      
    
  //后台线程类   
    class CoverThread2 extends Thread   
    {   
    	VideoCellHolder activity;
    	String sCoverUrl;
    	Bitmap bitmap;
        public CoverThread2(VideoCellHolder activity, String surl, Bitmap bp) {     
            this.activity = activity;
            this.sCoverUrl = surl;
            this.bitmap = bp;
        }
        
        @Override  
        public void run() {     
  
        	activity.videoCover.post(new Runnable() {     

                @Override  
                public void run() {   
                    // TODO Auto-generated method stub 
                	if(bitmap == null){
                		HttpRequest req = new HttpRequest();
                		req.setRequestUrl(sCoverUrl);
                		req.getImageRequest();
                		Bitmap bp = req.getBitmapData();
                		if(bp != null)
                			activity.videoCover.setImageBitmap(bp);
                	}
                	else
                	{
                		//activity.videoCover.setImageBitmap(null);
                		activity.videoCover.setImageBitmap(bitmap);
                	}
                }   
            });     
            super.run();   
        }
    }
    
    public static Bitmap readBitMap(Context context, int resId){         

    	BitmapFactory.Options opt = new BitmapFactory.Options();         
    	opt.inPreferredConfig = Bitmap.Config.RGB_565;         
    	opt.inPurgeable = true;         
    	opt.inInputShareable = true;         
    	//获取资源图片         
    	InputStream is = context.getResources().openRawResource(resId);         
    	return BitmapFactory.decodeStream(is, null, opt);     
    }  

}

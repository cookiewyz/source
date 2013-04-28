package com.chaoxing.adapter;

import java.util.ArrayList;

import com.chaoxing.video.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @Author yueguang.xin
 * @version 2013-3-21.
 */
public class GalleryAdapter extends BaseAdapter {

	private class cellHolder {
		ImageView imgCover;
		Button btnPlay;
		TextView txtDetail;
	}

	private Context context;
	private int itemIndex;
	private ArrayList<Bitmap> listCover;
	private LayoutInflater mInflater;
	private GalleryProvider provider = null;
	private boolean screenLand = false;
	
	public GalleryAdapter(Context c) {
		super();
		this.context = c;
		itemIndex = 0;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void setProvider(GalleryProvider pv) {
		provider = pv;
	}
	
	public void setScreenOrientation(boolean isLandscape) {
		screenLand = isLandscape;
	}
	
	public void setList(ArrayList<Bitmap> lst) {
		this.listCover = lst;
	}
	
	public void AddCover(Bitmap bmp) {
		this.listCover.add(bmp);
	}
	
	public int getCount() {
		// TODO Auto-generated method stub
		return Integer.MAX_VALUE;
	}

	@Override
	public Object getItem(int index) {
		return index;
	}

	@Override
	public long getItemId(int index) {
		return index;
	}
	
	public int getItemIndex() {
		// TODO Auto-generated method stub
		return this.itemIndex;
	}
	
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}
	
	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		cellHolder vCell;
		if (convertView == null || screenLand) {
			if(!screenLand) {
				convertView = mInflater.inflate(R.layout.gallery_item, null);
			}
			else {
				convertView = mInflater.inflate(R.layout.gallery_item_land, null);
			}
			vCell = new cellHolder();
			vCell.imgCover  = (ImageView)convertView.findViewById(R.id.img_back);
			vCell.btnPlay   = (Button)convertView.findViewById(R.id.btndetailplay);
			vCell.txtDetail = (TextView)convertView.findViewById(R.id.txtDetail);
			convertView.setTag(vCell);
		}
		else {
			vCell = (cellHolder) convertView.getTag();
		}
		
		if(listCover != null && listCover.size() > 0) {
			itemIndex = index%listCover.size();
        	Bitmap bp = listCover.get(itemIndex);
        	if(bp != null)
        		vCell.imgCover.setImageBitmap(bp);
        }

		String detail = "";
		if(provider != null) {
			detail = provider.onGetCoverDetail(itemIndex, screenLand);			
		}
		vCell.txtDetail.setText(detail);
		
		vCell.btnPlay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(provider != null) {
					provider.onItemPlayClicked(itemIndex);			
				}
			}
		});
		return convertView;
	}
	
	public interface GalleryProvider {
		int onItemPlayClicked(int index);//add_by_xin_2013-3-15.
		String onGetCoverDetail(int index, boolean isLand);//add_by_xin_2013-3-15.
	}

}

package com.chaoxing.adapter;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * @Author yueguang.xin
 * @version 2011-8-26
 */
public class GalleryVideoAdapter extends BaseAdapter {

	//private int gItemBg;
	private int iItemIndex;
    private Context mContext;

	private ArrayList<Bitmap> list;

	public GalleryVideoAdapter(Context c) {
		super();
		this.mContext = c;
		/* 使用在res/values/attrs.xml中的<declare-styleable>定义  Gallery属性.
	      TypedArray a = mContext.obtainStyledAttributes(R.styleable.Gallery);

	      //取得Gallery属性的Index id
	      gItemBg = a.getResourceId(R.styleable.Gallery_android_galleryItemBackground, 0);

	      //让对象的styleable属性能够反复使用 
	      a.recycle();//*/
	      iItemIndex = 0;
	}

	public void setList(ArrayList<Bitmap> list) {
		this.list = list;
	}
	
	public int getCount() {
		// TODO Auto-generated method stub
		return Integer.MAX_VALUE;
	}
	
	public int getItemIndex() {
		// TODO Auto-generated method stub
		return this.iItemIndex;
	}

	@Override
	public Object getItem(int position) {

		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
        ImageView image = new ImageView(mContext);

        // 设置图片给ImageView对象
        //image.setImageResource(imgIds[position%imgIds.length]);//求余 -实现循环播放 
        // 重新设置图片的宽高
        image.setScaleType(ImageView.ScaleType.FIT_XY);//FIT_CENTER,

        // 重新设置Layout的宽高
        //image.setLayoutParams(new Gallery.LayoutParams(768,350));//150,120
        //设置Gallery背景图
        //image.setBackgroundResource(gItemBg);
        if(list != null && list.size() > 0){
        	iItemIndex = position%list.size();
        	Bitmap bp = list.get(position%list.size());
        	if(bp != null)
        		image.setImageBitmap(bp);
        }
        return image;
	}

}

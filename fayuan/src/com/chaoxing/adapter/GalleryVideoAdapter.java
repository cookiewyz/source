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
		/* ʹ����res/values/attrs.xml�е�<declare-styleable>����  Gallery����.
	      TypedArray a = mContext.obtainStyledAttributes(R.styleable.Gallery);

	      //ȡ��Gallery���Ե�Index id
	      gItemBg = a.getResourceId(R.styleable.Gallery_android_galleryItemBackground, 0);

	      //�ö����styleable�����ܹ�����ʹ�� 
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

        // ����ͼƬ��ImageView����
        //image.setImageResource(imgIds[position%imgIds.length]);//���� -ʵ��ѭ������ 
        // ��������ͼƬ�Ŀ��
        image.setScaleType(ImageView.ScaleType.FIT_XY);//FIT_CENTER,

        // ��������Layout�Ŀ��
        //image.setLayoutParams(new Gallery.LayoutParams(768,350));//150,120
        //����Gallery����ͼ
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

package com.chaoxing.video;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author xin_yueguang
 * @version 2011-8-18.
 */
public class PageButton extends FrameLayout {

	private int itemId=-1;
	
	private OnClick click ;
	
	private Drawable icon;
	
	private String label;	
	
	private ImageView imageButton;
	private TextView textView;
	
	boolean selected;
	
	public PageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.TabButton);

		itemId= a.getInteger(R.styleable.TabButton_itemId, -1);
				
		icon = a.getDrawable(R.styleable.TabButton_icon);		
		
		label = a.getString(R.styleable.TabButton_label);		
		
		selected  = a.getBoolean(R.styleable.TabButton_selected, false);

		a.recycle();
		
		init();
	}

	public PageButton(Context context) {
		super(context);		
		
		init();
	}
	
	private class OnClick implements OnClickListener{		
		@Override
		public void onClick(View v) {
			getPageButtonHost().fireSelectedEvent(itemId, PageButton.this);		
		}
	}
	
	private void init(){
		if(click==null){
			click = new OnClick();
		}
		
		setOnClickListener(click);
		
		LayoutInflater inflater = LayoutInflater.from(getContext());
		inflater.inflate(R.drawable.page_button_host_xml, this);
		
		if(icon!=null){
			imageButton = (ImageView)findViewById(R.id.page_button_icon);
			imageButton.setImageDrawable(icon);
		}
		
		if(label!=null){
			textView = (TextView)findViewById(R.id.page_button_label);
			textView.setText(label);
			textView.setTextSize((float)16.0);
		}
	}
	
	PageButtonHost getPageButtonHost(){
		ViewParent parent = getParent();
		if(parent instanceof PageButtonHost){
			return	((PageButtonHost)parent);
		}else{
			throw new IllegalStateException("PageButton should be in a PageButtonHost.");
		}
	}
}

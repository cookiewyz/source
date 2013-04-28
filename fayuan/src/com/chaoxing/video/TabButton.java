package com.chaoxing.video;

import com.chaoxing.video.fayuan.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
//import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;
//import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author <a href="mailto:hb562100@163.com">HeBo</a>
 * @version 2011-6-17
 */
public class TabButton extends FrameLayout {

	private int itemId=-1;
	
	private OnClick click ;
	
	private Drawable icon;
	
	private String label;	
	
	private ImageView imageButton;
	private TextView textView;
	
	boolean selected;
	
	public TabButton(Context context, AttributeSet attrs) {
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

	public TabButton(Context context) {
		super(context);		
		
		init();
	}
	
	
	private class OnClick implements OnClickListener{		
		@Override
		public void onClick(View v) {
			getTabHost().fireSelectedEvent(itemId,TabButton.this);		
		}
	}
	
	private void init(){
		if(click==null){
			click = new OnClick();
		}
		
		setOnClickListener(click);
		
		LayoutInflater inflater = LayoutInflater.from(getContext());		
		inflater.inflate(R.drawable.main_bottom_tabhost_xml, this);
		
		if(icon!=null){
			imageButton = (ImageView)findViewById(R.id.tab_button_icon);
			imageButton.setImageDrawable(icon);
		}
		
		if(label!=null){
			textView = (TextView)findViewById(R.id.tab_button_label);						
			textView.setText(label);
		}
	}
	
	TabHost getTabHost(){
		ViewParent parent = getParent();
		if(parent instanceof TabHost){
			return	((TabHost)parent);
		}else{
			throw new IllegalStateException("TabButton should be in a TabHost.");
		}
	}
}

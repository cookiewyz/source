package com.chaoxing.video;

import com.chaoxing.video.fayuan.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

/**
 * @author <a href="mailto:hb562100@163.com">HeBo</a>
 * @version modify by xin 2011-8-9.
 */
public class TabHost extends LinearLayout {

	OnSelectedListener onSelectedListener ;
	
	private View selected;
	
	public TabHost(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TabHost(Context context) {
		super(context);
	}
	
	void fireSelectedEvent(int itemId,View selected){
		if(this.selected==selected){
			return ;
		}
		
		selected(selected);		
		
		if(onSelectedListener!=null){
			onSelectedListener.onSelected(itemId);
		}
	}	
	
	private void selected(View selected){
		if(this.selected!=null){
			this.selected.setBackgroundDrawable(null);
		}
		
		this.selected = selected;
		
		this.selected.setBackgroundResource(R.drawable.tab_bg_selected_xml);// modify by xin 2011-8-9
	}
	
	public interface OnSelectedListener{		
		void onSelected(int itemId);
	}
	
	public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
		this.onSelectedListener = onSelectedListener;
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		int c = getChildCount();
		for(int i=0;i<c;i++){
			View v= getChildAt(i);
			
			if(v instanceof TabButton){
				TabButton tb = (TabButton)v;
				if(tb.selected){
					selected(tb);
				}
			}else{
				throw new IllegalStateException("TabHost can't contain the other views except TabButton.");
			}
		}
	}
	
	public void unSelected(){
		if(this.selected != null){
			this.selected.setBackgroundDrawable(null);
			this.selected = null;
		}
	}
}

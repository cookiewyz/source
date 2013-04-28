package com.chaoxing.video;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;



/**
 * @author xin_yueguang
 * @version 2011-8-18.
 */
public class PageButtonHost extends LinearLayout {

	OnPageSelectedListener OnPageSelectedListener ;
	
	private View selected;
	private int selectedId = 0;
	
	public PageButtonHost(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PageButtonHost(Context context) {
		super(context);
	}
	
	void fireSelectedEvent(int itemId, View selected){
		if(this.selected==selected){
			return;
		}
		selectedId = itemId;
		selected(selected);		
		
		if(OnPageSelectedListener!=null){
			OnPageSelectedListener.onPageSelected(itemId);
		}
	}	
	
	private void selected(View selected){
		if(this.selected!=null){
			this.selected.setBackgroundResource(R.drawable.first_page_subject);
		}
		
		this.selected = selected;
		this.selected.setBackgroundResource(R.drawable.first_page_subject_selected);// modify by xin 2011-8-18
	}
	
	public interface OnPageSelectedListener{		
		void onPageSelected(int itemId);
	}
	
	public void setOnSelectedListener(OnPageSelectedListener onSelectedListener) {
		this.OnPageSelectedListener = onSelectedListener;
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		int c = getChildCount();
		for(int i=0; i<c; i++){
			View v= getChildAt(i);
			
			if(v instanceof PageButton){
				PageButton tb = (PageButton)v;
				if(tb.selected){
					selected(tb);
				}
			}else{
				throw new IllegalStateException("PageButtonHost can't contain the other views except PageButton.");
			}
		}
	}
	
	public void unSelected(){
		if(this.selected!=null){
			this.selected.setBackgroundResource(R.drawable.first_page_subject);
		}
	}
	
	public int getSelectedItem(){
		return this.selectedId;
	}
}

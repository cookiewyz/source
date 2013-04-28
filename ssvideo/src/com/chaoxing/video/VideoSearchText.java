package com.chaoxing.video;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class VideoSearchText extends EditText {

	OnKeyboardListener onKeyboardListener;
	
	public VideoSearchText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public VideoSearchText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public VideoSearchText(Context context) {
		super(context);
	}
	
	@Override
	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK 
				&& onKeyboardListener!=null){
			onKeyboardListener.onBackIME();
		}
		return false;
	}

	public void setOnKeyboardListener(OnKeyboardListener onKeyboardListener) {
		this.onKeyboardListener = onKeyboardListener;		
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if( (keyCode==KeyEvent.KEYCODE_SEARCH||keyCode==KeyEvent.KEYCODE_ENTER) 
				&& VideoSearchText.this.onKeyboardListener!=null){
			VideoSearchText.this.onKeyboardListener.onAction(VideoSearchText.this);
		}
		
		return super.onKeyUp(keyCode, event);
	}
	
	public interface OnKeyboardListener{
		void onAction(VideoSearchText text);
		void onBackIME();
	}
}

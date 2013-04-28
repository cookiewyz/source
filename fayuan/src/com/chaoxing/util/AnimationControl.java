package com.chaoxing.util;

import com.chaoxing.video.fayuan.R;

import android.content.Context;
import android.os.SystemClock;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;

public class AnimationControl {

	private static final int ANIMATION_DURATION = 400;
	
    //for the previous movement
    public static Animation inFromRightAnimation() {
        Animation inFromRight = new TranslateAnimation(
        Animation.RELATIVE_TO_PARENT,  +1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
        Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
        );
        inFromRight.setDuration(ANIMATION_DURATION);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
	}
    public static Animation outToLeftAnimation() {
        Animation outtoLeft = new TranslateAnimation(
         Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  -1.0f,
         Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
        );
        outtoLeft.setDuration(ANIMATION_DURATION);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
	}    
    // for the next movement
    public static Animation inFromLeftAnimation() {
        Animation inFromLeft = new TranslateAnimation(
        Animation.RELATIVE_TO_PARENT,  -1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
        Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
        );
        inFromLeft.setDuration(ANIMATION_DURATION);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        return inFromLeft;
	}
    public static Animation outToRightAnimation() {
        Animation outtoRight = new TranslateAnimation(
         Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  +1.0f,
         Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
        );
        outtoRight.setDuration(ANIMATION_DURATION);
        outtoRight.setInterpolator(new AccelerateInterpolator());
        return outtoRight;
	} 
    
  //for the previous movement
    public static Animation inFromDownAnimation() {

        Animation inFromUp = new TranslateAnimation(
        Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
        Animation.RELATIVE_TO_PARENT,  +1.0f, Animation.RELATIVE_TO_PARENT,   0.0f
        );
        inFromUp.setDuration(ANIMATION_DURATION);
        inFromUp.setInterpolator(new AccelerateInterpolator());
        return inFromUp;
	}
    public static Animation outToDownAnimation() {
        Animation outtoDown = new TranslateAnimation(
         Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
         Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   -1.0f
        );
        outtoDown.setDuration(ANIMATION_DURATION);
        outtoDown.setInterpolator(new AccelerateInterpolator());
        return outtoDown;
	}    
    // for the next movement
    public static Animation inFromUpAnimation() {
        Animation inFromDown = new TranslateAnimation(
        Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
        Animation.RELATIVE_TO_PARENT,  -1.0f, Animation.RELATIVE_TO_PARENT,   0.0f
        );
        inFromDown.setDuration(ANIMATION_DURATION);
        inFromDown.setInterpolator(new AccelerateInterpolator());
        return inFromDown;
	}
    public static Animation outToUpAnimation() {
        Animation outtoUp = new TranslateAnimation(
         Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
         Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   +1.0f
        );
        outtoUp.setDuration(ANIMATION_DURATION);
        outtoUp.setInterpolator(new AccelerateInterpolator());
        return outtoUp;
	} 
    
    
    private static Animation create(Context c, int resId , final Runnable callback){
    	Animation a = AnimationUtils.loadAnimation(c, resId);
    	a.setInterpolator(new AccelerateInterpolator(1.5f));
    	a.setStartTime(SystemClock.uptimeMillis());
    	
    	if(callback!=null){
    		a.setAnimationListener(new Animation.AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				callback.run();
			}
		});
    	}
    	return a;
    }
    
    public static Animation sildeinParentTop(Context c,Runnable callback){    	
    	return create(c, R.anim.slide_in_top, callback);
    }
     
    public static Animation sildeinParentTop(Context c){    	
    	return sildeinParentTop(c,null);
    }
        
    public static Animation sildeoutParentTop(Context c,Runnable callback){
    	return create(c,  R.anim.slide_out_top, callback);
    }
    
    public static Animation sildeoutParentTop(Context c){
    	return sildeoutParentTop(c,  null);
    }    
    
    public static Animation rotate(Context c,Runnable callback){
    	return create(c,R.anim.rotate, callback);
    }
    
    public static Animation rotate(Context c){
    	return rotate(c,null);
    }
}

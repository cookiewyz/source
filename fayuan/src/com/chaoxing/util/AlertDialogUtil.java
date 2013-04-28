package com.chaoxing.util;

import android.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.WindowManager.LayoutParams;


/**
 * @author <a href="mailto:hb562100@163.com">HeBo</a>
 * @version 2011-5-12
 */
public final class AlertDialogUtil {
	private AlertDialogUtil(){}
	
	public static void alert(Context context  ,int msgId){
		alert(context,R.string.dialog_alert_title,msgId);
	}
	
	public static void alert(Context context ,int tilteId , int msgId){		
		AlertDialog alertDialog =
			settingDlg(new AlertDialog.Builder(context))
			.setTitle(tilteId)
			.setMessage(msgId)
			.create();
		
		showDlg(alertDialog);
	}	
	
	public static void alert(Context context ,CharSequence msg){
		alert(context,null,msg);	
	}	
	 
	public static void alert(Context context ,CharSequence tilte , CharSequence msg){
		AlertDialog alertDialog =
			settingDlg(new AlertDialog.Builder(context))
			.setTitle(tilte)
			.setMessage(msg)
			.create();
		
		showDlg(alertDialog);
	}
	
	// confirm dialog
	
	public static void confirm(Context context  ,int msgId , ConfirmListener listener){
		confirm(context,R.string.dialog_alert_title,msgId,listener);
	}
	
	public static void confirm(Context context ,int tilteId , int msgId  , ConfirmListener listener){
		AlertDialog alertDialog =
			settingConfirmDlg(new AlertDialog.Builder(context),listener)
			.setTitle(tilteId)
			.setMessage(msgId)
			.create();
		
		showDlg(alertDialog);
	}
	
	
	public static void confirm(Context context ,CharSequence msg , ConfirmListener listener){
		confirm(context,null,msg,listener);	
	}
	
	public static void confirm(
			Context context ,
			CharSequence tilte , 
			CharSequence msg ,
			ConfirmListener listener
			){
		AlertDialog alertDialog =
			settingConfirmDlg(new AlertDialog.Builder(context) , listener)
			.setTitle(tilte)
			.setMessage(msg)
			.create();
		
		showDlg(alertDialog);
	}
	
	
	
	private static AlertDialog.Builder settingDlg(AlertDialog.Builder builder){
		builder.setCancelable(false)
		.setPositiveButton(
				R.string.yes
				,new AlertDialog.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dlg, int which) {
				dlg.cancel();
			}
		});
		return builder;
	}
	
	private static AlertDialog.Builder settingConfirmDlg(AlertDialog.Builder builder , final ConfirmListener listener){
		builder.setPositiveButton(
				R.string.yes
				,new AlertDialog.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dlg, int which) {
				listener.onPositive(dlg);
			}
		})
		.setNegativeButton(R.string.cancel,new AlertDialog.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dlg, int which) {
				listener.onCannel(dlg);
			}
		})		
		.setOnCancelListener(new AlertDialog.OnCancelListener() {			
			@Override
			public void onCancel(DialogInterface dlg) {
				listener.onCannel(dlg);
			}
		});
		return builder;
	}
	
	private static void showDlg(AlertDialog alertDialog){
		LayoutParams layoutParams = alertDialog.getWindow().getAttributes();
		layoutParams.y = -100;
		
		alertDialog.show();
	}
	
	public static abstract class ConfirmListener {
		public abstract void onPositive(DialogInterface dlg);
		public void onCannel(DialogInterface dlg){
			if(dlg!=null)
				dlg.cancel();
		}
	}
}

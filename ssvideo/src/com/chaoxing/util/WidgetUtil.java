package com.chaoxing.util;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Set;

import com.chaoxing.database.IWidgetDao;
import com.chaoxing.database.SSVideoLocalVideoBean;
import com.chaoxing.database.SSVideoPlayListBean;
import com.chaoxing.document.UserInformation;
import com.chaoxing.parser.ParserLoginJson;
import com.chaoxing.parser.IniReader;
import com.chaoxing.video.R;
import com.chaoxing.video.Splash;
import com.chaoxing.video.SsvideoPlayerActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
/**
 * @author xin_yueguang.
 * @version 2012-12-20.
 */
public class WidgetUtil {
	private final static String TAG = "WidgetUtil";
	private static UserInformation userInfo;
	private static String encodeUserInfo;
	/**
	 * 初始化logo图标
	 * @param views 当前view
	 * @param ids The id array of the view whose drawable should change
	 */
	public static void loadLogo(RemoteViews views, int[] ids) {
		try {
			String logoIni = UsersUtil.getLogoIniFile();
			IniReader reader = new IniReader(logoIni);
			String[] imgPath = new String[2];
			imgPath[0] = reader.getValue("logo", "imgPath");
			imgPath[1] = reader.getValue("title", "imgPath");
			for(int i=0; i<2; i++) {
				Uri uri = Uri.parse(imgPath[i]);
				views.setImageViewUri(ids[i], uri);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始化按钮
	 * @param context 当前activity的 Context.
	 * @param views 当前view
	 * @param classifyIds 按钮的名称数组
	 * @param classifyViewIds 按钮的资源id数组
	 * @param flags 当前activity的标志
	 */
	public static void renderClassifyViews(Context context, RemoteViews views, final String [] classifyIds, int[] classifyViewIds, String flags) {

		int idx = 0;
		if(classifyIds != null && classifyViewIds != null) {
//			Log.d(TAG, "renderViews: Len(sIds)="+ classifyIds.length +", Len(vIds)="+ classifyViewIds.length);
			for(int i=0; i< classifyIds.length && i<classifyViewIds.length; i++){
//				Log.d(TAG, "renderViews: sIds["+ i +"]="+ classifyIds[i] +", vIds["+ i +"]="+ classifyViewIds[i]);
				if("未分类".equals(classifyIds[i])) continue;
				views.setTextViewText(classifyViewIds[i], classifyIds[i]);
				views.setViewVisibility(classifyViewIds[i], View.VISIBLE);
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putInt("videoType", 0);
				intent.putExtras(bundle);
				intent.putExtra("classify", classifyIds[i]);
				intent.setClass(context, Splash.class);
				PendingIntent pendingIntent = PendingIntent.getActivity(context, i, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				views.setOnClickPendingIntent(classifyViewIds[i], pendingIntent);
			}// end for i;
			idx = classifyIds.length;
		}
		
		views.setTextViewText(R.id.classify_internet, "在线视频");
		views.setViewVisibility(R.id.classify_internet, View.VISIBLE);
		Intent intent = null;
		
		if(AppConfig.ORDER_ONLINE_VIDEO) {
			String url = context.getResources().getString(R.string.url_online_video);
//			url = getFayuanLoginUrl(context);
			String tempUrl = getFayuanLoginUrl(context);
			if(tempUrl != "") {
				url = tempUrl;
			}
			else {
//				CreateLoginFayuanDialog(context);
				
			}
			intent = getStartBrowserIntent(url);
		}
		else {
//		Intent intent =getStartBrowserIntent("http://video.chaoxing.com");
			intent = new Intent();		
			Bundle bundle = new Bundle();
			bundle.putInt("videoType", 2);
			intent.putExtras(bundle);
			intent.setClass(context, Splash.class);
		}
		PendingIntent pendingIntent = PendingIntent.getActivity(context, idx, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.classify_internet, pendingIntent);
		views.setTextViewText(R.id.classify_local, "本地视频");
		Intent intent1 = new Intent();
		Bundle bundle1 = new Bundle();
		bundle1.putInt("videoType", 0);
		bundle1.putString("resource", flags);
		intent1.putExtras(bundle1);
		intent1.setClass(context, Splash.class);
		PendingIntent pendingIntent1 = PendingIntent.getActivity(context, idx+1, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.classify_local, pendingIntent1);
	}
	
	public static Intent getStartBrowserIntent(String url){
		  Intent intent = new Intent();        
		  intent.setAction(Intent.ACTION_VIEW);        
		  Uri content_uri_browsers = Uri.parse(url);       
		  intent.setData(content_uri_browsers);
//		  intent.setClassName("com.android.browser",  "com.android.browser.BrowserActivity");        
		  return intent;
	}
	
	public static void renderVideoCellViews(Context context, RemoteViews views, IWidgetDao dbAdapter, int[] videoTittleIds, int[] videoViewIds) {
		String[] commendImgPaths = null;
		String[] commendVideoNames = null;
		String[] commendVideoPaths = null;
		
		try {
			String commendIni = UsersUtil.getCommendVideoIniFile();
			IniReader iniReader = new IniReader(commendIni);
			Set<String> sections = iniReader.getSections();
			commendImgPaths = new String[sections.size()];
			commendVideoNames = new String[sections.size()];
			commendVideoPaths = new String[sections.size()];
			int i=0;
			for(String section : sections){
				commendImgPaths[i] = iniReader.getValue(section, "imgPath");
				commendVideoNames[i] = iniReader.getValue(section, "videoName");
				commendVideoPaths[i] = iniReader.getValue(section, "videoPath");
				i++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(commendImgPaths == null || commendImgPaths.length < 1) {
			return;
		}
		
		for(int i=0; i<videoTittleIds.length && i<commendImgPaths.length; i++) {
//			Log.i("zzy", commendImgPaths[i] + "   i=" + i + "  bookView[i]" + BookViewIds[i]);
			Uri uri = Uri.parse(commendImgPaths[i]);
			views.setImageViewUri(videoViewIds[i], uri);
			views.setViewVisibility(videoViewIds[i], View.VISIBLE);
			views.setTextViewText(videoTittleIds[i], commendVideoNames[i]);

			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putInt("videoType", 0);
			SSVideoPlayListBean playListBean = null;
			
			SSVideoLocalVideoBean localVideoBean = dbAdapter.getDataInLocalVideoById("" + commendVideoPaths[i].hashCode());
			if(localVideoBean == null){
				playListBean = new SSVideoPlayListBean();
				playListBean.setStrVideoId("" + commendVideoPaths[i].hashCode());
				playListBean.setStrVideoFileName(commendVideoNames[i]);
				playListBean.setStrVideoLocalPath(commendVideoPaths[i]);
			}else{
				playListBean = new SSVideoPlayListBean(localVideoBean);
			}
			bundle.putSerializable("playListBean", playListBean);
		
			intent.putExtras(bundle);
			intent.setClass(context, SsvideoPlayerActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, i, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			views.setOnClickPendingIntent(videoViewIds[i], pendingIntent);
		}
	}
	
	public static String getFayuanLoginUrl(Context context) {
//		String url = "http://passport.lawy.com.cn/userauth.shtml?refer=http://www.lawy.com.cn/&device=test_device&uname=66005656&password=670055";
		String url = "http://passport.lawy.com.cn/userauth.shtml?refer=http://www.lawy.com.cn/&device=";
		String uName = UsersUtil.getUserName(context);
		String uPwd = UsersUtil.getUserPassWord(context);
		String deviceId = UsersUtil.getUserDeviceId(context);
		if(deviceId == "") {
			deviceId = UsersUtil.getUniqueId(context);
		}
		if(uName == "" || uPwd == "") {
			return "";
		}
		else {
			getEncodeUserInfo(uName, uPwd);
			url += deviceId +"&"+ encodeUserInfo;
		}//*/
		return url;
	}
	
	public static int goFayuanLogin(Context context, boolean startActivity) {
		// "http://passport.lawy.com.cn/userauth.shtml?refer=http://www.lawy.com.cn/&device=test_device&uname=66005656&password=670055";
		String url = "http://passport.lawy.com.cn/userauth.shtml?refer=http://www.lawy.com.cn/&device=";
		int rtVal = 0;
		if(userInfo != null && userInfo.getUserStatus() == 1) {
			UsersUtil.saveUserName(context, userInfo.getUserName());
	   		UsersUtil.saveUserPassWord(context, userInfo.getPassword());
	   		String deviceId = UsersUtil.getUniqueId(context);
	   		UsersUtil.saveUserDeviceId(context, deviceId);
	   		if(startActivity) {
	   			url += deviceId +"&"+ encodeUserInfo;
				Intent intent = getStartBrowserIntent(url);
				context.startActivity(intent);
	   		}
			userInfo = null;
			rtVal = 1;
		}
		return rtVal;
	}
	
	public static int saveUserInfo(Context context) {
		int rtVal = 0;
		if(userInfo != null && userInfo.getUserStatus() == 1) {
			UsersUtil.saveUserName(context, userInfo.getUserName());
	   		UsersUtil.saveUserPassWord(context, userInfo.getPassword());
	   		String deviceId = UsersUtil.getUniqueId(context);
	   		UsersUtil.saveUserDeviceId(context, deviceId);
			rtVal = 1;
		}
		return rtVal;
	}
	
	private static int getEncodeUserInfo (String uName, String uPwd){
		String sNameTemp = uName;
	  	  String sPwdTemp = uPwd;
	  		try {
	  			sNameTemp = URLEncoder.encode(uName, "GBK");
	  			sPwdTemp  = URLEncoder.encode(uPwd, "GBK");
	  		} catch (UnsupportedEncodingException e1) {
	  			e1.printStackTrace();
	  			return 1;
	  		}
	  		if(sNameTemp.length() < 1){
	  			return 2;
	  		}
	  		encodeUserInfo = "uname=" + sNameTemp + "&password=" + sPwdTemp;
		return 0;
	}
	
	public static int encodeUrlField(String field) {
		String sNameTemp = field;
  		try {
  			sNameTemp = URLEncoder.encode(field, "GBK");
  		} catch (UnsupportedEncodingException e1) {
  			e1.printStackTrace();
  			return 1;
  		}
  		if(sNameTemp.length() < 1){
  			return 2;
  		}
		return 0;
	}
	
	public static int getUserLoginFayuanInfo (String uName, String uPwd){
  	  int iRetVal = -1;
  	  //http://passport.lawy.com.cn/userauth.shtml?uname=heihei&password=wdwwdw&ajax=true
  	  String sUrl_login = "http://passport.lawy.com.cn/userauth.shtml?ajax=true&";
  	  int rt = getEncodeUserInfo(uName, uPwd);
  	  if(rt != 0) return rt;
  	  sUrl_login = sUrl_login + encodeUserInfo;
  	  ParserLoginJson jsonLogin = new ParserLoginJson();
  	  jsonLogin.getRequestFile(sUrl_login);
   	  int ires = jsonLogin.getResponse();
   	  if(ires != 0)
   	  {
   		  if(ires == -1)
   			  Log.d(TAG, "初始化失败！-get-User-Login-Info");
   		  else if(ires == 1)
   			  Log.d(TAG, "下载失败！-get-User-Login-Info");
   		  else if(ires == 2)
   			  Log.d(TAG, "数据长度为0-get-User-Login-Info");
   		  else
   			  Log.d(TAG, "解析失败！-get-User-Login-Info");
   		  return 3;
   	  }
//   	  int state = jsonLogin.getLoginState();
   	  userInfo = jsonLogin.getUserInfo();
   	  if(userInfo.getUserStatus() == 1)
   	  {
   		userInfo.setUserName(uName);
   		userInfo.setPassword(uPwd);
   		iRetVal = 0;
   	  }
  	  
  	  return iRetVal;
    }//end getUserLoginInfo;
    
	public static UserInformation getUserInfo() {
		return userInfo;
	}
	
	public static String getEncodeUserInfo() {
		return encodeUserInfo;
	}
	
}

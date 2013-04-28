package com.chaoxing.video;


import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import com.chaoxing.database.IWidgetDao;
import com.chaoxing.database.WidgetVideoDao;
import com.chaoxing.util.AppConfig;
import com.chaoxing.util.UsersUtil;
import com.chaoxing.util.WidgetUtil;

public class VideoWidget_5_2 extends AppWidgetProvider {
	private final static String TAG = "VideoWidget_5_2";
	private Context mContext = null;
	private IWidgetDao videoDao = null;
	
	final static int[] LogoIds = {
		R.id.widget_logo,
		R.id.widget_title
	};
	
	final static int [] BookViewIds = {
		R.id.book_0,
		R.id.book_1,
		R.id.book_2
	};

	final static int [] VideoTittleIds = {
		R.id.book_tittle_0,
		R.id.book_tittle_1,
		R.id.book_tittle_2
	};

	@Override
	public void onUpdate(Context context, 
			AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		mContext = context;
		if(videoDao == null) {
			videoDao = new WidgetVideoDao(context, TAG);
		}
		final int N = appWidgetIds.length;
		loadResourse();
		for (int i=0; i<N; i++) {
			int appWidgetId = appWidgetIds[i];
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.video_widget_5_2);
			WidgetUtil.loadLogo(views, LogoIds);
			WidgetUtil.renderVideoCellViews(mContext, views, videoDao, VideoTittleIds, BookViewIds);
			WidgetUtil.renderClassifyViews(mContext, views, null, null, TAG);
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
		videoDao.close();
	}
	
	public void loadResourse() {		
		UsersUtil.loadVideoDBFile(mContext);
		UsersUtil.copyRawResource(mContext, UsersUtil.getCommendVideoIniFile(), AppConfig.commendVideoListID);
		UsersUtil.copyRawResource(mContext, UsersUtil.getLogoIniFile(), AppConfig.logocfgID);
		UsersUtil.unZipRawResource(mContext, AppConfig.commendVideo, AppConfig.commendVideoZipID, true);
		UsersUtil.unZipRawResource(mContext, AppConfig.logo, AppConfig.logoZipID, true);
	}

}

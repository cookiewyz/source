package com.chaoxing.video;

import java.util.LinkedList;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import com.chaoxing.database.IWidgetDao;
import com.chaoxing.database.SSVideoCategoryBean;
import com.chaoxing.database.WidgetVideoDao;
import com.chaoxing.util.AppConfig;
import com.chaoxing.util.UsersUtil;
import com.chaoxing.util.WidgetUtil;

public class VideoWidget_8_5 extends AppWidgetProvider {
	private final static String TAG = "VideoWidget_8_5";
	private Context mContext = null;
	private IWidgetDao videoDao;
	
	final static int[] LogoIds = {
		R.id.widget_logo,
		R.id.widget_title
	};
	
	final static int [] BookViewIds = {
		R.id.book_0,
		R.id.book_1,
		R.id.book_2,
		R.id.book_3,
		R.id.book_4,
		R.id.book_5
	};

	final static int [] VideoTittleIds = {
		R.id.book_tittle_0,
		R.id.book_tittle_1,
		R.id.book_tittle_2,
		R.id.book_tittle_3,
		R.id.book_tittle_4,
		R.id.book_tittle_5
	};

	final static int []ClassifyViewIds = {
		R.id.classify_0,
		R.id.classify_1,
		R.id.classify_2,
		R.id.classify_3,
		R.id.classify_4,
		R.id.classify_5,
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
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.video_widget8_5);
			WidgetUtil.loadLogo(views, LogoIds);
			WidgetUtil.renderVideoCellViews(mContext, views, videoDao, VideoTittleIds, BookViewIds);
			final String[] ClassifyIds = getClassifyNames();
			WidgetUtil.renderClassifyViews(mContext, views, ClassifyIds, ClassifyViewIds, TAG);
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
		videoDao.close();
	}
	
	private String [] getClassifyNames(){
		LinkedList<SSVideoCategoryBean> categoryList = videoDao.getAllCategory();
		String[] classifys = new String[categoryList.size()];
		for(int i=0; i<categoryList.size() && i<ClassifyViewIds.length; i++){
			classifys[i] = categoryList.get(i).getStrCateName();
		}
		return classifys;
	}
	
	public void loadResourse() {		
		UsersUtil.loadVideoDBFile(mContext);
		UsersUtil.copyRawResource(mContext, UsersUtil.getCommendVideoIniFile(), AppConfig.commendVideoListID);
		UsersUtil.copyRawResource(mContext, UsersUtil.getLogoIniFile(), AppConfig.logocfgID);
		UsersUtil.unZipRawResource(mContext, AppConfig.commendVideo, AppConfig.commendVideoZipID, true);
		UsersUtil.unZipRawResource(mContext, AppConfig.logo, AppConfig.logoZipID, true);
	}
	
}

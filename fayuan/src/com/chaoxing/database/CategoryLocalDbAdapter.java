package com.chaoxing.database;

/**
 * ������������������ݿ�
 * @author xin_yueguang
 * @version 2012-12-28.
 */

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import com.chaoxing.document.CategoryNameInfo;
import com.chaoxing.document.DbInfo;
import com.chaoxing.document.SeriesInfo;
import com.chaoxing.util.AppConfig;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CategoryLocalDbAdapter {
//	private static final String TAG = "categoryLocalDbAdapter";
	private static final int VERSION = 1;

	private SQLiteDatabase mSQLiteDB = null;
	private DatabaseHelper mDBHelper = null;
	private Context mContext = null;
	
	private ArrayList<CategoryNameInfo> cateNameList = null;
	private ArrayList<SeriesInfo> serList = null;
	
	private class DatabaseHelper extends SQLiteOpenHelper {
		
		public DatabaseHelper(Context context, String name, CursorFactory factory, int version){
			super(context, name, factory, version);
		}
		
		public DatabaseHelper(Context context, String name){
			this(context, name, VERSION);
		}
		
		public DatabaseHelper(Context context, String name, int version) {
			this(context, name, null, version);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			// ����������Ƶ��local_video
//			db.execSQL("CREATE TABLE local_video (video_id VARCHAR PRIMARY KEY,video_name VARCHAR,  video_file_length INTEGER NOT NULL  DEFAULT 0, " +
//					"speaker VARCHAR, category_id VARCHAR, cover_name VARCHAR, video_file_name VARCHAR, video_local_path VARCHAR, " +
//					"video_download_status INTEGER NOT NULL  DEFAULT 0, video_download_progress INTEGER NOT NULL  DEFAULT 0," +
//					"video_download_remote_file_url VARCHAR DEFAULT 0, video_download_remote_cover_url VARCHAR DEFAULT 0, video_abstract VARCHAR)");
//			// ���������б��play_list
//			db.execSQL("CREATE TABLE play_list (video_id VARCHAR PRIMARY KEY, video_name VARCHAR, speaker VARCHAR, " +
//					"category_id VARCHAR, cover_name VARCHAR, video_file_name VARCHAR, current_play_time INTEGER NOT NULL  DEFAULT 0," +
//					" video_type INTEGER NOT NULL  DEFAULT 0, remote_cover_url VARCHAR NOT NULL  DEFAULT 0, video_local_path VARCHAR, " +
//					"m3u8_url VARCHAR NOT NULL  DEFAULT 0, series_id VARCHAR NOT NULL  DEFAULT 0, current_play INTEGER NOT NULL  DEFAULT 0, " +
//					"playtimes VARCHAR, date VARCHAR, score VARCHAR DEFAULT 0, scoreCount VARCHAR DEFAULT 0, video_abstract VARCHAR)");
//			// ���������б�video_series
//			db.execSQL("CREATE TABLE video_category (category_id VARCHAR PRIMARY KEY, category_name VARCHAR)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//			db.execSQL("DROP TABLE IF EXISTS nostes");
//			onCreate(db);
		}
	}
	
	public CategoryLocalDbAdapter(Context context){
		mContext = context;
//		mDBHelper = new DatabaseHelper(mContext, DB_NAME);
	}
	
	// �����ݿ�
	private void open() throws SQLException
	{
		if(mDBHelper == null) {
			mDBHelper = new DatabaseHelper(mContext, AppConfig.localCategoryDbName);
		}
		mSQLiteDB = mDBHelper.getWritableDatabase();
	}
	
	// �ر����ݿ�
	private void close()
	{
		mDBHelper.close();
	}
	
	public ArrayList<CategoryNameInfo> getCategoryNameOfLevel(String sparent, int ilevel) {
	     Cursor cursor = getCategoryNameData(sparent, ilevel);
	     if(cursor != null)
	    	 cursor.moveToFirst();
	     else 
	    	 return null;
		 
		 if(cursor.getCount() <= 0) {
			 return null;
		 }
		 
		 cateNameList = new ArrayList<CategoryNameInfo>();
		do {
			CategoryNameInfo cni = new CategoryNameInfo();
   		  	cni.setCateId(cursor.getString(0));//�����
   		  	cni.setTitle(cursor.getString(1));//��������
   		  	cni.setLevel(cursor.getString(2));//����
   		  	cni.setParentId(cursor.getString(3));//�������
   		  	String schild = cursor.getString(4);
   		  	if(schild.equals("")) {
   		  		schild = "0";
   		  	}
   		  	cni.setHasChild(schild);//isnode�Ƿ����ӽڵ�
   		  	cateNameList.add(cni);
   	  	} while(cursor.moveToNext());
		cursor.close();
//		getCategorySeriesCount();//����ÿ������ר������	
		return  cateNameList;
	}
	
	private Cursor getCategoryNameData(String sparent, int ilevel) {
		try {
			open();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			Log.e("get-CategoryNameData", "open:" + e.getMessage());
		}
		 String columns[] = {DbInfo.CATE_ID, DbInfo.CATE_NAME, DbInfo.CATE_LEVEL, DbInfo.CATE_PARENT, DbInfo.CATE_SUB, DbInfo.CATE_INDEX_ID};
		 String sWhere = "";
		 if(!sparent.equals("")) {
			 sWhere = DbInfo.CATE_LEVEL+"="+ilevel+" AND "+ DbInfo.CATE_PARENT +"='"+ sparent +"' ORDER BY "+ DbInfo.CATE_ID +" ASC"; 
		 }
		 else {
			 sWhere = DbInfo.CATE_LEVEL+"="+ilevel+" ORDER BY "+ DbInfo.CATE_INDEX_ID+" ASC";
		 }
//		 Log.d("��ȡ������", "sparent ="+sparent+", level="+ilevel+ ", where="+sWhere);
	     Cursor cursor = null;
			try {
				cursor = mSQLiteDB.query(true, DbInfo.TB_INFO_CATEGORY, columns, sWhere, null, null, null, null, null);
//				Log.d(TAG, "get all CategoryNameDate level ="+ilevel);
			} catch (Exception e) {
				// TODO: handle exception
				Log.e("get-CategoryNameData", "2:get all CategoryNameDate level ="+ilevel+", error=" + e.getMessage());
			}
	     if(cursor != null)
	    	 cursor.moveToFirst();
		 close();
	     
		 return  cursor;
	}
	
	public ArrayList<SeriesInfo> getSeriesInfoIndex(String serid, int idx) {
	     Cursor cursor = getCategorySeriesData(serid, idx);
	     if(cursor != null)
	    	 cursor.moveToFirst();
	     else 
	    	 return null;
		 
		 if(cursor.getCount() <= 0) {
			 return null;
		 }
		 
		serList = new ArrayList<SeriesInfo>();
		
		do {
			SeriesInfo si = new SeriesInfo();
			si.setSerid(cursor.getString(0));
			si.setTitle(cursor.getString(1));
			si.setCover(cursor.getString(2));
			String sPlaytimes = cursor.getString(3);
			if(sPlaytimes == null || sPlaytimes.equals(""))
				sPlaytimes = "0";
			si.setPlaytimes(sPlaytimes);
			String sScore = cursor.getString(4);
			if(sScore == null || sScore.equals(""))
				sScore = "0";
			si.setScore(sScore);
			String sScoreCount = cursor.getString(5);
			if(sScoreCount == null || sScoreCount.equals(""))
				sScoreCount = "0";
			si.setScoreCount(sScoreCount);
			si.setDate(cursor.getString(6));
			String sAbstract = cursor.getString(7);
			if(sAbstract == null || sAbstract.equals(""))
				sAbstract = "";
			si.setAbstracts(sAbstract);
			si.setKeySpeaker(cursor.getString(8));
			si.setSpeakerPhoto(cursor.getString(9));
			si.setSubject(cursor.getString(10));
			
  		  	serList.add(si);
  	  	} while(cursor.moveToNext());
		cursor.close();
		return  serList;
	}
	
	private Cursor getCategorySeriesData(String serid, int idx) {
		try {
			open();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*String columns[]={DbInfo.SER_ID, DbInfo.SER_NAME, DbInfo.SER_COVER, DbInfo.SER_PLAY_TIME, DbInfo.SER_SCORE, 
				 	DbInfo.SER_SCORE_COUNT, DbInfo.SER_DATE, DbInfo.SER_ABSTRACT, DbInfo.SER_SPEAKER, DbInfo.SER_SPEAKER_IMG, 
				 	DbInfo.SER_SUBJECT, DbInfo.SER_CATE_ID};
		*/
		 
		 String sqlWhere = null;
		 if(!serid.equals("")) {
			 sqlWhere = DbInfo.SER_CATE_ID +" like '%,"+serid+"%,'";
		 }
		 else {
			 sqlWhere = DbInfo.SER_VID +">="+idx+" and "+ DbInfo.SER_VID +"<"+(idx+20) +" ORDER BY "+ DbInfo.SER_ID+" ASC";
		 }
	     Cursor cursor = mSQLiteDB.query(true, DbInfo.TB_INFO_SERIES, DbInfo.columns, sqlWhere, null, null, null, null, null);
	     if(cursor != null)
	    	 cursor.moveToFirst();
		 close();
//	     Log.d(TAG, "get all CategoryNameDate level ="+idx);
		 return  cursor;
	}
	
	/**
	 * ��ȡר��������
	 * @param key
	 * 		�ؼ��ʣ�null����ר�⣬��������ؼ��ʵ�ר��
	 * @return
	 * 		ר���б� ArrayList - SeriesInfo
	 */
	public ArrayList<SeriesInfo> getSearchInfoOfKey(String key) {
	     Cursor cursor = getSearchSeriesData(key);
	     if(cursor != null)
	    	 cursor.moveToFirst();
	     else 
	    	 return null;
		 
		 if(cursor.getCount() <= 0) {
			 return null;
		 }
		 
		serList = new ArrayList<SeriesInfo>();
		
		do {
			SeriesInfo si = new SeriesInfo();
			si.setSerid(cursor.getString(0));
			si.setTitle(cursor.getString(1));
			si.setCover(cursor.getString(2));
			String sPlaytimes = cursor.getString(3);
			if(sPlaytimes == null || sPlaytimes.equals(""))
				sPlaytimes = "0";
			si.setPlaytimes(sPlaytimes);
			String sScore = cursor.getString(4);
			if(sScore == null || sScore.equals(""))
				sScore = "0";
			si.setScore(sScore);
			String sScoreCount = cursor.getString(5);
			if(sScoreCount == null || sScoreCount.equals(""))
				sScoreCount = "0";
			si.setScoreCount(sScoreCount);
			si.setDate(cursor.getString(6));
			String sAbstract = cursor.getString(7);
			if(sAbstract == null || sAbstract.equals(""))
				sAbstract = "";
			si.setAbstracts(sAbstract);
			si.setKeySpeaker(cursor.getString(8));
			si.setSpeakerPhoto(cursor.getString(9));
			si.setSubject(cursor.getString(10));
			
 		  	serList.add(si);
 	  	} while(cursor.moveToNext());
		cursor.close();
		return  serList;
	}
	
	/**
	 * ��ȡר��������
	 * @param key
	 * 		�ؼ��ʣ�null����ר�⣬��������ؼ��ʵ�ר��
	 * @return
	 * 		������α�
	 */
	private Cursor getSearchSeriesData(String key) {
		try {
			open();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/* String columns[]={DbInfo.SER_ID, DbInfo.SER_NAME, DbInfo.SER_COVER, DbInfo.SER_PLAY_TIME, DbInfo.SER_SCORE, 
				 	DbInfo.SER_SCORE_COUNT, DbInfo.SER_DATE, DbInfo.SER_ABSTRACT, DbInfo.SER_SPEAKER, DbInfo.SER_SPEAKER_IMG, 
				 	DbInfo.SER_SUBJECT, DbInfo.SER_CATE_ID};*/
//		 String args[]={key};//, key, key, key
		 String sqlWhere = null;
		 if(!key.equals("")) {
//			 sqlWhere = SER_NAME+" like '%?s%' OR "+SER_SPEAKER +" like '%?s%' OR "+SER_ABSTRACT +" like '%?s%' OR "+SER_SUBJECT +" like '%?s%'";
//			 sqlWhere = SER_NAME+" like '%"+key+"%'";
			 sqlWhere = DbInfo.SER_NAME+" like '%"+key+"%' OR "+ DbInfo.SER_SPEAKER +" like '%"+key+"%' OR "+ 
					 DbInfo.SER_ABSTRACT +" like '%"+key+"%' OR "+ DbInfo.SER_SUBJECT +" like '%"+key+"%'";
		 }
		
	     Cursor cursor = null;
	     try {
			cursor = mSQLiteDB.query(true, DbInfo.TB_INFO_SERIES, DbInfo.columns, sqlWhere, null, null, null, null, null);
	     } catch (Exception e) {
			// TODO: handle exception
			Log.e("get-SeriesSearch", "get all Series Search Data: key =["+key+"], error="+e.getMessage());
	     }
			
	     if(cursor != null)
	    	 cursor.moveToFirst();
		 close();
//	     Log.d(TAG, "get all Search CategoryDate");
		 return  cursor;
	}
	
	/**
	 * ��ȡ��Ƶר������
	 * @param key 
	 * 		null������ר��������not null��ָ������ŵ�ר������
	 * @param type
	 * 		1��������ʽ��2��������Ƶ�ⷽʽ
	 * @return
	 * 		ר������
	 */
	public int getSeriesCount(String key, int type) {
		int iRet = 0;
		try {
			open();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return iRet;
		}
		
		String sqlWhere = null;
		if(type == 1) {
//			sqlWhere = SER_NAME+" like '%"+key+"%'";
			sqlWhere = DbInfo.SER_NAME+" like '%"+key+"%' OR "+ DbInfo.SER_SPEAKER +" like '%"+key+"%' OR "+
						DbInfo.SER_ABSTRACT +" like '%"+key+"%' OR "+ DbInfo.SER_SUBJECT +" like '%"+key+"%'";
		}
		else {
			if(!key.equals("")) {
				sqlWhere = DbInfo.SER_CATE_ID+" like '%,"+key+"%,'";
			}
		}
		String columns[]={DbInfo.SER_ID};
		Cursor cursor = null;
		try {
			cursor = mSQLiteDB.query(true, DbInfo.TB_INFO_SERIES, columns, sqlWhere, null, null, null, null, null);
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("get-SeriesCount", "get all SeriesCount: key =["+key+"], error="+e.getMessage());
		}
		
		if(cursor != null) {
			iRet = cursor.getCount();
		}
		cursor.close();
		close(); 
		
		return iRet;
	}
	
	// ͨ��Cursor����ѯ��������
	public LinkedList<SSVideoPlayListBean> fetchAllDataInPlayList()
	{
		Cursor cursor = mSQLiteDB.query("", new String[]{ "video_id", "video_name", "speaker", "category_id", "cover_name", 
				"video_file_name", "video_local_path", "current_play_time", "video_type", "remote_cover_url", "m3u8_url", "series_id", 
				"current_play", "playtimes", "score", "scoreCount", "video_abstract"}, null, null, null, null, null);
		LinkedList<SSVideoPlayListBean> playList = null;
		if(cursor != null){
			playList = new LinkedList<SSVideoPlayListBean>();
			while(cursor.moveToNext()){
				SSVideoPlayListBean playListBean = new SSVideoPlayListBean();
				playListBean.setStrVideoId(cursor.getString(cursor.getColumnIndex("video_id")));
				playListBean.setStrVideoName(cursor.getString(cursor.getColumnIndex("video_name")));
				playListBean.setStrSpeaker(cursor.getString(cursor.getColumnIndex("speaker")));
				playList.addFirst(playListBean);
			}
		}
		return playList;
	}
	// ͨ��videoId ��ѯָ��������
	public Cursor fetchDataInPlayListById(String videoId) throws SQLException
	{
		Cursor cursor = mSQLiteDB.query("", new String[]{ "video_id", "video_name", "speaker", "category_id", "cover_name", 
				"video_file_name", "video_local_path", "current_play_time", "video_type", "remote_cover_url","m3u8_url", "series_id", 
				"current_play", "playtimes", "score", "scoreCount", "video_abstract"}, "video_id=?", new String[]{videoId}, null, null, null);
		return cursor;
	}
	
	// ���²��ż���
	public boolean updataCurrentPlay(String seriesId, int currentPlay)
	{
		ContentValues values = new ContentValues();
		values.put("current_play", currentPlay);
		return mSQLiteDB.update("", values, "series_id=?" ,new String[]{ seriesId}) > 0;
	}
	// ����һ������
	public boolean updateDataInPlayList(String videoId, SSVideoPlayListBean playListBean)
	{
		ContentValues values = new ContentValues();
		if(playListBean.getStrAbstract() != null)
			values.put("video_abstract", playListBean.getStrAbstract());
		return mSQLiteDB.update("", values, "video_id=?" ,new String[]{ videoId}) > 0;
	}
	
	// ��local_video���в�������
	public long insertIntoLocalVideo( SSVideoLocalVideoBean localVideoBean)
	{
		ContentValues values = new ContentValues();
		if(localVideoBean.getStrVideoId() != null)
			values.put("video_id", localVideoBean.getStrVideoId());
		if(localVideoBean.getStrVideoName() != null)
			values.put("video_name", localVideoBean.getStrVideoName());
		if(localVideoBean.getStrSpeaker() != null)
			values.put("speaker", localVideoBean.getStrSpeaker());
		if(localVideoBean.getStrCateId() != null)
			values.put("category_id", localVideoBean.getStrCateId());
		if(localVideoBean.getStrCoverName() != null)
			values.put("cover_name", localVideoBean.getStrCoverName());
		
		return mSQLiteDB.insert(DbInfo.TB_INFO_VIDEO, "video_id", values);
	}
	
	// ��ѯlocal_video������δ������ɵ�����
	public LinkedList<SSVideoLocalVideoBean> fetchDownloadDataInLocalVideo() throws SQLException
	{
		Cursor cursor = mSQLiteDB.query(DbInfo.TB_INFO_SPEAKER, new String[]{  "video_id", "video_name", "speaker", "category_id", "cover_name", "video_local_path", "video_file_length",
				"video_file_name", "video_download_status", "video_download_progress", "video_download_remote_cover_url","video_download_remote_file_url", "video_abstract"}, 
				"video_download_status>0" , null, null, null, null);
		LinkedList <SSVideoLocalVideoBean> localVideoList = new LinkedList<SSVideoLocalVideoBean>();
		while(cursor.moveToNext()) {
			SSVideoLocalVideoBean localVideoBean = new SSVideoLocalVideoBean();
			localVideoBean.setStrVideoId(cursor.getString(cursor.getColumnIndex("video_id")));
			localVideoBean.setStrVideoName(cursor.getString(cursor.getColumnIndex("video_name")));
			localVideoBean.setStrSpeaker(cursor.getString(cursor.getColumnIndex("speaker")));
			localVideoList.addFirst(localVideoBean);
		}
		cursor.close();
		return localVideoList;
	}
	
}

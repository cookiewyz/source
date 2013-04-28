package com.chaoxing.database;

import java.sql.SQLException;
import java.util.LinkedList;

import com.chaoxing.document.DbInfo;
import com.chaoxing.video.SsvideoRoboApplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SSVideoDatabaseAdapter {
//	private static final String TAG = "SSVideoDbAdapter";
	
//	private SQLiteDatabase mSQLiteDB = null;
	private UsersDbHelper mDBHelper = null;//private DatabaseHelper mDBHelper = null;
//	private Context mContext = null;
	
/*	private class DatabaseHelper extends SQLiteOpenHelper{
		
		public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}
		public DatabaseHelper(Context context, String name) {
			this(context, name, VERSION);
		}
		public DatabaseHelper(Context context, String name, int version) {
			this(context, name, null, version);
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
			// 创建本地视频表local_video
			db.execSQL("CREATE TABLE local_video (video_id VARCHAR PRIMARY KEY,video_name VARCHAR,  video_file_length INTEGER NOT NULL  DEFAULT 0, " +
					"speaker VARCHAR, category_id VARCHAR, cover_name VARCHAR, video_file_name VARCHAR, video_local_path VARCHAR, " +
					"video_download_status INTEGER NOT NULL  DEFAULT 0, video_download_progress INTEGER NOT NULL  DEFAULT 0," +
					"video_download_remote_file_url VARCHAR DEFAULT 0, video_download_remote_cover_url VARCHAR DEFAULT 0, video_abstract VARCHAR)");
			// 创建播放列表表play_list
			db.execSQL("CREATE TABLE play_list (video_id VARCHAR PRIMARY KEY, video_name VARCHAR, speaker VARCHAR, " +
					"category_id VARCHAR, cover_name VARCHAR, video_file_name VARCHAR, current_play_time INTEGER NOT NULL  DEFAULT 0," +
					" video_type INTEGER NOT NULL  DEFAULT 0, remote_cover_url VARCHAR NOT NULL  DEFAULT 0, video_local_path VARCHAR, " +
					"m3u8_url VARCHAR NOT NULL  DEFAULT 0, series_id VARCHAR NOT NULL  DEFAULT 0, current_play INTEGER NOT NULL  DEFAULT 0, " +
					"playtimes VARCHAR, date VARCHAR, score VARCHAR DEFAULT 0, scoreCount VARCHAR DEFAULT 0, video_abstract VARCHAR)");
			// 创建分类列表video_series
			db.execSQL("CREATE TABLE video_category (category_id VARCHAR PRIMARY KEY, category_name VARCHAR)");
			insertDefaultCategory(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS nostes");
			if(newVersion == 2) {
				insertDefaultCategory(db);
			}
		}
	}*/
	
	public SSVideoDatabaseAdapter(Context context, SsvideoRoboApplication ssvideo, String flags) {
//		mContext = context;
		mDBHelper = ssvideo.getUserDbHelper();// new UsersDbHelper(mContext);//new DatabaseHelper(mContext, DB_NAME);
//		Log.d(TAG, "userDB: flags ="+ flags);
	}
//	// 打开数据库
//	public void open() throws SQLException
//	{
////		mSQLiteDB = mDBHelper.getUserDb();//mDBHelper.getWritableDatabase();
//	}
//	// 关闭数据库
//	public void close()
//	{
////		mDBHelper.close();
//	}
	// 向play_list表中插入数据
	public long insertIntoPlayList( SSVideoPlayListBean playListBean)
	{
		ContentValues values = new ContentValues();
		if(playListBean.getStrVideoId() != null)
			values.put("video_id", playListBean.getStrVideoId());
		if(playListBean.getStrVideoName() != null)
			values.put("video_name", playListBean.getStrVideoName());
		if(playListBean.getStrSpeaker() != null)
			values.put("speaker", playListBean.getStrSpeaker());
		if(playListBean.getStrCateId() != null)
			values.put("category_id", playListBean.getStrCateId());
		if(playListBean.getStrCoverName() != null)
			values.put("cover_name", playListBean.getStrCoverName());
		if(playListBean.getStrVideoFileName() != null)
			values.put("video_file_name", playListBean.getStrVideoFileName());
		if(playListBean.getStrVideoLocalPath() != null)
			values.put("video_local_path", playListBean.getStrVideoLocalPath());
		if(playListBean.getnCurrentPlayTime() != null)
			values.put("current_play_time", playListBean.getnCurrentPlayTime());
		if(playListBean.getnVideoType() != null)
			values.put("video_type", playListBean.getnVideoType().intValue());
		if(playListBean.getStrRemoteCoverUrl() != null)
			values.put("remote_cover_url", playListBean.getStrRemoteCoverUrl());
		if(playListBean.getStrM3u8Url() != null)
			values.put("m3u8_url", playListBean.getStrM3u8Url());
		if(playListBean.getStrSeriesId() != null)
			values.put("series_id", playListBean.getStrSeriesId());
		if(playListBean.getnCurrentPlay() != null)
			values.put("current_play", playListBean.getnCurrentPlay().intValue());
		if(playListBean.getStrPlayTimes() != null)
			values.put("playtimes", playListBean.getStrPlayTimes());
		if(playListBean.getStrScore() != null)
			values.put("score", playListBean.getStrScore());
		if(playListBean.getStrScoreCount() != null)
			values.put("scoreCount", playListBean.getStrScoreCount());
		if(playListBean.getStrAbstract() != null)
			values.put("video_abstract", playListBean.getStrAbstract());
		SQLiteDatabase db = mDBHelper.getUserDb();
		return db.insert(DbInfo.TB_PLAY_LIST, "video_id", values);
	}
	// 删除play_list所有数据
	/**
	 * @return
	 */
	public boolean deleteAllDataInPlayList()
	{
		SQLiteDatabase db = mDBHelper.getUserDb();
		return db.delete(DbInfo.TB_PLAY_LIST, null, null) > 0;
	}
	// 删除指定数据
	public boolean deleteDataInPlayListById(String videoId)
	{
		SQLiteDatabase db = mDBHelper.getUserDb();
		return db.delete(DbInfo.TB_PLAY_LIST, "video_id=?" , new String[]{videoId}) > 0;
	}
	// 通过路径删除指定数据
	public boolean deleteDataInPlayListByPath(String videoPath)
	{
		SQLiteDatabase db = mDBHelper.getUserDb();
		return db.delete(DbInfo.TB_PLAY_LIST, "video_file_name=?", new String[]{videoPath}) > 0;
	}
	// 通过Cursor来查询所有数据
	public LinkedList<SSVideoPlayListBean> fetchAllDataInPlayList()
	{
		SQLiteDatabase db = mDBHelper.getUserDb();
		Cursor cursor = db.query(DbInfo.TB_PLAY_LIST, new String[]{ "video_id", "video_name", "speaker", "category_id", "cover_name", 
				"video_file_name", "video_local_path", "current_play_time", "video_type", "remote_cover_url","m3u8_url", "series_id", 
				"current_play", "playtimes", "score", "scoreCount", "video_abstract"}, null, null, null, null, null);
		LinkedList<SSVideoPlayListBean> playList = null;
		if(cursor != null) {
			playList = new LinkedList<SSVideoPlayListBean>();
			while(cursor.moveToNext()) {
				SSVideoPlayListBean playListBean = new SSVideoPlayListBean();
				playListBean.setStrVideoId(cursor.getString(cursor.getColumnIndex("video_id")));
				playListBean.setStrVideoName(cursor.getString(cursor.getColumnIndex("video_name")));
				playListBean.setStrSpeaker(cursor.getString(cursor.getColumnIndex("speaker")));
				playListBean.setStrCateId(cursor.getString(cursor.getColumnIndex("category_id")));
				playListBean.setStrCoverName(cursor.getString(cursor.getColumnIndex("cover_name")));
				playListBean.setStrVideoFileName(cursor.getString(cursor.getColumnIndex("video_file_name")));
				playListBean.setStrVideoLocalPath(cursor.getString(cursor.getColumnIndex("video_local_path")));
				playListBean.setnCurrentPlayTime(cursor.getInt(cursor.getColumnIndex("current_play_time")));
				playListBean.setnVideoType(cursor.getInt(cursor.getColumnIndex("video_type")));
				playListBean.setStrRemoteCoverUrl(cursor.getString(cursor.getColumnIndex("remote_cover_url")));
				playListBean.setStrM3u8Url(cursor.getString(cursor.getColumnIndex("m3u8_url")));
				playListBean.setStrSeriesId(cursor.getString(cursor.getColumnIndex("series_id")));
				playListBean.setnCurrentPlay(cursor.getInt(cursor.getColumnIndex("current_play")));
				playListBean.setStrPlayTimes(cursor.getString(cursor.getColumnIndex("playtimes")));
				playListBean.setStrScore(cursor.getString(cursor.getColumnIndex("score")));
				playListBean.setStrScore(cursor.getString(cursor.getColumnIndex("scoreCount")));
				playListBean.setStrAbstract(cursor.getString(cursor.getColumnIndex("video_abstract")));
				playList.addFirst(playListBean);
			}
			cursor.close();
		}
		return playList;
	}
	// 通过videoId 查询指定的数据
	public Cursor fetchDataInPlayListById(String videoId) throws SQLException
	{
		SQLiteDatabase db = mDBHelper.getUserDb();
		Cursor cursor = db.query(DbInfo.TB_PLAY_LIST, new String[]{ "video_id", "video_name", "speaker", "category_id", "cover_name", 
				"video_file_name", "video_local_path", "current_play_time", "video_type", "remote_cover_url","m3u8_url", "series_id", 
				"current_play", "playtimes", "score", "scoreCount", "video_abstract"}, "video_id=?", new String[]{videoId}, null, null, null);
		return cursor;
	}
	// 通过videoId 查询指定的数据
		public Cursor fetchDataInPlayListBySeriesId(String seriesId) throws SQLException
		{
			SQLiteDatabase db = mDBHelper.getUserDb();
			Cursor cursor = db.query(DbInfo.TB_PLAY_LIST, new String[]{ "video_id", "video_name", "speaker", "category_id", "cover_name", 
					"video_file_name", "video_local_path", "current_play_time", "video_type", "remote_cover_url","m3u8_url", "series_id", 
					"current_play", "playtimes", "score", "scoreCount", "video_abstract"}, "series_id=?" , new String[]{seriesId}, null, null, null);
			return cursor;
		}
	// 通过videoName查询指定的数据
	public Cursor fetchDataInPlayListByName(String videoName) throws SQLException
	{
		SQLiteDatabase db = mDBHelper.getUserDb();
		Cursor cursor = db.query(DbInfo.TB_PLAY_LIST, new String[]{ "video_id", "video_name", "speaker", "category_id", "cover_name", 
				"video_file_name", "video_local_path", "current_play_time", "video_type", "remote_cover_url","m3u8_url", "series_id", 
				"current_play", "playtimes", "score", "scoreCount", "video_abstract"}, "video_name=?", new String[]{videoName}, null, null, null);
//		if(cursor != null)
//			cursor.moveToFirst();
		return cursor;
	}
	// 通过视频的完整路径查询指定数据
	public Cursor fetchDataInPlayListByFullName(String videoFullName)
	{
		SQLiteDatabase db = mDBHelper.getUserDb();
		Cursor cursor = db.query(DbInfo.TB_PLAY_LIST, new String[]{ "video_id", "video_name", "speaker", "category_id", "cover_name", 
				"video_file_name", "video_local_path", "current_play_time", "video_type", "remote_cover_url","m3u8_url", "series_id", 
				"current_play", "playtimes", "score", "scoreCount", "video_abstract"}, "video_file_name=?" , new String[]{videoFullName}, null, null, null);
		if(cursor != null)
			cursor.moveToFirst();
		return cursor;
	}
	// 更新播放集数
	public boolean updataCurrentPlay(String seriesId, int currentPlay)
	{
		ContentValues values = new ContentValues();
		values.put("current_play", currentPlay);
		SQLiteDatabase db = mDBHelper.getUserDb();
		return db.update(DbInfo.TB_PLAY_LIST, values, "series_id=?" ,new String[]{ seriesId}) > 0;
	}
	// 更新一条数据
	public boolean updateDataInPlayList(String videoId, SSVideoPlayListBean playListBean)
	{
		ContentValues values = new ContentValues();
		if(playListBean.getStrVideoName() != null)
			values.put("video_name", playListBean.getStrVideoName());
		if(playListBean.getStrSpeaker() != null)
			values.put("speaker", playListBean.getStrSpeaker());
		if(playListBean.getStrCateId() != null)
			values.put("category_id", playListBean.getStrCateId());
		if(playListBean.getStrCoverName() != null)
			values.put("cover_name", playListBean.getStrCoverName());
		if(playListBean.getStrVideoFileName() != null)
			values.put("video_file_name", playListBean.getStrVideoFileName());
		if(playListBean.getStrVideoLocalPath() != null)
			values.put("video_local_path", playListBean.getStrVideoLocalPath());
		if(playListBean.getnCurrentPlayTime() != null)
			values.put("current_play_time", playListBean.getnCurrentPlayTime());
		if(playListBean.getnVideoType() != null)
			values.put("video_type", playListBean.getnVideoType().intValue());
		if(playListBean.getStrRemoteCoverUrl() != null)
			values.put("remote_cover_url", playListBean.getStrRemoteCoverUrl());
		if(playListBean.getStrM3u8Url() != null)
			values.put("m3u8_url", playListBean.getStrM3u8Url());
		if(playListBean.getStrSeriesId() != null)
			values.put("series_id", playListBean.getStrSeriesId());
		if(playListBean.getnCurrentPlay() != null)
			values.put("current_play", playListBean.getnCurrentPlay());
		if(playListBean.getStrPlayTimes() != null)
			values.put("playtimes", playListBean.getStrPlayTimes());
		if(playListBean.getStrScore() != null)
			values.put("score", playListBean.getStrScore());
		if(playListBean.getStrScoreCount() != null)
			values.put("scoreCount", playListBean.getStrScoreCount());
		if(playListBean.getStrAbstract() != null)
			values.put("video_abstract", playListBean.getStrAbstract());
		SQLiteDatabase db = mDBHelper.getUserDb();
		return db.update(DbInfo.TB_PLAY_LIST, values, "video_id=?" ,new String[]{ videoId}) > 0;
	}
	// 通过全路径更新一条数据
	public boolean updateDataInPlayListByFullName(String videoFullName, SSVideoPlayListBean playListBean)
	{
		ContentValues values = new ContentValues();
		if(playListBean.getStrVideoName() != null)
			values.put("video_name", playListBean.getStrVideoName());
		if(playListBean.getStrSpeaker() != null)
			values.put("speaker", playListBean.getStrSpeaker());
		if(playListBean.getStrCateId() != null)
			values.put("category_id", playListBean.getStrCateId());
		if(playListBean.getStrCoverName() != null)
			values.put("cover_name", playListBean.getStrCoverName());
		if(playListBean.getStrVideoFileName() != null)
			values.put("video_file_name", playListBean.getStrVideoFileName());
		if(playListBean.getStrVideoLocalPath() != null)
			values.put("video_local_path", playListBean.getStrVideoLocalPath());
		if(playListBean.getnCurrentPlayTime() != null)
			values.put("current_play_time", playListBean.getnCurrentPlayTime().intValue());
		if(playListBean.getnVideoType() != null)
			values.put("video_type", playListBean.getnVideoType().intValue());
		if(playListBean.getStrRemoteCoverUrl() != null)
			values.put("remote_cover_url", playListBean.getStrRemoteCoverUrl());
		if(playListBean.getStrM3u8Url() != null)
			values.put("m3u8_url", playListBean.getStrM3u8Url());
		if(playListBean.getStrSeriesId() != null)
			values.put("series_id", playListBean.getStrSeriesId());
		if(playListBean.getnCurrentPlay() != null)
			values.put("current_play", playListBean.getnCurrentPlay().intValue());
		if(playListBean.getStrPlayTimes() != null)
			values.put("playtimes", playListBean.getStrPlayTimes());
		if(playListBean.getStrScore() != null)
			values.put("score", playListBean.getStrScore());
		if(playListBean.getStrScoreCount() != null)
			values.put("scoreCount", playListBean.getStrScoreCount());
		if(playListBean.getStrAbstract() != null)
			values.put("video_abstract", playListBean.getStrAbstract());
		SQLiteDatabase db = mDBHelper.getUserDb();
		return db.update(DbInfo.TB_PLAY_LIST, values, "video_file_name=?" ,new String[]{ videoFullName}) > 0;
	}
	// 向local_video表中插入数据
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
		if(localVideoBean.getStrVideoFileName() != null)
			values.put("video_file_name", localVideoBean.getStrVideoFileName());
		if(localVideoBean.getStrVideoLocalPath() != null)
			values.put("video_local_path", localVideoBean.getStrVideoLocalPath());
		if(localVideoBean.getnVideoDownloadStatus() != null)
			values.put("video_download_status", localVideoBean.getnVideoDownloadStatus().intValue());
		if(localVideoBean.getnVideoDownloadProgress() != null)
			values.put("video_download_progress", localVideoBean.getnVideoDownloadProgress().intValue());
		if(localVideoBean.getStrVideoDownloadRemoteCoverUrl() != null)
			values.put("video_download_remote_cover_url", localVideoBean.getStrVideoDownloadRemoteCoverUrl());
		if(localVideoBean.getnVideoFileLength() != null)
			values.put("video_file_length", localVideoBean.getnVideoFileLength().intValue());
		if(localVideoBean.getStrVideoDownloadRemoteFileUrl() != null)
			values.put("video_download_remote_file_url", localVideoBean.getStrVideoDownloadRemoteFileUrl());
		if(localVideoBean.getStrAbstract() != null)
			values.put("video_abstract", localVideoBean.getStrAbstract());
		SQLiteDatabase db = mDBHelper.getUserDb();
		return db.insert(DbInfo.TB_LOCAL_VIDEO, "video_id", values);
	}
	
	/**
	 *  删除local_video所有数据
	 * @return
	 */
	public boolean deleteAllDataInLocalVideo()
	{
		SQLiteDatabase db = mDBHelper.getUserDb();
		return db.delete(DbInfo.TB_LOCAL_VIDEO, null, null) > 0;
	}
	// 删除指定数据
	public boolean deleteDataInLocalVideoById(String videoId)
	{
		SQLiteDatabase db = mDBHelper.getUserDb();
		return db.delete(DbInfo.TB_LOCAL_VIDEO, "video_id=?" , new String[]{videoId}) > 0;
	}
	
	/**
	 * 删除本地（不包括未下载完的）数据
	 * @param status 0 本地视频(包括下载完的)数据, 1 未下载完成数据
	 * @return 
	 */
	public boolean deleteLocalVideoByStatus(String status)
	{
		SQLiteDatabase db = mDBHelper.getUserDb();
		return db.delete(DbInfo.TB_LOCAL_VIDEO, "video_download_status=?" , new String[]{status}) > 0;
	}
	// 通过路径删除指定数据
	public boolean deleteDataInLocalVideoByPath(String videoPath)
	{
		SQLiteDatabase db = mDBHelper.getUserDb();
		return db.delete(DbInfo.TB_LOCAL_VIDEO, "video_local_path=?", new String[]{videoPath}) > 0;
	}
	// 通过Cursor来查询所有数据
	public LinkedList<SSVideoLocalVideoBean> fetchAllDataInLocalVideo()
	{
		SQLiteDatabase db = mDBHelper.getUserDb();
		Cursor cursor = db.query(DbInfo.TB_LOCAL_VIDEO, new String[]{ "video_id", "video_name", "speaker", "category_id", "cover_name", "video_local_path", "video_file_length",
				"video_file_name", "video_download_status", "video_download_progress", "video_download_remote_cover_url","video_download_remote_file_url", "video_abstract"}, 
				null, null, null, null, null);
		LinkedList <SSVideoLocalVideoBean> localVideoList = new LinkedList<SSVideoLocalVideoBean>();
		while(cursor.moveToNext()) {
			SSVideoLocalVideoBean localVideoBean = new SSVideoLocalVideoBean();
			localVideoBean.setStrVideoId(cursor.getString(cursor.getColumnIndex("video_id")));
			localVideoBean.setStrVideoName(cursor.getString(cursor.getColumnIndex("video_name")));
			localVideoBean.setStrSpeaker(cursor.getString(cursor.getColumnIndex("speaker")));
			localVideoBean.setStrCateId(cursor.getString(cursor.getColumnIndex("category_id")));
			localVideoBean.setStrCoverName(cursor.getString(cursor.getColumnIndex("cover_name")));
			localVideoBean.setStrVideoLocalPath(cursor.getString(cursor.getColumnIndex("video_local_path")));
			localVideoBean.setnVideoFileLength(cursor.getInt(cursor.getColumnIndex("video_file_length")));
			localVideoBean.setStrVideoFileName(cursor.getString(cursor.getColumnIndex("video_file_name")));
			localVideoBean.setnVideoDownloadStatus(cursor.getInt(cursor.getColumnIndex("video_download_status")));
			localVideoBean.setnVideoDownloadProgress(cursor.getInt(cursor.getColumnIndex("video_download_progress")));
			localVideoBean.setStrVideoDownloadRemoteCoverUrl(cursor.getString(cursor.getColumnIndex("video_download_remote_cover_url")));
			localVideoBean.setStrVideoDownloadRemoteFileUrl(cursor.getString(cursor.getColumnIndex("video_download_remote_file_url")));
			localVideoBean.setStrAbstract(cursor.getString(cursor.getColumnIndex("video_abstract")));
			localVideoList.addFirst(localVideoBean);
		}
		cursor.close();
		return localVideoList;
	}
	// 通过subject 查询指定的数据
	public LinkedList<SSVideoLocalVideoBean> fetchDataInLocalVideoBySubject(String subject) throws SQLException
	{
		SQLiteDatabase db = mDBHelper.getUserDb();
		Cursor cursor = db.query(DbInfo.TB_LOCAL_VIDEO, new String[]{  "video_id", "video_name", "speaker", "category_id", "cover_name", "video_local_path", "video_file_length",
				"video_file_name", "video_download_status", "video_download_progress", "video_download_remote_cover_url","video_download_remote_file_url", "video_abstract"}, 
				"category_id=?" , new String[]{subject}, null, null, null);
		LinkedList <SSVideoLocalVideoBean> localVideoList = new LinkedList<SSVideoLocalVideoBean>();
		while(cursor.moveToNext()) {
			SSVideoLocalVideoBean localVideoBean = new SSVideoLocalVideoBean();
			localVideoBean.setStrVideoId(cursor.getString(cursor.getColumnIndex("video_id")));
			localVideoBean.setStrVideoName(cursor.getString(cursor.getColumnIndex("video_name")));
			localVideoBean.setStrSpeaker(cursor.getString(cursor.getColumnIndex("speaker")));
			localVideoBean.setStrCateId(cursor.getString(cursor.getColumnIndex("category_id")));
			localVideoBean.setStrCoverName(cursor.getString(cursor.getColumnIndex("cover_name")));
			localVideoBean.setStrVideoLocalPath(cursor.getString(cursor.getColumnIndex("video_local_path")));
			localVideoBean.setnVideoFileLength(cursor.getInt(cursor.getColumnIndex("video_file_length")));
			localVideoBean.setStrVideoFileName(cursor.getString(cursor.getColumnIndex("video_file_name")));
			localVideoBean.setnVideoDownloadStatus(cursor.getInt(cursor.getColumnIndex("video_download_status")));
			localVideoBean.setnVideoDownloadProgress(cursor.getInt(cursor.getColumnIndex("video_download_progress")));
			localVideoBean.setStrVideoDownloadRemoteCoverUrl(cursor.getString(cursor.getColumnIndex("video_download_remote_cover_url")));
			localVideoBean.setStrVideoDownloadRemoteFileUrl(cursor.getString(cursor.getColumnIndex("video_download_remote_file_url")));
			localVideoBean.setStrAbstract(cursor.getString(cursor.getColumnIndex("video_abstract")));
			localVideoList.addFirst(localVideoBean);
		}
		cursor.close();
		return localVideoList;
	}
	// 通过videoId 查询指定的数据
	public SSVideoLocalVideoBean fetchDataInLocalVideoById(String videoId) throws SQLException
	{
		SQLiteDatabase db = mDBHelper.getUserDb();
		Cursor cursor = db.query(DbInfo.TB_LOCAL_VIDEO, new String[]{  "video_id", "video_name", "speaker", "category_id", "cover_name", "video_local_path", "video_file_length",
				"video_file_name", "video_download_status", "video_download_progress", "video_download_remote_cover_url","video_download_remote_file_url", "video_abstract"}, 
				"video_id=?" , new String[]{videoId}, null, null, null);
		SSVideoLocalVideoBean localVideoBean = null;
		if(cursor.moveToNext()) {
			 localVideoBean = new SSVideoLocalVideoBean();
			localVideoBean.setStrVideoId(cursor.getString(cursor.getColumnIndex("video_id")));
			localVideoBean.setStrVideoName(cursor.getString(cursor.getColumnIndex("video_name")));
			localVideoBean.setStrSpeaker(cursor.getString(cursor.getColumnIndex("speaker")));
			localVideoBean.setStrCateId(cursor.getString(cursor.getColumnIndex("category_id")));
			localVideoBean.setStrCoverName(cursor.getString(cursor.getColumnIndex("cover_name")));
			localVideoBean.setStrVideoLocalPath(cursor.getString(cursor.getColumnIndex("video_local_path")));
			localVideoBean.setnVideoFileLength(cursor.getInt(cursor.getColumnIndex("video_file_length")));
			localVideoBean.setStrVideoFileName(cursor.getString(cursor.getColumnIndex("video_file_name")));
			localVideoBean.setnVideoDownloadStatus(cursor.getInt(cursor.getColumnIndex("video_download_status")));
			localVideoBean.setnVideoDownloadProgress(cursor.getInt(cursor.getColumnIndex("video_download_progress")));
			localVideoBean.setStrVideoDownloadRemoteCoverUrl(cursor.getString(cursor.getColumnIndex("video_download_remote_cover_url")));
			localVideoBean.setStrVideoDownloadRemoteFileUrl(cursor.getString(cursor.getColumnIndex("video_download_remote_file_url")));
			localVideoBean.setStrAbstract(cursor.getString(cursor.getColumnIndex("video_abstract")));
		}
		cursor.close();
		return localVideoBean;
	}
	// 查询local_video中所有未下载完成的数据
	public LinkedList<SSVideoLocalVideoBean> fetchDownloadDataInLocalVideo() throws SQLException
	{
		SQLiteDatabase db = mDBHelper.getUserDb();
		Cursor cursor = db.query(DbInfo.TB_LOCAL_VIDEO, new String[]{  "video_id", "video_name", "speaker", "category_id", "cover_name", "video_local_path", "video_file_length",
				"video_file_name", "video_download_status", "video_download_progress", "video_download_remote_cover_url","video_download_remote_file_url", "video_abstract"}, 
				"video_download_status>0" , null, null, null, null);
		LinkedList <SSVideoLocalVideoBean> localVideoList = new LinkedList<SSVideoLocalVideoBean>();
		while(cursor.moveToNext()) {
			SSVideoLocalVideoBean localVideoBean = new SSVideoLocalVideoBean();
			localVideoBean.setStrVideoId(cursor.getString(cursor.getColumnIndex("video_id")));
			localVideoBean.setStrVideoName(cursor.getString(cursor.getColumnIndex("video_name")));
			localVideoBean.setStrSpeaker(cursor.getString(cursor.getColumnIndex("speaker")));
			localVideoBean.setStrCateId(cursor.getString(cursor.getColumnIndex("category_id")));
			localVideoBean.setStrCoverName(cursor.getString(cursor.getColumnIndex("cover_name")));
			localVideoBean.setStrVideoLocalPath(cursor.getString(cursor.getColumnIndex("video_local_path")));
			localVideoBean.setnVideoFileLength(cursor.getInt(cursor.getColumnIndex("video_file_length")));
			localVideoBean.setStrVideoFileName(cursor.getString(cursor.getColumnIndex("video_file_name")));
			localVideoBean.setnVideoDownloadStatus(cursor.getInt(cursor.getColumnIndex("video_download_status")));
			localVideoBean.setnVideoDownloadProgress(cursor.getInt(cursor.getColumnIndex("video_download_progress")));
			localVideoBean.setStrVideoDownloadRemoteCoverUrl(cursor.getString(cursor.getColumnIndex("video_download_remote_cover_url")));
			localVideoBean.setStrVideoDownloadRemoteFileUrl(cursor.getString(cursor.getColumnIndex("video_download_remote_file_url")));
			localVideoBean.setStrAbstract(cursor.getString(cursor.getColumnIndex("video_abstract")));
			localVideoList.addFirst(localVideoBean);
		}
		cursor.close();
		return localVideoList;
	}
	// 通过videoName查询指定的数据
	public Cursor fetchDataInLocalVideoByName(String videoName) throws SQLException
	{
		SQLiteDatabase db = mDBHelper.getUserDb();
		Cursor cursor = db.query(DbInfo.TB_LOCAL_VIDEO, new String[]{  "video_id", "video_name", "speaker", "category_id", "cover_name", "video_local_path", "video_file_length",
				"video_file_name", "video_download_status", "video_download_progress", "video_download_remote_cover_url","video_download_remote_file_url", "video_abstract"}, 
				"video_name=?" , new String[]{videoName}, null, null, null);
		return cursor;
	}
	
	// 通过视频的完整路径查询指定数据
	public Cursor fetchDataInLocalVideoByRemoteFilePath(String remoteFilePath)
	{
		SQLiteDatabase db = mDBHelper.getUserDb();
		Cursor cursor = db.query(DbInfo.TB_LOCAL_VIDEO, new String[]{   "video_id", "video_name", "speaker", "category_id", "cover_name", "video_local_path", "video_file_length",
				"video_file_name", "video_download_status", "video_download_progress", "video_download_remote_cover_url","video_download_remote_file_url", "video_abstract"}, 
				"video_download_remote_file_url=?" , new String[]{remoteFilePath}, null, null, null);
		if(cursor != null)
			cursor.moveToFirst();
		return cursor;
	}
	
	public boolean isLocalVideoEmpty() {
		SQLiteDatabase db = mDBHelper.getUserDb();
		Cursor cursor = db.query(DbInfo.TB_LOCAL_VIDEO + " limit 1", new String[]{"video_id"}, null, null, null, null, null);
		boolean isEmpty = cursor.getCount() <= 0;
		cursor.close();
		return isEmpty;
	}
	// 更新一条数据
	public boolean updateDataInLocalVideo(String videoId, SSVideoLocalVideoBean localVideoBean)
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
		if(localVideoBean.getStrVideoFileName() != null)
			values.put("video_file_name", localVideoBean.getStrVideoFileName());
		if(localVideoBean.getStrVideoLocalPath() != null)
			values.put("video_local_path", localVideoBean.getStrVideoLocalPath());
		if(localVideoBean.getnVideoDownloadStatus() != null) {
			values.put("video_download_status", localVideoBean.getnVideoDownloadStatus().intValue());
//			Log.i(TAG, "video download status =======" + localVideoBean.getnVideoDownloadStatus().intValue());
		}
		if(localVideoBean.getnVideoDownloadProgress() != null)
			values.put("video_download_progress", localVideoBean.getnVideoDownloadProgress().intValue());
		if(localVideoBean.getnVideoFileLength() != null)
			values.put("video_file_length", localVideoBean.getnVideoFileLength().intValue());
		if(localVideoBean.getStrVideoDownloadRemoteCoverUrl() != null)
			values.put("video_download_remote_cover_url", localVideoBean.getStrVideoDownloadRemoteCoverUrl());
		if(localVideoBean.getStrVideoDownloadRemoteFileUrl() != null)
			values.put("video_download_remote_file_url", localVideoBean.getStrVideoDownloadRemoteFileUrl());
		if(localVideoBean.getStrAbstract() != null)
			values.put("video_abstract", localVideoBean.getStrAbstract());
		SQLiteDatabase db = mDBHelper.getUserDb();
		return db.update(DbInfo.TB_LOCAL_VIDEO, values, "video_id=?" ,new String[]{ videoId}) > 0;
	}
	// 通过全路径更新一条数据
	public boolean updateDataInLocalVideoByRemoteFilePath(String remoteFilePath, SSVideoLocalVideoBean localVideoBean)
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
		if(localVideoBean.getStrVideoFileName() != null)
			values.put("video_file_name", localVideoBean.getStrVideoFileName());
		if(localVideoBean.getStrVideoLocalPath() != null)
			values.put("video_local_path", localVideoBean.getStrVideoLocalPath());
		if(localVideoBean.getnVideoDownloadStatus() != null)
			values.put("video_download_status", localVideoBean.getnVideoDownloadStatus().intValue());
		if(localVideoBean.getnVideoDownloadProgress() != null)
			values.put("video_download_progress", localVideoBean.getnVideoDownloadProgress().intValue());
		if(localVideoBean.getnVideoFileLength() != null)
			values.put("video_file_length", localVideoBean.getnVideoFileLength().intValue());
		if(localVideoBean.getStrVideoDownloadRemoteCoverUrl() != null)
			values.put("video_download_remote_cover_url", localVideoBean.getStrVideoDownloadRemoteCoverUrl());
		if(localVideoBean.getStrVideoDownloadRemoteFileUrl() != null)
			values.put("video_download_remote_file_url", localVideoBean.getStrVideoDownloadRemoteFileUrl());
		if(localVideoBean.getStrAbstract() != null)
			values.put("video_abstract", localVideoBean.getStrAbstract());
		SQLiteDatabase db = mDBHelper.getUserDb();
		return db.update(DbInfo.TB_LOCAL_VIDEO, values, "video_download_remote_file_url=?" ,new String[]{ remoteFilePath}) > 0;
	}
	public boolean updateDownloadingToPause()
	{
		ContentValues values = new ContentValues();
		values.put("video_download_status", 2);
		SQLiteDatabase db = mDBHelper.getUserDb();
		return db.update(DbInfo.TB_LOCAL_VIDEO, values, "video_download_status=1", null) > 0;
	}
	// 查询分类列表所有信息
	public LinkedList<SSVideoCategoryBean> fetchAllCategory() {
		SQLiteDatabase db = mDBHelper.getUserDb();
		Cursor cursor = db.query(DbInfo.TB_VIDEO_CATEGORY, new String[]{   "category_id", "category_name"}, 
				null , null, null, null, null);
		LinkedList <SSVideoCategoryBean> categoryList = null;
		if(cursor != null) {
			categoryList = new LinkedList<SSVideoCategoryBean>();
			while(cursor.moveToNext()) {
				SSVideoCategoryBean categoryBean = new SSVideoCategoryBean();
				categoryBean.setStrCateId(cursor.getString(cursor.getColumnIndex("category_id")));
				categoryBean.setStrCateName(cursor.getString(cursor.getColumnIndex("category_name")));
				categoryList.add(categoryBean);
			}
		}
		cursor.close();
		return categoryList;
	}
	// 通过Id查询分类信息
	public SSVideoCategoryBean fetchCategoryById(String strCateId) {
		SQLiteDatabase db = mDBHelper.getUserDb();
		Cursor cursor = db.query(DbInfo.TB_VIDEO_CATEGORY, new String[]{   "category_id", "category_name"}, 
				"category_id=?" , new String[]{strCateId}, null, null, null);
		SSVideoCategoryBean categoryBean = null;
		if(cursor != null) {
			if(cursor.moveToNext()) {
				categoryBean = new SSVideoCategoryBean();
				categoryBean.setStrCateId(cursor.getString(cursor.getColumnIndex("category_id")));
				categoryBean.setStrCateName(cursor.getString(cursor.getColumnIndex("category_name")));
			}
			cursor.close();
		}
		return categoryBean;
	}
	// 通过name查询分类信息
	public SSVideoCategoryBean fetchCategoryByName(String strCateName) {
		SQLiteDatabase db = mDBHelper.getUserDb();
		Cursor cursor = db.query(DbInfo.TB_VIDEO_CATEGORY, new String[]{"category_id", "category_name"}, 
				"category_name=?" , new String[]{strCateName}, null, null, null);
		SSVideoCategoryBean categoryBean = null;
		if(cursor != null) {
			if(cursor.moveToNext()) {
				categoryBean = new SSVideoCategoryBean();
				categoryBean.setStrCateId(cursor.getString(cursor.getColumnIndex("category_id")));
				categoryBean.setStrCateName(cursor.getString(cursor.getColumnIndex("category_name")));
			}
			cursor.close();
		}
		return categoryBean;
	}
	// 插入分类数据
	public long insertIntoCategory(SSVideoCategoryBean cateBean) {
		ContentValues values = new ContentValues();
		if(cateBean.getStrCateId() != null)
			values.put("category_id", cateBean.getStrCateId());
		if(cateBean.getStrCateName() != null)
			values.put("category_name", cateBean.getStrCateName());
		SQLiteDatabase db = mDBHelper.getUserDb();
		return db.insert(DbInfo.TB_VIDEO_CATEGORY, "category_id", values);
	}
	// 删除分类列表中所有数据
	public boolean deleteAllCategory() {
		SQLiteDatabase db = mDBHelper.getUserDb();
		return db.delete(DbInfo.TB_VIDEO_CATEGORY, null, null) > 0;
	}
	// 删除分类列表中指定id的数据
	public boolean deleteCategoryById(String categoryId) {
		SQLiteDatabase db = mDBHelper.getUserDb();
		return db.delete(DbInfo.TB_VIDEO_CATEGORY, "category_id=?" , new String[]{categoryId}) > 0;
	}
	
	// 插入默认分类数据
	private void insertDefaultCategory(SQLiteDatabase db) {
		insertCategory(db, "1", "文化");
		insertCategory(db, "2", "历史");
		insertCategory(db, "3", "科技");
		insertCategory(db, "4", "哲学");
		insertCategory(db, "5", "政法");
		insertCategory(db, "6", "文艺");
	}

	private long insertCategory(SQLiteDatabase db, String sId, String sName) {
		ContentValues values = new ContentValues();
		if(sId != null)
			values.put("category_id", sId);
		if(sName != null)
			values.put("category_name", sName);
		return db.insert(DbInfo.TB_VIDEO_CATEGORY, "category_id", values);
	}
	
	public int checkAllCategory() {
		int rtVal = getCategoryCount();
		if(rtVal < 1) {
			SQLiteDatabase db = mDBHelper.getUserDb();
			insertDefaultCategory(db);
		}
		return rtVal;
	}
	
	public int getCategoryCount() {
		int rtVal = 0;
		SQLiteDatabase db = mDBHelper.getUserDb();
		Cursor cursor = db.query(DbInfo.TB_VIDEO_CATEGORY, new String[]{"category_id", "category_name"}, 
				null , null, null, null, null);
		if(cursor != null) {
			rtVal = cursor.getCount();
		}
		cursor.close();
		return rtVal;
	}
		
}

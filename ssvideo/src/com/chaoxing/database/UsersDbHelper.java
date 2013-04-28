package com.chaoxing.database;

import java.io.File;
import java.io.IOException;

import com.chaoxing.util.UsersUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UsersDbHelper {
	public static final int DB_VERSION = 2;
	private final static String TB_PLAY_LIST = "play_list";
	private final static String TB_LOCAL_VIDEO = "local_video";
	private final static String TB_VIDEO_CATEGORY = "video_category";
	public static final String TABLE_DB_VERSION = "db_version";
	
	private SQLiteDatabase userDb;
	
	public UsersDbHelper(Context context){
		this.userDb = null;
	}
	
	public synchronized SQLiteDatabase getUserDb(){
		if(this.userDb == null)
		{
			String usersDir = UsersUtil.getUserDatabaseFileName();
			if (!UsersUtil.existSDCard()) {
				usersDir = UsersUtil.getSystermDatabaseFileName();
			}
			File fDb = new File(usersDir);
			boolean newDb = false;
			if(!fDb.exists()){
				try {
					fDb.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
				newDb = true;
			}
			this.userDb = SQLiteDatabase.openOrCreateDatabase(fDb, null);
			if(newDb) {
				initUserDb();
			}
			else {
				int oldVersion = getDbVersion(this.userDb);
				if(oldVersion < DB_VERSION){
//					initUserDb();
					if(oldVersion == 0) {
						initUserDb();
					}

					if(oldVersion == 1) {
						insertDefaultCategory();
					}
					updateDBVersion(DB_VERSION);
				}
					
			}
		}
		return this.userDb;
	}
	
	public synchronized void close(){
		if(this.userDb == null)
			return;
		if(this.userDb.isOpen())
			this.userDb.close();
		this.userDb = null;
	}
	
	private int getDbVersion(SQLiteDatabase db){
		int version = -1;//0//modify_by_xin_2013-4-23.
		Cursor cursor = null;
		try{
			cursor = db.query(UsersDbHelper.TABLE_DB_VERSION, null, null, null, null, null, null);	
		}catch (Exception e) {
			return version;
		}
		
   		int count = cursor.getCount();
   		if(count > 0){
   			cursor.moveToFirst();
    		version = cursor.getInt(cursor.getColumnIndex("version"));
   		}
   		cursor.close();
   		return version;
	}
	
	private void initUserDb(){
		if(this.userDb == null)
			return;
		long currentTime = System.currentTimeMillis();
		String sql = "create table if not exists " + UsersDbHelper.TB_LOCAL_VIDEO + "(video_id VARCHAR PRIMARY KEY,video_name VARCHAR,  video_file_length INTEGER NOT NULL  DEFAULT 0, " +
					"speaker VARCHAR, category_id VARCHAR, cover_name VARCHAR, video_file_name VARCHAR, video_local_path VARCHAR, " +
					"video_download_status INTEGER NOT NULL  DEFAULT 0, video_download_progress INTEGER NOT NULL  DEFAULT 0," +
					"video_download_remote_file_url VARCHAR DEFAULT 0, video_download_remote_cover_url VARCHAR DEFAULT 0, video_abstract VARCHAR)";
		this.userDb.execSQL(sql);
		
		sql = "create table if not exists " + UsersDbHelper.TB_PLAY_LIST + "(video_id VARCHAR PRIMARY KEY, video_name VARCHAR, speaker VARCHAR, " +
					"category_id VARCHAR, cover_name VARCHAR, video_file_name VARCHAR, current_play_time INTEGER NOT NULL  DEFAULT 0," +
					" video_type INTEGER NOT NULL  DEFAULT 0, remote_cover_url VARCHAR NOT NULL  DEFAULT 0, video_local_path VARCHAR, " +
					"m3u8_url VARCHAR NOT NULL  DEFAULT 0, series_id VARCHAR NOT NULL  DEFAULT 0, current_play INTEGER NOT NULL  DEFAULT 0, " +
					"playtimes VARCHAR, date VARCHAR, score VARCHAR DEFAULT 0, scoreCount VARCHAR DEFAULT 0, video_abstract VARCHAR)";
		this.userDb.execSQL(sql);
		
		sql = "create table if not exists " + UsersDbHelper.TB_VIDEO_CATEGORY + "(category_id VARCHAR PRIMARY KEY, category_name VARCHAR)";
		this.userDb.execSQL(sql);
		
		sql = "create table if not exists " + UsersDbHelper.TABLE_DB_VERSION + "(version int,remark TEXT,update_time INTEGER)";
		this.userDb.execSQL(sql);
		this.userDb.execSQL("INSERT INTO "+ UsersDbHelper.TABLE_DB_VERSION +" (version, remark, update_time)"
                + " VALUES (" + DB_VERSION + ", '', " + currentTime + ");");
		
		insertDefaultCategory();
	}
//	
//	private void bookMarkTableAddField() {
//		if(this.userDb == null)
//			return;
//		try {
////			this.userDb.execSQL(BookmarkMgrEx.DATABASE_ADD_COLUMNS_RECORD);
//		 } catch (SQLException e) {
////			 Log.e("UsersDbHelper", "update bookmark table add field '"+ BookmarkMgrEx.BOOKMARK_RECORD +"' failed.");
//		 }
//	}
	
	private void updateDBVersion(int newVer) {
		if(this.userDb == null)
			return;
		long currentTime = System.currentTimeMillis();
		 try {
			 this.userDb.execSQL("UPDATE "+ UsersDbHelper.TABLE_DB_VERSION +
					 " SET version="+ newVer +", update_time="+ currentTime + ";");
		 } catch (SQLException e) {
			 Log.e("UsersDbHelper", "update database version failed. newVer ="+ newVer);
		 }
		
	}
	
	public boolean existCategory(SQLiteDatabase db, String cateName) {
		boolean rtVal = false;
		Cursor cursor = db.query(TB_VIDEO_CATEGORY, new String[]{"category_id", "category_name"}, 
				"category_name=?" , new String[]{cateName}, null, null, null);
		if(cursor != null) {
			if(cursor.getCount() > 0) {
				rtVal = true;
			}
		}
		cursor.close();
		return rtVal;
	}
	
	// 插入默认分类数据
	private void insertDefaultCategory(){
		if(this.userDb == null)
			return;
		insertCategory(this.userDb, "1", "文化");
		insertCategory(this.userDb, "2", "历史");
		insertCategory(this.userDb, "3", "科技");
		insertCategory(this.userDb, "4", "哲学");
		insertCategory(this.userDb, "5", "政法");
		insertCategory(this.userDb, "6", "文艺");
	}
	
	private long insertCategory(SQLiteDatabase db, String sId, String sName){
		if(existCategory(db, sName)) return 0;
		ContentValues values = new ContentValues();
		if(sId != null)
			values.put("category_id", sId);
		if(sName != null)
			values.put("category_name", sName);
		return db.insert(TB_VIDEO_CATEGORY, "category_id", values);
	}
	
}

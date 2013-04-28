package com.chaoxing.database;

/**
 * @author xin_yueguang.
 * @version 2012-12-28.
 */

import java.sql.SQLException;
import java.util.LinkedList;

import com.chaoxing.document.DbInfo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class WidgetVideoDao implements IWidgetDao{
	
	private UsersDbHelper mDBHelper = null;
	public WidgetVideoDao(Context context, String flags) {
//		mContext = context;
		mDBHelper = new UsersDbHelper(null);
//		Log.d(TAG, "userDB: flags ="+ flags);
	}
	
	public void closeUserDb() {
		if(mDBHelper != null) {
			mDBHelper.close();
		}
	}
	
	// 通过videoId 查询指定的数据
	public SSVideoLocalVideoBean fetchDataInLocalVideoById(String videoId) throws SQLException
	{
		SQLiteDatabase db = mDBHelper.getUserDb();
		Cursor cursor = db.query(DbInfo.TB_LOCAL_VIDEO, new String[]{"video_id", "video_name", "speaker", "category_id", "cover_name", "video_local_path", "video_file_length",
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

	@Override
	public SSVideoLocalVideoBean getDataInLocalVideoById(String videoId) {
		// TODO Auto-generated method stub
		SSVideoLocalVideoBean videoBean = null;
		try {
			videoBean = fetchDataInLocalVideoById(videoId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return videoBean;
	}

	@Override
	public LinkedList<SSVideoCategoryBean> getAllCategory() {
		// TODO Auto-generated method stub
		SQLiteDatabase db = mDBHelper.getUserDb();
		Cursor cursor = db.query(DbInfo.TB_VIDEO_CATEGORY, new String[]{"category_id", "category_name"}, 
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

	@Override
	public void close() {
		// TODO Auto-generated method stub
		closeUserDb();
	}
}
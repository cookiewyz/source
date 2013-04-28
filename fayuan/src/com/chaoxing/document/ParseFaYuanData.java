package com.chaoxing.document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.chaoxing.database.SSVideoPlayListBean;
import com.chaoxing.util.Md5Util;
import com.chaoxing.util.ToolsLog;

/**
 * 
 * @author YongZheng
 * @version 2013-3-6
 */
public class ParseFaYuanData implements FaYuanInterface{

	private static final String TAG = "ParseWriteData  :   ";
	public SeriesInfo mSeriesInfo;
	private CategoryNameInfo mCategoryNameInfo;

	ArrayList<SeriesInfo> list = new ArrayList<SeriesInfo>();	
	private static boolean isFaYuanFlage = false;
	private static ParseFaYuanData mFaYuanData;
	public synchronized static ParseFaYuanData getInstance () {
		if (mFaYuanData == null){
			mFaYuanData = new ParseFaYuanData();
		}
		return mFaYuanData;
	}
	private ParseFaYuanData(){
		
	}
	
	public boolean isFaYuanFlage() {
		return isFaYuanFlage;
	}

	public void setFaYuanFlage(boolean FaYuanFlage) {
		isFaYuanFlage = FaYuanFlage;
	}

	public String readStream(InputStream input) {
		BufferedReader in = null;
		in = new BufferedReader(new InputStreamReader(input));
		StringBuffer buffer = new StringBuffer();

		String line = "";
		try {
			while ((line = in.readLine()) != null) {
				buffer.append(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String str = buffer.toString();
		if (str != null && !str.equals("")) {
			str = str.substring(str.indexOf("{"));
		} 
		return str;
	}

	public String readStream(InputStream input, String udecode) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(input,udecode));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		StringBuffer buffer = new StringBuffer();
		String line = "";
		try {
			while ((line = in.readLine()) != null) {
				buffer.append(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String str = buffer.toString();
		if (str != null ) {
			str = str.substring(str.indexOf("{"));
		} 
		return str;
	}
	
	/**
	 * 将输入的字符串编码转换为UTF-8格式
	 * @param str 需要转换的字符串
	 * @return 转换为UTF-8后的字符串
	 */
	private String getUTFString(String str){
		String sTemp = str;
		try {
			sTemp = URLEncoder.encode(str, "UTF-8");
  		} catch (UnsupportedEncodingException e1) {
  			// TODO Auto-generated catch block
  			e1.printStackTrace();
  		}
		return sTemp;
	}
	public UserInformation userInfo;
	public void LoginFaYuan(String uName, String uPwd) {
		String path = "http://passport.lawy.com.cn/userauth.shtml?uname="+uName+"&password="+uPwd+"&ajax=true";
		try {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			JSONObject object = new JSONObject(readStream(conn.getInputStream()));
			userInfo = new UserInformation();
			if (object.getString("result").equals("true")) {
				userInfo.setUserName(uName);
				userInfo.setPassword(uPwd);
				userInfo.setUserStatus(1);
				JSONObject item = object.getJSONObject("msg");
				userInfo.setRegDate(item.getString("regdate"));
				userInfo.setuId(item.getInt("uid"));
				setFaYuanFlage(true);
			} else {
				userInfo.setUserStatus(0);
				setFaYuanFlage(false);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	public ArrayList<SeriesInfo> getSearch(String key, int count, int callback) {
		ToolsLog.LOG_DBG(TAG+" getSearch  key     "+key+" count = "+count);
		String keyTemp = getUTFString(key);
		Log.e("wyz", "getSearch   keyTemp =  "+keyTemp);
		ArrayList<SeriesInfo> list = new ArrayList<SeriesInfo>();
		//String path = "http://video.chaoxing.com:9999/searchSeries.shtml?sw="+keyTemp+"&count="+count;
		String path = "http://bookcxpad.lawy.com.cn/searchSeries.shtml?sw="+keyTemp+"&count="+count+"&length=100&callback=1";
		Log.e("wyz", "getSearch    "+key+"path = "+path);
		URL url;
		try {
			url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			JSONObject object = new JSONObject(readStream(conn.getInputStream(),"GBK"));
			JSONArray array = object.getJSONArray("datas");
			for (int i = 0; i < array.length(); i++) {
				JSONObject item = array.getJSONObject(i);
				mSeriesInfo = new SeriesInfo();
				mSeriesInfo.setCover(item.getString("Img"));
				mSeriesInfo.setTitle(StringFilter(item.getString("seriesName")));
				mSeriesInfo.setSerid(StringFilter(String.valueOf(item.getInt("serieId"))));
				mSeriesInfo.setKeySpeaker(StringFilter(item.getString("speaker")));
				//SeriesInfo.setSpeakerId(item.getString("speakerId"));
				mSeriesInfo.setSchoolName(StringFilter(item.getString("schoolName")));
				mSeriesInfo.setSchoolId(StringFilter(item.getString("schoolId")));
				list.add(mSeriesInfo);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	public String StringFilter(String str) {
		// 只允许字母和数字
		// String regEx = "[^a-zA-Z0-9]";
		// 清除掉所有特殊字符
		String regEx = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？e m]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	
	public ArrayList<SeriesInfo> getHotSearch(int count, int callback) {
		//String path = "http://video.chaoxing.com:9999/getHotSeries.shtml?action=hot&count=6";
		String path = "http://bookcxpad.lawy.com.cn/getHotSeries.shtml?action=hot&count=6&callback=0";
		Log.e("wyz", "getHotSearch  path = "+path);
		try {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			JSONObject object = new JSONObject(readStream(conn.getInputStream(),"GBK"));
			JSONArray array = object.getJSONArray("datas");
			for (int i = 0; i < array.length(); i++) {
				JSONObject item = array.getJSONObject(i);	
				mSeriesInfo = new SeriesInfo();
				mSeriesInfo.setTitle(item.getString("name"));
				mSeriesInfo.setCover(item.getString("img"));
				//SeriesInfo.setAbstracts(item.getString("introduction"));
				//List<OtherInfo> info = new ArrayList<OtherInfo>();
				//for (int j = 0; j < object.length(); j++) {
					JSONObject obj = item.getJSONObject("otherInfo");
					//SeriesInfo.setAbstracts(item.getString("introduction"));
					mSeriesInfo.setScore(obj.getString("score"));
					mSeriesInfo.setScoreCount(obj.getString("scoreNum"));
					mSeriesInfo.setSerid(String.valueOf(obj.getInt("seriesNo")));
					JSONObject series = obj.getJSONObject("series");
					mSeriesInfo.setAbstracts(series.getString("introduction"));
					//JSONArray ary = obj.getJSONArray("schools");
					//for (int k = 0; k < ary.length(); k++) {
						//JSONObject time = ary.getJSONObject(k);
//					    JSONObject time = obj.getJSONObject("schools");
//						int tem = time.getInt("schoolNo");
//						mSeriesInfo.setSchoolId(String.valueOf(tem));
//						mSeriesInfo.setSchoolName(time.getString("name"));
//						mSeriesInfo.setSchoolImg(time.getString("imgPath"));
				//	}
				//}			
				//SeriesInfo.setListInfo(info);
				list.add(mSeriesInfo);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	
	public ArrayList<SSVideoPlayListBean> getPlayList(int serieId, int callback) {
		SSVideoPlayListBean  playList = null;
		VideoPathInfo pathInfo = null;
		ArrayList<SSVideoPlayListBean> mList = new ArrayList<SSVideoPlayListBean>();
		String mD5Str = Md5Util.strToMd5_32(serieId+"chaoxing");
		//String path = "http://video.chaoxing.com:9999/getVideoInfo.shtml?serieId="+serieId+"&key="+mD5Str;
		String path = "http://bookcxpad.lawy.com.cn/getVideoInfo.shtml?serieId="+serieId+"&key="+mD5Str+"&callback=0";
		Log.e("wyz", " getPlayList  path =  "+path);
		ToolsLog.LOG_DBG(TAG+" getPlayList  path =  "+path);
		URL url;
		try {
			url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			JSONObject obj = new JSONObject(readStream(conn.getInputStream(),"GBK"));
			if (obj.getString("status").equals("true")) {
				JSONArray array = obj.getJSONArray("datas");
				for (int i = 0; i < array.length(); i++) {
					playList = new SSVideoPlayListBean();
					JSONObject item = array.getJSONObject(i);
					playList.setStrVideoName(item.getString("name"));
					playList.setStrVideoId(String.valueOf(item.getInt("videoId")));
					//List<OtherInfo> listInfo = new ArrayList<OtherInfo>();
					for (int j = 0; j < item.length(); j++) {
						JSONObject obj1 = item.getJSONObject("videoInfo");
						//OtherInfo othinfo = new OtherInfo();
						List<VideoPathInfo> listpath = new ArrayList<VideoPathInfo>();	
						//for (int k = 0; k < obj1.length(); k++) {
							JSONObject videopath = obj1.getJSONObject("videoPathInfo");
							pathInfo = new VideoPathInfo();
							JSONObject json = videopath.getJSONObject("bj211data2ssvideocn");
							pathInfo.setServerpath(json.getString("serverpath"));
							pathInfo.setLine(json.getInt("line"));
							pathInfo.setWmvpath(json.getString("wmvpath"));
							pathInfo.setFlvpath(json.getString("flvpath"));
							pathInfo.setSoundpath(json.getString("soundpath"));
							pathInfo.setMp4path(json.getString("mp4path"));						
							listpath.add(pathInfo);
						//}
						playList.setList(listpath);
						//listInfo.add(othinfo);		
					}
					playList.setStrVideoRemoteUrl("http://"+pathInfo.getServerpath()+pathInfo.getMp4path());
					//playList.setListInfo(listInfo);
					mList.add(playList);
				}
			}			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}	
		return mList;
	}

	public ArrayList<SSVideoPlayListBean> recentPlayList(int start, int end, int userId) {
		SSVideoPlayListBean  playList = null;
		ArrayList<SSVideoPlayListBean>  list = new ArrayList<SSVideoPlayListBean>();
		//String path = "http://video.chaoxing.com:9999/service/getmyvisitlist.shtml?start="+start+"&userId="+userId;
		String path = "http://bookcxpad.lawy.com.cn/getmyvisitlist.shtml?start=1&end=10&userId="+userId;
		Log.e("wyz", " recentPlayList  path =  "+path);
		try {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(3000);
			conn.setRequestMethod("GET");
			JSONObject object = new JSONObject(readStream(conn.getInputStream(),"GBK"));
			JSONArray array = object.getJSONArray("datas");
			for (int i = 0; i < array.length(); i++) {
				JSONObject item = array.getJSONObject(i);
				playList = new SSVideoPlayListBean();
				playList.setStrVideoName(item.getString("title"));
				playList.setStrVideoId(String.valueOf(item.getInt("videoid")));
				playList.setStrRemoteCoverUrl(item.getString("img"));
				playList.setStrVideoRemoteUrl(item.getString("url"));
				playList.setStrDate(item.getString("datevisit"));
				list.add(playList);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public ArrayList<SeriesInfo> getClassifyInfo(int categoryId, int start,
			int pageSize, int callback) {
		ToolsLog.LOG_DBG(TAG+" getClassifyInfo  categoryId  "+categoryId+" start = "+start+" pageSize = "+pageSize);
		ArrayList<SeriesInfo> list = new ArrayList<SeriesInfo>();
		//String path = "http://video.chaoxing.com:9999/getClassifyInfo.shtml?categoryId="+categoryId+"&start="+start+"&pageSize="+pageSize;
		String path = "http://bookcxpad.lawy.com.cn/getClassifyInfo.shtml?categoryId="+categoryId+"&callback=0&start=0&pageSize=12";
		Log.e("wyz", "getClassifyInfo  path = "+path);
		URL url;	
			try {
				url = new URL(path);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5000);
				conn.setRequestMethod("GET");	
				JSONObject object = new JSONObject(readStream(conn.getInputStream(),"GBK"));
				JSONArray array = object.getJSONArray("datas");
				for (int i = 0; i < array.length(); i++) {
					JSONObject item = array.getJSONObject(i);
					mCategoryNameInfo = new CategoryNameInfo();
					mSeriesInfo = new SeriesInfo();
					//mCategoryNameInfo.setSeriesCount(String.valueOf(object.getInt("count")));
					mSeriesInfo.setTitle(item.getString("name"));
					mSeriesInfo.setCover(item.getString("img"));
					ArrayList<SeriesInfo> videoList = new ArrayList<SeriesInfo>();
					for (int j = 0; j < object.length(); j++) {
						JSONObject obj = item.getJSONObject("otherVideoInfo");
						//SeriesInfo.setSerid(StringFilter(String.valueOf(obj.getInt("seriesNo"))));
						mSeriesInfo.setScore(obj.getString("categoryNo"));
						mSeriesInfo.setScoreCount(String.valueOf(obj.getString("pid")));
						JSONObject time = obj.getJSONObject("category");
						List<OtherInfo> l = new ArrayList<OtherInfo>();
						//mOtherInfo = new OtherInfo();
						mSeriesInfo.setSubject(time.getString("categoryName"));
						//SeriesInfo.setDate(String.valueOf(time.getLong("time")));
						//mOtherInfo.setDate(time.getLong("time"));
						//l.add(mOtherInfo);
						//SeriesInfo.setListInfo(l);
						videoList.add(mSeriesInfo);		
					}
					mCategoryNameInfo.setInfo(videoList);
					list.add(mSeriesInfo);
				}
				//SeriesInfo.setCount(object.getInt("maxcount"));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		return list;
	}
	
	public ArrayList<CategoryNameInfo> getChildrenClassifyInfo(int categoryId, int level,
			int callback) {
		ToolsLog.LOG_DBG(TAG+" getChildrenClassifyInfo  categoryId  "+categoryId+" level = "+level);
		ArrayList<CategoryNameInfo> list = new ArrayList<CategoryNameInfo>(); 
		//String path = "http://video.chaoxing.com:9999/getChildrenClassifyInfo.shtml?categoryId="+categoryId+"&level="+level;
		String path = "http://bookcxpad.lawy.com.cn/getChildrenClassifyInfo.shtml?categoryId="+categoryId+"&level="+level+"&callback=0";
		Log.e("wyz", "getChildrenClassifyInfo  path = "+path);
		URL url;
		try {
			url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			JSONObject object = new JSONObject(readStream(conn.getInputStream(),"GBK"));
			JSONArray array = object.getJSONArray("datas");
			for (int i = 0; i < array.length(); i++) {
				JSONObject item = array.getJSONObject(i);
				mCategoryNameInfo = new CategoryNameInfo();
				mCategoryNameInfo.setTitle(item.getString("name"));
				mCategoryNameInfo.setCateId(String.valueOf(item.getInt("categoryId")));
				ArrayList<SeriesInfo> alist = new ArrayList<SeriesInfo>();
				for (int j = 0; j < array.length(); j++) {
					mSeriesInfo = new SeriesInfo();
					mSeriesInfo.setSubject(item.getString("name"));
					mSeriesInfo.setSerid(String.valueOf(item.getInt("categoryId")));
					alist.add(mSeriesInfo);		
				}
				mCategoryNameInfo.setInfo(alist);
				list.add(mCategoryNameInfo);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

}

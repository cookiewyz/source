package com.chaoxing.util;

/**
 * @author xin_yueguang.
 * @version 2012-12-7.
 */
import com.chaoxing.video.fayuan.R;

public class AppConfig {
	
	/**
	 * true: 仅有本地视频和在线视频(链接到网站)的定制程序, 需要同时修改程序名称、logo等信息. <br>
	 * false: 超星学术视频
	 */
	public static final boolean ORDER_ONLINE_VIDEO = false;     //定制需修改.
	public static final String packageName = "com.chaoxing.video.fayuan";//定制需修改.
	
	//文件夹
	public static final String userRootDir = "ssvideofayuan";//定制需修改.
	public static final String logo = "logo";
	public static final String commendVideo = "commendVideos";
	
	//raw资源文件
	public static final String userDbName = "ssvideofayuan.db";//定制需修改.
	public static final String logoIni = "logocfg.ini";
	public static final String logoZip = "logo.zip";
	public static final String localCategoryDbName = "video_info_category_local.db";
	public static final String commendVideoIni = "commendVideoList.ini";
	public static final String commendVideoZip = "commendVideos.zip";
	
	//raw资源 id
	public static final int userDbID = R.raw.video;
	public static final int logocfgID = R.raw.logocfg;
	public static final int logoZipID = R.raw.logo;
	public static final int localCategoryDbID = R.raw.video_info_category_local;
	public static final int commendVideoListID = R.raw.commend_video_list;
	public static final int commendVideoZipID = R.raw.commend_videos;
	
	

}

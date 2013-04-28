package com.chaoxing.util;

/**
 * @author xin_yueguang.
 * @version 2012-12-7.
 */
import com.chaoxing.video.fayuan.R;

public class AppConfig {
	
	/**
	 * true: ���б�����Ƶ��������Ƶ(���ӵ���վ)�Ķ��Ƴ���, ��Ҫͬʱ�޸ĳ������ơ�logo����Ϣ. <br>
	 * false: ����ѧ����Ƶ
	 */
	public static final boolean ORDER_ONLINE_VIDEO = false;     //�������޸�.
	public static final String packageName = "com.chaoxing.video.fayuan";//�������޸�.
	
	//�ļ���
	public static final String userRootDir = "ssvideofayuan";//�������޸�.
	public static final String logo = "logo";
	public static final String commendVideo = "commendVideos";
	
	//raw��Դ�ļ�
	public static final String userDbName = "ssvideofayuan.db";//�������޸�.
	public static final String logoIni = "logocfg.ini";
	public static final String logoZip = "logo.zip";
	public static final String localCategoryDbName = "video_info_category_local.db";
	public static final String commendVideoIni = "commendVideoList.ini";
	public static final String commendVideoZip = "commendVideos.zip";
	
	//raw��Դ id
	public static final int userDbID = R.raw.video;
	public static final int logocfgID = R.raw.logocfg;
	public static final int logoZipID = R.raw.logo;
	public static final int localCategoryDbID = R.raw.video_info_category_local;
	public static final int commendVideoListID = R.raw.commend_video_list;
	public static final int commendVideoZipID = R.raw.commend_videos;
	
	

}

package com.chaoxing.document;
/**
 * @author xin_yueguang.
 * @version 2012-12-28.
 */
public class DbInfo {
	public static final String TB_PLAY_LIST      = "play_list";
	public static final String TB_LOCAL_VIDEO    = "local_video";
	public static final String TB_VIDEO_CATEGORY = "video_category";	

	//video_info_category_local.db
	public static final String TB_INFO_SPEAKER  = "视频主讲人信息表";
	public static final String TB_INFO_VIDEO    = "视频信息表";
	public static final String TB_INFO_CATEGORY = "视频分类表";
	public static final String TB_INFO_SERIES   = "视频系列专题表";
	
	/*分类表字段*/
	public static final String CATE_ID     = "分类号";
	public static final String CATE_NAME   = "分类名称";
	public static final String CATE_LEVEL  = "级别";
	public static final String CATE_PARENT = "父分类号";
	public static final String CATE_SUB    = "isnode";
	public static final String CATE_INDEX_ID = "indexorderid";
	
	/*专题表字段*/
	public static final String SER_ID      = "系列ID";   //专题号
	public static final String SER_NAME    = "系列名称"; //专题名
	public static final String SER_CATE_ID = "分类号";
	public static final String SER_SUBJECT = "分类名称"; //专题分类名
	public static final String SER_SPEAKER = "主讲人";   //主讲人
	public static final String SER_SCORE   = "scorexj";  //评分
	public static final String SER_SCORE_COUNT = "scoreCount"; //评分次数
	public static final String SER_PLAY_TIME   = "clickcount"; //播放次数
	public static final String SER_ABSTRACT    = "主讲人简介"; //简介
	public static final String SER_COVER       = "小图片";     //图片地址
	public static final String SER_SPEAKER_IMG = "主讲人头像"; //主讲人图片
	public static final String SER_DATE        = "date";       //年代
	public static final String SER_VID         = "vID";        //自动编号
	public static final String SPEAKER_ID      = "主讲人ID";   //主讲人
	
	public static final String columns[]={DbInfo.SER_ID, DbInfo.SER_NAME, DbInfo.SER_COVER, DbInfo.SER_PLAY_TIME, 
											DbInfo.SER_SCORE, DbInfo.SER_SCORE_COUNT, DbInfo.SER_DATE, DbInfo.SER_ABSTRACT, 
											DbInfo.SER_SPEAKER, DbInfo.SER_SPEAKER_IMG, DbInfo.SER_SUBJECT, DbInfo.SER_CATE_ID
											};
	
}

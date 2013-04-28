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
	public static final String TB_INFO_SPEAKER  = "��Ƶ��������Ϣ��";
	public static final String TB_INFO_VIDEO    = "��Ƶ��Ϣ��";
	public static final String TB_INFO_CATEGORY = "��Ƶ�����";
	public static final String TB_INFO_SERIES   = "��Ƶϵ��ר���";
	
	/*������ֶ�*/
	public static final String CATE_ID     = "�����";
	public static final String CATE_NAME   = "��������";
	public static final String CATE_LEVEL  = "����";
	public static final String CATE_PARENT = "�������";
	public static final String CATE_SUB    = "isnode";
	public static final String CATE_INDEX_ID = "indexorderid";
	
	/*ר����ֶ�*/
	public static final String SER_ID      = "ϵ��ID";   //ר���
	public static final String SER_NAME    = "ϵ������"; //ר����
	public static final String SER_CATE_ID = "�����";
	public static final String SER_SUBJECT = "��������"; //ר�������
	public static final String SER_SPEAKER = "������";   //������
	public static final String SER_SCORE   = "scorexj";  //����
	public static final String SER_SCORE_COUNT = "scoreCount"; //���ִ���
	public static final String SER_PLAY_TIME   = "clickcount"; //���Ŵ���
	public static final String SER_ABSTRACT    = "�����˼��"; //���
	public static final String SER_COVER       = "СͼƬ";     //ͼƬ��ַ
	public static final String SER_SPEAKER_IMG = "������ͷ��"; //������ͼƬ
	public static final String SER_DATE        = "date";       //���
	public static final String SER_VID         = "vID";        //�Զ����
	public static final String SPEAKER_ID      = "������ID";   //������
	
	public static final String columns[]={DbInfo.SER_ID, DbInfo.SER_NAME, DbInfo.SER_COVER, DbInfo.SER_PLAY_TIME, 
											DbInfo.SER_SCORE, DbInfo.SER_SCORE_COUNT, DbInfo.SER_DATE, DbInfo.SER_ABSTRACT, 
											DbInfo.SER_SPEAKER, DbInfo.SER_SPEAKER_IMG, DbInfo.SER_SUBJECT, DbInfo.SER_CATE_ID
											};
	
}

package com.chaoxing.document;

import java.util.ArrayList;

import com.chaoxing.database.SSVideoPlayListBean;
/**
 * 
 * @author YongZheng
 * @version 2013-3-6
 */
public interface FaYuanInterface {
	/**
	 * ����������ݽӿ�
	 * 
	 * @param key      �����ؼ���
	 * @param count    ��������
	 * @param callback �������
	 * @return �������ݣ�List��һ��json���� Img��ͼƬ��ַ seriesName��ϵ������ serieId��ϵ��id
	 *         speaker������������ speakerId��������id schoolName��ѧУ���� schoolId��ѧУ��id
	 * 
	 */
	 
	ArrayList<SeriesInfo> getSearch(String key, int count, int callback);

	/**
	 * 
	 * @param count      ���ȷ�Դ��Ƶ6��
	 * @param callback   �����ʱ�����
	 * @return �������ݣ�List��һ��json���� Name������ û��Ϊ�� Img��ͼƬ��ַ û��Ϊ��
	 *         otherInfo����Ƶ������Ϣ������
	 */
	ArrayList<SeriesInfo> getHotSearch(int count, int callback);

	/**
	 * ��ò����б�
	 * 
	 * @param serieId        ����ID
	 * @param key            ���ܴ�
	 * @param callback       �����ʱ�����
	 * @return ����ֵ��status ��falseʧ�ܣ����ܴ����󣬻�����Ƶ������ datas��һ��json����:��Ƶ��Ϣ "name",
	 *         ��Ƶ���� videoId",��Ƶid videoInfo����Ƶ���ŵ�ַ����Ϣjson����
	 */
	ArrayList<SSVideoPlayListBean> getPlayList(int serieId,/* String key,*/ int callback);
	
	/**
	 * �ۿ���¼�б�
	 * @param start ��ʼҳ
	 * @param end ҳ���С�������ʹ���
	 * @param userId �û�ID
	 * @return
	 */
	ArrayList<SSVideoPlayListBean> recentPlayList(int start, int end, int userId);
	/**
	 * ����������Լ�����
	 * @param categoryId �����
	 * @param start ��ʼҳ
	 * @param pageSize ÿҳ����
	 * @param callback �����ʱ�����
	 * @return ��������,ͼƬ��ַ��video��������
	 */
	ArrayList<SeriesInfo> getClassifyInfo(int categoryId, int start, int pageSize, int callback);
	/**
	 * 
	 * @param categoryId ����ID�� ��0Ϊȫ��
	 * @param level ���༶��
	 * @param callback �����ʱ�����
	 * @return ֻ���ط������ƺͷַ���ID
	 */
	ArrayList<CategoryNameInfo> getChildrenClassifyInfo(int categoryId, int level, int callback);
	
}

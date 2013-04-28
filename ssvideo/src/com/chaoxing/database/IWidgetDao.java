package com.chaoxing.database;

import java.util.LinkedList;

public interface IWidgetDao {
	
	/**
	 * ͨ��videoId ��ѯָ��������
	 * @param videoId ��Ƶid
	 * @return ��Ƶ��Ϣ
	 */
	public SSVideoLocalVideoBean getDataInLocalVideoById(String videoId);
	
	/**
	 * ��ѯ�����б�������Ϣ
	 * @return ������Ϣ�б�
	 */
	public LinkedList<SSVideoCategoryBean> getAllCategory();
	public void close();
	
}

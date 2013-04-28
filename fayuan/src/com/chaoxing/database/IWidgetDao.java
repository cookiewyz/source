package com.chaoxing.database;

import java.util.LinkedList;

public interface IWidgetDao {
	
	/**
	 * 通过videoId 查询指定的数据
	 * @param videoId 视频id
	 * @return 视频信息
	 */
	public SSVideoLocalVideoBean getDataInLocalVideoById(String videoId);
	
	/**
	 * 查询分类列表所有信息
	 * @return 分类信息列表
	 */
	public LinkedList<SSVideoCategoryBean> getAllCategory();
	public void close();
	
}

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
	 * 获得搜索数据接口
	 * 
	 * @param key      搜索关键字
	 * @param count    搜索条数
	 * @param callback 跨域参数
	 * @return 返回数据：List是一个json数组 Img：图片地址 seriesName：系列名字 serieId：系列id
	 *         speaker：主讲人名字 speakerId：主讲人id schoolName：学校名字 schoolId：学校的id
	 * 
	 */
	 
	ArrayList<SeriesInfo> getSearch(String key, int count, int callback);

	/**
	 * 
	 * @param count      最热法源视频6个
	 * @param callback   跨域的时候调用
	 * @return 返回数据：List是一个json数组 Name：标题 没有为空 Img：图片地址 没有为空
	 *         otherInfo：视频其他信息的数组
	 */
	ArrayList<SeriesInfo> getHotSearch(int count, int callback);

	/**
	 * 获得播放列表
	 * 
	 * @param serieId        序列ID
	 * @param key            加密串
	 * @param callback       跨域的时候调用
	 * @return 返回值：status 是false失败，加密串错误，或则视频不存在 datas是一个json数组:视频信息 "name",
	 *         视频名字 videoId",视频id videoInfo：视频播放地址的信息json数据
	 */
	ArrayList<SSVideoPlayListBean> getPlayList(int serieId,/* String key,*/ int callback);
	
	/**
	 * 观看记录列表
	 * @param start 开始页
	 * @param end 页面大小具体类型待定
	 * @param userId 用户ID
	 * @return
	 */
	ArrayList<SSVideoPlayListBean> recentPlayList(int start, int end, int userId);
	/**
	 * 分类的数量以及数据
	 * @param categoryId 分类号
	 * @param start 开始页
	 * @param pageSize 每页条数
	 * @param callback 跨域的时候调用
	 * @return 返回名字,图片地址和video其他数据
	 */
	ArrayList<SeriesInfo> getClassifyInfo(int categoryId, int start, int pageSize, int callback);
	/**
	 * 
	 * @param categoryId 分类ID号 ，0为全部
	 * @param level 分类级数
	 * @param callback 跨域的时候调用
	 * @return 只返回分类名称和分分类ID
	 */
	ArrayList<CategoryNameInfo> getChildrenClassifyInfo(int categoryId, int level, int callback);
	
}

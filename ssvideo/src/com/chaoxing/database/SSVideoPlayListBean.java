package com.chaoxing.database;

import java.io.Serializable;

public class SSVideoPlayListBean implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4591754052345405413L;
	private String strVideoId;      // 视频Id
	private String strVideoName; // 专题名称
	private String strSpeaker;      // 主讲人
	private String strCateId;        // 分类id
	private String strCateName;  //分类名称
	private String strCoverName; // 封面名称
	private String strVideoFileName; // 视频名称
	private String strVideoLocalPath; //视频本地完整路径
	private String strVideoRemoteUrl; // 视频远程URL
	private Integer     nCurrentPlayTime = 0;  // 播放进度
	private Integer     nVideoType = 0;          // 视频类型
	private String strRemoteCoverUrl; // 远程封面地址
	private String strM3u8Url;           // m3u8在线播放地址
	private String strSeriesId;           // 系列Id
	private Integer     nCurrentPlay = 0; // 记录远程播放到哪一集
	private String strPlayTimes; // 播放次数
	private String strDate;         // 视频年份
	private String strAbstract;    // 摘要
	private String strScore;         // 得分
	private String strScoreCount;  // 得分计数

	public SSVideoPlayListBean(){
		
	}
	public SSVideoPlayListBean(SSVideoLocalVideoBean localVideoBean){
		if(localVideoBean.getStrVideoId() != null)
			this.strVideoId = localVideoBean.getStrVideoId();
		if(localVideoBean.getStrVideoName() != null)
			this.strVideoName = localVideoBean.getStrVideoName();
		if(localVideoBean.getStrSpeaker() != null)
			this.strSpeaker = localVideoBean.getStrSpeaker();
		if(localVideoBean.getStrCoverName() != null)
			this.strCoverName = localVideoBean.getStrCoverName();
		if(localVideoBean.getStrCateId() != null)
			this.strCateId = localVideoBean.getStrCateId();
		if(localVideoBean.getStrCateName() != null)
			this.strCateName = localVideoBean.getStrCateName();
		if(localVideoBean.getStrVideoFileName() != null)
		this.strVideoFileName = localVideoBean.getStrVideoFileName();
		this.strVideoLocalPath = localVideoBean.getStrVideoLocalPath();
		this.strVideoRemoteUrl = localVideoBean.getStrVideoDownloadRemoteFileUrl();
		this.strRemoteCoverUrl = localVideoBean.getStrVideoDownloadRemoteCoverUrl();
		this.strAbstract = localVideoBean.getStrAbstract();
	}
	
	public String getStrVideoId() {
		return strVideoId;
	}
	
	public void setStrVideoId(String strVideoId) {
		this.strVideoId = strVideoId;
	}
	public String getStrVideoName() {
		return strVideoName;
	}
	public void setStrVideoName(String strVideoName) {
		this.strVideoName = strVideoName;
	}
	public String getStrSpeaker() {
		return strSpeaker;
	}
	public void setStrSpeaker(String strSpeaker) {
		this.strSpeaker = strSpeaker;
	}
	public String getStrCateId() {
		return this.strCateId;
	}
	public void setStrCateId(String strCateId) {
		this.strCateId = strCateId;
	}
	public String getStrCateName() {
		return strCateName;
	}
	public void setStrCateName(String strCateName) {
		this.strCateName = strCateName;
	}
	public String getStrCoverName() {
		return strCoverName;
	}
	public void setStrCoverName(String strCoverName) {
		this.strCoverName = strCoverName;
	}
	public String getStrVideoFileName() {
		return strVideoFileName;
	}
	public void setStrVideoFileName(String strVideoFileName) {
		this.strVideoFileName = strVideoFileName;
	}
	public String getStrVideoLocalPath() {
		return strVideoLocalPath;
	}
	public void setStrVideoLocalPath(String strVideoLocalPath) {
		this.strVideoLocalPath = strVideoLocalPath;
	}
	public String getStrVideoRemoteUrl() {
		return strVideoRemoteUrl;
	}
	public void setStrVideoRemoteUrl(String strVideoRemoteUrl) {
		this.strVideoRemoteUrl = strVideoRemoteUrl;
	}
	public Integer getnCurrentPlayTime() {
		return nCurrentPlayTime;
	}
	public void setnCurrentPlayTime(int nCurrentPlayTime) {
		this.nCurrentPlayTime = nCurrentPlayTime;
	}
	public Integer getnVideoType() {
		return nVideoType;
	}
	public void setnVideoType(int nVideoType) {
		this.nVideoType = nVideoType;
	}
	public String getStrRemoteCoverUrl() {
		return strRemoteCoverUrl;
	}
	public void setStrRemoteCoverUrl(String strRemoteCoverUrl) {
		this.strRemoteCoverUrl = strRemoteCoverUrl;
	}
	public String getStrM3u8Url() {
		return strM3u8Url;
	}
	public void setStrM3u8Url(String strM3u8Url) {
		this.strM3u8Url = strM3u8Url;
	}
	public String getStrSeriesId() {
		return strSeriesId;
	}
	public void setStrSeriesId(String strSeriesId) {
		this.strSeriesId = strSeriesId;
	}
	public Integer getnCurrentPlay() {
		return nCurrentPlay;
	}
	public void setnCurrentPlay(int nCurrentPlay) {
		this.nCurrentPlay = nCurrentPlay;
	}
	public String getStrPlayTimes() {
		return strPlayTimes;
	}
	public void setStrPlayTimes(String strPlayTimes) {
		this.strPlayTimes = strPlayTimes;
	}
	public String getStrDate() {
		return strDate;
	}
	public void setStrDate(String strDate) {
		this.strDate = strDate;
	}
	public String getStrAbstract() {
		return strAbstract;
	}
	public void setStrAbstract(String strAbstract) {
		this.strAbstract = strAbstract;
	}
	public String getStrScore() {
		return strScore;
	}
	public void setStrScore(String strScore) {
		this.strScore = strScore;
	}
	public String getStrScoreCount() {
		return strScoreCount;
	}
	public void setStrScoreCount(String strScoreCount) {
		this.strScoreCount = strScoreCount;
	}
	
	
}

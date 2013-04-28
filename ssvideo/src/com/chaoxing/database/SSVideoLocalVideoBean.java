package com.chaoxing.database;

import java.io.Serializable;

public class SSVideoLocalVideoBean implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String strVideoId;      // 视频Id
	private String strVideoName; //专题名称
	private String strVideoFileName; //  视频名称
	private String strVideoLocalPath; //视频本地完整路径
	private String strSpeaker;      // 主讲人
	private String strCateId;        // 分类号
	private String strCateName;  // 分类名称
	private String strCoverName; // 封面名称
	private Integer nVideoDownloadStatus; // 视频下载状态
	private Integer nVideoDownloadProgress; // 视频下载进度
	private Integer nVideoFileLength;      // 视频文件的长度
	private String strVideoDownloadRemoteFileUrl; // 远程视频下载地址
	private String strVideoDownloadRemoteCoverUrl; // 远程封面下载地址
	private String strAbstract;    // 摘要
	
	public SSVideoLocalVideoBean() {
	}
	
	public SSVideoLocalVideoBean(SSVideoPlayListBean playListBean){
		if(playListBean.getStrVideoId() != null)
			this.strVideoId = playListBean.getStrVideoId();
		if(playListBean.getStrVideoName() != null)
			this.strVideoName = playListBean.getStrVideoName();
		if(playListBean.getStrSpeaker() != null)
			this.strSpeaker = playListBean.getStrSpeaker();
		if(playListBean.getStrCateId() != null)
			this.strCateId = playListBean.getStrCateId();
		if(playListBean.getStrCateName() != null)
			this.strCateName = playListBean.getStrCateName();
		if(playListBean.getStrCoverName() != null)
			this.strCoverName = playListBean.getStrCoverName();
		if(playListBean.getStrVideoFileName() != null)
			this.strVideoFileName = playListBean.getStrVideoFileName();
		if(playListBean.getStrVideoLocalPath() != null)
			this.strVideoLocalPath = playListBean.getStrVideoLocalPath();
		if(playListBean.getStrRemoteCoverUrl() != null)
			this.strVideoDownloadRemoteCoverUrl = playListBean.getStrRemoteCoverUrl();
		if(playListBean.getStrVideoRemoteUrl()!= null)
			this.strVideoDownloadRemoteFileUrl= playListBean.getStrVideoRemoteUrl();
		if(playListBean.getStrAbstract() != null)
			this.strAbstract = playListBean.getStrAbstract();
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
	public String getStrVideoLocalPath() {
		return strVideoLocalPath;
	}
	public void setStrVideoLocalPath(String strVideoLocalPath) {
		this.strVideoLocalPath = strVideoLocalPath;
	}
	public String getStrSpeaker() {
		return strSpeaker;
	}
	public void setStrSpeaker(String strSpeaker) {
		this.strSpeaker = strSpeaker;
	}
	public String getStrCateId() {
		return strCateId;
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
	public Integer getnVideoDownloadStatus() {
		return nVideoDownloadStatus;
	}
	public void setnVideoDownloadStatus(int nVideoDownloadStatus) {
		this.nVideoDownloadStatus = nVideoDownloadStatus;
	}
	public Integer getnVideoDownloadProgress() {
		return nVideoDownloadProgress;
	}
	public void setnVideoDownloadProgress(int nVideoDownloadProgress) {
		this.nVideoDownloadProgress = nVideoDownloadProgress;
	}
	public Integer getnVideoFileLength() {
		return nVideoFileLength;
	}

	public void setnVideoFileLength(int nVideoFileLength) {
		this.nVideoFileLength = nVideoFileLength;
	}
	public String getStrVideoDownloadRemoteFileUrl() {
		return strVideoDownloadRemoteFileUrl;
	}
	public void setStrVideoDownloadRemoteFileUrl(
			String strVideoDownloadRemoteFileUrl) {
		this.strVideoDownloadRemoteFileUrl = strVideoDownloadRemoteFileUrl;
	}
	public String getStrVideoDownloadRemoteCoverUrl() {
		return strVideoDownloadRemoteCoverUrl;
	}
	public void setStrVideoDownloadRemoteCoverUrl(
			String strVideoDownloadRemoteCoverUrl) {
		this.strVideoDownloadRemoteCoverUrl = strVideoDownloadRemoteCoverUrl;
	}
	public String getStrAbstract() {
		return strAbstract;
	}
	public void setStrAbstract(String strAbstract) {
		this.strAbstract = strAbstract;
	}
	
}

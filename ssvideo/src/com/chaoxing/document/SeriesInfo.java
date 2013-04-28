package com.chaoxing.document;

import java.io.Serializable;

public class SeriesInfo implements Serializable {
	
    /**
	 * @author xin_yueguang
	 */
	private static final long serialVersionUID = 4085390396471647494L;
	public String serid;      //专题号
	public String title;      //专题名
	public String subject;    //专题分类名
	public String keyspeaker; //主讲人
	public String score;      //评分
	public String scorecount; //评分次数
	public String playtimes;  //播放次数
	public String abstracts;  //简介
	public String cover;      //图片地址
	public String speakerphoto;//主讲人图片
	public String date;       //年代

	public SeriesInfo(){
		super();
		this.serid = "";
		this.title = "";
		this.subject = "";
		this.keyspeaker = "";
		this.score = "";
		this.scorecount = "";
		this.playtimes = "";
		this.abstracts = "";
		this.cover = "";
		this.speakerphoto = "";
		this.date = "";
	}
	
	public void setSerid(String serid){
		this.serid = serid;
	}
	public String getSerid(){
		return this.serid;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getKeySpeaker() {
		return keyspeaker;
	}
	public void setKeySpeaker(String speaker) {
		this.keyspeaker = speaker;
	}
	
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	
	public String getScoreCount() {
		return scorecount;
	}
	public void setScoreCount(String scorecount) {
		this.scorecount = scorecount;
	}
	
	public String getPlaytimes() {
		return playtimes;
	}
	public void setPlaytimes(String playtimes) {
		this.playtimes = playtimes;
	}
	
	public String getAbstracts() {
		return abstracts;
	}
	public void setAbstracts(String abstracts) {
		this.abstracts = abstracts;
	}
	
	public String getCover() {
		return cover;
	}
	public void setCover(String cover) {
		this.cover = cover;
	}
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getSpeakerPhoto() {
		return cover;
	}
	public void setSpeakerPhoto(String photo) {
		this.speakerphoto = photo;
	}
}

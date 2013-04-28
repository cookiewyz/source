package com.chaoxing.document;

import java.io.Serializable;

public class SeriesInfo implements Serializable {
	
    /**
	 * @author xin_yueguang
	 */
	private static final long serialVersionUID = 4085390396471647494L;
	public String serid;      //ר���
	public String title;      //ר����
	public String subject;    //ר�������
	public String keyspeaker; //������
	public String score;      //����
	public String scorecount; //���ִ���
	public String playtimes;  //���Ŵ���
	public String abstracts;  //���
	public String cover;      //ͼƬ��ַ
	public String speakerphoto;//������ͼƬ
	public String date;       //���

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

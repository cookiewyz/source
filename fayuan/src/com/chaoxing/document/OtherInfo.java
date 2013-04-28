package com.chaoxing.document;

import java.io.Serializable;

import android.text.format.DateFormat;

/**
 * 
 * @author YongZheng
 * @version 2013-3-6
 */
public class OtherInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int id ;
	public String  name;
	public String introduction;
	public String imgPath;

	private long date;

	
	@Override
	public String toString() {
		String datetime = DateFormat.format("yyyy:MM:dd kk:mm:ss", date).toString(); 
		String str = datetime;
		return str;
	}
	
	
	public void setDate(long date) {
		this.date = date;
	}
	public long getDate() {
		return date;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIntroduction() {
		return introduction;
	}
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
	public String getImgPath() {
		return imgPath;
	}
	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}
	
	
	

}

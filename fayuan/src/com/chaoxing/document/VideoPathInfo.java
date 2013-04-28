package com.chaoxing.document;

import java.io.Serializable;

public class VideoPathInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String serverpath;
	public int line;
	public String wmvpath;
	public String flvpath;
	public String soundpath;
	public String mp4path;
	public String getServerpath() {
		return serverpath;
	}
	public void setServerpath(String serverpath) {
		this.serverpath = serverpath;
	}
	public int getLine() {
		return line;
	}
	public void setLine(int line) {
		this.line = line;
	}
	public String getWmvpath() {
		return wmvpath;
	}
	public void setWmvpath(String wmvpath) {
		this.wmvpath = wmvpath;
	}
	public String getFlvpath() {
		return flvpath;
	}
	public void setFlvpath(String flvpath) {
		this.flvpath = flvpath;
	}
	public String getSoundpath() {
		return soundpath;
	}
	public void setSoundpath(String soundpath) {
		this.soundpath = soundpath;
	}
	public String getMp4path() {
		return mp4path;
	}
	public void setMp4path(String mp4path) {
		this.mp4path = mp4path;
	}
	
	@Override
	public String toString() {
		String str ="http://"+this.serverpath+this.flvpath;
		return str;
	}
}

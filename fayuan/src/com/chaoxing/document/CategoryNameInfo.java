package com.chaoxing.document;

import java.util.ArrayList;

public class CategoryNameInfo {

    public String id;          //分类号
	public String name;        //分类名称
	public String level;       //分类级别(1级分类、2级分类、3级分类)
	public String parentid;    //父分类id
	public String seriescount; //该分类中专题数量
	public String subcategory; //该分类是否包含子分类
	private ArrayList<SeriesInfo> info;
	
	
	public CategoryNameInfo() {
		super();
		this.id = "";
		this.name = "";
		this.level = "";
		this.parentid = "";
		this.seriescount = "";
		this.subcategory = "";
	}
	
	public ArrayList<SeriesInfo> getInfo() {
		return info;
	}

	public void setInfo(ArrayList<SeriesInfo> info) {
		this.info = info;
	}

	public void setCateId(String serid){
		this.id = serid;
	}
	public String getCateId(){
		return this.id;
	}
	
	public String getTitle() {
		return this.name;
	}
	public void setTitle(String ti) {
		this.name = ti;
	}
	
	public String getLevel() {
		return this.level;
	}
	public void setLevel(String lv) {
		this.level = lv;
	}
	
	public String getParentId() {
		return this.parentid;
	}
	public void setParentId(String pi) {
		this.parentid = pi;
	}
	
	public String getSeriesCount() {
		return this.seriescount;
	}
	public void setSeriesCount(String sc) {
		this.seriescount = sc;
	}
	
	public String getHasChild() {
		return this.subcategory;
	}
	public void setHasChild(String sub) {
		this.subcategory = sub;
	}
	
	
}

package com.chaoxing.database;

import java.io.Serializable;

public class SSVideoCategoryBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String strCateId; //����Id
	private String strCateName; // ������
	
	
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
	
}

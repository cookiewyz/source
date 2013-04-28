package com.chaoxing.document;

/**
 * @author xin_yueguang.
 * @version 2012-12-20.
 */
public class UserInformation {
	
	private String sName;   //用户名
	private String sPassword; //密码
	private int status;  //状态(在线、离线)
	private int xingbi;  //星币数量
	private int ismb;    //会员类型
	private String usertype;//用户类型
	private String regDate; //注册时间
	private int uId ;//用户ID
	private int iCode;

	public UserInformation() {
		initUserInfo();
	}
	
	public void initUserInfo() {
		sName = "";
		sPassword = "";
		status = 0;
		xingbi = 0;
		ismb = 0;
		usertype = "普通用户";
		iCode = -1;
	}
	
	public String getUserName() {
		return this.sName;
	}
	public void setUserName(String sname) {
		this.sName = sname;
	}
	
	public String getPassword() {
		return sPassword;
	}
	public void setPassword(String pwd) {
		sPassword = pwd;
	}
	
	public int getCode() {
		return iCode;
	}
	public void setCode(int code) {
		iCode = code;
	}
	
	public int getUserStatus() {
		return status;
	}
	public void setUserStatus(int st) {
		this.status = st;
	}
	
	public int getCoinsCount() {
		return xingbi;
	}
	public void setCoinsCount(Integer coins) {
		this.xingbi = coins;
	}
	
	public int getMenberType() {
		return ismb;
	}
	public void setMenberType(int mb) {
		this.ismb = mb;
	}
	
	public String getUsertype() {
		return usertype;
	}
	public void setUsertype(String usertype) {
		this.usertype = usertype;
	}

	public String getRegDate() {
		return regDate;
	}

	public void setRegDate(String regDate) {
		this.regDate = regDate;
	}

	public int getuId() {
		return uId;
	}

	public void setuId(int uId) {
		this.uId = uId;
	}
	
}

package com.chaoxing.document;

/**
 * @author xin_yueguang.
 * @version 2012-12-20.
 */
public class UserInformation {
	
	private String sName;   //�û���
	private String sPassword; //����
	private int status;  //״̬(���ߡ�����)
	private int xingbi;  //�Ǳ�����
	private int ismb;    //��Ա����
	private String usertype;//�û�����
	private String regDate; //ע��ʱ��
	private int uId ;//�û�ID
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
		usertype = "��ͨ�û�";
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

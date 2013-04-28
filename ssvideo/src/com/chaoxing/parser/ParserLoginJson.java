package com.chaoxing.parser;
import org.json.JSONObject;
import com.chaoxing.httpservice.HttpRequest;
import com.chaoxing.document.UserInformation;

/**
 * @author xin_yueguang.
 * @version 2012-12-20.
 */

public class ParserLoginJson {
//	private static final String TAG = "ParserLoginJson";
	
	private int iResponse;
	private String resJSon;
	private UserInformation userinfo = null;
	
	public ParserLoginJson(){
		super();
		iResponse = -1;
	}
	
	public void setJson(String json)
	{
		resJSon = json;
	}
	
	public String getJson()
	{
		return resJSon;
	}
	
	public int getResponse()
	{
		return iResponse;
	}
	
	public UserInformation getUserInfo()
	{
		return userinfo;
	}
	
	public int getLoginState()
	{
		return userinfo.getUserStatus();
	}
	
	private void parserJson(){

        JSONObject obj;
        try {        
            String json = new String(resJSon.getBytes(), "utf-8");   
            obj = new JSONObject(json); 
            userinfo = new UserInformation();
            boolean state = obj.getBoolean("result"); 
			if(state) {
				userinfo.setUserStatus(1);
			}
			else {
				userinfo.setUserStatus(0);
			}
			
			int code = obj.getInt("code");
			userinfo.setCode(code);
//            JSONArray array0 = obj.getJSONArray("seriesitem_hot");    
//            getParserJSon(array0, 0);
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
        	iResponse = 3;
            e.printStackTrace();
        }
        iResponse = 0;
	}
	/*
	private void getParserJSon(JSONArray jsObj, int idx) throws JSONException{
		int ilen = jsObj.length();
		for(int i=0; i<ilen; i++)
		{
			userinfo = new UserInformation();
			JSONObject obj = jsObj.getJSONObject(i);
			try{
				boolean state = obj.getBoolean("result"); 
				if(state) {
					userinfo.setUserStatus(1);
				}
				else {
					userinfo.setUserStatus(0);
				}
				
				int code = obj.getInt("code");
				userinfo.setCoinsCount(code);
			}
			catch (Exception e) {
	            e.printStackTrace();
	            iResponse = 3;
	            break;
	        }
			
		}//end for i;
	}*/
	//String surl = "http://www.chaoxing.cc/iphone/sgetdata.aspx?action=IndexFLJSON";
	public void getRequestFile(String surl)
	{
		HttpRequest req = new HttpRequest();
		req.setRequestUrl(surl);
		req.getRequestFile();
		int ires = req.getResponse();
		if(ires != 200)
		{
			iResponse = 1;
			return;
		}
		resJSon = req.getStringData();
		parserJson();
	}// end getRequestFile;
	
}

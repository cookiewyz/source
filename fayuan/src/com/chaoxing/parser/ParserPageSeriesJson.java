/*
 * 功能描述：解析精彩推荐返回的Json数据
 * Author：xinyueguang
 */

package com.chaoxing.parser;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chaoxing.httpservice.HttpRequest;
import com.chaoxing.document.ParseFaYuanData;
import com.chaoxing.document.SeriesInfo;


public class ParserPageSeriesJson {
	private static final String TAG = "ParserPageSeriesJson";
	ParseFaYuanData mFayuan =  ParseFaYuanData.getInstance();
	private int iResponse;
	private String resJSon;
	private ArrayList<SeriesInfo> serListConstitution = new ArrayList<SeriesInfo>();      //宪法=60202 
	private ArrayList<SeriesInfo> serListCriminal = new ArrayList<SeriesInfo>();          //刑法=60203
	private ArrayList<SeriesInfo> serListMinShang = new ArrayList<SeriesInfo>();          //民商法=60204
	private ArrayList<SeriesInfo> serListEconomicLaw = new ArrayList<SeriesInfo>();       //经济法=60205
	private ArrayList<SeriesInfo> serListProceduralLaw = new ArrayList<SeriesInfo>();     //诉讼法=60206
	private ArrayList<SeriesInfo> serListAdministrativeLaw = new ArrayList<SeriesInfo>(); //行政法=60202
	
	
	public ParserPageSeriesJson(){
		super();
		iResponse = -1;
	}

	public ArrayList<SeriesInfo> getSeries(int idx) {
		switch (idx) {
		case 0:
			return serListConstitution = mFayuan.getClassifyInfo(6, 1, 8, 0);		
		/*case 1:
			return serListCriminal = mFayuan.getClassifyInfo(60203, 1, 8, 0);
		case 2:
			return serListMinShang = mFayuan.getClassifyInfo(60204, 1, 8, 0);
		case 3:
			return serListEconomicLaw = mFayuan.getClassifyInfo(60205, 1, 8, 0);
		case 4:
			return serListProceduralLaw = mFayuan.getClassifyInfo(60206, 1, 8, 0);
		case 5:
			return serListAdministrativeLaw = mFayuan.getClassifyInfo(60202, 1, 8, 0);*/
		default:
			return serListConstitution = mFayuan.getClassifyInfo(6, 1, 8, 0);	
		}
	}
	
	public void setSeriesJson(String json)
	{
		this.resJSon = json;
	}
	
	public int getResponse()
	{
		return iResponse;
	}
	
	/*
	private void downloadJson(){
		Thread thread = new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				//super.run();
			}
		};
		thread.start();
		thread = null;
	}*/
	
	private void getJson(){

        JSONObject obj;
        try {        
            String json = new String(resJSon.getBytes(), "utf-8");   
            obj = new JSONObject(json); 
        
            JSONArray array0 = obj.getJSONArray("seriesitem_hot");    
            getParserJSon(array0, 0);
            
            JSONArray array1 = obj.getJSONArray("seriesitem_master");    
            getParserJSon(array1, 1);
            
            JSONArray array2 = obj.getJSONArray("seriesitem_philosophy");    
            getParserJSon(array2, 2);
            
            JSONArray array3 = obj.getJSONArray("seriesitem_literature");    
            getParserJSon(array3, 3);
            
            JSONArray array4 = obj.getJSONArray("seriesitem_economy");    
            getParserJSon(array4, 4);
            
            JSONArray array5 = obj.getJSONArray("seriesitem_history");    
            getParserJSon(array5, 5);
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	
	public void getParserJSon(JSONArray jsObj, int idx) throws JSONException{
		String sStr = "";
		int ilen = jsObj.length();
		for(int i=0; i<ilen; i++)
		{
			SeriesInfo ser = new SeriesInfo();
			JSONObject obj = jsObj.getJSONObject(i);
			try{
				sStr = obj.getString("id"); 
				ser.setSerid(sStr);
				sStr = obj.getString("title"); 
				ser.setTitle(sStr);
				sStr = obj.getString("owner"); 
				ser.setKeySpeaker(sStr);
				sStr = obj.getString("catename"); 
				ser.setSubject(sStr);
				sStr = obj.getString("coverurl"); 
				ser.setCover(sStr);
				sStr = obj.getString("score"); 
				ser.setScore(sStr);
				sStr = obj.getString("scorecount"); 
				ser.setScoreCount(sStr);
				sStr = obj.getString("date"); 
				ser.setDate(sStr);
				sStr = obj.getString("ownerintro"); 
				ser.setAbstracts(sStr);
				sStr = obj.getString("playtimes"); 
				ser.setPlaytimes(sStr);
				sStr = obj.getString("ownerphoto"); 
				ser.setSpeakerPhoto(sStr);
			}
			catch (Exception e) {
	            e.printStackTrace();
	            iResponse = 3;
	            break;
	        }
			
			switch(idx){
				case 0:
					serListConstitution.add(ser);
					break;
				case 1:
					serListCriminal.add(ser);
					break;
				case 2:
					serListMinShang.add(ser);
					break;
				case 3:
					serListEconomicLaw.add(ser);
					break;
				case 4:
					serListProceduralLaw.add(ser);
					break;
				case 5:
					serListAdministrativeLaw.add(ser);
					break;
			}
			
		}//end for i;
	}
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
		getJson();
		iResponse = 0;
	}// end getRequestFile;
	
}

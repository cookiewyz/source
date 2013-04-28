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
import com.chaoxing.document.SeriesInfo;


public class ParserPageSeriesJson {
	private static final String TAG = "ParserPageSeriesJson";
	
	private int iResponse;
	private String resJSon;
	private ArrayList<SeriesInfo> serListHot = new ArrayList<SeriesInfo>();        //热门=0
	private ArrayList<SeriesInfo> serListMaster = new ArrayList<SeriesInfo>();     //大师风采=1
	private ArrayList<SeriesInfo> serListPhilosophy = new ArrayList<SeriesInfo>(); //哲学=2
	private ArrayList<SeriesInfo> serListLiterature = new ArrayList<SeriesInfo>(); //文学=3
	private ArrayList<SeriesInfo> serListEconomy = new ArrayList<SeriesInfo>();    //经济=4
	private ArrayList<SeriesInfo> serListHistory = new ArrayList<SeriesInfo>();    //历史=5
	
	
	public ParserPageSeriesJson(){
		super();
		iResponse = -1;
	}

	public ArrayList<SeriesInfo> getSeries(int idx)
	{
		if(idx == 0)
			return serListHot;
		else if(idx == 1)
			return serListMaster;
		else if(idx == 2)
			return serListPhilosophy;
		else if(idx == 3)
			return serListLiterature;
		else if(idx == 4)
			return serListEconomy;
		//else if(idx == 5)
		return serListHistory;
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
					serListHot.add(ser);
					break;
				case 1:
					serListMaster.add(ser);
					break;
				case 2:
					serListPhilosophy.add(ser);
					break;
				case 3:
					serListLiterature.add(ser);
					break;
				case 4:
					serListEconomy.add(ser);
					break;
				case 5:
					serListHistory.add(ser);
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

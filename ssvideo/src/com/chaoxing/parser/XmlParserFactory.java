package com.chaoxing.parser;

/**
 * @解析服务器返回的精彩推荐的XML数据
 * @Author xin_yueguang
 * @version 2011-8-24
 */

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.util.ByteArrayBuffer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;
import com.chaoxing.document.CategoryNameInfo;
import com.chaoxing.httpservice.HttpRequest;
import com.chaoxing.document.SeriesInfo;
import com.chaoxing.document.UserInformation;


public class XmlParserFactory {
	private static final String TAG = "ParserTopRecommend";
	
	private int iResponse;
	private int iResultCount;
	private String resXML;
	private SeriesInfo res;
	private ArrayList<SeriesInfo> serList = null;
	private ByteArrayBuffer resByte;
	private UserInformation userinfo = null;
	private ArrayList<CategoryNameInfo> cateNameList = null;
	
	public XmlParserFactory(){
		super();
		iResponse = -1;
		iResultCount = 0;
	}

	public void setXML(String xml)
	{
		this.resXML = xml;
	}
	
	public int getResponse()
	{
		return iResponse;
	}
	
	public ArrayList<SeriesInfo> getSeriesInfoList()
	{
		return serList;
	}
	
	public ArrayList<CategoryNameInfo> getCategoryNameInfoListtPull()
	{
		if(resXML == ""){ 
			iResponse = 2;
			return null;
		}
		
		try {
			parseCategoryNameXML();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			iResponse = 3;
			return null;
		}
		
		iResponse = 0;
		return cateNameList;
	}
	
	public UserInformation getUserInfo()
	{
		if(resXML == ""){ 
			iResponse = 2;
			return null;
		}
		try {
			parseUserXML();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			iResponse = 3;
			return null;
		}
		
		iResponse = 0;
		return userinfo;
	}
	
	public ArrayList<SeriesInfo> getTopSeriesInfoListPull()
	{
		if(resXML == ""){ 
			iResponse = 2;
			return null;
		}
		try {
			GetTopRecommmendPull();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			iResponse = 3;
			return null;
		}
		iResponse = 0;
		return serList;
	}
	
/*	public ArrayList<SeriesInfo> getSearchVideoInfoListPull()
	{
		if(resXML == ""){ 
			iResponse = 2;
			return null;
		}
		try {
			GetSearchResultPull();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			iResponse = 3;
			return null;
		}
		iResponse = 0;
		return serList;
	}*/
	
	public int getResultCountPull()
	{
		if(resXML == ""){ 
			iResponse = 2;
			return 0;
		}
		try {
			GetResultCountPull();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			iResponse = 3;
			return 0;
		}
		iResponse = 0;
		return iResultCount;
	}
	
	public ArrayList<SeriesInfo> getTopSeriesInfoListSAX()
	{
		if(resXML == ""){ 
			iResponse = 2;
			return null;
		}

		try {
			parserTopRecommendXML();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			iResponse = 3;
		}
		
		iResponse = 0;
		return serList;
	}
	
	//http://www.chaoxing.cc/iphone/sgetdata.aspx?action=IndexTop6";
	public void getRequestFile(String surl) 
	{
		HttpRequest req = new HttpRequest();
		req.setRequestUrl(surl);
		req.getRequestFile();
		resXML = req.getStringData();
		int ires = req.getResponse();
		if(ires != 200)
		{
			iResponse = 1;
			return;
		}
		
		iResponse = 0;
	}// end getRequestFile;
	
	public void httpGetFilebyte(String surl)
	{
		try {
	            URL url = new URL(surl);
	            URLConnection con = url.openConnection();
	            
	            iResponse = ((HttpURLConnection)con).getResponseCode();
	            if(iResponse != 200){
	            	return;
	            }
	            InputStream is = con.getInputStream();
	            BufferedInputStream bis = new BufferedInputStream(is);
	            //ByteArrayBuffer bab = new ByteArrayBuffer(32);
	            resByte = new ByteArrayBuffer(32);
	            int current = 0;
	            while ((current = bis.read()) != -1 ){
	            	resByte.append((byte)current);
	            }
				
	            //resStr = EncodingUtils.getString(bab.toByteArray(), HTTP.UTF_8);
	            bis.close();
	            is.close();
		} catch (Exception e) {
	        	e.printStackTrace();
	    }
	    
	    iResponse = 0;
	}
	private void parserTopRecommendXML() throws ParserConfigurationException, SAXException, IOException{
		try{
			SAXParserFactory spf = SAXParserFactory.newInstance();
			//spf.setValidating(false);
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			TopRecommendXmlHandler resHandler = new TopRecommendXmlHandler();
			xr.setContentHandler(resHandler);

			InputStream in = new ByteArrayInputStream(resByte.toByteArray());
			InputStreamReader ir = new InputStreamReader(in, "UTF-8");//, "UTF-8"
			InputSource is = new InputSource(ir);
			xr.parse(is);
			serList = resHandler.getTopSeriesInfo();
			if(ir!=null)
				ir.close();
		}
		catch (ParserConfigurationException e) 
	    {
	        e.printStackTrace();
	        Log.d(TAG, e.toString());
	    } 
		catch (SAXException e) 
	    {
	        e.printStackTrace();
	        Log.d(TAG, e.toString());
	    } 
	    catch (IOException e) 
	    {
	        e.printStackTrace();
	        Log.d(TAG, e.toString());
	    }
		
	}
	
	public ArrayList<SeriesInfo> readXml2() throws Exception {
		
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser saxParser = spf.newSAXParser(); //创建解析器		
		InputStream in = new ByteArrayInputStream(resXML.getBytes());
		TopRecommendXmlHandler handler = new TopRecommendXmlHandler();
		saxParser.parse(in, handler);
		in.close();
		return handler.getTopSeriesInfo();		
	}
	
	public ArrayList<SeriesInfo> readXml() throws Exception {
		
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser saxParser = spf.newSAXParser(); //创建解析器		
		InputStream in = new ByteArrayInputStream(resXML.getBytes());
		TopRecommendXmlHandler handler = new TopRecommendXmlHandler();
		saxParser.parse(in, handler);
		in.close();
		return handler.getTopSeriesInfo();		
	}
	
	private void GetTopRecommmendPull() throws XmlPullParserException {
		if(resXML == null) {		
			iResponse = 1;
			return;
		}
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	    factory.setNamespaceAware(true);
	    XmlPullParser xmlParser = factory.newPullParser();	       
	    InputStream iStream = new ByteArrayInputStream(resXML.getBytes());//FileInputStream(resXML); 
	    xmlParser.setInput(iStream, "UTF-8");
	    try{
	    	int eventType = xmlParser.getEventType();
	    	String stag = null;
	    	while (eventType != XmlPullParser.END_DOCUMENT) { 
	    		   
	    		if(eventType == XmlPullParser.START_DOCUMENT){
	    			serList = new ArrayList<SeriesInfo>();
	    		} 
	    		else if (eventType == XmlPullParser.START_TAG) {
	    			stag = xmlParser.getName();
	    			if(stag.equals("seriesitem"))
	    			{
	    				res = new SeriesInfo();
	    	   		}
	    	   	} 
	    		else if (eventType == XmlPullParser.END_TAG) { 
	    	   		String etag = xmlParser.getName();
	    	   		if(etag.equals("seriesitem")){
	    	   			serList.add(res);
	                   	res = null;
	    	   		}
	    	   	} 
	    		else if (eventType == XmlPullParser.TEXT) { 
	    	   			
	    	   		String sText = xmlParser.getText();
	    	   		if(stag.equals("id")){
	    				res.setSerid(sText);
	    			}else if(stag.equals("title")){
	    				res.setTitle(sText);
	    			}else if(stag.equals("owner")){
	    				res.setKeySpeaker(sText);
	    			}else if(stag.equals("catename")){
	    				res.setSubject(sText);
	    			}else if(stag.equals("coverurl")){
	    				res.setCover(sText);
	    			}else if(stag.equals("score")){
	    				res.setScore(sText);
	    			}else if(stag.equals("scorecount")){
	    				res.setScoreCount(sText);
	    			}else if(stag.equals("date")){
	    				res.setDate(sText);
	    			}else if(stag.equals("ownerintro")){
	    				res.setAbstracts(sText);
	    			}else if(stag.equals("playtimes")){
	    				res.setPlaytimes(sText);
	    			}else if(stag.equals("ownerphoto")){
	    				res.setSpeakerPhoto(sText);
	    			}
	    	   	} 
	    	   	eventType = xmlParser.next();// */
	    	}//end while; 
	    } 
	    catch (XmlPullParserException e) 
	    {
	    	e.printStackTrace();
	    } 
	    catch (IOException e) 
	    {
	    	e.printStackTrace();
	    }
	    
	}
	
	private void GetSearchResultPull() throws XmlPullParserException {
		if(resXML == null) {		
			iResponse = 1;
			return;
		}
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	    factory.setNamespaceAware(true);
	    XmlPullParser xmlParser = factory.newPullParser();	       
	    InputStream iStream = new ByteArrayInputStream(resXML.getBytes());//FileInputStream(resXML); 
	    xmlParser.setInput(iStream, "UTF-8");
	    try{
	    	int eventType = xmlParser.getEventType();
	    	String stag = null;
	    	while (eventType != XmlPullParser.END_DOCUMENT) { 
	    		   
	    		if(eventType == XmlPullParser.START_DOCUMENT){
	    			serList = new ArrayList<SeriesInfo>();
	    		} 
	    		else if (eventType == XmlPullParser.START_TAG) {
	    			stag = xmlParser.getName();
	    			if(stag.equals("seriesitem"))
	    			{
	    				res = new SeriesInfo();
	    	   		}
	    	   	} 
	    		else if (eventType == XmlPullParser.END_TAG) { 
	    	   		String etag = xmlParser.getName();
	    	   		if(etag.equals("seriesitem")){
	    	   			serList.add(res);
	                   	res = null;
	    	   		}
	    	   	} 
	    		else if (eventType == XmlPullParser.TEXT) { 
	    	   			
	    	   		String sText = xmlParser.getText();
	    	   		if(stag.equals("id")){
	    				res.setSerid(sText);
	    			}else if(stag.equals("title")){
	    				res.setTitle(sText);
	    			}else if(stag.equals("owner")){
	    				res.setKeySpeaker(sText);
	    			}else if(stag.equals("catename")){
	    				res.setSubject(sText);
	    			}else if(stag.equals("coverurl")){
	    				res.setCover(sText);
	    			}else if(stag.equals("score")){
	    				res.setScore(sText);
	    			}else if(stag.equals("scorecount")){
	    				res.setScoreCount(sText);
	    			}else if(stag.equals("date")){
	    				res.setDate(sText);
	    			}else if(stag.equals("ownerintro")){
	    				res.setAbstracts(sText);
	    			}else if(stag.equals("playtimes")){
	    				res.setPlaytimes(sText);
	    			}else if(stag.equals("ownerphoto")){
	    				res.setSpeakerPhoto(sText);
	    			}
	    	   	} 
	    	   	eventType = xmlParser.next();// */
	    	}//end while; 
	    } 
	    catch (XmlPullParserException e) 
	    {
	    	e.printStackTrace();
	    } 
	    catch (IOException e) 
	    {
	    	e.printStackTrace();
	    }
	    
	}
	
	private void GetResultCountPull() throws XmlPullParserException {
		if(resXML == null)
		{		
			iResponse = 1;
			return;
		}
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	    factory.setNamespaceAware(true);
	    XmlPullParser xmlParser = factory.newPullParser();	       
	    InputStream iStream = new ByteArrayInputStream(resXML.getBytes());
	    xmlParser.setInput(iStream, "UTF-8");
	    try{
	    	int eventType = xmlParser.getEventType();
	    	String stag = null;
	    	while (eventType != XmlPullParser.END_DOCUMENT) { 
	    		
	    		if (eventType == XmlPullParser.START_TAG) {
	    			stag = xmlParser.getName();
	    			if(stag.equals("count"))
	    			{
	    				iResultCount = 0;
	    	   		}
	    	   	} 
	    		else if (eventType == XmlPullParser.TEXT) { 
	    	   		String sText = xmlParser.getText();
	    	   		if(stag.equals("count")){
	    	   			iResultCount = Integer.valueOf(sText);
	    	   			break;
	    			}
	    	   	} 
	    	   	eventType = xmlParser.next();// */
	    	}//end while; 
	    } 
	    catch (XmlPullParserException e) 
	    {
	    	e.printStackTrace();
	    } 
	    catch (IOException e) 
	    {
	    	e.printStackTrace();
	    }
	    
	}
	
	private void parseUserXML() throws Exception {
		if(resXML == null)
		{		
			iResponse = 1;
			return;
		}
		
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	    factory.setNamespaceAware(true);
		XmlPullParser parser = factory.newPullParser();
		InputStream iStream = new ByteArrayInputStream(resXML.getBytes());
		parser.setInput(iStream, "UTF-8");
		int eventType = parser.getEventType();//产生第一个事件
		while(eventType!=XmlPullParser.END_DOCUMENT){//只要不是文档结束事件
			switch (eventType) {
		
				case XmlPullParser.START_TAG:
					String name = parser.getName();//获取解析器当前指向的元素的名称
					if("user".equals(name)){
						userinfo = new UserInformation();
					}
					if(userinfo!=null){
						if("status".equals(name)){
							userinfo.setUserStatus(Integer.valueOf(parser.nextText()));//获取解析器当前指向元素的下一个文本节点的值
						}
						if("xingbi".equals(name)){
							userinfo.setCoinsCount(Integer.valueOf(parser.nextText()));
						}
						if("ismb".equals(name)){
							userinfo.setMenberType(Integer.valueOf(parser.nextText()));
						}
						if("usertype".equals(name)){
							userinfo.setUsertype(new String(parser.nextText()));
						}
					}
					break;

			}
			eventType = parser.next();
		}
		
	}
	
	private void parseCategoryNameXML() throws Exception {
		if(resXML == null)
		{
			return;
		}
		
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	    factory.setNamespaceAware(true);
		XmlPullParser parser = factory.newPullParser();
		InputStream iStream = new ByteArrayInputStream(resXML.getBytes());
		parser.setInput(iStream, "UTF-8");
		int eventType = parser.getEventType();//产生第一个事件
		
		CategoryNameInfo cni = null;
		while(eventType!=XmlPullParser.END_DOCUMENT){//只要不是文档结束事件
			switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					cateNameList = new ArrayList<CategoryNameInfo>();
					break;
				case XmlPullParser.START_TAG:
					String name = parser.getName();//获取解析器当前指向的元素的名称
					if(name.equals("cateitem")){
						cni = new CategoryNameInfo();
					}
					if(cni!=null){
						
						if(name.equals("id")){
							cni.setCateId(new String(parser.nextText()));
						}
						if(name.equals("name")){
							cni.setTitle(new String(parser.nextText()));
						}
						if(name.equals("level")){
							cni.setLevel(new String(parser.nextText()));
						}
						if(name.equals("parentid")){
							cni.setParentId(new String(parser.nextText()));
						}
						if(name.equals("seriescount")){
							cni.setSeriesCount(new String(parser.nextText()));
						}
						if(name.equals("subcategory")){
							cni.setHasChild(new String(parser.nextText()));
						}
					}
					break;
				case XmlPullParser.END_TAG:
					String sEnd = parser.getName();
					if(sEnd.equals("cateitem")) {
						cateNameList.add(cni);
					}
					break;

			}
			eventType = parser.next();
		}
		
	}
	
}

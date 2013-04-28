package com.chaoxing.parser;
/** 
 * @author xin_yueguang
 * @version 2011-8-24
 * @功能描述：解析服务器返回的精彩推荐的Top6的XML数据
 */

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.chaoxing.document.SeriesInfo;

public class TopRecommendXmlHandler extends DefaultHandler{
	private SeriesInfo res;
	private StringBuffer buf = new StringBuffer();
	private ArrayList<SeriesInfo> serList;

	public ArrayList<SeriesInfo> getTopSeriesInfo()
	{
		return serList;
	}
	
	@Override
	public void startDocument() throws SAXException{
		serList = new ArrayList<SeriesInfo>();
	}
	
	@Override
	public void endDocument() throws SAXException
	{
		
	}
	
	@Override
	public void startElement(String namespaceURI,String localName,
			String qName,Attributes atts) throws SAXException
	{
		if(localName.equals("seriesitem")){
			res = new SeriesInfo();
		}
	}
	
	@Override
	public void endElement(String namespaceURI,String localName,
			String qName) throws SAXException
	{
		if(localName.equals("seriesitem")&&res!=null){
			serList.add(res);
			res = null;
		} 
		
		if(res != null){
		if(localName.equals("id")){
			res.setSerid(buf.toString());
		}else if(localName.equals("title")){
			res.setTitle(buf.toString());
		}else if(localName.equals("owner")){
			res.setKeySpeaker(buf.toString());
		}else if(localName.equals("catename")){
			res.setSubject(buf.toString());
		}else if(localName.equals("coverurl")){
			res.setCover(buf.toString());
		}else if(localName.equals("score")){
			res.setScore(buf.toString());
		}else if(localName.equals("scorecount")){
			res.setScoreCount(buf.toString());
		}else if(localName.equals("date")){
			res.setDate(buf.toString());
		}else if(localName.equals("ownerintro")){
			res.setAbstracts(buf.toString());
		}else if(localName.equals("playtimes")){
			res.setPlaytimes(buf.toString());
		}else if(localName.equals("ownerphoto")){
			res.setSpeakerPhoto(buf.toString());
		}
		buf.setLength(0);
		}
	}
	
	@Override
	public void characters(char ch[],int start,int length)
	{
		buf.append(ch, start, length);
	}
	
	@Override
	public void fatalError(SAXParseException e)
	{
		String strMsg = e.getMessage();
	}
	
	@Override
	public void error (SAXParseException e) 
	{
		String strMsg = e.getMessage();
	}

	
}

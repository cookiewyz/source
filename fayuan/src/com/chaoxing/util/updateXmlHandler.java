/*
 * 功能描述：解析XML数据
 * Author：liujun
 */

package com.chaoxing.util;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import com.chaoxing.document.Global;


public class updateXmlHandler extends DefaultHandler{
	private StringBuffer buf = new StringBuffer();
	
	@Override
	public void startDocument() throws SAXException{
		
	}
	
	@Override
	public void endDocument() throws SAXException
	{
		
	}
	
	@Override
	public void startElement(String namespaceURI,String localName,
			String qName,Attributes atts) throws SAXException
	{
		if(localName.equals("ver")){
			
		}
	}
	
	@Override
	public void endElement(String namespaceURI,String localName,
			String qName) throws SAXException
	{
		if(localName.equals("appname"))
		{
			Global.appName = buf.toString().trim();
		}
		else if(localName.equals("apkname")){
			Global.apkName = buf.toString().trim();
		}
		else if(localName.equals("versionCode")){
			int verCode = Integer.valueOf(buf.toString().trim()).intValue();
			Global.serverVersion = verCode;
		}
		else if(localName.equals("versionName")){
			Global.verName = buf.toString().trim();
		}
		else if(localName.equals("updateInfo")){
			String updateInfo = buf.toString().trim();
			Global.updateInfo = updateInfo.replace(",|", "\n");
		}
		buf.setLength(0);
	}
	
	@Override
	public void characters(char ch[],int start,int length)
	{
		buf.append(ch,start,length);
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

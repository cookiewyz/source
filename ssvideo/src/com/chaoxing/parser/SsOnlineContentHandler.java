package com.chaoxing.parser;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.chaoxing.database.SSVideoPlayListBean;

import android.util.Log;

public class SsOnlineContentHandler extends DefaultHandler implements ContentHandler {
	String videoId, title, cateid, catename, series, seriesid, owner, coverUrl, m3u8Url, score, scoreCount, playtimes;
	String tagName;
	String mp4Url= "";
	String TAG = "onlineContentHandler";
	private final static int ONLINE = 1;
	private SsOnlineContentListener listener;
	
	public SsOnlineContentHandler(SsOnlineContentListener listener){
		this.listener = listener;
	}
	@Override
	public void startDocument() throws SAXException {
		Log.i(TAG, "---------------begin-----------------");
	}
	@Override
	public void endDocument() throws SAXException {
		Log.i(TAG, "---------------end------------------");
		listener.onEndDocument();
	}
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		tagName = localName;
//		Log.i(TAG, "startElement-------/-" + localName);
		if(localName.equals("videoitem")){
			for(int i=0; i<attributes.getLength(); i++){
				Log.i(TAG, attributes.getLocalName(i) + "=" + attributes.getValue(i));
			}
		}
	}
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
//		Log.i(TAG, "endElement----------" + localName);
		if(localName.equals("videoitem")){
			SSVideoPlayListBean playListBean = new SSVideoPlayListBean();
			playListBean.setnCurrentPlay(1);
			playListBean.setnCurrentPlayTime(0);
			playListBean.setnVideoType(ONLINE);
			playListBean.setStrM3u8Url(m3u8Url);
			playListBean.setStrPlayTimes(playtimes);
			playListBean.setStrRemoteCoverUrl(coverUrl);
			playListBean.setStrScore(score);
			playListBean.setStrScoreCount(scoreCount);
			playListBean.setStrSeriesId(seriesid);
			playListBean.setStrSpeaker(owner);
//			if(catename != null)
//				playListBean.setStrSubject(catename.trim());
			playListBean.setStrCateId(cateid);
			playListBean.setStrVideoId(videoId);
			playListBean.setStrVideoName(series);
			playListBean.setStrVideoFileName(title);
			playListBean.setStrVideoRemoteUrl(mp4Url);
			mp4Url = "";
			this.listener.onEndElement(playListBean);
//			Log.i(TAG, "one video item is parsed------------>"+mp4Url);
		}
	}
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if(tagName.equals("id")){
			videoId = new String(ch, start, length);
		}else if(tagName.equals("title")){
			title = new String(ch, start, length);
		}else if(tagName.equals("owner")){
			owner = new String(ch, start, length);
		}else if(tagName.equals("seriesid")){
			seriesid = new String(ch, start, length);
		}else if(tagName.equals("series")){
			series = new String(ch, start, length);
		}else if(tagName.equals("url1")){
//			mp4Url = new String(ch, start, length);
			String unUrl = new String(ch, start, length);
			mp4Url = mp4Url+unUrl;
		}else if(tagName.equals("cateid")){
			cateid = new String(ch, start, length);
		}else if(tagName.equals("catename")){
			catename = new String(ch, start, length);
		}else if(tagName.equals("coverurl")){
			coverUrl = new String(ch, start, length);
		}else if(tagName.equals("score")){
			score = new String(ch, start, length);
		}else if(tagName.equals("scorecount")){
			scoreCount = new String(ch, start, length);
		}else if(tagName.equals("playtimes")){
			playtimes = new String(ch, start, length);
		}
	}
}

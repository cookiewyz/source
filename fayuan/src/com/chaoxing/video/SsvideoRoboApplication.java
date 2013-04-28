package com.chaoxing.video;

import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.content.Intent;
import com.chaoxing.database.UsersDbHelper;
import com.chaoxing.document.ConstantModule;
import com.chaoxing.document.Global;
import com.chaoxing.download.DownloadData;
import com.chaoxing.util.AppConfig;
import com.chaoxing.util.updateXmlHandler;
import com.chaoxing.video.fayuan.R;
import com.google.inject.Module;

import roboguice.application.RoboApplication;

public class SsvideoRoboApplication extends RoboApplication {
	private UsersDbHelper dbHelper;
	
	@Override
	public void onCreate() {
		super.onCreate();
		dbHelper = new UsersDbHelper(null);
		initGlobal();
		if(!AppConfig.ORDER_ONLINE_VIDEO) {
			checkUpdate();
		}
		Intent service = new Intent("com.chaoxing.video.CoverService");//modify_by_xin_2013-1-18.
		startService(service);//modify_by_xin_2013-1-18.
	}
	
	public UsersDbHelper getUserDbHelper() {
		return this.dbHelper;
	}
	
	public void closeUserDb(){
		if(this.dbHelper != null){
			this.dbHelper.close();
		}
	}

	protected void addApplicationModules(List<Module> modules) {
        modules.add(new ConstantModule());
    }
	
	private void checkUpdate(){
		Thread thread = new Thread(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				//super.run();
				try
				{	
					DownloadData dl = new DownloadData();
					String url = getString(R.string.update_url)+ AppConfig.userRootDir +"_update_v1.xml";
					InputStream istream = dl.download(url);
					if(istream != null){
						SAXParserFactory spf = SAXParserFactory.newInstance();
						SAXParser sp = spf.newSAXParser();
						XMLReader xr = sp.getXMLReader();
						updateXmlHandler xmlHandler = new updateXmlHandler();
						xr.setContentHandler(xmlHandler);
						InputSource is = new InputSource(istream);         
						xr.parse(is);
//						Log.i("zzy", "splash checkUpdate----------------------------->");
					}
				}catch(Exception e)
				{		
					e.printStackTrace();
				}
			}
		};
		thread.start();
		thread = null;
	}
	
	private void initGlobal(){
		try{
			Global.localVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;//获取并设置本地版本号
			Global.serverVersion = 1;
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
		closeUserDb();
		Intent service = new Intent("com.chaoxing.video.CoverService");//modify_by_xin_2013-1-18.
		stopService(service);//modify_by_xin_2013-1-18.
	}
}

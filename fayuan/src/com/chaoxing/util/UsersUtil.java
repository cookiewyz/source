package com.chaoxing.util;
/**
 * @author xin_yueguang.
 * @version 2012-12-20.
 */
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import com.chaoxing.httpservice.HttpRequest;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

public class UsersUtil {
	private final static String TAG = "UsersUtil";
	
	/**
	 * ��ѹzip�ļ�
	 * @param zipStream zip�ļ���
	 * @param outputDirectory ��ѹ���Ŀ¼
	 */
	public static void unzip(InputStream zipStream, String outputDirectory) {
        try {
            ZipInputStream in = new ZipInputStream(zipStream);
            // ��ȡZipInputStream�е�ZipEntry��Ŀ��һ��zip�ļ��п��ܰ������ZipEntry��
            // ��getNextEntry�����ķ���ֵΪnull�������ZipInputStream��û����һ��ZipEntry��
            // ��������ȡ��ɣ�
            File file = new File(outputDirectory);
            if(!file.exists()){
            	file.mkdir();
            }
            ZipEntry entry = in.getNextEntry();
            while (entry != null) {
                if (entry.isDirectory()) {
                    String name = entry.getName();
                    name = name.substring(0, name.length() - 1);
//                    str = new String(str.getBytes("8859_1"), "GB2312");  
                    file = new File(outputDirectory + File.separator + name);
                    file.mkdir();
                } 
                else {
                    file = new File(outputDirectory + File.separator + entry.getName());
                    //Log.d(TAG, file.getAbsolutePath());
                    file.createNewFile();
                    FileOutputStream out = new FileOutputStream(file);
                    int len;
                    byte[] buffer = new byte[1024];
                    while ((len = in.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                    out.close();
                }
                // ��ȡ��һ��ZipEntry
                entry = in.getNextEntry();
            }
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	/**
	 * ��ѹzip�ļ�
	 * @param zipFile zipԴ�ļ�
	 * @param folderPath ��ѹ���Ŀ¼
	 * @throws ZipException Դ�ļ�����zipѹ���ļ�ʱ����
	 * @throws IOException
	 */
	public static void upZipFile(File zipFile, String folderPath) throws ZipException, IOException {  
        File desDir = new File(folderPath);  
        if (!desDir.exists()) {
            desDir.mkdirs();
        }
        ZipFile zf = new ZipFile(zipFile);
        for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements();) {
            ZipEntry entry = ((ZipEntry)entries.nextElement());
            InputStream in = zf.getInputStream(entry);
            String str = folderPath + File.separator + entry.getName();
            str = new String(str.getBytes("8859_1"), "GB2312");
            File desFile = new File(str);
            if (!desFile.exists()) {
                File fileParentDir = desFile.getParentFile();
                if (!fileParentDir.exists()) {
                    fileParentDir.mkdirs();
                }
                desFile.createNewFile();
            }
            OutputStream out = new FileOutputStream(desFile);
            byte buffer[] = new byte[1024];
            int realLength;
            while ((realLength = in.read(buffer)) > 0) {
                out.write(buffer, 0, realLength);
            }
            in.close();
            out.close();
        }
    }
	
	/**
	 * �����ļ���Ŀ��Ŀ¼
	 * @param inFileName Դ�ļ�
	 * @param outFileName Ŀ���ļ�
	 */
	public static void copyFile2Path(String inFileName, String outFileName) {
    	try {
    		File inputFile = new File(inFileName);
    		if (!inputFile.exists()) {
    			Log.e(TAG, "copyfile: input file not exist��"+ inFileName);
    			return;
    		}
    		
			byte[] buffer = new byte[1024];
			int count = 0;
			InputStream is = new FileInputStream(inFileName);
			FileOutputStream fos = new FileOutputStream(outFileName);
			while ((count = is.read(buffer)) > 0) {
				fos.write(buffer, 0, count);
			}
			fos.flush();
			fos.close();
			is.close();
    	} catch (Exception e) {
    		Log.e(TAG, "copy file error: "+ e.getMessage());
    	}
    }
	
	public static void unZipRawResource(Context context, String filename, int resId, boolean unZipToSubDir) {
		File tagFile = new File(filename);
		if(tagFile.exists() && tagFile.isFile()) {
			return;
		}
		InputStream inStream = null;
		try {
			inStream = context.getResources().openRawResource(resId);
		} catch(NotFoundException e) {
			Log.e(TAG, "rawResource file not exist.");
			return;
		}
		unZipRawResource(inStream, filename, false, unZipToSubDir);
	}
	
	public static void unZipRawResource(InputStream rawResStream, String dir, String name) {
		File dbFile = new File(dir +"/"+ name);
		if(!dbFile.exists() || !dbFile.isFile()) {
			unzip(rawResStream, dir);
		}
	}
	
	/**
	 * ��ѹ��Դ�ļ���sdCardͬʱ���浽ϵͳĿ¼
	 * @param rawResStream ��Դ�ļ���
	 * @param name Ŀ���ļ�����
	 */
	public static void unZipRawResource(InputStream rawResStream, String name) {
		unZipRawResource(rawResStream, name, true, false);
	}
	/**
	 * ��ѹ��Դ�ļ�
	 * @param rawResStream ��Դ�ļ���
	 * @param name Ŀ���ļ�����
	 * @param copyToSysDir true: ��ѹ��sdCardͬʱ���浽ϵͳĿ¼, false: ֻ��ѹ��sdCard.
	 */
	public static void unZipRawResource(InputStream rawResStream, String name, boolean copyToSysDir, boolean unZipToSubDir) {
		String tagCardFile = null;
		String tagSysFile = null;
		if(unZipToSubDir) {
			tagCardFile = getRootSubDir(name);
			tagSysFile = getSystermSubDir(name);
		}
		else {
			tagCardFile = getRootDir();
			tagSysFile = getSystermDir();
		}
		if (!existSDCard()) {
			File dbsFile = new File(tagSysFile +"/"+ name);
			if(dbsFile.exists() && dbsFile.isFile()/* && dbsFile.length() > 10000*/)
				return;
			unzip(rawResStream, tagSysFile);
		}
		else {
			File dbFile = new File(tagCardFile +"/"+ name);
			if(!dbFile.exists() || !dbFile.isFile()) {
				unzip(rawResStream, tagCardFile);
			}
			
			if(copyToSysDir) {
	    		File dbTagFile = new File(tagSysFile +"/"+ name);  
	    		if (!dbTagFile.exists()/* || dbTagFile.length() < 10000*/) {
	    			copyFile2Path(tagCardFile +"/"+ name, tagSysFile +"/"+ name);
	    		}
			}
		}
	}
	
	/**
	 * ��ȡ�������ݿ⵽ sdCard Ŀ¼
	 * @param context ��ǰactivity�� Context.
	 */
	public static void loadVideoDB2Card(Context context) {
		InputStream inStream = null;
		try {
			inStream = context.getResources().openRawResource(AppConfig.userDbID);
		} catch(NotFoundException e) {
			Log.e(TAG, "rawResource file videoDb not exist.");
			return;
		}
		String cardPath = getUserDatabaseFileName();
		File dbFile = new File(cardPath);
		if(!dbFile.exists() || !dbFile.isFile()) {
			copyRawResource2Path(inStream, AppConfig.userDbName, false);
		}
	}
	
	/**
	 * ��ȡ�������ݿ⵽ sdCard ��ϵͳĿ¼
	 * @param context ��ǰactivity�� Context.
	 */
	public static void loadVideoDBFile(Context context) {
		InputStream inStream = null;
		try {
			inStream = context.getResources().openRawResource(AppConfig.userDbID);
		} catch(NotFoundException e) {
			Log.e(TAG, "rawResource file videoDb not exist.");
			return;
		}
		copyRawResource2Path(inStream, AppConfig.userDbName);
	}
	
	/**
	 * ������Դ�ļ�
	 * @param inStream ��Դ�ļ���
	 * @param name Ŀ���ļ�����
	 * @param copyToSysDir true: ���Ƶ�sdCardͬʱ���浽ϵͳĿ¼, false: ֻ���Ƶ�sdCard.
	 */
	public static void copyRawResource2Path(InputStream inStream, String name, boolean copyToSysDir) {
		String sysPath = getSystermDatabaseDir() +"/"+ name;//
		if (!existSDCard()) {
			File dbsFile = new File(sysPath);
			if(dbsFile.exists() && dbsFile.isFile())
				return;
			copyRawResource(inStream, sysPath);
		}
		else {
			String cardPath = getUserDatabaseDir() +"/"+ name;
			File dbFile = new File(cardPath);
			if(!dbFile.exists() || !dbFile.isFile()) {
				copyRawResource(inStream, cardPath);
			}
			
			if(copyToSysDir) {
				File dbsFile = new File(sysPath);
				if(dbsFile.exists() && dbsFile.isFile())
					return;
				copyFile2Path(cardPath, sysPath);
			}
		}
	}
	
	/**
	 * ������Դ�ļ���sdCardͬʱ���浽ϵͳĿ¼
	 * @param inStream ��Դ�ļ���
	 * @param name Ŀ���ļ�����
	 */
	public static void copyRawResource2Path(InputStream inStream, String name) {
		copyRawResource2Path(inStream, name, true);
	}
	
	/**
	 * ������Դ�ļ���ָ��Ŀ¼
	 * @param context ��ǰactivity�� Context.
	 * @param filename Ŀ���ļ�����·��
	 * @param resId raw��Դ id
	 */
	public static void copyRawResource(Context context, String filename, int resId) {
		File tagFile = new File(filename);
		if(tagFile.exists() && tagFile.isFile()) {
			return;
		}
		InputStream inStream = null;
		try {
			inStream = context.getResources().openRawResource(resId);
		} catch(NotFoundException e) {
			Log.e(TAG, "rawResource file not exist.");
			return;
		}
		copyRawResource(inStream, filename);
	}
	
	public static void copyRawResource(InputStream inStream, String tagPathFileName) {
    	try {
    		File tagDBFile = new File(tagPathFileName);
    		if (!tagDBFile.exists()) {
    			byte[] buffer = new byte[1024];
    			int count = 0;
    			FileOutputStream fos = new FileOutputStream(tagDBFile);
    			while ((count = inStream.read(buffer)) > 0) {
					fos.write(buffer, 0, count);     
				}
    			fos.flush();
				fos.close();
				inStream.close();
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
	
	/**
	 * /data/data/com.chaoxing.video/databases/video_info_category_local.db
	 */
	public static String getSystermCategoryDbFileName() {
		String readerDir = getSystermSubFile(AppConfig.localCategoryDbName, true);//getSystermDatabaseDir() +"/"+ AppConfig.localCategoryDbName;
		return readerDir;
	}
	
	/**
	 * /data/data/com.chaoxing.video/databases/ssvideo.db
	 */
	public static String getSystermDatabaseFileName() {
		String readerDir = getSystermSubFile(AppConfig.userDbName, true);//getSystermDatabaseDir() +"/"+ AppConfig.userDbName;
		return readerDir;
	}
	
	/**
	 * mnt/sdcard/ssvideo/video_info_category_local.db
	 */
	public static String getLocalCategoryDbFile() {
		String readerDir = getRootSubFile(AppConfig.localCategoryDbName, false);
		return readerDir;
	}
	
	/**
	 * /data/data/com.chaoxing.video/databases
	 */
	public static String getSystermDatabaseDir() {
		return getSystermSubDir("databases/");
	}
	
	/**
	 * mnt/sdcard/ssvideo/database/ssvideo.db
	 */
	public static String getUserDatabaseFileName() {
		String readerDir = getRootSubFile(AppConfig.userDbName, true);//getUserDatabaseDir() +"/"+ AppConfig.userDbName;
		return readerDir;
	}
	
	/**
	 * mnt/sdcard/ssvideo/database
	 */
	public static String getUserDatabaseDir() {
		return getRootSubDir("databases/");
	}
	
	/**
	 * mnt/sdcard/ssvideo/commendVideoList.ini
	 */
	public static String getCommendVideoIniFile() {
		String readerDir = getRootSubFile(AppConfig.commendVideoIni, false);//getRootDir() +"/"+ AppConfig.commendVideoIni;
		return readerDir;
	}
	
	/**
	 * mnt/sdcard/ssvideo/+ filePath.
	 */	
/*	public static String getFilePath(String filePath){
		String readerDir = getRootDir() +"/"+ filePath;
		return readerDir;
	}*/
	public static String getPath(String subPath) {
		return getRootSubDir(subPath);
	}
	
	/**
	 * mnt/sdcard/ssvideo/commendVideos.zip
	 */
	public static String getCommendVideoZipFile() {
		String readerDir = getRootSubFile(AppConfig.commendVideoZip, false);//getRootDir() +"/"+ AppConfig.commendVideoZip;
		return readerDir;
	}
	
	/**
	 * mnt/sdcard/ssvideo/commendVideos
	 */
	public static String getCommendVideoDir() {
		String readerDir = getRootDir() +"/"+ AppConfig.commendVideo;
		return readerDir;
	}
	
	/**
	 * mnt/sdcard/ssvideo/logocfg.ini
	 */
	public static String getLogoIniFile() {
		String readerDir = getRootSubFile(AppConfig.logoIni, false);//getRootDir() +"/"+ AppConfig.logoIni;
		return readerDir;
	}
	
	/**
	 * mnt/sdcard/ssvideo/covers
	 */
	public static String getUserCoverDir() {
		return getRootSubDir("covers/");
	}
	
	/**
	 * mnt/sdcard/ssvideo
	 */
	public static String getRootDir() {
		String sdPath = Environment.getExternalStorageDirectory() +"/";
		String readerDir = sdPath + AppConfig.userRootDir +"/";
		File reader = new File(readerDir);
		if(!reader.exists() || !reader.isDirectory()) {
			reader.mkdirs();
		}
		return reader.getAbsolutePath();
	}
	
	/**
	 * mnt/sdcard/ssvideo/+ subPath
	 */
	public static String getRootSubDir(String subPath) {
		String sdPath = getRootDir() +"/"+ subPath;
		File reader = new File(sdPath);
		if(!reader.exists() || !reader.isDirectory()) {
			reader.mkdirs();
		}
		return reader.getAbsolutePath();
	}
	
	/**
	 * mnt/sdcard/ssvideo/+ subFilename
	 */
	public static String getRootSubFile(String subFilename, boolean isDbDirectory) {
		String sdPath = null;
		if(isDbDirectory) {
			sdPath = getUserDatabaseDir() +"/"+ subFilename;
		}
		else {
			sdPath = getRootDir() +"/"+ subFilename;
		}
		File reader = new File(sdPath);
		if(!reader.getParentFile().exists() || !reader.getParentFile().isDirectory()) {
			reader.getParentFile().mkdirs();
		}
		return reader.getAbsolutePath();
	}
	
	/**
	 * /data/data/com.chaoxing.video
	 */
	public static String getSystermDir() {
		String readerDir = "/data/data/"+ AppConfig.packageName +"/";
		File reader = new File(readerDir);
		if(!reader.exists() || !reader.isDirectory()) {
			reader.mkdirs();
		}
		return reader.getAbsolutePath();
	}
	
	/**
	 * /data/data/com.chaoxing.video/+ subPath
	 */
	public static String getSystermSubDir(String subPath) {
		String readerDir = getSystermDir() +"/"+ subPath;
		File reader = new File(readerDir);
		if(!reader.exists() || !reader.isDirectory()) {
			reader.mkdirs();
		}
		return reader.getAbsolutePath();
	}
	
	/**
	 * mnt/sdcard/com.chaoxing.video/+ subFilename
	 */
	public static String getSystermSubFile(String subFilename, boolean isDbDirectory) {
		String sdPath = getSystermDir() +"/"+ subFilename;
		if(isDbDirectory) {
			sdPath = getSystermDatabaseDir() +"/"+ subFilename;
		}
		else {
			sdPath = getSystermDir() +"/"+ subFilename;
		}
		File reader = new File(sdPath);
		if(!reader.getParentFile().exists() || !reader.getParentFile().isDirectory()) {
			reader.getParentFile().mkdirs();
		}
		return reader.getAbsolutePath();
	}
	
	/**
	 * �ж�sd���Ƿ����
	 */
	public static boolean existSDCard() {
		boolean sdCardExist = Environment.getExternalStorageState() 
				.equals(android.os.Environment.MEDIA_MOUNTED);
		return sdCardExist;
	}
	
	/**
     * ��ȡ�豸ΨһID
     */
	public static String getUniqueId(Context context) {
		String uniqueId = "";
		String mac = getLocalMacAddress(context);
		if(mac != null && mac.length() > 0) {
			uniqueId = mac;
		}
		else {
			String devId = getDeviceId(context);
			if(devId != null && devId.length() > 0) {
				uniqueId = devId;
			}
		}
		return Integer.toString(uniqueId.hashCode());
    }
	
    /**
     * ��ȡMAC��ַ
     */
	public static String getLocalMacAddress(Context context) {
    	WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    	if(wifi == null)
    		return "";
    	WifiInfo info = wifi.getConnectionInfo();
    	if(info == null)
    		return "";
    	return info.getMacAddress();
    }
	
    /**
     * ��ȡ�豸ID�����кţ�, ��ʱȡ������
     */
	public static String getDeviceId(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
    }
	
	public static void saveUserName(Context context, String uName) {
		SaveConfig(context, "userName", uName);
	}

	public static String getUserName(Context context) {
		return GetConfigS(context, "userName");// Ĭ��Ϊnull
	}
	
	public static void saveUserPassWord(Context context, String uName) {
		SaveConfig(context, "passWord", uName);
	}

	public static String getUserPassWord(Context context) {
		return GetConfigS(context, "passWord");// Ĭ��Ϊnull
	}
	
	public static void saveUserDeviceId(Context context, String devideid) {
		SaveConfig(context, "deviceId", devideid);
	}

	public static String getUserDeviceId(Context context) {
		return GetConfigS(context, "deviceId");// Ĭ��Ϊnull
	}
	
	public static void SaveConfig(Context context, String key, int value) {
		SharedPreferences share = context.getSharedPreferences("userCfg", Context.MODE_PRIVATE);
		Editor editor = share.edit();// ȡ�ñ༭��
		editor.putInt(key, value);// �洢���� ����1 ��key ����2 ��ֵ
		editor.commit();// �ύˢ������
	}

	public static int GetConfigI(Context context, String key) {
		SharedPreferences share = context.getSharedPreferences("userCfg", Context.MODE_PRIVATE);
		return share.getInt(key, 0);// ȡ��key��Ӧ��ֵ ������ ��Ĭ��Ϊ0
	}
	
	public static void SaveConfig(Context context, String key, String value) {
		SharedPreferences share = context.getSharedPreferences("userCfg", Context.MODE_PRIVATE);
		Editor editor = share.edit();// ȡ�ñ༭��
		editor.putString(key, value);// �洢���� ����1 ��key ����2 ��ֵ
		editor.commit();// �ύˢ������
	}

	public static String GetConfigS(Context context, String key) {
		SharedPreferences share = context.getSharedPreferences("userCfg", Context.MODE_PRIVATE);
		return share.getString(key, "");// ȡ��key��Ӧ��ֵ ������ ��Ĭ��Ϊnull
	}
	
	public static Bitmap getBitmap(String url) {
		Bitmap bmp;
		HttpRequest hr = new HttpRequest();
		bmp = hr.getImageUrlRequest(url);
		return bmp; 
	}
	
	public static Bitmap getCoverImage(String imgLink) throws Throwable {
		byte[] bytes = getByte(imgLink);
		Bitmap coverImageBitmap = BitmapFactory.decodeByteArray(bytes, 0,
				bytes.length, null);
//		int bmpWidth=coverImageBitmap.getWidth(); 
//	    int bmpHeight=coverImageBitmap.getHeight();
//	    Matrix matrix = new Matrix();
//        matrix.postScale((float) (bmpWidth*1.2), (float) (bmpHeight*1.2));
//        Bitmap resizeBmp = Bitmap.createBitmap(coverImageBitmap,0,0,bmpWidth, 
//                bmpHeight,matrix,true); 
		return coverImageBitmap;
	}
	
	public static byte[] getByte(String urlPath) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int len = 0;

		URL url = new URL(urlPath);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(500000);
		InputStream inStream = conn.getInputStream();		

		while ((len = inStream.read(data)) != -1) {
			outStream.write(data, 0, len);
		}
		inStream.close();
		return outStream.toByteArray();
	}
	
	public static int getSDKVersionNumber() {
		int sdkVersion;
		try {
		sdkVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
		} catch (NumberFormatException e) {
		sdkVersion = 0;
		}
		return sdkVersion; 
	}
    
	
}

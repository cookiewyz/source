package com.chaoxing.video;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Video;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chaoxing.database.SSVideoCategoryBean;
import com.chaoxing.database.SSVideoDatabaseAdapter;
import com.chaoxing.database.SSVideoLocalVideoBean;
import com.chaoxing.util.AppConfig;
import com.chaoxing.util.BitmapUtil;
import com.chaoxing.util.UsersUtil;
import com.chaoxing.util.WidgetUtil;
import com.chaoxing.video.fayuan.R;

/**
 * @author create_by_xin.
 * @version modify_by_xin_2011-11-15.
 */

public class Splash extends Activity {
	private final static String TAG = "Splash";
	private final static int SPLASH_DISPLAY_TIME = 2500;
	private Context m_context;
	private String SScoversPath;
	private File mFile;
//	private String DatabasePath = "/data/data/com.fayuan.video/databases/";
	private SSVideoDatabaseAdapter mDBAdapter = null;
	private SsvideoRoboApplication ssvideo;
	private boolean fromBKW = false;
	protected Intent intentBK;
	
	private ProgressBar pbLoadVideo;
	private ProgressBar pbLoadDB;
	private TextView tvLoadText;
	private List<File> listLocalVideoFils;
	private ImageView mWellcome;
	
	private int mResponse = -100;
	private Dialog mDialog = null;
	private TextView tvErrorMsg ;
    private ProgressBar proBarLogin;
    private MyHandler handler = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		pbLoadDB = (ProgressBar) findViewById(R.id.pb_loading_database);
		pbLoadDB.setVisibility(View.GONE);
		pbLoadVideo = (ProgressBar) findViewById(R.id.pb_loading_local_videos);
		pbLoadVideo.setVisibility(View.GONE);
		tvLoadText = (TextView) findViewById(R.id.tv_loading_text);
		tvLoadText.setVisibility(View.GONE);
		mWellcome = (ImageView)findViewById(R.id.wellcome_cover);
		listLocalVideoFils = new ArrayList<File>();
		initMember();
		if(AppConfig.ORDER_ONLINE_VIDEO) {
			mWellcome.setImageResource(R.drawable.all_wellcome_cover_common);
		}
		else {
			mWellcome.setImageResource(R.drawable.all_wellcome_cover_common);
		}
		intentBK = this.getIntent();
		if(intentBK != null){
			fromBKW = true;
		}
//		Start();
		if(AppConfig.ORDER_ONLINE_VIDEO) {//法源
			String url = WidgetUtil.getFayuanLoginUrl(Splash.this);//法源
			if(url == "") {
				CreateLoginFayuanDialog(Splash.this);//法源
			}
			else {
				Start();
			}
		}
		else {
			Start();
		}
		pbLoadVideo.setVisibility(View.GONE);
		tvLoadText.setVisibility(View.GONE);
	}
	
	@Override  
    protected void onDestroy() {
		super.onDestroy(); 
	}
	
/*	public final Handler handler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case 1:
				if(msg.arg1 > 0) {
					pbLoadVideo.setVisibility(View.VISIBLE);
					tvLoadText.setVisibility(View.VISIBLE);
				}
				pbLoadVideo.setMax(msg.arg1);
				break;
			case 2:
				pbLoadVideo.setProgress(msg.arg2+1);
				break;
			case 3:
				pbLoadDB.setVisibility(msg.arg1);
				break;
			case 200:
				loginState(msg.arg1);
				break;
			}
		}
	};*/
	
	static class MyHandler extends Handler {
        WeakReference<Splash> mActivity;

        MyHandler(Splash activity) {
                mActivity = new WeakReference<Splash>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
        		Splash theActivity = mActivity.get();
                switch (msg.what) {
                	case 1:
        				if(msg.arg1 > 0) {
        					theActivity.pbLoadVideo.setVisibility(View.VISIBLE);
        					theActivity.tvLoadText.setVisibility(View.VISIBLE);
        				}
        				theActivity.pbLoadVideo.setMax(msg.arg1);
        				break;
        			case 2:
        				theActivity.pbLoadVideo.setProgress(msg.arg2+1);
        				break;
        			case 3:
        				theActivity.pbLoadDB.setVisibility(msg.arg1);
        				break;
                	case 200:
                		theActivity.loginState(msg.arg1);
        				break;
                }
        }
	};
	
	public void Start() {
        new Thread() {
				public void run() {
                    localVideoInit();
                    Intent intent ;
                    if(fromBKW == true){
                    	intent = intentBK;
                    }else{
                    	intent = new Intent();
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intent.setClass(Splash.this, SsvideoActivity.class);
                    startActivity(intent);
                    finish();
                }
        }.start();
	}
	
	private void initMember() {
		m_context = Splash.this;
        SScoversPath = UsersUtil.getUserCoverDir() +"/";
        ssvideo = (SsvideoRoboApplication)getApplication();
        if(mDBAdapter == null) {
        	mDBAdapter = new SSVideoDatabaseAdapter(m_context, ssvideo, TAG);
        }
        mFile = new File(UsersUtil.getRootDir());
        handler = new MyHandler(this);
	}
	
	private void localVideoInit(){
        // 加载数据库
		setLoadingView(View.VISIBLE);
        UsersUtil.loadVideoDBFile(m_context);
        if(!AppConfig.ORDER_ONLINE_VIDEO) {
        	unZipCategoryDbFile();
        }
        setLoadingView(View.GONE);
        // 加载本地视频
        loadLocalVideo();
    }
	
    private void loadLocalVideo(){
//    	m_DBAdapter.checkAllCategory();
        if(mDBAdapter.isLocalVideoEmpty()){
        	listLocalVideoFils.clear();
        	int totalFiles = getFileCount(mFile, listLocalVideoFils);
        	if(totalFiles > 0) {
        		Message msg = handler.obtainMessage();//new Message();
        		msg.arg1 = totalFiles;
        		msg.what = 1;
        		handler.sendMessage(msg);
        		loadLocalVideos(listLocalVideoFils);
        	}
        	else {
        		sleepTimes(SPLASH_DISPLAY_TIME);
        	}
        }else{
        	sleepTimes(SPLASH_DISPLAY_TIME);
        }
    }
    
    private void sleepTimes(int milliseconds) {
    	try {
//    		Log.d("sleepTimes", "Thread sleep:"+ milliseconds/1000.00+" s");
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }

    // 搜索给定目录下的所有MP4文件，包括子目录    
	private int getFileCount(File dir, List<File> listFiles){
		int fileCount = 0;
		File[] theFiles = dir.listFiles();
		if(theFiles != null)
		for( File tempF : theFiles){
			if(tempF.isDirectory()){
				fileCount += getFileCount(tempF, listFiles);
			}else{
				if(tempF.getName().toLowerCase(Locale.getDefault()).indexOf(".mp4") > -1) {
					fileCount++;
					listFiles.add(tempF);
				}
			}
		}
		return fileCount;
	}
    	
	private void loadLocalVideos(List<File> listFiles){
		for (int i=0; i<listFiles.size(); i++){
			File tempF  = listFiles.get(i);
			Message msg = handler.obtainMessage();//new Message();
        	msg.what = 2;
        	msg.arg2 = i;
        	
			try{
				String tempFileName = tempF.getName().toLowerCase(Locale.getDefault());
				if(tempFileName.indexOf(".mp4")>-1 || tempFileName.indexOf(".mp4")>-1){
					String path =tempF.getPath();
					String fileName = tempF.getName();
					int fileLength = (int) tempF.length();
					String coverName = fileName.substring(0, fileName.lastIndexOf('.'));
//					Log.i(TAG, "file path+++++++++++++++++" + path);
					Bitmap bitmap = ThumbnailUtils.createVideoThumbnail (path, Video.Thumbnails.MINI_KIND);
					
					if(bitmap != null){
						BitmapUtil.saveBitmap2png(new File(SScoversPath), coverName+".png", bitmap, 213, 160);
					}
		        	SSVideoLocalVideoBean localVideoBean = new SSVideoLocalVideoBean();
		        	localVideoBean.setStrVideoFileName(fileName.substring(0, fileName.lastIndexOf('.')));
		        	localVideoBean.setStrVideoId("" + path.hashCode());
		        	String tempPath = path.substring(0, path.lastIndexOf('/'));
		        	
		        	String cateName = tempPath.substring(tempPath.lastIndexOf('/')+1);
		        	SSVideoCategoryBean cateBean = mDBAdapter.fetchCategoryByName(cateName);
		        	
		        	if(cateBean != null){
		        		localVideoBean.setStrCateId(cateBean.getStrCateId());
		        	}else{
		        		cateBean = mDBAdapter.fetchCategoryByName("未分类");
		        		if(cateBean == null){
		        			cateBean = new SSVideoCategoryBean();
		        			cateBean.setStrCateId("XXX");
		        			cateBean.setStrCateName("未分类");
		        			mDBAdapter.insertIntoCategory(cateBean);
		        		}
		        		localVideoBean.setStrCateId("XXX");
		        	}
		        	localVideoBean.setStrVideoLocalPath(path);
		        	localVideoBean.setStrCoverName(coverName + ".png");
		        	localVideoBean.setnVideoFileLength((int) fileLength);
		        	mDBAdapter.insertIntoLocalVideo(localVideoBean);

		        	handler.sendMessage(msg);
				}
			}catch(Exception e){
				handler.sendMessage(msg);
				e.printStackTrace();
			}
		}
	}
    	
	// 获取视频的封面图片
	public static Bitmap getVideoThumbnail(ContentResolver cr, String filePath) {   
	    Bitmap bitmap = null;   
	    BitmapFactory.Options options = new BitmapFactory.Options();   
	    options.inDither = false;   
	    options.inPreferredConfig = Bitmap.Config.ARGB_8888;   
	    options.inSampleSize=4;
	    Cursor cursor = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
	    		new String[] { MediaStore.Video.Media._ID }, 
	    		MediaStore.Video.Media.DATA + "=?", new String[]{filePath}, null);    
	    if (cursor == null || cursor.getCount() == 0) {   
	        return null;   
	    }   
	    cursor.moveToFirst();   
	    String videoId = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID));  //image id in image table.s   
	    if (videoId == null) {   
	    return null;   
	    }   
	    cursor.close();   
	    long videoIdLong = Long.parseLong(videoId);   
	    bitmap = MediaStore.Video.Thumbnails.getThumbnail (cr, videoIdLong,Video.Thumbnails.MINI_KIND, options);   
	    return bitmap;   
	}   
	
	public void saveMyBitmap(String bitName, Bitmap mBitmap) throws IOException {
		File saveDir = new File(SScoversPath);
		if(!saveDir.exists()) 	{
			saveDir.mkdirs();
		}else{
		}
	    File f = new File(saveDir, bitName + ".png");
	    f.createNewFile();
	    FileOutputStream fOut = null;
	    try {
	            fOut = new FileOutputStream(f);
	    } catch (FileNotFoundException e) {
	            e.printStackTrace();
	    }
	    
	    mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
	    try {
	            fOut.flush();
	    } catch (IOException e) {
	            e.printStackTrace();
	    }
	    try {
	            fOut.close();
	    } catch (IOException e) {
	            e.printStackTrace();
	    }
	}
	/*//methord same:
		public View LoadMainView(  LayoutInflater flater){
	    	View view = flater.inflate(R.layout.splash, null);  
			 return view;
	    }
	  private class LoadMainTask extends AsyncTask<Object, Object, View> {
			public LoadMainTask(Context context) {
			}
			protected View doInBackground(Object... params) {
				View view = null;
				view = LoadMainView(m_flater);
				//为了测试加了延时，大家可以在这一块加载资源，数据等
				try {
					Thread.sleep(0);//SPLASH_DISPLAY_TIME
				} catch (InterruptedException e) {			 
					e.printStackTrace();
				}
				return view;
			}
	    // 执行完毕
		protected void onPostExecute(View view) {
			setContentView(view);
			Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.setClass(Splash.this, SsvideoActivity.class);
            startActivity(intent);
            finish();
		}
	}//*/
	
    private void setLoadingView(int visible) {
    	Message msg = handler.obtainMessage();//new Message();
		msg.arg1 = visible;
		msg.what = 3;
		handler.sendMessage(msg);
    }
    
    private void unZipCategoryDbFile() {
    	InputStream inStream = null;
		try {
			inStream = getResources().openRawResource(AppConfig.localCategoryDbID);
		} catch(NotFoundException e) {
			Log.e(TAG, "rawResource file localCategoryDb not exist.");
			return;
		}
		
		String tagSysFile = UsersUtil.getSystermCategoryDbFileName();
		if (!UsersUtil.existSDCard()) {
			File dbsFile = new File(tagSysFile);
			if(!dbsFile.exists() || !dbsFile.isFile() || dbsFile.length() < 10000) {
				UsersUtil.unzip(inStream, UsersUtil.getSystermDatabaseDir());
			}
		}
		else {
			String tagCardFile = UsersUtil.getLocalCategoryDbFile();	 
			File dbcFile = new File(tagCardFile);
			if(!dbcFile.exists() || !dbcFile.isFile() || dbcFile.length() < 10000) {
				UsersUtil.unzip(inStream, UsersUtil.getRootDir());
			}
			    
			File dbFile = new File(tagSysFile);
			if(!dbFile.exists() || !dbFile.isFile() || dbcFile.length() < 10000) {
				UsersUtil.copyFile2Path(tagCardFile, tagSysFile);
			}
		}		
    }
	
    private void loginState(int state) {
    	String msg = "";
    	if(state == 0) {
    		msg = "登录成功！";
    		WidgetUtil.goFayuanLogin(Splash.this, false);
    		mDialog.dismiss();
    	}
    	else if(state == 1) {
		   msg = "编码转换错误！";
    	}
    	else if(state == 2) {
		   msg = "请输入用户名！";
    	}
    	else if(state == 3) {
		   msg = "从服务器获取用户信息失败！";
    	}
    	else {
		   msg = "登录失败！";
    	}
    	if(tvErrorMsg != null) {
    		tvErrorMsg.setText(msg);
    	}
    	if(proBarLogin != null) {
    		proBarLogin.setVisibility(View.GONE);
    	}
    	Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    	mResponse = -100;
    }
    
    public void CreateLoginFayuanDialog(final Context context) {
//	   	   final Dialog dialog = new Dialog(context);
    		if(mDialog == null) {
    			mDialog = new Dialog(context);
    		}
    		mDialog.setTitle("                                          用户登录");
	          LayoutInflater factory = LayoutInflater.from(context);
	          final View DialogView = factory.inflate(R.layout.login_dialog, null);
	          mDialog.setContentView(DialogView);
	          //final TextView tvTopHintMsg =  (TextView) DialogView.findViewById(R.id.txt_msg);
	          tvErrorMsg =  (TextView) DialogView.findViewById(R.id.txt_errormsg);
	          final EditText edtName =  (EditText) DialogView.findViewById(R.id.txt_username);
	          final EditText edtPasswd =  (EditText) DialogView.findViewById(R.id.txt_password);
	          proBarLogin = (ProgressBar) DialogView.findViewById(R.id.progressBarLogin);
	          
	          Button btnLoginDlg = (Button) DialogView.findViewById(R.id.btnlogin);
	          btnLoginDlg.setOnClickListener(new OnClickListener() {
	   			
	       	   @Override
	       	   public void onClick(View v) {
//	       		   Toast.makeText(context, "你选择了[登录]", Toast.LENGTH_SHORT).show();
	       		   final Editable eName = edtName.getEditableText(); 
	       		   final Editable ePwd = edtPasswd.getEditableText();
	       		   //tvErrorMsg.setText(eName.toString().trim());
	       		   proBarLogin.setVisibility(View.VISIBLE);
	       		   
	       		   if(mResponse == 0) {
	       			   Toast.makeText(Splash.this, "正在登录中", Toast.LENGTH_SHORT).show();
	       		   }
	       		   else {
	       			   mResponse = 0;
	//	       		   int ir = WidgetUtil.getUserLoginFayuanInfo(eName.toString().trim(), ePwd.toString().trim());
		       		   new Thread() {
							public void run() {
								int ir = WidgetUtil.getUserLoginFayuanInfo(eName.toString().trim(), ePwd.toString().trim());
			                    Message msg = handler.obtainMessage();
			                    msg.what = 200;
			                    msg.arg1 = ir;
			                    handler.sendMessage(msg);
			                }
		       		   }.start();
	       		   }
	       	   }
	          });
	          
	          mDialog.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface dialog) {
					// TODO Auto-generated method stub
					Start();
				}
			});
	     		
       Button btnRegister = (Button) DialogView.findViewById(R.id.btnregister);
       btnRegister.setVisibility(View.GONE);
  		
       Button btnCancel = (Button) DialogView.findViewById(R.id.btncancel);
       btnCancel.setOnClickListener(new OnClickListener() {
			
    	   @Override
    	   public void onClick(View v) {
    		   mDialog.dismiss();
    	   }
       });
       mDialog.show();
	}//end CreateLoginFayuanDialog;
    
}

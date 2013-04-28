package com.chaoxing.video;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import com.chaoxing.database.SSVideoDatabaseAdapter;
import com.chaoxing.database.SSVideoLocalVideoBean;
import com.chaoxing.database.SSVideoPlayListBean;
import com.chaoxing.document.ParseFaYuanData;
import com.chaoxing.document.SeriesInfo;
import com.chaoxing.download.FileService;
import com.chaoxing.util.UsersUtil;
import com.chaoxing.video.fayuan.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


public class SsvideoPlayerActivity extends Activity  implements  OnBufferingUpdateListener,
						 OnCompletionListener, MediaPlayer.OnPreparedListener, SurfaceHolder.Callback{
	private final String TAG = "videoPlayer";
	private TextView m_video_tv_video_name;
	private TextView m_video_tv_time;
	
	private Button m_video_ib_download;
	private Button m_video_ib_video_detail_info;
	
	private ImageButton m_video_ib_play_pause;
	private ImageButton m_video_ib_volume_switch;
	private ImageView m_video_iv_wait;
	
	private SurfaceView m_video_sv_show_video;
	
	private ProgressBar m_video_pb_wait;
	private SeekBar m_video_sb_process_play;
	private SeekBar m_video_sb_volume;
	
	private MediaPlayer m_mediaPlayer;
	private SurfaceHolder m_holder;
	
	private String m_path;
	private String m_videoName;
	
	private boolean m_volume_on = true;
	private boolean m_isChanging = false;
	
	private int m_videoWidth;
	private int m_videoHeight;
	
	private AudioManager m_audioManager;
	private Timer m_timer;
	private TimerTask m_timerTask;
	
	private int m_maxVolume;
	private int m_curVolume;
	private int m_curVolumeProcess = 50;
	private int m_videoMaxLength;
	private int m_curVideoProcess = 0;
	private int m_currentPlayTime = 0;
	
	private String m_videoTotalTime;
	private String m_videoCurTime;
	
	private int windowWidth;
	private int windowHeight;
	private RelativeLayout relativeLayoutToolBar;
	
	private SSVideoDatabaseAdapter mDBAdapter = null;
	private SsvideoRoboApplication ssvideo;
	private SSVideoPlayListBean m_playListBean = null;
	
	private int m_hideCount = 0;
	private static final int SECONDS_TO_HIDE = 5;
	
	private int videoType; // 0:������Ƶ�� 1��������Ƶ, 2:�ⲿ��Ƶ
	public static final int TYPE_LOCAL = 0;
	public static final int TYPE_ONLINE = 1;
	public static final int TYPE_EXTERNAL = 2;
	
	private int videoCurEpisode;
	private ArrayList<SSVideoPlayListBean> seriesVector;
	private Vector<Button> episodeButtonVector;
	private LinearLayout episodeButtonsLayout;
	
	private boolean isBuffing = false;
	private boolean isToolBarShown = true;
	private boolean isPrepared = false;
	private boolean isExists = false;
	private boolean stoped = false;
	private SeriesInfo seriesInfo = null;
	private String seriesId;
	
    private Intent m_intent = null; 
    private Bundle m_bundle = null;
    private VideoHandler videoHandler;
    private PlayListThread playListThread;
    private Context mContext = SsvideoPlayerActivity.this;
    
    private ParseFaYuanData mFaYuanData;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); 
    	getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN); 
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ssvideo_player);
        //String SDCardRoot =  Environment.getExternalStorageDirectory().getAbsolutePath();
        mFaYuanData =  ParseFaYuanData.getInstance();
        videoHandler = new VideoHandler();
        seriesVector = new ArrayList<SSVideoPlayListBean>();
    
        ssvideo = (SsvideoRoboApplication)getApplication();
        if(mDBAdapter == null) {
        	mDBAdapter = new SSVideoDatabaseAdapter(SsvideoPlayerActivity.this, ssvideo, TAG);
        }
        m_intent = this.getIntent();
        String action = m_intent.getAction();
        
        if(Intent.ACTION_VIEW.equals(action)){
        	videoType = TYPE_EXTERNAL;
        }else{
        	m_bundle = m_intent.getExtras();
        	videoType = m_bundle.getInt("videoType");
        }
        initView();	
        if(videoType == TYPE_ONLINE){
        	getSeriesInfo();
        	//String seriesXMLUrl = "http://www.chaoxing.cc/iphone/getdata.aspx?action=search&padtype=android&page=0&count=214748364&sid=" + seriesId;
        	getPlayList(m_path);
        }else if(videoType == TYPE_LOCAL){
	        // ��ȡҪ������Ƶ����Ϣ
        	m_video_ib_download.setVisibility(ImageButton.GONE);
	        playLocalVideo();
        }else if(videoType == TYPE_EXTERNAL){
        	m_video_ib_download.setVisibility(ImageButton.GONE);
        	playExternalVideo();
        }
       
    }
    
    public void initView() {
    	 m_video_tv_video_name = (TextView)findViewById(R.id.video_tv_video_name);
         m_video_ib_download = (Button)findViewById(R.id.video_ib_download);
         //m_video_ib_download.setVisibility(ImageButton.GONE);
         m_video_ib_video_detail_info = (Button)findViewById(R.id.video_ib_video_detail_info);
         m_video_ib_video_detail_info.setOnClickListener(new ImageButtonOnClickListener());
         
         m_video_ib_play_pause = (ImageButton)findViewById(R.id.video_ib_play_pause);
         m_video_ib_volume_switch = (ImageButton)findViewById(R.id.video_ib_volume_switch);
         Log.i(TAG, "OnCreate ---- Thread id" + Thread.currentThread().getId());
         m_video_sv_show_video = (SurfaceView)findViewById(R.id.video_sv_show_video);
         m_video_pb_wait = (ProgressBar)findViewById(R.id.video_pb_wait);
         m_video_tv_time = (TextView)findViewById(R.id.video_tv_time);
         m_video_sb_process_play = (SeekBar) findViewById(R.id.video_sb_process_play);
         m_video_sb_volume = (SeekBar) findViewById(R.id.video_sb_volume);
         m_video_iv_wait = (ImageView)findViewById(R.id.video_iv_wait);
         episodeButtonsLayout = (LinearLayout)findViewById(R.id.video_linearLayout_episode_buttons);
         
         m_holder = m_video_sv_show_video.getHolder();
         m_holder.addCallback(this);
         m_holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
         m_video_ib_download.setOnClickListener(new ImageButtonOnClickListener());
         
         m_video_ib_play_pause.setOnClickListener(new ImageButtonOnClickListener());
         
         m_video_ib_volume_switch.setOnClickListener(new ImageButtonOnClickListener());
         m_video_ib_volume_switch.setBackgroundResource(R.drawable.video_view_volume_switch);
         m_video_ib_play_pause.setBackgroundResource(R.drawable.video_view_play);
         m_video_ib_play_pause.setEnabled(false);
         m_video_sb_process_play.setOnSeekBarChangeListener(new SeekBarChangeEvent());
         m_video_sb_volume.setOnSeekBarChangeListener(new SeekBarChangeEvent());
         m_video_iv_wait.setImageResource(R.drawable.video_view_loading);
         m_video_tv_time.setText("00:00/00:00");
         relativeLayoutToolBar = (RelativeLayout)findViewById(R.id.video_relativeLayout_toolbar);
    }
    
    @Override
  	protected void onDestroy() {
    	mFaYuanData.setFaYuanFlage(false);
  		super.onDestroy();
  
  	}
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//    	boolean superRes = super.onKeyDown(keyCode, event);
    	if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ){
    		if(m_audioManager != null){
    			m_maxVolume = m_audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    			m_curVolume = m_audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//    			m_audioManager.setStreamVolume(streamType, index, flags)
    			m_curVolume--;
    			m_audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, m_curVolume, 0);
    			m_video_sb_volume.setProgress(m_curVolume * 100 / m_maxVolume);
    			if(m_curVolume == 0) m_video_ib_volume_switch.setBackgroundResource(R.drawable.video_view_volume_switch_mute);
    			else m_video_ib_volume_switch.setBackgroundResource(R.drawable.video_view_volume_switch);

    		}
    	}
    	if(keyCode == KeyEvent.KEYCODE_VOLUME_UP ){
    		if(m_audioManager != null){
    			m_maxVolume = m_audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    			m_curVolume = m_audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//    			m_audioManager.setStreamVolume(streamType, index, flags)
    			m_curVolume++;
    			m_audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, m_curVolume, 0);
    			m_video_sb_volume.setProgress(m_curVolume * 100 / m_maxVolume);
    			if(m_curVolume == 0) m_video_ib_volume_switch.setBackgroundResource(R.drawable.video_view_volume_switch_mute);
    			else m_video_ib_volume_switch.setBackgroundResource(R.drawable.video_view_volume_switch);
    			
    		}
    	}
		return  super.onKeyDown(keyCode, event);
	}
    @Override
	public boolean onTouchEvent(MotionEvent event) {
    	if(event.getAction() == MotionEvent.ACTION_DOWN){
    		if(isToolBarShown == true){
        		m_hideCount = SECONDS_TO_HIDE * 10;
        		m_video_sb_process_play.setVisibility(SeekBar.GONE);
    			relativeLayoutToolBar.setVisibility(RelativeLayout.GONE);
    			if(videoType == TYPE_ONLINE)
    				episodeButtonsLayout.setVisibility(LinearLayout.GONE);
    	        isToolBarShown = false;
        	}else{
        		m_hideCount = 0;
        		relativeLayoutToolBar.setVisibility(RelativeLayout.VISIBLE);
        		m_video_sb_process_play.setVisibility(SeekBar.VISIBLE);
        		if(videoType == TYPE_ONLINE)
        			episodeButtonsLayout.setVisibility(LinearLayout.VISIBLE);
        		isToolBarShown = true;
        	}
    	}
		return super.onTouchEvent(event);
	}
	@Override 
	public void onConfigurationChanged(Configuration newConfig) {	 
		super.onConfigurationChanged(newConfig); 		
		// �����Ļ�ķ����������� 		
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) { 
			//��ǰΪ������ �ڴ˴���Ӷ���Ĵ������ 		
			 setPlayWindow();
		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { 			
			//��ǰΪ������ �ڴ˴���Ӷ���Ĵ������ 		
			setPlayWindow();
		} 	
	}
	
	class VideoHandler extends Handler {
		public static final int PLAY_LIST_OK = 0;
		public static final int PLAY_LIST_ITEM = 1;
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			
			case PLAY_LIST_OK:	
				playOnlineVideo();
				playVideo();
				break;
			case PLAY_LIST_ITEM:
				SSVideoPlayListBean playListBean = (SSVideoPlayListBean) msg.obj;
				getEpisodesInfo(playListBean);
				break;
			}
		}
		
	}
	
	public void getPlayList(final String seriesXMLUrl){
		if(playListThread==null || playListThread.isFinished()){
			playListThread = new PlayListThread(seriesXMLUrl);
			playListThread.start();
		}
	}
	
	class PlayListThread extends Thread {
		private String seriesXMLUrl;
		private boolean finished = false;
		
		public PlayListThread(String seriesXMLUrl) {
			super();
			this.seriesXMLUrl = seriesXMLUrl;
		}

		public void run(){		
			//�ۿ���¼û��ϵ��ID���أ���������������������������ID����
			if (seriesId != null && !seriesId.equals("")) {
				seriesVector = mFaYuanData.getPlayList(Integer.valueOf(seriesId), 0);
			} else {
				seriesVector = mFaYuanData.getPlayList(400000002, 0);
			}
			videoHandler.sendEmptyMessage(VideoHandler.PLAY_LIST_ITEM);
			videoHandler.sendEmptyMessageDelayed(VideoHandler.PLAY_LIST_OK, 3500);			
			
		}
		public boolean isFinished() {
			return finished;
		}

		public void setFinished(boolean finished) {
			this.finished = finished;
		}
	}
	
	private void playOnlineVideo() {
		videoCurEpisode = 1;
		Log.e("wyz", " playOnlineVideo()  temp = "+temp);
		if (temp < 0) {
			return;
		}
		if (seriesVector.size() < temp) {
			Toast.makeText(this, "��ǰ��Դ������", Toast.LENGTH_SHORT).show();
			this.finish();
			return;
		}
	
		episodeButtonVector.get(temp).setBackgroundResource(R.drawable.video_view_episode_selected);
		m_playListBean = seriesVector.get(temp);
		m_path = m_playListBean.getStrVideoRemoteUrl();
		
		if (UsersUtil.getSDKVersionNumber() > 13) {// ������//add_by_xin_2012-12-25.
		// m_path = m_playListBean.getStrM3u8Url();
		}
		if (m_path == null) {
			Toast.makeText(this, "��ǰ��Դ������", Toast.LENGTH_SHORT).show();
			this.finish();
			return;
		}
		m_videoName = m_playListBean.getStrVideoName();
		
	}

	private void getSeriesInfo() {
		// ��ȡϵ����Ϣ
		seriesInfo = (SeriesInfo) m_bundle.getSerializable("SeriesInfo");
		episodeButtonVector = new Vector<Button>();
		if (seriesInfo != null)
			seriesId = seriesInfo.getSerid();
		else {
			SSVideoPlayListBean	tempPlayListBean = (SSVideoPlayListBean) m_bundle.getSerializable("playListBean");
			seriesInfo = new SeriesInfo();
			seriesInfo.setVideoId(Integer.valueOf(tempPlayListBean.getStrVideoId()));
			seriesInfo.setTitle(tempPlayListBean.getStrVideoName());
			m_path = tempPlayListBean.getStrVideoRemoteUrl();

		}
	}
	
	int temp = -1;
	private void getEpisodesInfo( final SSVideoPlayListBean playListBean) {
		Log.e("wyz", TAG+" : getEpisodesInfo() seriesVector.size() = "+seriesVector.size());
		for (int i = 1; i <= seriesVector.size(); i++) {
			//SSVideoPlayListBean playList = seriesVector.get(i);
			Button bt = new Button(SsvideoPlayerActivity.this);
			bt.setBackgroundResource(R.drawable.video_view_episode_default);
			int episode = i;
			temp = i;
			bt.setText(String.format("��%d��",i));
			bt.setTextSize(14);
			bt.setTextColor(Color.WHITE);
			bt.setTag(episode);
			bt.setWidth(110);
			bt.setHeight(65);
			bt.setOnClickListener(new Button.OnClickListener(){
				@Override
				public void onClick(View v) {
					episodeButtonVector.get(videoCurEpisode-1).setBackgroundResource(R.drawable.video_view_episode_default);
					videoCurEpisode = (Integer) v.getTag();
					episodeButtonVector.get(videoCurEpisode-1).setBackgroundResource(R.drawable.video_view_episode_selected);
					m_playListBean = seriesVector.get(videoCurEpisode - 1);
		        	m_path = m_playListBean.getStrVideoRemoteUrl();
		        	m_videoName = m_playListBean.getStrVideoFileName();		
		        	m_playListBean.setStrAbstract(seriesInfo.abstracts);
		        	m_timerTask.cancel();
					m_video_iv_wait.setVisibility(ImageView.VISIBLE);
					m_video_pb_wait.setVisibility(ProgressBar.VISIBLE);
					m_video_tv_time.setText("00:00/00:00");
					m_video_sb_process_play.setProgress(0);
		        	m_mediaPlayer.release();
		        	m_mediaPlayer = null;
		        	playVideo();
		        	mDBAdapter.updataCurrentPlay(seriesId, videoCurEpisode);
				}
			});
			Log.e("wyz", "end   seriesVector.size() " + seriesVector.size());
			episodeButtonsLayout.addView(bt);
			episodeButtonVector.add(bt);		
		}
		/*playListBean.setStrAbstract(seriesInfo.getAbstracts());
		playListBean.setStrScore(seriesInfo.getScore());
		playListBean.setStrScoreCount(seriesInfo.getScoreCount());
		playListBean.setStrCateName(seriesInfo.getSubject());
		String coverUrl = playListBean.getStrRemoteCoverUrl();
		if(coverUrl != null){
			int index = coverUrl.lastIndexOf('.');
			if (index != -1) {
				playListBean.setStrCoverName(playListBean.getStrVideoFileName()+coverUrl.substring(index));
			}
		}
		seriesVector.add(playListBean);*/
		
	}
	
	private void playExternalVideo() {
		m_playListBean = new SSVideoPlayListBean();
		m_playListBean.setnVideoType(2);
		m_path = m_intent.getData().getPath();
		m_path = m_path.replaceAll("file://", "");
		File videoF = new File(m_path);
		if(videoF.exists()){
			isExists = true;
			m_videoName = videoF.getName();
			m_videoName = m_videoName.substring(0, m_videoName.lastIndexOf('.'));
			m_playListBean.setStrVideoFileName(m_videoName);
			m_playListBean.setStrVideoId("" + m_path.hashCode());
			m_playListBean.setStrVideoLocalPath(m_path);
		}
		else {
			Log.d("testVideo", "playExternalVideo-Path:"+m_path);
		}
	}
	private void playLocalVideo() {
		m_playListBean = (SSVideoPlayListBean) m_bundle.getSerializable("playListBean");
		//m_path = m_bundle.getString("videoPath");
		m_path = m_playListBean.getStrVideoLocalPath();
		m_videoName = m_playListBean.getStrVideoName();
		File videoF = new File(m_path);
		if(videoF.exists()){
			isExists = true;
		}
	}
	
    synchronized private void playVideo() {
		if (m_path == null) {
			setPlayCompeted();
			return;
		}
    	add2PlayList();
    	m_video_tv_video_name.setText(m_videoName);
       
    	try {
      		Log.i(TAG, "file =" + m_path);
    		m_mediaPlayer = new MediaPlayer();
    		m_mediaPlayer.reset();
    		// ��ȡϵͳ������Ϣ
			m_audioManager = (AudioManager) SsvideoPlayerActivity.this.getSystemService(Context.AUDIO_SERVICE);
			m_maxVolume = m_audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			m_curVolume = m_audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			m_video_sb_volume.setProgress(m_curVolume * 100 / m_maxVolume);
    		m_mediaPlayer.setDataSource(m_path);
    		m_mediaPlayer.setDisplay(m_holder);
    		Log.i(TAG, "before prepare  ---- Thread id " + Thread.currentThread().getId());
    		this.isPrepared = false;
    		m_mediaPlayer.prepareAsync();
    		m_mediaPlayer.setOnBufferingUpdateListener(this);
			m_mediaPlayer.setOnCompletionListener(this);
			m_mediaPlayer.setOnPreparedListener(this);
			
			m_mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// ���ö�ʱ�����������ò�����Ƶʱ������Ƶ��������λ��
        m_timer = new Timer();
        m_timerTask = new PlayTimerTask();
        m_timer.schedule(m_timerTask, 0, 100);
    }

	private void add2PlayList() {
		if(m_playListBean == null)
			return ;
		Cursor cursor = null;
        try {
			cursor = mDBAdapter.fetchDataInPlayListById(m_playListBean.getStrVideoId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
        if(cursor.moveToFirst()){
        	m_currentPlayTime = cursor.getInt(cursor.getColumnIndex("current_play_time"));
        }else{
        	mDBAdapter.insertIntoPlayList(m_playListBean);
        }
        cursor.close();
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if(videoType == TYPE_LOCAL) {
			playVideo();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		super.onDestroy();
	}

	private void stopPlayer() {
		if(m_timer != null)
			m_timer.cancel();
		if(m_mediaPlayer != null){
			m_playListBean.setnCurrentPlayTime(m_curVideoProcess);
			mDBAdapter.updateDataInPlayList(m_playListBean.getStrVideoId(), m_playListBean);
			m_mediaPlayer.release();
			m_mediaPlayer = null;
		}
		if(playListThread != null)
			playListThread.setFinished(true);
		stoped = true;
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		this.isPrepared = true;
		m_videoWidth = mp.getVideoWidth();
		m_videoHeight = mp.getVideoHeight();
		if(m_videoWidth != 0 && m_videoHeight != 0){
			// ��ò�������Ƶ����󳤶�
			m_videoMaxLength = m_mediaPlayer.getDuration();
			m_video_sb_process_play.setMax(m_videoMaxLength);
			
			String temp = int2TimeStr(m_videoMaxLength);
			if(temp != null)
				m_videoTotalTime =temp;
			
			setPlayWindow();
	       
			m_video_iv_wait.setVisibility(ImageView.GONE);
			m_video_pb_wait.setVisibility(ProgressBar.GONE);
			m_video_ib_play_pause.setBackgroundResource(R.drawable.video_view_pause);
			m_video_ib_play_pause.setEnabled(true);
			// ��ת����ǰ����λ��
			m_video_sb_process_play.setProgress(m_currentPlayTime);
			mp.seekTo(m_currentPlayTime);
			mp.start();
		}
	}

	private String int2TimeStr(int intLen) {
		int seconds, minutes;
		minutes = intLen / 60000;
		seconds = (intLen % 60000) / 1000;
		String temp = String.format("%02d:%02d", minutes, seconds);
		return temp;
	}

	private void setPlayWindow() {
		// ��ȡ��Ļ��С
		DisplayMetrics dm = new DisplayMetrics(); 
		dm = SsvideoPlayerActivity.this.getApplicationContext().getResources().getDisplayMetrics(); 
		windowWidth = dm.widthPixels;
		windowHeight = dm.heightPixels;

		LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams)m_video_sv_show_video.getLayoutParams();
		linearParams.width = windowWidth;
		linearParams.height =  (int)(((double)windowWidth) / (double)m_videoWidth * m_videoHeight );
		if(linearParams.height > windowHeight){
			linearParams.height = windowHeight;
			linearParams.width = (int)(((double)windowHeight) / (double)m_videoHeight * m_videoWidth);
		}
		m_video_sv_show_video.setLayoutParams(linearParams);
		m_holder.setFixedSize(linearParams.width, linearParams.height);
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		setPlayCompeted();
	}

	private void setPlayCompeted() {
		m_video_ib_play_pause.setBackgroundResource(R.drawable.video_view_play);
		if(this.isPrepared == false && !stoped){
			if(videoType == TYPE_ONLINE){
				Toast.makeText(SsvideoPlayerActivity.this, "��Ƶ����ʧ��", Toast.LENGTH_SHORT).show();
			} else if(isExists == false){
				Toast.makeText(SsvideoPlayerActivity.this, "����Ƶ������", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(SsvideoPlayerActivity.this, "����Ƶ�޷�����", Toast.LENGTH_SHORT).show();
			}
		}
		if(videoType == TYPE_ONLINE){
			if( videoCurEpisode+1 <= seriesVector.size()){
				episodeButtonVector.get(videoCurEpisode-1).setBackgroundResource(R.drawable.video_view_episode_default);
				videoCurEpisode++;
				episodeButtonVector.get(videoCurEpisode-1).setBackgroundResource(R.drawable.video_view_episode_selected);
				m_playListBean = seriesVector.get(videoCurEpisode - 1);
	        	m_path = m_playListBean.getStrVideoRemoteUrl();
	        	m_videoName = m_playListBean.getStrVideoFileName();	
	        	if(m_timerTask != null)
	        		m_timerTask.cancel();
				m_video_iv_wait.setVisibility(ImageView.VISIBLE);
				m_video_pb_wait.setVisibility(ProgressBar.VISIBLE);
				if(m_mediaPlayer != null){
					m_mediaPlayer.release();
					m_mediaPlayer = null;
				}
	        	playVideo();
	        	mDBAdapter.updataCurrentPlay(seriesId, videoCurEpisode);
			} else {
				finish();
			}
		} else {
			finish();
		}
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		int max = m_video_sb_process_play.getMax();
		int cur = m_video_sb_process_play.getProgress();
		if((percent - (cur * 100 / max)) < 1){
			// ����ʱ�ȴ�
			if(mp.isPlaying() == true){
				m_video_ib_play_pause.setBackgroundResource(R.drawable.video_view_play);
				mp.pause();
				isBuffing = true;
			}
			m_video_pb_wait.setVisibility(ProgressBar.VISIBLE);
		}else{
			if(mp.isPlaying() == false && isBuffing == true){
				m_video_ib_play_pause.setBackgroundResource(R.drawable.video_view_pause);
				mp.start();
				isBuffing = false;
			}
			m_video_pb_wait.setVisibility(ProgressBar.GONE);
		}
	}
	
	class ImageButtonOnClickListener implements ImageButton.OnClickListener{
		
		@Override
		public void onClick(View v) {
			int id = v.getId();
			if (id == R.id.video_ib_play_pause) {
				if(m_mediaPlayer.isPlaying()){
					m_video_ib_play_pause.setBackgroundResource(R.drawable.video_view_play);
					m_mediaPlayer.pause();
					getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				}else{
					m_video_ib_play_pause.setBackgroundResource(R.drawable.video_view_pause);
					m_mediaPlayer.start();
					getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				}
			} else if (id == R.id.video_ib_volume_switch) {
				m_volume_on = !m_volume_on;
				if(m_volume_on){
					m_video_ib_volume_switch.setBackgroundResource(R.drawable.video_view_volume_switch);
					m_audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, m_maxVolume * m_curVolumeProcess / 100, 0);
				}else{
					m_video_ib_volume_switch.setBackgroundResource(R.drawable.video_view_volume_switch_mute);
		   			m_audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
				}
			} else if (id == R.id.video_ib_download) {
				//FIXME
				if (mFaYuanData.isFaYuanFlage()) {
					downloadFile(m_path);
				} else {
					Toast.makeText(mContext, "��¼֮������������ص�����", 0).show();
				}
				
			} else if (id == R.id.video_ib_video_detail_info) {
				showDetailInfo();
			} else {
			}
		}//onClick
		private void downloadFile(String m_path) {
			try {
				SSVideoLocalVideoBean localVideoBean1 = mDBAdapter.fetchDataInLocalVideoById(m_playListBean.getStrVideoId());
				if(localVideoBean1 != null){
					Toast.makeText(SsvideoPlayerActivity.this, "����Ƶ�Ѿ�����", Toast.LENGTH_LONG).show();
				}else{
			
					Intent serviceIntent = new Intent(mContext,FileService.class);
					Bundle bundle = new Bundle();
					bundle.putInt("op", 0);
					//SSVideoLocalVideoBean localVideoBean = new SSVideoLocalVideoBean(m_playListBean);
					SSVideoLocalVideoBean localVideoBean = new SSVideoLocalVideoBean();
					bundle.putSerializable("localVideoBean", localVideoBean);
					serviceIntent.putExtras(bundle);
					startService(serviceIntent);
					Toast.makeText(SsvideoPlayerActivity.this, "��ʼ�������ط�����", Toast.LENGTH_LONG).show();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		private void showDetailInfo() {
			LayoutInflater factory = LayoutInflater.from(SsvideoPlayerActivity.this);
	    	final View DialogView = factory.inflate(R.layout.detail_info_dialog, null);
	    	final TextView tvDetailTitle =  (TextView) DialogView.findViewById(R.id.dialog_detail_title);
	    	final TextView tvDetailSbject=  (TextView) DialogView.findViewById(R.id.dialog_detail_subject);
	    	final TextView tvDetailScore =  (TextView) DialogView.findViewById(R.id.dialog_detail_score);
	    	final TextView tvDetailScoreCount =  (TextView) DialogView.findViewById(R.id.dialog_detail_score_count);
	    	final TextView tvDetailSpeaker =  (TextView) DialogView.findViewById(R.id.dialog_detail_speaker);
	    	final TextView tvDetailAbstract =  (TextView) DialogView.findViewById(R.id.dialog_detail_abstract);
	    
	    	if (!seriesInfo.getTitle().equals("")) {
	    		tvDetailTitle.setText("��Ƶ���ƣ� "+seriesInfo.getTitle());
	    	} 
	    	if (!seriesInfo.getSubject().equals("")) {
	    		tvDetailSbject.setText("�������ƣ� "+seriesInfo.getSubject());
	    	}
	    	if (!seriesInfo.getKeySpeaker().equals("")) {
	    		tvDetailSpeaker.setText("�����ˣ� "+seriesInfo.getKeySpeaker());
	    	}   	
	    	tvDetailAbstract.setText(seriesInfo.getAbstracts());
	    	if (!seriesInfo.getScore().equals("") ) {
	    		double score = Double.parseDouble(seriesInfo.getScore());
	    		tvDetailScore.setText("���֣�" + String.format("%.2f", score));
	    	}
	    	
	    	
	    	/*if(m_playListBean.getStrVideoFileName() != null)
	    		tvDetailTitle.setText(m_playListBean.getStrVideoFileName());
	    	if(m_playListBean.getStrCateId() != null){
	    		SSVideoCategoryBean cateBean = mDBAdapter.fetchCategoryById(m_playListBean.getStrCateId());
	    		if(cateBean != null)
	    			tvDetailSbject.setText("���ࣺ" + cateBean.getStrCateName());
	    		else tvDetailSbject.setVisibility(View.GONE);
	    	}
	    	if(m_playListBean.getStrVideoName() != null)
	    		tvDetailTitle.setText(m_playListBean.getStrVideoName());
	    	else tvDetailTitle.setVisibility(View.GONE);
	    	if(m_playListBean.getStrScore() != null && m_playListBean.getStrScore().equals("")==false){
	    		double score = Double.parseDouble(m_playListBean.getStrScore());
	    		tvDetailScore.setText("���֣�" + String.format("%.2f", score));
	    	}
	    	else
	    	if(m_playListBean.getStrScoreCount() != null && m_playListBean.getStrScoreCount().equals("")==false)
	    		tvDetailScoreCount.setText("���ִ�����" + m_playListBean.getStrScoreCount());
	    	else tvDetailScoreCount.setVisibility(View.GONE);
	    	if(m_playListBean.getStrSpeaker() != null)
	    		tvDetailSpeaker.setText("�����ˣ�" + m_playListBean.getStrSpeaker());
	    	else tvDetailSpeaker.setVisibility(View.GONE);
	    	if(m_playListBean.getStrAbstract() != null)
	    		tvDetailAbstract.setText(m_playListBean.getStrAbstract());
	    	else tvDetailAbstract.setVisibility(View.GONE);*/
	    	
	    	PopupWindow detailPopupWindow = new PopupWindow(DialogView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
	    	ColorDrawable dw = new ColorDrawable(686869);
			detailPopupWindow.setBackgroundDrawable(dw);
	    	detailPopupWindow.showAsDropDown(m_video_ib_video_detail_info);
		}
	}// class ImageButtonOnClickListener
	
	class PlayTimerTask extends TimerTask{
		@Override
		public void run() {
			// ��������������ʱ�ޣ����ؿ�����
			if(m_isChanging == true){
				return ;
			}
			if(m_hideCount < SECONDS_TO_HIDE * 10)
				m_hideCount++;
			if(m_mediaPlayer != null){
				if(m_mediaPlayer.isPlaying()){
					m_curVideoProcess = m_mediaPlayer.getCurrentPosition();
					m_video_sb_process_play.setProgress(m_curVideoProcess);
				}
			}
		}        	
    }
    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener
    {
    	@Override
    	public void onProgressChanged(SeekBar seekBar, int progress,
    			boolean fromUser) {
    		int id = seekBar.getId();
			if (id == R.id.video_sb_process_play) {
				m_curVideoProcess = progress;
				m_videoCurTime = int2TimeStr(m_curVideoProcess);
				if(m_videoCurTime != null && m_videoTotalTime != null){
					String time = m_videoCurTime + "/" + m_videoTotalTime;
					m_video_tv_time.setText(time);
				}
				if(m_hideCount >= SECONDS_TO_HIDE  * 10 && isToolBarShown == true){
					m_video_sb_process_play.setVisibility(SeekBar.GONE);
					relativeLayoutToolBar.setVisibility(RelativeLayout.GONE);
					if(videoType == TYPE_ONLINE)
						episodeButtonsLayout.setVisibility(LinearLayout.GONE);
				}
			} else if (id == R.id.video_sb_volume) {
				m_curVolumeProcess = progress;
			}
    	}
    	
    	@Override
    	public void onStartTrackingTouch(SeekBar seekBar) {
    		m_isChanging = true;
    	}
    	
    	@Override
    	public void onStopTrackingTouch(SeekBar seekBar) {
    		int id = seekBar.getId();
			if (id == R.id.video_sb_process_play) {
				if (m_mediaPlayer != null && seekBar != null) {
					m_mediaPlayer.seekTo(seekBar.getProgress());
				}
			} else if (id == R.id.video_sb_volume) {
				m_curVolumeProcess = seekBar.getProgress();
				if(m_audioManager !=null) {
					m_audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, m_maxVolume * m_curVolumeProcess / 100, 0);
					if(m_curVolumeProcess == 0) m_video_ib_volume_switch.setBackgroundResource(R.drawable.video_view_volume_switch_mute);
	    			else m_video_ib_volume_switch.setBackgroundResource(R.drawable.video_view_volume_switch);
				}
			}
    		m_isChanging = false;
    	}// onStopTrackingTouch
    	
    }// SeekBarChangeEnent
    

	@Override
	public void onBackPressed() {
		stopPlayer();
		super.onBackPressed();
	}
}
package com.chaoxing.video;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Video;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.chaoxing.adapter.CategoryVideoAdapter;
import com.chaoxing.adapter.GalleryVideoAdapter;
import com.chaoxing.adapter.LocalVideoListAdapter;
import com.chaoxing.adapter.PageVideoAdapter;
import com.chaoxing.database.CategoryLocalDbAdapter;
import com.chaoxing.database.SSVideoCategoryBean;
import com.chaoxing.database.SSVideoDatabaseAdapter;
import com.chaoxing.database.SSVideoLocalVideoBean;
import com.chaoxing.database.SSVideoPlayListBean;
import com.chaoxing.document.CategoryNameInfo;
import com.chaoxing.document.Global;
import com.chaoxing.document.ParseFaYuanData;
import com.chaoxing.document.SeriesInfo;
import com.chaoxing.httpservice.HttpRequest;
import com.chaoxing.parser.ParserPageSeriesJson;
import com.chaoxing.parser.XmlParserFactory;
import com.chaoxing.util.AlertDialogUtil;
import com.chaoxing.util.AnimationControl;
import com.chaoxing.util.AppConfig;
import com.chaoxing.util.BitmapUtil;
import com.chaoxing.util.StringUtil;
import com.chaoxing.util.ToolsLog;
import com.chaoxing.util.UsersUtil;
import com.chaoxing.util.WidgetUtil;
import com.chaoxing.video.fayuan.R;
import com.chaoxing.video.fayuan.R.string;

/**
 * @author create by xin_yueguang.
 * @version 2011-8-10.
 */
public class SsvideoActivity extends Activity implements TabHost.OnSelectedListener, PageButtonHost.OnPageSelectedListener{
	private static final String TAG = SsvideoActivity.class.getSimpleName();
	
	private final int TIMER_SCROLL_GALLERY_DURATION = 6000;
	private static final int INDEX_BEST_VIEW     = 0;
	private static final int INDEX_CATEGORY_VIEW = 1;
	private static final int INDEX_LOCAL_VIEW    = 2;
	private static final int INDEX_SEARCH_VIEW   = 3;
	private static final int INDEX_REMEN_VIEW    = 4;
	private static final int INDEX_DASHI_VIEW    = 5;
	private static final int INDEX_ZHEXUE_VIEW   = 6;
	private static final int INDEX_JINGJI_VIEW   = 7;
	private static final int INDEX_LISHI_VIEW    = 8;
	private static final int INDEX_WENXUE_VIEW   = 9;
	private static final int INDEX_GALLERY_VIEW  = 10;
	private static final int INDEX_GALLERY_IMAGE = 11;
	private static final int INDEX_CATEGORY_IMAGE= 12;
	private static final int INDEX_SEARCH_IMAGE  = 13;
	private static final int INDEX_DATA_COUNT    = 14;
	private static final int INDEX_DATA_CATEGORY = 15;
	private static final int INDEX_DATA_SEARCH   = 16;
	private static final int INDEX_DATA_ADDMORE  = 17;
	private static final int INDEX_DATA_ADDNEW   = 18;
	private static final int INDEX_CATEGORY_NAME = 19;
	private static final int INDEX_CATEGORY_FIRST= 20;
	private static final int INDEX_CATEGORY_SECOND=21;
	private static final int INDEX_CATEGORY_THIRD= 22;
	
//	private static final int VIEW_LOADING_VISIBLE = 23;
//	private static final int VIEW_LOADING_GONE    = 24;
	private static final int VIEW_LOADING_STOP    = 25;
	private static final int VIEW_LOADING_START   = 26;
	private static final int VIEW_DATA_RELOAD     = 27;
	private static final int RECENT_PLAY_HANLDER  = 28;
	private static final int ORIENTATION_PORTRAIT = 1;//���� 
	private static final int ORIENTATION_LANDSCAPE= 2;//���� 
	
	private static final int INDEX_SCREEN_PARAMS = 23;
	
	private static final int INDEX_ONLINE_VIDEO = 24;
	
	private static final int INDEX_DEFAULT_VIEW = INDEX_BEST_VIEW;//INDEX_BEST_VIEW, INDEX_ONLINE_VIDEO//
	
//	��ȡ��ǰ������״̬  -1��û������  1��WIFI����2��wap����3��net����
	private static final int NET_TYPE_NULL  = -1;
	private static final int NET_TYPE_CMWAP = 0;
	private static final int NET_TYPE_CMNET = 1;
	private static final int NET_TYPE_WIFI  = 2;
//	private static final int NET_TYPE_3G    = 3;	
	private static final int ALERT_NO_NETWORK = 1;
	private static final int ALERT_REPEAT_NETWORK = 2;
	
	private static final int DATA_FROM_LOCAL   = 100;
	private static final int DATA_FROM_NETWORK = 101;
	
	private Button btnLogin;
	private Button btnRecentplay;
	private Button btnSearch;
	private Button btnDetailPlay;
	private InputMethodManager imm;
	private VideoSearchText searchText;
	
	private Button btnDelete;
	private Button btnSelectAll;
	private Button btnDone;
	private Button btnEdit;
	private Button btnRefresh;
	private Button btnCategory;
	
	private GridView gdvShowLocalVideos;
	private List<Map<String, Object>> listLocalVideo;
	private LocalVideoListAdapter adapter ;

	private TabHost tabHost;
	private TabButton tabOnline;
	private TabButton tabBtnBest;
	private TabButton tabLocal;
	private TabButton tabCate;
	private PageButtonHost pageBtnHost;
	private ViewFlipper viewFlipHost;
	private ViewFlipper viewFlipPageHost;
	//private Handler pageViewHandler = null;
	
	private Gallery mygallery;
	public Timer timerScrollGallery;
	private TextView txtDetailBackGround;
	private TextView txtDetail;
	private TextView txtLogo;
	
	private ImageView imgLogo;
	
	private Vector<View> categoryNameVectorView1 = new Vector<View>();
	private Vector<View> categoryNameVectorView2 = new Vector<View>();
	private Vector<View> categoryNameVectorView3 = new Vector<View>();
	
	private int lastSelectedImg1 = 0;
	private int lastSelectedImg2 = 0;
	private int lastSelectedImg3 = 0;
	private int screenWidth = 0;
	private int screenHeight= 0;
	
	private LinearLayout layoutCateScrollBar1;
	private LinearLayout layoutCateScrollBar2;
	private LinearLayout layoutCateScrollBar3;
	private LinearLayout viewLoadingGallery;
	private LinearLayout viewLoadingReMen;
	private LinearLayout viewLoadingDaShi;
	private LinearLayout viewLoadingZheXue;
	private LinearLayout viewLoadingJingJi;
	private LinearLayout viewLoadingLiShi;
	private LinearLayout viewLoadingWenXue;
	private LinearLayout viewLoadingCategory;
	private LinearLayout viewLoadingSearch;
	private LinearLayout viewLoadingLocal;
	private LinearLayout llLocalVideoCategory;
	
	private GalleryVideoAdapter gvAdapter;
	private PageVideoAdapter pvAdapterXianFa;
	private PageVideoAdapter pvAdapterXingFa;
	private PageVideoAdapter pvAdapterMinShang;
	private PageVideoAdapter pvAdapterJingJi;
	private PageVideoAdapter pvAdapterSuSong;
	private PageVideoAdapter pvAdapterWenXue;
	private CategoryVideoAdapter pvAdapterCategory;
	private CategoryVideoAdapter pvAdapterSearch;
	
	private GridView GridViewXianFa;
	private GridView GridViewXingFa;
	private GridView GridViewMinShang;
	private GridView GridViewJingJi;
	private GridView GridViewSuSong;
	private GridView GridViewWenXue;
	private GridView GridViewCategory;
	private GridView GridViewSearch;
	
	private ArrayList<SeriesInfo> serListXianFa;
	private ArrayList<SeriesInfo> serListXingFa;
	private ArrayList<SeriesInfo> serListMinShang;
	private ArrayList<SeriesInfo> serListJingJi;
	private ArrayList<SeriesInfo> serListSuSong;
	private ArrayList<SeriesInfo> serListWenXue;
	private ArrayList<SeriesInfo> serListTop;
	
	private ArrayList<SeriesInfo> serListCateAll;
	private ArrayList<SeriesInfo> serListSearch;
	private ArrayList<CategoryNameInfo> cateNameListFirst;
	private ArrayList<CategoryNameInfo> cateNameListSecond;
	private ArrayList<CategoryNameInfo> cateNameListThird;
	
	private String SScoversPath;
	private SSVideoDatabaseAdapter mDBAdapter = null;
	private SsvideoRoboApplication ssvideo;
	private String classify;
	private boolean isFirstDisplayLocalVideo = true;
	private boolean mStop = false;
	private boolean mBackIME = false;

	//private List<Map<String, Object>> recentPlayList = null;
	private Set<Integer> selectedItemsSet;
	private ButtonClickListener listener;
	private PopupWindow categoryPopupWindow;
	
	private DownloadBroadcastReceiver receiver = null;
	private IntentFilter filter ;
	private Vector<Map<String, Object>> downloadingVector;
	
	private String sCurrentSearchKey;
	
	private int mResponse = -100;
	private Dialog mDialog = null;
	private TextView tvErrorMsg ;
    private ProgressBar proBarLogin;
    private CoverHandler coverHandler;
    private ParseFaYuanData mFaYuanData;
//	private final Handler handlerScroll = new Handler();
	
	private CategoryLocalDbAdapter mCateLocalDBAdapter = new CategoryLocalDbAdapter(this);
	private int videoType;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.main);
//        Log.d(TAG, "��ʼ������ҳ��");
        getScreenParams();
        injectViews();
        initMember();
        mFaYuanData = ParseFaYuanData.getInstance();
//        Log.d(TAG, "��ʼ���ض���6���Ƽ�");
        //String urlBestTop6 = getResources().getString(R.string.url_top6_recommend);
		getBestTopInfoView();
		
        gvAdapter = new GalleryVideoAdapter(this);
        mygallery.setAdapter(gvAdapter);
        
        boolean isDefaultView = true;
        
        // ���������С������ת��ֱ�ӽ��뱾����Ƶ�����������
		Intent intent = this.getIntent();
		if (intent != null) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				videoType = bundle.getInt("videoType");
				if (videoType == 0) {// ���뱾����Ƶ
					isDefaultView = false;
					setDefaultView(INDEX_DEFAULT_VIEW, isDefaultView);
				} else if (videoType == 1) {// ����������
					isDefaultView = false;
					tabCate.selected = true;
					tabHost.onFinishInflate();
					onSelected(INDEX_CATEGORY_VIEW);
				} else if (videoType == 2) {// ����������
					isDefaultView = false;
					setDefaultView(INDEX_CATEGORY_VIEW, isDefaultView);
				}
			}
		}

        if(isDefaultView) {
        	setDefaultView(INDEX_DEFAULT_VIEW, isDefaultView);
        }
        
        mygallery.setOnItemClickListener(new OnItemClickListener()
        {
           @Override
           public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
           {
				if (serListTop != null) {
					int idx = (arg2) % serListTop.size();
					SeriesInfo info = serListTop.get(idx);
					String sAbstract = info.getAbstracts();
					try {
						sAbstract = getPartString(info.getAbstracts(), 378);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					txtDetail.setText(info.getTitle() + "\r\n\r\n" + sAbstract);
				}
			}
        });
        
        btnDetailPlay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int idx = gvAdapter.getItemIndex();
				if((idx >= 0) && (serListTop != null) && (idx < serListTop.size())) {
					SeriesInfo info = serListTop.get(idx);
	  				goPlayMovie(info);
				}
				
			}
		});
   
//        Log.d(TAG, "��ʼ�����Ƽ���6������");
        //String urlBest = getResources().getString(R.string.url_best_recommend);
        getPageViewData();
        
        pvAdapterXianFa = new PageVideoAdapter(this);
        //GridViewReMen.setAdapter(pvAdapterReMen);
        GridViewXianFa.setAdapter(pvAdapterXianFa);
  	  	//�¼�����
		GridViewXianFa.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				if (serListXianFa != null) {
					SeriesInfo info = serListXianFa.get(position);
					goPlayMovie(info);
				}
				// Toast.makeText(SsvideoActivity.this, "��ѡ���ˡ����š�" + (position +	// 1) + " ����Ƶ", Toast.LENGTH_SHORT).show();
			}
		});
        
//        Log.d(TAG, "��ʼ���ش�ʦҳ��");
  	  	//-----------------
        
        pvAdapterXingFa = new PageVideoAdapter(this);
        GridViewXingFa.setAdapter(pvAdapterXingFa);
	  	//�¼�����
		GridViewXingFa.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				if (serListXingFa != null) {
					SeriesInfo info = serListXingFa.get(position);
					goPlayMovie(info);
				}
				// Toast.makeText(SsvideoActivity.this, "��ѡ���ˡ���ʦ��ɡ�" + (position
				// + 1) + " ����Ƶ", Toast.LENGTH_SHORT).show();
			}
		});
	  	//-------------------*/
//	  	Log.d(TAG, "��ʼ������ѧҳ��");
	  	
	  	pvAdapterMinShang = new PageVideoAdapter(this);
	  	GridViewMinShang.setAdapter(pvAdapterMinShang);
	  	//�¼�����
		GridViewMinShang.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				if (serListMinShang != null) {
					SeriesInfo info = serListMinShang.get(position);
					goPlayMovie(info);
				}
				// Toast.makeText(SsvideoActivity.this, "��ѡ���ˡ���ѧ��" + (position +		// 1) + " ����Ƶ", Toast.LENGTH_SHORT).show();
			}
		});
	  	//-------------------*/
//	  	Log.d(TAG, "��ʼ���ؾ���ѧҳ��");
	  	
	  	pvAdapterJingJi = new PageVideoAdapter(this);
	  	GridViewJingJi.setAdapter(pvAdapterJingJi);
	  	//�¼�����
		GridViewJingJi.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				if (serListJingJi != null) {
					SeriesInfo info = serListJingJi.get(position);
					goPlayMovie(info);
				}
				// Toast.makeText(SsvideoActivity.this, "��ѡ���ˡ�����ѧ��" + (position	// + 1) + " ����Ƶ", Toast.LENGTH_SHORT).show();
			}
		});
  	  	//-------------------*/
	  	
	  	pvAdapterSuSong = new PageVideoAdapter(this);
	  	GridViewSuSong.setAdapter(pvAdapterSuSong);
	  	//�¼�����
        GridViewSuSong.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id)
			{
				if(serListSuSong != null)
  				{
  				    SeriesInfo info = serListSuSong.get(position);
	  				goPlayMovie(info);
  				}    
//				Toast.makeText(SsvideoActivity.this, "��ѡ���ˡ���ʷѧ��" + (position + 1) + " ����Ƶ", Toast.LENGTH_SHORT).show();
			}
		});
  	  	//-------------------*/
        
        pvAdapterWenXue = new PageVideoAdapter(this);
        GridViewWenXue.setAdapter(pvAdapterWenXue);
	  	//�¼�����
        GridViewWenXue.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id)
			{
				if(serListWenXue != null)
  				{
  				    SeriesInfo info = serListWenXue.get(position);
	  				goPlayMovie(info);
  				}    
//				Toast.makeText(SsvideoActivity.this, "��ѡ���ˡ���ѧ��" + (position + 1) + " ����Ƶ", Toast.LENGTH_SHORT).show();
			}
		});
  	  	//-------------------*/
//        Log.d(TAG, "��ʼ���ط���ҳ��");
        //String sUrlCateAll = "http://www.chaoxing.cc/ipad/getdata.aspx?action=hot&page=1&count=20&u=&type=2";
        
  	  	pvAdapterCategory = new CategoryVideoAdapter(this);
  	  	pvAdapterCategory.setHandler(coverHandler);
  	  	GridViewCategory.setAdapter(pvAdapterCategory);
  	  	//�¼�����
		GridViewCategory.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				if (serListCateAll != null) {
					int iSize = serListCateAll.size();
					if (position != iSize) {// ��ȥmore��ť
						boolean blState = getNetIsConnected();
						if (!blState) {
							Message msgTopData = handlerBest.obtainMessage();
							msgTopData.what = 0;
							msgTopData.arg1 = 1;
							msgTopData.obj = "δ�������磡";
							handlerBest.sendMessage(msgTopData);
							return;
						}
						SeriesInfo info = serListCateAll.get(position);
						goPlayMovie(info);
					} else {
						// Toast.makeText(SsvideoActivity.this, "��ѡ���ˡ���ȡ������Ƶ�� ��"
						// + (position + 1) + "��", Toast.LENGTH_SHORT).show();
						int iPage = v.getId();
						if (iPage == -1) {
							Toast.makeText(SsvideoActivity.this, "�ѷ������һҳ", Toast.LENGTH_SHORT).show();
							return;
						}
						sendStartLoadMessage(INDEX_CATEGORY_VIEW);// startLoadingViewThread(INDEX_CATEGORY_VIEW);
						String sUrlCatePage = "http://www.chaoxing.cc/ipad/getdata.aspx?action=hot&padtype=android&count=20&u=&type=2&page=" + iPage;

						boolean blState = getNetIsConnected();
						if (!blState) {
							int idx = iPage * 10 + 1;
							getCategoryDataFromLocal("", idx, INDEX_DATA_ADDMORE);
						} else {
							sUrlCatePage = "0";
							getCategoryData(sUrlCatePage, INDEX_DATA_ADDMORE);
						}
					}
				}
				// Toast.makeText(SsvideoActivity.this, "��ѡ���ˡ����������" + (position
				// + 1) + " ����Ƶ", Toast.LENGTH_SHORT).show();
			}
		});
//  	  	Log.d(TAG, "��ʼ���ؼ���ҳ��");
  	  	
	  	pvAdapterSearch = new CategoryVideoAdapter(this);
	  	pvAdapterSearch.setHandler(coverHandler);
	  	GridViewSearch.setAdapter(pvAdapterSearch);
	  	//�¼�����
		GridViewSearch.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,	int position, long id) {
				int iSize = serListSearch.size();
				if (position != iSize) {// ��ȥmore��ť
					SeriesInfo info = serListSearch.get(position);
					goPlayMovie(info);
				} else {
					// Toast.makeText(SsvideoActivity.this, "��ѡ���ˡ�������Ƶ�� ��" +
					// (position + 1) + "��", Toast.LENGTH_SHORT).show();
					int iPage = v.getId();
					if (iPage == -1) {
						Toast.makeText(SsvideoActivity.this, "�ѷ������һҳ", Toast.LENGTH_SHORT).show();
						return;
					}
					sendStartLoadMessage(INDEX_SEARCH_VIEW);// startLoadingViewThread(INDEX_SEARCH_VIEW);
					// getSearchData(sCurrentSearchKey, INDEX_DATA_ADDMORE,
					// iPage, false);
					boolean blState = getNetIsConnected();
					if (!blState) {
						// int idx = iPage*10+1;
						// getCategoryDataFromLocal("", idx,
						// INDEX_DATA_ADDMORE);
						// getSearchDataLocal(sCurrentSearchKey,
						// INDEX_DATA_ADDMORE, false);
						stopLodingView(INDEX_SEARCH_VIEW);
					} else {
						getSearchData(sCurrentSearchKey, INDEX_DATA_ADDMORE,
								iPage, false);
					}
				}
			}
		});
//	  	Log.d(TAG, "��ʼ���ؼ�������");
	  	// ������ע��BroadcaseReceiver
	  	receiver = new DownloadBroadcastReceiver();
		filter = new IntentFilter();
		filter.addAction("com.chaoxing.download.DownloadBroadcastReceiver");
		registerReceiver(receiver, filter);
	
		checkVersion();
		
    }//end onCreate;
    private static int ID_FALGE = -1;
	private void setDefaultView(int viewType, boolean isDefault) {

		if(viewType == INDEX_BEST_VIEW && isDefault) {
			tabBtnBest.setVisibility(View.VISIBLE);
			tabCate.setVisibility(View.VISIBLE);
			tabOnline.setVisibility(View.GONE);
			tabCate.selected = false;
			tabBtnBest.selected = true;
			tabHost.onFinishInflate();
			onSelected(INDEX_BEST_VIEW);
		}
		else if(viewType == INDEX_ONLINE_VIDEO){
			tabBtnBest.setVisibility(View.GONE);
			tabCate.setVisibility(View.GONE);
			tabOnline.setVisibility(View.VISIBLE);
			tabLocal.selected = true;
			tabHost.onFinishInflate();
			onSelected(INDEX_LOCAL_VIEW);
		} else if(viewType == INDEX_CATEGORY_VIEW) {
			tabBtnBest.setVisibility(View.VISIBLE);
			tabCate.setVisibility(View.VISIBLE);
			tabOnline.setVisibility(View.GONE);
			tabBtnBest.selected = false;
			tabCate.selected = true;
			tabHost.onFinishInflate();
			onSelected(INDEX_CATEGORY_VIEW);
		}
		else {
			tabBtnBest.setVisibility(View.VISIBLE);
			tabCate.setVisibility(View.VISIBLE);
			tabOnline.setVisibility(View.GONE);
			tabLocal.selected = true;
			tabHost.onFinishInflate();
			onSelected(INDEX_LOCAL_VIEW);
		}
	}
	
	private void injectViews() {
		initLoadingView();
		btnLogin = (Button)findViewById(R.id.btnLogin);
		btnRecentplay = (Button)findViewById(R.id.btnRecentplay);
		btnSearch = (Button)findViewById(R.id.btnSearch);
		btnDelete = (Button)findViewById(R.id.btnDelete);
		btnDone = (Button)findViewById(R.id.btnDone);
		btnSelectAll = (Button)findViewById(R.id.btnSelectAll);
		btnEdit = (Button)findViewById(R.id.btnEdit);
		btnRefresh = (Button)findViewById(R.id.btnRefresh);
		btnCategory = (Button) findViewById(R.id.btnCategory);
		txtLogo = (TextView)findViewById(R.id.text_logo);
        imgLogo = (ImageView)findViewById(R.id.imageLogo);
        layoutCateScrollBar1 = (LinearLayout)findViewById(R.id.view_cateNamefirstlevel);
        layoutCateScrollBar2 = (LinearLayout)findViewById(R.id.view_cateNamesecondlevel);
        layoutCateScrollBar3 = (LinearLayout)findViewById(R.id.view_cateNamethirdlevel);
        
        tabHost = (TabHost)findViewById(R.id.tabHost);
        tabOnline = (TabButton) findViewById(R.id.tabBtnOnlineVideo);
        tabBtnBest = (TabButton)findViewById(R.id.tabBtnBestRecommend);
        tabLocal = (TabButton)findViewById(R.id.tabBtnLocalVideo);
        tabCate = (TabButton) findViewById(R.id.tabBtnCategoryBrowse);
        pageBtnHost = (PageButtonHost)findViewById(R.id.pageButtonHost);
        
        viewFlipHost = (ViewFlipper)findViewById(R.id.views_host);
        viewFlipPageHost = (ViewFlipper)findViewById(R.id.page_views_host);
        searchText = (VideoSearchText)findViewById(R.id.search_text);
        
        mygallery = (Gallery)findViewById(R.id.customgallery);
        gdvShowLocalVideos = (GridView) findViewById(R.id.gdvShowLocalVideos);
        btnDetailPlay = (Button)findViewById(R.id.btndetailplay);
        
        txtDetail = (TextView)findViewById(R.id.txtDetail);
        txtDetailBackGround = (TextView)findViewById(R.id.txtDetailbackground);
        
        GridViewXianFa = (GridView)findViewById(R.id.pageGridViewReMen);
        GridViewXingFa = (GridView)findViewById(R.id.pageGridViewDaShi);
        GridViewMinShang = (GridView)findViewById(R.id.pageGridViewZheXue);
        GridViewJingJi = (GridView)findViewById(R.id.pageGridViewJingJi);
        GridViewSuSong = (GridView)findViewById(R.id.pageGridViewLiShi);
        GridViewWenXue = (GridView)findViewById(R.id.pageGridViewWenXue);
        GridViewCategory = (GridView)findViewById(R.id.categoryvideolist);
        GridViewSearch = (GridView)findViewById(R.id.searchvideolist);
        
	}
	
	private void initMember() {
        SScoversPath = UsersUtil.getUserCoverDir() +"/";
        ssvideo = (SsvideoRoboApplication)getApplication();
        if(mDBAdapter == null) {
        	mDBAdapter = new SSVideoDatabaseAdapter(SsvideoActivity.this, ssvideo, TAG);
        }
		listener = new ButtonClickListener();
        
        btnLogin.setOnClickListener(listener);
        btnRecentplay.setOnClickListener(listener);
        btnSearch.setOnClickListener(listener);
        btnDelete.setOnClickListener(listener);
        btnDone.setOnClickListener(listener);
        btnSelectAll.setOnClickListener(listener);
        btnEdit.setOnClickListener(listener);
        btnRefresh.setOnClickListener(listener);
        btnCategory.setOnClickListener(listener);

        layoutCateScrollBar1.setVisibility(View.GONE);
        layoutCateScrollBar2.setVisibility(View.GONE);
        layoutCateScrollBar3.setVisibility(View.GONE);
        
        tabHost.setOnSelectedListener(this);
        pageBtnHost.setOnSelectedListener(this);

        viewFlipHost.setInAnimation(this, R.anim.fadein);
        viewFlipPageHost.setInAnimation(this, R.anim.fadein);
        
        searchText.setOnKeyboardListener(keyboardListener);
        imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE); 
        searchText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

        timerScrollGallery = new Timer();
        timerScrollGallery.schedule(taskScrollGallery, TIMER_SCROLL_GALLERY_DURATION, TIMER_SCROLL_GALLERY_DURATION);
        txtDetailBackGround.setBackgroundColor(Color.argb(155, 50, 50, 50)); // ����͸����
        
        coverHandler = new CoverHandler();
        
	}
	
	private void initLoadingView() {
		viewLoadingGallery = (LinearLayout)findViewById(R.id.loadingViewGallery);
    	viewLoadingReMen   = (LinearLayout)findViewById(R.id.loadingViewReMen);//loadingViewReMen
    	viewLoadingDaShi   = (LinearLayout)findViewById(R.id.loadingViewDaShi);
    	viewLoadingZheXue  = (LinearLayout)findViewById(R.id.loadingViewZheXue);
    	viewLoadingJingJi  = (LinearLayout)findViewById(R.id.loadingViewJingJi);
    	viewLoadingLiShi   = (LinearLayout)findViewById(R.id.loadingViewLiShi);
    	viewLoadingWenXue  = (LinearLayout)findViewById(R.id.loadingViewWenXue);
    	viewLoadingCategory= (LinearLayout)findViewById(R.id.loadingViewCategory);
    	viewLoadingSearch  = (LinearLayout)findViewById(R.id.loadingViewSearch);
    	viewLoadingLocal   = (LinearLayout)findViewById(R.id.loadingViewLocal);
	}
	/**
	 * ��ʼ���ػ�����ͼ
	 * @param iType
	 */
	public void startLodingView(int iType) {
		
		if (iType == INDEX_CATEGORY_VIEW) {
			viewLoadingCategory.setVisibility(View.VISIBLE);
		} else if (iType == RECENT_PLAY_HANLDER) {
			popll.setVisibility(View.VISIBLE);
		} else if (iType == INDEX_SEARCH_VIEW) {
			viewLoadingSearch.setVisibility(View.VISIBLE);
		} else if (iType == INDEX_LOCAL_VIEW) {
			viewLoadingLocal.setVisibility(View.VISIBLE);
		} else if (iType == INDEX_REMEN_VIEW) {
			viewLoadingReMen.setVisibility(View.VISIBLE);
		} else if (iType == INDEX_DASHI_VIEW) {
			viewLoadingDaShi.setVisibility(View.VISIBLE);
		} else if (iType == INDEX_ZHEXUE_VIEW) {
			viewLoadingZheXue.setVisibility(View.VISIBLE);
		} else if (iType == INDEX_JINGJI_VIEW) {
			viewLoadingJingJi.setVisibility(View.VISIBLE);
		} else if (iType == INDEX_LISHI_VIEW) {
			viewLoadingLiShi.setVisibility(View.VISIBLE);
		} else if (iType == INDEX_WENXUE_VIEW) {
			viewLoadingWenXue.setVisibility(View.VISIBLE);
		} else if (iType == INDEX_GALLERY_VIEW) {
			viewLoadingGallery.setVisibility(View.VISIBLE);
		} else if (iType == INDEX_BEST_VIEW) {
			viewLoadingReMen.setVisibility(View.VISIBLE);
			viewLoadingDaShi.setVisibility(View.VISIBLE);
			viewLoadingZheXue.setVisibility(View.VISIBLE);
			viewLoadingJingJi.setVisibility(View.VISIBLE);
			viewLoadingLiShi.setVisibility(View.VISIBLE);
			viewLoadingWenXue.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * ֹͣ���ػ�����ͼ
	 * @param iType
	 */
	public void stopLodingView(int iType) {
		if (iType == INDEX_CATEGORY_VIEW) {
			viewLoadingCategory.setVisibility(View.GONE);
		} else if (iType == RECENT_PLAY_HANLDER) {
			popll.setVisibility(View.GONE);
		} else if (iType == INDEX_SEARCH_VIEW) {
			viewLoadingSearch.setVisibility(View.GONE);
		} else if (iType == INDEX_LOCAL_VIEW) {
			viewLoadingLocal.setVisibility(View.GONE);
		} else if (iType == INDEX_REMEN_VIEW) {
			viewLoadingReMen.setVisibility(View.GONE);
		} else if (iType == INDEX_DASHI_VIEW) {
			viewLoadingDaShi.setVisibility(View.GONE);
		} else if (iType == INDEX_ZHEXUE_VIEW) {
			viewLoadingZheXue.setVisibility(View.GONE);
		} else if (iType == INDEX_JINGJI_VIEW) {
			viewLoadingJingJi.setVisibility(View.GONE);
		} else if (iType == INDEX_LISHI_VIEW) {
			viewLoadingLiShi.setVisibility(View.GONE);
		} else if (iType == INDEX_WENXUE_VIEW) {
			viewLoadingWenXue.setVisibility(View.GONE);
		} else if (iType == INDEX_GALLERY_VIEW) {
			viewLoadingGallery.setVisibility(View.GONE);
		} else if (iType == INDEX_BEST_VIEW) {
			viewLoadingReMen.setVisibility(View.GONE);
			viewLoadingDaShi.setVisibility(View.GONE);
			viewLoadingZheXue.setVisibility(View.GONE);
			viewLoadingJingJi.setVisibility(View.GONE);
			viewLoadingLiShi.setVisibility(View.GONE);
			viewLoadingWenXue.setVisibility(View.GONE);
		}
	}
    
	/**
	 * ���¼��ػ�����ͼ
	 * @param iType
	 */
    public void updateLodingView(int iType) {
    	if(iType == INDEX_CATEGORY_VIEW){
    		ProgressBar pb = (ProgressBar)findViewById(R.id.progressBarCategory);
    		int ival = pb.getProgress();
    		pb.setProgress(++ival); 
    		viewLoadingCategory.setVisibility(View.VISIBLE);
    	} else if (iType == RECENT_PLAY_HANLDER) {
    		ProgressBar pb = (ProgressBar) findViewById(R.id.progressBarDialog);
    		int ival = pb.getProgress();
    		pb.setProgress(++ival); 
    		popll.setVisibility(View.VISIBLE);
    	}
    	else if(iType == INDEX_SEARCH_VIEW){
    		ProgressBar pb = (ProgressBar)findViewById(R.id.progressBarSearch);
    		int ival = pb.getProgress();
    		pb.setProgress(++ival); 
    		viewLoadingSearch.setVisibility(View.VISIBLE);
    	}
    	else if(iType == INDEX_REMEN_VIEW){
    		viewLoadingReMen.setVisibility(View.VISIBLE);
    	}
    	else if(iType == INDEX_DASHI_VIEW){
    		viewLoadingDaShi.setVisibility(View.VISIBLE);
    	}
    	else if(iType == INDEX_ZHEXUE_VIEW){
    		viewLoadingZheXue.setVisibility(View.VISIBLE);
    	}
    	else if(iType == INDEX_JINGJI_VIEW){
    		viewLoadingJingJi.setVisibility(View.VISIBLE);
    	}
    	else if(iType == INDEX_LISHI_VIEW){
    		viewLoadingLiShi.setVisibility(View.VISIBLE);
    	}
    	else if(iType == INDEX_WENXUE_VIEW){
    		viewLoadingWenXue.setVisibility(View.VISIBLE);
    	}
    	else if(iType == INDEX_GALLERY_VIEW){
    		viewLoadingGallery.setVisibility(View.VISIBLE);
    	}
	}
    /**
     * ���¼���Handler
     */
	final Handler handlerLodingView = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case View.GONE:
				stopLodingView(msg.arg1);
				break;
			case View.VISIBLE:
				startLodingView(msg.arg1);
				break;
			case View.INVISIBLE:
				updateLodingView(msg.arg1);
				break;
			case INDEX_LOCAL_VIEW:
				if (msg.arg1 == View.VISIBLE) {
					tabLocal.selected = true;
				} else {
					tabLocal.selected = false;
				}
				tabHost.onFinishInflate();
				break;
			}// end switch;

		}
	};// end handlerLodingView;
    /**
     * ���¼��ؽ���
     */
    public void updateLoadingViewThread(final int iType){
    	Thread thread = new Thread(){
    		@Override
    		public void run(){
    			try {
    		    	Message msg = Message.obtain();
    				msg.what = View.INVISIBLE;
    				msg.arg1 = iType;
    				handlerLodingView.sendMessage(msg);
				} catch (Exception e) {
				} 
    		}
    	};
    	thread.start();
    	thread = null;    	
    }
    /**
     * ������Ƶ������
     * @param info
     */
	private void goPlayMovie(SeriesInfo info) {
		Intent intent = new Intent();
		intent.setClass(SsvideoActivity.this, SsvideoPlayerActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("videoType", 1);
		bundle.putSerializable("SeriesInfo", (Serializable) info);
		intent.putExtras(bundle);
		startActivity(intent);
	}
    
    private class ButtonClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			try {
				switch (v.getId()) {
				case R.id.btnLogin:
					loginClicked();
					break;
				case R.id.btnRecentplay:
					list.clear();
					test();
					break;
				case R.id.btnSearch:
					searchClicked();
					break;
				case R.id.btnEdit:
					editClicked();
					break;
				case R.id.btnRefresh:
					refreshLocalvideo();
					break;						
				case R.id.btnSelectAll:
					selectAllClicked();
					break;
				case R.id.btnDone:
					doneClicked();
					break;
				case R.id.btnDelete:
					deleteClicked();
					break;
				case R.id.btnCategory:
					categoryClicked();
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}//end ButtonClickListener;
  
    @Override
	public void onSelected(int itemId) {
    	ID_FALGE = itemId;
    	//TODO:
    	if(!AppConfig.ORDER_ONLINE_VIDEO || itemId != INDEX_ONLINE_VIDEO) {
    		viewFlipHost.setDisplayedChild(itemId);
    	}
    	if(itemId == INDEX_CATEGORY_VIEW){
    		btnLogin.setVisibility(ImageButton.VISIBLE);
    		btnDelete.setVisibility(ImageButton.GONE);
    		btnSearch.setVisibility(ImageButton.VISIBLE);
    		btnDone.setVisibility(ImageButton.GONE);
    		btnSelectAll.setVisibility(ImageButton.GONE);
    		btnEdit.setVisibility(ImageButton.GONE);
    		btnRefresh.setVisibility(ImageButton.GONE);
    		btnCategory.setVisibility(Button.GONE);
    		imgLogo.setVisibility(ImageView.GONE);
    		txtLogo.setVisibility(View.GONE);
    		
    		if(serListCateAll == null) {
    			//startLodingView(INDEX_CATEGORY_VIEW);
    			sendStartLoadMessage(INDEX_CATEGORY_VIEW);//startLoadingViewThread(INDEX_CATEGORY_VIEW);
    			serListCateAll = new ArrayList<SeriesInfo>();
    			boolean blState = getNetIsConnected();
    			if(blState) {
    				String sUrlCateAll = "http://www.chaoxing.cc/ipad/getdata.aspx?action=hot&padtype=android&page=1&count=20&u=&type=2";
    				sUrlCateAll = "0";
    				getCategoryData(sUrlCateAll, INDEX_DATA_ADDNEW);
    	    		String sUrlCateAllCount = "http://www.chaoxing.cc/ipad/getdata.aspx?action=hot&padtype=android&page=1&count=20&u=&type=2&getcount=1";
    	    		getDataCountOfServer(sUrlCateAllCount, INDEX_CATEGORY_VIEW);
    	    		String sUrlCateName = "http://www.chaoxing.cc/ipad/getdata.aspx?action=cate&padtype=android&u=&type=1";
    	    		//getCategoryName(sUrlCateName, INDEX_CATEGORY_FIRST);
    	    		getCategoryName(0, INDEX_CATEGORY_FIRST);	    			
    			} else {
//    				getCategoryDataLocal();
    				getCategoryDataLocalThread();
    			}
      	  }
    	}
    	else if(itemId == INDEX_LOCAL_VIEW){
    		btnLogin.setVisibility(ImageButton.GONE);
    		btnDelete.setVisibility(ImageButton.GONE);
    		btnSearch.setVisibility(ImageButton.GONE);
    		btnDone.setVisibility(ImageButton.GONE);
    		btnSelectAll.setVisibility(ImageButton.GONE);
    		btnEdit.setVisibility(ImageButton.VISIBLE);
    		btnRefresh.setVisibility(ImageButton.VISIBLE);
    		btnCategory.setVisibility(Button.GONE);
    		txtLogo.setText(string.tab_local_video);
    		txtLogo.setVisibility(View.VISIBLE);
    		imgLogo.setVisibility(ImageView.GONE);
    		if(isFirstDisplayLocalVideo == true){
    			Intent intent = this.getIntent();
    	        classify = intent.getStringExtra("classify");
    			isFirstDisplayLocalVideo = false;
    		}
    		displayLocalVideo(true);
    	}
    	else if(itemId == INDEX_BEST_VIEW){
    		btnLogin.setVisibility(ImageButton.VISIBLE);
    		btnDelete.setVisibility(ImageButton.GONE);
    		btnSearch.setVisibility(ImageButton.VISIBLE);
    		btnDone.setVisibility(ImageButton.GONE);
    		btnSelectAll.setVisibility(ImageButton.GONE);
    		btnEdit.setVisibility(ImageButton.GONE);
    		btnRefresh.setVisibility(ImageButton.GONE);
    		btnCategory.setVisibility(Button.GONE);
    		txtLogo.setText(string.app_welcome_name);
    		txtLogo.setVisibility(View.VISIBLE);
    		imgLogo.setVisibility(ImageView.VISIBLE);
    	}
    	else if(itemId == INDEX_ONLINE_VIDEO){
    		if(!AppConfig.ORDER_ONLINE_VIDEO) {
	    		btnLogin.setVisibility(ImageButton.VISIBLE);
	    		btnDelete.setVisibility(ImageButton.GONE);
	    		btnSearch.setVisibility(ImageButton.VISIBLE);
	    		btnDone.setVisibility(ImageButton.GONE);
	    		btnSelectAll.setVisibility(ImageButton.GONE);
	    		btnEdit.setVisibility(ImageButton.GONE);
	    		btnRefresh.setVisibility(ImageButton.GONE);
	    		btnCategory.setVisibility(Button.GONE);
    		}
    		
    		String url = getResources().getString(R.string.url_online_video);
    		url = WidgetUtil.getFayuanLoginUrl(SsvideoActivity.this);//��Դ
    		if(url == "") {
    			CreateLoginFayuanDialog(SsvideoActivity.this);//��Դ
    		}
    		else {
    			Intent intent = getDefaultBrowserIntent(url);
    			startActivity(intent);
    		}
    	}
	}//end onSelected;
    
	@Override
	protected void onStart() {
		mStop = false;	
		if(AppConfig.ORDER_ONLINE_VIDEO) {
			setDefaultView(INDEX_ONLINE_VIDEO, false);//INDEX_DEFAULT_VIEW, INDEX_BEST_VIEW, INDEX_ONLINE_VIDEO
		}
		super.onStart();
	}
	
	private Intent getDefaultBrowserIntent(String url) {
	/*	String url = getSearchUrl(srcKey);
		if(url == null || url.equals("")) return;
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));*/
		Intent intent = new Intent();        
		 intent.setAction(Intent.ACTION_VIEW);        
		 Uri content_uri_browsers = Uri.parse(url);       
		 intent.setData(content_uri_browsers);
//		 intent.setClassName("com.android.browser",  "com.android.browser.BrowserActivity");        
		 return intent;
	}
    
	private void displayLocalVideo(boolean isFirstLoad) {
		listLocalVideo = new LinkedList<Map<String, Object>>();
		downloadingVector = new Vector<Map<String, Object>>();
		selectedItemsSet = new HashSet<Integer>();
		// Log.d(TAG, "test load time start 1.");
		if (isFirstLoad)
			loadCategoryButtons();
		LinkedList<SSVideoLocalVideoBean> localVideoList = mDBAdapter.fetchAllDataInLocalVideo();
		adapter = new LocalVideoListAdapter(SsvideoActivity.this,listLocalVideo);
		gdvShowLocalVideos.setAdapter(adapter);
		if (classify != null) {
			try {
				SSVideoCategoryBean categoryBean = mDBAdapter.fetchCategoryByName(classify);
				LinkedList<SSVideoLocalVideoBean> classifiedLocalVideoList = mDBAdapter.fetchDataInLocalVideoBySubject(categoryBean.getStrCateId());
				fillShowAndDownloadContainer(classifiedLocalVideoList);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			fillShowAndDownloadContainer(localVideoList);
		}

		if (isFirstLoad)
			gdvShowLocalVideos.setOnItemClickListener(new GridViewNormalListener());
	}// end displayLocalVideo

	private void fillShowAndDownloadContainer(
			LinkedList<SSVideoLocalVideoBean> LocalVideoList) {
		for (SSVideoLocalVideoBean temLocalVideoBean : LocalVideoList) {
			int status = temLocalVideoBean.getnVideoDownloadStatus();
			Map<String, Object> item = makeListLocalVideoItem(temLocalVideoBean);
			((LinkedList<Map<String, Object>>) listLocalVideo).addLast(item);
			if (status > 0)
				downloadingVector.add(item);
		}
	}

	private Map<String, Object> makeListLocalVideoItem(
			SSVideoLocalVideoBean localVideoBean) {
		Map<String, Object> item = new HashMap<String, Object>();
		item.put("localVideoBean", localVideoBean);
		item.put("videoId", localVideoBean.getStrVideoId());
		item.put("videoCover", SScoversPath + localVideoBean.getStrCoverName());
		item.put("status", localVideoBean.getnVideoDownloadStatus());
		item.put("progress", localVideoBean.getnVideoDownloadProgress());
		item.put("fileLen", localVideoBean.getnVideoFileLength());
		item.put("delImg", 0);
		return item;
	}
    
	//FIXME  ��ʾ���ż�¼ 
    List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
    ArrayList<SSVideoPlayListBean> recentList = null;

    private View popupWindowView;
    private PopupWindow recentPopupWindow;
    private LinearLayout popll ;
    private void test() {
    	LayoutInflater factory = LayoutInflater.from(SsvideoActivity.this);
    	popupWindowView = factory.inflate(R.layout.recent_play_dialog, null);
    	recentPopupWindow = new PopupWindow(popupWindowView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
    	ColorDrawable dw = new ColorDrawable(686869);
		recentPopupWindow.setBackgroundDrawable(dw);
    	recentPopupWindow.showAsDropDown(btnRecentplay, 10, 0);	
    	popll = (LinearLayout) popupWindowView.findViewById(R.id.loadingRecentDialog);

    	startLodingView(RECENT_PLAY_HANLDER);
    	new Thread() {
    		public void run() {
    			recentList = mFaYuanData.recentPlayList(1, 100, 1483377);
    			if (mFaYuanData.isFaYuanFlage()) {
    				recentList = mFaYuanData.recentPlayList(1, 100, 1483377/*mFaYuanData.userInfo.getuId()*/);
    			} else {
    				//Toast.makeText(getApplicationContext(), "���ȵ�¼֮����ִ�д˲���", 0).show();
    			}  	
    		};
    	}.start();	
		Message msgData = handlerCategory.obtainMessage();
		msgData.what = RECENT_PLAY_HANLDER;
		msgData.obj = recentList;
		handlerCategory.sendEmptyMessageDelayed(RECENT_PLAY_HANLDER, 4000);
    }
 
	public void recentplayClicked(ArrayList<SSVideoPlayListBean> videolist) {    	
		ListView recentListView = (ListView)popupWindowView.findViewById(R.id.records_recent_dlg_lv);
    	final TextView recentCountTextView =  (TextView) popupWindowView.findViewById(R.id.count_recent_dlg_tv);
    	Button ibClearRecent = (Button) popupWindowView.findViewById(R.id.clear_recent_dlg_ib);

    	try {
        	recentCountTextView.setText("�ۿ���¼" + videolist.size() + "��");
        	for (int i = 0; i < videolist.size(); i++) {
				SSVideoPlayListBean tempPlayListBean = videolist.get(i);
				Map<String, Object> item = new HashMap<String, Object>();
				item.put("videoname", tempPlayListBean.getStrVideoName());
				item.put("datevisit", tempPlayListBean.getStrDate());
				item.put("playlist", tempPlayListBean);
				list.add(item);
			}
        	final SimpleAdapter recentAdapter  = new SimpleAdapter(SsvideoActivity.this, list, R.layout.recent_play_list_item, 
        			new String[]{"videoname","datevisit", "recentDelete"}, 
        			new int[]{R.id.name_recent_play_list_tv, R.id.progress_recent_play_list_tv, R.id.delete_recent_play_list_ib});
			recentListView.setAdapter(recentAdapter);
			ibClearRecent.setOnClickListener(new ImageButton.OnClickListener(){
				@Override
				public void onClick(View v) {
					try {
						mDBAdapter.deleteAllDataInPlayList();
						list.clear();
						recentAdapter.notifyDataSetChanged();
						recentCountTextView.setText("�ۿ���¼0��");
					} catch (Exception e){//(SQLException e) {
						e.printStackTrace();
					}
				}
			});
			recentListView.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Map<String, Object> map = list.get(position);
					SSVideoPlayListBean playListBean = (SSVideoPlayListBean) map.get("playlist");
					Intent iPlayVideo = new Intent(SsvideoActivity.this, SsvideoPlayerActivity.class);
  					Bundle bundle = new Bundle();
  					bundle.putInt("videoType", 1);
  					bundle.putInt("position", position);
  					bundle.putSerializable("playListBean", playListBean);
  					iPlayVideo.putExtras(bundle);
  					startActivity(iPlayVideo);
  					recentPopupWindow.dismiss();
				}
			});
		} catch (Exception e){//(SQLException e) {
			e.printStackTrace();
		}
    }//end recentplayClicked;
	
	/**
	 *  �༭������Ƶ
	 */
	public void editClicked() {
		btnEdit.setVisibility(ImageButton.GONE);
		btnRefresh.setVisibility(ImageButton.GONE);
		btnCategory.setVisibility(ImageButton.GONE);
		btnDelete.setVisibility(ImageButton.VISIBLE);
		btnDone.setVisibility(ImageButton.VISIBLE);
		btnSelectAll.setVisibility(ImageButton.VISIBLE);
		gdvShowLocalVideos.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (selectedItemsSet.contains(position)) {
					listLocalVideo.get(position).put("delImg", 0);
					selectedItemsSet.remove(position);
				} else {
					listLocalVideo.get(position).put("delImg",	R.drawable.local_view_delete);
					selectedItemsSet.add(position);
				}
				adapter.notifyDataSetChanged();
			}
		});
	}
	
	/**
	 *  ѡ�����б�����Ƶ
	 */
	private void selectAllClicked() {
		int position = 0;
		if (listLocalVideo.size() > selectedItemsSet.size()) {
			for (Map<String, Object> item : listLocalVideo) {
				selectedItemsSet.add(position);
				item.put("delImg", R.drawable.local_view_delete);
				++position;
			}
		} else {
			for (Map<String, Object> item : listLocalVideo) {
				selectedItemsSet.remove(position);
				item.put("delImg", 0);
				++position;
			}
		}
		adapter.notifyDataSetChanged();
	}
    
    //ɾ��ѡ����Ƶ
	public void deleteClicked() {
		for (int selectedPos : selectedItemsSet) {
			Map<String, Object> map = listLocalVideo.get(selectedPos);
			SSVideoLocalVideoBean localVideoBean = (SSVideoLocalVideoBean) map.get("localVideoBean");
			String filePath = localVideoBean.getStrVideoLocalPath();
			String coverPath = SScoversPath + localVideoBean.getStrCoverName();

			if ((Integer) map.get("status") == 1) {// ��Ҫɾ��һ�����������е���Ƶ
				Intent serviceIntent = new Intent("com.chaoxing.download.FileService");
				localVideoBean.setStrCoverName(coverPath); // ������ʱ��coverName������coverPath
				Bundle bundle = new Bundle();
				bundle.putInt("op", 3);
				bundle.putSerializable("localVideoBean", localVideoBean);
				serviceIntent.putExtras(bundle);
				startService(serviceIntent);
			} else {
				mDBAdapter.deleteDataInLocalVideoByPath(filePath);
				File file = new File(filePath);
				file.delete();
				File fCover = new File(coverPath);
				fCover.delete();
			}
			listLocalVideo.remove(selectedPos);
			selectedItemsSet.remove(selectedPos);
		}
		downloadingVector.clear();
		for (Map<String, Object> item : listLocalVideo) {
			if ((Integer) item.get("status") > 0) {
				downloadingVector.add(item);
			}
		}
		adapter.notifyDataSetChanged();
	}
    
    /*
     * ��ɱ༭����
     */
	public void doneClicked() {
		btnEdit.setVisibility(ImageButton.VISIBLE);
		btnRefresh.setVisibility(ImageButton.VISIBLE);
		btnCategory.setVisibility(ImageButton.GONE);
		btnDelete.setVisibility(ImageButton.GONE);
		btnDone.setVisibility(ImageButton.GONE);
		btnSelectAll.setVisibility(ImageButton.GONE);
		for (int tempSet : selectedItemsSet) {
			listLocalVideo.get(tempSet).put("delImg", 0);
		}
		adapter.notifyDataSetChanged();
		selectedItemsSet.clear();
		gdvShowLocalVideos.setOnItemClickListener(new GridViewNormalListener());
	}
	/*
	 * ���ط��ఴť���
	 */
	private void loadCategoryButtons() {
		llLocalVideoCategory = (LinearLayout) findViewById(R.id.layout_categorys);
		llLocalVideoCategory.removeAllViewsInLayout();
		Button btAll = new Button(SsvideoActivity.this);
		btAll.setBackgroundResource(R.drawable.category_btn_normal_selector_xml);
		btAll.setText("ȫ��");
		btAll.setWidth(100);
		btAll.setTextAppearance(SsvideoActivity.this, R.style.text_24_white);
		btAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				btnCategory.setText(((Button) v).getText());
				getListAllCategory();
				classify = null;
				setCateButtonBg();
			}
		});
		llLocalVideoCategory.addView(btAll);

		final LinkedList<SSVideoCategoryBean> categoryList = mDBAdapter.fetchAllCategory();
		for (int i = 0; i < categoryList.size(); i++) {
			if ("XXX".equals(categoryList.get(i).getStrCateId())) {
				continue;
			}
			Button btnCate = new Button(SsvideoActivity.this);
			btnCate.setBackgroundResource(R.drawable.category_btn_normal_selector_xml);
			btnCate.setText(categoryList.get(i).getStrCateName());
			btnCate.setTextAppearance(SsvideoActivity.this,	R.style.text_24_white);
			// btnCate.setHeight(40);
			btnCate.setWidth(100);
			btnCate.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					btnCategory.setText(((Button) v).getText());
					getListByCategory((String) ((Button) v).getText());
					classify = (String) ((Button) v).getText();
					setCateButtonBg();
				}
			});
			// ���ڷָ�������ť֮��Ĵ�ֱ���룬��Ѱ�Ҹ��õİ취
			llLocalVideoCategory.addView(btnCate);
		}
		setCateButtonBg();
	}

	private void setCateButtonBg() {
		for(int i=0; i<llLocalVideoCategory.getChildCount(); i++){
			llLocalVideoCategory.getChildAt(i).setBackgroundResource(R.drawable.category_btn_normal_selector_xml);
			if(classify == null){
				llLocalVideoCategory.getChildAt(0).setBackgroundResource(R.drawable.category_btn_selected_selector_xml);
			}
			else if(classify.equals(((Button)(llLocalVideoCategory.getChildAt(i))).getText())){
					llLocalVideoCategory.getChildAt(i).setBackgroundResource(R.drawable.category_btn_selected_selector_xml);
			}
		}
	}

	public void categoryClicked() {
		View categoryPopupView = LayoutInflater.from(SsvideoActivity.this).inflate(R.layout.category_popup, null);
		Button btAll = (Button) categoryPopupView.findViewById(R.id.bt_category_all);
		btAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				btnCategory.setText(((Button)v).getText());
				getListAllCategory();
				categoryPopupWindow.dismiss();
				classify = null;
			}
		});
		
		LinearLayout llCategory = (LinearLayout) categoryPopupView.findViewById(R.id.ll_category);
		//final LinkedList<SSVideoCategoryBean> categoryList = mDBAdapter.fetchAllCategory();
		final ArrayList<CategoryNameInfo> categoryList = listCateName;
		for( int i=0; i<categoryList.size(); i++){
			Button btnCate = new Button(SsvideoActivity.this);
			btnCate.setBackgroundResource(R.drawable.category_btn_selector_xml);
			//btnCate.setText(categoryList.get(i).getStrCateName());
			btnCate.setText(categoryList.get(i).getTitle());
			btnCate.setTextAppearance(SsvideoActivity.this, R.style.text_24_white);
			btnCate.setHeight(50);
			btnCate.setWidth(200);
			btnCate.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					btnCategory.setText(((Button)v).getText());
					getListByCategory((String) ((Button)v).getText());
					categoryPopupWindow.dismiss();	
					classify = (String) ((Button)v).getText();
				}
			});
			// ���ڷָ�������ť֮��Ĵ�ֱ���룬��Ѱ�Ҹ��õİ취
			TextView tv = new TextView(SsvideoActivity.this);
			tv.setHeight(10);
			tv.setWidth(200);
			
			llCategory.addView(btnCate);
			llCategory.addView(tv);
		}
		
		categoryPopupWindow = new PopupWindow(categoryPopupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		ColorDrawable dw = new ColorDrawable(686869);
		categoryPopupWindow.setBackgroundDrawable(dw);
		categoryPopupWindow.setFocusable(true);
		categoryPopupWindow.showAsDropDown (btnCategory, -60, 0);
		
	}
	
	private void getListByCategory(String category){
		LinkedList<SSVideoLocalVideoBean> categoriedLocalVideoList = null;
		try {
			SSVideoCategoryBean categoryBean = mDBAdapter.fetchCategoryByName(category);
			categoriedLocalVideoList = mDBAdapter.fetchDataInLocalVideoBySubject(categoryBean.getStrCateId());
			listLocalVideo.clear();
			downloadingVector.clear();
			fillShowAndDownloadContainer(categoriedLocalVideoList);
			adapter.notifyDataSetChanged();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void getListAllCategory(){
		LinkedList<SSVideoLocalVideoBean> categoriedLocalVideoList = null;
		try {
			categoriedLocalVideoList = mDBAdapter.fetchAllDataInLocalVideo();
			listLocalVideo.clear();
			downloadingVector.clear();
			fillShowAndDownloadContainer(categoriedLocalVideoList);
			adapter.notifyDataSetChanged();
		} catch (Exception e){//(SQLException e) {
			e.printStackTrace();
		}
	}
		
    @Override
	public void onPageSelected(int itemId) {
    	viewFlipPageHost.setDisplayedChild(itemId);
    	setPageSelectedView(itemId);
	}
    
    public void loginClicked() {
    	String btnCaption = btnLogin.getText().toString();
    	if(btnCaption.equals("��¼")){
    		CreateLoginFayuanDialog(SsvideoActivity.this);
    	}
    	else if(btnCaption.equals("�û���Ϣ")){
    		CreateUserInfoDialog();
    	}
    }
    
    private void searchClicked() {
    	mBackIME = !mBackIME;
    	triggerSearch(true);
    }
    
    private void hideSearchBar() {
		int visbility = searchText.getVisibility();
		if(visbility==View.GONE){
			/*searchText.setVisibility(View.VISIBLE);
			searchText.startAnimation(
					AnimationControl.sildeinParentTop(
							SsvideoActivity.this,
							new Runnable(){
								@Override
								public void run() {
									if(searchText.isFocusable()){
										searchText.requestFocus();
										imm.showSoftInput(searchText, InputMethodManager.SHOW_IMPLICIT);
									}
								}
							}
					)
			);*/
		}
		else {
			searchText.startAnimation(
					AnimationControl.sildeoutParentTop(
							SsvideoActivity.this,
							new Runnable(){	
								public void run() {
									searchText.setVisibility(View.GONE);
								};
							})
			);
			imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
		}
    }
    
	final VideoSearchText.OnKeyboardListener keyboardListener = new VideoSearchText.OnKeyboardListener() {
		@Override
		public void onAction(VideoSearchText text) {
			ID_FALGE = -1;
			triggerSearch(true);
		}

		@Override
		public void onBackIME() {
			// onBackPressed();
			hideSearchBar();
		}
	};
	
	/**��������Ҫ���£����ڵİ汾����Ч�������롣*/
	private void triggerSearch(boolean doSearch){
		int visbility = searchText.getVisibility();
		if(visbility==View.GONE){
			searchText.setText("");
			searchText.setVisibility(View.VISIBLE);
			searchText.startAnimation(
					AnimationControl.sildeinParentTop(
							SsvideoActivity.this,
							new Runnable(){
								@Override
								public void run() {
									if(searchText.isFocusable()){
										searchText.requestFocus();
										imm.showSoftInput(searchText, InputMethodManager.SHOW_IMPLICIT);
									}
								}
							}
					)
			);
		}else{
			imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
			searchText.startAnimation(
					AnimationControl.sildeoutParentTop(
							SsvideoActivity.this,
							new Runnable(){	
								public void run() {
									searchText.setVisibility(View.GONE);
								};
							})
			);
			
			if(doSearch){
				Editable editable = searchText.getText();
				if(editable!=null){
					String text = editable.toString().trim();
					if(!StringUtil.isEmpty(text)){
						if (serListSearch == null)
							serListSearch = new ArrayList<SeriesInfo>();
						sendStartLoadMessage(INDEX_SEARCH_VIEW);//startLoadingViewThread(INDEX_SEARCH_VIEW);
						getSearchData(text, INDEX_DATA_ADDNEW, 1, true);
						boolean blState = getNetIsConnected();
	  				  	if(!blState) {
	  				  		getSearchDataLocal(text, INDEX_DATA_ADDNEW, true);
	  				  	}
	  				  	else {
	  				  		getSearchData(text, INDEX_DATA_ADDNEW, 1, true);
	  				  	}
						if(viewFlipHost.getDisplayedChild()!=INDEX_SEARCH_VIEW){
				   		 	viewFlipHost.setDisplayedChild(INDEX_SEARCH_VIEW);
							tabHost.unSelected();
						}
					}
				}
			}
		}
	}//end triggerSearch;
	
	
	final TimerTask taskScrollGallery = new TimerTask(){   
 	   int ipisition = 0;
 	   @Override
        public void run() {   
 		   ipisition = ipisition + 1;
            Message message = new Message();
            Bundle date = new Bundle();// �������
            date.putInt("pos", ipisition);
            message.setData(date);
            message.what = 1;
            handlerScrollGallery.sendMessage(message);     
        }   
             
     };   
      
     final Handler handlerScrollGallery = new Handler() {
     	public void handleMessage(Message message) {
              super.handleMessage(message);
              switch (message.what) {
                  case 1:
                 	 mygallery.setSelection(message.getData().getInt("pos"));
                 	 if(serListTop != null && serListTop.size() != 0)
                 	 {
                 		 int idx = message.getData().getInt("pos")%serListTop.size();
                 		 SeriesInfo info = serListTop.get(idx);
                 		 String sAbstract = info.getAbstracts();
              		 	 try {
    						sAbstract = getPartString(info.getAbstracts(), 378);
              		 		} catch (UnsupportedEncodingException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					 }
                 		 txtDetail.setText(info.getTitle()+"\r\n\r\n"+sAbstract);
                 	 }
                      break;
              }
     	}
      }; 
	
      @Override  
      protected void onDestroy() {
    	  mStop = true;
      	if (timerScrollGallery != null) {   
      		timerScrollGallery.cancel();   
      		timerScrollGallery = null;   
          }
      	
      	Intent serviceIntent = new Intent("com.chaoxing.download.FileService");
		stopService(serviceIntent);
		unregisterReceiver(receiver);
//		Log.d(TAG, "SsvideoActivity  onDestroy=====================");
      	super.onDestroy();   
      }   
      
      @Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
//    	  Log.d(TAG, "backPressed: "+ mBackIME);
    	  if(mBackIME) {
  			imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
  			searchText.startAnimation(
  					AnimationControl.sildeoutParentTop(
  							SsvideoActivity.this,
  							new Runnable(){	
  								public void run() {
  									searchText.setVisibility(View.GONE);
  								};
  							})
  			);
  			mBackIME = false;
  		}
  		else {
  			AlertDialogUtil.confirm(this, getString(R.string.alert_title), StringUtil.format(getString(R.string.alert_close_tip), getString(R.string.app_name)),
				new AlertDialogUtil.ConfirmListener() {
					@Override
					public void onPositive(DialogInterface dlg) {
						finish();
					}

				});
  		}
//		super.onBackPressed();
	}

	private void initTopView(String surl) {
		XmlParserFactory xmlParserTop = new XmlParserFactory();
		// String urlTop =
		// getResources().getString(R.string.url_top6_recommend);
		xmlParserTop.getRequestFile(surl);
		int ires = xmlParserTop.getResponse();
		if (ires != 0) {
			if (ires == -1)
				ToolsLog.LOG_DBG(TAG + "��ʼ��ʧ�ܣ�-initTopView");
			else if (ires == 1)
				ToolsLog.LOG_DBG(TAG + "����ʧ�ܣ�-initTopView");
			else if (ires == 2)
				ToolsLog.LOG_DBG(TAG + "���ݳ���Ϊ0-initTopView");
			else
				ToolsLog.LOG_DBG(TAG + "����ʧ�ܣ�-initTopView");
			return;
		}
		serListTop = xmlParserTop.getTopSeriesInfoListPull();
		if (serListTop != null) {
			int ilen = serListTop.size();
			ToolsLog.LOG_DBG(TAG + "Top length is:" + ilen);

		}
	}// end initTopView;
      
      final Handler handlerCategory = new Handler(){
		
		@Override
  		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Toast.makeText(SsvideoActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
				break;
			case RECENT_PLAY_HANLDER:
				 ArrayList<SSVideoPlayListBean> ll = recentList;
  				 recentplayClicked(ll);
  				 stopLodingView(RECENT_PLAY_HANLDER);
				break;
			case INDEX_DATA_COUNT:
				if (msg.arg1 == INDEX_CATEGORY_VIEW) {
					pvAdapterCategory.setMaxDataCount(msg.arg2);
				} else if (msg.arg1 == INDEX_SEARCH_VIEW) {
					SeriesInfo si = (SeriesInfo) msg.obj;
					sCurrentSearchKey = si.getTitle();// (String)msg.obj;
					pvAdapterSearch.setMaxDataCount(msg.arg2);
				}
				break;
			case INDEX_DATA_CATEGORY:
				try {
					ArrayList<SeriesInfo> serLstCate = (ArrayList<SeriesInfo>) msg.obj;
					if (msg.arg1 == INDEX_DATA_ADDNEW) {
						pvAdapterCategory.clearListData();
						serListCateAll.clear();
					}
					if (serLstCate.size() > 0) {
						serListCateAll.addAll(serLstCate);
						pvAdapterCategory.addList(serLstCate);
						pvAdapterCategory.notifyDataSetChanged();
						stopLodingView(INDEX_CATEGORY_VIEW);
					} else {
						stopLodingView(INDEX_CATEGORY_VIEW);
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				break;
			case INDEX_DATA_SEARCH:
				ArrayList<SeriesInfo> listSearchData = (ArrayList<SeriesInfo>) msg.obj;
				ToolsLog.LOG_DBG(TAG + " listSearchData.size() " + listSearchData.size());
				try {
					if (msg.arg1 == INDEX_DATA_ADDNEW) {
						pvAdapterSearch.clearListData();
						serListSearch.clear();
					}
					if (listSearchData.size() > 0) {
						serListSearch.addAll(listSearchData);
						pvAdapterSearch.addList(listSearchData);
						pvAdapterSearch.notifyDataSetChanged();
						stopLodingView(INDEX_SEARCH_VIEW);
					} else {
						stopLodingView(INDEX_SEARCH_VIEW);
					}
				} catch (Exception e) {
					// TODO: handle exception
					ToolsLog.LOG_DBG(TAG + "  ��ȡ����ʧ�ܣ�" + e.toString());
				}
				break;
			case INDEX_CATEGORY_NAME:
				try {
					ArrayList<CategoryNameInfo> listCateName = (ArrayList<CategoryNameInfo>) msg.obj;
					if (msg.arg1 == INDEX_CATEGORY_FIRST) {
						cateNameListFirst = listCateName;
						initFirstLevelCategoryImg(cateNameListFirst, 1);
						// getLevelCategoryBar(cateNameListFirst, 1);
					} else if (msg.arg1 == INDEX_CATEGORY_SECOND) {
						cateNameListSecond = listCateName;
						initFirstLevelCategoryImg(cateNameListSecond, 2);
						// getLevelCategoryBar(cateNameListSecond, 2);
					} else if (msg.arg1 == INDEX_CATEGORY_THIRD) {
						cateNameListThird = listCateName;
						initFirstLevelCategoryImg(cateNameListThird, 3);
						// getLevelCategoryBar(cateNameListThird, 3);
					}
				} catch (Exception e) {
					// TODO: handle exception
					ToolsLog.LOG_DBG(TAG + "��ʼ����������ʧ�ܣ�" + e.toString());
				}
				break;
			default:
				break;
			}
  		}//end handleMessage;	
  	};//end handlerCategory;
  	
  	final Handler handlerBest = new Handler(){
		
		@Override
  		public void handleMessage(Message msg) {
  			// TODO Auto-generated method stub
  			//super.handleMessage(msg);
  			if(msg.what == 0)
  			{
  				if(msg.arg1 == ALERT_NO_NETWORK) {
  					showAlert((String)msg.obj);
  				}
  				else if(msg.arg1 == ALERT_REPEAT_NETWORK) {
  					showAlertRepeat((String)msg.obj, msg.arg2);
  				}
  				else
  					Toast.makeText(SsvideoActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
  			}
  			else if(msg.what == INDEX_GALLERY_VIEW)
  			{
				ArrayList<SeriesInfo> listSeries = (ArrayList<SeriesInfo>) msg.obj;
				try {
					if (msg.arg1 == 1) {
						serListTop = listSeries;
						getSeriesCovers(serListTop, INDEX_GALLERY_IMAGE, 0,
								INDEX_GALLERY_VIEW);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}	
  			}
  			else if(msg.what == INDEX_GALLERY_IMAGE)
  			{
  				ArrayList<Bitmap> listBmp = (ArrayList<Bitmap>)msg.obj;
  				gvAdapter.setList(listBmp);
  				gvAdapter.notifyDataSetChanged();
  				stopLodingView(INDEX_GALLERY_VIEW);
  			}
  			else if(msg.what == INDEX_SCREEN_PARAMS)
  			{
  				screenWidth = msg.arg1;
  				screenHeight= msg.arg2;
  			}
  			else if(msg.what == VIEW_LOADING_STOP)
  			{
  				stopLodingView(msg.arg1);
  			}
  			else if(msg.what == VIEW_LOADING_START) {
  				startLodingView(msg.arg1);
  			}
  			else if(msg.what == VIEW_DATA_RELOAD) {
  				if(msg.arg1 == INDEX_GALLERY_VIEW) {
//					Log.d(TAG, "��ʼ���ض���6���Ƽ�");
  					startLodingView(INDEX_GALLERY_VIEW);
  					//String urlBestTop6 = getResources().getString(R.string.url_top6_recommend);
  					getBestTopInfoView();
  				}
  				else if(msg.arg1 == INDEX_BEST_VIEW) {
//  				Log.d(TAG, "��ʼ�����Ƽ���6������");
  					startLodingView(INDEX_BEST_VIEW);
  	  				//String urlBest = getResources().getString(R.string.url_best_recommend);
  	  				getPageViewData();
  				}
  				else if(msg.arg1 == INDEX_CATEGORY_VIEW) {
  					startLodingView(INDEX_CATEGORY_VIEW);
  					String sUrlCateAll = "http://www.chaoxing.cc/ipad/getdata.aspx?action=hot&padtype=android&page=1&count=20&u=&type=2";
  					sUrlCateAll = "0";
  					getCategoryData(sUrlCateAll, INDEX_DATA_ADDNEW);
	    			String sUrlCateAllCount = "http://www.chaoxing.cc/ipad/getdata.aspx?action=hot&padtype=android&page=1&count=20&u=&type=2&getcount=1";
	    			getDataCountOfServer(sUrlCateAllCount, INDEX_CATEGORY_VIEW);
	    			String sUrlCateName = "http://www.chaoxing.cc/ipad/getdata.aspx?action=cate&padtype=android&u=&type=1";
	    			//getCategoryName(sUrlCateName, INDEX_CATEGORY_FIRST);
	    			getCategoryName(0, INDEX_CATEGORY_FIRST);
  				}
  				else if(msg.arg1 == INDEX_LOCAL_VIEW) {
  					displayLocalVideo(false);
  					if(!btnRefresh.isEnabled()) {
  						stopLodingView(msg.arg1);
  						btnRefresh.setEnabled(true);
  					}
  				}
  			}

  		}	
  	};//end handlerBest;
  	
final Handler handlerPageView = new Handler(){
		
		@Override
  		public void handleMessage(Message msg) {
  			// TODO Auto-generated method stub
  			//super.handleMessage(msg);
			int icurPage = pageBtnHost.getSelectedItem();
			Configuration cf= getResources().getConfiguration();
			int ori = cf.orientation;
  			if(msg.what == -1)
  			{
  				Toast.makeText(SsvideoActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
  			}
  			else if(msg.what == INDEX_REMEN_VIEW)
  			{
  				ArrayList<SeriesInfo> listSeries = (ArrayList<SeriesInfo>) msg.obj;
  				try {
  						serListXianFa = listSeries;
  						if(listSeries.size() > 0){
  							getSeriesCovers(listSeries, INDEX_BEST_VIEW, INDEX_REMEN_VIEW, INDEX_BEST_VIEW);
  							pvAdapterXianFa.setList(serListXianFa);
  							pvAdapterXianFa.notifyDataSetChanged();
  							if(icurPage+4 == INDEX_REMEN_VIEW){
  								Message msgP = Message.obtain();
  								msgP.what = ori;
  								msgP.arg1 = INDEX_REMEN_VIEW;
  								handlerOrientation.sendMessage(msgP);
  							}
  						}
  						else
  							stopLodingView(INDEX_REMEN_VIEW);
				} catch (Exception e) {
					// TODO: handle exception
				}
  			}
  			else if(msg.what == INDEX_DASHI_VIEW)
  			{
  				ArrayList<SeriesInfo> listSeries = (ArrayList<SeriesInfo>) msg.obj;
  				try {
  						serListXingFa = listSeries;
  						if(listSeries.size() > 0){
  							getSeriesCovers(listSeries, INDEX_BEST_VIEW, INDEX_DASHI_VIEW, INDEX_BEST_VIEW);
  							pvAdapterXingFa.setList(serListXingFa);
  							pvAdapterXingFa.notifyDataSetChanged();
  							if(icurPage+4 == INDEX_DASHI_VIEW){
  								Message msgP = Message.obtain();
  								msgP.what = ori;
  								msgP.arg1 = INDEX_DASHI_VIEW;
  								handlerOrientation.sendMessage(msgP);
  							}
  						}
  						else
  							stopLodingView(INDEX_DASHI_VIEW);
				} catch (Exception e) {
					// TODO: handle exception
				}
  			}
  			else if(msg.what == INDEX_ZHEXUE_VIEW)
  			{
  				ArrayList<SeriesInfo> listSeries = (ArrayList<SeriesInfo>) msg.obj;
  				try {
  						serListMinShang = listSeries;
  						if(listSeries.size() > 0){
  							getSeriesCovers(listSeries, INDEX_BEST_VIEW, INDEX_ZHEXUE_VIEW, INDEX_BEST_VIEW);
  							pvAdapterMinShang.setList(serListMinShang);
  							pvAdapterMinShang.notifyDataSetChanged();
  							if(icurPage+4 == INDEX_ZHEXUE_VIEW){
  								Message msgP = Message.obtain();
  								msgP.what = ori;
  								msgP.arg1 = INDEX_ZHEXUE_VIEW;
  								handlerOrientation.sendMessage(msgP);
  							}
  						}
  						else
  							stopLodingView(INDEX_ZHEXUE_VIEW);
				} catch (Exception e) {
					// TODO: handle exception
				}
  			}
  			else if(msg.what == INDEX_WENXUE_VIEW)
  			{
  				ArrayList<SeriesInfo> listSeries = (ArrayList<SeriesInfo>) msg.obj;
  				try {
  						serListWenXue = listSeries;
  						if(listSeries.size() > 0){
  							getSeriesCovers(listSeries, INDEX_BEST_VIEW, INDEX_WENXUE_VIEW, INDEX_BEST_VIEW);
  							pvAdapterWenXue.setList(serListWenXue);
  							pvAdapterWenXue.notifyDataSetChanged();
  							if(icurPage+4 == INDEX_WENXUE_VIEW){
  								Message msgP = Message.obtain();
  								msgP.what = ori;
  								msgP.arg1 = INDEX_WENXUE_VIEW;
  								handlerOrientation.sendMessage(msgP);
  							}
  						}
  						else
  							stopLodingView(INDEX_WENXUE_VIEW);
				} catch (Exception e) {
					// TODO: handle exception
				}
  			}
  			else if(msg.what == INDEX_JINGJI_VIEW)
  			{
  				ArrayList<SeriesInfo> listSeries = (ArrayList<SeriesInfo>) msg.obj;
  				try {
  						serListJingJi = listSeries;
  						if(listSeries.size() > 0){
  							getSeriesCovers(listSeries, INDEX_BEST_VIEW, INDEX_JINGJI_VIEW, INDEX_BEST_VIEW);
  							pvAdapterJingJi.setList(serListJingJi);
  							pvAdapterJingJi.notifyDataSetChanged();
  							if(icurPage+4 == INDEX_JINGJI_VIEW){
  								Message msgP = Message.obtain();
  								msgP.what = ori;
  								msgP.arg1 = INDEX_JINGJI_VIEW;
  								handlerOrientation.sendMessage(msgP);
  							}
  						}
  						else
  							stopLodingView(INDEX_JINGJI_VIEW);
				} catch (Exception e) {
					// TODO: handle exception
				}
  			}
  			else if(msg.what == INDEX_LISHI_VIEW)
  			{
  				ArrayList<SeriesInfo> listSeries = (ArrayList<SeriesInfo>) msg.obj;
  				try {
  						serListSuSong = listSeries;
  						if(listSeries.size() > 0){
  							getSeriesCovers(listSeries, INDEX_BEST_VIEW, INDEX_LISHI_VIEW, INDEX_BEST_VIEW);
  							pvAdapterSuSong.setList(serListSuSong);
  							pvAdapterSuSong.notifyDataSetChanged();
  							if(icurPage+4 == INDEX_LISHI_VIEW){
  								Message msgP = Message.obtain();
  								msgP.what = ori;
  								msgP.arg1 = INDEX_LISHI_VIEW;
  								handlerOrientation.sendMessage(msgP);
  							}
  						}
  						else
  							stopLodingView(INDEX_LISHI_VIEW);
				} catch (Exception e) {
				}
  			}
  			else if(msg.what == INDEX_BEST_VIEW)
  			{
  				ArrayList<Bitmap> listBmp = (ArrayList<Bitmap>)msg.obj;
  				if(msg.arg1 == INDEX_REMEN_VIEW){
  					pvAdapterXianFa.setCoverList(listBmp);
  					pvAdapterXianFa.notifyDataSetChanged();
  					stopLodingView(INDEX_REMEN_VIEW);
  				}
  				else if(msg.arg1 == INDEX_DASHI_VIEW){
  					pvAdapterXingFa.setCoverList(listBmp);
  					pvAdapterXingFa.notifyDataSetChanged();
  					stopLodingView(INDEX_DASHI_VIEW);
  				}
  				else if(msg.arg1 == INDEX_ZHEXUE_VIEW){
  					pvAdapterMinShang.setCoverList(listBmp);
  					pvAdapterMinShang.notifyDataSetChanged();
  					stopLodingView(INDEX_ZHEXUE_VIEW);
  				}
  				else if(msg.arg1 == INDEX_WENXUE_VIEW){
  					pvAdapterWenXue.setCoverList(listBmp);
  					pvAdapterWenXue.notifyDataSetChanged();
  					stopLodingView(INDEX_WENXUE_VIEW);
  				}
  				else if(msg.arg1 == INDEX_JINGJI_VIEW){
  					pvAdapterJingJi.setCoverList(listBmp);
  					pvAdapterJingJi.notifyDataSetChanged();
  					stopLodingView(INDEX_JINGJI_VIEW);
  				}
  				else if(msg.arg1 == INDEX_LISHI_VIEW){
  					pvAdapterSuSong.setCoverList(listBmp);
  					pvAdapterSuSong.notifyDataSetChanged();
  					stopLodingView(INDEX_LISHI_VIEW);
  				}
  				
  			}

  		}	
  	};//end handlerPageView;
  	
  	public class CoverHandler extends Handler {
		public static final int COVER_CATEGORY_OK = 0;
		public static final int COVER_BEST_OK     = 1;
		public static final int COVER_REMEN_OK    = 2;
		public static final int COVER_DASHI_OK    = 3;
		public static final int COVER_ZHEXUE_OK   = 4;
		public static final int COVER_JINGJI_OK   = 5;
		public static final int COVER_LISHI_OK    = 6;
		public static final int COVER_WENXUE_OK   = 7;
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case COVER_CATEGORY_OK:
				if (pvAdapterCategory != null) {
//					Log.i(TAG, "category cover ok");
					pvAdapterCategory.notifyDataSetChanged();
				}
				break;
			case COVER_BEST_OK:
				notifyDataSetChanged(msg.arg1);
				break;
			case COVER_LISHI_OK:
//				AlertDialogUtil.alert(SsvideoActivity.this, "��ʾ", "��ȡͼ����Ϣʧ�ܣ���鿴�����Ƿ�������");
				break;
			}
		}
	}
  	
  	private void notifyDataSetChanged(int arg1) {
  		switch (arg1) {
			case CoverHandler.COVER_REMEN_OK:
				if (pvAdapterXianFa != null) {
//					Log.i(TAG, "cover ok remen");
					pvAdapterXianFa.notifyDataSetChanged();
				}
				break;
			case CoverHandler.COVER_DASHI_OK:
				if (pvAdapterXingFa != null) {
//					Log.i(TAG, "cover ok XingFa");
					pvAdapterXingFa.notifyDataSetChanged();
				}
				break;
			case CoverHandler.COVER_ZHEXUE_OK:
				if (pvAdapterMinShang != null) {
//					Log.i(TAG, "cover ok MinShang");
					pvAdapterMinShang.notifyDataSetChanged();
				}
				break;
			case CoverHandler.COVER_JINGJI_OK:
				if (pvAdapterJingJi != null) {
//					Log.i(TAG, "cover ok jingji");
					pvAdapterJingJi.notifyDataSetChanged();
				}
				break;
			case CoverHandler.COVER_LISHI_OK:
				if (pvAdapterSuSong != null) {
//					Log.i(TAG, "cover ok SuSong");
					pvAdapterSuSong.notifyDataSetChanged();
				}
				break;
			case CoverHandler.COVER_WENXUE_OK:
				if (pvAdapterWenXue != null) {
//					Log.i(TAG, "cover ok wenxue");
					pvAdapterWenXue.notifyDataSetChanged();
				}
				break;
		}
  	}

  	private void getDataCountOfServer(final String srcKeyOrUrl, final int viewType){

		// http://www.chaoxing.cc/ipad/getdata.aspx?action=searchtopic&kw= [�ؼ���]&getcount=1
	  Thread thread = new Thread(){
			@Override
			public void run() {
				//super.run();
				try
				{	
//						Log.d(TAG,"get-DataCountOfServer");
						String sTempUrl = srcKeyOrUrl;
			    		if(viewType == INDEX_SEARCH_VIEW){
			    			String skeyTemp = getGBKString(srcKeyOrUrl);
			    			sTempUrl = "http://www.chaoxing.cc/ipad/getdata.aspx?action=searchtopic&padtype=android&getcount=1&kw="+skeyTemp;//searchtopic//modify_by_xin_2013-2-28.
			    			//String srcUrlResult = "http://www.chaoxing.cc/ipad/getdata.aspx?action=searchtopic&count=20&kw="+skeyTemp;
			    		}
			    		
			    		XmlParserFactory xmlParserDataCount = new XmlParserFactory();
			    		xmlParserDataCount.getRequestFile(sTempUrl);
			       	  	int ires = xmlParserDataCount.getResponse();
			       	  	if(ires != 0)
			       	  	{
			       		  if(ires == -1)
			       			ToolsLog.LOG_DBG(TAG+"��ʼ��ʧ�ܣ�- get-DataCountOfServer");
			       		  else if(ires == 1)
			       			ToolsLog.LOG_DBG(TAG+"����ʧ�ܣ�- get-DataCountOfServer");
			       		  else if(ires == 2)
			       			ToolsLog.LOG_DBG(TAG+"���ݳ���Ϊ0 - get-DataCountOfServer");
			       		  else
			       			ToolsLog.LOG_DBG(TAG+"����ʧ�ܣ�- get-DataCountOfServer");   		 
			       		  return;
			       	  	}
			       	    int iCount = xmlParserDataCount.getResultCountPull();
			       	  	//int iCount = mFaYuanData.SeriesInfo.getCount();
			       	   Log.e("wyz", "iCount  = "+iCount);
						Message msgMaxCount = handlerCategory.obtainMessage();
						msgMaxCount.what = INDEX_DATA_COUNT;
						msgMaxCount.arg1 = viewType;
						msgMaxCount.arg2 = iCount;
						msgMaxCount.obj = srcKeyOrUrl;
//						handlerCategory.sendMessage(msgMaxCount);
						//FIXME ���ݼ������� ������ָ����ʾ����Ŀ����һ�£�����᷵�ظ÷����������
						if(viewType == INDEX_CATEGORY_VIEW){
		  					pvAdapterCategory.setMaxDataCount(iCount);
		  				}
		  				else if(viewType == INDEX_SEARCH_VIEW){
		  					sCurrentSearchKey = srcKeyOrUrl;
			  				pvAdapterSearch.setMaxDataCount(iCount);
		  				}				
				}catch(Exception e) {
					e.printStackTrace();
				}
			}	
		};
		thread.start();
		thread = null;
	}//end get-DataCountOfServer;
  	
  	private void getDataCountOfLocal(final String srcKeyOrUrl, final int viewType){
  	
		try
		{
//			Log.d(TAG,"get-DataCountOfLocal");
			int iCount = 0;
			if (viewType == INDEX_SEARCH_VIEW) {
				iCount = mCateLocalDBAdapter.getSeriesCount(srcKeyOrUrl, 1);
			} else {
				iCount = mCateLocalDBAdapter.getSeriesCount(srcKeyOrUrl, 0);
			}

			Message msgMaxCount = handlerCategory.obtainMessage();
			msgMaxCount.what = INDEX_DATA_COUNT;
			msgMaxCount.arg1 = viewType;
			msgMaxCount.arg2 = iCount;
			
			SeriesInfo si = new SeriesInfo();
			si.setSerid(String.valueOf(DATA_FROM_LOCAL));
			si.setTitle(srcKeyOrUrl);
			msgMaxCount.obj = si;//srcKeyOrUrl;
//			handlerCategory.sendMessage(msgMaxCount);
			
			if(viewType == INDEX_CATEGORY_VIEW){
				pvAdapterCategory.setMaxDataCount(iCount);
			}
			else if(viewType == INDEX_SEARCH_VIEW){
				sCurrentSearchKey = srcKeyOrUrl;
				pvAdapterSearch.setMaxDataCount(iCount);
			}
		}
		catch(Exception e) {
		}
  			
  	}//end get-DataCountOfLocal;
      
      class myAsynctask extends AsyncTask<String, Integer, ArrayList<Bitmap>>{ 
      	
      	@Override 
      	protected ArrayList<Bitmap> doInBackground(String... params) { 
      		String sUrl = params[0];
      		initTopView(sUrl);
      		ArrayList<Bitmap> listBmp = new ArrayList<Bitmap>();
      		if(serListTop != null)
      		{
      			int ilen = serListTop.size();
//      			Log.d(TAG, "Top length is:"+ilen);
      			HttpRequest req = new HttpRequest();
      			for(int i=0; i<serListTop.size(); i++) {
      				SeriesInfo info = serListTop.get(i);
      				String surl = info.getCover();
      				req.setRequestUrl(surl);
      				req.getImageRequest();
      				Bitmap bp = req.getBitmapData();
      				listBmp.add(bp);
      			}	
      		}
    		return listBmp;
      	} 

      	@Override 
      	protected void onPostExecute(ArrayList<Bitmap> result) { 
      		if(result != null){
            	gvAdapter.setList(result);
            	mygallery.setAdapter(gvAdapter);
            }
      		stopLodingView(INDEX_GALLERY_VIEW);
      	} 
      	
      	@Override 
      	protected void onProgressUpdate(Integer... values) { 
      		//progress.setProgress(values[0]);
      		super.onProgressUpdate(values); 
      	} 

      	@Override 
      	protected void onPreExecute() { 
      		//progress.setVisibility(View.VISIBLE); 
      		//progress.setProgress(0); 
      		startLodingView(INDEX_GALLERY_VIEW);
      		super.onPreExecute(); 
      	} 
      	    
      }//end myAsynctask;
      
      private void getBestTopInfoView(){
    	  ToolsLog.LOG_DBG(TAG+" : getBestTopInfoView()");
    	  Thread thread = new Thread(){

    		  @Override
    		  public void run() {
    			  //super.run();
    			  try
    			  {	
//    				  Log.d(TAG, "getBestTopInfoView");
    				  boolean blState = getNetIsConnected();
    				  if(!blState) {
    					  Message msgTopData = handlerBest.obtainMessage();
    					  msgTopData.what = 0;
    					  msgTopData.obj = "��������ʧ�ܣ�";
        				 // handlerBest.sendMessage(msgTopData); 
        				  stopLodingView(INDEX_GALLERY_VIEW);
        				  return;
    				  }
    				  //FIXME 			
					  serListTop = mFaYuanData.getHotSearch(6, 0);
					  if(serListTop != null) {
    					  int ilen = serListTop.size();
//    					  Log.d(TAG, "Top6 length is:"+ilen);
    				  }
					  Log.e("wyz", "serListTop  = "+serListTop.size());
    				  Message msgTopData = handlerBest.obtainMessage();
    				  if(serListTop != null && serListTop.size() > 0) {
    					  int ilen = serListTop.size();
//    					  Log.d(TAG, "getBestTopInfoView listBmp length is:"+ilen);
    					  msgTopData.what = INDEX_GALLERY_VIEW;
    					  msgTopData.arg1 = 1;
    					  msgTopData.obj = serListTop;
    				  } else {
    					  msgTopData.what = 0;
    					  msgTopData.obj = "��ȡ��������ʧ�ܣ�";
    				  }
    				  handlerBest.sendMessage(msgTopData);
    			  }
    			  catch(Exception e) {
    			  } 
    			  }	
    		};
    		thread.start();
    		thread = null;
      }//end getBestTopInfoView;     
      
      private void sendStopLoadMessage(int viewType) {
    	  Message msgStop = handlerBest.obtainMessage();
		  msgStop.what = VIEW_LOADING_STOP;
		  msgStop.arg1 = viewType;
		  handlerBest.sendMessage(msgStop); 
      }
      
      private void sendStartLoadMessage(int viewType) {
    	  Message msgStop = handlerBest.obtainMessage();
		  msgStop.what = VIEW_LOADING_START;
		  msgStop.arg1 = viewType;
		  handlerBest.sendMessage(msgStop); 
      }
      
      private void loginState(int state) {
        	String msg = "";
        	if(state == 0) {
        		msg = "��¼�ɹ���";
        		btnLogin.setText("�û���Ϣ");
        		mDialog.dismiss();
        	}
        	else if(state == 1) {
    		   msg = "����ת������";
        	}
        	else if(state == 2) {
    		   msg = "�������û�����";
        	}
        	else if(state == 3) {
    		   msg = "�ӷ�������ȡ�û���Ϣʧ�ܣ�";
        	}
        	else {
    		   msg = "��¼ʧ�ܣ�";
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
	   			mDialog = new Dialog(SsvideoActivity.this);
	   		}
    	  mDialog.setTitle("                                          �û���¼");
	          LayoutInflater factory = LayoutInflater.from(SsvideoActivity.this);
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
//	       		   Toast.makeText(context, "��ѡ����[��¼]", Toast.LENGTH_SHORT).show();
	       		   final Editable eName = edtName.getEditableText(); 
	       		   final Editable ePwd = edtPasswd.getEditableText();
	       		   //tvErrorMsg.setText(eName.toString().trim());
	       		   proBarLogin.setVisibility(View.VISIBLE);

	       		   if(mResponse == 0) {
	       			   Toast.makeText(SsvideoActivity.this, "���ڵ�¼��", Toast.LENGTH_SHORT).show();
	       		   }
	       		   else {
	       			   mResponse = 0;
		       		   new Thread() {
							public void run() {
								int ir = -1;
								mFaYuanData.LoginFaYuan(eName.toString().trim(), ePwd.toString().trim());
							    if (mFaYuanData.userInfo.getUserStatus() == 1) {
							    	ir = 0;
							     } else if (eName.toString() == null) {
							    	ir = 2;
							     }
			                    Message msg = handlerOrientation.obtainMessage();
			                    msg.what = 200;
			                    msg.arg1 = ir;
			                    handlerOrientation.sendMessage(msg);
			                }
		       		   }.start();
	       		   }
	       	   }
	          });
	          
	          mDialog.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface dialog) {
					// TODO Auto-generated method stub
					int rt = WidgetUtil.goFayuanLogin(context, true);
					if(rt != 1) {
//						if(viewFlipHost.getDisplayedChild() == INDEX_ONLINE_VIDEO) {
					
					if (ID_FALGE == 0) {// ���뱾����Ƶ
						setDefaultView(INDEX_DEFAULT_VIEW, true);
					} else if (ID_FALGE == 1) {// ����������
						setDefaultView(INDEX_CATEGORY_VIEW, false);
					}
					// tabLocal.selected = true;
					// tabHost.onFinishInflate();
//						}
					}
				}
			});
	     		
//          Button btnRegister = (Button) DialogView.findViewById(R.id.btnregister);
//          btnRegister.setVisibility(View.GONE);
     		
          Button btnCancel = (Button) DialogView.findViewById(R.id.btncancel);
          btnCancel.setOnClickListener(new OnClickListener() {
   			
       	   @Override
       	   public void onClick(View v) {
       		Toast.makeText(SsvideoActivity.this, "��ѡ����[ȡ��]", Toast.LENGTH_SHORT).show();
       		mDialog.dismiss();
       	   }
          });
          mDialog.show();
	}//end CreateLoginFayuanDialog;
      
      private void CreateUserInfoDialog() {
   	   final Dialog dialog = new Dialog(SsvideoActivity.this);
          dialog.setTitle("                                          �û���Ϣ");
          LayoutInflater factory = LayoutInflater.from(SsvideoActivity.this);
          final View DialogView = factory.inflate(R.layout.userinfo_dialog, null);
          dialog.setContentView(DialogView);
          
          final TextView tvErrorMsg =  (TextView) DialogView.findViewById(R.id.txt_errormsg);
          final TextView tvName =  (TextView) DialogView.findViewById(R.id.txt_name);
          final TextView tvState =  (TextView) DialogView.findViewById(R.id.txt_state);
          final TextView tvType =  (TextView) DialogView.findViewById(R.id.txt_type);
          final TextView tvCoin =  (TextView) DialogView.findViewById(R.id.txt_coin);
          final ProgressBar proBarLogout = (ProgressBar) DialogView.findViewById(R.id.progressBarLogout);
     		
          if(mFaYuanData.userInfo != null)
     	  {
//     		  Log.d(TAG, "m_userinfo is:"+m_userinfo.getUserStatus()+","+m_userinfo.getCoinsCount()+
//     				  ","+m_userinfo.getUsertype()+","+m_userinfo.getMenberType());
     		  tvName.setText(mFaYuanData.userInfo.getUserName());
     		  if(mFaYuanData.userInfo.getUserStatus() == 1)
     			  tvState.setText("����");
     		  else
     			  tvState.setText("����");  
     		 // tvType.setText(m_userinfo.getUsertype());
     		 //tvCoin.setText(m_userinfo.getCoinsCount()+"");
     	  } 
          else{
        	  tvErrorMsg.setText("��ȡ�û���Ϣʧ�ܣ�");
          }
          
          Button btnRegister = (Button) DialogView.findViewById(R.id.btnlogout);
          btnRegister.setOnClickListener(new OnClickListener() {
   			
       	   @Override
       	   public void onClick(View v) {
       		   Toast.makeText(SsvideoActivity.this, "��ѡ����[ע��]", Toast.LENGTH_SHORT).show();
       		   proBarLogout.setVisibility(View.VISIBLE);
       		   dialog.dismiss();
       		   mFaYuanData.setFaYuanFlage(false);
//       	   m_userinfo.initUserInfo();
       		   //m_userinfo = null;
       		   btnLogin.setText("��¼");
       	   }
          });
     		
          Button btnCancel = (Button) DialogView.findViewById(R.id.btncancel);
          btnCancel.setOnClickListener(new OnClickListener() {
   			
       	   @Override
       	   public void onClick(View v) {
       		   Toast.makeText(SsvideoActivity.this, "��ѡ����[ȡ��]", Toast.LENGTH_SHORT).show();
       		   dialog.dismiss();
       	   }
          });
          dialog.show();

      }//end CreateUserInfoDialog;

  	public class DownloadBroadcastReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals("com.chaoxing.download.DownloadBroadcastReceiver")){
				Bundle bundle = intent.getExtras();
				if(bundle.getInt("type") == 0){
					String threadId = bundle.getString("threadId");
					int status = bundle.getInt("status");
					int progress = bundle.getInt("progress");
					int fileLen = bundle.getInt("fileLen");
//					Log.i(TAG, "threadId+++++++++++++++++++++++++ " + threadId);
//					Log.i(TAG, "broadcast receiver download progress---------------------->" + progress);
					if(listLocalVideo != null){
						if(listLocalVideo.isEmpty() == true)
							return ;
						if(downloadingVector != null){
//							Log.i(TAG, "broadcast receiver downloadingVector size---------------->" + downloadingVector.size());
							
							for(Map<String, Object> map : downloadingVector){
								if(map.containsValue(threadId)){
//									Log.i(TAG, "video download status ------------------> " + map.get("status"));
									if((Integer)map.get("status") == 2)
										break;
									map.put("progress", progress);
									map.put("fileLen", fileLen);
									map.put("speed", bundle.getInt("speed"));
									map.put("status", status);
//									Log.i(TAG, "broadcast receiver download progress---------------------->" + progress);
									if(status == 0){
										downloadingVector.remove(map);
										if(downloadingVector.isEmpty() == true){
											Intent serviceIntent = new Intent("com.chaoxing.download.FileService");
											stopService(serviceIntent);
										}
									}
									adapter.notifyDataSetChanged();
									break;
								}
							}
						}
					}
				}else if(bundle.getInt("type") == 1){
					Toast.makeText(SsvideoActivity.this, bundle.getString("msg"), Toast.LENGTH_LONG).show();
				}
			}			
		}
	}//end DownloadBroadcastReceiver;
  	
  	private void setCoverLenth(ImageView img, String sStr) {
	   	int iLen = sStr.length();
	   	if(iLen < 2) iLen = 2;
		ViewGroup.LayoutParams paramsI = (ViewGroup.LayoutParams) img.getLayoutParams();
		paramsI.width = iLen*25;;
		img.setLayoutParams(paramsI);
	}//end setCoverLenth;

  	private void initFirstLevelCategoryImg(ArrayList<CategoryNameInfo> cateInfo, int level) {
  	ToolsLog.LOG_DBG(TAG+ "level = "+level);
  	  if(level > 3) return;
 	   if(cateInfo != null && cateInfo.size() > 0) {
 		   LayoutInflater mInflater = (LayoutInflater) SsvideoActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 		   LinearLayout categoryButtonsLayout = null;// (LinearLayout)findViewById(R.id.layout_cateNamefirstlevel);
 		   if(level == 1){
				categoryButtonsLayout = (LinearLayout)findViewById(R.id.layout_cateNamefirstlevel);
 			   layoutCateScrollBar1.setVisibility(View.VISIBLE);
 			   layoutCateScrollBar2.setVisibility(View.GONE);
 			   layoutCateScrollBar3.setVisibility(View.GONE);
 		   }
 		   else if(level == 2) {
 			   categoryButtonsLayout = (LinearLayout)findViewById(R.id.layout_cateNamesecondlevel);
 			   layoutCateScrollBar2.setVisibility(View.VISIBLE);
 			   layoutCateScrollBar3.setVisibility(View.GONE);
 		   }
 		   else if(level == 3) {
 			   categoryButtonsLayout = (LinearLayout)findViewById(R.id.layout_cateNamethirdlevel);
 			   layoutCateScrollBar3.setVisibility(View.VISIBLE);
 		   }      	   					
 		   categoryButtonsLayout.removeAllViewsInLayout();
 		   categoryButtonClickListener listener = new categoryButtonClickListener();
 		   
    			for(int i=0; i<=cateInfo.size(); i++) {
    				if(i == 0) {
    					
    					if(level == 1){
    						View convertView = mInflater.inflate(R.layout.categorynamecell, null);
    						ImageView videoCover = (ImageView)convertView.findViewById(R.id.cate_cover);
    						TextView vieoName = (TextView)convertView.findViewById(R.id.txt_name);
    						videoCover.setId(i);
    						videoCover.setTag(level);
    						vieoName.setText("ȫ��");
    						setCoverLenth(videoCover, "ȫ��");
    						videoCover.setBackgroundResource(R.drawable.all_categorycell_selected);
    						videoCover.setOnClickListener(listener);
    						categoryButtonsLayout.addView(convertView);
    	   				
    	   					categoryNameVectorView1.clear();
    	   					lastSelectedImg1 = 0;
    	   					categoryNameVectorView1.add(videoCover);
    	   				}
    	   				else if(level == 2) {
    	   					categoryNameVectorView2.clear();
    	   					lastSelectedImg2 = 1;
    	   				}
    	   				else if(level == 3) {
    	   					categoryNameVectorView3.clear();
    	   					lastSelectedImg3 = 1;
    	   				}      	   					
    	   				continue;
    				}
    			
    				CategoryNameInfo info = cateInfo.get(i-1);
    				if(info != null) {
    					View convertView = mInflater.inflate(R.layout.categorynamecell, null);
    					ImageView videoCover = (ImageView)convertView.findViewById(R.id.cate_cover);
    					TextView vieoName = (TextView)convertView.findViewById(R.id.txt_name);
    					videoCover.setBackgroundResource(R.drawable.ranks_view_subject_back);
    					
    					vieoName.setText(info.getTitle());    					
    					setCoverLenth(videoCover, info.getTitle());
    					videoCover.setId(i);
    					videoCover.setTag(level);
    					videoCover.setOnClickListener(listener);
    					categoryButtonsLayout.addView(convertView);
    					if(level == 1){
     	   					categoryNameVectorView1.add(videoCover);
     	   				}
     	   				else if(level == 2) {
     	   					categoryNameVectorView2.add(videoCover);
     	   				}
     	   				else if(level == 3) {
     	   					categoryNameVectorView3.add(videoCover);
     	   				}
    				}
    			}//end for i;
    		}
 	   		else {
 	   			if(level == 1){
 	   				layoutCateScrollBar1.setVisibility(View.GONE);
 	   			}
 	   			else if(level == 2) {
 	   				layoutCateScrollBar2.setVisibility(View.GONE);
 	   			}
 	   			else if(level == 3) {
 	   				layoutCateScrollBar3.setVisibility(View.GONE);
 	   			}      	   					
 	   		}
    }//end initFirstLevelCategoryImg;
  	
    private class categoryButtonClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			//Toast.makeText(SsvideoActivity.this, "��ѡ����[id="+v.getId()+"]/tag="+v.getTag(), Toast.LENGTH_SHORT).show();
			int selIndex = v.getId();
			int selLevel = (Integer)v.getTag();
			int rtVal = setSelectedImg(selIndex, selLevel);
			if(rtVal != 0){
				ToolsLog.LOG_DBG("ѡ�����  : "+ "��ѡ����["+v.getTag()+"������]/��["+v.getId()+"]��");
				return;
			}
			if(selLevel == 1) {
				if(cateNameListFirst != null) {
					boolean blState = getNetIsConnected();
  				  	if(!blState) {
	  				  	if(selIndex == 0) {
							layoutCateScrollBar2.setVisibility(View.GONE);
							layoutCateScrollBar3.setVisibility(View.GONE);
							getCategoryDataLocal();
						}
						else {
							CategoryNameInfo cniFirst = cateNameListFirst.get(selIndex-1);
//							String sMsg = "1��Local������ID="+cniFirst.getCateId()+"����������="+cniFirst.getTitle()+"�����༶��="+cniFirst.getLevel()+"��������="+cniFirst.getParentId()+"���ӷ���="+cniFirst.getHasChild()+"\r\n";
//							Log.d("ѡ�����", sMsg);
							sendStartLoadMessage(INDEX_CATEGORY_VIEW);//startLoadingViewThread(INDEX_CATEGORY_VIEW);
							if(cniFirst.getHasChild().equals("1")) {
								getCategoryNameFromLocal(cniFirst.getCateId(), INDEX_CATEGORY_SECOND);
							}
							else {
								layoutCateScrollBar2.setVisibility(View.GONE);
								layoutCateScrollBar3.setVisibility(View.GONE);
							}
							getDataCountOfLocal(cniFirst.getCateId(), INDEX_CATEGORY_VIEW);
							getCategoryDataFromLocal(cniFirst.getCateId(), 1, INDEX_DATA_ADDNEW);
						}
	  				  	
  				  	}
  				  	else {
						String sUrlCate = "http://www.chaoxing.cc/ipad/getdata.aspx?action=hot&padtype=android&page=1&count=20&u=&type=2";
						CategoryNameInfo cniFirst = null;
						if(selIndex == 0) {
							sUrlCate = "http://www.chaoxing.cc/ipad/getdata.aspx?action=hot&padtype=android&page=1&count=20&u=&type=2";
							layoutCateScrollBar2.setVisibility(View.GONE);
							layoutCateScrollBar3.setVisibility(View.GONE);
						}
						else {
							cniFirst = cateNameListFirst.get(selIndex-1);
//							String sMsg = "1��������ID="+cniFirst.getCateId()+"����������="+cniFirst.getTitle()+"�����༶��="+cniFirst.getLevel()+"��������="+cniFirst.getParentId()+"���ӷ���="+cniFirst.getHasChild()+"\r\n";
//							Log.d("ѡ�����", sMsg);
							sUrlCate = "http://www.chaoxing.cc/ipad/getdata.aspx?action=searchtopic&padtype=android&count=20&page=1&cate="+cniFirst.getCateId();
							//FIXME
							sendStartLoadMessage(INDEX_CATEGORY_VIEW);
							getCategoryName(Integer.valueOf(cniFirst.getCateId()), INDEX_CATEGORY_SECOND);

							if(cniFirst.getHasChild().equals("1")) {
								sendStartLoadMessage(INDEX_CATEGORY_VIEW);//startLoadingViewThread(INDEX_CATEGORY_VIEW);
								String sSubUrl = "http://www.chaoxing.cc/ipad/getdata.aspx?action=cate&padtype=android&cate="+cniFirst.getCateId();
								//getCategoryName(sSubUrl, INDEX_CATEGORY_SECOND);
								getCategoryName(sSubUrl, INDEX_CATEGORY_SECOND);
							}
							else {
								layoutCateScrollBar2.setVisibility(View.GONE);
								layoutCateScrollBar3.setVisibility(View.GONE);
							}
							
						}
		      			
						sendStartLoadMessage(INDEX_CATEGORY_VIEW);//startLoadingViewThread(INDEX_CATEGORY_VIEW);
						getDataCountOfServer(sUrlCate + "&getcount=1", INDEX_CATEGORY_VIEW);
						if (v.getId() == 0) {
							getCategoryData("0", INDEX_DATA_ADDNEW);
						} else {
							getCategoryData(cniFirst.getCateId(), INDEX_DATA_ADDNEW);
						}
						
					}
	      		}
	      	}
	      	else if(selLevel == 2) {
	      		if(cateNameListSecond != null)
	      		{
	      			boolean blState = getNetIsConnected();
  				  	if(!blState) {
							CategoryNameInfo cniSecond = cateNameListSecond.get(selIndex-1);
//			      			String sUrlCateSub = "http://www.chaoxing.cc/ipad/getdata.aspx?action=searchtopic&count=20&page=1&cate="+cniSecond.getCateId();
			      			if(cniSecond.getHasChild().equals("1"))
			      			{
//								String sMsg = "2��Local������ID="+cniSecond.getCateId()+"����������="+cniSecond.getTitle()+"�����༶��="+cniSecond.getLevel()+"��������="+cniSecond.getParentId()+"���ӷ���="+cniSecond.getHasChild()+"\r\n";
//								Log.d("ѡ�����", sMsg);
								String sHasChild = cniSecond.getHasChild();
								
								if(sHasChild.equals("1"))// && cateNameListThird == null)
								{
									sendStartLoadMessage(INDEX_CATEGORY_VIEW);//startLoadingViewThread(INDEX_CATEGORY_VIEW);
//									String sSubUrl = "http://www.chaoxing.cc/ipad/getdata.aspx?action=cate&cate="+cniSecond.getCateId();
//									getCategoryName(sSubUrl, INDEX_CATEGORY_THIRD);
									getCategoryNameFromLocal(cniSecond.getCateId(), INDEX_CATEGORY_THIRD);
								}
								else {
									layoutCateScrollBar3.setVisibility(View.GONE);
								}
								
			      			}
			      			sendStartLoadMessage(INDEX_CATEGORY_VIEW);//startLoadingViewThread(INDEX_CATEGORY_VIEW);
//			      			getDataCountOfServer(sUrlCateSub + "&getcount=1", INDEX_CATEGORY_VIEW);
//			      			getCategoryData(sUrlCateSub, INDEX_DATA_ADDNEW);
			      			getDataCountOfLocal(cniSecond.getCateId(), INDEX_CATEGORY_VIEW);
							getCategoryDataFromLocal(cniSecond.getCateId(), 1, INDEX_DATA_ADDNEW);
			      			
  				  	}
  				  	else {
		      			CategoryNameInfo cniSecond = cateNameListSecond.get(selIndex-1);
		      			String sUrlCateSub = "http://www.chaoxing.cc/ipad/getdata.aspx?action=searchtopic&padtype=android&count=20&page=1&cate="+cniSecond.getCateId();
		      			sendStartLoadMessage(INDEX_CATEGORY_VIEW);
						getCategoryName(Integer.valueOf(cniSecond.getCateId()), INDEX_CATEGORY_THIRD);
		      			
		      			if(cniSecond.getHasChild().equals("1"))
		      			{
//							String sMsg = "2��������ID="+cniSecond.getCateId()+"����������="+cniSecond.getTitle()+"�����༶��="+cniSecond.getLevel()+"��������="+cniSecond.getParentId()+"���ӷ���="+cniSecond.getHasChild()+"\r\n";
//							Log.d("ѡ�����", sMsg);
							String sHasChild = cniSecond.getHasChild();
							if(sHasChild.equals("1"))// && cateNameListThird == null)
							{
								sendStartLoadMessage(INDEX_CATEGORY_VIEW);//startLoadingViewThread(INDEX_CATEGORY_VIEW);
								String sSubUrl = "http://www.chaoxing.cc/ipad/getdata.aspx?action=cate&padtype=android&cate="+cniSecond.getCateId();
								getCategoryName(sSubUrl, INDEX_CATEGORY_THIRD);
							}
							else {
								layoutCateScrollBar3.setVisibility(View.GONE);
							}
							
		      			}
		      			sendStartLoadMessage(INDEX_CATEGORY_VIEW);//startLoadingViewThread(INDEX_CATEGORY_VIEW);
		      			getDataCountOfServer(sUrlCateSub + "&getcount=1", INDEX_CATEGORY_VIEW);
		      			getCategoryData(cniSecond.getCateId(), INDEX_DATA_ADDNEW);
  				  	}
	      		}
	      	}
	      	else if(selLevel == 3) {
	      		if(cateNameListThird != null)
	      		{
	      			boolean blState = getNetIsConnected();
  				  	if(!blState) {
	  				  	CategoryNameInfo cniThird = cateNameListThird.get(selIndex-1);
//		      			String sMsg = "3��Local������ID="+cniThird.getCateId()+"����������="+cniThird.getTitle()+"�����༶��="+cniThird.getLevel()+"��������="+cniThird.getParentId()+"���ӷ���="+cniThird.getHasChild()+"\r\n";
//		      			Log.d("ѡ�����", sMsg);
		      			sendStartLoadMessage(INDEX_CATEGORY_VIEW);//startLoadingViewThread(INDEX_CATEGORY_VIEW);
//		      		 	String sUrlCateSubThird = "http://www.chaoxing.cc/ipad/getdata.aspx?action=searchtopic&count=20&page=1&cate="+cniThird.getCateId();
//		      		 	getDataCountOfServer(sUrlCateSubThird + "&getcount=1", INDEX_CATEGORY_VIEW);
//		      		 	getCategoryData(sUrlCateSubThird, INDEX_DATA_ADDNEW);
		      		 	getDataCountOfLocal(cniThird.getCateId(), INDEX_CATEGORY_VIEW);
						getCategoryDataFromLocal(cniThird.getCateId(), 1, INDEX_DATA_ADDNEW);
  				  	}
  				  	else {
		      			CategoryNameInfo cniThird = cateNameListThird.get(selIndex-1);
//		      			String sMsg = "3��������ID="+cniThird.getCateId()+"����������="+cniThird.getTitle()+"�����༶��="+cniThird.getLevel()+"��������="+cniThird.getParentId()+"���ӷ���="+cniThird.getHasChild()+"\r\n";
//		      			Log.d("ѡ�����", sMsg);
		      			sendStartLoadMessage(INDEX_CATEGORY_VIEW);//startLoadingViewThread(INDEX_CATEGORY_VIEW);
		      		 	String sUrlCateSubThird = "http://www.chaoxing.cc/ipad/getdata.aspx?action=searchtopic&padtype=android&count=20&page=1&cate="+cniThird.getCateId();
		      		 	getDataCountOfServer(sUrlCateSubThird + "&getcount=1", INDEX_CATEGORY_VIEW);
		      		 	getCategoryData(cniThird.getCateId(), INDEX_DATA_ADDNEW);
  				  	}
	      		}
	      	}
			
		}
    }//end categoryButtonClickListener;
    //modify by yongzheng for fix tab not responding 2013/04/28
	public int setSelectedImg(int idx, int ilevel) {
		int rtVal = -1;
		if (ilevel == 1 && lastSelectedImg1 != idx) {
			categoryNameVectorView1.get(lastSelectedImg1).setBackgroundResource(R.drawable.ranks_view_subject_back);// all_category_button_xml
			lastSelectedImg1 = idx;
			categoryNameVectorView1.get(idx).setBackgroundResource(R.drawable.all_categorycell_selected);
			rtVal = 0;
		} else if (ilevel == 2 && lastSelectedImg2 != idx) {
			categoryNameVectorView2.get(lastSelectedImg2-1).setBackgroundResource(R.drawable.ranks_view_subject_back);// all_category_button_xml
			lastSelectedImg2 = idx;
			categoryNameVectorView2.get(idx-1).setBackgroundResource(R.drawable.all_categorycell_selected);
			rtVal = 0;
		} else if (ilevel == 2 && 1 == idx) {
			categoryNameVectorView2.get(lastSelectedImg2-1).setBackgroundResource(R.drawable.ranks_view_subject_back);// all_category_button_xml
			lastSelectedImg2 = idx;
			categoryNameVectorView2.get(idx-1).setBackgroundResource(R.drawable.all_categorycell_selected);
			rtVal = 0;
		} else if (ilevel == 3 && lastSelectedImg3 != idx) {
			categoryNameVectorView3.get(lastSelectedImg3-1).setBackgroundResource(R.drawable.ranks_view_subject_back);// all_category_button_xml
			lastSelectedImg3 = idx;
			categoryNameVectorView3.get(idx-1).setBackgroundResource(R.drawable.all_categorycell_selected);
			rtVal = 0;
		}else if (ilevel == 3 && 1 == idx) {
			categoryNameVectorView3.get(lastSelectedImg3-1).setBackgroundResource(R.drawable.ranks_view_subject_back);// all_category_button_xml
			lastSelectedImg3 = idx;
			categoryNameVectorView3.get(idx-1).setBackgroundResource(R.drawable.all_categorycell_selected);
			rtVal = 0;
		}
		return rtVal;
	}// end setSelectedImg;
   
    /*isAdd=1, is Add Data; isAdd=0 is replace Data*/
    private void getCategoryData(final String url, final int isAdd)  {
    	// http://www.chaoxing.cc/ipad/getdata.aspx?action=searchtopic&kw= [�ؼ���]&getcount=1	 
	  Thread thread = new Thread(){
		  @Override
		  public void run() {
			  //super.run();
			  try {	
				  //FIXME
					  char[] ch = url.toCharArray();
						 StringBuffer sb = new StringBuffer();
						 sb.append(ch).reverse();
					     ArrayList<SeriesInfo> serListCate  = null;
						if (sb.length() >= 2) {	
							String	str = sb.substring(0, 2);
							//serListCate = mFaYuanData.getClassifyInfo(Integer.valueOf(url), Integer.valueOf(str), 10, 0);
							serListCate = mFaYuanData.getClassifyInfo(Integer.valueOf(url), 1, 10, 0);
						} else {
							//serListCate = mFaYuanData.getClassifyInfo(Integer.valueOf(url), Integer.valueOf(url), 10, 0);
							serListCate = mFaYuanData.getClassifyInfo(Integer.valueOf(url), 1, 10, 0);
						}
					
		    			Message msgData = handlerCategory.obtainMessage();
		     			if (serListCate != null) {
		     				msgData.what = INDEX_DATA_CATEGORY;
		     				msgData.arg1 = isAdd;
		     				msgData.arg2 = DATA_FROM_NETWORK;
		     				msgData.obj = serListCate;
		     			} else {
		     				msgData.what = 0;
		     				msgData.obj = "��ȡ��������ʧ�ܣ�";
		     			}
		     			handlerCategory.sendMessage(msgData);
			  }
			  catch(Exception e) {
			  }
			}	
		};
		thread.start();
		thread = null;
	}//end get-CategoryData;
    
	private void getCategoryDataFromLocal(final String serID, final int index, final int isAdd) {
		try {
			// Log.d(TAG, "get-CategoryData");
			Message msgData = handlerCategory.obtainMessage();
			ArrayList<SeriesInfo> serListCate = mCateLocalDBAdapter
					.getSeriesInfoIndex(serID, index);
			if (serListCate != null) {
				int ilen = serListCate.size();
				ToolsLog.LOG_DBG(TAG+"get-CategoryData serListCate length is:" + ilen);
				msgData.what = INDEX_DATA_CATEGORY;
				msgData.arg1 = isAdd;
				msgData.arg2 = DATA_FROM_LOCAL;
				msgData.obj = serListCate;
			} else {
				msgData.what = 0;
				msgData.obj = "��ȡ��������ʧ�ܣ�";
				stopLodingView(INDEX_CATEGORY_VIEW);
			}
			handlerCategory.sendMessage(msgData);
		} catch (Exception e) {
		}

	}//end get-CategoryData;
    //FIXME
	ArrayList<CategoryNameInfo> listCateName = null;
    private void getCategoryName(final int categoryId, final int level) {
    	Thread thread = new Thread(){
		  @Override
		  public void run() {
			  try
			  {		  
					Message msgNameData = handlerCategory.obtainMessage();
					
					if (level == INDEX_CATEGORY_SECOND) {
						listCateName = mFaYuanData.getChildrenClassifyInfo(categoryId, 1, 0);
					} else if (level == INDEX_CATEGORY_THIRD) {
						listCateName = mFaYuanData.getChildrenClassifyInfo(categoryId, 2, 0);
					} else {
						listCateName = mFaYuanData.getChildrenClassifyInfo(categoryId, 0, 0);
					}
				  if(listCateName != null){
					  int ilen = listCateName.size();
					  ToolsLog.LOG_DBG(TAG+ "get-CategoryName serListCate length is:"+ilen);
					  msgNameData.what = INDEX_CATEGORY_NAME;
					  msgNameData.arg1 = level;
					  msgNameData.obj = listCateName;
				  }
				  else{
					  msgNameData.what = 0;
					  msgNameData.obj = "��ȡ������������ʧ�ܣ�";
				  }
				  handlerCategory.sendMessage(msgNameData);  
			  }
			  catch(Exception e) {
			  }
			}	
		};
		thread.start();
		thread = null;
    }

	private void getCategoryName(final String url, final int ilevel) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				// super.run();
				try {
					// Log.d(TAG, "get-CategoryName");
					// String sUrl =
					// "http://www.chaoxing.cc/ipad/getdata.aspx?action=cate&u=&type=1";
					XmlParserFactory xmlParserTop = new XmlParserFactory();
					xmlParserTop.getRequestFile(url);
					int ires = xmlParserTop.getResponse();
					if (ires != 0) {
						if (ires == -1)
							ToolsLog.LOG_DBG(TAG+ "��ʼ��ʧ�ܣ�-get-CategoryName");
						else if (ires == 1)
							ToolsLog.LOG_DBG(TAG+ "����ʧ�ܣ�-get-CategoryName");
						else if (ires == 2)
							ToolsLog.LOG_DBG(TAG+ "���ݳ���Ϊ0-get-CategoryName");
						else
							ToolsLog.LOG_DBG(TAG+ "����ʧ�ܣ�-get-CategoryName");

						// getCategoryNameFromLocal("", ilevel);
						return;
					}

					Message msgNameData = handlerCategory.obtainMessage();
					ArrayList<CategoryNameInfo> listCateName  = xmlParserTop.getCategoryNameInfoListtPull();
					
					if (listCateName != null) {
						int ilen = listCateName.size();
						ToolsLog.LOG_DBG(TAG+ "get-CategoryName serListCate length is:"
								+ ilen);
						msgNameData.what = INDEX_CATEGORY_NAME;
						msgNameData.arg1 = ilevel;
						msgNameData.obj = listCateName;
					} else {
						msgNameData.what = 0;
						msgNameData.obj = "��ȡ������������ʧ�ܣ�";
					}
					handlerCategory.sendMessage(msgNameData);
				} catch (Exception e) {
				}
			}
		};
		thread.start();
		thread = null;
	}//end get-CategoryName;
    
	private void getCategoryDataLocalThread() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				// super.run();
				try {
					// Log.d(TAG, "get-CategoryDataLocal");
					getCategoryNameFromLocal("", INDEX_CATEGORY_FIRST);
					getCategoryDataFromLocal("", 1, INDEX_DATA_ADDNEW);
					getDataCountOfLocal("", INDEX_CATEGORY_VIEW);
				} catch (Exception e) {
				}
			}
		};
		thread.start();
		thread = null;
	}// end get-CategoryName;
    
	private void getCategoryDataLocal() {
		try {
			// Log.d(TAG, "get-CategoryDataLocal");
			getCategoryNameFromLocal("", INDEX_CATEGORY_FIRST);
			getCategoryDataFromLocal("", 1, INDEX_DATA_ADDNEW);
			getDataCountOfLocal("", INDEX_CATEGORY_VIEW);
		} catch (Exception e) {
		}

	}// end get-CategoryName;
	/**
	 * �ӱ��ػ�ȡ��Ƶ��������
	 * @param sParent
	 * @param ilevel
	 */
	private void getCategoryNameFromLocal(final String sParent, final int ilevel) {
		try {
			int lvl = ilevel % 20 + 1;
			Message msgNameData = handlerCategory.obtainMessage();
			ArrayList<CategoryNameInfo> listCateName = mCateLocalDBAdapter.getCategoryNameOfLevel(sParent, lvl);
			if (listCateName != null) {
				int ilen = listCateName.size();
				ToolsLog.LOG_DBG(TAG + "get-CategoryName serListCate length is:" + ilen);
				msgNameData.what = INDEX_CATEGORY_NAME;
				msgNameData.arg1 = ilevel;
				msgNameData.arg2 = DATA_FROM_LOCAL;
				msgNameData.obj = listCateName;
			} else {
				msgNameData.what = 0;
				msgNameData.obj = "��ȡ������������ʧ�ܣ�";
				stopLodingView(INDEX_CATEGORY_VIEW);
			}
			handlerCategory.sendMessage(msgNameData);
		} catch (Exception e) {
		}

	}// end get-CategoryName;
    /**
     * ��ȡ��Ƶ����ͼƬ
     * @param serList
     * @param adapterType
     * @param arg1
     * @param handlerType
     */
	private void getSeriesCovers(final ArrayList<SeriesInfo> serList, final int adapterType, final int arg1, final int handlerType) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					ArrayList<Bitmap> listBmp = new ArrayList<Bitmap>();
					if (serList != null) {
						int ilen = serList.size();
						ToolsLog.LOG_DBG(TAG + "serList length is:" + ilen);
						listBmp.clear();
						HttpRequest req = new HttpRequest();
						for (int i = 0; i < serList.size(); i++) {
							updateLoadingViewThread(handlerType);
							SeriesInfo info = serList.get(i);
							if (info.getSerid().equals("more")) {
								continue;
							}
							String surl = info.getCover();
							Bitmap bp = req.getImageUrlRequest(surl);
							if (bp != null)
								listBmp.add(bp);
							else {
								listBmp.add(null);
								ToolsLog.LOG_DBG("��������Ϊ�� : " + "SerId=" + info.getSerid() + "Title=" + info.getTitle() + "CoverUrl=" + surl);
							}
						}
					}

					Message msgNameData = null;
					if (handlerType == INDEX_CATEGORY_VIEW || handlerType == INDEX_SEARCH_VIEW) {
						msgNameData = handlerCategory.obtainMessage();
					} else if (handlerType == INDEX_GALLERY_VIEW) {
						msgNameData = handlerBest.obtainMessage();
					} else if (handlerType == INDEX_BEST_VIEW) {
						msgNameData = handlerPageView.obtainMessage();
					} else {
						msgNameData = handlerCategory.obtainMessage();
					}

					if (listBmp != null && listBmp.size() > 0) {
						int ilen = listBmp.size();
						ToolsLog.LOG_DBG(TAG + "getSeriesCovers listBmp length is:" + ilen);
						msgNameData.what = adapterType;
						msgNameData.arg1 = arg1;
						msgNameData.obj = listBmp;
					} else {
						msgNameData.what = 0;
						msgNameData.obj = "��ȡ��������ʧ�ܣ�";
					}

					if (handlerType == INDEX_CATEGORY_VIEW || handlerType == INDEX_SEARCH_VIEW) {
						handlerCategory.sendMessage(msgNameData);
					} else if (handlerType == INDEX_GALLERY_VIEW) {
						handlerBest.sendMessage(msgNameData);
					} else if (handlerType == INDEX_BEST_VIEW) {
						handlerPageView.sendMessage(msgNameData);
					}
				} catch (Exception e) {
				}
			}
		};
		thread.start();
		thread = null;
	}// end getSeriesCovers;
    /**
     * ת���ַ�����ΪGBK��ʽ
     * @param str
     * @return
     */
	private String getGBKString(String str) {
		String sTemp = str;
		try {
			sTemp = URLEncoder.encode(str, "GBK");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		return sTemp;
	}// end getGBKString;

	/**
	 * ���ݼ���������ȥ��Ƶ
	 * @param sKey
	 * @param itype
	 * @param ipage
	 * @param isGetCount
	 */
	private void getSearchData(final String sKey, final int itype, final int ipage, final boolean isGetCount) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				if (isGetCount) {
					getDataCountOfServer(sKey, INDEX_SEARCH_VIEW);
				}
				try {
					ArrayList<SeriesInfo> listSearch = mFaYuanData.getSearch(sKey, 12, 0);
					Message msgSrcData = handlerCategory.obtainMessage();
					if (listSearch != null && listSearch.size() > 0) {
						int ilen = listSearch.size();
						ToolsLog.LOG_DBG(TAG + "get-SearchData listBmp length is:" + ilen);
						msgSrcData.what = INDEX_DATA_SEARCH;
						msgSrcData.arg1 = itype;
						msgSrcData.arg2 = DATA_FROM_NETWORK;
						msgSrcData.obj = listSearch;
					} else {
						msgSrcData.what = 0;
						msgSrcData.obj = "û�м���������";// "��ȡ��������ʧ�ܣ�";
						sendStopLoadMessage(INDEX_SEARCH_VIEW);// stopLoadingViewThread(INDEX_SEARCH_VIEW);
					}
					handlerCategory.sendMessage(msgSrcData);
				} catch (Exception e) {
				}
			}
		};
		thread.start();
		thread = null;
	}// end get-SearchData;
	/**
	 * ��ȡ������Ƶ��Ϣ
	 * @param sKey
	 * @param itype
	 * @param isGetCount
	 */
	private void getSearchDataLocal(final String sKey, final int itype,	final boolean isGetCount) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				// super.run();
				if (isGetCount) {
					getDataCountOfLocal(sKey, INDEX_SEARCH_VIEW);
				}
				try {
					// Log.d(TAG, "get-SearchDataLocal");
					Message msgSrcData = handlerCategory.obtainMessage();
					ArrayList<SeriesInfo> listSearch = mCateLocalDBAdapter
							.getSearchInfoOfKey(sKey);
					if (listSearch != null && listSearch.size() > 0) {
						int ilen = listSearch.size();
						ToolsLog.LOG_DBG(TAG + "get-SearchDataLocal listBmp length is:"	+ ilen);
						msgSrcData.what = INDEX_DATA_SEARCH;
						msgSrcData.arg1 = itype;
						msgSrcData.arg2 = DATA_FROM_LOCAL;
						msgSrcData.obj = listSearch;
					} else {
						msgSrcData.what = 0;
						msgSrcData.obj = "û�м���������";// "��ȡ��������ʧ�ܣ�";
						sendStopLoadMessage(INDEX_SEARCH_VIEW);// stopLoadingViewThread(INDEX_SEARCH_VIEW);
					}
					handlerCategory.sendMessage(msgSrcData);
				} catch (Exception e) {
				}
			}
		};
		thread.start();
		thread = null;
	}// end get-SearchDataLocal;
	
	private void getPageViewData() {
		if(AppConfig.ORDER_ONLINE_VIDEO) return;
		  Thread thread = new Thread(){

			  @Override
			  public void run() {
				  // TODO Auto-generated method stub
				  //super.run();
				  try
				  {	
//					  Log.d(TAG, "getPageViewData");
					  boolean blState = getNetIsConnected();
    				  if(!blState) {
    					  Message msgTopData = handlerBest.obtainMessage();
    					  msgTopData.what = 0;
    					  msgTopData.arg1 = ALERT_NO_NETWORK;
    					  msgTopData.obj = "δ�������磬�����������ã�";//"��������ʧ�ܣ�";
    					  if(tabLocal.selected == false)
    						  handlerBest.sendMessage(msgTopData); 
        				  stopLodingView(INDEX_REMEN_VIEW);
        				  stopLodingView(INDEX_DASHI_VIEW);
        				  stopLodingView(INDEX_ZHEXUE_VIEW);
        				  stopLodingView(INDEX_WENXUE_VIEW);
        				  stopLodingView(INDEX_JINGJI_VIEW);
        				  stopLodingView(INDEX_LISHI_VIEW);
        				  return;
    				  }

				  	 ParserPageSeriesJson serJson = new ParserPageSeriesJson();
					  ArrayList<SeriesInfo> listReMen = serJson.getSeries(0);
					  if(listReMen != null && listReMen.size() > 0){
						  Message msgReMen = handlerPageView.obtainMessage();
//						  int ilen = listReMen.size();
//						  Log.d(TAG, "msgReMen is:"+ilen);
						  msgReMen.what = INDEX_REMEN_VIEW;
						  msgReMen.obj = listReMen;
						  handlerPageView.sendMessage(msgReMen); 
					  }
					  
					  ArrayList<SeriesInfo> listDaShi = serJson.getSeries(1);
					  if(listDaShi != null && listDaShi.size() > 0){
						  Message msgDaShi = handlerPageView.obtainMessage();
//						  int ilen = listDaShi.size();
//						  Log.d(TAG, "msgDaShi is:"+ilen);
						  msgDaShi.what = INDEX_DASHI_VIEW;
						  msgDaShi.obj = listDaShi;
						  handlerPageView.sendMessage(msgDaShi); 
					  }
					  
					  ArrayList<SeriesInfo> listZheXue = serJson.getSeries(2);
					  if(listZheXue != null && listZheXue.size() > 0){
						  Message msgZheXue = handlerPageView.obtainMessage();
//						  int ilen = listZheXue.size();
//						  Log.d(TAG, "msgZheXue is:"+ilen);
						  msgZheXue.what = INDEX_ZHEXUE_VIEW;
						  msgZheXue.obj = listZheXue;
						  handlerPageView.sendMessage(msgZheXue); 
					  }
					  
					  ArrayList<SeriesInfo> listWenXue = serJson.getSeries(3);
					  if(listWenXue != null && listWenXue.size() > 0){
						  Message msgWenXue = handlerPageView.obtainMessage();
//						  int ilen = listWenXue.size();
//						  Log.d(TAG, "msgWenXue is:"+ilen);
						  msgWenXue.what = INDEX_WENXUE_VIEW;
						  msgWenXue.obj = listWenXue;
						  handlerPageView.sendMessage(msgWenXue); 
					  }
					  
					  ArrayList<SeriesInfo> listJingJi = serJson.getSeries(4);
					  if(listJingJi != null && listJingJi.size() > 0){
						  Message msgJingJi = handlerPageView.obtainMessage();
//						  int ilen = listJingJi.size();
//						  Log.d(TAG, "msgReMen is:"+ilen);
						  msgJingJi.what = INDEX_JINGJI_VIEW;
						  msgJingJi.obj = listJingJi;
						  handlerPageView.sendMessage(msgJingJi); 
					  }
					  
					  ArrayList<SeriesInfo> listLiShi = serJson.getSeries(5);
					  if(listLiShi != null && listLiShi.size() > 0){
						  Message msgLiShi = handlerPageView.obtainMessage();
//						  int ilen = listLiShi.size();
//						  Log.d(TAG, "msgReMen is:"+ilen);
						  msgLiShi.what = INDEX_LISHI_VIEW;
						  msgLiShi.obj = listLiShi;
						  handlerPageView.sendMessage(msgLiShi); 
					  }
				  }
				  catch(Exception e) {
				  }
				}	
			};
			thread.start();
			thread = null;
	}//end getPageViewData;
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  // TODO Auto-generated method stub
	  super.onConfigurationChanged(newConfig);
//	  Log.d(TAG, " == onConfigurationChanged == orientation:["+newConfig.orientation+"]");
	  //1���� == ORIENTATION_PORTRAIT�� 2���� == ORIENTATION_LANDSCAPE;
	  int iCurrentPage = pageBtnHost.getSelectedItem();
	  	if(newConfig.orientation == ORIENTATION_LANDSCAPE){
	  		//changePageViewStateLandscape(true);
	  		Message msg = Message.obtain();
			msg.what = ORIENTATION_LANDSCAPE;
			msg.arg1 = iCurrentPage + 4;
			handlerOrientation.sendMessage(msg);
			gdvShowLocalVideos.setNumColumns(4);
		}
	  	else {
	  		//changePageViewStatePortrait(ORIENTATION_PORTRAIT);
	  		Message msg = Message.obtain();
			msg.what = ORIENTATION_PORTRAIT;
			msg.arg1 = iCurrentPage + 4;
			handlerOrientation.sendMessage(msg);
			gdvShowLocalVideos.setNumColumns(3);
	  	}
	}
	
	private void changePageViewStateLandscape(GridView gridview, int proBar){
		startLodingView(proBar);
		//LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(8 * (150 + 10), LinearLayout.LayoutParams.WRAP_CONTENT);
		if(screenHeight == 0) screenHeight = 1024;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(8 * (182 + 10), LinearLayout.LayoutParams.WRAP_CONTENT);
		gridview.setLayoutParams(params);
		gridview.setColumnWidth(150);
		gridview.setHorizontalSpacing(10);
		gridview.setVerticalSpacing(10);
		gridview.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		gridview.setNumColumns(8);
		stopLodingView(proBar);
	}
	
	private void changePageViewStatePortrait(GridView gridview, int proBar){
		//screenWidth
		startLodingView(proBar);
		//LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(4 * (182 + 10), LinearLayout.LayoutParams.WRAP_CONTENT);
		if(screenWidth == 0) screenWidth = 768;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
		gridview.setLayoutParams(params);
		gridview.setColumnWidth(150);
		gridview.setHorizontalSpacing(10);
		gridview.setVerticalSpacing(10);
		gridview.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);//NO_STRETCH
		gridview.setNumColumns(4);
		stopLodingView(proBar);
	}
	
	final Handler handlerOrientation = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case ORIENTATION_PORTRAIT:
					if ((serListXianFa == null) || (serListXianFa.size() == 0)) {
						return;
					} else {
						switch (msg.arg1) {
							case INDEX_REMEN_VIEW:
								changePageViewStatePortrait(GridViewXianFa, INDEX_REMEN_VIEW);
								break;
							case INDEX_DASHI_VIEW:
								changePageViewStatePortrait(GridViewXingFa, INDEX_DASHI_VIEW);
								break;
							case INDEX_ZHEXUE_VIEW:
								changePageViewStatePortrait(GridViewMinShang, INDEX_ZHEXUE_VIEW);
								break;
							case INDEX_JINGJI_VIEW:
								changePageViewStatePortrait(GridViewJingJi, INDEX_JINGJI_VIEW);
								break;
							case INDEX_LISHI_VIEW:
								changePageViewStatePortrait(GridViewSuSong, INDEX_LISHI_VIEW);
								break;
							case INDEX_WENXUE_VIEW:
								changePageViewStatePortrait(GridViewWenXue, INDEX_WENXUE_VIEW);
								break;
						
						}//end switch;
						
					}
					break;
				case ORIENTATION_LANDSCAPE:
					if ((serListXianFa == null) || (serListXianFa.size() == 0)) {
						return;
					} else {
						switch (msg.arg1) {
							case INDEX_REMEN_VIEW:
								changePageViewStateLandscape(GridViewXianFa, INDEX_REMEN_VIEW);
								break;
							case INDEX_DASHI_VIEW:
								changePageViewStateLandscape(GridViewXingFa, INDEX_DASHI_VIEW);
								break;
							case INDEX_ZHEXUE_VIEW:
								changePageViewStateLandscape(GridViewMinShang, INDEX_ZHEXUE_VIEW);
								break;
							case INDEX_JINGJI_VIEW:
								changePageViewStateLandscape(GridViewJingJi, INDEX_JINGJI_VIEW);
								break;
							case INDEX_LISHI_VIEW:
								changePageViewStateLandscape(GridViewSuSong, INDEX_LISHI_VIEW);
								break;
							case INDEX_WENXUE_VIEW:
								changePageViewStateLandscape(GridViewWenXue, INDEX_WENXUE_VIEW);
								break;					
						}//end switch;			
					}
					break;
				case 200:
					loginState(msg.arg1);
					break;
		
			}//end switch;	
		}
	};//end handlerOrientation;
	/**
	 * �����Ļ��С
	 */
	private void getScreenParams() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				DisplayMetrics dm = new DisplayMetrics();
				// ȡ�ô�������
				getWindowManager().getDefaultDisplay().getMetrics(dm);
				int scrWidth = dm.widthPixels;// ���ڵĿ��
				int scrHeight = dm.heightPixels;// ���ڸ߶�
				Message msg = Message.obtain();
				msg.what = INDEX_SCREEN_PARAMS;
				msg.arg1 = scrWidth;
				msg.arg2 = scrHeight;
				handlerBest.sendMessage(msg);
			}
		};
		thread.start();
		thread = null;
	}

	private void setPageSelectedView(int itemId) {
		Configuration cf= getResources().getConfiguration();
		int ori = cf.orientation;
		
		Message msgP = Message.obtain();
		msgP.what = ori;
		
    	switch (itemId+4) {
			case INDEX_REMEN_VIEW:
				msgP.arg1 = INDEX_REMEN_VIEW;
				break;
			case INDEX_DASHI_VIEW:
				msgP.arg1 = INDEX_DASHI_VIEW;
				break;
			case INDEX_ZHEXUE_VIEW:
				msgP.arg1 = INDEX_ZHEXUE_VIEW;
				break;
			case INDEX_JINGJI_VIEW:
				msgP.arg1 = INDEX_JINGJI_VIEW;
				break;
			case INDEX_LISHI_VIEW:
				msgP.arg1 = INDEX_LISHI_VIEW;
				break;
			case INDEX_WENXUE_VIEW:
				msgP.arg1 = INDEX_WENXUE_VIEW;
				break;
	
    	}//end switch; 	
    	handlerOrientation.sendMessage(msgP);
	}
	
	class GridViewNormalListener implements OnItemClickListener{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String mp4path = null;
				Map<String, Object> map = new HashMap<String, Object>();
				map = listLocalVideo.get(arg2);
				int status = (Integer)map.get("status");
				if(status == 0){ // ��������������Ƶ�Ĳ���
//					Log.i(TAG, "onItemClick --->" + mp4path);
					Intent iPlayVideo = new Intent(SsvideoActivity.this, SsvideoPlayerActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt("videoType", 0);
					SSVideoPlayListBean playListBean = new SSVideoPlayListBean((SSVideoLocalVideoBean) map.get("localVideoBean"));
					bundle.putSerializable("playListBean", playListBean);
					iPlayVideo.putExtras(bundle);
					startActivity(iPlayVideo);
				}else if(status == 1){ // ��ͣ�������ص���Ƶ
					Intent serviceIntent = new Intent("com.chaoxing.download.FileService");
					SSVideoLocalVideoBean localVideoBean = (SSVideoLocalVideoBean) map.get("localVideoBean");
					localVideoBean.setnVideoDownloadStatus(2); 
					
				Bundle bundle = new Bundle();
				bundle.putInt("op", 2);
				bundle.putSerializable("localVideoBean", localVideoBean);
				serviceIntent.putExtras(bundle);
				startService(serviceIntent);
				try {
					mDBAdapter.updateDataInLocalVideo(localVideoBean.getStrVideoId(), localVideoBean);
				} catch (Exception e){//(SQLException e) {
					e.printStackTrace();
				}
				map.put("status", 2);
				adapter.notifyDataSetChanged();
				}else if(status ==2){ // ��ʼ�Ѿ���ͣ����Ƶ
					Intent serviceIntent = new Intent("com.chaoxing.download.FileService");
					SSVideoLocalVideoBean localVideoBean2s = (SSVideoLocalVideoBean) map.get("localVideoBean");
					try {
			          	localVideoBean2s = mDBAdapter.fetchDataInLocalVideoById(localVideoBean2s.getStrVideoId());
//			          	Log.i(TAG, "start download    video id===========" + map.get("videoId"));
			          	if(localVideoBean2s != null){
			          		localVideoBean2s.setStrVideoDownloadRemoteFileUrl(localVideoBean2s.getStrVideoDownloadRemoteFileUrl());
			          		localVideoBean2s.setnVideoDownloadStatus(1);
			          		mDBAdapter.updateDataInLocalVideo(localVideoBean2s.getStrVideoId(), localVideoBean2s);
			          	}
			         } catch (SQLException e) {
			          	e.printStackTrace();
			         }
					Bundle bundle = new Bundle();
				bundle.putInt("op", 1);
				bundle.putSerializable("localVideoBean", localVideoBean2s);
				serviceIntent.putExtras(bundle);
				startService(serviceIntent);
				
				map.put("status", 1);
				adapter.notifyDataSetChanged();
				}
			}
      }

	public static int getAPNType(Context context) {
		int netType = NET_TYPE_NULL;
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String type = networkInfo.getExtraInfo();
			// Log.d("networkInfo", "network type is "+ type);
			if (type.toLowerCase().equals("cmnet")) {
				netType = NET_TYPE_CMNET;
			} else {
				netType = NET_TYPE_CMWAP;
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NET_TYPE_WIFI;
		}
		return netType;
	}
	/**
	 *  �жϵ�ǰ�����Ƿ��Ѿ�����
	 * @return true ����������false �����쳣
	 */
	public boolean getNetIsConnected() {
		boolean netType = false;
		ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {	
			if (netInfo.getState() == NetworkInfo.State.CONNECTED) {
				netType = true;
			}
		}
		return netType;
	}

	private static String getPartString(String str, int len) throws UnsupportedEncodingException {
		byte b[];
		int counterOfDoubleByte = 0;
		b = str.getBytes("GBK");
		if (b.length <= len) {
			return str;
		}
		for (int i = 0; i < len; i++) {
			if (b[i] < 0) {
				counterOfDoubleByte++;
			}
		}
		if (counterOfDoubleByte % 2 == 0) {
			return new String(b, 0, len, "GBK") + "...";
		} else {
			return new String(b, 0, len - 1, "GBK") + "...";
		}
	}
	/**
	 * �����°汾
	 */
	public void checkVersion() {
		if (Global.checkUpdate == 0)
			return;
//		Log.i("zzy", "splash checkVersion----------------------------->");
		if (Global.localVersion < Global.serverVersion) {
			String updateInfo = getString(R.string.update_tip1)
					+ Global.verName + "\n\n" + getString(R.string.update_tip2)
					+ "\n" + Global.updateInfo;
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle(getString(R.string.update_title))
					.setMessage(updateInfo)
					.setPositiveButton(getString(R.string.update_ok),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									Intent updateIntent = new Intent(
											SsvideoActivity.this, UpdateService.class);
									updateIntent.putExtra("titleId",
											R.string.app_name);
									startService(updateIntent);
								}
							})
					.setNegativeButton(getString(R.string.update_cancel),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									Global.checkUpdate = 0;
									dialog.dismiss();
								}
							});
			alert.create().show();
		} else {

		}
	}
	
	private void showAlert(String str) {
		if (str == "")
			return;

		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(getString(R.string.alert_title))
			.setMessage(str)
			.setPositiveButton(getString(R.string.alert_ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if(viewFlipHost.getDisplayedChild() == INDEX_BEST_VIEW) {
						tabLocal.selected = true;
						tabHost.onFinishInflate();
						onSelected(INDEX_LOCAL_VIEW);
					}
				}
			})
			//.setNegativeButton(getString(R.string.alert_cancel), null)
			.create().show();
	}
	
	private void sendReloadDataMessage(int viewType) {
  	  	Message msgReload = handlerBest.obtainMessage();
  	  	msgReload.what = VIEW_DATA_RELOAD;
  	  	msgReload.arg1 = viewType;
		handlerBest.sendMessage(msgReload); 
    }
	
	private void showAlertRepeat(String str, final int viewType) {
		if (str == "" || mStop)
			return;

		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(getString(R.string.alert_title))
			.setMessage(str)
			.setPositiveButton(getString(R.string.alert_repeat), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					boolean blState = getNetIsConnected();
					if(blState) {
						if(viewType == INDEX_BEST_VIEW) {
	//						Log.d(TAG, "��ʼ���ض���6���Ƽ�");
							sendReloadDataMessage(INDEX_GALLERY_VIEW);
	//						Log.d(TAG, "��ʼ�����Ƽ���6������");
							sendReloadDataMessage(INDEX_BEST_VIEW);
						    
						}
						else if(viewType == INDEX_CATEGORY_VIEW) {
							sendReloadDataMessage(INDEX_CATEGORY_VIEW);
						}
					}
					else {
						 Message msgAlert = handlerBest.obtainMessage();
						 msgAlert.what = 0;
						 msgAlert.arg1 = ALERT_NO_NETWORK;
						 msgAlert.obj  = "δ�������磬�����������ã�";
			    		 handlerBest.sendMessage(msgAlert);
					}
				}
			})
			.setNegativeButton(getString(R.string.alert_cancel), null)
			.create().show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		// if(viewFlipHost.getDisplayedChild() == INDEX_LOCAL_VIEW)
		if (!AppConfig.ORDER_ONLINE_VIDEO) {
			inflater.inflate(R.menu.menu_local, menu);
		}
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		// Log.d("test-Menu", " selId ="+ viewFlipHost.getDisplayedChild());
		if (!AppConfig.ORDER_ONLINE_VIDEO) {
			if (viewFlipHost.getDisplayedChild() == INDEX_LOCAL_VIEW) {
				MenuItem mi = menu.getItem(0);
				mi.setVisible(false);// true
			} else {
				MenuItem mi = menu.getItem(0);
				mi.setVisible(false);
			}
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.refresh_localvideo:
			refreshLocalvideo();
			return true;
		case R.id.about_ssvideo:
			aboutSSVideo();
			return true;
		};
		return false; // should never happen
	}

	private void refreshLocalvideo() {
		// m_DBAdapter.checkAllCategory();
		List<File> lstVideos = new ArrayList<File>();
		lstVideos.clear();
		File file = new File(UsersUtil.getRootDir());
		int totalFiles = getFileCount(file, lstVideos);
		btnRefresh.setEnabled(false);
		if (totalFiles > 0) {
			sendToastMessage("ȫ����Ƶ���� " + totalFiles);
			sendStartLoadMessage(INDEX_LOCAL_VIEW);
		} else {
			sendToastMessage("������Ƶ");
		}
		loadLocalVideos(lstVideos);
	}

	private void sendToastMessage(String msg) {
		Message msgToast = handlerBest.obtainMessage();
		msgToast.what = 0;
		msgToast.obj = msg;
		handlerBest.sendMessage(msgToast); 
	}
	
	private void aboutSSVideo() {
		String msg = getResources().getString(R.string.name_about);
		msg += " "+ getResources().getString(R.string.version);
		msg += "\r\n\r\n "+ getResources().getString(R.string.copyright) +" "+ getResources().getString(R.string.copyright_co);
		msg += "\r\n\r\n "+ getResources().getString(R.string.contact_pone_title) +" "+ getResources().getString(R.string.contact_pone);
		msg += "\r\n "+ getResources().getString(R.string.contact_mail_title) +" "+ getResources().getString(R.string.contact_mail);
		msg += "\r\n "+ getResources().getString(R.string.contact_web_title) +" "+ getResources().getString(R.string.contact_web);
		msg += "\r\n";
		showAboutDialog(msg);
	}
	
	private void showAboutDialog(String msg) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(getString(R.string.about_ssvideo)).setIcon(R.drawable.icon_about_menu)
			.setMessage(msg).setCancelable(true)
			.setPositiveButton(getString(R.string.alert_ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					
				}
			})
			//.setNegativeButton(getString(R.string.alert_cancel), null)
			.create().show();
	}
	
	private void loadLocalVideos(final List<File> listFiles){
		Thread thread = new Thread() {
			@Override
			public void run() {
				mDBAdapter.deleteLocalVideoByStatus("0");
				for (int i=0; i<listFiles.size(); i++) {
					File tempF  = listFiles.get(i);
					try {
						String tempFileName = tempF.getName().toLowerCase();
						if(tempFileName.indexOf(".mp4") > -1) {
							String path = tempF.getPath();
							String fileName = tempF.getName();
							int fileLength = (int) tempF.length();
							String coverName = fileName.substring(0, fileName.lastIndexOf('.'));
							saveVideoCover(path, coverName+".png");

				        	SSVideoLocalVideoBean localVideoBean = new SSVideoLocalVideoBean();
				        	localVideoBean.setStrVideoFileName(fileName.substring(0, fileName.lastIndexOf('.')));
				        	localVideoBean.setStrVideoId("" + path.hashCode());
				        	String tempPath = path.substring(0, path.lastIndexOf('/'));
				        	
				        	String cateName = tempPath.substring(tempPath.lastIndexOf('/')+1);
				        	SSVideoCategoryBean cateBean = mDBAdapter.fetchCategoryByName(cateName);
				        	
				        	if(cateBean != null){
				        		localVideoBean.setStrCateId(cateBean.getStrCateId());
				        	}
				        	else {
				        		cateBean = mDBAdapter.fetchCategoryByName("δ����");
				        		if(cateBean == null){
				        			cateBean = new SSVideoCategoryBean();
				        			cateBean.setStrCateId("XXX");
				        			cateBean.setStrCateName("δ����");
				        			mDBAdapter.insertIntoCategory(cateBean);
				        		}
				        		localVideoBean.setStrCateId("XXX");
				        	}
				        	
				        	localVideoBean.setStrVideoLocalPath(path);
				        	localVideoBean.setStrCoverName(coverName + ".png");
				        	localVideoBean.setnVideoFileLength((int) fileLength);

				        	mDBAdapter.insertIntoLocalVideo(localVideoBean);
						}
					}catch(Exception e){
						
					}
				}//end for;
				sendReloadDataMessage(INDEX_LOCAL_VIEW);
			}
		};
		thread.start();
		thread = null;
	}
	
	private int saveVideoCover(String path, String name) {
		File fCover = new File(SScoversPath + name);
		if (fCover.exists() && fCover.isFile()) {
			return 1;
		}
		Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path, Video.Thumbnails.MINI_KIND);
		if (bitmap != null) {
			BitmapUtil.saveBitmap2png(new File(SScoversPath), name, bitmap,
					213, 160);
		}
		return 0;
	}
	
	// ��������Ŀ¼�µ�����MP4�ļ���������Ŀ¼    
	private int getFileCount(File dir, List<File> listFiles) {
		int fileCount = 0;
		File[] theFiles = dir.listFiles();
		if (theFiles != null)
			for (File tempF : theFiles) {
				if (tempF.isDirectory()) {
					fileCount += getFileCount(tempF, listFiles);
				} else {
					if (tempF.getName().toLowerCase().indexOf(".mp4") > -1) {
						fileCount++;
						listFiles.add(tempF);
					}
				}
			}// end for;
		return fileCount;
	}
	
}
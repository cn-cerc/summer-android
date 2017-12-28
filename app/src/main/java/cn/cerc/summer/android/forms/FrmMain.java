package cn.cerc.summer.android.forms;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.util.H5PayResultModel;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.Thing;
import com.mimrc.vine.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.cerc.summer.android.core.VisualKeyboardTool;
import cn.cerc.summer.android.core.CommBottomPopWindow;
import cn.cerc.summer.android.core.Constans;
import cn.cerc.summer.android.core.MainPopupMenu;
import cn.cerc.summer.android.core.MainTitleMenu;
import cn.cerc.summer.android.core.MyApp;
import cn.cerc.summer.android.core.ScreenUtils;
import cn.cerc.summer.android.forms.view.BrowserView;
import cn.cerc.summer.android.forms.view.DragPointView;
import cn.cerc.summer.android.services.RefreshMenu;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;


/**
 * 主界面
 */
public class FrmMain extends AppCompatActivity implements View.OnLongClickListener, View.OnClickListener {
    public static final String NETWORK_CHANGE = "android.net.conn.NETWORK_CHANGE";
    public static final String APP_UPDATA = "com.mimrc.vine.APP_UPDATA";
    public static final String JSON_ERROR = "com.mimrc.vine.JSON_ERROR";
    public final static int FILECHOOSER_RESULTCODE = 41;
    public final static int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 42;
    private static final String LOGTAG = "FrmMain";
    private static FrmMain instance;
    private final int REQUEST_SETTING = 101;
    public ArrayList<MainTitleMenu> mRightMenu;  //右侧菜单集合
    public ArrayList<MainTitleMenu> mTitleMenu;  //标题菜单集合
    public List<MainTitleMenu> mRightMenuTemp = new ArrayList<>();
    public int winClose = -1;  //控制关闭窗口
    ImageView imgHome, imgBack, imgMore;
    TextView lblTitle;
    LinearLayout boxTitle;
    int mIndex = 0;   //当前页面窗口位置下标
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            {
                switch (msg.what) {
                    case 1:
                        boolean visibility = (boolean) msg.obj;
                        boxTitle.setVisibility(visibility ? View.VISIBLE : View.GONE);
                        break;
                    case 2:
                        String title = (String) msg.obj;
                        lblTitle.setText(title);
                        break;

                }
            }
        }
    };
    private SharedPreferences settings;
    private BrowserView browser; //浏览器
    private String homeUrl;//Web系统首页
    private boolean islogin = false;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadMessageForAndroid5;
    private ProgressBar progress;
    private DragPointView dragpointview;
    private ImageView tipsImage;
    private String logoutUrl = "";
    private boolean is_ERROR = false;//是否错误了
    private String[] menus;//菜单
    private int[] menu_img = new int[]{R.mipmap.message, R.mipmap.msg_manager, R.mipmap.home, R.mipmap.setting, R.mipmap.wipe, R.mipmap.logout, R.mipmap.reload};
    private List<MainPopupMenu> menuList;
    private ListPopupWindow popupWindow;//列表弹框
    private FrameLayout mainframe;
    private View view;      //弹出框子布局
    private ListView list_pop;
    private PopupWindow pop;
    private CommBottomPopWindow mTitlePopWindow;  //标题栏菜单项
    private CommBottomPopWindow mpopWindow; //
    private ArrayList<MainTitleMenu> mTitleWinMenu;  //标题菜单窗口集合
    private ArrayList<ArrayList<MainTitleMenu>> allTitleList;  //标题菜单总集合
    private ArrayList<ArrayList<MainTitleMenu>> allRightList;  //右侧总集合
    private List<MainTitleMenu> titlePage;  //多页面集合
    private BrowserView newsWebView[] = new BrowserView[6];  //webView数组，限制上限为6
    private String currentUrl = null;
    private int classWebView = 0;
    private boolean webViewState = false;  //判断是否新建webView
    private RefreshMenu mRefreshMenu;
    private View hightview;
    private MyApp myApp;
    /**
     * 推送消息的消息id， 点击通知栏打开
     */
    private String msgId = "";
    private TagAliasCallback tac = new TagAliasCallback() {
        @Override
        public void gotResult(int i, String s, Set<String> set) {
            switch (i) {
                case 0://成功
                    Log.e(LOGTAG, "设置成功");
                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    break;
                case 6002://失败
                    // 延迟 30 秒来调用 Handler 设置别名
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            JPushInterface.setAlias(FrmMain.this, MyApp.getInstance().getClientId(), tac);
                        }
                    }, 30000);
                    break;
                default:
            }
        }
    };
    /**
     * 是否直接退出
     */
    private boolean is_exit = false;
    //标题菜单回调的点击事件
    private CommBottomPopWindow.PopWindowListener mPopListener = new CommBottomPopWindow.PopWindowListener() {
        @Override
        public void onPopSelected(int which) {
            switch (which) {
                case 1:

                case 2:
                    switch (mTitleMenu.get(which).getName()) {
                        case "关闭页面":
                            //关闭当前窗口
                            int windowNum = 0;
                            for (int i = 0; i < allTitleList.size(); i++) {
                                if (allTitleList.get(i).size() > 0) {
                                    windowNum++;
                                }
                            }
                            if (windowNum > 1) {
                                newsWebView[classWebView].setVisibility(View.GONE);
                                newsWebView[classWebView] = null;
                                for (int i = 0; i < allTitleList.size(); i++) {
                                    if (allTitleList.get(i).size() > 0) {
                                        if (allTitleList.get(i).get(0).getOnlySign() == classWebView) {
                                            allTitleList.get(classWebView).clear();
                                            allRightList.get(classWebView).clear();
                                        }
                                    }
                                }
                                for (int i = 0; i < titlePage.size(); i++) {
                                    if (titlePage.get(i).getOnlySign() == classWebView) {
                                        titlePage.remove(i);
                                    }
                                }
                                for (int i = 0; i < newsWebView.length; i++) {
                                    if (newsWebView[i] != null) {
                                        newsWebView[i].setVisibility(View.VISIBLE);
                                        browser = newsWebView[i];
                                        classWebView = i;
                                        for (int l = 0; l < allTitleList.size(); l++) {
                                            if (allTitleList.get(l).size() > 0) {
                                                if (allTitleList.get(l).get(0).getOnlySign() == classWebView) {
                                                    upDataAggre(allTitleList.get(l), titlePage);
                                                    mTitleMenu.clear();
                                                    mTitleMenu.addAll(allTitleList.get(l));
                                                    mRightMenu.clear();
                                                    mRightMenu.addAll(allRightList.get(l));
                                                    break;
                                                }
                                            }
                                        }
                                        for (int j = 0; j < titlePage.size(); j++) {
                                            if (titlePage.get(j).getOnlySign() == i) {
                                                lblTitle.setText(titlePage.get(j).getName());
                                                break;
                                            }
                                        }
                                        break;
                                    }
                                }
                            } else {
                                Toast.makeText(myApp, "已经是顶层菜单", Toast.LENGTH_SHORT).show();
                            }
                            mTitlePopWindow.dismiss();
                            initTitlePopWindow();
                            break;
                        case "新建窗口":
                            AddWebView();
                            mTitlePopWindow.dismiss();
                            break;
                        default:
                            bettWin(which);
                            break;
                    }
                    break;
                default:
                    bettWin(which);
                    break;
            }
        }
    };
    /**
     * 右侧菜单回调的点击事件
     */
    private CommBottomPopWindow.PopWindowListener mPopListener1 = new CommBottomPopWindow.PopWindowListener() {
        @Override
        public void onPopSelected(int which) {
            switch (which) {
                case 0:
                    //设置界面
                    FrmSettings.startFormForResult(FrmMain.getInstance(), REQUEST_SETTING, browser.getUrl());
                    mpopWindow.dismiss();
                    break;
                case 1:
                    browser.reload();
                    clearAllCache(getApplicationContext());
                    Toast.makeText(FrmMain.this, "刷新成功", Toast.LENGTH_SHORT).show();
                    mpopWindow.dismiss();
                    break;
                case 2:
                    Toast.makeText(FrmMain.this, "退出系统", Toast.LENGTH_SHORT).show();
                    //退出系统
                    finish();
                    break;
                default:
                    // runScript(String.format("%s('%s', '%s')", mRightMenu.get(which).getUrl(), this.scriptTag, resultString));
                    //   browser.loadUrl(mRightMenu.get(which).getName());
                    runScript(String.format("%s('%s', '%s')", mRightMenu.get(which).getUrl(), mRightMenu.get(which).getScriptTag(), "回调成功"));
                    mpopWindow.dismiss();
                    break;
            }
        }
    };
    private List<MainTitleMenu> list;

    public static FrmMain getInstance() {
        return instance;
    }

    /**
     * @param context 删除缓存
     */
    public static void clearAllCache(Context context) {
        deleteDir(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            deleteDir(context.getExternalCacheDir());
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            int size = 0;
            if (children != null) {
                size = children.length;
                for (int i = 0; i < size; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }

        }
        if (dir == null) {
            return true;
        } else {

            return dir.delete();
        }
    }

    public BrowserView getBrowser() {
        return browser;
    }

    public void setHomeUrl(String homeUrl) {
        this.homeUrl = homeUrl;
        browser.loadUrl(homeUrl);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindows();//沉浸式全屏设置
        setContentView(R.layout.activity_main);
        VisualKeyboardTool.assistActivity(findViewById(android.R.id.content)); //自动监听虚拟按键的变化，改变高度
        instance = this;

        settings = getSharedPreferences(Constans.SHARED_SETTING_TAB, MODE_PRIVATE);

        mRightMenu = new ArrayList<MainTitleMenu>();
        mTitleMenu = new ArrayList<MainTitleMenu>();
        allTitleList = new ArrayList<ArrayList<MainTitleMenu>>();
        allRightList = new ArrayList<ArrayList<MainTitleMenu>>();
        myApp = MyApp.getInstance();
        for (int i = 0; i < 6; i++) {
            allTitleList.add(new ArrayList<MainTitleMenu>());
        }
        for (int i = 0; i < 6; i++) {
            allRightList.add(new ArrayList<MainTitleMenu>());
        }
        titlePage = new ArrayList<MainTitleMenu>();
        mTitleWinMenu = new ArrayList<MainTitleMenu>();
        initData();
        InitView();
        browser.loadUrl(myApp.getStartPage());

    }

    private void initWindows() {
        Window window = getWindow();
        int color = getResources().getColor(android.R.color.transparent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            window.setStatusBarColor(color);
            //设置导航栏颜色
//            window.setNavigationBarColor(color);
            ViewGroup contentView = ((ViewGroup) findViewById(android.R.id.content));
            View childAt = contentView.getChildAt(0);
            if (childAt != null) {
                childAt.setFitsSystemWindows(true);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            //设置contentview为fitsSystemWindows
            ViewGroup contentView = (ViewGroup) findViewById(android.R.id.content);
            View childAt = contentView.getChildAt(0);
            if (childAt != null) {
                childAt.setFitsSystemWindows(true);
            }
            //给statusbar着色
            View view = new View(this);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, VisualKeyboardTool.getStatusBarHeight(this)));
            view.setBackgroundColor(color);
            contentView.addView(view);
        }
    }

    /**
     * 固定数据初始化
     */
    private void initData() {
        mTitleMenu.clear();
        mRightMenu.clear();
        mTitleMenu.add(new MainTitleMenu("返回首页", false, myApp.getStartPage(), 1, classWebView));  //设置初始化数据
        mTitleMenu.add(new MainTitleMenu("新建窗口", false, "", 1, classWebView));
        mRightMenu.add(new MainTitleMenu("设置", false, "", 1, ""));
        mRightMenu.add(new MainTitleMenu("刷新", false, "", 1, ""));
        // TODO 临时扫一扫
        // mRightMenu.add(new MainTitleMenu("扫一扫", true, "", 1));
        mRightMenu.add(new MainTitleMenu("退出系统", true, "", 1, ""));
    }

    /**
     * 查看消息的url
     */
    private String getMsgUrl(String read) {
        String url = settings.getString(Constans.SHARED_MSG_URL, "") + read;
        return MyApp.buildDeviceUrl(url);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra("msgId")) {
            msgId = intent.getStringExtra("msgId");
            String msgurl = getMsgUrl(".show") + "&msgId=" + msgId;
            Log.e(LOGTAG, msgurl);
            browser.loadUrl(msgurl);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SETTING:
                if (resultCode == RESULT_OK) {
                    browser.loadUrl(data.getStringExtra("home"));
                }
                break;
            case FILECHOOSER_RESULTCODE:
                if (null == mUploadMessage)
                    return;
                Uri result1 = data == null || resultCode != RESULT_OK ? null : data.getData();
                mUploadMessage.onReceiveValue(result1);
                mUploadMessage = null;
                break;
            case FILECHOOSER_RESULTCODE_FOR_ANDROID_5:
                if (null == mUploadMessageForAndroid5)
                    return;
                Uri result2 = (data == null || resultCode != RESULT_OK) ? null : data.getData();
                if (result2 != null) {
                    mUploadMessageForAndroid5.onReceiveValue(new Uri[]{result2});
                } else {
                    mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
                }
                mUploadMessageForAndroid5 = null;
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);

        Set<String> set = new HashSet<String>();
        set.add("android");
        JPushInterface.setAlias(getApplicationContext(), MyApp.getInstance().getClientId(), tac);//极光推送设置别名
    }

    @SuppressLint("JavascriptInterface")
    private void InitView() {
        imgBack = (ImageView) this.findViewById(R.id.imgBack);
        imgMore = (ImageView) this.findViewById(R.id.imgMore);
        lblTitle = (TextView) this.findViewById(R.id.lblTitle);
        boxTitle = (LinearLayout) findViewById(R.id.boxTitle);
        hightview = (View) findViewById(R.id.hightview);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            hightview.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, VisualKeyboardTool.getStatusBarHeight(FrmMain.this)));
            hightview.setVisibility(View.VISIBLE);
        } else {
            hightview.setVisibility(View.GONE);
        }
        imgBack.setOnClickListener(this);
        imgMore.setOnClickListener(this);
        lblTitle.setOnClickListener(this);
        lblTitle.setSelected(true);
        dragpointview = (DragPointView) this.findViewById(R.id.dragpointview);
        dragpointview.setEnable(false);
        dragpointview.setClickable(false);
        dragpointview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        dragpointview.setVisibility(View.INVISIBLE);
        mainframe = (FrameLayout) this.findViewById(R.id.mainframe);
        progress = (ProgressBar) this.findViewById(R.id.progress);
        tipsImage = (ImageView) this.findViewById(R.id.image_tips);
        browser = (BrowserView) this.findViewById(R.id.webView);
        browser.getSettings().setTextZoom(settings.getInt(Constans.SCALE_SHAREDKEY, ScreenUtils.getScales(this, ScreenUtils.getInches(this))));

        //jsAndroid 供web端js调用标识，修改请通知web开发者
        browser.addJavascriptInterface(new JavaScriptProxy(this), "JSobj");

        browser.setWebViewClient(new MyWebViewClient());

        browser.setWebChromeClient(new WebChromeClient() {

            // For Android  > 4.1.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooserImpl(uploadMsg);
                mUploadMessage = uploadMsg;
            }

            // For Android  > 3.0
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                if (mUploadMessage != null) return;
                mUploadMessage = uploadMsg;
            }

            // For Android  > 5.0
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                mUploadMessageForAndroid5 = filePathCallback;
                openFileChooserImplForAndroid5(filePathCallback);
                return true;
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }

            @Override
            public void onPermissionRequest(PermissionRequest request) {
                super.onPermissionRequest(request);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    progress.setProgress(newProgress, true);
                else progress.setProgress(newProgress);
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

            @Override
            public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
                super.onReceivedTouchIconUrl(view, url, precomposed);
            }
        });
        browser.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK) && event.getAction() == KeyEvent.ACTION_UP) {
                    if (is_exit) {
                        Intent home = new Intent(Intent.ACTION_MAIN);
                        home.addCategory(Intent.CATEGORY_HOME);
                        startActivity(home);
                    } else {
                        if (browser.canGoBack()) browser.goBack();// 返回键退回
                        else finish();
                    }
                    return true;
                } else
                    return false;
            }
        });
        browser.setOnLongClickListener(this);
        AddWebView();
    }

    @Override
    public boolean onLongClick(View v) {
        //webview的长按事件，设置true后webview将不会触发长按复制动作
        return true;
    }

    /**
     * 获得标题上层级菜单
     *
     * @param title
     * @param newUrl
     */
    public void CatalogTitleWebView(String title, String newUrl) {
        mTitleMenu.add(new MainTitleMenu(title, false, newUrl, 2, classWebView));
        initTitlePopWindow();
    }

    /**
     * 获得右侧上级菜单
     *
     * @param title
     * @param newUrl
     */
    public void CatalogWebView(String title, String newUrl) {
        mRightMenu.add(new MainTitleMenu(title, false, newUrl, 2, ""));
        initPopWindow();
    }

    /**
     * 切换窗口
     *
     * @param which
     */
    private void bettWin(int which) {
        switch (mTitleMenu.get(which).getLayerSign()) {
            case 3:
                //切换窗口
                mIndex = mTitleMenu.get(which).getOnlySign();
                classWebView = mIndex;
                newsWebView[mTitleMenu.get(which).getOnlySign()].setVisibility(View.VISIBLE);
                browser = newsWebView[mIndex];
                lblTitle.setText(mTitleMenu.get(which).getName());
                for (int i = 0; i < titlePage.size(); i++) {

                }
                for (int i = 0; i < allTitleList.size(); i++) {
                    if (allTitleList.get(i).size() > 0) {
                        if (allTitleList.get(i).get(0).getOnlySign() == classWebView) {
//                                        upDataAggre(allTitleList.get(i), titlePage);
                            mTitleMenu.clear();
                            mTitleMenu.addAll(allTitleList.get(i));
                            mRightMenu.clear();
                            mRightMenu.addAll(allRightList.get(i));
                            break;
                        }
                    }
                }
                for (int i = 0; i < newsWebView.length; i++) {
                    if (i != mIndex && newsWebView[i] != null) {
                        newsWebView[i].setVisibility(View.GONE);
                    }
                }
                mTitlePopWindow.dismiss();
                break;
            default:
                browser.loadUrl(mTitleMenu.get(which).getUrl());
                winClose = which;
                mTitlePopWindow.dismiss();
                break;
        }
    }

    /**
     * 设置标题栏点击菜单
     */
    private void initTitlePopWindow() {
        view = this.getLayoutInflater().inflate(R.layout.comm_popwindow_item, null);
        mTitlePopWindow = new CommBottomPopWindow(this);
        upDataAggre(mTitleMenu, titlePage);
        for (int i = 0; i < allTitleList.size(); i++) {
            if (allTitleList.get(i).size() > 0) {
                if (allTitleList.get(i).get(0).getOnlySign() == classWebView) {
                    allTitleList.get(classWebView).clear();
                    allTitleList.get(classWebView).addAll(mTitleMenu);
                }
            }
        }
        for (int j = 0; j < mTitleMenu.size() - 1; j++) {
            if (mTitleMenu.get(j).getLayerSign() != mTitleMenu.get(j + 1).getLayerSign()) {
                mTitleMenu.get(j).setLine(true);
            } else {
                mTitleMenu.get(j).setLine(false);
            }
        }
        if (mTitleMenu.get(mTitleMenu.size() - 1).isLine()) {
            mTitleMenu.get(mTitleMenu.size() - 1).setLine(false);
        }
        mTitlePopWindow.initPopItem(mTitleMenu);
        mTitlePopWindow.setPopListener(mPopListener);
    }

    /**
     * 重新添加标题类
     *
     * @param list1
     * @param list2
     */
    private void upDataAggre(List<MainTitleMenu> list1, List<MainTitleMenu> list2) {
        List<MainTitleMenu> tempList = new ArrayList<MainTitleMenu>();
        for (int i = 0; i < list1.size(); i++) {
            if (list1.get(i).getLayerSign() == 3) {
                tempList.add(list1.get(i));
            }
        }
        list1.removeAll(tempList);
        list1.addAll(list2);
    }

    /**
     * 设置右侧弹出框
     */
    private void initPopWindow() {
        view = this.getLayoutInflater().inflate(R.layout.comm_popwindow_item, null);
        mpopWindow = new CommBottomPopWindow(this, true);
        for (int i = 0; i < allTitleList.size(); i++) {
            if (allTitleList.get(i).size() > 0) {
                if (allTitleList.get(i).get(0).getOnlySign() == classWebView) {
                    allRightList.get(i).clear();
                    allRightList.get(i).addAll(mRightMenu);
                }
            }
        }
        mRightMenu.get(2).setLine(true);
        if (mRightMenu.get(mRightMenu.size() - 1).isLine()) {
            mRightMenu.get(mRightMenu.size() - 1).setLine(false);
        }
        mpopWindow.initPopItem(mRightMenu);
        mpopWindow.setPopListener(mPopListener1);
    }

    private boolean newsWebView() {
        for (int i = 0; i < 6; i++) {
            if (newsWebView[i] != null) {
                webViewState = false;
            } else {
                webViewState = true;
                classWebView = i;
                break;
            }
        }
        if (webViewState) {
            initData();         //每次新建重新初始化数据
            titlePage.add(new MainTitleMenu("欢迎页", false, currentUrl, 3, classWebView));
            newsWebView[classWebView] = new BrowserView(this);
            mainframe.addView(newsWebView[classWebView]);
            newsWebView[classWebView].getSettings().setTextZoom(settings.getInt(Constans.SCALE_SHAREDKEY, ScreenUtils.getScales(this, ScreenUtils.getInches(this))));

            //jsAndroid 供web端js调用标识，修改请通知web开发者
            newsWebView[classWebView].addJavascriptInterface(new JavaScriptProxy(this), "JSobj");

            newsWebView[classWebView].setWebViewClient(new MyWebViewClient());

            newsWebView[classWebView].setWebChromeClient(new WebChromeClient() {

                // For Android  > 4.1.1
                public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                    openFileChooserImpl(uploadMsg);
                    mUploadMessage = uploadMsg;
                }

                // For Android  > 3.0
                public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                    if (mUploadMessage != null) return;
                    mUploadMessage = uploadMsg;
                }

                // For Android  > 5.0
                @Override
                public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                    mUploadMessageForAndroid5 = filePathCallback;
                    openFileChooserImplForAndroid5(filePathCallback);
                    return true;
                }

                @Override
                public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                    callback.invoke(origin, true, false);
                    super.onGeolocationPermissionsShowPrompt(origin, callback);
                }

                @Override
                public void onPermissionRequest(PermissionRequest request) {
                    super.onPermissionRequest(request);
                }

                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        progress.setProgress(newProgress, true);
                    else progress.setProgress(newProgress);
                    super.onProgressChanged(view, newProgress);
                }

                @Override
                public void onReceivedTitle(WebView view, String title) {
                    super.onReceivedTitle(view, title);
                }

                @Override
                public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
                    super.onReceivedTouchIconUrl(view, url, precomposed);
                }
            });
            newsWebView[classWebView].setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((keyCode == KeyEvent.KEYCODE_BACK) && event.getAction() == KeyEvent.ACTION_UP) {
                        if (is_exit) {
                            Intent home = new Intent(Intent.ACTION_MAIN);
                            home.addCategory(Intent.CATEGORY_HOME);
                            startActivity(home);
                        } else {
                            if (newsWebView[classWebView].canGoBack())
                                newsWebView[classWebView].goBack();// 返回键退回
                            else finish();
                        }
                        return true;
                    } else
                        return false;
                }
            });

        } else {
            Toast.makeText(this, "已达到新窗口上限", Toast.LENGTH_SHORT).show();
        }
        return webViewState;
    }

    /**
     * 新建窗口
     */
    private void AddWebView() {
        if (newsWebView()) {
            //将数据存进总集合
            for (int i = 0; i < allTitleList.size(); i++) {
                if (allTitleList.get(i).size() == 0) {
                    allTitleList.get(i).clear();
                    allTitleList.get(i).addAll(mTitleMenu);
                    allRightList.get(i).clear();
                    allRightList.get(i).addAll(mRightMenu);
                    break;
                }
            }
            browser = newsWebView[classWebView];
            browser.loadUrl(myApp.getStartPage());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBack:
                browser.goBack();
                break;
            case R.id.imgMore:
                initPopWindow();
                mpopWindow.showAsDropDown(boxTitle);
                mpopWindow.show(lblTitle);
                break;
            case R.id.lblTitle:
                //标题菜单
                if (titlePage.size() > 1) {
                    if (mTitleMenu.get(1).getName().equals("关闭页面")) {
                    } else {
                        mTitleMenu.add(1, new MainTitleMenu("关闭页面", false, "", 1, classWebView));
                    }
                } else {
                    for (int i = 0; i < mTitleMenu.size(); i++) {
                        if (mTitleMenu.get(i).getName().equals("关闭页面")) {
                            mTitleMenu.remove(i);
                            break;
                        }
                    }
                }
                initTitlePopWindow();
                mTitlePopWindow.showAsDropDown(boxTitle);
                mTitlePopWindow.show(view);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    /**
     * 清除缓存
     */
    private int clearCacheFolder(File dir, long numDays) {
        int deletedFiles = 0;
        if (dir != null && dir.isDirectory()) {
            try {
                for (File child : dir.listFiles()) {
                    if (child.isDirectory())
                        deletedFiles += clearCacheFolder(child, numDays);
                    if (child.lastModified() < numDays)
                        if (child.delete()) deletedFiles++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return deletedFiles;
    }

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page")
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW).setObject(object).setActionStatus(Action.STATUS_TYPE_COMPLETED).build();
    }

    //在系统进行登入、登出时通知
    public void LoginOrLogout(boolean islogin, String url) {
        this.logoutUrl = url;
        this.islogin = islogin;
    }

    public void reload(int scales) {
        browser.getSettings().setTextZoom(Integer.valueOf(settings.getInt(Constans.SCALE_SHAREDKEY, 90)));
        browser.reload();
    }

    /*
   *android 4.1以上webview调用的图片方法
   * @param uploadMsg 回调方法
    */
    private void openFileChooserImpl(ValueCallback<Uri> uploadMsg) {
        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
    }

    /*
    android 5.0以上webview调用的图片方法
    @param uploadMsg 回调方法
     */
    private void openFileChooserImplForAndroid5(ValueCallback<Uri[]> uploadMsg) {
        mUploadMessageForAndroid5 = uploadMsg;
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");
        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
        startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
    }

    public void setTitleVisibility(boolean visibility) {
        Message message = new Message();
        message.what = 1;
        message.obj = visibility;
        handler.sendMessage(message);
    }

    public void loadUrl(String url) {
        browser.loadUrl(url);
    }

    public void runScript(final String scriptCommand) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                instance.loadUrl(String.format("javascript:%s", scriptCommand));
            }
        });
    }

    /**
     * 页面发生改变时清空js上层数据
     */
    public void clearData() {
        list = new ArrayList<MainTitleMenu>();
        for (int i = 0; i < allTitleList.get(classWebView).size(); i++) {
            if (2 == allTitleList.get(classWebView).get(i).getLayerSign()) {
                list.add(allTitleList.get(classWebView).get(i));
            }
        }
        allTitleList.get(classWebView).remove(list);
        for (int d = 0; d < mTitleMenu.size(); d++) {
            if (2 == mTitleMenu.get(d).getLayerSign()) {
                list.add(mTitleMenu.get(d));
            }
        }
        mTitleMenu.removeAll(list);
        list.clear();
        for (int i = 0; i < mRightMenu.size(); i++) {
            if (2 == mRightMenu.get(i).getLayerSign()) {
                list.add(mRightMenu.get(i));
            }
        }
        mRightMenu.removeAll(list);
        list.clear();
        initTitlePopWindow();
        initPopWindow();
    }

    public void setWebTitle(String title) {
        Message message = new Message();
        message.what = 2;
        message.obj = title;
        handler.sendMessage(message);
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
            if (!(url.startsWith("http") || url.startsWith("https"))) {
                return true;
            }

            final PayTask task = new PayTask(FrmMain.this);
            final String ex = task.fetchOrderInfoFromH5PayUrl(url);
            if (!TextUtils.isEmpty(ex)) {
                Log.e(LOGTAG, url);

                new Thread(new Runnable() {
                    public void run() {
                        Log.e(LOGTAG, ex);
                        final H5PayResultModel result = task.h5Pay(ex, true);
                        if (!TextUtils.isEmpty(result.getReturnUrl())) {
                            FrmMain.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e(LOGTAG, result.getReturnUrl());
                                    view.loadUrl(result.getReturnUrl());
                                }
                            });
                        } else {
                            FrmMain.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    view.goBack();
                                }
                            });
                        }
                    }
                }).start();
            } else {
                view.loadUrl(url);
            }
            return true;
        }

        @Override
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            WebResourceResponse webreq = browser.WebResponseO(request.getUrl().toString());
            return webreq != null ? webreq : super.shouldInterceptRequest(view, request);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            WebResourceResponse webreq = browser.WebResponseO(url);
            return webreq != null ? webreq : super.shouldInterceptRequest(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (!MyApp.getNetworkState(view.getContext())) return;
            currentUrl = url;
            Log.d("print", "url___: " + url);
            clearData();
            is_ERROR = false;
            /*
            if (WebConfig.getInstance() == null) return;
            is_exit = false;
            isGoHome = false;
            for (int i = 0; i < WebConfig.getInstance().getHomePagers().size(); i++) {
                if (url.contains(WebConfig.getInstance().getHomePagers().get(i).getHomeurl())) {
                    isGoHome = true;
                    is_exit = WebConfig.getInstance().getHomePagers().get(i).is_home();
                }
            }
            */

            //TODO
            for (int i = 0; i < mRightMenu.size(); i++) {
                if (mRightMenuTemp.size() > 0) {
                    if (mRightMenuTemp.get(i).getName().equals(mRightMenu.get(i + 2).getName())) {
                        mRightMenu.remove(i + 2);
                        mRightMenuTemp.remove(i);
                    }
                }
            }

            progress.setVisibility(View.VISIBLE);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            is_ERROR = true;
            super.onReceivedError(view, request, error);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            is_ERROR = true;
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (is_ERROR) {
                lblTitle.setText("出错了");
                for (int i = 0; i < titlePage.size(); i++) {
                    if (titlePage.get(i).getOnlySign() == classWebView) {
                        titlePage.get(i).setName("出错了");
                    }
                }
                tipsImage.setVisibility(View.VISIBLE);
            } else {
                lblTitle.setText(browser.getTitle());
                for (int i = 0; i < titlePage.size(); i++) {
                    if (titlePage.get(i).getOnlySign() == classWebView) {
                        titlePage.get(i).setName(browser.getTitle());
                    }
                }
                tipsImage.setVisibility(View.GONE);
            }
            /*
            if (isGoHome) {
                browser.clearHistory();
                browser.clearCache(true);
                imgBack.setVisibility(View.INVISIBLE);
            } else {
                imgBack.setVisibility(View.VISIBLE);
            }
            */
            progress.setVisibility(View.GONE);
            super.onPageFinished(view, url);
        }
    }
}

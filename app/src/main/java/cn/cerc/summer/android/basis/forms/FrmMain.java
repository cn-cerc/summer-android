package cn.cerc.summer.android.basis.forms;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

import cn.cerc.summer.android.basis.core.CommBottomPopWindow;
import cn.cerc.summer.android.basis.core.Constans;
import cn.cerc.summer.android.basis.core.MainPopupMenu;
import cn.cerc.summer.android.basis.core.MainTitleMenu;
import cn.cerc.summer.android.basis.core.MyApp;
import cn.cerc.summer.android.basis.core.ScreenUtils;
import cn.cerc.summer.android.basis.core.WebViewEntity;
import cn.cerc.summer.android.basis.db.RemoteForm;
import cn.cerc.summer.android.basis.view.BrowserView;
import cn.cerc.summer.android.basis.view.DragPointView;
import cn.cerc.summer.android.basis.view.ShowPopupWindow;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import static android.os.Build.VERSION.SDK;

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

    ImageView imgHome, imgBack, imgMore;
    TextView lblTitle;
    LinearLayout boxTitle;

    private final int REQUEST_SETTING = 101;
    private static FrmMain instance;

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
    public List<MainTitleMenu> mTitleList1;  //右侧菜单集合
    private List<MainTitleMenu> mTitleList2;
    public List<MainTitleMenu> maxListl;  //标题菜单集合
    private RelativeLayout relative_main;

    public BrowserView getBrowser() {
        return browser;
    }

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
                            JPushInterface.setAlias(FrmMain.this, MyApp.IMEI, tac);
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

    public static FrmMain getInstance() {
        return instance;
    }

    public void setHomeUrl(String homeUrl) {
        this.homeUrl = homeUrl;
        browser.loadUrl(homeUrl);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;

        settings = getSharedPreferences(Constans.SHARED_SETTING_TAB, MODE_PRIVATE);

        mTitleList1 = new ArrayList<MainTitleMenu>();
        mTitleList2 = new ArrayList<MainTitleMenu>();
        maxListl = new ArrayList<MainTitleMenu>();

        InitView();

        myApp = MyApp.getInstance();
        maxListl.add(new MainTitleMenu("返回首页", false, myApp.getStartPage(), 1));  //设置初始化数据
        maxListl.add(new MainTitleMenu("关闭页面", false, "", 1));
        maxListl.add(new MainTitleMenu("新建窗口", true, "", 1));
        mTitleList1.add(new MainTitleMenu("设置", false, "", 1));
        mTitleList1.add(new MainTitleMenu("退出系统", true, "", 1));
        browser.loadUrl(myApp.getStartPage());
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
        JPushInterface.setAlias(getApplicationContext(), MyApp.IMEI, tac);//极光推送设置别名
    }

    @SuppressLint("JavascriptInterface")
    private void InitView() {
        imgBack = (ImageView) this.findViewById(R.id.imgBack);
        imgMore = (ImageView) this.findViewById(R.id.imgMore);
        lblTitle = (TextView) this.findViewById(R.id.lblTitle);
        boxTitle = (LinearLayout) findViewById(R.id.boxTitle);
        imgBack.setOnClickListener(this);
        imgMore.setOnClickListener(this);
        lblTitle.setOnClickListener(this);

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
        maxListl.add(new MainTitleMenu(title, false, newUrl, 2));
        initTitlePopWindow();
    }

    /**
     * 获得右侧上级菜单
     *
     * @param title
     * @param newUrl
     */
    public void CatalogWebView(String title, String newUrl) {
        mTitleList1.add(new MainTitleMenu(title, false, newUrl, 2));
        initPopWindow();
    }

    public int bb = -1;  //控制关闭窗口
    //标题菜单回调的点击事件
    private CommBottomPopWindow.PopWindowListener mPopListener = new CommBottomPopWindow.PopWindowListener() {
        @Override
        public void onPopSelected(int which) {
            switch (which) {
                case 1:
                    //关闭当前页面
                    browser.goBack();
                    if (bb > 0) {
                        maxListl.remove(bb);
                        bb = -1;
                    }
                    mTitlePopWindow.dismiss();
                    initTitlePopWindow();
                    break;
                case 2:
                    //新建窗口
                    break;
                default:
                    browser.loadUrl(maxListl.get(which).getUrl());
                    bb = which;
                    mTitlePopWindow.dismiss();
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
                    Toast.makeText(FrmMain.this, "退出系统", Toast.LENGTH_SHORT).show();
                    //退出系统
                    mpopWindow.dismiss();
                    break;
                default:
                    browser.loadUrl(maxListl.get(which).getUrl());
                    mpopWindow.dismiss();
                    break;
            }
        }
    };

    /**
     * 设置标题栏点击菜单
     */
    private void initTitlePopWindow() {
        view = this.getLayoutInflater().inflate(R.layout.comm_popwindow_item, null);
        mTitlePopWindow = new CommBottomPopWindow(this);
        mTitlePopWindow.initPopItem(maxListl);
        mTitlePopWindow.setPopListener(mPopListener);
    }

    /**
     * 设置右侧弹出框
     */
    private void initPopWindow() {
        view = this.getLayoutInflater().inflate(R.layout.comm_popwindow_item, null);
        mpopWindow = new CommBottomPopWindow(this, true);
        // 带显示小标题，可加可不加
        //mPopWindow.initPopSubTitle("返回首页");
        mpopWindow.initPopItem(mTitleList1);
        mpopWindow.setPopListener(mPopListener1);
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
                mpopWindow.show(view);
                break;
            case R.id.lblTitle:
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
        boxTitle.setVisibility(visibility ? View.VISIBLE : View.GONE);
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

    private List<MainTitleMenu> list;

    /**
     * 页面发生改变时清空js上层数据
     */
    public void clealurl() {
        list = new ArrayList<MainTitleMenu>();
        for (int d = 0; d < maxListl.size(); d++) {
            if (2 == maxListl.get(d).getNum()) {
                list.add(maxListl.get(d));
            }
        }
        maxListl.removeAll(list);
        list.clear();
        for (int i = 0; i < mTitleList1.size(); i++) {
            if (2 == mTitleList1.get(i).getNum()) {
                list.add(mTitleList1.get(i));
            }
        }
        mTitleList1.removeAll(list);
        list.clear();
        initTitlePopWindow();
        initPopWindow();
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
            clealurl();
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
                tipsImage.setVisibility(View.VISIBLE);
            } else {
                lblTitle.setText(browser.getTitle());
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

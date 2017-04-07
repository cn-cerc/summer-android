package cn.cerc.summer.android.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.cerc.summer.android.Entity.Config;
import cn.cerc.summer.android.Entity.Menu;
import cn.cerc.summer.android.Interface.GetFileCallback;
import cn.cerc.summer.android.Interface.JSInterfaceLintener;
import cn.cerc.summer.android.Interface.RequestCallback;
import cn.cerc.summer.android.MyApplication;
import cn.cerc.summer.android.MyConfig;
import cn.cerc.summer.android.Receiver.MyBroadcastReceiver;
import cn.cerc.summer.android.Utils.AppUtil;
import cn.cerc.summer.android.Utils.Constans;
import cn.cerc.summer.android.Interface.JSInterface;
import cn.cerc.summer.android.Utils.FileUtil;
import cn.cerc.summer.android.Utils.PermissionUtils;

import cn.cerc.summer.android.Utils.PhotoUtils;
import cn.cerc.summer.android.Utils.ScreenUtils;
import cn.cerc.summer.android.Utils.SoundUtils;
import cn.cerc.summer.android.Utils.UMHybrid;
import cn.cerc.summer.android.Utils.XHttpRequest;
import cn.cerc.summer.android.Utils.ZXingUtils;
import cn.cerc.summer.android.View.DragPointView;
import cn.cerc.summer.android.View.MyWebView;
import cn.cerc.summer.android.View.ShowDialog;
import cn.cerc.summer.android.View.ShowPopupWindow;

import com.huagu.ehealth.R;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.MobclickAgent.UMAnalyticsConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.cerc.summer.android.View.pullTorefreshwebView.PullToRefreshBase;
import cn.cerc.summer.android.View.pullTorefreshwebView.PullToRefreshWebView;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * 通用框架
 * 主界面
 */
@SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
public class MainActivity extends BaseActivity implements View.OnLongClickListener, View.OnClickListener, JSInterfaceLintener, ActivityCompat.OnRequestPermissionsResultCallback, SoundUtils.SoundPlayerStatusLintener, RequestCallback {

    public MyWebView webview;
    private PullToRefreshWebView pullTorefreshwebView;
    private ProgressBar progress;
    private DragPointView dragpointview;//消息
    private ImageView image_tips;

    private String logoutUrl = "";

    private boolean isGoHome = false;//是否返回home
    private boolean is_ERROR = false;//是否错误了

    private RelativeLayout menu_more;
    private ImageView back, more,change_card;//返回/更多/切换卡
    private TextView title_right;//右上文字点击按键
    private TextView title;//标题

    public String homeurl;//默认打开页

//    private GoogleApiClient client;

    private String[] menus;//菜单
    private int[] menu_img = new int[]{R.mipmap.message, R.mipmap.home, R.mipmap.setting,R.mipmap.help, R.mipmap.wipe, R.mipmap.logout, R.mipmap.reload};
    private List<Menu> menulist;
    private ListPopupWindow lpw;//列表弹框

    public static final String NETWORK_CHANGE = "android.net.conn.NETWORK_CHANGE";//网络广播action
    public static final String APP_UPDATA = "com.fmk.huagu.efitness.APP_UPDATA";//更新广播action
    public static final String JSON_ERROR = "com.fmk.huagu.efitness.JSON_ERROR";//JSON错误广播action

    public static final int REQUEST_PHOTO_CAMERA = 111;//拍照请求
    public static final int REQUEST_PHOTO_CROP = 113;//裁剪请求
    public static final int REQUEST_SCAN_QRCODE = 114;//扫码请求
    public static final int REQUEST_SCAN_CARD = 115;//扫卡请求

    private final int REQUEST_SETTING = 101;//进入设置页面请求
    /** 视频全屏参数 */
    protected static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    private View customView;
    private FrameLayout fullscreenContainer;
    private WebChromeClient.CustomViewCallback customViewCallback;
    /**
     * 初始化广播
     */
    private void initbro() {
        //通过代码的方式动态注册MyBroadcastReceiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(NETWORK_CHANGE);
        filter.addAction(APP_UPDATA);
        filter.addAction(JSON_ERROR);
        //注册receiver
        registerReceiver(receiver, filter);
    }

    private static MainActivity mainactivity;

    public static MainActivity getInstance() {
        return mainactivity;
    }

    public void setHomeurl(String homeurl) {
        this.homeurl = homeurl;
        webview.loadUrl(homeurl);
    }

    /**
     * 推送消息的消息id， 点击通知栏打开
     */
    private String msgId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mainactivity = this;

        initbro();//初始化广播
        InitView();//初始化控件和相关配置

        startActivity(new Intent(this, StartActivity.class));

//        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * 查看消息的url
     */
    private String getMsgUrl(String read) {
        String url = settingShared.getString(Constans.SHARED_MSG_URL, "") + read;
        return AppUtil.buildDeviceUrl(url);
    }
    /**
     * 查看帮助中心的url
     */
    private String getHelpUrl(String help) {
        String url = MyConfig.HOME_URL + help;
        return AppUtil.buildDeviceUrl(url);
    }

    /**
     * 无论什么模式，只有activity是同一个实例的情况下，intent发生了变化，
     * 就会进入onNewIntent中，这个方法的作用也是让你来对旧的intent进行保存，
     * 对新的intent进行对应的处理。
     *
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra("msgId")) {
            msgId = intent.getStringExtra("msgId");
            String msgurl = getMsgUrl(".show") + "&msgId=" + msgId;
            Log.e("mainactivity", msgurl);
            webview.loadUrl(msgurl);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
        MobclickAgent.onResume(this);

        Set<String> set = new HashSet<String>();
        set.add("android");
        //用手机设备号来注册极光别名
        JPushInterface.setAlias(getApplicationContext(), PermissionUtils.IMEI, tac);//极光推送设置别名
    }

    private TagAliasCallback tac = new TagAliasCallback() {
        @Override
        public void gotResult(int i, String s, Set<String> set) {
            switch (i) {
                case 0://成功
                    Log.e("regsert:", "设置成功");
                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    break;
                case 6002://失败
                    // 延迟 30 秒来调用 Handler 设置别名
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //用手机设备号来注册极光别名
                            JPushInterface.setAlias(MainActivity.this, PermissionUtils.IMEI, tac);
                        }
                    }, 30000);
                    break;
                default:
            }
        }
    };

    @SuppressLint("JavascriptInterface")
    private void InitView() {
        back = (ImageView) this.findViewById(R.id.back);
        more = (ImageView) this.findViewById(R.id.more);
        change_card=(ImageView)this.findViewById(R.id.change_card);
        title = (TextView) this.findViewById(R.id.title);
        menu_more =(RelativeLayout)this.findViewById(R.id.menu_more);
        title_right =(TextView)this.findViewById(R.id.title_right);
        back.setOnClickListener(this);
        more.setOnClickListener(this);
        change_card.setOnClickListener(this);

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
        image_tips = (ImageView) this.findViewById(R.id.image_tips);

        image_tips.setOnClickListener(this);

        pullTorefreshwebView = (PullToRefreshWebView) this.findViewById(R.id.pullTorefreshwebView);
        //下拉刷新
        pullTorefreshwebView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<MyWebView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<MyWebView> refreshView) {
                webview.reload();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<MyWebView> refreshView) {
            }
        });
        webview = pullTorefreshwebView.getRefreshableView();

        webview.getSettings().setTextZoom(settingShared.getInt(Constans.SCALE_SHAREDKEY, ScreenUtils.getScales(this, ScreenUtils.getInches(this))));
//        webview.getSettings().setLoadWithOverviewMode(true);
//        webview.getSettings().setUseWideViewPort(true);

        //登陆和退出的js调用回调(接口：JSInterfaceLintener 方法：1.LoginOrLogout 2.Action)
        webview.addJavascriptInterface(new JSInterface(this), "JSobj");//JSobj 供web端js调用标识，修改请通知web开发者

//        统计sdk
        MobclickAgent.setDebugMode(true);
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.setSessionContinueMillis(1000);

        webview.setWebViewClient(new WebViewClient() {

            //在每一次请求资源时，都会通过这个函数来回调
            @Override
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                WebResourceResponse webreq = webview.WebResponseO(request.getUrl().toString());
                return webreq != null ? webreq : super.shouldInterceptRequest(view, request);
            }

            //在每一次请求资源时，都会通过这个函数来回调
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                WebResourceResponse webreq = webview.WebResponseO(url);
                return webreq != null ? webreq : super.shouldInterceptRequest(view, url);
            }

            // 在点击请求的是链接是才会调用，重写此方法返回true表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (request.getUrl().toString().startsWith("http:") || request.getUrl().toString().startsWith("https:")) {
                        return super.shouldOverrideUrlLoading(view, request);
                    }
                }
                return true;
            }

            // 在点击请求的是链接是才会调用，重写此方法返回true表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                try {
                    Log.d("UMHybrid", "shouldOverrideUrlLoading url:" + url);
                    String decodedURL = java.net.URLDecoder.decode(url, "UTF-8");
                    UMHybrid.getInstance(MainActivity.this).execute(decodedURL, view);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (url.toString().startsWith("http:") || url.startsWith("https:")) {
                    return super.shouldOverrideUrlLoading(view, url);
                }
                return true;
            }

            //在页面加载开始时调用
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                Toast.makeText(view.getContext(), url, Toast.LENGTH_SHORT).show();
                pullrefresh(true);//打开下拉刷新手势监控，避免之前的页面后台忘了打开
                if (!AppUtil.getNetWorkStata(view.getContext())) return;
                Log.e("cururl", url);

                is_ERROR = false;
                if (Config.getConfig() == null) return;
                is_exit = false;
                isGoHome = false;
                title_right.setVisibility(View.GONE);
                change_card.setVisibility(View.GONE);
                menu_more.setVisibility(View.VISIBLE);
                for (int i = 0; i < Config.getConfig().getHomePagers().size(); i++) {
                    if (url.contains(Config.getConfig().getHomePagers().get(i).getHomeurl())) {
                        isGoHome = true;
                        is_exit = Config.getConfig().getHomePagers().get(i).is_home();
                    }
                }
                progress.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            //报告错误信息
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                is_ERROR = true;
                super.onReceivedError(view, request, error);
            }

            //报告错误信息
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                is_ERROR = true;
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            //在页面加载结束时调用
            @Override
            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:setWebViewFlag()");
                if (url != null && url.contains("FrmIndex")) {
                    MobclickAgent.onPageStart(url);
                }
                pullTorefreshwebView.onPullDownRefreshComplete();

                setLastUpdateTime();
                //webview出错判断
                if (is_ERROR) {
                    title.setText("出错了");
                    image_tips.setVisibility(View.VISIBLE);
                } else {
                    if (webview.getTitle().length() < 10)
                        title.setText(webview.getTitle());
                        if(webview.getTitle().equals("我的")|webview.getTitle().equals("我的消息")|webview.getTitle().equals("就医记录")|webview.getTitle().equals("健康服务")|webview.getTitle().equals("自检记录")){
                            change_card.setVisibility(View.VISIBLE);
                        }else {
                            change_card.setVisibility(View.GONE);
                        }
                    image_tips.setVisibility(View.GONE);
                }
                //webview返回主界面判断（返回按钮的显示与隐藏）
                if (isGoHome) {
                    back.setVisibility(View.INVISIBLE);
                } else {
                    back.setVisibility(View.VISIBLE);
                }
                progress.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }
        });

        webview.setWebChromeClient(new WebChromeClient() {
            /*
                            视频全屏设置
                         */
            @Override
            public View getVideoLoadingProgressView() {
                FrameLayout frameLayout = new FrameLayout(MainActivity.this);
                frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                return frameLayout;
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                showCustomView(view, callback);
            }

            @Override
            public void onHideCustomView() {
                hideCustomView();
            }

            //配置权限
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }

            @Override
            public void onPermissionRequest(PermissionRequest request) {
                super.onPermissionRequest(request);
            }

            // 当WebView进度改变时更新窗口进
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    progress.setProgress(newProgress, true);
                else progress.setProgress(newProgress);
                super.onProgressChanged(view, newProgress);
            }

            //WebView获取页面title
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

            //当前页面有个新的favicon时候，会回调这个函数
            @Override
            public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
                super.onReceivedTouchIconUrl(view, url, precomposed);
            }
        });

        //点击后退按钮,让WebView后退一页(也可以覆写Activity的onKeyDown方法)
        webview.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK) && event.getAction() == KeyEvent.ACTION_UP) {
                    if (is_exit) {
                        Intent home = new Intent(Intent.ACTION_MAIN);
                        home.addCategory(Intent.CATEGORY_HOME);
                        startActivity(home);
                    } else {
                        if (webview.canGoBack())
                            webview.loadUrl("javascript:ReturnBtnClick()");// 返回键退回
                        else finish();
                    }
                    return true;
                } else
                    return false;
            }
        });
        webview.setOnLongClickListener(this);
    }

    private void setLastUpdateTime() {
        String text = AppUtil.formatDateTime(System.currentTimeMillis());
        pullTorefreshwebView.setLastUpdatedLabel(text);
    }


    /**
     * 退出点击的第一次的时间戳
     */
    private long timet = 0;

    /**
     * 是否直接退出
     */
    private boolean is_exit = false;

    @Override
    public boolean onLongClick(View v) {
        //webview的长按事件，设置true后webview将不会触发长按复制动作
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back://返回按钮
                if (webview.getTitle().equals("QQ登录")){
                    if(webview.canGoBack()){
                        webview.goBack();
                    }
                }else {
                    webview.loadUrl("javascript:ReturnBtnClick()");
                }

                break;
            case R.id.more://右上角更多按钮
                showPopu(more);
                break;
            case R.id.change_card://切换绑定卡号
                webview.loadUrl("javascript:appChangeCard()");
                break;
            case R.id.image_tips:
                webview.reload();
                break;

            default:
                break;
        }
    }

    public void Update() {
        try {//检查是否需要更新
            if (!AppUtil.getVersionName(this).equals(Config.getConfig().getAppVersion())) {
                ShowDialog.getDialog(this).UpDateDialogShow();
            }else {
                ShowDialog.getDialog(this).IsNewDialogShow();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean islogin = false;

    /**
     * 显示菜单栏的窗口
     * 初始化菜单
     *
     * @param view
     */
    public void showPopu(View view) {

        menulist = new ArrayList<Menu>();
        menus = getResources().getStringArray(R.array.menu);

        for (int i = 0; i < menus.length; i++) {
//            if ("退出登录".equals(menus[i]) && !islogin) continue;
            Menu menu = new Menu(i == 0 ? 12 : 0, menus[i], menu_img[i]);
            menulist.add(menu);
        }

        lpw = ShowPopupWindow.getPopupwindow().show(this, menulist);
        lpw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0://未读消息
                        webview.loadUrl(getMsgUrl(".unread"));
                        break;
//                    case 1://消息管理
//                        webview.loadUrl(getMsgUrl(""));
//                        break;
                    case 1://首页
//                        Action("","card");//测试时使用
                        webview.loadUrl(homeurl);
                        break;
                    case 2://设置
                        startActivityForResult(new Intent(MainActivity.this, SettingActivity.class), REQUEST_SETTING);
                        break;
                    case 3://帮助中心
                        webview.loadUrl(getHelpUrl("/forms/FrmAPPHelplist"));
                        break;
                    case 4://清除缓存
                        clearCacheFolder(MainActivity.this.getCacheDir(), System.currentTimeMillis());
//                        Action("","loginwx");//测试时使用
                        break;
                    case 5://退出登录
                        if (islogin) {
                            webview.loadUrl("javascript:exit()");
                            webview.clearCache(true);
                            webview.clearHistory();
                        } else
                            webview.reload();
                        break;
                    case 6://重新加载
                        webview.reload();
                        break;
                }
                lpw.dismiss();
            }
        });
        lpw.setAnchorView(view);
        lpw.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);//注销广播
    }

    /**
     * 广播接收者
     */
    private MyBroadcastReceiver receiver = new MyBroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e("xxxx", "mainactivity " + action);
            switch (action) {
                case NETWORK_CHANGE://网络广播
                    if (AppUtil.getNetWorkStata(context)) {
                        image_tips.setVisibility(View.GONE);
                        webview.reload();
                    } else {
                        image_tips.setImageResource(R.mipmap.nonework);
                        image_tips.setVisibility(View.VISIBLE);
                        ShowDialog.getDialog(context).showTips();
                    }
                    break;
                case APP_UPDATA://有更新

                    break;
                default:
                    Log.e("mainact", "mainactivity:接收到广播");
                    break;
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
        MobclickAgent.onPause(this);
    }

    @Override
    public void onStart() {
        super.onStart();

//        client.connect();
//        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (su != null)
            su.onCompletion(null);
    }

    //声音播放结束的回调
    @Override
    public void Completion() {

    }

    @Override
    public void success(String url, JSONObject json) {
        if (json != null) {
            try {
                if(url.contains("api.weixin.qq.com")){
                    Log.e("loginWx", json.toString());
                    webview.loadUrl("javascript:wxClientLogin('" + json.getString("openid") + "')");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void Failt(String url, String error) {

    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void LoginOrLogout(boolean islogin, String url) {
        this.logoutUrl = url;
        this.islogin = islogin;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (MainActivity.this.islogin) more.setVisibility(View.VISIBLE);
                else more.setVisibility(View.INVISIBLE);
            }
        });
    }


    @Override
    public void showBack(final boolean flag) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (flag) {
                    back.setVisibility(View.GONE);
                } else {
                    back.setVisibility(View.VISIBLE);
                }
            }
        });
    }



    @Override
    public void openAd(String url) {
        Intent intent = new Intent(this, ShowExternalActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    @Override
    public void pullrefresh(boolean flag) {
            pullTorefreshwebView.setPullRefreshEnabled(flag);
            pullTorefreshwebView.setPullRefreshEnabled(flag);

    }

    /**
     * web端调用方法，显示右上角文字按键命名与js的调用方法
     * @param name  按键命名
     * @param callBack js的调用方法
     */
    @Override
    public void showBtn(final String name, final String callBack, final boolean flag) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(flag){
                    title_right.setText(name);
                    title_right.setVisibility(View.VISIBLE);
                    menu_more.setVisibility(View.GONE);
                    title_right.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            webview.loadUrl("javascript:"+callBack);
                        }
                    });
                }else {
                    title_right.setVisibility(View.GONE);
                    menu_more.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public void showImage(String imgUrl) {
        Log.i("imgUlr", imgUrl);
        Intent intent = new Intent(MainActivity.this, ShowImageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("imageurl", imgUrl);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private PhotoUtils pu;//相册工具类
    private SoundUtils su;//声音工具类
    private ZXingUtils zxu;//二维码工具类

    @Override
    public void Action(String json, String action) {
        if ("camera".equals(action)) {//调用手机相机
            pu = PhotoUtils.getInstance();
            pu.setJson(json);
            //首先判断是否有权限使用摄像头
            if (PermissionUtils.getPermission(new String[]{Manifest.permission.CAMERA}, PermissionUtils.REQUEST_CAMERA_P_STATE, this))
                pu.Start_P(this, REQUEST_PHOTO_CAMERA);
        } else if ("sound".equals(action)) {//调用声音播放器
            su = SoundUtils.getInstance(this);
            su.setJson(json);
            su.startPlayer();
        }else if ("File".equals(action)) {//调用文件下载

        }else if ("zxing".equals(action)) {//调用二维码扫描
            zxu = ZXingUtils.getInstance();
            zxu.setJson(json);
            if (PermissionUtils.getPermission(new String[]{Manifest.permission.CAMERA}, PermissionUtils.REQUEST_CAMERA_Q_STATE, this))
                zxu.startScan(this, REQUEST_SCAN_QRCODE);
        } else if ("card".equals(action)) {//调用卡片扫描
            zxu = ZXingUtils.getInstance();
            zxu.setJson(json);
            if (PermissionUtils.getPermission(new String[]{Manifest.permission.CAMERA}, PermissionUtils.REQUEST_CAMERA_C_STATE, this)) {
                File file = new File(Constans.getAppPath(Constans.TESSDATA_PATH), "eng.traineddata");
                if (file.exists()) zxu.startScan(this, REQUEST_SCAN_CARD);
                else {
                    XHttpRequest.getInstance().getTess(MyConfig.HOME_URL + settingShared.getString(Constans.OCR_PATH, "/data/eng.traineddata"));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.getInstance(), "正在初始化卡识别程序", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        } else if ("call".equals(action)) {//
            phone = json;
            if (PermissionUtils.getPermission(new String[]{Manifest.permission.CALL_PHONE}, PermissionUtils.REQUEST_CALL_PHONE_STATE, this)) {
                call(json);
            }
        }else if("version".equals(action)){//获取本地android 版本名称
//            Update();
        }else if("LoginWx".equals(action)){
            String Requrl ="https://api.weixin.qq.com/sns/oauth2/access_token?appid="+MyConfig.WX_appId+"&secret="+MyConfig.WX_Secret+"&code="+json+"&grant_type=authorization_code";
					XHttpRequest.getInstance().GET(Requrl, this);
        }
    }

    String phone = "";

    public void reload(int scales) {
        webview.getSettings().setTextZoom(scales);
        webview.reload();
    }

    //打电话
    public void call(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phone);
        intent.setData(data);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case PermissionUtils.REQUEST_CAMERA_P_STATE:
                    pu.Start_P(this, REQUEST_PHOTO_CAMERA);
                    break;
                case PermissionUtils.REQUEST_CAMERA_C_STATE:
                    zxu.startScan(this, REQUEST_SCAN_CARD);
                    break;
                case PermissionUtils.REQUEST_CAMERA_Q_STATE:
                    zxu.startScan(this, REQUEST_SCAN_QRCODE);
                    break;
                case PermissionUtils.REQUEST_CALL_PHONE_STATE:
                    call(phone);
                    break;
                default:
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, requestCode);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SETTING) {
            if (resultCode == RESULT_OK) {
                homeurl = data.getStringExtra("home");
                webview.loadUrl(homeurl);
            }
        } else if (requestCode == REQUEST_PHOTO_CAMERA) {
            if (resultCode == RESULT_OK) pu.Start_Crop(REQUEST_PHOTO_CROP);
            else if (resultCode == RESULT_CANCELED)
                Toast.makeText(this, "取消拍照", Toast.LENGTH_SHORT).show();
            ;
        } else if (requestCode == REQUEST_PHOTO_CROP) {
            // 拿到剪切数据
            Bitmap bmap = data.getParcelableExtra("data");
            if (resultCode == RESULT_OK) {
                if (bmap != null)
                    pu.Cropfinish(bmap);
                else Toast.makeText(this, "裁剪图片失败", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED)
                Toast.makeText(this, "取消裁剪图片", Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_SCAN_QRCODE) {
            if (resultCode == RESULT_OK) {
                String Scanresult = data.getStringExtra("result");
//                Toast.makeText(this, Scanresult, Toast.LENGTH_SHORT).show();
                webview.loadUrl(String.format("javascript:appRichScan('%s')", Scanresult));
            }
        } else if (requestCode == REQUEST_SCAN_CARD) {
            if (resultCode == RESULT_OK) {
                String Scanresult = data.getStringExtra("result");
//                Toast.makeText(this, Scanresult, Toast.LENGTH_SHORT).show();
                webview.loadUrl(String.format("javascript:scanCall('%s')", Scanresult));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    /** 视频播放全屏 **/
    private void showCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        // if a view already exists then immediately terminate the new one
        if (customView != null) {
            callback.onCustomViewHidden();
            return;
        }

        MainActivity.this.getWindow().getDecorView();

        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
        fullscreenContainer = new FullscreenHolder(MainActivity.this);
        fullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
        decor.addView(fullscreenContainer, COVER_SCREEN_PARAMS);
        customView = view;
        setStatusBarVisibility(false);
        customViewCallback = callback;
    }
    /** 隐藏视频全屏 */
    private void hideCustomView() {
        if (customView == null) {
            return;
        }

        setStatusBarVisibility(true);
        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
        decor.removeView(fullscreenContainer);
        fullscreenContainer = null;
        customView = null;
        customViewCallback.onCustomViewHidden();
        webview.setVisibility(View.VISIBLE);
    }
    /** 全屏容器界面 */
    static class FullscreenHolder extends FrameLayout {

        public FullscreenHolder(Context ctx) {
            super(ctx);
            setBackgroundColor(ctx.getResources().getColor(android.R.color.black));
        }

        @Override
        public boolean onTouchEvent(MotionEvent evt) {
            return true;
        }
    }
    private void setStatusBarVisibility(boolean visible) {
        int flag = visible ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setFlags(flag, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}

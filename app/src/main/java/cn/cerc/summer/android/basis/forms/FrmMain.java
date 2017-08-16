package cn.cerc.summer.android.basis.forms;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
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
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.util.H5PayResultModel;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mimrc.vine.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.cerc.summer.android.basis.core.WebConfig;
import cn.cerc.summer.android.basis.core.MainPopupMenu;
import cn.cerc.summer.android.basis.core.MyApp;
import cn.cerc.summer.android.basis.core.MyBroadcastReceiver;
import cn.cerc.summer.android.basis.core.Constans;
import cn.cerc.summer.android.basis.core.PermissionUtils;
import cn.cerc.summer.android.basis.core.ScreenUtils;
import cn.cerc.summer.android.basis.view.DragPointView;
import cn.cerc.summer.android.basis.view.BrowserView;
import cn.cerc.summer.android.basis.view.ShowDialog;
import cn.cerc.summer.android.basis.view.ShowPopupWindow;
import cn.cerc.summer.android.parts.music.FrmCaptureMusic;
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
    private boolean isGoHome = false;//是否返回home
    private boolean is_ERROR = false;//是否错误了
    private ImageView backImage, moreImage;
    private TextView title;
    private GoogleApiClient client;
    private String[] menus;//菜单
    private int[] menu_img = new int[]{R.mipmap.message, R.mipmap.msg_manager, R.mipmap.home, R.mipmap.setting, R.mipmap.wipe, R.mipmap.logout, R.mipmap.reload};
    private List<MainPopupMenu> menuList;
    private ListPopupWindow popupWindow;//列表弹框

    public BrowserView getBrowser(){return browser;};

    /**
     * 推送消息的消息id， 点击通知栏打开
     */
    private String msgId = "";
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
                            JPushInterface.setAlias(FrmMain.this, PermissionUtils.IMEI, tac);
                        }
                    }, 30000);
                    break;
                default:
            }
        }
    };
    /**
     * 退出点击的第一次的时间戳
     */
    private long timet = 0;
    /**
     * 是否直接退出
     */
    private boolean is_exit = false;
    /**
     * 广播接收者
     */
    private MyBroadcastReceiver receiver = new MyBroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e("xxxx", "instance " + action);
            switch (action) {
                case NETWORK_CHANGE:
                    if (MyApp.getNetworkState(context)) browser.reload();
                    else ShowDialog.getDialog(context).showTips();
                    break;
                case APP_UPDATA://有更新
                    break;
                default:
                    Log.e("mainact", "instance:接收到广播");
                    break;
            }
        }
    };

    public static FrmMain getInstance() {
        return instance;
    }

    /**
     * 初始化广播
     */
    private void initbro() {
        //通过代码的方式动态注册MyBroadcastReceiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(NETWORK_CHANGE);
        filter.addAction(JSON_ERROR);
        filter.addAction(APP_UPDATA);
        //注册receiver
        registerReceiver(receiver, filter);
    }

    public void setHomeUrl(String homeUrl) {
        this.homeUrl = homeUrl;
        browser.loadUrl(homeUrl);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings = getSharedPreferences(Constans.SHARED_SETTING_TAB, MODE_PRIVATE);

        instance = this;

        initbro();
        InitView();

        startActivity(new Intent(this, FrmStart.class));

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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
            Log.e("instance", msgurl);
            browser.loadUrl(msgurl);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SETTING) {
            if (resultCode == RESULT_OK) {
                browser.loadUrl(data.getStringExtra("home"));
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;

        } else if (requestCode == FILECHOOSER_RESULTCODE_FOR_ANDROID_5) {
            if (null == mUploadMessageForAndroid5)
                return;
            Uri result = (data == null || resultCode != RESULT_OK) ? null : data.getData();
            if (result != null) {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{result});
            } else {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
            }
            mUploadMessageForAndroid5 = null;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);

        Set<String> set = new HashSet<String>();
        set.add("android");
        JPushInterface.setAlias(getApplicationContext(), PermissionUtils.IMEI, tac);//极光推送设置别名
    }

    @SuppressLint("JavascriptInterface")
    private void InitView() {
        backImage = (ImageView) this.findViewById(R.id.back);
        moreImage = (ImageView) this.findViewById(R.id.more);
        title = (TextView) this.findViewById(R.id.title);
        backImage.setOnClickListener(this);
        moreImage.setOnClickListener(this);

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

        browser = (BrowserView) this.findViewById(R.id.webview);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                browser.goBack();
                break;
            case R.id.more:
                showPopupMenu(moreImage);
                break;
            default:
                break;
        }
    }

    public void checkUpdate() {
        try {//检查是否需要更新
            if (!MyApp.getVersionName(this).equals(WebConfig.getInstance().getAppVersion())) {
                ShowDialog.getDialog(this).UpDateDialogShow();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示菜单栏的窗口
     *
     * @param view
     */
    public void showPopupMenu(View view) {
        menuList = new ArrayList<MainPopupMenu>();
        menus = getResources().getStringArray(R.array.mainPopupMenu);

        for (int i = 0; i < menus.length; i++) {
            if ("退出登录".equals(menus[i]) && !islogin) continue;
            MainPopupMenu mainPopupMenu = new MainPopupMenu(i == 0 ? 12 : 0, menus[i], menu_img[i]);
            menuList.add(mainPopupMenu);
        }

        popupWindow = ShowPopupWindow.getPopupwindow().show(this, menuList);
        popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        browser.loadUrl(getMsgUrl(".unread"));
                        break;
                    case 1:
                        browser.loadUrl(getMsgUrl(""));
                        break;
                    case 2:
                        browser.loadUrl(homeUrl);
                        break;
                    case 3:
//                        startActivityForResult(new Intent(FrmMain.this, FrmSettings.class).putExtra("address", browser.getUrl()), REQUEST_SETTING);
                        startActivity(new Intent(FrmMain.this, FrmCaptureMusic.class));
                        break;
                    case 4:
                        clearCacheFolder(FrmMain.this.getCacheDir(), System.currentTimeMillis());
                        break;
                    case 5:
                        if (islogin) {
                            if (!TextUtils.isEmpty(logoutUrl)) {
                                browser.loadUrl(logoutUrl);
                                browser.clearCache(true);
                                browser.clearHistory();
                            }
                        } else
                            browser.reload();
                        break;
                    case 6:
                        browser.reload();
                        break;
                }
                popupWindow.dismiss();
            }
        });
        popupWindow.setAnchorView(view);
        popupWindow.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);//注销广播
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

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for moreImage information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page")
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW).setObject(object).setActionStatus(Action.STATUS_TYPE_COMPLETED).build();
    }

    @Override
    public void onStart() {
        super.onStart();

        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
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

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
            if (!(url.startsWith("http") || url.startsWith("https"))) {
                return true;
            }

            final PayTask task = new PayTask(FrmMain.this);
            final String ex = task.fetchOrderInfoFromH5PayUrl(url);
            if (!TextUtils.isEmpty(ex)) {
                Log.e("url:::", url);
                new Thread(new Runnable() {
                    public void run() {
                        Log.e("ex:::", ex);
                        final H5PayResultModel result = task.h5Pay(ex, true);
                        if (!TextUtils.isEmpty(result.getReturnUrl())) {
                            FrmMain.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("return url:::", result.getReturnUrl());
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
            Log.e("cururl", url);
            is_ERROR = false;
            if (WebConfig.getInstance() == null) return;
            is_exit = false;
            isGoHome = false;
            for (int i = 0; i < WebConfig.getInstance().getHomePagers().size(); i++) {
                if (url.contains(WebConfig.getInstance().getHomePagers().get(i).getHomeurl())) {
                    isGoHome = true;
                    is_exit = WebConfig.getInstance().getHomePagers().get(i).is_home();
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
                title.setText("出错了");
                tipsImage.setVisibility(View.VISIBLE);
            } else {
                title.setText(browser.getTitle());
                tipsImage.setVisibility(View.GONE);
            }
            if (isGoHome) {
                browser.clearHistory();
                browser.clearCache(true);
                backImage.setVisibility(View.INVISIBLE);
            } else {
                backImage.setVisibility(View.VISIBLE);
            }
            progress.setVisibility(View.GONE);
            super.onPageFinished(view, url);
        }
    }
}

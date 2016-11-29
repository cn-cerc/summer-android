package com.fmk.huagu.efitness.Activity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.fmk.huagu.efitness.Entity.Menu;
import com.fmk.huagu.efitness.R;
import com.fmk.huagu.efitness.Receiver.MyBroadcastReceiver;
import com.fmk.huagu.efitness.Utils.Constans;
import com.fmk.huagu.efitness.Utils.JSInterface;
import com.fmk.huagu.efitness.View.DragPointView;
import com.fmk.huagu.efitness.View.ShowDialog;
import com.fmk.huagu.efitness.View.ShowPopupWindow;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.jpush.android.data.JPushLocalNotification;

/**
 * 主界面
 */
public class MainActivity extends BaseActivity implements View.OnLongClickListener, View.OnClickListener{

    private WebView webview;
    private WebSettings websetting;
    private ProgressBar progress;
    private DragPointView dragpointview;
    private ImageView image_tips;

    private boolean isGoHome = false;//是否返回home
    private boolean is_ERROR = false;//是否错误了

    private ImageView back, more;
    private TextView title;

    private String homeurl;//默认打开页

    private GoogleApiClient client;

    private String[] menus;//菜单
    private int[] menu_img = new int[]{R.mipmap.message,R.mipmap.msg_manager,R.mipmap.home,R.mipmap.setting,R.mipmap.home,R.mipmap.home};
    private List<Menu> menulist;
    private ListPopupWindow lpw;//列表弹框

    public static final String NETWORK_CHANGE = "android.net.conn.NETWORK_CHANGE";
    public static final String APP_UPDATA = "com.fmk.huagu.efitness.APP_UPDATA";
    public static final String JSON_ERROR = "com.fmk.huagu.efitness.JSON_ERROR";
    public static final String PACKAGE_ADDED = "android.intent.action.PACKAGE_ADD";
    private void initbro() {
        //通过代码的方式动态注册MyBroadcastReceiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(NETWORK_CHANGE);
        filter.addAction(APP_UPDATA);
        filter.addAction(JSON_ERROR);
        filter.addAction(PACKAGE_ADDED);
        //注册receiver
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_main);

        initbro();
        inithome();
        InitView();

        JPushLocalNotification jPushLocalNotification = new JPushLocalNotification();
//        jPushLocalNotification.setContent("updateAPP");
//        jPushLocalNotification.setTitle("更新");
        jPushLocalNotification.setBuilderId(2);
        jPushLocalNotification.setNotificationId(123);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("action","update");
        JSONObject obj = new JSONObject(map);
        jPushLocalNotification.setExtras(obj.toString());
        JPushInterface.addLocalNotification(this,jPushLocalNotification);

        startActivity(new Intent(this, GuidanceActivity.class));
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private List<String> homelist;

    private void inithome() {
        homelist = new ArrayList<String>();
        homelist.add("Login");
        homelist.add("Registered");
        homelist.add("forms/FrmIndex");
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
        if (TextUtils.isEmpty(settingShared.getString(Constans.HOME, ""))) {
            homeurl = Constans.HOME_URL + "?device=android&deviceid=" + IMEI;
        } else {
            homeurl = settingShared.getString(Constans.HOME, "") + "?device=android&deviceid=" + IMEI;
        }
        homelist.add(homeurl);

        Log.e("IMEI", "IMEI: " + IMEI);
        websetting.setTextZoom(Integer.valueOf(settingShared.getInt(Constans.SCALE_SHAREDKEY, 90)));
//        websetting.setTextZoom();
        webview.loadUrl(homeurl);

        Set<String> set = new HashSet<String>();
        set.add("android");
        JPushInterface.setAlias(getApplicationContext(), IMEI, tac);//极光推送设置别名


    }

    private TagAliasCallback tac = new TagAliasCallback() {
        @Override
        public void gotResult(int i, String s, Set<String> set) {
            switch (i) {
                case 0://成功
                    Log.e("regsert:","设置成功");
                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    break;
                case 6002://失败
                    // 延迟 60 秒来调用 Handler 设置别名
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            JPushInterface.setAlias(MainActivity.this, IMEI, tac);
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
        title = (TextView) this.findViewById(R.id.title);
        back.setOnClickListener(this);
        more.setOnClickListener(this);

        dragpointview = (DragPointView) this.findViewById(R.id.dragpointview);
        dragpointview.setEnable(false);
        dragpointview.setClickable(false);
        dragpointview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        progress = (ProgressBar) this.findViewById(R.id.progress);
        image_tips = (ImageView) this.findViewById(R.id.image_tips);

        webview = (WebView) this.findViewById(R.id.webview);

        websetting = webview.getSettings();
        websetting.setJavaScriptEnabled(true);
        websetting.setJavaScriptCanOpenWindowsAutomatically(true);
        websetting.setDomStorageEnabled(true);
        websetting.setGeolocationEnabled(true);
        websetting.setUseWideViewPort(true);
        websetting.setAppCacheEnabled(true);
        websetting.setCacheMode(WebSettings.LOAD_DEFAULT);
        websetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        //自适应屏幕
        websetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        websetting.setLoadWithOverviewMode(true);
        webview.setHorizontalScrollBarEnabled(true);
        webview.setHorizontalFadingEdgeEnabled(true);
        webview.addJavascriptInterface(new JSInterface(), "JSobj");//hello2Html

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (request.getUrl().toString().startsWith("http:") || request.getUrl().toString().startsWith("https:")) {
                        return super.shouldOverrideUrlLoading(view, request);
                    }
                }
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.toString().startsWith("http:") || url.startsWith("https:")) {
                    return super.shouldOverrideUrlLoading(view, url);
                }
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                is_ERROR = false;
                for (int i=0;i<homelist.size();i++){
                    if (url.contains(homelist.get(i))) {
                        isGoHome = false;
                        break;
                    } else {
                        isGoHome = true;
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
                    image_tips.setVisibility(View.VISIBLE);
                } else {
                    title.setText(webview.getTitle());
                    image_tips.setVisibility(View.GONE);
                }
                if (isGoHome) {
                    back.setVisibility(View.VISIBLE);
                } else {
                    webview.clearHistory();
                    webview.clearCache(true);
                    back.setVisibility(View.INVISIBLE);
                }
                progress.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }
        });

        webview.setWebChromeClient(new WebChromeClient() {
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    progress.setProgress(newProgress, true);
                } else {
                    progress.setProgress(newProgress);
                }
                MainActivity.this.setProgress(newProgress);
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
        webview.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK) && event.getAction() == KeyEvent.ACTION_UP) {
                    if (!isGoHome)
                        finish();
                    else
                        webview.goBack();// 返回键退回
                    return true;
                } else
                    return false;
            }
        });
        webview.setOnLongClickListener(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RESULT_OK) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onLongClick(View v) {
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                if (!isGoHome)
                    finish();
                else
                    webview.goBack();
                break;
            case R.id.home:
                webview.loadUrl(homeurl);
                break;
            case R.id.more:
                showPopu(more);
                break;
            default:
                break;
        }
    }

    /**
     * 显示菜单栏的窗口
     *
     * @param view
     */
    public void showPopu(View view) {
        if (menulist == null) {
            menulist = new ArrayList<Menu>();
            menus = getResources().getStringArray(R.array.menu);
            for (int i = 0; i < menus.length; i++) {
                Menu menu = new Menu(i==0?12:0, menus[i], menu_img[i]);
                menulist.add(menu);
            }
        }

        lpw = ShowPopupWindow.getPopupwindow().show(this,menulist);
        lpw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        setNotification();
                        break;
                    case 1:
                        manager.cancel(100);
                        break;
                    case 2:
                        webview.loadUrl(homeurl);
                        break;
                    case 3:
                        startActivity(new Intent(MainActivity.this, SettingActivity.class));
                        break;
                    case 4:
                        clearCacheFolder(MainActivity.this.getCacheDir(), System.currentTimeMillis());
                        break;
                    case 5:
                        webview.reload();
                        break;
                }
                lpw.dismiss();
            }
        });
        lpw.setAnchorView(view);
        lpw.show();
    }

    private RemoteViews remoteViews;
    private NotificationManager manager;

    /**
     * 设置通知
     */
    private void setNotification() {
        if (manager == null)
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        remoteViews = new RemoteViews(getPackageName(), R.layout.customnotice);
        remoteViews.setImageViewResource(R.id.widget_album, R.mipmap.ic_launcher);
        remoteViews.setTextViewText(R.id.widget_title, "name、name");
        remoteViews.setTextViewText(R.id.widget_artist, "歌手");
        remoteViews.setImageViewResource(R.id.widget_play, android.R.drawable.ic_media_pause);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        Intent intent = new Intent(this, MainActivity.class);
        // 点击跳转到主界面
        PendingIntent intent_go = PendingIntent.getActivity(this, 5, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notice, intent_go);

        Intent closeintent = new Intent();
        // 4个参数context, requestCode, intent, flags
        PendingIntent intent_close = PendingIntent.getBroadcast(this, 7, closeintent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_close, intent_close);

        // 设置上一曲
        Intent prv = new Intent();
        PendingIntent intent_prev = PendingIntent.getBroadcast(this, 1, prv, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_prev, intent_prev);

        // 设置播放
        Intent playorpause = new Intent();
        PendingIntent intent_play = PendingIntent.getBroadcast(this, 2, playorpause, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_play, intent_play);

        // 下一曲
        Intent next = new Intent();
//        next.setAction(ACTION_NEXT);
        PendingIntent intent_next = PendingIntent.getBroadcast(this, 3, next,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_next, intent_next);

        // 设置收藏
        PendingIntent intent_fav = PendingIntent.getBroadcast(this, 4, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_fav, intent_fav);

        builder.setSmallIcon(R.mipmap.ic_launcher); // 设置顶部图标

        Notification notify = builder.build();
        notify.contentView = remoteViews; // 设置下拉图标
        notify.bigContentView = remoteViews; // 防止显示不完全,需要添加apisupport
        notify.flags = Notification.FLAG_ONGOING_EVENT;
        notify.icon = R.mipmap.ic_launcher;

        manager.notify(100, notify);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);//注销广播
    }

    private ShowDialog sd;

    /**
     * 广播接收者
     */
    private MyBroadcastReceiver receiver = new MyBroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e("xxxx","00000000 "+action);
            switch (action){
                case NETWORK_CHANGE:
                    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeInfo != null && activeInfo.getState() == NetworkInfo.State.CONNECTED) {
                        webview.reload();
                    }
                    break;
                case APP_UPDATA:
                    sd = ShowDialog.getDialog(context).UpDateDialogShow();
                    break;
                case PACKAGE_ADDED:
                    sd.openApk();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    /**
     * clear the cache before time numDays
     */
    private int clearCacheFolder(File dir, long numDays) {
        int deletedFiles = 0;
        if (dir != null && dir.isDirectory()) {
            try {
                for (File child : dir.listFiles()) {
                    if (child.isDirectory()) {
                        deletedFiles += clearCacheFolder(child, numDays);
                    }
                    if (child.lastModified() < numDays) {
                        if (child.delete()) {
                            deletedFiles++;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return deletedFiles;
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
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
}

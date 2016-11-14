package com.fmk.huagu.efitness.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fmk.huagu.efitness.R;
import com.fmk.huagu.efitness.Utils.Constans;
import com.fmk.huagu.efitness.Utils.JSInterface;
import com.fmk.huagu.efitness.Utils.ScreenUtils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import static android.R.attr.screenDensity;
import static android.R.attr.start;

/**
 * 主界面
 */
public class MainActivity extends BaseActivity implements View.OnLongClickListener, View.OnClickListener, PopupMenu.OnMenuItemClickListener, ScaleGestureDetector.OnScaleGestureListener/*, View.OnTouchListener */{

    private WebView webview;
    private ProgressBar progress;
    private ImageView image_tips;

    private boolean isGoHome = false;//是否返回home
    private boolean is_ERROR = false;//是否错误了

    private ImageView back, home, more;
    private TextView title;

    private String homeurl;//默认打开页

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_main);

        initbro();
        InitView();

//        window.devicePixelRatio;

        startActivity(new Intent(this, GuidanceActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (TextUtils.isEmpty(settingShared.getString(Constans.HOME, ""))) {
            homeurl = Constans.HOME_URL + "?device=android&deviceid=" + IMEI;
        } else {
            homeurl = settingShared.getString(Constans.HOME, "") + "?device=android&deviceid=" + IMEI;
        }
        if (!TextUtils.isEmpty(settingShared.getString(Constans.SCALE_SHAREDKEY, ""))) {
            webview.setInitialScale((int) Double.parseDouble(settingShared.getString(Constans.SCALE_SHAREDKEY, "")));
            setWidth(webview, 1f);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                webview.zoomBy(20f);
//            }else{
//                setScaleValue(webview);
//            }
        }
        webview.loadUrl(homeurl);

        Set<String> set = new HashSet<String>();
        set.add("android");
        JPushInterface.setAliasAndTags(this, "12345612", set, tac);

        JPushInterface.onResume(this);
    }

    private TagAliasCallback tac = new TagAliasCallback() {
        @Override
        public void gotResult(int i, final String s, Set<String> set) {
            switch (i) {
                case 0://成功
                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    break;
                case 6002://失败
                    // 延迟 60 秒来调用 Handler 设置别名
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            JPushInterface.setAlias(MainActivity.this, s, tac);
                        }
                    }, 30000);
                    break;
                default:
            }
        }
    };

    private void initbro() {
        //通过代码的方式动态注册MyBroadcastReceiver
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        //注册receiver
        registerReceiver(receiver, filter);
    }

    @SuppressLint("JavascriptInterface")
    private void InitView() {
        back = (ImageView) this.findViewById(R.id.back);
        home = (ImageView) this.findViewById(R.id.home);
        more = (ImageView) this.findViewById(R.id.more);
        title = (TextView) this.findViewById(R.id.title);
        back.setOnClickListener(this);
        home.setOnClickListener(this);
        more.setOnClickListener(this);

        progress = (ProgressBar) this.findViewById(R.id.progress);
        image_tips = (ImageView) this.findViewById(R.id.image_tips);

        webview = (WebView) this.findViewById(R.id.webview);

        WebSettings websetting = webview.getSettings();
        websetting.setJavaScriptEnabled(true);
        websetting.setJavaScriptCanOpenWindowsAutomatically(true);
        websetting.setDomStorageEnabled(true);
        websetting.setGeolocationEnabled(true);
        websetting.setSupportZoom(true);
        websetting.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        websetting.setDisplayZoomControls(true);
        websetting.setBuiltInZoomControls(true);
        websetting.setUseWideViewPort(true);
        websetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        //自适应屏幕
        websetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        websetting.setLoadWithOverviewMode(true);
        webview.setHorizontalScrollBarEnabled(true);
        webview.setHorizontalFadingEdgeEnabled(true);
        webview.addJavascriptInterface(new JSInterface(), "jsObj");//hello2Html

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
                if (url.contains(Constans.HOME_URL+"/forms/FrmIndex") || url.contains("Login")) {
                    isGoHome = false;
                    home.setVisibility(View.INVISIBLE);
                } else {
                    isGoHome = true;
                    home.setVisibility(View.VISIBLE);
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

        mScaleGestureDetector = new ScaleGestureDetector(MainActivity.this, this);
//        webview.setOnTouchListener(this);
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
                showPopu(v);
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
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.main_activity_action, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);//注销广播
//         清除缓存的代码
//        File file = .getCacheFileBaseDir();
//        if (file != null && file.exists() && file.isDirectory()) {
//            for (File item : file.listFiles()) {
//                item.delete();
//            }
//            file.delete();
//        }
//        this.deleteDatabase("webview.db");
//        this.deleteDatabase("webviewCache.db");
    }

    /**
     * 广播接收者
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
            if (activeInfo != null && activeInfo.getState() == NetworkInfo.State.CONNECTED) {
                webview.reload();
            }
        }
    };

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.text1:
                webview.reload();
                break;
            case R.id.text2:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.text3:
                clearCacheFolder(this.getCacheDir(), System.currentTimeMillis());
                break;
//            case R.id.text4:
//                webview.zoomIn();
//                webview.reload();
//                break;
        }
        return false;
    }

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

    public void setWidth(WebView view, float scale) {
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        llp.width = (int) ((float) ScreenUtils.getScreenWidth(this) * scale);
        view.setLayoutParams(llp);
    }

    /**
     * 缩放的手势检测
     */
    private ScaleGestureDetector mScaleGestureDetector = null;
    private float scaleFactor = 1f;

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        scaleFactor *= detector.getScaleFactor();
        if (scaleFactor > 1.5) {
            scaleFactor = 1.5f;
            setWidth(webview, scaleFactor);
        } else if (scaleFactor <= 1) {
            scaleFactor = 1f;
            setWidth(webview, scaleFactor);
        } else
            setWidth(webview, scaleFactor);
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
    }

    float xx, yy;

//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        if (event.getPointerCount() <= 1) {
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    xx = event.getX();
//                    yy = event.getY();
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    if (Math.abs(xx - event.getX()) > Math.abs(yy - event.getY())) {
//                        return false;
//                    }else {
//                        return true;
//                    }
//            }
//            return false;
//        }
//        return mScaleGestureDetector.onTouchEvent(event);
//    }
}

package com.fmk.huagu.efitness.Activity;

import android.annotation.SuppressLint;
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
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.PopupMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fmk.huagu.efitness.R;
import com.fmk.huagu.efitness.Utils.Constans;
import com.fmk.huagu.efitness.Utils.JSInterface;

import java.net.URL;

import static android.R.attr.start;

/**
 * 主界面
 */
public class MainActivity extends BaseActivity implements View.OnLongClickListener, View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private WebView webview;
    private ProgressBar progress;
    private ImageView image_tips;

    private boolean isGoHome = false;//是否返回home
    private boolean is_ERROR = false;//是否错误了

    private ImageView back,home,more;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initbro();

        InitView();

        startActivity(new Intent(this, StartActivity.class));
    }

    private void initbro() {
        //通过代码的方式动态注册MyBroadcastReceiver
        IntentFilter filter=new IntentFilter();
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
        websetting.setDisplayZoomControls(true);
        websetting.setBuiltInZoomControls(true);
        websetting.setUseWideViewPort(true);
        webview.loadUrl(Constans.LOGIN_URL);
//        webview.setInitialScale(500);
        //自适应屏幕
        websetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        websetting.setLoadWithOverviewMode(true);
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
                if (url.equals(Constans.HOME_URL) || url.equals(Constans.LOGIN_URL)) {
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
                if (is_ERROR){
                    title.setText("出错了");
                    image_tips.setVisibility(View.VISIBLE);
                }else{
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
                    progress.setProgress(newProgress,true);
                }else{
                    progress.setProgress(newProgress);
                }
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
                if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                    if (webview.canGoBack())
                        webview.goBack();// 返回键退回
                    else
                        finish();
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
        switch (v.getId()){
            case R.id.back:
                if (webview.canGoBack())
                    webview.goBack();
                else
                    finish();
                break;
            case R.id.home:
                webview.loadUrl(Constans.HOME_URL);
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
     * @param view
     */
    public void showPopu(View view){
        PopupMenu popupMenu = new PopupMenu(this,view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.main_activity_action, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);//注销广播
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
            if (activeInfo != null && activeInfo.getState() == NetworkInfo.State.CONNECTED){
                webview.reload();
            }
        }
    };


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.text1:
                webview.reload();
                break;
        }
        return false;
    }
}

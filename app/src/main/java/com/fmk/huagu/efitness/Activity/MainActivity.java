package com.fmk.huagu.efitness.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

/**
 * 主界面
 */
public class MainActivity extends BaseActivity implements View.OnLongClickListener {


    private WebView webview;
    private ProgressBar progress;
    private ActionBar actionbar;
    private ImageView image_tips;

    private boolean isGoHome = false;//是否返回home
    private boolean is_ERROR = false;//是否错误了

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionbar = getSupportActionBar();
        actionbar.setHomeButtonEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);
//        actionbar.setHomeAsUpIndicator();
        actionbar.setTitle(getString(R.string.app_name));
        actionbar.setElevation(0);
//        TextView textview = new TextView(this);
//        textview.setText("陕西");
//        actionbar.setCustomView(textview);
//        actionbar.setDisplayShowCustomEnabled(true);
//        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        actionbar.setHomeAsUpIndicator(R.mipmap.ic_launcher);
        InitView();

        startActivity(new Intent(this, GuidanceActivity.class));
    }

    @SuppressLint("JavascriptInterface")
    private void InitView() {
        progress = (ProgressBar) this.findViewById(R.id.progress);
        image_tips = (ImageView) this.findViewById(R.id.image_tips);

        webview = (WebView) this.findViewById(R.id.webview);
        webview.loadUrl(Constans.LOGIN_URL);
        WebSettings websetting = webview.getSettings();
        websetting.setJavaScriptEnabled(true);
        websetting.setJavaScriptCanOpenWindowsAutomatically(true);
        websetting.setDomStorageEnabled(true);
        websetting.setGeolocationEnabled(true);
        websetting.setSupportZoom(true);
        webview.addJavascriptInterface(new JSInterface(), "jsObj");//hello2Html
//        websetting.setTextZoom(5);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (request.getUrl().toString().startsWith("http:") || request.getUrl().toString().startsWith("https:")) {
                    return super.shouldOverrideUrlLoading(view, request);
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
                    actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
                } else {
                    isGoHome = true;
                    actionbar.setHomeAsUpIndicator(R.drawable.ic_home_black_24dp);
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
                    actionbar.setTitle("错误了");
                    image_tips.setVisibility(View.VISIBLE);
                }else{
                    actionbar.setTitle(webview.getTitle());
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
                if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
                    // 返回键退回
                    webview.goBack();
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
    public boolean onSupportNavigateUp() {
        if (isGoHome) {
            webview.loadUrl(Constans.HOME_URL);
            isGoHome = false;
            return true;
        }
        if (webview.getUrl().equals(Constans.HOME_URL))
            finish();
        webview.goBack();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.text1:
                webview.reload();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onLongClick(View v) {
        return true;
    }
}

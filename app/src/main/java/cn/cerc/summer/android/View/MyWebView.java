package cn.cerc.summer.android.View;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import cn.cerc.summer.android.Utils.Constans;

/**
 * Created by fff on 2016/12/3.
 */

public class MyWebView extends WebView {

    private WebSettings websetting;

    public MyWebView(Context context) {
        super(context);
        init(context);
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public void init(Context context) {
        websetting = getSettings();
        websetting.setJavaScriptEnabled(true);//如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        websetting.setJavaScriptCanOpenWindowsAutomatically(true);////支持通过JS打开新窗口
        websetting.setDomStorageEnabled(true);//开启 DOM storage API 功能
        websetting.setGeolocationEnabled(true);//定位是否可用，默认为true
        websetting.setUseWideViewPort(true);//将图片调整到适合webview的大小
        websetting.setAppCacheEnabled(true);//开启 Application Caches 功能
        websetting.setCacheMode(WebSettings.LOAD_DEFAULT);//没网，则从本地获取，即离线加载
        websetting.setRenderPriority(WebSettings.RenderPriority.HIGH);//提高渲染的优先级
        //自适应屏幕
        websetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        websetting.setLoadWithOverviewMode(true);// 缩放至屏幕的大小

    }

    public WebResourceResponse getWebResponse(String type, InputStream input) {
        return new WebResourceResponse("text/" + type, "UTF-8", input);
    }

    //获取js/css
    public WebResourceResponse WebResponseO(String url) {
        String type = "";
        if (url.contains(".css")) type = "css";
        else if (url.contains(".js")) type = "js";
        else if (url.contains(".png")) type = "png";
        else if (url.contains(".jpg") | url.contains(".jpeg")) type = "jpg";
        else if (url.contains(".gif")) type = "gif";
        else return null;

        String filename = url.substring(url.indexOf("com") + 3);
        File file = new File(Constans.getAppPath(Constans.DATA_PATH));
        String fileurl = file.getAbsolutePath() + filename;
        File files = new File(fileurl);
        if (!(files.isFile() && files.exists()))
            return null;
        try {
            InputStream input = new FileInputStream(fileurl);
            return getWebResponse(type, input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


}

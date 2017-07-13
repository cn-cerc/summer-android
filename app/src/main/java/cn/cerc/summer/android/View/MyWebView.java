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

        //TODO Android5.0以上默认不允许加载非https站点内容 2017-07-13_黄荣君，升级服务器完成后需删除
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            websetting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    public WebResourceResponse getWebResponse(String type, InputStream input) {
        return new WebResourceResponse("text/" + type, "UTF-8", input);
    }

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

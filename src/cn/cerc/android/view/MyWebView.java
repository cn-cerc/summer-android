package cn.cerc.android.view;

import org.apache.http.util.EncodingUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings.Secure;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage.QuotaUpdater;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.cerc.android.activity.MainActivity;
import cn.cerc.android.js.JsClass;
import cn.cerc.android.net.LocalConfig;

public class MyWebView extends WebView
{
    // public static final String BASE_URL = "http://vine.aliapp.com/service/";
    // public static final String URL_END = "?m=Welcome";
    public static final String BASE_URL = "http://sb.knowall.cn/form/";
    // public static final String BASE_URL =
    // "http://sm.knowall.cn/form/TFrmWelcome.getBarcode";
    // public static final String URL_END = "";
    public static final String URL_END = "?device=phone";

    // public static final String HOME_URL =
    // "http://vine.aliapp.com/service/?m=Welcome";
    public static final String DEVICE_NULL = "null";
    private Listener mListener;
    // 是否正在加载网页
    private boolean isLoadding = false;

    private ProgressBar mProgressBar;
    private TextView mTextView;
    private Context mContext;
    private InputListener mInputListener;

    public MyWebView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mContext = context;
        setHorizontalScrollBarEnabled(false);
        WebSettings setting = getSettings();
        setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        setting.setSupportZoom(true);
        setting.setBuiltInZoomControls(false);
        setting.setJavaScriptEnabled(true);
        setting.setJavaScriptCanOpenWindowsAutomatically(true);
        setting.setDomStorageEnabled(true);
        setting.setAppCacheMaxSize(1024 * 1024 * 16);// 设置缓冲大小，我设的是16M
        String appCacheDir = mContext.getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
        setting.setAppCachePath(appCacheDir);
        setting.setAllowFileAccess(true);
        setting.setAppCacheEnabled(true);
        setting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        setScale();
        setHorizontalScrollbarOverlay(true);

        setDownloadListener(new DownloadListener()
        {

            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                    long contentLength)
            {
                // 系统默认浏览器
                Uri uri = Uri.parse(url);
                Intent downloadIntent = new Intent(Intent.ACTION_VIEW, uri);
                mContext.startActivity(downloadIntent);

                // 系统下载管理器下载
                // DownloadManager downloadManager = (DownloadManager)
                // mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                //
                // Uri uri = Uri.parse(url);
                // Request request = new Request(uri);
                //
                // //设置允许使用的网络类型，这里是移动网络和wifi都可以
                // request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE|DownloadManager.Request.NETWORK_WIFI);
                //
                // //禁止发出通知，既后台下载，如果要使用这一句必须声明一个权限：android.permission.DOWNLOAD_WITHOUT_NOTIFICATION
                // request.setShowRunningNotification(true);
                //
                // //不显示下载界面
                // request.setVisibleInDownloadsUi(true);
                //
                // //request.setDestinationInExternalFilesDir(this, null,
                // "tar.apk");
                // long id = downloadManager.enqueue(request);

            }
        });
        WebChromeClient webChromeClient = new WebChromeClient()
        {
            private MainActivity activity = (MainActivity) mContext;

            @Override
            public void onProgressChanged(WebView view, int newProgress)
            {
                super.onProgressChanged(view, newProgress);
                if (mProgressBar != null)
                {
                    mProgressBar.setProgress(newProgress);
                    if (newProgress != 100)
                    {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        mProgressBar.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title)
            {
                super.onReceivedTitle(view, title);
                if (mTextView != null)
                    mTextView.setText("" + title);
            }

            @Override
            public void onReachedMaxAppCacheSize(long spaceNeeded, long totalUsedQuota, QuotaUpdater quotaUpdater)
            {
                quotaUpdater.updateQuota(spaceNeeded * 2);
            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg)
            {

                activity.mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                activity.startActivityForResult(Intent.createChooser(i, "File Chooser"), MainActivity.FILECHOOSER_RESULTCODE);
            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback uploadMsg, String acceptType)
            {
                activity.mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                activity.startActivityForResult(Intent.createChooser(i, "File Chooser"), MainActivity.FILECHOOSER_RESULTCODE);
            }

            // For Android 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture)
            {
                activity.mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                activity.startActivityForResult(Intent.createChooser(i, "File Chooser"), MainActivity.FILECHOOSER_RESULTCODE);
            }

        };
        this.setWebChromeClient(webChromeClient);
        this.setWebViewClient(new WebViewClient()
        {

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
            {
                if (mListener != null)
                    mListener.pageErro();
                super.onReceivedError(view, errorCode, description, failingUrl);

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                setScale();
                isLoadding = true;
                super.onPageStarted(view, url, favicon);
                if (mListener != null)
                    mListener.pageStart();
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                // 解析网页源码获取头信息
                Log.d("colin", url);
                view.loadUrl("javascript:window.local_obj.showSource('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                isLoadding = false;
                super.onPageFinished(view, url);
                if (mListener != null)
                    mListener.pagefinsh();
            }

        });
        addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
        addJavascriptInterface(new JsClass(this), "vnjs");
    }

    void setScale()
    {
        if (Build.VERSION.SDK_INT >= 14)
        {
            zoom();
        }
        else
        {
            setInitialScale(LocalConfig.getScale(mContext));
        }
    }

    @SuppressLint("NewApi")
    void zoom()
    {
        getSettings().setTextZoom(LocalConfig.getScale(mContext));
    }

    /**
     * 前进方法
     */
    public void onFarword()
    {
        setScale();
        goForward();
    }

    /**
     * 后退方法
     */
    public void onBack()
    {
        setScale();
        goBack();
    }

    /**
     * 停止方法
     */
    public void onStop()
    {
        stopLoading();
    }

    /**
     * 刷新方法
     */
    public void onRefresh()
    {
        setScale();
        if ((LocalConfig.getHomeUrl(mContext) + URL_END).equals(getUrl()))
        {
            home();
        }
        else
        {
            reload();
        }
    }

    /**
     * 主页方法
     */
    public void home()
    {
        setScale();
        String deviceId = getDeviceId();
        if (deviceId == null || deviceId.length() == 0)
        {
            deviceId = DEVICE_NULL;
        }
        String ver = "0";
        try
        {
            ver = getVersionCode() + "";
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        String data = "CLIENTID=" + deviceId + "&CLIENTVER=" + ver;
        String url = LocalConfig.getHomeUrl(mContext) + URL_END;
        // loadUrl(url);
        postUrl(url, EncodingUtils.getBytes(data, "base64"));
    }

    public void postBarcode(String value)
    {
        String data = "value=" + value;
        postUrl(getUrl(), EncodingUtils.getBytes(data, "base64"));
    }

    public boolean isLoadding()
    {
        return isLoadding;
    }

    public void setLoadding(boolean isLoadding)
    {
        this.isLoadding = isLoadding;
    }

    public void setListener(Listener listener)
    {
        mListener = listener;
    }

    public void setProgressBar(ProgressBar mProgressBar)
    {
        this.mProgressBar = mProgressBar;
    }

    public void setmTextView(TextView mTextView)
    {
        this.mTextView = mTextView;
    }

    public interface Listener
    {
        public void pageStart();

        public void pagefinsh();

        public void pageErro();
    }

    /**
     * 获取设备号
     * 
     * @return
     */
    public String getDeviceId()
    {
        String android_id = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);
        return android_id;
    }

    /**
     * 查询当前版本号
     * 
     * @return
     * @throws Exception
     */
    public int getVersionCode() throws Exception
    {
        // 获取packagemanager的实例
        PackageManager packageManager = mContext.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
        int version = packInfo.versionCode;
        return version;
    }

    /**
     * 调用网页源码类
     * 
     * @author colin
     * 
     */
    final class InJavaScriptLocalObj
    {
        public void showSource(String html)
        {
            Log.d("colin", html);
            Document doc = Jsoup.parse(html);
            Elements metas = doc.select("meta");
            for (Element element : metas)
            {
                String content = element.attr("content");
                if ("getBarcode".equals(content))
                {
                    if (mInputListener != null)
                    {
                        mInputListener.dobarcode(true);
                        return;
                    }
                }
            }
            if (mInputListener != null)
            {
                mInputListener.dobarcode(false);
            }

        }
    }

    public Listener getmListener()
    {
        return mListener;
    }

    public void setmListener(Listener mListener)
    {
        this.mListener = mListener;
    }

    public InputListener getmInputListener()
    {
        return mInputListener;
    }

    public void setmInputListener(InputListener mInputListener)
    {
        this.mInputListener = mInputListener;
    }

    public interface InputListener
    {
        void dobarcode(boolean isShow);
    }

    public Context getmContext()
    {
        return mContext;
    }

    public void setmContext(Context mContext)
    {
        this.mContext = mContext;
    }

    public void openFileChooser(ValueCallback uploadMsg,

    String acceptType, String capture)
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        ((Activity) mContext).startActivityForResult(Intent.createChooser(intent, "完成操作需要使用"), 1);
    }

}

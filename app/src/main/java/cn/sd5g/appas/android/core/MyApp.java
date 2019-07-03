package cn.sd5g.appas.android.core;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.Toast;

import com.sd5gs.vine.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sd5g.appas.android.forms.FrmMain;
import cn.jpush.android.api.JPushInterface;

public class MyApp extends android.app.Application {
    public static final String DEVICE_TYPE = "android";
    public static String HOME_URL = "https://www.baidu.com";
    public static String HOME_PAGE = "WebDefault";
    public static String SERVICES_PATH = "services";
    public static String FORMS_PATH = "forms";
    private static MyApp instance;
    private final String APPCODE = "vine-android-standard";
    public boolean debug = false;
    private DisplayImageOptions options;
    private String appVersion;
    private String clientId;
    private List<String> cacheFiles = new ArrayList<>();
    private Context mContext;

    public static MyApp getInstance() {
        return instance;
    }

    /**
     * 获取的版本名
     *
     * @param context 上下文
     * @return 版本号
     * @throws PackageManager.NameNotFoundException
     */
    public static String getVersionName(Context context) throws PackageManager.NameNotFoundException {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        return packageInfo.versionName;
    }

    /**
     * 读取缓存的配置文件
     *
     * @return 返回的文件json字符串
     */
    public static JSONObject getCacheList() {
        File file = new File(MyApp.getAppPath(Constans.CONFIG_PATH) + "/" + Constans.CONFIGNAME);
        try {
            FileInputStream fis = new FileInputStream(file);
            int length = fis.available();
            byte[] buffer = new byte[length];
            fis.read(buffer);
            String str = new String(buffer);
            fis.close();
            return new JSONObject(str);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取url的文件名
     *
     * @param url 文件路径
     * @param nrp 0 带远程路径， 否则为文件名
     * @return 文件名
     */
    public static String fileurl2name(String url, int nrp) {
        if (url.contains("?"))
            url = url.replace("?", "");
        String Remotename = url.split(",")[0];
        return nrp == 0 ? Remotename : Remotename.substring(Remotename.lastIndexOf("/"), Remotename.length());
    }

    /**
     * 获取当前网络状态
     *
     * @param context 上下文
     * @return 是否有网络
     */
    public static boolean getNetworkState(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null && info.isConnected()) { // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) { // 当前所连接的网络可用
                    return true;
                }
            }
        }
        Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT).show();
        return false;
    }

    public static boolean needUpdate(String url, JSONObject jsonarr) {
        String remote = fileurl2name(url, 0);
        String savepath = MyApp.getAppPath(Constans.DATA_PATH) + fileurl2name(url, 0);
        if (jsonarr != null && jsonarr.has(remote)) {// 此段代码用于判断文件是否需要更新或删除
            String modis = "";
            try {
                modis = jsonarr.getString(remote);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if ("delete".equals(FileUtils.getconfigTime(url))) {
                FileUtils.deleteFile(savepath);
                return false;
            } else {
                if (FileUtils.getconfigTime(url).equals(modis)) {
                    return false;
                }
            }
        } else {
            File file = new File(savepath);
            if (file.exists())
                return false;
        }
        return true;
    }

    public static String getAppPath(String Dir) {
        File file = MyApp.getInstance().getExternalFilesDir(Dir);
        if (!file.exists()) file.mkdirs();
        return file.getAbsolutePath();
    }

    public static String getFormUrl(String formCode) {
        return getFormUrl(formCode, false);
    }

    public static String getFormUrl(String formCode, boolean first) {
        if (first)
            return String.format("%s/%s/%s?CLIENTID=%s&device=%s", MyApp.HOME_URL, FORMS_PATH, formCode,
                    MyApp.getInstance().getClientId(), DEVICE_TYPE);
        else
            return String.format("%s/%s/%s", HOME_URL, FORMS_PATH, formCode);
    }

    public static String getNewUrl(String formCode, boolean first) {
        if (first)
            return String.format("%s/%s/%s?CLIENTID=%s&device=%s", MyApp.HOME_URL, FORMS_PATH, formCode,
                    MyApp.getInstance().getClientId(), DEVICE_TYPE);
        else
            return String.format("%s/%s/%s&CLIENTID=%s&device=%s", HOME_URL, FORMS_PATH, formCode,MyApp.getInstance().getClientId(), DEVICE_TYPE);
    }

    public static String getServiceUrl(String serviceCode) {
        return String.format("%s/%s/%s", HOME_URL, SERVICES_PATH, serviceCode);
    }

    public static void setHomeUrl(String homeUrl) {
        HOME_URL = homeUrl;
        MyApp.getInstance().getSharedPreferences("NEW_HOST", Context.MODE_PRIVATE).edit().putString("host", homeUrl).commit();

    }

    /**
     * 批量申请权限
     *
     * @param permArray
     * @param questCode
     * @param activity
     * @return
     */
    public static boolean isPermissionsAllGranted(String[] permArray, int questCode, Activity activity) {
        //6.0以下系统，取消请求权限
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        //获得批量请求但被禁止的权限列表
        List<String> deniedPerms = new ArrayList<String>();
        for (int i = 0; permArray != null && i < permArray.length; i++) {
            if (PackageManager.PERMISSION_GRANTED != activity.checkSelfPermission(permArray[i])) {
                deniedPerms.add(permArray[i]);
            }
        }
        int denyPermNum = deniedPerms.size();
        //进行批量请求
        if (denyPermNum != 0) {
            activity.requestPermissions(deniedPerms.toArray(new String[denyPermNum]), questCode);
            return false;
        }
        return true;
    }

    public boolean isDebug() {
        return debug;
    }

    public DisplayImageOptions getImageOptions() {
        return options;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        x.Ext.init(this);//xutils 初始化
        x.Ext.setDebug(true);//设置为debug

        initImageLoader();

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.start) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.error) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.error) // 设置图片加载或解码过程中发生错误显示的图片
                .resetViewBeforeLoading(false)  // default 设置图片在加载前是否重置、复位
                .cacheInMemory(true) // default  设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // default  设置下载的图片是否缓存在SD卡中
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default 设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565) // default 设置图片的解码类型
                .displayer(new FadeInBitmapDisplayer(500))
                .build();

        JPushInterface.init(this);
        JPushInterface.setDebugMode(true);
    }

    /**
     * ImageLoader 初始化
     */
    private void initImageLoader() {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(this);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.discCacheFileCount(100);//缓存的文件数量
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(200 * 1024 * 1024); // 100 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        // webConfig.writeDebugLogs(); // Remove for release app
        ImageLoader.getInstance().init(config.build());

    }

    public void executiveJS(String function, String jsonObject) {
        FrmMain.getInstance().runScript(String.format("(new Function('return %s') ()) ('%s')", function, jsonObject));
    }

    /**
     * 判断服务状态
     *
     * @param mContext
     * @param serviceName
     * @return
     */
    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(200);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    /**
     * 获取的版本号
     *
     * @param context 上下文
     * @return 版本号
     */
    public String getCurrentVersion(Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pi.versionName;
    }

    /**
     * 储存缓存配置列表
     */
    public void saveCacheList() {
        Map<String, String> map = new HashMap<String, String>();
        for (String str : cacheFiles) {
            String[] args = str.split(",");
            map.put(args[0], args.length == 2 ? args[1] : "0");
        }
        JSONObject jsonObject = new JSONObject(map);
        FileUtils.createFile(jsonObject.toString().getBytes(Charset.forName("utf-8")), Constans.CONFIGNAME);
    }

    public String getClientId() {
        return this.clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getRootSite() {
        return this.HOME_URL;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getStartPage() {
        return String.format("%s/%s/%s?CLIENTID=%s&device=%s", MyApp.HOME_URL, MyApp.FORMS_PATH, MyApp.HOME_PAGE, MyApp.getInstance().getClientId(), DEVICE_TYPE);
    }

    //载入配置
    public void loadConfig(JSONObject json) throws JSONException {
        String value = json.getString("appVersion");
        MyApp.getInstance().setAppVersion(value);
    }

    //返回应用代码
    public String getAppCode() {
        return this.APPCODE;
    }
}

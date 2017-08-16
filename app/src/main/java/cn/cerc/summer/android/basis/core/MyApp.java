package cn.cerc.summer.android.basis.core;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.mimrc.vine.R;
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
import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Jason<sz9214e@qq.com> on 2016/11/2.
 */

public class MyApp extends android.app.Application {
    public static String HOME_URL = "http://192.168.1.178/forms/FrmRobotHome";

    private static MyApp instance;
    private DisplayImageOptions options;

    public static MyApp getInstance() {
        return instance;
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

    /**
     * 获取的版本号
     *
     * @param context 上下文
     * @return 版本号
     * @throws PackageManager.NameNotFoundException
     */
    public static int getVersionCode(Context context) throws PackageManager.NameNotFoundException {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
        int versioncode = pi.versionCode;
        return versioncode;
    }

    /**
     * 获取的版本名
     *
     * @param context 上下文
     * @return 版本号
     * @throws PackageManager.NameNotFoundException
     */
    public static String getVersionName(Context context) throws PackageManager.NameNotFoundException {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
        String versionname = pi.versionName;
        return versionname;
    }

    /**
     * 配置url
     *
     * @param baseUrl host
     * @return url
     */
    public static String buildDeviceUrl(String baseUrl) {
        return String.format("%s?device=%s&CLIENTID=%s", baseUrl, Constans.DEVICE_TYPE, PermissionUtils.IMEI);
    }

    /**
     * 储存缓存配置列表
     *
     * @param webConfig 配置文件类
     */
    public static void saveCacheList(WebConfig webConfig) {
        Map<String, String> map = new HashMap<String, String>();
        for (String str : webConfig.getCacheFiles()) {
            String[] args = str.split(",");
            map.put(args[0], args.length == 2 ? args[1] : "0");
        }
        JSONObject jsonObject = new JSONObject(map);
        FileUtils.createFile(jsonObject.toString().getBytes(Charset.forName("utf-8")), Constans.CONFIGNAME);
    }

    /**
     * 读取缓存的配置文件
     *
     * @return 返回的文件json字符串
     */
    public static JSONObject getCacheList() {
        File file = new File(Constans.getAppPath(Constans.CONFIG_PATH) + "/" + Constans.CONFIGNAME);
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
        String savepath = Constans.getAppPath(Constans.DATA_PATH) + fileurl2name(url, 0);
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
    }}

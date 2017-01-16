package cn.cerc.summer.android.Utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.huagu.ehealth.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.cerc.summer.android.Entity.Config;

/**
 * Created by fff on 2016/11/28.
 */

public class AppUtil {

    /**
     * 获取的版本号
     * @param context   上下文
     * @return          版本号
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
     * @param context   上下文
     * @return          版本号
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
     * @param baseUrl   host
     * @return  url
     */
    public static String buildDeviceUrl(String baseUrl){
        return String.format("%s?device=%s&CLIENTID=%s", baseUrl, Constans.DEVICE_TYPE,  PermissionUtils.IMEI);
    }

    /**
     * 储存缓存配置列表
     *
     * @param config 配置文件类
     */
    public static void saveCacheList(Config config) {
        Map<String, String> map = new HashMap<String, String>();
        for (String str : config.getCacheFiles()) {
            String[] args = str.split(",");
            map.put(args[0], args.length == 2 ? args[1] : "0");
        }
        JSONObject jsonObject = new JSONObject(map);
        FileUtil.createFile(jsonObject.toString().getBytes(Charset.forName("utf-8")), Constans.CONFIGNAME);
    }

    /**
     * 读取缓存的配置文件
     * @return  返回的文件json字符串
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
     * @param context   上下文
     * @return          是否有网络
     */
    public static boolean getNetWorkStata(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null && info.isConnected()){ // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED){ // 当前所连接的网络可用
                    return true;
                }
            }
        }
        Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * 判断静态文件是否需要更新
     * @param url
     * @param jsonarr
     * @return
     */
    public static boolean needUpdate(String url, JSONObject jsonarr){
        String remote = AppUtil.fileurl2name(url, 0);
        String savepath = Constans.getAppPath(Constans.DATA_PATH) + AppUtil.fileurl2name(url, 0);
        if (jsonarr != null && jsonarr.has(remote)) {// 此段代码用于判断文件是否需要更新或删除
            String modis = "";
            try {
                modis = jsonarr.getString(remote);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if ("delete".equals(FileUtil.getconfigTime(url))) {
                FileUtil.deleteFile(savepath);
                return false;
            } else {
                if (FileUtil.getconfigTime(url).equals(modis)) {
                    return false;
                }
            }
        }else{
            File file = new File(savepath);
            if (file.exists())
                return false;
        }
        return true;
    }

    private static SimpleDateFormat mDateFormat;

    public static String formatDateTime(long time) {
        if (mDateFormat == null){
            mDateFormat = new SimpleDateFormat("MM-dd HH:mm");
        }
        if (0 == time) {
            return "";
        }
        return mDateFormat.format(new Date(time));
    }

}

package cn.cerc.summer.android.Utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import cn.cerc.summer.android.Entity.Config;

/**
 * Created by fff on 2016/11/28.
 */

public class AppUtil {



    public static int getVersionCode(Context context) throws PackageManager.NameNotFoundException {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
        int versioncode = pi.versionCode;
        return versioncode;
    }

    public static String getVersionName(Context context) throws PackageManager.NameNotFoundException {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
        String versionname = pi.versionName;
        return versionname;
    }

    /**
     * 储存缓存配置列表
     * @param config    配置文件类
     */
    public static void saveCacheList(Config config){
        Map<String, String> map = new HashMap<String, String>();
        for (String str : config.getCacheFiles()) {
            String[] args = str.split(",");
            map.put(args[0], args.length == 2 ? args[1]: "0");
        }
        JSONObject jsonObject = new JSONObject(map);
        FileUtil.createFile(jsonObject.toString().getBytes(Charset.forName("utf-8")), Constans.CONFIGNAME);
    }

    public static JSONObject getCacheList(){
        File file = new File(Constans.getAppPath(Constans.CACHEFILE_PATH) + Constans.CONFIGNAME);
        try {
            FileInputStream fis = new FileInputStream(file);
            int length = fis.available();
            byte [] buffer = new byte[length];
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
     * @param url   文件路径
     * @param nrp   0 带远程路径， 否则为文件名
     * @return      文件名
     */
    public static String fileurl2name(String url,int nrp) {
        if (url.contains("?"))
            url = url.replace("?", "");
        String Remotename = url.split(",")[0];
        return nrp==0?Remotename:Remotename.substring(Remotename.lastIndexOf("/"), Remotename.length());
    }

}

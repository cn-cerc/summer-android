package cn.cerc.summer.android.services;

import android.content.Context;
import android.content.pm.PackageManager;

import org.json.JSONObject;

import cn.cerc.summer.android.core.MyApp;
import cn.cerc.summer.android.forms.JavaScriptService;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/8/9.
 */

public class GetClientVersion implements JavaScriptService {

    @Override
    public void execute(Context context, JSONObject request, JSONObject response) throws Exception {
        try {
            response.put("version",MyApp.getInstance().getVersionName(context)) ;
        } catch (PackageManager.NameNotFoundException e) {
            response.put("version","权限缺少，无法获取") ;
            e.printStackTrace();
        }
    }
}

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
    public String execute(Context context, JSONObject request) {

        try {
            return MyApp.getInstance().getVersionName(context);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "0.0.0";

    }
}

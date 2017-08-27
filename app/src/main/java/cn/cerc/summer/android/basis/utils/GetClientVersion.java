package cn.cerc.summer.android.basis.utils;

import android.content.Context;
import android.content.pm.PackageManager;

import org.json.JSONObject;

import cn.cerc.summer.android.basis.core.MyApp;
import cn.cerc.summer.android.basis.forms.JavaScriptService;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/8/9.
 */

public class GetClientVersion implements JavaScriptService {
    @Override
    public String execute(Context context, JSONObject request) {

        try {
            return "" + MyApp.getVersionCode(context);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "0";

    }
}

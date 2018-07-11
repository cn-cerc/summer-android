package cn.cerc.summer.android.services;

import android.content.Context;
import android.content.pm.PackageManager;

import org.json.JSONException;
import org.json.JSONObject;

import cn.cerc.summer.android.core.MyApp;
import cn.cerc.summer.android.forms.JavaScriptResult;
import cn.cerc.summer.android.forms.JavaScriptService;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/8/9.
 */

public class GetClientVersion implements JavaScriptService {
    @Override
    public String execute(Context context, JSONObject request) {

        JavaScriptResult json = new JavaScriptResult();
        json.setResult(true);
        if (!"".equals(MyApp.getInstance().getCurrentVersion(context)) && MyApp.getInstance().getCurrentVersion(context) != null) {
            json.setData(MyApp.getInstance().getCurrentVersion(context));
        } else {
            json.setResult(false);
        }
        try {
            MyApp.getInstance().executiveJS(request.getString(request.getString("_callback_")), json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "0.0.0";

    }
}

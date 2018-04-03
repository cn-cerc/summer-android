package cn.cerc.summer.android.services;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import cn.cerc.summer.android.core.MyApp;
import cn.cerc.summer.android.forms.JavaScriptService;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/8/9.
 */

public class GetClientId implements JavaScriptService {

    @Override
    public String execute(Context context, JSONObject request) throws JSONException {
        if (!request.has("_callback_")) {
            return "没有传入指定参数";
        }
        String function = request.getString("_callback_");
        MyApp.getInstance().executiveJS(function,MyApp.getInstance().getClientId());
        return MyApp.getInstance().getClientId();
    }
}

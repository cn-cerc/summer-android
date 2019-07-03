package cn.sd5g.appas.android.services;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import cn.sd5g.appas.android.core.MyApp;
import cn.sd5g.appas.android.forms.JavaScriptService;

public class GetClientId implements JavaScriptService {

    @Override
    public String execute(Context context, JSONObject request) throws JSONException {
        if (!request.has("_callback_")) {
            return "没有传入指定参数";
        }
        String function = request.getString("_callback_");
        MyApp.getInstance().executiveJS(function, MyApp.getInstance().getClientId());
        return MyApp.getInstance().getClientId();
    }
}

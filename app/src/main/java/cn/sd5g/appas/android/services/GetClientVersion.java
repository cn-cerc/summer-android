package cn.sd5g.appas.android.services;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import cn.sd5g.appas.android.core.MyApp;
import cn.sd5g.appas.android.forms.JavaScriptResult;
import cn.sd5g.appas.android.forms.JavaScriptService;

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

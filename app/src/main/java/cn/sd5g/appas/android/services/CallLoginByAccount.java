package cn.sd5g.appas.android.services;

import android.content.Context;

import org.json.JSONObject;

import cn.sd5g.appas.android.forms.JavaScriptService;

public class CallLoginByAccount implements JavaScriptService {

    @Override
    public String execute(Context context, JSONObject request) throws Exception {
        String loginUrl = "/services/SvrUserLogin.check";
        return "true";
    }
}

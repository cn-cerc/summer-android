package cn.sd5g.appas.android.services;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONObject;

import cn.sd5g.appas.android.forms.JavaScriptService;
import cn.sd5g.appas.android.parts.login.FrmLoginByAccount;


public class CallLoginByAccount implements JavaScriptService {

    @Override
    public String execute(Context context, JSONObject request) throws Exception {
        String loginUrl = "/services/SvrUserLogin.check";
        if (request.has("loginUrl"))
            loginUrl = request.getString("loginUrl");
        FrmLoginByAccount.startForm((AppCompatActivity) context, loginUrl);
        return "true";
    }
}

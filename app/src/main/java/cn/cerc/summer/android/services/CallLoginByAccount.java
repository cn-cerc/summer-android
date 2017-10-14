package cn.cerc.summer.android.services;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONObject;

import cn.cerc.summer.android.forms.JavaScriptService;
import cn.cerc.summer.android.parts.login.FrmLoginByAccount;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/8/9.
 */

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

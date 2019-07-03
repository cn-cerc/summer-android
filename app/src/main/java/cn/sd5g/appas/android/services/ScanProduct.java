package cn.sd5g.appas.android.services;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import cn.sd5g.appas.android.units.MySession;
import cn.sd5g.appas.android.forms.JavaScriptService;

public class ScanProduct implements JavaScriptService {
    @Override
    public String execute(Context context, JSONObject json) throws Exception {
        if (!json.has("title")) {
            return "title is null.";
        }
        if (!json.has("homeUrl")) {
            return "homeUrl is null.";
        }
        if (!json.has("returnUrl")) {
            return "returnUrl is null.";
        }
        if (!json.has("appendUrl")) {
            return "appendUrl is null.";
        }
        if (!json.has("modifyUrl")) {
            return "modifyUrl is null.";
        }
        if (!json.has("deleteUrl")) {
            return "deleteUrl is null.";
        }
        if (!json.has("viewUrl")) {
            return "viewUrl is null.";
        }

        String token = null;
        if (json.has("token")) {
            token = json.getString("token");
        } else {
            if (MySession.getInstance().getToken() != null) {
                token = MySession.getInstance().getToken();
            }
        }
        if (token == null || "".equals(token)) {
            return "token is null.";
        }

        Log.d("ScanProduct", json.getString("title"));
        MySession.getInstance().setToken(token);
        return "true";
    }
}

package cn.sd5g.appas.android.services;

import android.content.Context;

import org.json.JSONObject;

import cn.sd5g.appas.android.forms.FrmMain;
import cn.sd5g.appas.android.forms.JavaScriptService;

public class SetAppliedTitle implements JavaScriptService {
    @Override
    public String execute(Context context, JSONObject json) throws Exception {
        FrmMain main = (FrmMain) context;
        if (json.has("visibility"))
            main.setTitleVisibility(json.getBoolean("visibility"));
        if (json.has("title"))
            main.setWebTitle(json.getString("title"));
        return "true";
    }
}

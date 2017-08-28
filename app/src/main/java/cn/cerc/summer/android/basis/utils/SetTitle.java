package cn.cerc.summer.android.basis.utils;

import android.content.Context;

import org.json.JSONObject;

import cn.cerc.summer.android.basis.forms.FrmMain;
import cn.cerc.summer.android.basis.forms.JavaScriptService;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/8/27.
 */

public class SetTitle implements JavaScriptService {
    @Override
    public String execute(Context context, JSONObject json) throws Exception {
        FrmMain main = (FrmMain) context;
        if (json.has("visibility"))
            main.setTitleVisibility(json.getBoolean("visibility"));
        if (json.has("title"))
            main.setTitle(json.getString("title"));
        return "true";
    }
}

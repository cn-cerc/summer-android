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
    public String execute(Context context, String dataIn) throws Exception {
        FrmMain main = (FrmMain) context;
        JSONObject json = new JSONObject(dataIn);
        if (json.has("visibility"))
            main.setTitleVisibility(json.getBoolean("visibility"));
        if (json.has("title"))
            main.setTitle(json.getString("title"));
        return "true";
    }
}

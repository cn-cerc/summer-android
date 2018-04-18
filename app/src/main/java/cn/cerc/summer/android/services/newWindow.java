package cn.cerc.summer.android.services;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import cn.cerc.summer.android.core.MyApp;
import cn.cerc.summer.android.forms.FrmMain;
import cn.cerc.summer.android.forms.JavaScriptService;

/**
 * Created by Administrator on 2018/4/10.
 */

public class newWindow implements JavaScriptService {

    @Override
    public String execute(Context context, JSONObject request) throws Exception {
        Log.d("print", "execute: " + request.toString());
        if (!request.has("url")) {
            return "没有传入指定参数";
        }
        String url = request.getString("url");
        if (!"".equals(url) && url != null) {
            if (url.contains("?")) {
                FrmMain.getInstance().setAddWindow(MyApp.getNewUrl(url, false));
            } else {
                FrmMain.getInstance().setAddWindow(MyApp.getNewUrl(url, true));
            }

        } else {
            return "传入参数错误";
        }
        return "true";
    }
}

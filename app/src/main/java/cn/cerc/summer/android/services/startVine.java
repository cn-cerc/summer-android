package cn.cerc.summer.android.services;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import cn.cerc.summer.android.core.MyApp;
import cn.cerc.summer.android.core.MySession;
import cn.cerc.summer.android.forms.FrmMain;
import cn.cerc.summer.android.forms.JavaScriptService;

/**
 * Created by Administrator on 2018/2/24.
 */

public class startVine implements JavaScriptService {
    @Override
    public String execute(Context context, JSONObject request) throws Exception {
        if (!request.has("sid")) {
            return "没有传入指定参数";
        }
        if (!request.has("host")) {
            return "没有传入指定参数";
        }

        if (!"".equals(request.getString("host")) || request.getString("host") != null) {
            if (MyApp.HOME_URL.contains(request.getString("host"))) {
                return "相同主机";
            }
            MyApp.setHomeUrl("https://" + request.getString("host"));
        }

        if (!"".equals(request.getString("sid")) || request.getString("sid") != null) {
            MySession.getInstance().setToken(request.getString("sid"));
        }

        FrmMain.getInstance().setHomeUrl(MyApp.HOME_URL + "/forms/WebDefault?" + String.format("device=%s&CLIENTID=%s&sid=%s", MyApp.DEVICE_TYPE, MyApp.getInstance().getClientId(), MySession.getInstance().getToken()));
        return "true";
    }
}

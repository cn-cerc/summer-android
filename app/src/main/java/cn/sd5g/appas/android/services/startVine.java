package cn.sd5g.appas.android.services;

import android.content.Context;

import org.json.JSONObject;

import cn.sd5g.appas.android.core.MyApp;
import cn.sd5g.appas.android.core.MySession;
import cn.sd5g.appas.android.forms.FrmMain;
import cn.sd5g.appas.android.forms.JavaScriptService;

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

package cn.sd5g.appas.android.services;

import android.content.Context;

import org.json.JSONObject;

import cn.sd5g.appas.android.units.MyApp;
import cn.sd5g.appas.android.forms.FrmMain;
import cn.sd5g.appas.android.forms.JavaScriptService;

public class newWindow implements JavaScriptService {

    @Override
    public String execute(Context context, JSONObject request) throws Exception {
        if (!request.has("url")) {
            return "没有传入指定参数";
        }
        String url = request.getString("url");
        if (!url.startsWith("http")) {
            url = String.format("%s/%s/%s", MyApp.HOME_URL, MyApp.FORMS_PATH, url);
        }
        if (!"".equals(url) && url != null) {
            FrmMain.getInstance().setAddWindow(url);
        } else {
            return "传入参数错误";
        }
        return "true";
    }
}

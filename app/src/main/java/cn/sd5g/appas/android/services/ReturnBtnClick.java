package cn.sd5g.appas.android.services;

import android.content.Context;

import org.json.JSONObject;

import cn.sd5g.appas.android.forms.FrmMain;
import cn.sd5g.appas.android.forms.JavaScriptService;

public class ReturnBtnClick implements JavaScriptService {
    @Override
    public String execute(Context context, JSONObject request) throws Exception {
        String backUrl = null;
        if (request.has("backUrl")) {
            backUrl = request.getString("backUrl");
            if (backUrl != null && !"".equals(backUrl)) {
                FrmMain.getInstance().setHomeUrl(backUrl);
            }
        }

        return "true";
    }
}

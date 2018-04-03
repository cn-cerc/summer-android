package cn.cerc.summer.android.services;

import android.content.Context;

import org.json.JSONObject;

import cn.cerc.summer.android.forms.FrmMain;
import cn.cerc.summer.android.forms.JavaScriptService;

/**
 * Created by Administrator on 2018/2/6.
 */

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

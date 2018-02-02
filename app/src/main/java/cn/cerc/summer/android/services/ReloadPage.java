package cn.cerc.summer.android.services;

import android.content.Context;

import org.json.JSONObject;

import cn.cerc.summer.android.forms.FrmMain;
import cn.cerc.summer.android.forms.JavaScriptService;

/**
 * Created by Administrator on 2018/1/30.
 */

public class ReloadPage implements JavaScriptService {
    @Override
    public String execute(Context context, JSONObject request) throws Exception {
        FrmMain.getInstance().reloadPage();
        return "true";
    }
}

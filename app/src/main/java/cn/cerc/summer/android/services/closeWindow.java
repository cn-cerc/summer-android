package cn.cerc.summer.android.services;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import cn.cerc.summer.android.forms.FrmMain;
import cn.cerc.summer.android.forms.JavaScriptService;

/**
 * Created by Administrator on 2018/4/10.
 */

public class closeWindow implements JavaScriptService {

    @Override
    public String execute(Context context, JSONObject request) throws Exception {
        FrmMain.getInstance().setCloseWindow();
        return "true";
    }
}

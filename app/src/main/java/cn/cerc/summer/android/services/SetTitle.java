package cn.cerc.summer.android.services;

import android.content.Context;

import org.json.JSONObject;

import cn.cerc.summer.android.forms.FrmMain;
import cn.cerc.summer.android.forms.JavaScriptService;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/8/27.
 */

public class SetTitle implements JavaScriptService {

    @Override
    public void execute(Context context, JSONObject request, JSONObject response) throws Exception {
        FrmMain main = (FrmMain) context;
        if (!request.has("visibility")){
            response.put("result", false);
            response.put("message", "visibility 不允许为空！");
            return;
        }

        if (!request.has("title")){
            response.put("result", false);
            response.put("message", "title 不允许为空！");
            return;
        }

        main.setTitle(request.getString("title"));
        main.setTitleVisibility(request.getBoolean("visibility"));
        response.put("result", true);
        response.put("message", "OK");
    }
}

package cn.cerc.summer.android.services;

import android.content.Context;

import org.json.JSONObject;

import cn.cerc.summer.android.forms.JavaScriptService;
import cn.cerc.summer.android.parts.dialog.FrmShowWarning;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/9/5.
 */

public class ShowWarning implements JavaScriptService {

    @Override
    public void execute(Context context, JSONObject request, JSONObject response) throws Exception {
        if(!request.has("message"))      {
            response.put("result", false);
            response.put("message", "(请设置警告内容!)");
            return;
        }
        String message = request.getString("message");
        FrmShowWarning.startForm(context, message);
        response.put("result", true);
        response.put("message", "OK");
    }
}

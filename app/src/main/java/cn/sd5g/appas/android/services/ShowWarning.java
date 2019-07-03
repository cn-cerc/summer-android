package cn.sd5g.appas.android.services;

import android.content.Context;

import org.json.JSONObject;

import cn.sd5g.appas.android.forms.JavaScriptService;
import cn.sd5g.appas.android.parts.dialog.FrmShowWarning;

public class ShowWarning implements JavaScriptService {
    @Override
    public String execute(Context context, JSONObject request) throws Exception {
        String message = "(请设置警告内容!)";
        if (request.has("message"))
            message = request.getString("message");
        FrmShowWarning.startForm(context, message);
        return "";
    }
}

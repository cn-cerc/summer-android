package cn.cerc.summer.android.basis.utils;

import android.content.Context;

import org.json.JSONObject;

import cn.cerc.summer.android.basis.forms.JavaScriptService;
import cn.cerc.summer.android.parts.dialog.FrmShowWarning;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/9/5.
 */

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

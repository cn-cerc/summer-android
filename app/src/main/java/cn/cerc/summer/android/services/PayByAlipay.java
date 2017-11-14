package cn.cerc.summer.android.services;

import android.content.Context;

import org.json.JSONObject;

import cn.cerc.summer.android.forms.JavaScriptService;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/8/9.
 */

public class PayByAlipay implements JavaScriptService {
    @Override
    public void execute(Context context, JSONObject request, JSONObject response) throws Exception {
        response.put("result", false);
        response.put("message", "此功能还未准备好");
    }
}

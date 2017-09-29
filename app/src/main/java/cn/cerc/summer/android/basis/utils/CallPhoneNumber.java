package cn.cerc.summer.android.basis.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import cn.cerc.summer.android.basis.forms.JavaScriptService;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/8/9.
 */

public class CallPhoneNumber implements JavaScriptService {
    @Override
    public String execute(Context context, JSONObject request) throws JSONException {
        if (request.has("phoneNumber")) {
            String uri = "tel:" + request.getString("phoneNumber");
            Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            context.startActivity(it);
        } else {
            return "请输入手机号";
        }
        return "true";
    }
}

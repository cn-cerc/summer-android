package cn.cerc.summer.android.basis.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

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
            Intent it = new Intent(Intent.ACTION_CALL, Uri.parse(uri));
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return "没有拨打电话权限";
            }
            context.startActivity(it);
        } else {
            return "请输入手机号";
        }
        return "ok";

    }
}

package cn.cerc.summer.android.services;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import cn.cerc.summer.android.forms.JavaScriptService;
import cn.cerc.summer.android.parts.image.FrmCaptureImage;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/8/9.
 */

public class CaptureImage implements JavaScriptService {
    @Override
    public String execute(Context context, JSONObject request) {
        try {
            String url = request.getString("uploadUrl");
            Log.e("CatureImage", "url: " + url);
            FrmCaptureImage.startForm(context, url);
        } catch (JSONException e) {
            throw new RuntimeException(e.getMessage());
        }
        return "true";
    }
}

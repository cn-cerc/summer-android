package cn.sd5g.appas.android.services;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import cn.sd5g.appas.android.forms.JavaScriptService;
import cn.sd5g.appas.android.parts.image.FrmCaptureImage;


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

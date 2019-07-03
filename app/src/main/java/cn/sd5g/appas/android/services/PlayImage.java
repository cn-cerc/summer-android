package cn.sd5g.appas.android.services;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import cn.sd5g.appas.android.forms.JavaScriptService;
import cn.sd5g.appas.android.parts.image.FrmZoomImage;

public class PlayImage implements JavaScriptService {
    @Override
    public String execute(Context context, JSONObject request) throws Exception {
        //{"url":"http://pic.4j4j.cn/upload/pic/20130617/55695c3c95.jpg"}
        if (!request.has("url"))
            throw new RuntimeException("params error: url is null.");

        Log.d("PlayImage", "url:" + request.get("url"));
        FrmZoomImage.startForm(context, request.getString("url"));
        return "ok";
    }
}

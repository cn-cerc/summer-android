package cn.cerc.summer.android.basis.utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import cn.cerc.summer.android.basis.forms.JavaScriptService;
import cn.cerc.summer.android.parts.image.FrmZoomImage;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/8/9.
 */

public class PlayImage implements JavaScriptService {
    @Override
    public String execute(Context context, String dataIn) throws Exception {
        //{"url":"http://pic.4j4j.cn/upload/pic/20130617/55695c3c95.jpg"}
        JSONObject json = new JSONObject(dataIn);
        if (!json.has("url"))
            throw new RuntimeException("params error: url is null.");

        Log.d("PlayImage", "url:" + json.get("url"));
        FrmZoomImage.startForm(context, json.getString("url"));
        return "ok";
    }
}

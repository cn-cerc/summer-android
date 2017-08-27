package cn.cerc.summer.android.basis.utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import cn.cerc.summer.android.basis.core.MyApp;
import cn.cerc.summer.android.basis.forms.JavaScriptService;
import cn.cerc.summer.android.parts.music.FrmCaptureMusic;

/**
 * Created by Administrator on 2017/8/14.
 */

public class CaptureMusic implements JavaScriptService {

    @Override
    public String execute(Context context, JSONObject request) {
        try {
            String url = request.getString("uploadUrl");
            Log.e("CaptureMusic", "url: " + url);
            FrmCaptureMusic.startForm(context, url);
        } catch (JSONException e) {
            throw new RuntimeException(e.getMessage());
        }
        return "true";
    }
}

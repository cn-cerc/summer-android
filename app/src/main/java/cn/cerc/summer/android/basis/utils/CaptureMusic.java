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
    public String execute(Context context, String dataIn) {
        try {
            JSONObject json = new JSONObject(dataIn);
            String reqName = json.getString("reqName");
            String url = "http://192.168.1.178/forms/" + json.getString("uploadUrl");
            Log.e("CaptureMusic", url);
            FrmCaptureMusic.startForm(context, url);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "true";
    }
}

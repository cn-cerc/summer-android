package cn.cerc.summer.android.services;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import cn.cerc.summer.android.forms.JavaScriptService;
import cn.cerc.summer.android.parts.image.FrmCaptureImage;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/8/9.
 */

public class CaptureImage implements JavaScriptService {


    @Override
    public void execute(Context context, JSONObject request, JSONObject response) throws Exception {
            if(!request.has("uploadUrl")){
                response.put("result", false);
                response.put("message", "uploadUrl 不允许为空！");
                return;
            }
            String url = request.getString("uploadUrl");
            Log.e("CatureImage", "url: " + url);
            FrmCaptureImage.startForm(context, url);
            response.put("result", true);
            response.put("message", "ok");
    }
}

package cn.sd5g.appas.android.services;

import android.content.Context;

import org.json.JSONObject;

import cn.sd5g.appas.android.units.MyApp;
import cn.sd5g.appas.android.units.MySession;
import cn.sd5g.appas.android.forms.JavaScriptService;
import cn.sd5g.appas.android.parts.image.FrmCaptureImage;


public class UploadImgField implements JavaScriptService {

    @Override
    public String execute(Context context, JSONObject request) throws Exception {
        if (!request.has("sid")) {
            return "没有传入指定参数";
        }
        if (!"".equals(request.getString("sid")) || request.getString("sid") != null) {
            MySession.getInstance().setToken(request.getString("sid"));
        }
        FrmCaptureImage.startForm(context, MyApp.getFormUrl("FrmCusFollowUp.uploadFile"));
        return "true";
    }
}

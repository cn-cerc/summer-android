package cn.cerc.summer.android.services;

import android.content.Context;

import org.json.JSONObject;

import cn.cerc.summer.android.core.MyApp;
import cn.cerc.summer.android.core.MySession;
import cn.cerc.summer.android.forms.JavaScriptService;
import cn.cerc.summer.android.parts.image.FrmCaptureImage;

/**
 * @class name：cn.cerc.summer.android.services
 * @class 图片（相机）、文件
 * @anthor zhuhao
 * @time 2018-5-11 10:37
 */
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

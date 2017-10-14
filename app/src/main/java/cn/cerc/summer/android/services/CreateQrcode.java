package cn.cerc.summer.android.services;

import android.content.Context;

import org.json.JSONObject;

import cn.cerc.summer.android.forms.JavaScriptService;
import cn.cerc.summer.android.parts.barcode.FrmCreateQrcode;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/9/9.
 */

public class CreateQrcode implements JavaScriptService {

    @Override
    public String execute(Context context, JSONObject request) throws Exception {
        String text = "";
        if (request.has("text"))
            text = request.getString("text");
        boolean qrcode = true;
        if (request.has("qrcode"))
            qrcode = request.getBoolean("qrcode");
        FrmCreateQrcode.startForm(context, text, qrcode);
        return "true";
    }
}

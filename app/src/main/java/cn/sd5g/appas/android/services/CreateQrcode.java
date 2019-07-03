package cn.sd5g.appas.android.services;

import android.content.Context;

import org.json.JSONObject;

import cn.sd5g.appas.android.forms.JavaScriptService;
import cn.sd5g.appas.android.parts.barcode.FrmCreateQrcode;


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

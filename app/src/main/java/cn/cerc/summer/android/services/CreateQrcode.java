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
    public void execute(Context context, JSONObject request, JSONObject response) throws Exception {
        String text = "";
        if (!request.has("text")){
            response.put("result", false);
            response.put("message", "text 不允许为空！");
            return;
        }

        if (!request.has("qrcode")){
            response.put("result", false);
            response.put("message", "qrcode 不允许为空！");
            return;
        }
        boolean qrcode = true;
        text = request.getString("text");
        qrcode = request.getBoolean("qrcode");
        FrmCreateQrcode.startForm(context, text, qrcode);
        response.put("result", true);
        response.put("message", "OK");
        return ;
    }
}

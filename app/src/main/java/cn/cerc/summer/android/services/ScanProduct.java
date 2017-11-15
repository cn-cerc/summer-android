package cn.cerc.summer.android.services;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import cn.cerc.summer.android.core.MySession;
import cn.cerc.summer.android.forms.JavaScriptService;
import cn.cerc.summer.android.parts.barcode.FrmScanProduct;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/8/9.
 */

public class ScanProduct implements JavaScriptService {

    @Override
    public void execute(Context context, JSONObject request, JSONObject response) throws Exception {
        if (!request.has("title")) {
            response.put("result", false);
            response.put("message", "title is null.");
            return;
        }
        if (!request.has("homeUrl")) {
            response.put("result", false);
            response.put("message", "homeUrl is null.");
            return;
        }
        if (!request.has("returnUrl")) {
            response.put("result", false);
            response.put("message", "returnUrl is null.");
            return;
        }
        if (!request.has("appendUrl")) {
            response.put("result", false);
            response.put("message", "appendUrl is null.");
            return;
        }
        if (!request.has("modifyUrl")) {
            response.put("result", false);
            response.put("message", "modifyUrl is null.");
            return;
        }
        if (!request.has("deleteUrl")) {
            response.put("result", false);
            response.put("message", "deleteUrl is null.");
            return;
        }
        if (!request.has("viewUrl")) {
            response.put("result", false);
            response.put("message", "viewUrl is null.");
            return;
        }

        String token = null;
        if (request.has("token")) {
            token = request.getString("token");
        } else {
            if (MySession.getInstance().getToken() != null) {
                token = MySession.getInstance().getToken();
            }
        }
        if (token == null || "".equals(token)) {
            response.put("result", false);
            response.put("message", "token is null.");
            return;
        }

        Log.d("ScanProduct", request.getString("title"));
        MySession.getInstance().setToken(token);
        FrmScanProduct.startForm(context, request.getString("title"), request.getString("homeUrl"),
                request.getString("returnUrl"), request.getString("appendUrl"), request.getString("modifyUrl"),
                request.getString("deleteUrl"), request.getString("viewUrl"));
        response.put("result", true);
        response.put("message", "OK");
    }
}

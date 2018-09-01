package cn.cerc.summer.android.services;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import cn.cerc.summer.android.forms.FrmMain;
import cn.cerc.summer.android.forms.JavaScriptService;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/8/9.
 */

public class qrcode implements JavaScriptService {
    public Context mContext;

    @Override
    public String execute(Context context, JSONObject request) throws JSONException {
        mContext = context;
        Log.d("print", "execute: "+request);
        if (!request.has("qrcode")) {
            return "没有传入指定参数";
        }
        FrmMain.getInstance().skipAPPlication(request.getString("qrcode"));
        return "true";

    }
}

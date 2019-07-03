package cn.sd5g.appas.android.services;

import android.annotation.SuppressLint;
import android.content.Context;

import org.json.JSONObject;

import cn.sd5g.appas.android.forms.FrmMain;
import cn.sd5g.appas.android.forms.JavaScriptService;


public class jaLogin implements JavaScriptService {
    private Context mContext;
    private String url = null;
    private String downloadUrl = null;

    @SuppressLint("WrongConstant")
    @Override
    public String execute(Context context, JSONObject request) throws Exception {
        mContext = context;
        if (request.has("URL")) {
            return "没有传入指定参数";
        }
        url = request.getString("URL");
        if (request.has("jayunUrl")) {
            downloadUrl = request.getString("jayunUrl");
        }
        FrmMain.getInstance().skipAPPlication(request.getString("URL"), downloadUrl);
        return "true";
    }

}

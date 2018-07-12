package cn.cerc.summer.android.services;

import android.annotation.SuppressLint;
import android.content.Context;

import org.json.JSONObject;

import cn.cerc.summer.android.forms.FrmMain;
import cn.cerc.summer.android.forms.JavaScriptService;

/**
 * Created by Administrator on 2018/4/16.
 */

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

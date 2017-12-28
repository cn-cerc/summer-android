package cn.cerc.summer.android.services;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import org.json.JSONObject;

import cn.cerc.summer.android.forms.JavaScriptService;

/**
 * Created by Administrator on 2017/12/28.
 */

public class CallBrowser implements JavaScriptService {

    @Override
    public String execute(Context context, JSONObject request) throws Exception {
        if (request.has("url")) {
            String url = request.getString("url");
            if (!"".equals(url)) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(url);
                intent.setData(content_url);
                context.startActivity(intent);
            }
        } else {
            return "没有传入指定参数";
        }
        return "";
    }
}

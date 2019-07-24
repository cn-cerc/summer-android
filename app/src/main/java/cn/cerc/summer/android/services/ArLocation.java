package cn.cerc.summer.android.services;

import android.content.Context;
import android.content.Intent;

import com.yt.hz.financial.argame.LocationActivity;

import org.json.JSONObject;

import cn.cerc.summer.android.forms.JavaScriptService;

//跳转ar地图

public class ArLocation implements JavaScriptService {

    @Override
    public String execute(Context context, JSONObject request) throws Exception {
        Intent intent = new Intent(context, LocationActivity.class);
        context.startActivity(intent);
        return "true";
    }
}

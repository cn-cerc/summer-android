package cn.sd5g.appas.android.services;

import android.content.Context;
import android.content.Intent;

import org.json.JSONObject;

import cn.sd5g.appas.android.forms.JavaScriptService;
import cn.sd5g.appas.android.parts.sign.ClockInActivity;

public class ClockIn implements JavaScriptService {

    @Override
    public String execute(Context context, JSONObject request) throws Exception {
        Intent intent = new Intent(context, ClockInActivity.class);
        context.startActivity(intent);
        return "true";
    }
}
package cn.cerc.summer.android.services;

import android.content.Context;
import android.content.Intent;

import org.json.JSONObject;

import cn.cerc.summer.android.forms.JavaScriptService;
import cn.cerc.summer.android.forms.view.ClockOffActivity;

/**
 * Created by Administrator on 2018/1/9.
 */

public class CallClockOff implements JavaScriptService {

    @Override
    public String execute(Context context, JSONObject request) throws Exception {
        Intent intent = new Intent(context, ClockOffActivity.class);
        context.startActivity(intent);
        return "true";
    }
}
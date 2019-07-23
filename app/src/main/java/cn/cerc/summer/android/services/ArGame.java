package cn.cerc.summer.android.services;

import android.content.Context;
import android.content.Intent;


import com.yt.hz.financial.argame.ARPlayActivity;

import org.json.JSONObject;

import cn.cerc.summer.android.forms.JavaScriptService;

public class ArGame implements JavaScriptService {
    @Override
    public String execute(Context context, JSONObject request) throws Exception {
        Intent intent = new Intent(context, ARPlayActivity.class);
        context.startActivity(intent);
        return "true";
    }
}

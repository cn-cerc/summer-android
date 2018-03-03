package cn.cerc.summer.android.services;

import android.content.Context;
import android.content.Intent;

import org.json.JSONObject;

import cn.cerc.summer.android.forms.JavaScriptService;

/**
 * Created by Administrator on 2018/2/24.
 */

public class CallLogin implements JavaScriptService {
    @Override
    public String execute(Context context, JSONObject request) throws Exception {

        return "true";
    }
}

package cn.cerc.summer.android.services;

import android.content.Context;

import org.json.JSONObject;

import cn.cerc.summer.android.basis.TimerMethod;
import cn.cerc.summer.android.forms.JavaScriptService;

/**
 * Created by Administrator on 2017/12/29.
 */

public class HeartbeatCheck implements JavaScriptService {

    private int count = -1;
    private String token = null;

    @Override
    public String execute(Context context, JSONObject request) throws Exception {
        if (!request.has("status")) {
            return "没有传入指定参数";
        }
        if (!request.has("time")) {
            return "没有传入指定参数";
        }
        if (!request.has("token")) {
            return "没有传入指定参数";
        }
        boolean status = request.getBoolean("status");
        token = request.getString("token");
        if(status) {
            if (TimerMethod.getInstance().getTimer() == null) {
                if (request.has("time")) {
                    count = request.getInt("time") * 60000;
                }
                TimerMethod.getInstance().exce(status, count, token);
            }
        }else{
            if(TimerMethod.getInstance().getTimer() != null) {
                TimerMethod.getInstance().exce(status, count, token);
            }
        }
        return "";
    }
}
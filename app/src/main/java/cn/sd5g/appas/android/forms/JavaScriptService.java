package cn.sd5g.appas.android.forms;

import android.content.Context;

import org.json.JSONObject;

public interface JavaScriptService {

    String execute(Context context, JSONObject request) throws Exception;

}

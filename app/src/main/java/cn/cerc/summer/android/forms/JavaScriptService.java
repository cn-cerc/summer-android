package cn.cerc.summer.android.forms;

import android.content.Context;

import org.json.JSONObject;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/8/9.
 */

public interface JavaScriptService {

    String execute(Context context, JSONObject request) throws Exception;

}

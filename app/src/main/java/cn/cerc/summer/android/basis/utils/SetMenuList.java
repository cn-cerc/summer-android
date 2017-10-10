package cn.cerc.summer.android.basis.utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.util.Iterator;

import cn.cerc.summer.android.basis.forms.JavaScriptService;

/**
 * Created by Hasee on 2017/10/10.
 */

public class SetMenuList implements JavaScriptService {
    @Override
    public String execute(Context context, JSONObject request) throws Exception {
        if(!request.has("menus")){
            return "没有菜单项";
        }
        JSONObject obj = request.getJSONObject("menus");
        Iterator it = obj.keys();
        while (it.hasNext()) {
            String key = (String) it.next();
            String value = obj.getString(key);
            Log.i("SetMenuList", key + "-" + value);
        }
        return "ok";
    }
}

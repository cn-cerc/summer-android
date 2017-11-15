package cn.cerc.summer.android.services;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.util.Iterator;

import cn.cerc.summer.android.forms.FrmMain;
import cn.cerc.summer.android.forms.JavaScriptService;

/**
 * Created by Hasee on 2017/10/10.
 */

public class SetMenuList implements JavaScriptService {

    @Override
    public void execute(Context context, JSONObject request, JSONObject response) throws Exception {
        FrmMain frmMain = (FrmMain) context;


        if (request.has("menus")) {
            response.put("result", false);
            response.put("message", "menus 不允许为空！");
            return;
        }
        if (request.has("history")) {
            response.put("result", false);
            response.put("message", "history 不允许为空！");
            return;
        }

        //标题栏菜单数据
        JSONObject menus = request.getJSONObject("menus");
        Iterator it = menus.keys();
        while (it.hasNext()) {
            String key = (String) it.next();
            String value = menus.getString(key);
            Log.i("SetMenuList", key + "-" + value);
            frmMain.CatalogTitleWebView(key, value);
        }

        //右侧菜单数据
        JSONObject history = request.getJSONObject("history");
        Iterator itTitle = history.keys();
        while (itTitle.hasNext()) {
            String key = (String) itTitle.next();
            String value = history.getString(key);
            Log.i("SetMenuList——————", key + "-" + value);
            frmMain.CatalogWebView(key, value);
        }
        response.put("result", true);
        response.put("message", "OK");
    }
}

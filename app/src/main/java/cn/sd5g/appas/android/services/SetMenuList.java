package cn.sd5g.appas.android.services;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.util.Iterator;

import cn.sd5g.appas.android.forms.FrmMain;
import cn.sd5g.appas.android.forms.JavaScriptService;

public class SetMenuList implements JavaScriptService {

    @Override
    public String execute(Context context, JSONObject request) throws Exception {
        FrmMain frmMain = (FrmMain) context;
        if (request.has("menus")) {
            //标题栏菜单数据
            JSONObject menus = request.getJSONObject("menus");
            Iterator it = menus.keys();
            while (it.hasNext()) {
                String key = (String) it.next();
                String value = menus.getString(key);
                Log.i("SetMenuList", key + "-" + value);
                frmMain.CatalogTitleWebView(key, value);
            }
        }
        if (request.has("history")) {
            //右侧菜单数据
            JSONObject history = request.getJSONObject("history");
            Iterator itTitle = history.keys();
            while (itTitle.hasNext()) {
                String key = (String) itTitle.next();
                String value = history.getString(key);
                Log.i("SetMenuList——————", key + "-" + value);
                frmMain.CatalogWebView(key, value);
            }
        }
        return "ok";
    }
}

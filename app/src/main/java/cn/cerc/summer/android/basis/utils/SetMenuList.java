package cn.cerc.summer.android.basis.utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.cerc.summer.android.basis.core.MainTitleMenu;
import cn.cerc.summer.android.basis.forms.FrmMain;
import cn.cerc.summer.android.basis.forms.JavaScriptService;

/**
 * Created by Hasee on 2017/10/10.
 */

public class SetMenuList implements JavaScriptService {
    private List<MainTitleMenu> list;  //临时存储数据集合

    @Override
    public String execute(Context context, JSONObject request) throws Exception {
        if (!request.has("menus")) {
            return "没有菜单项";
        }
        FrmMain frmMain = (FrmMain) context;
        list = new ArrayList<MainTitleMenu>();
        for (int d = 0; d < frmMain.maxListl.size(); d++) {
            if (2 == frmMain.maxListl.get(d).getNum()) {
                list.add(frmMain.maxListl.get(d));
            }
        }
        frmMain.maxListl.removeAll(list);
        list.clear();
        for (int i = 0; i < frmMain.mTitleList1.size(); i++) {
            if (2 == frmMain.mTitleList1.get(i).getNum()) {
                list.add(frmMain.mTitleList1.get(i));
            }
        }
        frmMain.mTitleList1.removeAll(list);
        list.clear();
        //右侧菜单数据
        JSONObject objTitle = request.getJSONObject("history");
        Iterator itTitle = objTitle.keys();
        while (itTitle.hasNext()) {
            String key = (String) itTitle.next();
            String value = objTitle.getString(key);
            Log.i("SetMenuList——————", key + "-" + value);
            frmMain.CatalogWebView(key, value);
        }

        //标题栏菜单数据
        JSONObject objSetup = request.getJSONObject("menus");
        Iterator it = objSetup.keys();
        while (it.hasNext()) {
            String key = (String) it.next();
            String value = objSetup.getString(key);
            Log.i("SetMenuList", key + "-" + value);
            frmMain.CatalogTitleWebView(key, value);
        }
        return "ok";
    }
}

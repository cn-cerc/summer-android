package cn.cerc.summer.android.services;

import android.content.Context;

import org.json.JSONObject;

import cn.cerc.summer.android.core.MainTitleMenu;
import cn.cerc.summer.android.forms.FrmMain;
import cn.cerc.summer.android.forms.JavaScriptService;



public class RefreshMenu implements JavaScriptService {


    @Override
    public String execute(Context context, JSONObject request) throws Exception {
        if (!request.has("scriptTag")){
            return "没有指定的标记参数";
        }
        if (!request.has("scriptFunction")){
            return "没有指定要回调的函数";
        }
        if (request.has("title")){
            String title = request.optString("title");
            String scriptFunction = request.optString("scriptFunction");
            String scriptTag = request.optString("scriptTag");


            FrmMain.getInstance().mRightMenu.add(new MainTitleMenu(title, false, scriptFunction, 1,scriptTag));
            FrmMain.getInstance().mRightMenuTemp.add(new MainTitleMenu(title, false, scriptFunction, 1,scriptTag));
        }else {
            return "没有菜单更新";
        }


        return "";
    }

}

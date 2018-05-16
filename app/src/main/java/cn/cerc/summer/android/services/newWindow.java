package cn.cerc.summer.android.services;

import android.content.Context;

import org.json.JSONObject;

import cn.cerc.summer.android.core.MyApp;
import cn.cerc.summer.android.forms.FrmMain;
import cn.cerc.summer.android.forms.JavaScriptService;

/**
 * Created by Administrator on 2018/4/10.
 */

public class newWindow implements JavaScriptService {

    @Override
    public String execute(Context context, JSONObject request) throws Exception {
        if(!request.has("url")){
            return "没有传入指定参数";
        }
        String url = request.getString("url");
        if (!url.startsWith("http")) {
            url = String.format("%s/%s/%s", MyApp.HOME_URL, MyApp.FORMS_PATH, url);
        }
        if(!"".equals(url) && url != null) {
            FrmMain.getInstance().setAddWindow(url);
        }else{
            return "传入参数错误";
        }
        return "true";
    }
}

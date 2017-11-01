package cn.cerc.summer.android.services;

import android.content.Context;

import org.json.JSONObject;

import cn.cerc.summer.android.forms.JavaScriptService;

/**
 * Created by ${廖昭启} on 2017/11/1.
 */

public class RefreshMenu implements JavaScriptService {

    private RefreshMenuListener  mRefreshMenuListener;//提供动态修改菜单的监听器
    @Override
    public String execute(Context context, JSONObject request) throws Exception {
        if (!request.has("scriptTag")){
            return "没有指定的标记参数";
        }
        if (!request.has("scriptFunction")){
            return "没有指定要回调的函数";
        }
        if (!request.has("title")){
            if (mRefreshMenuListener!=null){
            mRefreshMenuListener.onRefreshMenuListener(request);
            return "true";
            }
        }else {
            return "没有更新菜单";
        }


        return "";
    }
    public  interface RefreshMenuListener{
        void onRefreshMenuListener(JSONObject title);
    }
    public  void setonRefreshMenuListener(RefreshMenuListener listener){
        mRefreshMenuListener  =listener;
    }
}

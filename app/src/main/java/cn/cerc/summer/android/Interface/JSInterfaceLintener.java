package cn.cerc.summer.android.Interface;

import android.content.Context;

import cn.cerc.summer.android.Utils.PhotoUtils;

/**
 * Created by fengm on 2016/12/23.
 */

public interface JSInterfaceLintener {

    Context getContext();

    /**
     * 登陆和退出的js调用回调
     * @param islogin   true：登录 false：退出
     */
    void LoginOrLogout(boolean islogin,String rul);

    void Action(String json, String action);

}

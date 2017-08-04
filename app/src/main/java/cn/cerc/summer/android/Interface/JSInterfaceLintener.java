package cn.cerc.summer.android.Interface;

import android.content.Context;

public interface JSInterfaceLintener {

    Context getContext();

    /**
     * 登陆和退出的js调用回调
     *
     * @param islogin true：登录
     *                false：退出
     */
    void LoginOrLogout(boolean islogin, String rul);

}

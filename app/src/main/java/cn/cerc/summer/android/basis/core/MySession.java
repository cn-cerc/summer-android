package cn.cerc.summer.android.basis.core;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/9/4.
 */

public class MySession {
    private static MySession instance = null;
    private String token = null;

    public static MySession getInstance() {
        if (instance == null)
            instance = new MySession();
        return instance;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

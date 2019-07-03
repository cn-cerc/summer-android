package cn.sd5g.appas.android.core;


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

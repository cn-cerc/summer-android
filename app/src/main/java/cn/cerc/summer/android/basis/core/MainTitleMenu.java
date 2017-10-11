package cn.cerc.summer.android.basis.core;

/**
 * Created by yty on 2017/10/10.
 */

public class MainTitleMenu {
    private String name;
    private boolean isline;
    private String url;
    private int num;

    public MainTitleMenu(String name, boolean isline, String url,int num) {
        this.name = name;
        this.isline = isline;
        this.url = url;
        this.num = num;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isline() {
        return isline;
    }

    public void setIsline(boolean isline) {
        this.isline = isline;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

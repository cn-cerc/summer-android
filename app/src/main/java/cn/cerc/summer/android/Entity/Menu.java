package cn.cerc.summer.android.Entity;

/**
 * Created by fff on 2016/11/22.
 */

public class Menu {
    private String menu;

    private int res;

    private int msg_num=0;

    public Menu(int msg_num, String menu, int res) {
        this.msg_num = msg_num;
        this.menu = menu;
        this.res = res;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }

    public int getMsg_num() {
        return msg_num;
    }

    public void setMsg_num(int msg_num) {
        this.msg_num = msg_num;
    }
}

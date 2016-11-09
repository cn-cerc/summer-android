package com.fmk.huagu.efitness.Entity.UI;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 数据库表
 * Created by fmk on 2016/11/9.
 */
@Table(name = "PasswordLogin")
public class PasswordLogin {

    @Column(name = "background")
    private String background;

    @Column(name = "backpath")
    private String backpath;

    @Column(name = "changelogin_text")
    private String changelogin_text;

    @Column(name = "changelogin_color")
    private String changelogin_color;

    @Column(name = "changelogin_size")
    private int changelogin_size;

    @Column(name = "changelogin_background")
    private String changelogin_background;

    @Column(name = "logopath")
    private String logopath;

    @Column(name = "phone_hint")
    private String phone_hint;

    @Column(name = "phone_left")
    private String phone_left;

    @Column(name = "phone_background")
    private String phone_background;

    @Column(name = "phone_color")
    private String phone_color;

    @Column(name = "phone_size")
    private int phone_size;

    @Column(name = "psd_hint")
    private String psd_hint;

    @Column(name = "psd_left")
    private String psd_left;

    @Column(name = "psd_background")
    private String psd_background;

    @Column(name = "psd_color")
    private String psd_color;

    @Column(name = "psd_size")
    private int psd_size;

    @Column(name = "forgetpsd_text")
    private String forgetpsd_text;

    @Column(name = "forgetpsd_color")
    private String forgetpsd_color;

    @Column(name = "forgetpsd_size")
    private int forgetpsd_size;

    @Column(name = "login_text")
    private String login_text;

    @Column(name = "login_color")
    private String login_color;

    @Column(name = "login_background")
    private String login_background;

    @Column(name = "login_size")
    private int login_size;

    @Column(name = "register_text")
    private String register_text;

    @Column(name = "register_color")
    private String register_color;

    @Column(name = "register_background")
    private String register_background;

    @Column(name = "register_size")
    private int register_size;

    @Column(name = "qqlogin_path")
    private String qqlogin_path;

    @Column(name = "wxlogin_path")
    private String wxlogin_path;

    public PasswordLogin(String background, String backpath, String changelogin_text, String changelogin_color, int changelogin_size, String changelogin_background, String logopath, String phone_hint, String phone_left, String phone_background, String phone_color, int phone_size, String psd_hint, String psd_left, String psd_background, String psd_color, int psd_size, String forgetpsd_text, String forgetpsd_color, int forgetpsd_size, String login_text, String login_color, String login_background, int login_size, String register_text, String register_color, String register_background, int register_size, String qqlogin_path, String wxlogin_path) {
        this.background = background;
        this.backpath = backpath;
        this.changelogin_text = changelogin_text;
        this.changelogin_color = changelogin_color;
        this.changelogin_size = changelogin_size;
        this.changelogin_background = changelogin_background;
        this.logopath = logopath;
        this.phone_hint = phone_hint;
        this.phone_left = phone_left;
        this.phone_background = phone_background;
        this.phone_color = phone_color;
        this.phone_size = phone_size;
        this.psd_hint = psd_hint;
        this.psd_left = psd_left;
        this.psd_background = psd_background;
        this.psd_color = psd_color;
        this.psd_size = psd_size;
        this.forgetpsd_text = forgetpsd_text;
        this.forgetpsd_color = forgetpsd_color;
        this.forgetpsd_size = forgetpsd_size;
        this.login_text = login_text;
        this.login_color = login_color;
        this.login_background = login_background;
        this.login_size = login_size;
        this.register_text = register_text;
        this.register_color = register_color;
        this.register_background = register_background;
        this.register_size = register_size;
        this.qqlogin_path = qqlogin_path;
        this.wxlogin_path = wxlogin_path;
    }

    public PasswordLogin() {
    }

    @Override
    public String toString() {
        return "PasswordLogin{" +
                "background='" + background + '\'' +
                ", backpath='" + backpath + '\'' +
                ", changelogin_text='" + changelogin_text + '\'' +
                ", changelogin_color='" + changelogin_color + '\'' +
                ", changelogin_size=" + changelogin_size +
                ", changelogin_background='" + changelogin_background + '\'' +
                ", logopath='" + logopath + '\'' +
                ", phone_hint='" + phone_hint + '\'' +
                ", phone_left='" + phone_left + '\'' +
                ", phone_background='" + phone_background + '\'' +
                ", phone_color='" + phone_color + '\'' +
                ", phone_size=" + phone_size +
                ", psd_hint='" + psd_hint + '\'' +
                ", psd_left='" + psd_left + '\'' +
                ", psd_background='" + psd_background + '\'' +
                ", psd_color='" + psd_color + '\'' +
                ", psd_size=" + psd_size +
                ", forgetpsd_text='" + forgetpsd_text + '\'' +
                ", forgetpsd_color='" + forgetpsd_color + '\'' +
                ", forgetpsd_size=" + forgetpsd_size +
                ", login_text='" + login_text + '\'' +
                ", login_color='" + login_color + '\'' +
                ", login_background='" + login_background + '\'' +
                ", login_size=" + login_size +
                ", register_text='" + register_text + '\'' +
                ", register_color='" + register_color + '\'' +
                ", register_background='" + register_background + '\'' +
                ", register_size=" + register_size +
                ", qqlogin_path='" + qqlogin_path + '\'' +
                ", wxlogin_path='" + wxlogin_path + '\'' +
                '}';
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getBackpath() {
        return backpath;
    }

    public void setBackpath(String backpath) {
        this.backpath = backpath;
    }

    public String getChangelogin_text() {
        return changelogin_text;
    }

    public void setChangelogin_text(String changelogin_text) {
        this.changelogin_text = changelogin_text;
    }

    public String getChangelogin_color() {
        return changelogin_color;
    }

    public void setChangelogin_color(String changelogin_color) {
        this.changelogin_color = changelogin_color;
    }

    public int getChangelogin_size() {
        return changelogin_size;
    }

    public void setChangelogin_size(int changelogin_size) {
        this.changelogin_size = changelogin_size;
    }

    public String getChangelogin_background() {
        return changelogin_background;
    }

    public void setChangelogin_background(String changelogin_background) {
        this.changelogin_background = changelogin_background;
    }

    public String getLogopath() {
        return logopath;
    }

    public void setLogopath(String logopath) {
        this.logopath = logopath;
    }

    public String getPhone_hint() {
        return phone_hint;
    }

    public void setPhone_hint(String phone_hint) {
        this.phone_hint = phone_hint;
    }

    public String getPhone_left() {
        return phone_left;
    }

    public void setPhone_left(String phone_left) {
        this.phone_left = phone_left;
    }

    public String getPhone_background() {
        return phone_background;
    }

    public void setPhone_background(String phone_background) {
        this.phone_background = phone_background;
    }

    public String getPhone_color() {
        return phone_color;
    }

    public void setPhone_color(String phone_color) {
        this.phone_color = phone_color;
    }

    public int getPhone_size() {
        return phone_size;
    }

    public void setPhone_size(int phone_size) {
        this.phone_size = phone_size;
    }

    public String getPsd_hint() {
        return psd_hint;
    }

    public void setPsd_hint(String psd_hint) {
        this.psd_hint = psd_hint;
    }

    public String getPsd_left() {
        return psd_left;
    }

    public void setPsd_left(String psd_left) {
        this.psd_left = psd_left;
    }

    public String getPsd_background() {
        return psd_background;
    }

    public void setPsd_background(String psd_background) {
        this.psd_background = psd_background;
    }

    public String getPsd_color() {
        return psd_color;
    }

    public void setPsd_color(String psd_color) {
        this.psd_color = psd_color;
    }

    public int getPsd_size() {
        return psd_size;
    }

    public void setPsd_size(int psd_size) {
        this.psd_size = psd_size;
    }

    public String getForgetpsd_text() {
        return forgetpsd_text;
    }

    public void setForgetpsd_text(String forgetpsd_text) {
        this.forgetpsd_text = forgetpsd_text;
    }

    public String getForgetpsd_color() {
        return forgetpsd_color;
    }

    public void setForgetpsd_color(String forgetpsd_color) {
        this.forgetpsd_color = forgetpsd_color;
    }

    public int getForgetpsd_size() {
        return forgetpsd_size;
    }

    public void setForgetpsd_size(int forgetpsd_size) {
        this.forgetpsd_size = forgetpsd_size;
    }

    public String getLogin_text() {
        return login_text;
    }

    public void setLogin_text(String login_text) {
        this.login_text = login_text;
    }

    public String getLogin_color() {
        return login_color;
    }

    public void setLogin_color(String login_color) {
        this.login_color = login_color;
    }

    public String getLogin_background() {
        return login_background;
    }

    public void setLogin_background(String login_background) {
        this.login_background = login_background;
    }

    public int getLogin_size() {
        return login_size;
    }

    public void setLogin_size(int login_size) {
        this.login_size = login_size;
    }

    public String getRegister_text() {
        return register_text;
    }

    public void setRegister_text(String register_text) {
        this.register_text = register_text;
    }

    public String getRegister_color() {
        return register_color;
    }

    public void setRegister_color(String register_color) {
        this.register_color = register_color;
    }

    public String getRegister_background() {
        return register_background;
    }

    public void setRegister_background(String register_background) {
        this.register_background = register_background;
    }

    public int getRegister_size() {
        return register_size;
    }

    public void setRegister_size(int register_size) {
        this.register_size = register_size;
    }

    public String getQqlogin_path() {
        return qqlogin_path;
    }

    public void setQqlogin_path(String qqlogin_path) {
        this.qqlogin_path = qqlogin_path;
    }

    public String getWxlogin_path() {
        return wxlogin_path;
    }

    public void setWxlogin_path(String wxlogin_path) {
        this.wxlogin_path = wxlogin_path;
    }
}

package com.fmk.huagu.efitness.Entity.UI;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by fff on 2016/11/9.
 */
@Table(name = "CaptchaLogin")
public class CaptchaLogin {

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

    @Column(name = "captcha_hint")
    private String captcha_hint;

    @Column(name = "captcha_left")
    private String captcha_left;

    @Column(name = "captcha_background")
    private String captcha_background;

    @Column(name = "captcha_color")
    private String captcha_color;

    @Column(name = "captcha_size")
    private int captcha_size;

    @Column(name = "captchas_color")
    private String captchas_color;

    @Column(name = "captchas_size")
    private int captchas_size;

    @Column(name = "captchas_text")
    private String captchas_text;

    @Column(name = "captchas_background")
    private String captchas_background;

    @Column(name = "login_text")
    private String login_text;

    @Column(name = "login_color")
    private String login_color;

    @Column(name = "login_background")
    private String login_background;

    @Column(name = "login_size")
    private int login_size;

    public CaptchaLogin(String background, String backpath, String changelogin_text, String changelogin_color, int changelogin_size, String changelogin_background, String logopath, String phone_hint, String phone_left, String phone_background, String phone_color, int phone_size, String captcha_hint, String captcha_left, String captcha_background, String captcha_color, int captcha_size, String captchas_color, int captchas_size, String captchas_text, String captchas_background, String login_text, String login_color, String login_background, int login_size) {
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
        this.captcha_hint = captcha_hint;
        this.captcha_left = captcha_left;
        this.captcha_background = captcha_background;
        this.captcha_color = captcha_color;
        this.captcha_size = captcha_size;
        this.captchas_color = captchas_color;
        this.captchas_size = captchas_size;
        this.captchas_text = captchas_text;
        this.captchas_background = captchas_background;
        this.login_text = login_text;
        this.login_color = login_color;
        this.login_background = login_background;
        this.login_size = login_size;
    }

    public CaptchaLogin() {
    }

    @Override
    public String toString() {
        return "CaptchaLogin{" +
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
                ", captcha_hint='" + captcha_hint + '\'' +
                ", captcha_left='" + captcha_left + '\'' +
                ", captcha_background='" + captcha_background + '\'' +
                ", captcha_color='" + captcha_color + '\'' +
                ", captcha_size=" + captcha_size +
                ", captchas_color='" + captchas_color + '\'' +
                ", captchas_size=" + captchas_size +
                ", captchas_text='" + captchas_text + '\'' +
                ", captchas_background='" + captchas_background + '\'' +
                ", login_text='" + login_text + '\'' +
                ", login_color='" + login_color + '\'' +
                ", login_background='" + login_background + '\'' +
                ", login_size=" + login_size +
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

    public String getCaptcha_hint() {
        return captcha_hint;
    }

    public void setCaptcha_hint(String captcha_hint) {
        this.captcha_hint = captcha_hint;
    }

    public String getCaptcha_left() {
        return captcha_left;
    }

    public void setCaptcha_left(String captcha_left) {
        this.captcha_left = captcha_left;
    }

    public String getCaptcha_background() {
        return captcha_background;
    }

    public void setCaptcha_background(String captcha_background) {
        this.captcha_background = captcha_background;
    }

    public String getCaptcha_color() {
        return captcha_color;
    }

    public void setCaptcha_color(String captcha_color) {
        this.captcha_color = captcha_color;
    }

    public int getCaptcha_size() {
        return captcha_size;
    }

    public void setCaptcha_size(int captcha_size) {
        this.captcha_size = captcha_size;
    }

    public String getCaptchas_color() {
        return captchas_color;
    }

    public void setCaptchas_color(String captchas_color) {
        this.captchas_color = captchas_color;
    }

    public int getCaptchas_size() {
        return captchas_size;
    }

    public void setCaptchas_size(int captchas_size) {
        this.captchas_size = captchas_size;
    }

    public String getCaptchas_text() {
        return captchas_text;
    }

    public void setCaptchas_text(String captchas_text) {
        this.captchas_text = captchas_text;
    }

    public String getCaptchas_background() {
        return captchas_background;
    }

    public void setCaptchas_background(String captchas_background) {
        this.captchas_background = captchas_background;
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
}

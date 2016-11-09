package com.fmk.huagu.efitness.Entity.UI;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by fff on 2016/11/9.
 */
@Table(name = "Register")
public class Register {
    @Column(name = "backgroundcolor")
    private String backgroundcolor;
    @Column(name = "background")
    private String background;
    @Column(name = "backpath")
    private String backpath;
    @Column(name = "backgroundcolor")
    private String logopath;
    @Column(name = "phone_hint")
    private String phone_hint;
    @Column(name = "phone_color")
    private String phone_color;
    @Column(name = "phone_left")
    private String phone_left;
    @Column(name = "phone_size")
    private int phone_size;
    @Column(name = "phone_bg")
    private String phone_bg;
    @Column(name = "psd_hint")
    private String psd_hint;
    @Column(name = "psd_color")
    private String psd_color;
    @Column(name = "ppsd_left")
    private String ppsd_left;
    @Column(name = "psd_size")
    private int psd_size;
    @Column(name = "psd_bg")
    private String psd_bg;
    @Column(name = "capt_hint")
    private String capt_hint;
    @Column(name = "capt_color")
    private String capt_color;
    @Column(name = "capt_left")
    private String capt_left;
    @Column(name = "capt_size")
    private int capt_size;
    @Column(name = "capt_bg")
    private String capt_bg;
    @Column(name = "capts_text")
    private String capts_text;
    @Column(name = "capts_color")
    private String capts_color;
    @Column(name = "capts_size")
    private int capts_size;
    @Column(name = "capts_bg")
    private String capts_bg;
    @Column(name = "regiest_text")
    private String regiest_text;
    @Column(name = "regiest_bg")
    private String regiest_bg;
    @Column(name = "regiest_color")
    private String regiest_color;
    @Column(name = "regiest_size")
    private int regiest_size;

    public Register(String backgroundcolor, String background, String backpath, String logopath, String phone_hint, String phone_color, String phone_left, int phone_size, String phone_bg, String psd_hint, String psd_color, String ppsd_left, int psd_size, String psd_bg, String capt_hint, String capt_color, String capt_left, int capt_size, String capt_bg, String capts_text, String capts_color, int capts_size, String capts_bg, String regiest_text, String regiest_bg, String regiest_color, int regiest_size) {
        this.backgroundcolor = backgroundcolor;
        this.background = background;
        this.backpath = backpath;
        this.logopath = logopath;
        this.phone_hint = phone_hint;
        this.phone_color = phone_color;
        this.phone_left = phone_left;
        this.phone_size = phone_size;
        this.phone_bg = phone_bg;
        this.psd_hint = psd_hint;
        this.psd_color = psd_color;
        this.ppsd_left = ppsd_left;
        this.psd_size = psd_size;
        this.psd_bg = psd_bg;
        this.capt_hint = capt_hint;
        this.capt_color = capt_color;
        this.capt_left = capt_left;
        this.capt_size = capt_size;
        this.capt_bg = capt_bg;
        this.capts_text = capts_text;
        this.capts_color = capts_color;
        this.capts_size = capts_size;
        this.capts_bg = capts_bg;
        this.regiest_text = regiest_text;
        this.regiest_bg = regiest_bg;
        this.regiest_color = regiest_color;
        this.regiest_size = regiest_size;
    }

    @Override
    public String toString() {
        return "Register{" +
                "backgroundcolor='" + backgroundcolor + '\'' +
                ", background='" + background + '\'' +
                ", backpath='" + backpath + '\'' +
                ", logopath='" + logopath + '\'' +
                ", phone_hint='" + phone_hint + '\'' +
                ", phone_color='" + phone_color + '\'' +
                ", phone_left='" + phone_left + '\'' +
                ", phone_size=" + phone_size +
                ", phone_bg='" + phone_bg + '\'' +
                ", psd_hint='" + psd_hint + '\'' +
                ", psd_color='" + psd_color + '\'' +
                ", ppsd_left='" + ppsd_left + '\'' +
                ", psd_size=" + psd_size +
                ", psd_bg='" + psd_bg + '\'' +
                ", capt_hint='" + capt_hint + '\'' +
                ", capt_color='" + capt_color + '\'' +
                ", capt_left='" + capt_left + '\'' +
                ", capt_size=" + capt_size +
                ", capt_bg='" + capt_bg + '\'' +
                ", capts_text='" + capts_text + '\'' +
                ", capts_color='" + capts_color + '\'' +
                ", capts_size=" + capts_size +
                ", capts_bg='" + capts_bg + '\'' +
                ", regiest_text='" + regiest_text + '\'' +
                ", regiest_bg='" + regiest_bg + '\'' +
                ", regiest_color='" + regiest_color + '\'' +
                ", regiest_size=" + regiest_size +
                '}';
    }

    public String getBackgroundcolor() {
        return backgroundcolor;
    }

    public void setBackgroundcolor(String backgroundcolor) {
        this.backgroundcolor = backgroundcolor;
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

    public String getPhone_color() {
        return phone_color;
    }

    public void setPhone_color(String phone_color) {
        this.phone_color = phone_color;
    }

    public String getPhone_left() {
        return phone_left;
    }

    public void setPhone_left(String phone_left) {
        this.phone_left = phone_left;
    }

    public int getPhone_size() {
        return phone_size;
    }

    public void setPhone_size(int phone_size) {
        this.phone_size = phone_size;
    }

    public String getPhone_bg() {
        return phone_bg;
    }

    public void setPhone_bg(String phone_bg) {
        this.phone_bg = phone_bg;
    }

    public String getPsd_hint() {
        return psd_hint;
    }

    public void setPsd_hint(String psd_hint) {
        this.psd_hint = psd_hint;
    }

    public String getPsd_color() {
        return psd_color;
    }

    public void setPsd_color(String psd_color) {
        this.psd_color = psd_color;
    }

    public String getPpsd_left() {
        return ppsd_left;
    }

    public void setPpsd_left(String ppsd_left) {
        this.ppsd_left = ppsd_left;
    }

    public int getPsd_size() {
        return psd_size;
    }

    public void setPsd_size(int psd_size) {
        this.psd_size = psd_size;
    }

    public String getPsd_bg() {
        return psd_bg;
    }

    public void setPsd_bg(String psd_bg) {
        this.psd_bg = psd_bg;
    }

    public String getCapt_hint() {
        return capt_hint;
    }

    public void setCapt_hint(String capt_hint) {
        this.capt_hint = capt_hint;
    }

    public String getCapt_color() {
        return capt_color;
    }

    public void setCapt_color(String capt_color) {
        this.capt_color = capt_color;
    }

    public String getCapt_left() {
        return capt_left;
    }

    public void setCapt_left(String capt_left) {
        this.capt_left = capt_left;
    }

    public int getCapt_size() {
        return capt_size;
    }

    public void setCapt_size(int capt_size) {
        this.capt_size = capt_size;
    }

    public String getCapt_bg() {
        return capt_bg;
    }

    public void setCapt_bg(String capt_bg) {
        this.capt_bg = capt_bg;
    }

    public String getCapts_text() {
        return capts_text;
    }

    public void setCapts_text(String capts_text) {
        this.capts_text = capts_text;
    }

    public String getCapts_color() {
        return capts_color;
    }

    public void setCapts_color(String capts_color) {
        this.capts_color = capts_color;
    }

    public int getCapts_size() {
        return capts_size;
    }

    public void setCapts_size(int capts_size) {
        this.capts_size = capts_size;
    }

    public String getCapts_bg() {
        return capts_bg;
    }

    public void setCapts_bg(String capts_bg) {
        this.capts_bg = capts_bg;
    }

    public String getRegiest_text() {
        return regiest_text;
    }

    public void setRegiest_text(String regiest_text) {
        this.regiest_text = regiest_text;
    }

    public String getRegiest_bg() {
        return regiest_bg;
    }

    public void setRegiest_bg(String regiest_bg) {
        this.regiest_bg = regiest_bg;
    }

    public String getRegiest_color() {
        return regiest_color;
    }

    public void setRegiest_color(String regiest_color) {
        this.regiest_color = regiest_color;
    }

    public int getRegiest_size() {
        return regiest_size;
    }

    public void setRegiest_size(int regiest_size) {
        this.regiest_size = regiest_size;
    }
}

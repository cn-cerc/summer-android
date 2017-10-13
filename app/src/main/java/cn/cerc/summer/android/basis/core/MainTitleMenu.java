package cn.cerc.summer.android.basis.core;

/**
 * Created by yangtaiyu on 2017/10/10.
 */

public class MainTitleMenu {
    private String name;
    private boolean isLine;
    private String url;
    private int layerSign;  //层次标识
    private int onlySign;     //唯一标识

    public int getOnlySign() {
        return onlySign;
    }

    public void setOnlySign(int onlySign) {
        this.onlySign = onlySign;
    }

    public MainTitleMenu(String name, boolean isLine, String url, int layerSign) {
        this.name = name;
        this.isLine = isLine;
        this.url = url;
        this.layerSign = layerSign;
    }

    public MainTitleMenu(String name, boolean isLine, String url, int layerSign, int onlySign) {
        this.name = name;
        this.isLine = isLine;
        this.url = url;
        this.layerSign = layerSign;
        this.onlySign = onlySign;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLine() {
        return isLine;
    }

    public void setLine(boolean line) {
        isLine = line;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getLayerSign() {
        return layerSign;
    }

    public void setLayerSign(int layerSign) {
        this.layerSign = layerSign;
    }

}

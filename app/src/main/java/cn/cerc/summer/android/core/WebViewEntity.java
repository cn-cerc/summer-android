package cn.cerc.summer.android.core;

/**
 * Created by yangtaiyu on 2017/10/10.
 */

public class WebViewEntity {
    private String title;
    private String url;

    public WebViewEntity(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

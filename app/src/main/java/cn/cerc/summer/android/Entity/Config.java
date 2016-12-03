package cn.cerc.summer.android.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by fff on 2016/12/1.
 */

public class Config {
    private static Config conf;
    public Config() {
        conf = this;
    }

    public static Config getConfig(){
        return conf;
    }

    private String rootSite;
    private String webVersion;
    private String appVersion;
    private String appUpgrade;

    private String startImage;
    private List<String> welcomeImages;
    private List<String> adImages;
    private HashMap<String,Boolean> homePages;
    private List<HomePager> homePagers;

    private String msgService;
    private String msgConfig;
    private boolean debug;

    public class HomePager{

        public HomePager(String homeurl, boolean is_home) {
            this.homeurl = homeurl;
            this.is_home = is_home;
        }

        private String homeurl;
        private boolean is_home;

        public String getHomeurl() {
            return homeurl;
        }

        public void setHomeurl(String homeurl) {
            this.homeurl = homeurl;
        }

        public boolean is_home() {
            return is_home;
        }

        public void setIs_home(boolean is_home) {
            this.is_home = is_home;
        }
    }

    public String getStartImage() {
        return startImage;
    }

    public void setStartImage(String startImage) {
        this.startImage = startImage;
    }

    public String getRootSite() {
        return rootSite;
    }

    public void setRootSite(String rootSite) {
        this.rootSite = rootSite;
    }

    public String getWebVersion() {
        return webVersion;
    }

    public void setWebVersion(String webVersion) {
        this.webVersion = webVersion;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getAppUpgrade() {
        return appUpgrade;
    }

    public void setAppUpgrade(String appUpgrade) {
        this.appUpgrade = appUpgrade;
    }

    public List<String> getWelcomeImages() {
        return welcomeImages;
    }

    public void setWelcomeImages(List<String> welcomeImages) {
        this.welcomeImages = welcomeImages;
    }

    public List<String> getAdImages() {
        return adImages;
    }

    public void setAdImages(List<String> adImages) {
        this.adImages = adImages;
    }

    public HashMap<String, Boolean> getHomePages() {
        return homePages;
    }

    public void setHomePages(HashMap<String, Boolean> homePages) {
        this.homePages = homePages;
        if (homePagers != null)
            homePagers.clear();
        else
            homePagers = new ArrayList<>();
        Iterator it = homePages.keySet().iterator();
        while (it.hasNext()){
            String key = (String)it.next();
            HomePager hp = new HomePager(key,homePages.get(key));
            homePagers.add(hp);
        }
    }

    public List<HomePager> getHomePagers() {
        return homePagers;
    }

    public void setHomePagers(List<HomePager> homePagers) {
        this.homePagers = homePagers;
    }

    public String getMsgService() {
        return msgService;
    }

    public void setMsgService(String msgService) {
        this.msgService = msgService;
    }

    public String getMsgConfig() {
        return msgConfig;
    }

    public void setMsgConfig(String msgConfig) {
        this.msgConfig = msgConfig;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}

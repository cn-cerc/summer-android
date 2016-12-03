package cn.cerc.summer.android.Utils;

/**
 * Created by fff on 2016/11/11.
 */

public class Constans {

    /**
     * 设备类型
     */
    public static final String DEVICE_TYPE = "android";
    /**
     *  shared 设置的key
     */
    public static String SHARED_SETTING_TAB = "setting";
    /**
     * 默认这个是首页
     * http://ehealth.lucland.com
     * http://121.40.181.228
     */
    public static String HOME_URL = "http://ehealth.lucland.com";
    /**
     *  shared 设置的 启动页
     */
    public static String SHARED_START_URL = "START";
    /**
     * 获取配置
     */
//    public static String GET_CONFIG = "http://ehealth.lucland.com/MobileConfig?device=android&deviceId=";

    /**
     * 第一次打开的页面  key
     */
    public static String HOME = "home";
    /**
     * 是不是第一次打开app key
     */
    public static String IS_FIRST_SHAREDKEY = "first";
    /**
     * 缩放程度  shared   key
     */
    public static String SCALE_SHAREDKEY = "InitialScale";

    /**
     * 接收网络变化 连接/断开 since 1.6.3
     */
    public final static String CONNECTION = "cn.jpush.android.intent.CONNECTION";
    /**
     * 用户打开自定义通知栏的intent
     */
    public final static String NOTIFICATION_OPENED = "cn.jpush.android.intent.NOTIFICATION_OPENED";
    /**
     * 用户接收SDK通知栏信息的intent-
     */
    public final static String NOTIFICATION_RECEIVED = "cn.jpush.android.intent.NOTIFICATION_RECEIVED";
    /**
     * 用户接收SDK消息的intent
     */
    public final static String MESSAGE_RECEIVED = "cn.jpush.android.intent.MESSAGE_RECEIVED";
    /**
     * 用户注册SDK的intent
     */
    public final static String REGISTRATION = "cn.jpush.android.intent.REGISTRATION";



}

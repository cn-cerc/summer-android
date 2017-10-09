package cn.cerc.summer.android.basis.core;

import java.io.File;

/**
 * Created by fff on 2016/11/11.
 */

public class Constans {

    /**
     * 设备类型
     */
    public static final String DEVICE_TYPE = "android";
    /**
     * 文件存储跟目录路径
     */
    public final static String APP_PATH = "app";
    public final static String DATA_PATH = "data";
    public final static String CONFIG_PATH = "config";
    public final static String HTML_PATH = "html";
    public final static String IMAGE_PATH = "image";
    /**
     * //缓存文件列表
     */
    public final static String CONFIGNAME = "cahcefile.txt";
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
    /**
     * shared 设置的key
     */
    public static String SHARED_SETTING_TAB = "setting";
    /**
     * shared 设置的 启动页
     */
    public static String SHARED_START_URL = "START";
    /**
     * shared 设置的 消息页
     */
    public static String SHARED_MSG_URL = "MSG_URL";
    /**
     * 第一次打开的页面  key
     */
    public static String HOME = "home";
    /**
     * 是不是第一次打开app key
     */
    public static String IS_FIRST_SHAREDKEY = "first";
    public static String FAIL_NUM_SHAREDKEY = "load";
    /**
     * 缩放程度  shared   key
     */
    public static String SCALE_SHAREDKEY = "InitialScale";

}

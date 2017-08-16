package cn.cerc.summer.android.basis.forms;

import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.HashMap;
import java.util.Map;

import cn.cerc.summer.android.basis.core.MyApp;
import cn.cerc.summer.android.basis.utils.CallLoginByAccount;
import cn.cerc.summer.android.basis.utils.CallLoginByPhone;
import cn.cerc.summer.android.basis.utils.CallPhoneNumber;
import cn.cerc.summer.android.basis.utils.CaptureImage;
import cn.cerc.summer.android.basis.utils.CaptureMovie;
import cn.cerc.summer.android.basis.utils.CaptureMusic;
import cn.cerc.summer.android.basis.utils.GetClientGPS;
import cn.cerc.summer.android.basis.utils.GetClientId;
import cn.cerc.summer.android.basis.utils.GetClientVersion;
import cn.cerc.summer.android.basis.utils.GetTokenByAlipay;
import cn.cerc.summer.android.basis.utils.GetTokenByQQ;
import cn.cerc.summer.android.basis.utils.GetTokenByWeibo;
import cn.cerc.summer.android.basis.utils.GetTokenByWeixin;
import cn.cerc.summer.android.basis.utils.PayByAlipay;
import cn.cerc.summer.android.basis.utils.PayByBank;
import cn.cerc.summer.android.basis.utils.PayByWeixin;
import cn.cerc.summer.android.basis.utils.PlayMovie;
import cn.cerc.summer.android.basis.utils.PlayMusic;
import cn.cerc.summer.android.basis.utils.ScanBarcode;
import cn.cerc.summer.android.basis.utils.ScanProduct;
import cn.cerc.summer.android.basis.utils.PlayImage;
import cn.cerc.summer.android.basis.utils.ShareToWeibo;
import cn.cerc.summer.android.basis.utils.ShareToWeixin;

/**
 * 供js调用的js
 * Created by fff on 2016/11/11.
 */
public class JavaScriptProxy extends Object {
    private static Map<Class, String> services = new HashMap<>();

    static {
        services.put(CallPhoneNumber.class, "拔打指定的电话号码");
        services.put(CallLoginByAccount.class, "使用标准的帐号、密码进行登录");
        services.put(CallLoginByPhone.class, "使用手机号及验证码进行登录");
        //
        services.put(CaptureImage.class, "拍照或选取本地图片，并上传到指定的位置");
        services.put(CaptureMovie.class, "录像或选择本地视频，并上传到指定的位置");
        services.put(CaptureMusic.class, "录音并生成指定文件，并上传到指定的位置");
        //
        services.put(GetClientGPS.class, "取得当前的GPS地址");
        services.put(GetClientId.class, "取得当前设备ID");
        services.put(GetClientVersion.class, "取得当前软件版本号");
        //
        services.put(GetTokenByAlipay.class, "使用支付宝帐号登录");
        services.put(GetTokenByQQ.class, "使用QQ帐号登录");
        services.put(GetTokenByWeibo.class, "使用微博帐号登录");
        services.put(GetTokenByWeixin.class, "使用微信帐号登录");
        //
        services.put(PayByAlipay.class, "呼叫支付宝付款");
        services.put(PayByBank.class, "呼叫银联付款");
        services.put(PayByWeixin.class, "呼叫微信付款");
        //
        services.put(PlayImage.class, "取得网上图片文件并进行缩放");
        services.put(PlayMovie.class, "取得网上视频文件并进行播放");
        services.put(PlayMusic.class, "取得网上音频文件并进行播放");
        //
        services.put(ScanBarcode.class, "扫一扫功能，扫描成功后上传到指定网址");
        services.put(ScanProduct.class, "批次扫描商品条码");
        //
        services.put(ShareToWeixin.class, "分享到微信");
        services.put(ShareToWeibo.class, "分享到微博");


    }

    private AppCompatActivity owner;
    private Map<String, String> resultunifiedorder;
    private StringBuffer sb;
    private String appid;

    private PayReq req;
    private IWXAPI msgApi;

    public JavaScriptProxy(AppCompatActivity owner) {
        this.owner = owner;
    }

    @JavascriptInterface
    public String hello2Html() {
        return "Hello Browser, from android.";
    }

    /**
     * 返回当前的版本号
     *
     * @return
     */
    public int getVersion() {
        try {
            return MyApp.getVersionCode(this.owner);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 供html调用 微信支付
     *
     * @param appId     app id
     * @param partnerId 商户号
     * @param prepayId  与支付单号
     * @param nonceStr  随机码
     * @param timeStamp 时间戳
     * @param sign      签名
     */
    @JavascriptInterface
    public void wxPay(String appId, String partnerId, String prepayId, String nonceStr, String timeStamp, String sign) {
        Toast.makeText(owner, "正在支付，请等待...", Toast.LENGTH_SHORT).show();
        Log.e("JavaScriptProxy", appId + " " + partnerId + " " + prepayId + " " + nonceStr + " " + timeStamp + " " + sign);
        msgApi = WXAPIFactory.createWXAPI(owner, appId);
        req = new PayReq();
        req.appId = appId;
        req.partnerId = partnerId;
        req.prepayId = prepayId;
        req.packageValue = "Sign=WXPay";
        req.nonceStr = nonceStr;
        req.timeStamp = timeStamp;
        req.sign = sign;
        msgApi.registerApp(req.appId);
        msgApi.sendReq(req);
    }

    /**
     * 登陆
     */
    @JavascriptInterface
    public void login() {
        String loginUrl = "http://ehealth.lucland.com/forms/Login.exit";
        login(loginUrl);
    }

    @JavascriptInterface
    public void login(String loginUrl) {
        if (owner instanceof FrmMain) {
            FrmMain aintf = (FrmMain) owner;
            aintf.LoginOrLogout(true, loginUrl);
        }
    }

    /**
     * 退出
     */
    @JavascriptInterface
    public void logout() {
        if (owner instanceof FrmMain) {
            FrmMain aintf = (FrmMain) owner;
            aintf.LoginOrLogout(false, "");
        }
    }

    //根据名称取得相应的函数名
    private Class getClazz(String classCode) {
        for (Class clazz : services.keySet()) {
            String args[] = clazz.getName().split("\\.");
            String temp = args[args.length - 1];
            if (temp.toUpperCase().equals(classCode.toUpperCase())) {
                return clazz;
            }
        }
        return null;
    }

    //列出所有可用的服务
    @JavascriptInterface
    public String list(){
        Map<String, String> items = new HashMap<>();
        JSONObject json = new JSONObject();
        for(Class clazz : services.keySet()){
            String args[] = clazz.getName().split("\\.");
            String temp = args[args.length - 1];
            json.put(temp, services.get(clazz));
        }
        return json.toString();
    }

    //判断是否支持指定的服务
    @JavascriptInterface
    public String support(String classCode) {
        Class clazz = getClazz(classCode);
        JavaScriptResult json = new JavaScriptResult();
        if (clazz != null) {
            json.setData(services.get(clazz));
            json.setResult(true);
        } else {
            json.setMessage("当前版本不支持: " + classCode);
        }
        return json.toString();
    }

    //调用指定的服务，须立即返回
    @JavascriptInterface
    public String send(String classCode, String dataIn) {
        Class clazz = getClazz(classCode);
        JavaScriptResult json = new JavaScriptResult();
        if (clazz != null) {
            try {
                Object object = clazz.newInstance();
                if (object instanceof JavaScriptService) {
                    JavaScriptService object1 = (JavaScriptService) object;
                    try {
                        json.setData(object1.execute(this.owner, dataIn));
                        json.setResult(true);
                    } catch (Exception e) {
                        json.setMessage(e.getMessage());
                    }
                } else {
                    json.setMessage("not support JavascriptInterface");
                }
            } catch (InstantiationException e) {
                json.setMessage("InstantiationException");
            } catch (IllegalAccessException e) {
                json.setMessage("IllegalAccessException");
            }
        } else {
            json.setMessage("当前版本不支持: " + classCode);
        }
        return json.toString();
    }

}

package cn.cerc.summer.android.basis.utils;

import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.HashMap;
import java.util.Map;

import cn.cerc.summer.android.basis.core.MyApp;
import cn.cerc.summer.android.basis.forms.FrmMain;

/**
 * 供js调用的js
 * Created by fff on 2016/11/11.
 */
public class JavaScriptProxy extends Object {
    private static Map<Class, String> kinds = new HashMap<>();

    static {
        kinds.put(GetClientId.class, "取得当前设备ID");
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
        for (Class clazz : kinds.keySet()) {
            String args[] = clazz.getName().split("\\.");
            String temp = args[args.length-1];
            if (temp.toUpperCase().equals(classCode.toUpperCase())) {
                return clazz;
            }
        }
        return null;
    }

    @JavascriptInterface
    public String support(String classCode) {
        Class clazz = getClazz(classCode);
        JavaScriptResult json = new JavaScriptResult();
        if (clazz != null) {
            json.setData(kinds.get(clazz));
            json.setResult(true);
        } else {
            json.setMessage("当前版本不支持: " + classCode);
        }
        return json.toString();
    }

    @JavascriptInterface
    public String send(String classCode, String dataIn) {
        Class clazz = getClazz(classCode);
        JavaScriptResult json = new JavaScriptResult();
        if (clazz != null) {
            try {
                Object object = clazz.newInstance();
                if (object instanceof JavaScriptService) {
                    JavaScriptService object1 = (JavaScriptService) object;
                    json.setData(object1.getData());
                    json.setResult(true);
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

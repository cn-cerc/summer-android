package cn.cerc.summer.android.Interface;

import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.Map;

import cn.cerc.summer.android.Activity.FrmMain;
import cn.cerc.summer.android.Utils.AppUtil;

/**
 * 供js调用的js
 * Created by fff on 2016/11/11.
 */
public class JavaScriptProxy extends Object {
    private AppCompatActivity owner;
    private Map<String, String> resultunifiedorder;
    private StringBuffer sb;
    private String appid;

    private PayReq req;
    private IWXAPI msgApi;

    public JavaScriptProxy(AppCompatActivity owner) {
        this.owner = owner;
    }

    public String hello2Html() {
        return "Hello Html";
    }

    /**
     * 返回当前的版本号
     *
     * @return
     */
    public int getVersion() {
        try {
            return AppUtil.getVersionCode(this.owner);
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

}

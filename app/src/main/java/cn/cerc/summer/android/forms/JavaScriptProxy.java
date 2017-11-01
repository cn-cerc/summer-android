package cn.cerc.summer.android.forms;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import cn.cerc.summer.android.services.CallLoginByAccount;
import cn.cerc.summer.android.services.CallLoginByPhone;
import cn.cerc.summer.android.services.CallPhoneNumber;
import cn.cerc.summer.android.services.CaptureImage;
import cn.cerc.summer.android.services.CaptureMovie;
import cn.cerc.summer.android.services.CaptureMusic;
import cn.cerc.summer.android.services.CreateQrcode;
import cn.cerc.summer.android.services.GetClientGPS;
import cn.cerc.summer.android.services.GetClientId;
import cn.cerc.summer.android.services.GetClientVersion;
import cn.cerc.summer.android.services.GetTalkLength;
import cn.cerc.summer.android.services.GetTokenByAlipay;
import cn.cerc.summer.android.services.GetTokenByQQ;
import cn.cerc.summer.android.services.GetTokenByWeibo;
import cn.cerc.summer.android.services.GetTokenByWeixin;
import cn.cerc.summer.android.services.PayByAlipay;
import cn.cerc.summer.android.services.PayByBank;
import cn.cerc.summer.android.services.PayByWeixin;
import cn.cerc.summer.android.services.PlayImage;
import cn.cerc.summer.android.services.PlayMovie;
import cn.cerc.summer.android.services.PlayMusic;
import cn.cerc.summer.android.services.RefreshMenu;
import cn.cerc.summer.android.services.ScanBarcode;
import cn.cerc.summer.android.services.ScanProduct;
import cn.cerc.summer.android.services.SetMenuList;
import cn.cerc.summer.android.services.SetTitle;
import cn.cerc.summer.android.services.ShareToWeibo;
import cn.cerc.summer.android.services.ShareToWeixin;
import cn.cerc.summer.android.services.ShowWarning;

/**
 * 供js调用的js
 * Created by fff on 2016/11/11.
 */
public class JavaScriptProxy extends Object {
    private static Map<Class, String> services = new LinkedHashMap<>();

    static {
        services.put(CallPhoneNumber.class, "拔打指定的电话号码");
        services.put(GetTalkLength.class, "取得最近一次电话拨出时长");
        //
        services.put(CallLoginByAccount.class, "使用标准的帐号、密码进行登录");
        services.put(CallLoginByPhone.class, "使用手机号及验证码进行登录");
        //
        services.put(CaptureImage.class, "拍照或选取本地图片，并上传到指定的位置");
        services.put(CaptureMovie.class, "录像或选择本地视频，并上传到指定的位置");
        services.put(CaptureMusic.class, "录音并生成指定文件，并上传到指定的位置");
        //
        services.put(CreateQrcode.class, "生成二维码或一维码");
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
        services.put(SetTitle.class, "设置浏览器标题");
        //
        services.put(ShareToWeixin.class, "分享到微信");
        services.put(ShareToWeibo.class, "分享到微博");
        //
        services.put(ShowWarning.class, "显示严重警告对话框");
        //
        services.put(SetMenuList.class, "设置菜单列表");
        services.put(RefreshMenu.class,"动态添加菜单");
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
    public String list() {
        Map<String, String> items = new LinkedHashMap<>();
        JSONObject json = new JSONObject();
        try {
            for (Class clazz : services.keySet()) {
                String args[] = clazz.getName().split("\\.");
                String temp = args[args.length - 1];
                json.put(temp, services.get(clazz));
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
                        JSONObject request = new JSONObject(dataIn);
                        json.setData(object1.execute(this.owner, request));
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

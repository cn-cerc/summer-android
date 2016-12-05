package cn.cerc.summer.android.Interface;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import cn.cerc.summer.android.Utils.AppUtil;

/**
 * Created by fff on 2016/11/11.
 */

public class JSInterface extends Object {

    private Context context;

    public JSInterface(Context context) {
        this.context = context;
    }

    public String hello2Html(){
        return "Hello Html";
    }


    /**
     * 返回当前的版本号
     * @return
     */
    public int getVersion(Context context) {
        try {
            return AppUtil.getVersionCode(context);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }


    private PayReq req;
    private IWXAPI msgApi;

    @JavascriptInterface
    public void wxPay(String appId, String partnerId,String prepayId,String nonceStr, String timeStamp,String sign){
        Log.e("xxxxxx",appId);
        Log.e("xxxxxx",partnerId);
        Log.e("xxxxxx",prepayId);
        Log.e("xxxxxx",nonceStr);
        Log.e("xxxxxx",timeStamp);
        Log.e("xxxxxx",sign);
        Toast.makeText(context,"Hello Html",Toast.LENGTH_SHORT).show();
        req = new PayReq();
        msgApi = WXAPIFactory.createWXAPI(context, appId);
        req.appId = appId;
        req.prepayId = prepayId;
        req.packageValue = "Sign=WXPay";
        req.nonceStr = nonceStr;
        req.timeStamp = timeStamp;
        req.sign = sign;
        msgApi.registerApp(appId);
        msgApi.sendReq(req);
    }

}

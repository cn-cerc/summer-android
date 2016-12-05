package cn.cerc.summer.android.Interface;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import org.json.JSONObject;

import cn.cerc.summer.android.Utils.AppUtil;

/**
 * Created by fff on 2016/11/11.
 */

public class JSInterface extends Object {

    private Context context;

    public JSInterface(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public String hello2Html(){
        Toast.makeText(context,"Hello Html",Toast.LENGTH_SHORT).show();
        Log.e("xxxxx","xxxxxxxxxx");
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

    //{nonce_str=pNN5yZlqew36TzxU, appid=wx880d8fc48ac1e88e, sign=49892924323589C849C4E3D0ADD225B7, trade_type=APP, return_msg=OK,
    // result_code=SUCCESS, mch_id=1281260601, return_code=SUCCESS, prepay_id=wx20161205152251561fa982900583537302, timestamp=1480922571}




}

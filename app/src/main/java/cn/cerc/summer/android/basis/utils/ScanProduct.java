package cn.cerc.summer.android.basis.utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import cn.cerc.summer.android.basis.forms.JavaScriptService;
import cn.cerc.summer.android.parts.barcode.FrmScanProduct;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/8/9.
 */

public class ScanProduct implements JavaScriptService {
    @Override
    public String execute(Context context, String dataIn) throws Exception {
        Log.d("ScanProduct", dataIn);
        JSONObject json = new JSONObject(dataIn);
        Log.d("ScanProduct", json.getString("title"));
        FrmScanProduct.startForm(context, json.getString("title"), json.getString("homeUrl"),
                json.getString("viewUrl"), json.getString("postUrl"));
        return "true";
    }
}

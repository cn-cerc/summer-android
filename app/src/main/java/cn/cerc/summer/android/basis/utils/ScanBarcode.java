package cn.cerc.summer.android.basis.utils;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONObject;

import cn.cerc.summer.android.basis.forms.JavaScriptService;
import cn.cerc.summer.android.parts.barcode.FrmScanBarcode;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/8/9.
 */

public class ScanBarcode implements JavaScriptService {
    @Override
    public String execute(Context context, JSONObject request) throws Exception {
        FrmScanBarcode.startForm((AppCompatActivity) context);
        return "true";
    }
}

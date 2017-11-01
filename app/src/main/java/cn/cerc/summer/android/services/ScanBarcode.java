package cn.cerc.summer.android.services;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONObject;

import cn.cerc.summer.android.forms.JavaScriptService;
import cn.cerc.summer.android.parts.barcode.FrmScanBarcode;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/8/9.
 */

public class ScanBarcode implements JavaScriptService {
    @Override
    public String execute(Context context, JSONObject request) throws Exception {
        //操作类型：0、回调js方法, 1: post到指定的url
        int type = request.has("type") ? request.getInt("type") : 0;
        if (type == 0) {
            if (!request.has("scriptFunction")) {
                return "没有指定要回调的javaScript函数";
            }
            if (!request.has("scriptTag")) {
                return "没有指定要回调的scriptTag参数";
            }
            FrmScanBarcode.startForm((AppCompatActivity) context,
                    request.getString("scriptFunction"), request.getString("scriptTag"));
            return "true";
        } else if (type == 1) {
            if (!request.has("postUrl")) {
                return "没有指定要回调的javaScript函数";
            }
            return "此功能还未实现！";
        } else {
            return "错误的参数调用方式！";
        }
    }
}

package cn.sd5g.appas.android.services;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONObject;

import cn.sd5g.appas.android.forms.JavaScriptService;
import cn.sd5g.appas.android.parts.barcode.FrmScanBarcode;

public class ScanBarcode implements JavaScriptService {
    @Override
    public String execute(Context context, JSONObject request) throws Exception {
        //操作类型：0、回调js方法, 1: post到指定的url
        int type = request.has("type") ? request.getInt("type") : 0;
        if (type == 0) {
            if (!request.has("_callback_")) {
                return "没有指定要回调的javaScript函数";
            }
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                FrmScanBarcode.startForm((AppCompatActivity) context,
                        request.getString("_callback_"));
                return "true";
            } else {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, 35);
                return "没有足够权限，请到手机权限设置中予以开放";
            }
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

package cn.cerc.summer.android.services;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONObject;

import cn.cerc.summer.android.forms.JavaScriptService;
import cn.cerc.summer.android.parts.barcode.FrmScanBarcode;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/8/9.
 */

public class ScanBarcode implements JavaScriptService {

    @Override
    public void execute(Context context, JSONObject request, JSONObject response) throws Exception {
        if (!request.has("type")) {
            response.put("result", false);
            response.put("message", "没有操作类型");
            return;
        }

        //操作类型：1、调后台；2、数据为 url ,直接跳转、3、回调js方法
        int type = request.getInt("type");
        if(type == 3 && !request.has("jsFun")){
            response.put("result", false);
            response.put("message", "没有回调js方法");
            return;
        }
        //后台 forms
        String forms = request.getString("forms");
        //js方法
        String jsFun = request.getString("jsFun");
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            FrmScanBarcode.startForm((AppCompatActivity) context, type, forms, jsFun);
            response.put("result", true);
            response.put("message", "OK");
            return;
        }else {
            ActivityCompat.requestPermissions((Activity)context, new String[]{Manifest.permission.CAMERA}, 35);
            response.put("result", false);
            response.put("message", "正在请求权限");
            return;
        }
    }
}

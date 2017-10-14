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
        if (!request.has("type")) {
            return "没有操作类型";
        }
        //操作类型：1、调后台；2、数据为 url ,直接跳转、3、回调js方法
        int type = request.getInt("type");
        if(type == 3 && !request.has("jsFun")){
            return "没有回调js方法";
        }
        //后台 forms
        String forms = request.getString("forms");
        //js方法
        String jsFun = request.getString("jsFun");

        FrmScanBarcode.startForm((AppCompatActivity) context, type, forms, jsFun);
        return "true";
    }
}

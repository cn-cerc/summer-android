package cn.cerc.summer.android.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSON;

import cn.cerc.summer.android.Activity.MipcaActivityCapture;
import cn.cerc.summer.android.Entity.JSParam;

/**
 * Created by fff on 2016/12/29.
 */

public class ZXingUtils extends HardwareJSUtils{

    public ZXingUtils() {
    }

    private static ZXingUtils zxu;

    public static ZXingUtils getInstance(){
        if (zxu == null) zxu = new ZXingUtils();
        return zxu;
    }

    public JSParam jsp;

    @Override
    public void setJson(String json) {
        jsp = JSON.parseObject(json,JSParam.class);
    }

    public void startScan(Activity activity, int requestCode){
        Intent intent = new Intent(activity, MipcaActivityCapture.class);
        activity.startActivityForResult(intent, requestCode);
    }


}

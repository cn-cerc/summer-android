package com.fmk.huagu.efitness.Utils;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by fff on 2016/11/11.
 */

public class JSInterface extends Object {


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



}

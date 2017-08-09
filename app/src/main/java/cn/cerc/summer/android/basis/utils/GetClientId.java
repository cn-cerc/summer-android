package cn.cerc.summer.android.basis.utils;

import android.content.Context;

import cn.cerc.summer.android.basis.core.PermissionUtils;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/8/9.
 */

public class GetClientId implements JavaScriptService {

    @Override
    public void setContent(Context context, String dataIn) {

    }

    @Override
    public String getData(){
        return PermissionUtils.IMEI;
    }

}

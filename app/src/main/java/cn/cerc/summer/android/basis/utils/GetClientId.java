package cn.cerc.summer.android.basis.utils;

import android.content.Context;

import cn.cerc.summer.android.basis.core.PermissionUtils;
import cn.cerc.summer.android.basis.forms.JavaScriptService;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/8/9.
 */

public class GetClientId implements JavaScriptService {

    @Override
    public String execute(Context context, String dataIn) {
        return PermissionUtils.IMEI;
    }
}

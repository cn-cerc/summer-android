package cn.cerc.summer.android.basis.utils;

import android.content.Context;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/8/9.
 */

public interface JavaScriptService {
    public void execute(Context context, String dataIn);

    public String getDataOut();
}

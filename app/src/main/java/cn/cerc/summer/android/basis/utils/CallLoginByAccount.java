package cn.cerc.summer.android.basis.utils;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import cn.cerc.summer.android.basis.forms.JavaScriptService;
import cn.cerc.summer.android.parts.login.FrmLoginByAccount;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/8/9.
 */

public class CallLoginByAccount implements JavaScriptService {
    @Override
    public String execute(Context context, String dataIn) {
        FrmLoginByAccount.startForm((AppCompatActivity) context);
        return "true";
    }
}

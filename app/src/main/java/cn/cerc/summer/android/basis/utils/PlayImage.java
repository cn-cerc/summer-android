package cn.cerc.summer.android.basis.utils;

import android.content.Context;

import cn.cerc.summer.android.basis.forms.JavaScriptService;
import cn.cerc.summer.android.parts.image.FrmZoomImage;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/8/9.
 */

public class PlayImage implements JavaScriptService {
    @Override
    public String execute(Context context, String dataIn) {
        //TODO: 此功能还未准备好
        FrmZoomImage.startForm(context, dataIn);
        return "还没有做完呢。。。";
    }
}

package cn.cerc.summer.android.basis.utils;

import android.content.Context;

import cn.cerc.summer.android.basis.forms.JavaScriptService;
import cn.cerc.summer.android.parts.movie.FrmPlayMovie;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/8/9.
 */

public class PlayMovie implements JavaScriptService {
    @Override
    public void execute(Context context, String dataIn) {
        //TODO: 此功能还未准备好
        FrmPlayMovie.startForm(context, dataIn);
    }

    @Override
    public String getDataOut() {
        return null;
    }
}

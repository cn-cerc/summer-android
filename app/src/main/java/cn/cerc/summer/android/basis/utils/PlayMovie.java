package cn.cerc.summer.android.basis.utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import cn.cerc.summer.android.basis.forms.JavaScriptService;
import cn.cerc.summer.android.parts.movie.FrmPlayMovie;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/8/9.
 */

public class PlayMovie implements JavaScriptService {

    @Override
    public String execute(Context context, JSONObject request) throws Exception {
        //TODO: 此功能还未准备好
        if (!request.has("movieUrl"))
            throw new RuntimeException("params error: url is null.");

        Log.d("PlayMovie", "movieUrl:" + request.get("movieUrl"));
        FrmPlayMovie.startForm(context, request.getString("movieUrl"));
        return "还没有做完呢。。。";
    }
}

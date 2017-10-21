package cn.cerc.summer.android.services;

import android.content.Context;

import org.json.JSONObject;

import cn.cerc.summer.android.forms.JavaScriptService;
import cn.cerc.summer.android.parts.movie.FrmPlayMovie;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/8/9.
 */

public class PlayMovie implements JavaScriptService {

    @Override
    public String execute(Context context, JSONObject request) throws Exception {
        String movieUrl = request.getString("movieUrl");
        if(movieUrl != null){
        FrmPlayMovie.startForm(context, movieUrl);
        }else{
            throw new RuntimeException("URL参数错误");
        }
        return "true";
    }
}

package cn.sd5g.appas.android.services;

import android.content.Context;

import org.json.JSONObject;

import cn.sd5g.appas.android.forms.JavaScriptService;
import cn.sd5g.appas.android.parts.movie.FrmPlayMovie;

public class PlayMovie implements JavaScriptService {

    @Override
    public String execute(Context context, JSONObject request) throws Exception {
        String movieUrl = request.getString("movieUrl");
        if (movieUrl != null) {
            FrmPlayMovie.startForm(context, movieUrl);
        } else {
            throw new RuntimeException("URL参数错误");
        }
        return "true";
    }
}

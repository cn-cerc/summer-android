package cn.cerc.summer.android.Interface;

import android.content.Context;

import org.json.JSONObject;

/**
 * Created by fff on 2016/11/30.
 */

public interface RequestCallback {

    void success(String url, JSONObject json);

    void failt(String url, String error);

    Context getContext();
}

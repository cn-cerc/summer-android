package cn.cerc.summer.android.Interface;

import android.content.Context;

import org.json.JSONObject;

public interface RequestCallback {
    void success(String url, JSONObject json);
    void Failt(String url, String error);
    Context getContext();
}

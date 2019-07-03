package cn.sd5g.appas.android.core;

import android.content.Context;

import org.json.JSONObject;

public interface RequestCallback {

    void success(String url, JSONObject json);

    void failt(String url, String error);

    Context getContext();
}

package cn.sd5g.appas.android.units;

import android.content.Context;

import java.io.File;

/**
 * 文件下载接口回调
 */
public interface GetFileCallback {
    void success(String url, File file);

    void failt(String url, String error);

    Context getContext();
}

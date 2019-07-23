package com.yt.hz.financial.argame.util;

/**
 * Created by admin on 2019/1/7.
 */

public interface AsyncCallback<T> {
    void onSuccess(T var1);

    void onFail(Throwable var1);

    void onProgress(String var1, float var2);
}

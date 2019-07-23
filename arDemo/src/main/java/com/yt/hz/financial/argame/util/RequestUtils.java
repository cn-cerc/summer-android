package com.yt.hz.financial.argame.util;

import android.util.Log;

import com.google.gson.Gson;
import com.yt.hz.financial.argame.bean.CommonJson;
import com.yt.hz.financial.argame.bean.CommonJson4List;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by admin on 2018/10/12.
 */

public class RequestUtils{

    private static Gson mGson;
    public static void requestList(final String url, final RequstCallbackList requstCallback){
        OkHttpUtils.get().url(url).build().execute(new Callback() {
            @Override
            public Object parseNetworkResponse(Response response, int i) throws Exception {
                String str = response.body().string();
                Log.i("yuan", response.message() + " , body " + str);
                JSONObject jsonObject = new JSONObject(str);
                String data = jsonObject.optString("data");

                Log.i("yuan",  " , data :" + data);
                if (requstCallback == null) {
                    return null;
                }

                if (mGson == null) {
                    mGson = new Gson();
                }
                Type type = requstCallback.getType();
                Log.e("yuan",requstCallback.getType().toString());
                Log.e("yuan",requstCallback.getClass().toString());
                Log.e("yuan",requstCallback.getTClass().getSimpleName());
                Log.e("yuan",requstCallback.getTClass().toString());
                Log.e("yuan",requstCallback.getTClass().getName());
                Log.e("yuan",requstCallback.getTClass()+"");
                CommonJson4List commonJson4List = CommonJson4List.fromJson(str,requstCallback.getTClass());
                Log.e("yuan",commonJson4List.getData().get(0).toString());
                requstCallback.success(commonJson4List.getData());
                //mGson.fromJson(str, type);



                /*boolean returnJson = false;
                Type type = requstCallback.getType();

                if (type instanceof Class) {
                    switch (((Class) type).getSimpleName()) {
                        case "Object":
                        case "String":
                            returnJson = true;
                            break;
                        default:
                            break;
                    }
                }

                if (returnJson) {
                    try {
                        requstCallback.onResolve(url);
                    } catch (Exception e) {
                        requstCallback.onFailed(-1, e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    try {
                        requstCallback.onResolve(mGson.fromJson(url, type));
                    } catch (Exception e) {
                        requstCallback.onFailed(-1, e.getMessage());
                        e.printStackTrace();
                    }
                }*/
                //requstCallback.success(commonJson4List);
                return null;
            }

            @Override
            public void onError(Call call, Exception e, int i) {

            }

            @Override
            public void onResponse(Object o, int i) {
            }
        });
    }

    public static void requestBean(final String url, final RequstCallback requstCallback){
        OkHttpUtils.get().url(url).build().execute(new Callback() {
            @Override
            public Object parseNetworkResponse(Response response, int i) throws Exception {
                String str = response.body().string();
                String result = (new JSONObject(str)).optString("result");
                Log.i("yuan", response.message() + " , body " + str);
                Log.i("yuan", response.message() + " , body " + result);

                if (requstCallback == null) {
                    return null;
                }
                requstCallback.success(str);

                /*CommonJson commonJson = CommonJson.fromJson(str,requstCallback.getTClass());
                Log.e("yuan ", commonJson.getResult().toString());
                requstCallback.success(commonJson.getResult());*/
                //mGson.fromJson(str, type);



                /*boolean returnJson = false;
                Type type = requstCallback.getType();

                if (type instanceof Class) {
                    switch (((Class) type).getSimpleName()) {
                        case "Object":
                        case "String":
                            returnJson = true;
                            break;
                        default:
                            break;
                    }
                }

                if (returnJson) {
                    try {
                        requstCallback.onResolve(url);
                    } catch (Exception e) {
                        requstCallback.onFailed(-1, e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    try {
                        requstCallback.onResolve(mGson.fromJson(url, type));
                    } catch (Exception e) {
                        requstCallback.onFailed(-1, e.getMessage());
                        e.printStackTrace();
                    }
                }*/
                //requstCallback.success(commonJson4List);
                return null;
            }

            @Override
            public void onError(Call call, Exception e, int i) {

            }

            @Override
            public void onResponse(Object o, int i) {
            }
        });
    }
    public abstract static class RequstCallbackList<T>{
        private Type mGenericSuperclass;

        public RequstCallbackList() {
            Type genericSuperclass = getClass().getGenericSuperclass();
            if (genericSuperclass instanceof ParameterizedType) {
                mGenericSuperclass = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
            } else {
                mGenericSuperclass = Object.class;
            }
        }
        public abstract void error();
        public abstract void success(List<T> t);
        public Type getType() {
            return mGenericSuperclass;
        }
        public Class getTClass()
        {
            Class tClass = (Class)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            return tClass;
        }
    }
    public abstract static class RequstCallback<T>{
        private Type mGenericSuperclass;

        public RequstCallback() {
            Type genericSuperclass = getClass().getGenericSuperclass();
            if (genericSuperclass instanceof ParameterizedType) {
                mGenericSuperclass = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
            } else {
                mGenericSuperclass = Object.class;
            }
        }
        public abstract void error();
        public abstract void success(T t);
        public Type getType() {
            return mGenericSuperclass;
        }
        public Class getTClass()
        {
            Class tClass = (Class)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            return tClass;
        }
    }
}

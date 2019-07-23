package com.yt.hz.financial.argame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.yt.hz.financial.argame.bean.AnswerBean;
import com.yt.hz.financial.argame.bean.CommonJson;
import com.yt.hz.financial.argame.bean.LocationMsgBean;
import com.yt.hz.financial.argame.util.RequestUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        findViewById(R.id.btn_arid).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LocationActivity.class);
                intent.putExtra("id","31d32f43-a030-4517-862b-1e863d334c0d");
                intent.putExtra("desc","hh");
                startActivity(intent);
            }
        });
        findViewById(R.id.btn_preload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, ARPlayActivity.class);
                intent.putExtra("id","31d32f43-a030-4517-862b-1e863d334c0d");
                intent.putExtra("desc","hh");
                startActivity(intent);
                /*Intent intent = new Intent(MainActivity.this, LoaderPreloadIDActivity.class);
                intent.putExtra("id","222e2c5c-76dd-4df6-baf6-50bf90a610b6");
                intent.putExtra("desc","hh");
                startActivity(intent);*/
            }
        });

        /*RequestUtils.requestBean("http://apis.haoservice.com/efficient/robot?info=你好美男子&key=4e0f836f8f174bd59a415c1d2b9f50a5", new RequestUtils.RequstCallback<String>() {
            @Override
            public void error() {

            }

            @Override
            public void success(String o) {
                Log.e("yuan getText",o);
                Gson gson = new Gson();
                AnswerBean answerBean =  gson.fromJson(o, AnswerBean.class);
                Log.e("yuan answerBean",answerBean.getResult().getText());
            }
        });*/
        requestMsg();
    }

    private void requestMsg(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");//数据类型为json格式，
                String jsonStr = "{\n" +
                        "  \"data\":{\n" +
                        "             \"content\": [\n" +
                        "                {\n" +
                        "                  \"data\": \"你好\"\n" +
                        "                }\n" +
                        "              ],\n" +
                        "             \"userInfo\": {\n" +
                        "                \"uniqueId\": \"uniqueId\"\n" +
                        "              }\n" +
                        "          },\n" +
                        "  \"key\":\"d8336a2db103406d8163dee0bf410f74\",\n" +
                        "  \"timestamp\":\""+System.currentTimeMillis()+"\"\n" +
                        "}";//json数据.
                RequestBody body = RequestBody.create(JSON, jsonStr);
                Request request = new Request.Builder()
                        .url("http://api.turingos.cn/turingos/api/v2")
                        .post(body)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        Log.e("yuan response",response.body().string());

                    }
                });//此处省略回调方法。
                /*OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("http://apis.haoservice.com/efficient/robot?info=你好美男子&key=4e0f836f8f174bd59a415c1d2b9f50a5")
                        //.url("http://api.map.baidu.com/place/v2/search?query=%E7%BE%8E%E9%A3%9F&scope=2&location=39.915,116.404&radius=2000&output=json&ak=Wp2U9nyGTbsbmuGWaG70mKNUMIZM0Ybz")
                        .get()
                        .build();

                try {
                    Response response = client.newCall(request).execute();

                    Log.e("yuan",response.message());
                    Log.e("yuan",response.toString());
                    Log.e("yuan",response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }
        }).start();

    }
}

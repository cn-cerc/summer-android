package cn.sd5g.appas.android.basis;

import android.util.Log;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cn.sd5g.appas.android.units.RequestCallback;

public class HttpClient {
    private final static String ENCODE = "utf-8";
    //要提交的网址
    private String webUrl;
    private int resultCode = 0;
    private String message = null;

    public HttpClient(String webUrl) {
        this.webUrl = webUrl;
    }

    /**
     * 封装请求体信息
     * params     :   params请求体内容，encode编码格式
     */
    private static StringBuffer getRequestData(Map<String, String> params) {
        StringBuffer stringBuffer = new StringBuffer();        //存储封装好的请求体信息
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), ENCODE))
                        .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //删除最后的一个"&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }

    public String post(Map<String, String> params) {
        return post(getRequestData(params).toString());
    }


    public String post(String request) {
        HttpURLConnection connection = null;
        try {
            Log.d("HttpClient", "post:" + webUrl);
            URL url = new URL(webUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3 * 1000);
            connection.setReadTimeout(3 * 1000);
            connection.setDoInput(true);                  //打开输入流，以便从服务器获取数据
            connection.setDoOutput(true);                 //打开输出流，以便向服务器提交数据
            connection.setUseCaches(false);               //使用Post方式不能使用缓存
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //传出数据
            if (request != null) {
                connection.setRequestProperty("Content-Length", "" + request.length());
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.writeBytes(request);
            }
            this.resultCode = connection.getResponseCode();            //获得服务器的响应码
            if (this.resultCode == HttpURLConnection.HTTP_OK) {
                //接收数据
                InputStream in = connection.getInputStream();
                //将接收到的数据转成String
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null)
                    response.append(line);
                return response.toString();
            } else {
                return null;
            }
        } catch (Exception e) {
            return e.getMessage();
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }

    /**
     * 处理服务器的响应结果（将输入流转化成字符串）
     * inputStream     :   inputStream服务器的响应输入流
     */
    private String dealResponseResult(InputStream inputStream) {
        String resultData = null;      //存储处理结果
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        try {
            while ((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultData = new String(byteArrayOutputStream.toByteArray());
        return resultData;
    }

    /**
     * POST请求
     * 带图片上传
     */
    public void POST(final String url, HashMap<String, String> map, final RequestCallback rc) {

        Log.e("map", map.toString());
        RequestParams param = new RequestParams(url);
        param.setMultipart(true);
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            String val = (String) entry.getValue();
            Log.d("print", "POST: " + key + "    " + val);
            if (null != key && key.contains("FileUrl_") || key.contains("followup"))
                param.addBodyParameter(key, new File(val));
            else {
                param.addBodyParameter(key, val);
            }
        }
        Log.d("print", "POST: " + url);
        x.http().post(param, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                Log.e("json", result.toString());
                rc.success(url, result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d("print", "onError: " + ex.toString());
                rc.failt(url, ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                rc.failt(url, "已取消");
            }

            @Override
            public void onFinished() {
            }
        });
    }

}

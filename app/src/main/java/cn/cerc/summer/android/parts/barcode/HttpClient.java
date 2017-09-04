package cn.cerc.summer.android.parts.barcode;

import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import cn.cerc.jdb.core.DataSet;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/8/26.
 */

public class HttpClient {
    private final static String ENCODE = "utf-8";
    //要提交的网址
    private String webUrl;
    private int resultCode = 0;
    private String message = null;

    public HttpClient(String webUrl) {
        this.webUrl = webUrl;
    }

    public String post(Map<String, String> params) {
        return post(getRequestData(params).toString());
    }

    public String post(DataSet dataIn) {
        return post(dataIn.toString());
    }

    public String post(String request) {
        HttpURLConnection connection = null;
        try {
            Log.d("HttpClient", "post:" + webUrl);
            URL url = new URL(webUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
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

}

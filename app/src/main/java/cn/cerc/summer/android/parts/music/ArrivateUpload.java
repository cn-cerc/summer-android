package cn.cerc.summer.android.parts.music;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2017/8/16.
 */

public class ArrivateUpload extends Thread {
    private final String BOUNDARYSTR = "--------aifudao7816510d1hq";
    private final String END = "\r\n";
    private final String LAST = "--";

    private String urlStr;
    private FileInputStream fis;//文件输入流
    private String fileName;

    public ArrivateUpload(String urlStr, FileInputStream fis, String fileName) {
        this.urlStr = urlStr;
        this.fis = fis;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        try {
            Log.e("ArrivateUpload", urlStr);
            URL httpUrl = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
            connection.setRequestMethod("POST");//必须为post
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-type", "multipart/form-data;boundary=" + BOUNDARYSTR);//固定格式
            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
            StringBuffer sb = new StringBuffer();
            /**
             * 写入文本数据
             */

            sb.append(LAST + BOUNDARYSTR + END);
            sb.append("Content-Disposition: form-data; name=\"data\"" + END + END);
//            sb.append(data + END);//内容
            /**
             * 循环写入文件
             */
            sb.append(LAST + BOUNDARYSTR + END);
            sb.append("Content-Disposition:form-data;Content-Type:application/octet-stream;name=\"file\";");
            sb.append("filename=\"" + fileName + "\"" + END + END);
            dos.write(sb.toString().getBytes("utf-8"));
            if (fis != null) {
                byte[] b = new byte[1024];
                int len;
                while ((len = fis.read(b)) != -1) {
                    dos.write(b, 0, len);
                }
                dos.write(END.getBytes());
            }
            dos.write((LAST + BOUNDARYSTR + LAST + END).getBytes());
            dos.flush();

            Log.e("ArrivateUpload", "上传完成");

            sb = new StringBuffer();
            if (connection.getResponseCode() == 200) {//请求成功
                Log.e("ArrivateUpload", "请求成功");
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                JSONObject object = new JSONObject(sb.toString());
                Log.e("ArrivateUpload", sb.toString());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

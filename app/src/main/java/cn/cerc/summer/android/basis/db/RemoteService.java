package cn.cerc.summer.android.basis.db;

import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import cn.cerc.jdb.core.DataSet;
import cn.cerc.summer.android.basis.core.MyApp;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/9/4.
 */

public class RemoteService {
    private final String serviceCode;
    private DataSet dataIn = new DataSet();
    private DataSet dataOut = new DataSet();
    private String message = null;
    private boolean result = false;

    public RemoteService(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public Message execByMessage(int messageId) {
        Message msg = new Message();
        msg.what = messageId;
        msg.obj = this.exec();
        return msg;
    }

    public RemoteService exec() {
        result = false;
        HttpClient client = new HttpClient(String.format("%s/%s/%s", MyApp.HOME_URL, MyApp.SERVICES_PATH, serviceCode));
        String response = client.post(dataIn);
        try {
            JSONObject json = null;
            json = new JSONObject(response);
            if (json.has("result")) {
                result = json.getBoolean("result");
                message = json.getString("message");
                if (json.has("data")) {
                    String data = json.getString("data");
//                  Log.d("AppDataSet", "data:" + data);
                    if (data.startsWith("[") && data.endsWith("]")) {
                        if (!dataOut.setJSON(data.substring(1, data.length() - 1))) {
                            Log.d("RemoteService", "dataSet:" + dataOut.getJSON());
                            message = "DataSet JSON error!";
                            result = false;
                        }
                    }
                }
            } else {
                if (json.has("message")) {
                    message = json.getString("message");
                } else {
                    message = response;
                }
            }
        } catch (JSONException e) {
            message = e.getMessage();
        } catch (Exception e) {
            message = e.getMessage();
        }
        return this;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public DataSet getDataIn() {
        return dataIn;
    }

    public DataSet getDataOut() {
        return dataOut;
    }

    public String getMessage() {
        return message;
    }

    public boolean isOk() {
        return result;
    }
}

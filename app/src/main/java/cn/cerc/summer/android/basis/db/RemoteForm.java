package cn.cerc.summer.android.basis.db;

import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.cerc.summer.android.basis.core.MyApp;
import cn.cerc.summer.android.basis.core.MySession;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/9/4.
 */

public class RemoteForm {
    private final String formCode;
    private Map<String, String> params = new HashMap<String, String>();
    private boolean result = false;
    private String message = null;
    private String data = null;

    public RemoteForm(String formCode) {
        this.formCode = formCode;
    }

    public Message execByMessage(int messageId) {
        Message msg = new Message();
        msg.what = messageId;
        msg.obj = this.exec();
        return msg;
    }

    public RemoteForm exec() {
        result = false;
        JSONObject json = null;
        try {
            String token = null;
            if (!params.containsKey("token")) {
                token = MySession.getInstance().getToken();
            }
            HttpClient client = null;
            if (token != null && !"".equals(token))
                client = new HttpClient(MyApp.getFormUrl(formCode) + String.format("?sid=%s", token));
            else
                client = new HttpClient(MyApp.getFormUrl(formCode));
            String response = client.post(params);
            json = new JSONObject(response);
            if (json.has("result")) {
                result = json.getBoolean("result");
                message = json.getString("message");
                data = json.getString("data");
            } else {
                if (json.has("message")) {
                    message = json.getString("message");
                } else {
                    message = response;
                }
            }
        } catch (Exception e) {
            message = e.getMessage();
        }
        return this;
    }

    public void putParam(String key, String value) {
        params.put(key, value);
    }

    public String getParam(String key) {
        return params.get(key);
    }

    public String getFormCode() {
        return formCode;
    }

    public String getMessage() {
        return message;
    }

    public String getData() {
        return data;
    }

    public boolean isOk() {
        return result;
    }

    public JSONObject getJSONData() {
        JSONObject json = null;
        try {
            json = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}

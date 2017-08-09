package cn.cerc.summer.android.basis.forms;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/8/9.
 */

public class JavaScriptResult {
    private boolean result = false;
    private String message = null;
    private String data = null;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        json.put("result", result);
        json.put("message", message);
        json.put("data", data);
        return json.toString();
    }
}

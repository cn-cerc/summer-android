package cn.cerc.android.js;

import org.json.JSONException;
import org.json.JSONObject;

public class JsRecord
{
    // 执行成功否
    private boolean result = false;
    // 返回讯息
    private String message = "";
    //
    public JSONObject resp = new JSONObject();

    public boolean getResult()
    {
        return result;
    }

    public void setResult(boolean result)
    {
        this.result = result;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    @Override
    public String toString()
    {
        try
        {
            resp.put("result", this.result);
            resp.put("message", this.message);
            return resp.toString();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return "{\"result\":false,\"message\":\"JSONException\"}";
        }
    }
}

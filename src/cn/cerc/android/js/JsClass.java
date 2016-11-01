package cn.cerc.android.js;

import org.json.JSONException;
import org.json.JSONObject;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import cn.cerc.android.R;
import cn.cerc.android.view.MyWebView;

public class JsClass
{
    private MyWebView myWebView;
    private JSONObject dataIn = null;
    private JsRecord dataOut = new JsRecord();
    private SoundPool soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
    private int streamId = -1;

    public JsClass(MyWebView myWebView)
    {
        this.myWebView = myWebView;
    }

    public String send(String key, String json)
    {
        try
        {
            this.setDataIn(new JSONObject(json));
            if (key.equals("scan"))
                this.dataOut.setResult(this.scan());
            else if (key.equals("picture"))
                this.dataOut.setResult(this.picture());
            else if (key.equals("playVoice"))
                this.dataOut.setResult(this.playVoice());
            else if (key.equals("getClientId")){
                String clientId = this.myWebView.getDeviceId();
                this.dataOut.setMessage(clientId);
                this.dataOut.setResult(true);
            }
            else if(key.equals("setParam")){
                myWebView.getContext();
            }
            else if(key.equals("getParam")){
                
            }
            else if(key.equals("getVersion")){
                this.dataOut.resp.put("version", "1.5.4");
                dataOut.setResult(true);
            }
            else
            {
                dataOut.setResult(false);
                dataOut.setMessage(String.format("不支持的动作：%s", key));
            }
        }
        catch (JSONException e)
        {
            dataOut.setResult(false);
            dataOut.setMessage(e.getMessage());
        }

        return dataOut.toString();
    }

    private boolean playVoice() throws JSONException
    {
        // TODO Auto-generated method stub
        boolean play = this.dataIn.getBoolean("play");
        if (play)
        {
            final int soundId = soundPool.load(myWebView.getmContext(), R.raw.warn, 1);
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    streamId = soundPool.play(soundId, 1, 1, 1, 0, 1);
                }
            }, 200);
            return true;
        }
        else
        {
            if (streamId > 0)
            {
                soundPool.stop(streamId);
                streamId = -1;
                return true;
            }
            else
                return false;
        }

    }

    private boolean picture()
    {
        // TODO Auto-generated method stub
        return false;
    }

    private boolean scan() throws JSONException
    {
        setMessage(this.dataIn.getString("context"));
        return true;
    }

    private void setMessage(String message)
    {
        this.dataOut.setMessage(message);
    }

    public JSONObject getDataIn()
    {
        return dataIn;
    }

    public void setDataIn(JSONObject dataIn)
    {
        this.dataIn = dataIn;
    }
 

}

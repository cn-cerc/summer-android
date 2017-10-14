package cn.cerc.summer.android.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import cn.cerc.summer.android.forms.FrmMain;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by fff on 2016/11/28.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {
//https://www.pgyer.com/app/install/9c2b21c08cee498109cfc02913ad6a64

    private static final String ACTION_NOTIFICATION_OPENED = JPushInterface.ACTION_NOTIFICATION_OPENED;
    private final String EXTRA = "cn.jpush.android.EXTRA";
    private final String NOTIFI_ID = "cn.jpush.android.NOTIFICATION_ID";
    private final String MSG_ID = "MSG_ID";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e("Receiver:", action);
        switch (action) {
            case Constans.CONNECTION:
                if (JPushInterface.isPushStopped(context))
                    JPushInterface.onResume(context);
                Intent networkintent = new Intent(FrmMain.NETWORK_CHANGE);
                context.sendBroadcast(networkintent);
                break;
            case Constans.NOTIFICATION_OPENED:
                //打开自定义的Activity
                try {
                    JSONObject jsonObject = new JSONObject(intent.getExtras().getString("cn.jpush.android.EXTRA"));
                    Intent i = new Intent(context, FrmMain.class);
                    i.putExtra("msgId", jsonObject.getString("msgId"));
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constans.NOTIFICATION_RECEIVED:
                if (intent.hasExtra(EXTRA)) {
                    try {
                        JSONObject jsonObject = new JSONObject(intent.getStringExtra(EXTRA));
                        if ("update".equals(jsonObject.getString("action"))) {
                            Intent updateintent = new Intent(FrmMain.APP_UPDATA);
                            context.sendBroadcast(updateintent);
                        }
                    } catch (JSONException e) {
                        Intent updateintent = new Intent(FrmMain.JSON_ERROR);
                        context.sendBroadcast(updateintent);
                        e.printStackTrace();
                    }
                }
                break;
            case Constans.MESSAGE_RECEIVED:
                Log.e("xxxx", "33333333");
                break;
            case Constans.REGISTRATION:
                Log.e("xxxx", "444444444");
                break;
            default:
                break;
        }
    }
}

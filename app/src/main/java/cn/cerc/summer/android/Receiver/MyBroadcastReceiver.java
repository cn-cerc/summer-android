package cn.cerc.summer.android.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import cn.cerc.summer.android.Activity.MainActivity;
import cn.cerc.summer.android.Utils.Constans;
import cn.jpush.android.api.JPushInterface;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by fff on 2016/11/28.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {
//https://www.pgyer.com/app/install/9c2b21c08cee498109cfc02913ad6a64

    private final String EXTRA = "cn.jpush.android.EXTRA";
    private final String NOTIFI_ID = "cn.jpush.android.NOTIFICATION_ID";
    private final String MSG_ID = "MSG_ID";
//    Bundle[{cn.jpush.android.ALERT=副研究员客户, cn.jpush.android.EXTRA={}, cn.jpush.android.NOTIFICATION_ID=834122552, cn.jpush.android.NOTIFICATION_CONTENT_TITLE=e健康, cn.jpush.android.MSG_ID=834122552}]
//Bundle[{cn.jpush.android.NOTIFICATION_TYPE=0, app=com.huagu.ehealth, cn.jpush.android.ALERT=副研究员客户的说法都是, cn.jpush.android.EXTRA={}, cn.jpush.android.NOTIFICATION_ID=835082864, cn.jpush.android.NOTIFICATION_CONTENT_TITLE=e健康, cn.jpush.android.MSG_ID=835082864}]

    private static final String ACTION_NOTIFICATION_OPENED = JPushInterface.ACTION_NOTIFICATION_OPENED;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e("Receiver:",action);
        switch (action){
            case Constans.CONNECTION:
                Intent networkintent = new Intent(MainActivity.NETWORK_CHANGE);
                context.sendBroadcast(networkintent);
                break;
            case Constans.NOTIFICATION_OPENED:
                //打开自定义的Activity
                try {
                    JSONObject jsonObject = new JSONObject(intent.getExtras().getString("cn.jpush.android.EXTRA"));
                    Intent i = new Intent(context, MainActivity.class);
                    i.putExtra("msgId",jsonObject.getString("msgId"));
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
                    context.startActivity(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constans.NOTIFICATION_RECEIVED:
                if (intent.hasExtra(EXTRA)){
                    try {
                        JSONObject jsonObject = new JSONObject(intent.getStringExtra(EXTRA));
                        if ("update".equals(jsonObject.getString("action"))){
                            Intent updateintent = new Intent(MainActivity.APP_UPDATA);
                            context.sendBroadcast(updateintent);
                        }
                    } catch (JSONException e) {
                        Intent updateintent = new Intent(MainActivity.JSON_ERROR);
                        context.sendBroadcast(updateintent);
                        e.printStackTrace();
                    }
                }
                break;
            case Constans.MESSAGE_RECEIVED:
                Log.e("xxxx","33333333");
                break;
            case Constans.REGISTRATION:
                Log.e("xxxx","444444444");
                break;
            default:
                break;
        }
    }
}

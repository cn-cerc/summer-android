package cn.cerc.summer.android.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import cn.cerc.summer.android.Activity.MainActivity;
import cn.cerc.summer.android.Utils.Constans;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by fff on 2016/11/28.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {
//https://www.pgyer.com/app/install/9c2b21c08cee498109cfc02913ad6a64

    private final String EXTRA = "cn.jpush.android.EXTRA";

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
                Log.e("xxxx","111111");
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
//            case Intent.ACTION_PACKAGE_ADDED:
//                Intent addedintent = new Intent(MainActivity.PACKAGE_ADDED);
//                context.sendBroadcast(addedintent);
//                break;
//            case Intent.ACTION_PACKAGE_REMOVED:
//                Intent removedintent = new Intent(MainActivity.PACKAGE_ADDED);
//                context.sendBroadcast(removedintent);
//                break;
//            case Intent.ACTION_PACKAGE_CHANGED:
//                Intent changedintent = new Intent(MainActivity.PACKAGE_ADDED);
//                context.sendBroadcast(changedintent);
//                break;
//            case Intent.ACTION_PACKAGE_REPLACED:
//                Intent replacedintent = new Intent(MainActivity.PACKAGE_ADDED);
//                context.sendBroadcast(replacedintent);
//                break;
//            case Intent.ACTION_PACKAGE_RESTARTED:
//                Intent restartedintent = new Intent(MainActivity.PACKAGE_ADDED);
//                context.sendBroadcast(restartedintent);
//                break;
            default:
                break;
        }
    }
}

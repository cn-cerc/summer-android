package cn.sd5g.appas.android.units;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.sd5gs.views.R;

import org.json.JSONException;
import org.json.JSONObject;

import cn.sd5g.appas.android.basis.RemoteForm;
import cn.sd5g.appas.android.forms.FrmMain;
import cn.jpush.android.api.JPushInterface;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MyBroadcastReceiver extends BroadcastReceiver {

    private static final String ACTION_NOTIFICATION_OPENED = JPushInterface.ACTION_NOTIFICATION_OPENED;
    private static String TAG = "print";
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
                    boolean CustomSound = false;
                    try {
                        CustomSound = processCustomMessage(context, intent.getExtras());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (!CustomSound) {
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
                }
                break;
            case Constans.MESSAGE_RECEIVED:
                Log.e("xxxx", "33333333");
                break;
            case Constans.REGISTRATION:
                Log.e("xxxx", "444444444");
                break;
            case "ELITOR_CLOCK":
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        RemoteForm rf = new RemoteForm("WebDefault.heartbeatCheck");
                        rf.execByMessage(3);
                    }
                }).start();
                break;
            default:
                break;
        }
    }

    /**
     * 自定义推送的声音
     *
     * @param context
     * @param bundle
     */
    private boolean processCustomMessage(Context context, Bundle bundle) throws JSONException {
        JSONObject json = null;
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
        //这一步必须要有而且setSmallIcon也必须要，没有就会设置自定义声音不成功
        notification.setAutoCancel(true).setSmallIcon(R.mipmap.android_logo);
        String alert = bundle.getString(JPushInterface.EXTRA_EXTRA);
        String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        String msg = bundle.getString(JPushInterface.EXTRA_ALERT);
        json = new JSONObject(alert);
        if (json.has("sound")) {
            String sound = json.getString("sound");
            switch (sound) {
                case "trade_mall.wav":
                    notification.setSound(
                            Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.trade_mall));
                    break;
                case "new_order.wav":
                    notification.setSound(
                            Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.new_order));
                    break;
                default:
                    return false;
            }
        } else {
            return false;
        }
        Intent mIntent = new Intent(context, FrmMain.class);
        mIntent.putExtra("msgId", json.getString("msgId"));
        mIntent.putExtras(bundle);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mIntent, 0);

        notification.setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setContentText(msg)
                .setContentTitle(title.equals("") ? "title" : title)
                .setSmallIcon(R.mipmap.android_logo);

        int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
        //bundle.get(JPushInterface.EXTRA_ALERT);推送内容
        Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
        //最后刷新notification是必须的
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(notifactionId, notification.build());
        return true;
    }
}

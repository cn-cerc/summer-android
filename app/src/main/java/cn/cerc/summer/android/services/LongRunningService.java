package cn.cerc.summer.android.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import cn.cerc.summer.android.core.MySession;


/**
 * Created by Administrator on 2018/1/5.
 */

public class LongRunningService extends Service {
    private static int anHour = 60000;
    private AlarmManager manager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getStringExtra("token") != null) {
            MySession.getInstance().setToken(intent.getStringExtra("token"));
        }
        anHour = intent.getIntExtra("time", anHour);
        Log.d("print", "onStartCommand: "+anHour);
        manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent intent1 = new Intent("ELITOR_CLOCK");
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent1, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        } else {
            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        }
        return super.onStartCommand(intent, flags, startId);
    }

}

package cn.cerc.summer.android.basis;

import android.os.Handler;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

import cn.cerc.summer.android.core.MySession;

/**
 * Created by Administrator on 2017/12/29.
 */

public class TimerMethod {
    private static TimerMethod instance = null;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            {
                switch (msg.what) {
                    case 1:
                        RemoteForm rf = new RemoteForm("WebDefault.heartbeatCheck");
                        rf.execByMessage(3);
                        break;
                }
            }
        }
    };
    private Timer mTimer;
    private long count = 900000;
    private TimerTask mTimerTask;

    private TimerMethod() {
    }

    public static TimerMethod getInstance() {
        if (instance == null) {
            instance = new TimerMethod();
        }
        return instance;
    }

    public void exce(boolean isTimer, int count, String token) {
        if (token != null) {
            MySession.getInstance().setToken(token);
        }
        stopTimer();
        if (isTimer) {
            startTimer(count);
        }

    }

    //启动定时器
    private void startTimer(int count) {
        if (count != -1) {
            this.count = count;
        }
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }
            };

            if (mTimer != null && mTimerTask != null) {
                mTimer.schedule(mTimerTask, this.count, this.count);
            }
        }
    }

    //停止定时器
    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    public Timer getTimer() {
        return this.mTimer;
    }
}

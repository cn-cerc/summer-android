package cn.cerc.summer.android.basis.forms;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.mimrc.vine.R;

import java.util.Timer;
import java.util.TimerTask;

public class FrmCheck extends AppCompatActivity {
    TextView textView;

    private FrmCheck instince;
    private Timer timer = new Timer();
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                Intent intent = new Intent();
                intent.setClass(instince, FrmMain.class);
                instince.startActivity(intent);
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frm_check);
        instince = this;
        textView = (TextView) findViewById(R.id.textView);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        }, 3000);

    }
}

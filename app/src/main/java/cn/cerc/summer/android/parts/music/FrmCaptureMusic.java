package cn.cerc.summer.android.parts.music;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.elves.app.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FrmCaptureMusic extends AppCompatActivity implements View.OnClickListener {

    public static String url;
    TextView tv_time;
    Button bt_start;
    Button bt_pause;
    Button bt_stop;
    ListView lv_recorder;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };
    private AudioRecorderUtils audioRecorderUtils;
    private String path;
    private int time;
    Runnable runTime = new Runnable() {
        @Override
        public void run() {
            time += 1;
            StringBuffer sb = new StringBuffer();
            int seconds;
            seconds = time / 3600;
            sb.append((seconds < 10) ? "0" + seconds + ":" : "" + seconds + ":");

            seconds = time % 3600 / 60;
            sb.append((seconds < 10) ? "0" + seconds + ":" : "" + seconds + ":");

            seconds = time % 3600 % 60;
            sb.append((seconds < 10) ? "0" + seconds : "" + seconds);
            tv_time.setText(sb.toString());
            handler.postDelayed(runTime, 1000);
        }
    };

    public static void startForm(Context context, String url) {
        FrmCaptureMusic.url = url;
        Intent intent = new Intent();
        intent.setClass(context, FrmCaptureMusic.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frm_capture_music);

        initUI();
        initData();
    }

    private void initUI() {
        tv_time = (TextView) findViewById(R.id.tv_time);
        bt_start = (Button) findViewById(R.id.bt_recorder_start);
        bt_pause = (Button) findViewById(R.id.bt_recorder_pause);
        bt_stop = (Button) findViewById(R.id.bt_recorder_stop);

        bt_start.setOnClickListener(this);
        bt_pause.setOnClickListener(this);
        bt_stop.setOnClickListener(this);
    }

    private void initData() {
        audioRecorderUtils = new AudioRecorderUtils();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_recorder_start:
                if (audioRecorderUtils.getStatus() != AudioRecorderUtils.Status.STATUS_START) {
                    if (audioRecorderUtils.getStatus() == AudioRecorderUtils.Status.STATUS_NO_READY) {
                        //初始化录音
                        String fileName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
                        Log.e(this.getClass().getSimpleName(), "录音文件名:" + fileName);
                        audioRecorderUtils.createDefaultAudio(fileName);
                    }
                    audioRecorderUtils.startRecorder();

                    bt_start.setVisibility(View.GONE);
                    bt_pause.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.bt_recorder_pause:
                if (audioRecorderUtils.getStatus() == AudioRecorderUtils.Status.STATUS_START) {
                    audioRecorderUtils.pauseRecord();
                    bt_start.setVisibility(View.VISIBLE);
                    bt_pause.setVisibility(View.GONE);
                }
                break;

            case R.id.bt_recorder_stop:
                if (audioRecorderUtils.getStatus() != AudioRecorderUtils.Status.STATUS_NO_READY) {
                    audioRecorderUtils.stopRecord();
                    bt_start.setVisibility(View.VISIBLE);
                    bt_pause.setVisibility(View.GONE);
                    tv_time.setText("停止录音");
                }
                break;
        }
    }
}

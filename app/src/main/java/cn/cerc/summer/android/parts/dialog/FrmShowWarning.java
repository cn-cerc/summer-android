package cn.cerc.summer.android.parts.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mimrc.vine.R;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/9/5.
 */

public class FrmShowWarning extends Activity implements View.OnClickListener {
    LinearLayout llRect;
    TextView lblMessage;
    Button btnOk;
    private MediaPlayer mediaPlayer;

    public static void startForm(Context content, String message) {
        Intent intent = new Intent();
        intent.setClass(content, FrmShowWarning.class);
        intent.putExtra("message", message);
        content.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFinishOnTouchOutside(false);
        setContentView(R.layout.activity_warn_dialog);

        llRect = (LinearLayout) findViewById(R.id.llRect);
        llRect.setOnClickListener(this);

        lblMessage = (TextView) findViewById(R.id.lblMessage);
        lblMessage.setText(getIntent().getStringExtra("message"));

        btnOk = (Button) findViewById(R.id.btnOk);
        btnOk.setOnClickListener(this);

        mediaPlayer = MediaPlayer.create(this, R.raw.alarm);
        mediaPlayer.setLooping(true); //循环播放
        mediaPlayer.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOk:
            case R.id.llRect:
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
                finish();
                break;
            default:
                break;
        }
    }
}

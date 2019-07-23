package com.yt.hz.financial.argame;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class LauncherActivity extends Activity{

    private CustomVideoView vv;
    private Button btn_start;
    private String mode = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        Intent intent = getIntent();
        if (intent!=null){
            mode = intent.getStringExtra("mode");
        }
        vv = (CustomVideoView) findViewById(R.id.videoview);
        btn_start = (Button) findViewById(R.id.btn_start);
        initView();
    }
    private void initView() {
        //设置播放加载路径
        vv.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+ R.raw.guide));
        //播放
        vv.start();
        //循环播放
        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (mode==null){
                    mode = "2";
                }
                switch (mode){
                    case "1":
                        startActivity(new Intent(LauncherActivity.this, LocationActivity.class));
                        break;
                    case "2":
                        startActivity(new Intent(LauncherActivity.this, ARPlayActivity.class));
                        break;
                     default:
                         startActivity(new Intent(LauncherActivity.this, LocationActivity.class));
                         break;
                }
                finish();
                //vv.start();
            }
        });

    }

   /* @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_start:
                Toast.makeText(this,"进入了主页", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }
    }*/
}
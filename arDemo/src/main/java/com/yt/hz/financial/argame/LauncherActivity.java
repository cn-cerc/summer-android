package com.yt.hz.financial.argame;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.easy.occlient.OCUtil;
import com.yt.hz.financial.argame.permission.PermissionDenied;
import com.yt.hz.financial.argame.permission.PermissionHelper;
import com.yt.hz.financial.argame.permission.PermissionPermanentDenied;
import com.yt.hz.financial.argame.permission.PermissionSucceed;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

public class LauncherActivity extends Activity {

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
//        btn_start = (Button) findViewById(R.id.btn_start);
//        btn_start.setOnClickListener(this);
        initView();
        //Log.e("yuan",getFilesDir().getAbsolutePath()+"ssdf_guida.ssdf");
        requestPermission();
    }

    private final int PERMISSION_CODE = 1;
    private void requestPermission() {
        PermissionHelper.with(this).requestCode(PERMISSION_CODE).requestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA
        ).request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.requestPermissionsResult(this, requestCode, permissions, grantResults);
    }
    public void log(String msg){
        Log.e("yuan",msg);
    }
    @PermissionDenied(requestCode = PERMISSION_CODE)
    private void onPermissionDenied() {
        Toast.makeText(this, "您拒绝了开启权限,可去设置界面打开", Toast.LENGTH_SHORT).show();
    }


    @PermissionPermanentDenied(requestCode = PERMISSION_CODE)
    private void onPermissionPermanentDenied() {
        Toast.makeText(this, "您选择了永久拒绝,可在设置界面重新打开", Toast.LENGTH_SHORT).show();
    }

    @PermissionSucceed(requestCode = PERMISSION_CODE)
    private void onPermissionSuccess() {
        try {
            File file = new File(getFilesDir().getAbsolutePath());
            if (!file.exists()){
                file.mkdirs();
            }
            OCUtil.getInstent().copyZipToSDCard(new File(getFilesDir().getAbsolutePath()+"/ssdf_guida.ssdf"),getAssets().open("ssdf_guida.ssdf"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        jumpAnother();
    }
    private void initView() {
        //设置播放加载路径
        vv.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.guide));
        //播放
        vv.start();
        //循环播放
        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                jumpAnother();
                //vv.start();
            }
        });
    }

    private void jumpAnother(){
        if (isOk){
            if (mode==null){
                mode = "1";
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
        }
        else {
            isOk = true;
        }
    }

    private boolean isOk = false;

//    @Override
//    public void onClick(View view) {
//        switch (view.getId()){
//            case R.id.btn_start:
//                Toast.makeText(this,"进入了主页", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(this, MainActivity.class));
//                finish();
//                break;
//        }
//    }
}
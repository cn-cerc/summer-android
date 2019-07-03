package cn.sd5g.appas.android.parts.movie;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.sd5gs.views.R;

public class FrmPlayMovie extends AppCompatActivity {
    /**
     * 视频播放
     */
    private static String PLAY_MOVIE = "FrmPlayMovie_playmovie";
    private CommonVideoView commonVideoView;
    private String videoUrl = null;
    private SharedPreferences sharedPreferences;
    private int num = 0;

    public static void startForm(Context context, String movieUrl) {
        Intent intent = new Intent();
        intent.setClass(context, FrmPlayMovie.class);
        intent.putExtra("url", movieUrl);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frm_play_movie);
        sharedPreferences = getSharedPreferences(PLAY_MOVIE, MODE_PRIVATE);
        commonVideoView = (CommonVideoView) findViewById(R.id.videoview_movie);
        Intent intent = getIntent();
        videoUrl = intent.getStringExtra("url");
        Log.d("print", "onCreate: url___)))___" + videoUrl);
        commonVideoView.start(videoUrl);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            commonVideoView.setFullScreen();
        } else {
            commonVideoView.setNormalScreen();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt(PLAY_MOVIE, commonVideoView.getCurrentPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        commonVideoView.setCurrentPosition(savedInstanceState.getInt(PLAY_MOVIE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        num = commonVideoView.getCurrentPosition();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (num != 0) {
            commonVideoView.setCurrentPosition(num);
            num = 0;
        }
    }

    @Override
    public void onBackPressed() {
        int i = getResources().getConfiguration().orientation;
        if (i == Configuration.ORIENTATION_PORTRAIT) {
            super.onBackPressed();
        } else if (i == Configuration.ORIENTATION_LANDSCAPE) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
}

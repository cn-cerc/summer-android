package cn.cerc.summer.android.parts.movie;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.mimrc.vine.R;

public class FrmPlayMovie extends AppCompatActivity implements MediaPlayer.OnCompletionListener, View.OnClickListener {
    ImageView content;
    VideoView vdv;
    private int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frm_play_movie);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //保存屏幕一直开着不会自动休眠
        Intent it = getIntent();
        Uri uri = Uri.parse(it.getStringExtra("url"));

        if(savedInstanceState != null)
            pos = savedInstanceState.getInt("pos", 0);
        content = (ImageView) findViewById(R.id.movieBack);
        vdv = (VideoView) findViewById(R.id.vdv);

        MediaController mediaCtrl = new MediaController(this);

        content.setOnClickListener(this);
        vdv.setMediaController(mediaCtrl);
        vdv.setVideoURI(uri);
        vdv.setOnCompletionListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        vdv.seekTo(pos);
        vdv.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        pos = vdv.getCurrentPosition();
        vdv.stopPlayback();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("pos", pos);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgZoomBack:
                finish();
                break;
            default:
                break;
        }
    }

    public static void startForm(Context context, String movieUrl) {
        Intent intent = new Intent();
        intent.setClass(context, FrmPlayMovie.class);
        intent.putExtra("url", movieUrl);
        context.startActivity(intent);
    }
}

package cn.cerc.summer.android.parts.movie;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mimrc.vine.R;

import cn.cerc.summer.android.parts.image.FrmZoomImage;

public class FrmPlayMovie extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frm_play_movie);
    }

    public static void startForm(Context context, String movieUrl) {
        Intent intent = new Intent();
        intent.setClass(context, FrmZoomImage.class);
        intent.putExtra("url", movieUrl);
        context.startActivity(intent);
    }
}

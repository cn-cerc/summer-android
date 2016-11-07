package com.fmk.fff.efitness.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.fmk.fff.efitness.MyApplication;
import com.fmk.fff.efitness.R;

import org.xutils.x;

import java.io.File;

public class StartActivity extends BaseActivity implements Animation.AnimationListener {

    private ImageView startimage;
    private boolean is_skip;//是否跳转
    private Animation animation;//渐变动画

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start);

        startimage = (ImageView) this.findViewById(R.id.startimage);

        x.image().bind(startimage, new File("/storage/emulated/0/DCIM/Screenshots/Screenshot_20161031-205359.png").toURI().toString(), MyApplication.getInstance().imageOptions);


        animation = AnimationUtils.loadAnimation(this, R.anim.startanim);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startimage.startAnimation(animation);
        animation.setAnimationListener(this);
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (is_skip) is_skip = true;
        else {
            startActivity(new Intent(this, PasswordLoginActivity.class));
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }
}

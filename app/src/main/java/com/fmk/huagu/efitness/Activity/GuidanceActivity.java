package com.fmk.huagu.efitness.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.fmk.huagu.efitness.R;
import com.fmk.huagu.efitness.Utils.Constans;

public class GuidanceActivity extends BaseActivity implements Animation.AnimationListener {

    private ImageView imageview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_guidance);

        imageview = (ImageView) this.findViewById(R.id.imageview);

        imageview.setImageResource(R.mipmap.startimage);

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.startanim);
        imageview.startAnimation(animation);
        animation.setAnimationListener(this);

    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (settingShared.getBoolean(Constans.IS_FIRST_SHAREDKEY,true)) {
            settingShared.edit().putBoolean(Constans.IS_FIRST_SHAREDKEY, false).commit();
            startActivity(new Intent(this, StartActivity.class));
        }
        finish();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

}

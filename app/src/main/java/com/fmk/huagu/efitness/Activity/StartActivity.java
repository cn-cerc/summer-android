package com.fmk.huagu.efitness.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.fmk.huagu.efitness.Entity.UI.CaptchaLogin;
import com.fmk.huagu.efitness.Entity.UI.PasswordLogin;
import com.fmk.huagu.efitness.Entity.UI.Register;
import com.fmk.huagu.efitness.MyApplication;
import com.fmk.huagu.efitness.R;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
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

        x.image().bind(startimage, new File("/storage/emulated/0/DCIM/Screenshots/Screenshot_20161031-205359.png").toURI().toString());
        animation = AnimationUtils.loadAnimation(this, R.anim.startanim);

        try {
            InitData();
        } catch (DbException e) {
            e.printStackTrace();
        }

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
        else startActivity(new Intent(this, PasswordLoginActivity.class));
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    private void InitData() throws DbException {
        DbManager db = x.getDb(MyApplication.getInstance().daoconfig);
        PasswordLogin psdlogin = new PasswordLogin("#FFFFFF","path","验证码登录","#48B3BD",16,"#48B3BD","path","请输入手机号码","path","#48B3BD","#48B3BD",16,"请输入密码","path","#48B3BD","#48B3BD",16,"忘记密码","#48B3BD",16,"登录","#48B3BD","#48B3BD",16,"注册","#48B3BD","#48B3BD",16,"path","path");
        db.save(psdlogin);
        CaptchaLogin captLogin = new CaptchaLogin("#FFFFFF","path","验证码登录","#48B3BD",16,"#48B3BD","path","请输入手机号码","path","#48B3BD","#48B3BD",16,"请输入验证码","path","#48B3BD","#48B3BD",16,"#48B3BD",16,"发送验证码","#48B3BD","登录","#48B3BD","#48B3BD",16);
        db.save(captLogin);
        Register regiest = new Register("#FFFFFF","path","path","path","请输入手机号","#48B3BD","path",16,"#48B3BD","请输入密码","#48B3BD","path",16,"#48B3BD","请输入验证码","#48B3BD","path",16,"#48B3BD","发送验证码","#48B3BD",16,"#48B3BD","注册","#48B3BD","#48B3BD",16);
        db.save(regiest);

    }

}

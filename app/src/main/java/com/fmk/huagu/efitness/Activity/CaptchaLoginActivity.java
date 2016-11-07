package com.fmk.huagu.efitness.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.fmk.huagu.efitness.R;

/**
 * 验证码登录
 */
public class CaptchaLoginActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton back;
    private ImageView logo, phone_pic, captcha_pic;
    private Button get_captcha, login;
    private EditText phone_num, captcha_code;
    private TextView login_change;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_captcha_login);

        InitView();//初始化视图

    }

    private void InitView() {
        back = (ImageButton) this.findViewById(R.id.back);
        logo = (ImageView) this.findViewById(R.id.logo);
        phone_pic = (ImageView) this.findViewById(R.id.phone_pic);
        captcha_pic = (ImageView) this.findViewById(R.id.captcha_pic);
        get_captcha = (Button) this.findViewById(R.id.get_captcha);
        login = (Button) this.findViewById(R.id.login);
        phone_num = (EditText) this.findViewById(R.id.phone_num);
        captcha_code = (EditText) this.findViewById(R.id.captcha_code);
        login_change = (TextView) this.findViewById(R.id.login_change);

        back.setOnClickListener(this);
        login_change.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.login_change:
                startActivity(new Intent(this, PasswordLoginActivity.class));
                finish();
                break;
            case R.id.login:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            default:
                break;
        }
    }

}

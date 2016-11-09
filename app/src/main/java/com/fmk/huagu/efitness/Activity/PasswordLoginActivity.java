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
 * 密码登录
 */
public class PasswordLoginActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton back, qq_login, wx_login;
    private ImageView logo, phone_pic, pwd_pic;
    private Button login, register;
    private EditText phone_num, password;
    private TextView forget_pwd, login_change;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_password_login);

        InitView();

    }

    private void InitView() {
        back = (ImageButton) this.findViewById(R.id.back);
        qq_login = (ImageButton) this.findViewById(R.id.qq_login);
        wx_login = (ImageButton) this.findViewById(R.id.wx_login);
        logo = (ImageView) this.findViewById(R.id.logo);
        phone_pic = (ImageView) this.findViewById(R.id.phone_pic);
        pwd_pic = (ImageView) this.findViewById(R.id.pwd_pic);
        login = (Button) this.findViewById(R.id.login);
        register = (Button) this.findViewById(R.id.register);
        phone_num = (EditText) this.findViewById(R.id.phone_num);
        password = (EditText) this.findViewById(R.id.password);
        forget_pwd = (TextView) this.findViewById(R.id.forget_pwd);
        login_change = (TextView) this.findViewById(R.id.login_change);

        back.setOnClickListener(this);
        login_change.setOnClickListener(this);
        register.setOnClickListener(this);
        forget_pwd.setOnClickListener(this);
        login.setOnClickListener(this);
        qq_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.login_change:
                startActivity(new Intent(this, CaptchaLoginActivity.class));
                finish();
                break;
            case R.id.register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.forget_pwd:
                startActivity(new Intent(this, ForgetPasswordActivity.class));
                break;
            case R.id.login:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            case R.id.qq_login:
                break;
            default:
                break;
        }
    }
}

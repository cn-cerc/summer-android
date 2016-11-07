package com.fmk.fff.efitness.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.fmk.fff.efitness.R;

/**
 * 注册
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton back;
    private ImageView logo, register, phone_pic, captcha_pic, pwd_pic;
    private Button get_captcha, login;
    private EditText phone_num, captcha_code, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        InitView();

    }

    private void InitView() {
        back = (ImageButton) this.findViewById(R.id.back);
        logo = (ImageView) this.findViewById(R.id.logo);
        phone_pic = (ImageView) this.findViewById(R.id.phone_pic);
        captcha_pic = (ImageView) this.findViewById(R.id.captcha_pic);
        pwd_pic = (ImageView) this.findViewById(R.id.pwd_pic);
        get_captcha = (Button) this.findViewById(R.id.get_captcha);
        login = (Button) this.findViewById(R.id.login);
        phone_num = (EditText) this.findViewById(R.id.phone_num);
        captcha_code = (EditText) this.findViewById(R.id.captcha_code);
        password = (EditText) this.findViewById(R.id.password);

        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            default:
                break;
        }
    }
}

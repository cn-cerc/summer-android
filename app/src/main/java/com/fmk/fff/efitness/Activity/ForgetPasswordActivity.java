package com.fmk.fff.efitness.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fmk.fff.efitness.R;

public class ForgetPasswordActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton back;
    private TextView title;
    private EditText phone, catcha_code, password;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        InitView();

    }

    private void InitView() {
        back = (ImageButton) this.findViewById(R.id.back);
        title = (TextView) this.findViewById(R.id.title);
        phone = (EditText) this.findViewById(R.id.phone);
        catcha_code = (EditText) this.findViewById(R.id.catcha_code);
        password = (EditText) this.findViewById(R.id.password);
        submit = (Button) this.findViewById(R.id.submit);

        back.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }
}

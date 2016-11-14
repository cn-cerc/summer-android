package com.fmk.huagu.efitness.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fmk.huagu.efitness.R;
import com.fmk.huagu.efitness.Utils.Constans;

public class SettingActivity extends BaseActivity {

    private TextView textview;
    private EditText edittext,scale;
    private Button button;
    private String InitialScale;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        button = (Button) this.findViewById(R.id.save);
        edittext = (EditText) this.findViewById(R.id.url);
        scale = (EditText) this.findViewById(R.id.scale);
        edittext.setText(settingShared.getString(Constans.HOME,""));
        scale.setText(settingShared.getString(Constans.SCALE_SHAREDKEY,""));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!TextUtils.isEmpty(edittext.getText().toString().trim())){
                    settingShared.edit().putString(Constans.HOME,edittext.getText().toString().trim()).commit();
//                }

                if (!TextUtils.isEmpty(scale.getText().toString().trim())){
                    settingShared.edit().putString(Constans.SCALE_SHAREDKEY,scale.getText().toString().trim()).commit();
                }else{
                    settingShared.edit().putString(Constans.SCALE_SHAREDKEY,"100").commit();
                }
            }
        });



    }
}

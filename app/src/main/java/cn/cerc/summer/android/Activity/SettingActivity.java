package cn.cerc.summer.android.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.huagu.ehealth.R;

import cn.cerc.summer.android.Entity.Config;
import cn.cerc.summer.android.Utils.Constans;
import cn.cerc.summer.android.View.CustomSeekBar;

public class SettingActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener {

    private TextView textview,url_tit;
    private EditText edittext;
    private CustomSeekBar customseekbar;
    private Button button;
    private ImageView back;
    private String InitialScale;
    private int scales=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        back = (ImageView) this.findViewById(R.id.back);
        button = (Button) this.findViewById(R.id.save);
        edittext = (EditText) this.findViewById(R.id.url);
        url_tit = (TextView) this.findViewById(R.id.url_tit);
        customseekbar = (CustomSeekBar) this.findViewById(R.id.customseekbar);
        customseekbar.setOnSeekBarChangeListener(this);
        edittext.setText(settingShared.getString(Constans.HOME, "125"));
        customseekbar.setProgress(settingShared.getInt(Constans.SCALE_SHAREDKEY, 90));

        if (!Config.getConfig().isDebug()){
            url_tit.setVisibility(View.GONE);
            edittext.setVisibility(View.GONE);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(edittext.getText().toString().trim()) && !edittext.getText().toString().trim().contains("http")) {
                    Toast.makeText(SettingActivity.this, R.string.no_http_tips, Toast.LENGTH_SHORT).show();
                } else {
                    settingShared.edit().putString(Constans.HOME, edittext.getText().toString().trim()).commit();
                }
                if (scales==0) {
                    settingShared.edit().putInt(Constans.SCALE_SHAREDKEY, 90).commit();
                } else {
                    settingShared.edit().putInt(Constans.SCALE_SHAREDKEY, scales).commit();
                }
            }
        });
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        scales = (progress + 70);//其中70是设置的最小值
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}

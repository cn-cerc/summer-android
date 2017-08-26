package cn.cerc.summer.android.basis.forms;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mimrc.vine.R;

import cn.cerc.summer.android.basis.core.Constans;
import cn.cerc.summer.android.basis.core.MyApp;
import cn.cerc.summer.android.basis.core.ScreenUtils;
import cn.cerc.summer.android.basis.core.WebConfig;
import cn.cerc.summer.android.basis.view.CustomSeekBar;

public class FrmSettings extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private SharedPreferences settings;
    private TextView url_tit, scale;
    private EditText edittext;
    private CustomSeekBar customseekbar;
    private Button button, recover;
    private ImageView back;
    private int scales = 0;
    private int def_scales = 0;
    private LinearLayout lin_cun;

    private Button[] buttons = new Button[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        settings = getSharedPreferences(Constans.SHARED_SETTING_TAB, MODE_PRIVATE);


        def_scales = ScreenUtils.getScales(this, ScreenUtils.getInches(this));
        back = (ImageView) this.findViewById(R.id.back);
        button = (Button) this.findViewById(R.id.save);
        edittext = (EditText) this.findViewById(R.id.url);
        url_tit = (TextView) this.findViewById(R.id.url_tit);
        scale = (TextView) this.findViewById(R.id.scale);
        recover = (Button) this.findViewById(R.id.recover);
        customseekbar = (CustomSeekBar) this.findViewById(R.id.customseekbar);
        customseekbar.setOnSeekBarChangeListener(this);
        if (null == getIntent().getStringExtra("address"))
            edittext.setText(settings.getString(Constans.HOME, ""));
        else edittext.setText(getIntent().getStringExtra("address"));
        scales = settings.getInt(Constans.SCALE_SHAREDKEY, def_scales);
        customseekbar.setProgress(scales);

        lin_cun = (LinearLayout) this.findViewById(R.id.lin_cun);

        if (!WebConfig.getInstance().isDebug()) {
            url_tit.setVisibility(View.GONE);
            edittext.setVisibility(View.GONE);
//            lin_cun.setVisibility(View.GONE);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("scale", scales);
                intent.putExtra("home", edittext.getText().toString().trim());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(edittext.getText().toString().trim()) && !edittext.getText().toString().trim().contains("http"))
                    Toast.makeText(FrmSettings.this, R.string.no_http_tips, Toast.LENGTH_SHORT).show();
                else
                    settings.edit().putString(Constans.HOME, edittext.getText().toString().trim()).commit();
                if (scales == 0)
                    settings.edit().putInt(Constans.SCALE_SHAREDKEY, scales).commit();
                else settings.edit().putInt(Constans.SCALE_SHAREDKEY, scales).commit();
                FrmMain.getInstance().reload(scales);
                Toast.makeText(v.getContext(), "保存成功", Toast.LENGTH_SHORT).show();
            }
        });

        recover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settings.edit().putString(Constans.HOME, MyApp.HOME_URL).putInt(Constans.SCALE_SHAREDKEY, def_scales).commit();
                customseekbar.setProgress(def_scales);
                FrmMain.getInstance().reload(def_scales);
                Toast.makeText(v.getContext(), "已恢复默认", Toast.LENGTH_SHORT).show();
            }
        });

        buttons[0] = (Button) this.findViewById(R.id.button1);
        buttons[1] = (Button) this.findViewById(R.id.button2);
        buttons[2] = (Button) this.findViewById(R.id.button3);
        buttons[3] = (Button) this.findViewById(R.id.button4);
        buttons[4] = (Button) this.findViewById(R.id.button5);

        for (Button button : buttons)
            button.setOnClickListener(this);

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        scales = (progress + 80);//其中70是设置的最小值
        String str = String.format("界面缩放比例（80%% -- 120%%）当前值：%d%%", scales);
        scale.setText(str);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                scales = ScreenUtils.getScales(this, 4);
                break;
            case R.id.button2:
                scales = ScreenUtils.getScales(this, 4.5);
                break;
            case R.id.button3:
                scales = ScreenUtils.getScales(this, 5);
                break;
            case R.id.button4:
                scales = ScreenUtils.getScales(this, 5.5);
                break;
            case R.id.button5:
                scales = ScreenUtils.getScales(this, 6);
                break;
        }
        customseekbar.setProgress(scales);
        String str = String.format("界面缩放比例（80%% -- 120%%）当前值：%d%%", scales);
        scale.setText(str);
    }

    public static void startFormForResult(AppCompatActivity content, int requestNo, String address) {
        Intent intent = new Intent(content, FrmSettings.class);
        intent.putExtra("address", address);
        content.startActivityForResult(intent, requestNo);
    }
}

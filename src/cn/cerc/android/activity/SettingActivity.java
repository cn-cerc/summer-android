package cn.cerc.android.activity;

import cn.cerc.android.R;
import cn.cerc.android.net.LocalConfig;
import cn.cerc.android.view.MyWebView;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class SettingActivity extends Activity implements OnClickListener {
	private EditText mEditText;
	private View defalutView;
	private EditText mScaleEt;
	private SeekBar mScaleSb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		findViewById(R.id.setting_set_homeurl_btn).setOnClickListener(this);
		defalutView = findViewById(R.id.setting_set_default_btn);
		mScaleEt = (EditText) findViewById(R.id.setting_scale_tv);
		mScaleSb = (SeekBar) findViewById(R.id.setting_scale_sb);
		defalutView.setOnClickListener(this);
		findViewById(R.id.top_left_iv).setOnClickListener(this);
		findViewById(R.id.setting_set_scale_btn).setOnClickListener(this);
		mEditText = (EditText) findViewById(R.id.setting_edit);
		mEditText.setText(LocalConfig.getHomeUrl(this));

		mScaleSb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				int scale = (int) (100 + 200 * ((double) progress / (double) 100));
				mScaleEt.setText("" + scale);
			}
		});

		mScaleEt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (TextUtils.isEmpty(s)) {
					return;
				}
				Integer scale =  Integer.parseInt(s+"");
				if (scale >= 100 && scale <= 300) {
					int progress = (int) ((double) (scale - 100) / 2.0);
					mScaleSb.setProgress(progress);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		int scale = LocalConfig.getScale(this);
		mScaleEt.setText(scale+"");
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setting_set_homeurl_btn:
			String url = mEditText.getText().toString();
			LocalConfig.setHomeUrl(this, url);
			mEditText.setText(LocalConfig.getHomeUrl(this));
			Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show();
			break;
		case R.id.setting_set_default_btn:
			LocalConfig.setHomeUrl(this, MyWebView.BASE_URL);
			mEditText.setText(LocalConfig.getHomeUrl(this));
			Toast.makeText(this, "已恢复默认值", Toast.LENGTH_SHORT).show();
			break;
		case R.id.top_left_iv:
			finish();
			break;
		case R.id.setting_set_scale_btn:
			if (TextUtils.isEmpty(mScaleEt.getText().toString())) {
				Toast.makeText(this, "请设置缩放比例", Toast.LENGTH_SHORT).show();
				return;
			}
			Integer scale = Integer.parseInt(mScaleEt.getText().toString());
			if (scale < 100) {
				Toast.makeText(this, "缩放比例不能小于100%",Toast.LENGTH_SHORT).show();
				return;
			}
			if (scale > 300) {
				Toast.makeText(this, "缩放比例不能大于300%", Toast.LENGTH_SHORT).show();
				return;
			}
			LocalConfig.setScale(this, scale);
			Toast.makeText(this, "缩放比例设置成功", Toast.LENGTH_SHORT).show();
			break;
		}
	}

}

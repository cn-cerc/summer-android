package cn.cerc.summer.android.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.huagu.ehealth.R;

import cn.cerc.summer.android.Entity.Config;
import cn.cerc.summer.android.Interface.ConfigFileLoafCallback;
import cn.cerc.summer.android.Interface.RequestCallback;
import cn.cerc.summer.android.MyApplication;

import cn.cerc.summer.android.Utils.Constans;
import cn.cerc.summer.android.Utils.PermissionUtils;
import cn.cerc.summer.android.Utils.ScreenUtils;
import cn.cerc.summer.android.Utils.XHttpRequest;

import org.json.JSONObject;
import org.xutils.x;

public class StartActivity extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback, RequestCallback, ConfigFileLoafCallback {

    private ImageView imageview;
    private ImageView load_gif;
    private static StartActivity ga;

    public static StartActivity getInstance() {
        return ga;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        ga = this;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_guidance);
        if (PermissionUtils.getPermission(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PermissionUtils.REQUEST_READ_PHONE_STATE, this)) {
            XHttpRequest.getInstance().GET(PermissionUtils.buildDeviceUrl(Constans.HOME_URL + "/MobileConfig"), this);
        }

        initView();
    }

    private void initView() {
        imageview = (ImageView) this.findViewById(R.id.imageview);
        load_gif = (ImageView) this.findViewById(R.id.load_gif);
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rlp.leftMargin = ScreenUtils.getScreenWidth(this) / 2;
        rlp.topMargin = ScreenUtils.getScreenHeight(this) / 2;
        load_gif.setLayoutParams(rlp);
        String image = settingShared.getString(Constans.SHARED_START_URL, "");
        if (TextUtils.isEmpty(image))
            imageview.setImageResource(R.mipmap.start);
        else
            x.image().bind(imageview, image, MyApplication.getInstance().imageOptions);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtils.REQUEST_READ_PHONE_STATE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    PermissionUtils.IMEI = TelephonyMgr.getDeviceId();
                    XHttpRequest.getInstance().GET(PermissionUtils.buildDeviceUrl(Constans.HOME_URL + "/MobileConfig"), this);
                } else {
                    ActivityCompat.requestPermissions(this, permissions, requestCode);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * 跳转
     */
    public void skip() {
        if (settingShared.getBoolean(Constans.IS_FIRST_SHAREDKEY, true)) {
            if (config != null && config.getWelcomeImages() != null && config.getWelcomeImages().size() > 0) {
                startActivity(new Intent(this, GuidanceActivity.class));
            }
        } else {
            if (config != null && config.getAdImages() != null && config.getAdImages().size() > 0) {
                startActivity(new Intent(this, AdActivity.class));
            }
        }
        StartActivity.getInstance().finish();
    }

    /**
     * 线上的配置参数
     */
    public Config config;
    private String homeurl;


    @Override
    public void success(String url, JSONObject json) {
        config = JSON.parseObject(json.toString(), Config.class);
        if (settingShared.getBoolean(Constans.IS_FIRST_SHAREDKEY, true)){
            imageview.setImageResource(R.mipmap.init_bg);
            load_gif.setVisibility(View.VISIBLE);
            load_gif.setBackgroundResource(R.drawable.init_start);
            AnimationDrawable animationDrawable = (AnimationDrawable) load_gif.getBackground();
            animationDrawable.start();
//            x.image().bind(load_gif, "assets://start_init.gif", MyApplication.getInstance().imageOptions);
        }
        homeurl = PermissionUtils.buildDeviceUrl(Constans.HOME_URL);
        String msgurl = config.getRootSite() + "/" + config.getMsgManage();
        settingShared.edit().putString(Constans.HOME, homeurl).putString(Constans.SHARED_MSG_URL, msgurl).putString(Constans.SHARED_START_URL, config.getStartImage()).commit();

        MainActivity.getInstance().Update();


        new Thread(new Runnable() {
            @Override
            public void run() {
                XHttpRequest.getInstance().ConfigFileGet(config.getCacheFiles(), StartActivity.this);
            }
        }).start();
    }

    @Override
    public void Failt(String url, String error) {
        skip();
    }

    @Override
    public void loadfinish() {
        MainActivity.getInstance().webview.loadUrl(homeurl);
        skip();
        settingShared.edit().putBoolean(Constans.IS_FIRST_SHAREDKEY, false).commit();
    }
}

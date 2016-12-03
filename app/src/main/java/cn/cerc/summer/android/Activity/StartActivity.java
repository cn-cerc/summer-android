package cn.cerc.summer.android.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.huagu.ehealth.R;

import cn.cerc.summer.android.Entity.Config;
import cn.cerc.summer.android.Interface.RequestCallback;
import cn.cerc.summer.android.MyApplication;

import cn.cerc.summer.android.Utils.Constans;
import cn.cerc.summer.android.Utils.PermissionUtils;
import cn.cerc.summer.android.Utils.XHttpRequest;

import org.json.JSONObject;
import org.xutils.x;

public class StartActivity extends BaseActivity implements Animation.AnimationListener, ActivityCompat.OnRequestPermissionsResultCallback,RequestCallback {

    private ImageView imageview;
    private static StartActivity ga;
    public static StartActivity getInstance(){
        return ga;
    }

    private boolean is_skip = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        ga = this;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_guidance);

        if (PermissionUtils.getPermission(Manifest.permission.READ_PHONE_STATE, PermissionUtils.REQUEST_READ_PHONE_STATE,this)) {
            XHttpRequest.getInstance().GET(Constans.HOME_URL + "/MobileConfig?device=android&deviceId=" + PermissionUtils.IMEI, this);
        }

        initView();

    }

    private void initView() {
        imageview = (ImageView) this.findViewById(R.id.imageview);
        String image = settingShared.getString(Constans.SHARED_START_URL,"");
        if (TextUtils.isEmpty(image))
            imageview.setImageResource(R.mipmap.startimage);
        else
            x.image().bind(imageview,image, MyApplication.getInstance().imageOptions);

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.startanim);
        imageview.startAnimation(animation);
        animation.setAnimationListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtils.REQUEST_READ_PHONE_STATE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    PermissionUtils.IMEI = TelephonyMgr.getDeviceId();
                    XHttpRequest.getInstance().GET(Constans.HOME_URL + "/MobileConfig?device=android&deviceId=" + PermissionUtils.IMEI, this);
                } else {
                    if (PermissionUtils.getPermission(Manifest.permission.READ_PHONE_STATE, PermissionUtils.REQUEST_READ_PHONE_STATE,this)) {
                        XHttpRequest.getInstance().GET(Constans.HOME_URL + "/MobileConfig?device=android&deviceId=" + PermissionUtils.IMEI, this);
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (is_skip) skip();
        else is_skip = true;
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    /**
     * 跳转
     */
    public void skip(){
        if (settingShared.getBoolean(Constans.IS_FIRST_SHAREDKEY,true)) {
            settingShared.edit().putBoolean(Constans.IS_FIRST_SHAREDKEY, false).commit();
            if (config != null && config.getWelcomeImages() != null && config.getWelcomeImages().size() > 0){
                startActivity(new Intent(this, GuidanceActivity.class));
            }
        }else{
            if (config != null && config.getAdImages() != null && config.getAdImages().size() > 0){
                startActivity(new Intent(this, AdActivity.class));
            }
        }
        StartActivity.getInstance().finish();
    }

    /**
     * 线上的配置参数
     */
    public Config config;

    @Override
    public void success(String url, JSONObject json) {
        config = JSON.parseObject(json.toString(), Config.class);
        String homeurl = config.getRootSite()+"?device=android&deviceid=" + PermissionUtils.IMEI;
        settingShared.edit().putString(Constans.HOME_URL, homeurl).putString(Constans.SHARED_START_URL, config.getStartImage()).commit();
        MainActivity.getInstance().webview.loadUrl(homeurl);
        if (is_skip) skip();
        else is_skip = true;
        MainActivity.getInstance().Update();
    }

    @Override
    public void Failt(String url, String error) {
        if (is_skip) skip();
        else is_skip = true;
    }

}

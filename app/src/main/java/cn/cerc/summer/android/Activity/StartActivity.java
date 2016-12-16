package cn.cerc.summer.android.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.ant.liao.GifView;
import com.mimrc.vine.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import cn.cerc.summer.android.Entity.Config;
import cn.cerc.summer.android.Interface.ConfigFileLoafCallback;
import cn.cerc.summer.android.Interface.RequestCallback;
import cn.cerc.summer.android.MyApplication;

import cn.cerc.summer.android.Utils.AppUtil;
import cn.cerc.summer.android.Utils.Constans;
import cn.cerc.summer.android.Utils.FileUtil;
import cn.cerc.summer.android.Utils.PermissionUtils;
import cn.cerc.summer.android.Utils.ScreenUtils;
import cn.cerc.summer.android.Utils.XHttpRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StartActivity extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback, RequestCallback, ConfigFileLoafCallback {

    private ImageView imageview;
    private GifView load_gif;
    private static StartActivity ga;

    public static StartActivity getInstance() {
        return ga;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
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
        load_gif = (GifView) this.findViewById(R.id.load_gif);
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rlp.leftMargin = ScreenUtils.getScreenWidth(this) / 2;
        rlp.bottomMargin = ScreenUtils.getScreenHeight(this) / 8 * 5;
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rlp.width = ScreenUtils.getScreenWidth(this) / 3;
        rlp.height = ScreenUtils.getScreenHeight(this) / 5;
        load_gif.setLayoutParams(rlp);
        load_gif.setShowDimension(ScreenUtils.getScreenWidth(this) / 3, ScreenUtils.getScreenHeight(this) / 5);
        load_gif.setGifImage(R.mipmap.start_init);
        load_gif.setGifImageType(GifView.GifImageType.WAIT_FINISH);

        String image = settingShared.getString(Constans.SHARED_START_URL, "");
        if (settingShared.getBoolean(Constans.IS_FIRST_SHAREDKEY, true))
            imageview.setVisibility(View.INVISIBLE);
        else
            ImageLoader.getInstance().displayImage(image, imageview, MyApplication.getInstance().options);
//            x.image().bind(imageview, image, MyApplication.getInstance().imageOptions);

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


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void success(String url, JSONObject json) {
        config = JSON.parseObject(json.toString(), Config.class);
        homeurl = PermissionUtils.buildDeviceUrl(Constans.HOME_URL);
        String msgurl = config.getRootSite() + "/" + config.getMsgManage();
        settingShared.edit().putString(Constans.HOME, homeurl).putString(Constans.SHARED_MSG_URL, msgurl).putString(Constans.SHARED_START_URL, config.getStartImage()).commit();

        MainActivity.getInstance().Update();

        load_gif.setVisibility(View.VISIBLE);
        imageview.setVisibility(View.VISIBLE);
        imageview.setImageResource(R.mipmap.init_bg);
        List<String> list = config.getCacheFiles();
        if (list != null && list.size() > 0) {
            XHttpRequest.getInstance().ConfigFileGet(list, StartActivity.this);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadfinish();
                }
            }, 2000);
        }
        prestrainImage();//预加载一张图片， 有缓存的

    }

    public void prestrainImage() {
        ImageLoader.getInstance().loadImage(config.getWelcomeImages().get(0), MyApplication.getInstance().options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
            }
        });
    }

    @Override
    public void Failt(String url, String error) {
        skip();
    }

    @Override
    public void loadfinish() {
        MainActivity.getInstance().setHomeurl(homeurl);
        settingShared.edit().putBoolean(Constans.IS_FIRST_SHAREDKEY, false).commit();
        AppUtil.saveCacheList(config);
        skip();
    }

}

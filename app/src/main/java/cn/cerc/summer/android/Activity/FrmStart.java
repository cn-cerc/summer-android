package cn.cerc.summer.android.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
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

import org.json.JSONObject;

import java.util.List;

import cn.cerc.summer.android.Utils.MyApp;
import cn.cerc.summer.android.Entity.Config;
import cn.cerc.summer.android.Interface.ConfigFileLoadCallback;
import cn.cerc.summer.android.Interface.RequestCallback;
import cn.cerc.summer.android.Utils.Constans;
import cn.cerc.summer.android.Utils.PermissionUtils;
import cn.cerc.summer.android.Utils.ScreenUtils;
import cn.cerc.summer.android.Utils.XHttpRequest;

public class FrmStart extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, RequestCallback, ConfigFileLoadCallback {

    private SharedPreferences settings;
    private static FrmStart ga;
    /**
     * 线上的配置参数
     */
    public Config config;
    private ImageView imageview;
    private GifView load_gif;
    private String homeurl;

    public static FrmStart getInstance() {
        return ga;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FrmMain.getInstance().finish();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        ga = this;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_guidance);

        settings = getSharedPreferences(Constans.SHARED_SETTING_TAB, MODE_PRIVATE);

        if (PermissionUtils.getPermission(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PermissionUtils.REQUEST_READ_PHONE_STATE, this)) {
            XHttpRequest.getInstance().GET(MyApp.buildDeviceUrl(MyApp.HOME_URL + "/MobileConfig"), this);
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

        String image = settings.getString(Constans.SHARED_START_URL, "");
        if (settings.getBoolean(Constans.IS_FIRST_SHAREDKEY, true))
            imageview.setVisibility(View.INVISIBLE);
        else
            ImageLoader.getInstance().displayImage(image, imageview, MyApp.getInstance().getImageOptions());
//            x.image().bind(imageview, image, MyApp.getInstance().imageOptions);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtils.REQUEST_READ_PHONE_STATE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    PermissionUtils.IMEI = TelephonyMgr.getDeviceId();
                    XHttpRequest.getInstance().GET(MyApp.buildDeviceUrl(MyApp.HOME_URL + "/MobileConfig"), this);
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
        if (settings.getBoolean(Constans.IS_FIRST_SHAREDKEY, true)) {
            if (config != null && config.getWelcomeImages() != null && config.getWelcomeImages().size() > 0) {
                startActivity(new Intent(this, FrmWelcome.class));
            }
        } else {
            if (config != null && config.getAdImages() != null && config.getAdImages().size() > 0) {
                startActivity(new Intent(this, FrmAD.class));
            }
        }
        FrmStart.getInstance().finish();
    }

    @Override
    public void success(String url, JSONObject json) {
        config = JSON.parseObject(json.toString(), Config.class);
        homeurl = MyApp.buildDeviceUrl(MyApp.HOME_URL);
        String msgurl = config.getRootSite() + "/" + config.getMsgManage();
        settings.edit().putString(Constans.HOME, homeurl).putString(Constans.SHARED_MSG_URL, msgurl).putString(Constans.SHARED_START_URL, config.getStartImage()).commit();

        FrmMain.getInstance().Update();

        if (settings.getInt(Constans.FAIL_NUM_SHAREDKEY, 1) > 0) {
            load_gif.setVisibility(View.VISIBLE);
            imageview.setVisibility(View.VISIBLE);
            imageview.setImageResource(R.mipmap.init_bg);
        }
        List<String> list = config.getCacheFiles();
        if (list != null && list.size() > 0) {
            XHttpRequest.getInstance().ConfigFileGet(list, FrmStart.this);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadfinish(0);
                }
            }, 2000);
        }
        prestrainImage();//预加载一张图片， 有缓存的

    }

    public void prestrainImage() {
        ImageLoader.getInstance().loadImage(config.getWelcomeImages().get(0), MyApp.getInstance().getImageOptions(), new ImageLoadingListener() {
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
    public Context getContext() {
        return this;
    }

    @Override
    public void failt(String url, String error) {
        FrmMain.getInstance().setHomeurl(MyApp.HOME_URL);
        skip();
    }

    @Override
    public void loadfinish(final int fail_num) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FrmMain.getInstance().setHomeurl(homeurl);
                settings.edit().putBoolean(Constans.IS_FIRST_SHAREDKEY, false).putInt(Constans.FAIL_NUM_SHAREDKEY, fail_num).commit();
                skip();
            }
        });
    }

    @Override
    public void loadAllfinish() {
        MyApp.saveCacheList(config);
    }

}

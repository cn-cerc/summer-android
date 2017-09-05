package cn.cerc.summer.android.basis.forms;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import cn.cerc.summer.android.basis.core.ConfigFileLoadCallback;
import cn.cerc.summer.android.basis.core.Constans;
import cn.cerc.summer.android.basis.core.MyApp;
import cn.cerc.summer.android.basis.core.RequestCallback;
import cn.cerc.summer.android.basis.core.ScreenUtils;
import cn.cerc.summer.android.basis.core.WebConfig;
import cn.cerc.summer.android.basis.core.XHttpRequest;

public class FrmStart extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, RequestCallback, ConfigFileLoadCallback {
    private static final int REQUEST_READ_PHONE_STATE = 123;
    private SharedPreferences settings;
    private static FrmStart instance;
    private WebConfig webConfig; //线上的配置参数
    private ImageView imageview;
    private GifView load_gif;
    private String homeurl;

    public static FrmStart getInstance() {
        return instance;
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
        instance = this;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_guidance);

        settings = getSharedPreferences(Constans.SHARED_SETTING_TAB, MODE_PRIVATE);

        if (getPermission(this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_READ_PHONE_STATE)) {
            XHttpRequest.getInstance().GET(MyApp.buildDeviceUrl(MyApp.HOME_URL + "/MobileConfig"), this);
        }

        initView();
    }

    /**
     * 检查是否已获取权限
     *
     * @param permissions 获取的权限名字数组
     * @param requestcode 请求权限的请求码
     * @return 返回是否已获取了这个权限
     */
    private boolean getPermission(Activity activity, String[] permissions, int requestcode) {
        boolean is_req = false; //是否需要请求权限
        for (String permission : permissions) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(activity, permission);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) is_req = true;
        }
        if (is_req) {
            ActivityCompat.requestPermissions(activity, permissions, requestcode);
            return false;
        } else {
            TelephonyManager TelephonyMgr = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
            MyApp.IMEI = "n_" + TelephonyMgr.getDeviceId();
            return true;
        }
    }

    private void initView() {
        imageview = (ImageView) this.findViewById(R.id.imageView);
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
            case REQUEST_READ_PHONE_STATE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    MyApp.IMEI = TelephonyMgr.getDeviceId();
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
            if (webConfig != null && webConfig.getWelcomeImages() != null && webConfig.getWelcomeImages().size() > 0) {
                startActivity(new Intent(this, FrmWelcome.class));
            }
        } else {
            if (webConfig != null && webConfig.getAdImages() != null && webConfig.getAdImages().size() > 0) {
                startActivity(new Intent(this, FrmAD.class));
            }
        }
        FrmStart.getInstance().finish();
    }

    @Override
    public void success(String url, JSONObject json) {
        webConfig = JSON.parseObject(json.toString(), WebConfig.class);

        homeurl = MyApp.buildDeviceUrl(MyApp.HOME_URL);
        String msgurl = webConfig.getRootSite() + "/" + webConfig.getMsgManage();
        settings.edit().putString(Constans.HOME, homeurl).putString(Constans.SHARED_MSG_URL, msgurl).putString(Constans.SHARED_START_URL, webConfig.getStartImage()).commit();

        FrmMain.getInstance().checkUpdate();

        if (settings.getInt(Constans.FAIL_NUM_SHAREDKEY, 1) > 0) {
            load_gif.setVisibility(View.VISIBLE);
            imageview.setVisibility(View.VISIBLE);
            imageview.setImageResource(R.mipmap.init_bg);
        }
        List<String> list = webConfig.getCacheFiles();
        if (list != null && list.size() > 0) {
            XHttpRequest.getInstance().ConfigFileGet(list, FrmStart.this);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadFinish(0);
                }
            }, 2000);
        }
        prestrainImage();//预加载一张图片， 有缓存的

    }

    public void prestrainImage() {
        ImageLoader.getInstance().loadImage(webConfig.getWelcomeImages().get(0), MyApp.getInstance().getImageOptions(), new ImageLoadingListener() {
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
        FrmMain.getInstance().setHomeUrl(MyApp.HOME_URL);
        skip();
    }

    @Override
    public void loadFinish(final int fail_num) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FrmMain.getInstance().setHomeUrl(homeurl);
                settings.edit().putBoolean(Constans.IS_FIRST_SHAREDKEY, false).putInt(Constans.FAIL_NUM_SHAREDKEY, fail_num).commit();
                skip();
            }
        });
    }

    @Override
    public void loadAllFinish() {
        MyApp.saveCacheList(webConfig);
    }

}

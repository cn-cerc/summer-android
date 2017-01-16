package cn.cerc.summer.android.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.UiThread;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.ant.liao.GifView;
import com.huagu.ehealth.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import cn.cerc.summer.android.Entity.Config;
import cn.cerc.summer.android.Interface.ConfigFileLoafCallback;
import cn.cerc.summer.android.Interface.RequestCallback;
import cn.cerc.summer.android.MyApplication;

import cn.cerc.summer.android.MyConfig;
import cn.cerc.summer.android.Utils.AppUtil;
import cn.cerc.summer.android.Utils.Constans;
import cn.cerc.summer.android.Utils.FileUtil;
import cn.cerc.summer.android.Utils.PermissionUtils;
import cn.cerc.summer.android.Utils.ScreenUtils;
import cn.cerc.summer.android.Utils.XHttpRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 启动界面
 */
public class StartActivity extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback, RequestCallback, ConfigFileLoafCallback {

    private ImageView imageview;

    private static StartActivity ga;

    public static StartActivity getInstance() {
        return ga;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.getInstance().finish();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        super.onCreate(savedInstanceState);
        ga = this;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//撑满全屏
        setContentView(R.layout.activity_guidance);
        //判断手机的sd卡的读写权限
        if (PermissionUtils.getPermission(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PermissionUtils.REQUEST_READ_PHONE_STATE, this)) {
            TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            //获取手机设备号
            PermissionUtils.IMEI = TelephonyMgr.getDeviceId();
            Log.e("IMEI:", "IMEI: " + PermissionUtils.IMEI);
            //下载获取配置文件（接口：RequestCallback   方法：1.success   2.Failt）
            XHttpRequest.getInstance().GET(AppUtil.buildDeviceUrl(MyConfig.HOME_URL + "/MobileConfig"), this);
        }
        initView();
    }

    //初始化
    private void initView() {
        imageview = (ImageView) this.findViewById(R.id.imageview);

        String image = settingShared.getString(Constans.SHARED_START_URL, "");
        if (settingShared.getBoolean(Constans.IS_FIRST_SHAREDKEY, true)) imageview.setVisibility(View.INVISIBLE);
        else ImageLoader.getInstance().displayImage(image, imageview, MyApplication.getInstance().options);
//            x.image().bind(imageview, image, MyApplication.getInstance().imageOptions);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtils.REQUEST_READ_PHONE_STATE://23以上获取手机权限的操作
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //获取手机设备号
                    TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    PermissionUtils.IMEI = TelephonyMgr.getDeviceId();
                    //下载获取配置文件（接口：RequestCallback   方法：1.success   2.Failt）
                    XHttpRequest.getInstance().GET(AppUtil.buildDeviceUrl(MyConfig.HOME_URL + "/MobileConfig"), this);
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
        if (settingShared.getBoolean(Constans.IS_FIRST_SHAREDKEY, true)) {//第一次安装进入引导界面
            if (config != null && config.getWelcomeImages() != null && config.getWelcomeImages().size() > 0) {
                startActivity(new Intent(this, GuidanceActivity.class));
            }
        } else {//下次进入直接跳入到主界面
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
        Log.i("-------config------",json.toString());
        config = JSON.parseObject(json.toString(), Config.class);
        homeurl = AppUtil.buildDeviceUrl(config.getRootSite());
        String msgurl = config.getRootSite() + "/" + config.getMsgManage();
        settingShared.edit().putString(Constans.HOME, homeurl)
                .putString(Constans.SHARED_MSG_URL, msgurl)
                .putString(Constans.SHARED_START_URL, config.getStartImage())
                .putString(Constans.OCR_PATH, config.getOcrDataPath())
                .commit();
        //XXX 这里要方法success方法里面
        XHttpRequest.getInstance().getTess(MyConfig.HOME_URL + config.getOcrDataPath());//下载语言文件


        MainActivity.getInstance().Update();//提示更新处理

        imageview.setVisibility(View.VISIBLE);
        imageview.setImageResource(R.mipmap.init_bg);
        //下载js、css相关文件
        List<String> list = config.getCacheFiles();
        if (list != null && list.size() > 0) {
            //下载js、css相关文件（接口：ConfigFileLoafCallback 方法：1.loadfinish 2.loadAllfinish）
            XHttpRequest.getInstance().ConfigFileGet(list, StartActivity.this);
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
        if (config.getWelcomeImages() != null && config.getWelcomeImages().size()>0)
            ImageLoader.getInstance().loadImage(config.getWelcomeImages().get(0), MyApplication.getInstance().options, null);
    }

    @Override
    public Context getContext(){
        return this;
    }

    @Override
    public void Failt(String url, String error) {
        MainActivity.getInstance().setHomeurl(MyConfig.HOME_URL);
        skip();
    }

    @Override
    public void loadfinish(final int fail_num) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity.getInstance().setHomeurl(homeurl);
                skip();
                settingShared.edit().putBoolean(Constans.IS_FIRST_SHAREDKEY, false).putInt(Constans.FAIL_NUM_SHAREDKEY,fail_num).commit();
            }
        });
    }

    @Override
    public void loadAllfinish(){
        AppUtil.saveCacheList(config);
    }

}

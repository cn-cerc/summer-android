package cn.cerc.summer.android;

import android.app.Application;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.mimrc.vine.R;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import cn.jpush.android.api.JPushInterface;


/**
 * Created by huagu on 2016/11/2.
 */

public class MyApplication extends Application {

    public ImageOptions imageOptions;

    private static MyApplication instance;

    public static MyApplication getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        x.Ext.init(this);//xutils 初始化
        x.Ext.setDebug(true);//设置为debug

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f,1.0f);
        alphaAnimation.setDuration((long)1000);
        alphaAnimation.setInterpolator(new DecelerateInterpolator());

        imageOptions = new ImageOptions.Builder()
                .setLoadingDrawableId(R.mipmap.ic_launcher)
                .setFailureDrawableId(R.mipmap.ic_launcher)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setUseMemCache(true)
                .setAnimation(alphaAnimation)
                .setIgnoreGif(false)
                .setAutoRotate(true).build();

        JPushInterface.init(this);
        JPushInterface.setDebugMode(true);



    }



}

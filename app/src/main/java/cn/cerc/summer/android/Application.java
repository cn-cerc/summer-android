package cn.cerc.summer.android;

import android.graphics.Bitmap;

import com.mimrc.vine.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.xutils.x;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by huagu on 2016/11/2.
 */

public class Application extends android.app.Application {
    public static String HOME_URL = "https://m.knowall.cn";

    private static Application instance;

    private DisplayImageOptions options;

    public static Application getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        x.Ext.init(this);//xutils 初始化
        x.Ext.setDebug(true);//设置为debug

        initImageLoader();

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.start) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.error) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.error) // 设置图片加载或解码过程中发生错误显示的图片
                .resetViewBeforeLoading(false)  // default 设置图片在加载前是否重置、复位
                .cacheInMemory(true) // default  设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // default  设置下载的图片是否缓存在SD卡中
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default 设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565) // default 设置图片的解码类型
                .displayer(new FadeInBitmapDisplayer(500))
                .build();

        JPushInterface.init(this);
        JPushInterface.setDebugMode(true);
    }

    /**
     * ImageLoader 初始化
     */
    private void initImageLoader() {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(this);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.discCacheFileCount(100);//缓存的文件数量
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(200 * 1024 * 1024); // 100 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        // config.writeDebugLogs(); // Remove for release app
        ImageLoader.getInstance().init(config.build());

    }

    public DisplayImageOptions getImageOptions() {
        return options;
    }
}

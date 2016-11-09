package com.fmk.huagu.efitness;

import android.app.Application;
import android.os.Environment;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import org.xutils.DbManager;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.File;


/**
 * Created by huagu on 2016/11/2.
 */

public class MyApplication extends Application {

    public ImageOptions imageOptions;

    private static MyApplication instance;
    public static MyApplication getInstance(){
        return instance;
    }

    public DbManager.DaoConfig daoconfig;


    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        x.Ext.init(this);//xutils 初始化
        x.Ext.setDebug(true);//设置为debug

        daoconfig = new DbManager.DaoConfig()
                .setDbName("UI.db")
                // 不设置dbDir时, 默认存储在app的私有目录.
                .setDbDir(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/EFitness/"))
                .setDbVersion(1)
                .setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {
                        // 开启WAL, 对写入加速提升巨大
                        db.getDatabase().enableWriteAheadLogging();
                    }
                })
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                        // TODO: ...
                        // db.addColumn(...);
                        // db.dropTable(...);
                        // ...
                        // or
                        // db.dropDb();
                    }
                });

        ScaleAnimation magnifImage = new ScaleAnimation(0.8f, 1.0f, 0.8f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        magnifImage.setDuration((long)1000);
        magnifImage.setInterpolator(new DecelerateInterpolator());

        imageOptions = new ImageOptions.Builder()
                .setLoadingDrawableId(R.mipmap.ic_launcher)
                .setFailureDrawableId(R.mipmap.ic_launcher)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setUseMemCache(true)
                .setAnimation(magnifImage)
                .setIgnoreGif(true)
                .setAutoRotate(true).build();

    }

}

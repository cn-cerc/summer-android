package cn.cerc.summer.android.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import java.io.File;

import cn.cerc.summer.android.Utils.Constans;


/**
 * Created by huagu on 2016/11/2.
 */

public abstract class BaseActivity extends AppCompatActivity {


    //保存销量数据的sp工具
    protected SharedPreferences settingShared;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settingShared = getSharedPreferences(Constans.SHARED_SETTING_TAB, MODE_PRIVATE);

    }

    /**
     * 清除缓存
     */
    public int clearCacheFolder(File dir, long numDays) {
        int deletedFiles = 0;
        if (dir != null && dir.isDirectory()) {
            try {
                for (File child : dir.listFiles()) {
                    if (child.isDirectory())
                        deletedFiles += clearCacheFolder(child, numDays);
                    if (child.lastModified() < numDays)
                        if (child.delete()) deletedFiles++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return deletedFiles;
    }


}

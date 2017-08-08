package cn.cerc.summer.android.forms;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import cn.cerc.summer.android.Utils.Constans;

/**
 * Created by huagu on 2016/11/2.
 */

public abstract class BaseForm extends AppCompatActivity {

    protected SharedPreferences settingShared;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settingShared = getSharedPreferences(Constans.SHARED_SETTING_TAB, MODE_PRIVATE);
    }

}

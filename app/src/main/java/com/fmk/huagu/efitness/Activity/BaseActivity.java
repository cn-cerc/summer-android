package com.fmk.huagu.efitness.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;

import com.fmk.huagu.efitness.Utils.Constans;


/**
 * Created by huagu on 2016/11/2.
 */

public abstract class BaseActivity extends AppCompatActivity {


    protected SharedPreferences settingShared;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        settingShared = getSharedPreferences(Constans.SHARED_SETTING_TAB, MODE_PRIVATE);

    }



}

package com.fmk.huagu.efitness.Activity;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import com.fmk.huagu.efitness.Utils.Constans;


/**
 * Created by huagu on 2016/11/2.
 */

public class BaseActivity extends AppCompatActivity {

    public static final int REQUEST_READ_PHONE_STATE = 123;

    protected SharedPreferences settingShared;

    protected String IMEI;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settingShared = getSharedPreferences(Constans.SHARED_SETTING_TAB,MODE_PRIVATE);

        if (getPermission(Manifest.permission.READ_PHONE_STATE,REQUEST_READ_PHONE_STATE)){
            TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
            IMEI = TelephonyMgr.getDeviceId();
        }
    }

    public boolean getPermission(String permission, int requestcode){
        int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, permission);
        if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{permission}, requestcode);
            return false;
        }else{
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
                    IMEI = TelephonyMgr.getDeviceId();
                }else{
                    getPermission(Manifest.permission.READ_PHONE_STATE,REQUEST_READ_PHONE_STATE);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}

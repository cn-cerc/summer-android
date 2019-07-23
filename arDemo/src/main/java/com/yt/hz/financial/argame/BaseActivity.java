package com.yt.hz.financial.argame;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;

/**
 * Created by admin on 2018/9/17.
 */

public class BaseActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    public void showToast(String msg){
        /*if (false)
        Toast.makeText(BaseActivity.this,msg,Toast.LENGTH_SHORT).show();*/
    }
    public void showToastNormal(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(BaseActivity.this,msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void log(String msg){
        Log.e("yuan",msg);
    }
}

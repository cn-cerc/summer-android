package com.fmk.fff.efitness.Activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fmk.fff.efitness.R;

/**
 * Created by fff on 2016/11/2.
 */

public class BaseActivity extends AppCompatActivity {

    public ImageButton title_back;
    public TextView title_right,title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);


    }

    /**
     * 初始化通用的title
     * @param backres   左边的ImageButton需显示的图片， 0即不显示，
     * @param titles    中间title(TextView)显示的文字， 传不传都显示，默认appname
     * @param right     右边菜单(TextView)显示的文字，为空即不显示
     */
    public void initTitle(int backres,String titles,String right){
        title_back = (ImageButton) this.findViewById(R.id.title_back);
        title_right = (TextView) this.findViewById(R.id.title_right);
        title = (TextView) this.findViewById(R.id.title);
        if (backres!=0){
            title_back.setVisibility(View.VISIBLE);
            title_back.setImageResource(backres);
        }
        if (!TextUtils.isEmpty(titles)){
            title.setText(titles);
        }
        if (!TextUtils.isEmpty(right)){
            title_right.setVisibility(View.VISIBLE);
            title_right.setText(right);
        }
    }


}

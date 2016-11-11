package com.fmk.huagu.efitness.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.fmk.huagu.efitness.R;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StartActivity extends BaseActivity implements View.OnClickListener {

    private ViewPager viewpager;
    private boolean is_skip;//是否跳转
    private Animation animation;//渐变动画

    private List<ImageView> imageview;

    private int[] image = new int[]{R.mipmap.startimage4};//R.mipmap.startimage1, R.mipmap.startimage2, R.mipmap.startimage3,

    @Override
    protected void onCreate(Bundle savedInstanceState) {//?device=android&clientId=44444444
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start);

        viewpager = (ViewPager) this.findViewById(R.id.viewpager);

        imageview = new ArrayList<ImageView>();
        for (int i = 0; i < image.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(image[i]);
            imageview.add(imageView);
            if (i == (image.length-1)) {
                imageView.setOnClickListener(this);
            }
        }


        viewpager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return imageview.size();
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(imageview.get(position));
                return imageview.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(imageview.get(position));
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}

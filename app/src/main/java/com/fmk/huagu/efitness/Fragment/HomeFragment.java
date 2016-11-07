package com.fmk.huagu.efitness.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.fmk.huagu.efitness.Adapter.HomeCarouselPagerAdapter;
import com.fmk.huagu.efitness.Adapter.HomeInformationAdapter;
import com.fmk.huagu.efitness.R;
import com.fmk.huagu.efitness.View.FixedListView;

/**
 * Created by huagu on 2016/11/4.
 */

public class HomeFragment extends Fragment implements ViewPager.OnPageChangeListener {

    public HomeFragment() {
        super();
    }

    private MyRunnable myRunnable;
    private View rootview;
    private static HomeFragment instance;
    private FixedListView information;//下方资讯
    private ViewPager carousel;//轮播
    private LinearLayout dot_content;

    //单例 提供实例
    public static HomeFragment getInstance() {
//        if (instance!=null) return instance;
        return instance = new HomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home, container, false);
        initView();
        return rootview;
    }

    private void initView() {
        information = (FixedListView) rootview.findViewById(R.id.information);
        carousel = (ViewPager) rootview.findViewById(R.id.carousel);
        carousel.setAdapter(new HomeCarouselPagerAdapter(getActivity()));
        carousel.addOnPageChangeListener(this);
        information.setAdapter(new HomeInformationAdapter(getActivity()));

        dot_content = (LinearLayout) rootview.findViewById(R.id.dot_content);//下方的点
        if (myRunnable == null) {
            myRunnable = new MyRunnable();
            mHandler.postDelayed(myRunnable, 2500);
            dotset(0);
        }
    }

    /**
     * 初始化轮播下面的点，设置点的位置
     * @param index     点的位置
     */
    private void dotset(int index) {
        dot_content.removeAllViews();
        for (int i = 0; i < 4; i++) {
            View view = new View(getActivity());
            view.setBackgroundResource(R.color.background);
            if (i == index) {
                view.setBackgroundResource(R.color.white);
            }
            dot_content.addView(view, i);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(20, 20);
            lp.leftMargin = 30;
            lp.rightMargin = 30;
            view.setLayoutParams(lp);
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        this.position = position;
        mHandler.removeCallbacks(myRunnable);
        mHandler.postDelayed(myRunnable, 2500);
        dotset(position % 4);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private Handler mHandler = new Handler();

    private int position=0;

    /**
     * 用于定时滚动广告栏广告
     */
    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            carousel.setCurrentItem(position+1);
        }
    }

}

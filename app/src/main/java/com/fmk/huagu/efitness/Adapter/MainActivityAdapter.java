package com.fmk.huagu.efitness.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.fmk.huagu.efitness.Fragment.HomeFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huagu on 2016/11/4.
 */

public class MainActivityAdapter extends FragmentPagerAdapter {

    List<Fragment> fragment;

    public MainActivityAdapter(FragmentManager fm) {
        super(fm);
        fragment = new ArrayList<Fragment>();
        fragment.add(HomeFragment.getInstance());
        fragment.add(HomeFragment.getInstance());
        fragment.add(HomeFragment.getInstance());
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        return fragment.get(position);
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return fragment.size();
    }

}

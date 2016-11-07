package com.fmk.fff.efitness.Adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fmk.fff.efitness.MyApplication;
import com.fmk.fff.efitness.R;

import org.xutils.common.Callback;
import org.xutils.x;

import java.io.File;

/**
 * Created by fff on 2016/11/4.
 */

public class HomeCarouselPagerAdapter extends PagerAdapter {

    private int[] image ;
    private Context mContext;
    public HomeCarouselPagerAdapter(Context mContext) {
        super();
        this.mContext = mContext;
        image = new int[]{R.mipmap.ic_launcher,R.mipmap.ic_launcher,R.mipmap.ic_launcher,R.mipmap.ic_launcher};
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageview = new ImageView(mContext);
        imageview.setImageResource(image[position % image.length]);
        x.image().bind(imageview, new File("/storage/emulated/0/DCIM/Screenshots/Screenshot_20161031-205359.png").toURI().toString(), MyApplication.getInstance().imageOptions);
        container.addView(imageview);
        return imageview;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

}

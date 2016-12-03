package cn.cerc.summer.android.Activity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huagu.ehealth.R;

import cn.cerc.summer.android.Entity.Config;
import cn.cerc.summer.android.MyApplication;


import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class GuidanceActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private ViewPager viewpager;
    private LinearLayout contan;
    private TextView skip;
    private boolean is_skip;//是否跳转
    private Animation animation;//渐变动画

    private List<ImageView> imageview;

    private List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {//?device=android&clientId=44444444
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start);

        viewpager = (ViewPager) this.findViewById(R.id.viewpager);
        contan = (LinearLayout) this.findViewById(R.id.contan);
        skip = (TextView) this.findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        list = Config.getConfig().getWelcomeImages();

        imageview = new ArrayList<ImageView>();
        for (int i = 0; i < list.size(); i++) {
            ImageView imageView = new ImageView(this);
            x.image().bind(imageView, list.get(i), MyApplication.getInstance().imageOptions);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageview.add(imageView);
            if (i == (list.size() - 1)) {
                imageView.setOnClickListener(this);
            }
            View view = new View(this);
            view.setBackgroundResource(R.drawable.point_white);
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llp.width = 15;
            llp.height = 15;
            llp.leftMargin = 20;
            llp.rightMargin = 20;
            contan.addView(view, llp);
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

        viewpager.addOnPageChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < contan.getChildCount(); i++) {
            if (position == i) {
                contan.getChildAt(i).setBackgroundResource(R.drawable.point_white);
            } else {
                contan.getChildAt(i).setBackgroundResource(R.drawable.point_color);
            }
        }
        if (position == (imageview.size() - 1)) skip.setVisibility(View.VISIBLE);
        else skip.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}

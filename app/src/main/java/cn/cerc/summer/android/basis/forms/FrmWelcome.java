package cn.cerc.summer.android.basis.forms;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mimrc.vine.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import cn.cerc.summer.android.basis.core.MyApp;
import cn.cerc.summer.android.basis.core.WebConfig;

public class FrmWelcome extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private ViewPager viewPager;
    private LinearLayout boxContan;
    private TextView lblSkip;
    private boolean is_skip;//是否跳转
    private Animation animation;//渐变动画
    private List<ImageView> imageViewList;
    private List<String> imageUrlList;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FrmMain.getInstance().finish();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {//?device=android&clientId=44444444
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start);

        viewPager = (ViewPager) this.findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2);
        boxContan = (LinearLayout) this.findViewById(R.id.contan);
        lblSkip = (TextView) this.findViewById(R.id.skip);
        lblSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imageUrlList = WebConfig.getInstance().getWelcomeImages();

        imageViewList = new ArrayList<ImageView>();
        for (int i = 0; i < imageUrlList.size(); i++) {
            ImageView imageView = new ImageView(this);
            ImageLoader.getInstance().displayImage(imageUrlList.get(i), imageView, MyApp.getInstance().getImageOptions());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageViewList.add(imageView);
            if (i == (imageUrlList.size() - 1)) {
                imageView.setOnClickListener(this);
            }
            View view = new View(this);
            view.setBackgroundResource(R.drawable.point_white);
            if (i == 0) view.setBackgroundResource(R.drawable.point_color);
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llp.width = 15;
            llp.height = 15;
            llp.leftMargin = 20;
            llp.rightMargin = 20;
            boxContan.addView(view, llp);
        }

        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return imageViewList.size();
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(imageViewList.get(position));
                return imageViewList.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(imageViewList.get(position));
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });

        viewPager.addOnPageChangeListener(this);
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
        for (int i = 0; i < boxContan.getChildCount(); i++) {
            if (position == i)
                boxContan.getChildAt(i).setBackgroundResource(R.drawable.point_white);
            else boxContan.getChildAt(i).setBackgroundResource(R.drawable.point_color);
        }
        if (position == (imageViewList.size() - 1)) lblSkip.setVisibility(View.VISIBLE);
        else lblSkip.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}

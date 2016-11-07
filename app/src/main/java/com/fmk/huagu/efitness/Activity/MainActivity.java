package com.fmk.huagu.efitness.Activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.fmk.huagu.efitness.Adapter.MainActivityAdapter;
import com.fmk.huagu.efitness.R;

/**
 * 主界面
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {

    private ViewPager main_viewpager;
    private TextView[] tab = new TextView[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitView();
    }

    private void InitView() {
        super.initTitle(0, getResources().getString(R.string.app_name), "");//调用父类方法初始化通用title

        main_viewpager = (ViewPager) this.findViewById(R.id.main_viewpager);
//        main_viewpager.setAdapter();

        tab[0] = (TextView) this.findViewById(R.id.tab1);
        tab[1] = (TextView) this.findViewById(R.id.tab2);
        tab[2] = (TextView) this.findViewById(R.id.tab3);

        for (TextView t : tab){
            t.setOnClickListener(this);
        }
        main_viewpager.setAdapter(new MainActivityAdapter(getSupportFragmentManager()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tab1:
                main_viewpager.setCurrentItem(0);
                break;
            case R.id.tab2:
                main_viewpager.setCurrentItem(1);
                break;
            case R.id.tab3:
                main_viewpager.setCurrentItem(2);
                break;
        }
    }
}

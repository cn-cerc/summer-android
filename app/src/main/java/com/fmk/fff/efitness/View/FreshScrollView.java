package com.fmk.fff.efitness.View;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.fmk.fff.efitness.Interface.Pullable;


/**
 * Created by fff on 2016/11/1.
 */

public class FreshScrollView extends ScrollView implements Pullable {
    public FreshScrollView(Context context) {
        super(context);
    }

    public FreshScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FreshScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FreshScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean canPullDown() {
        if (getScrollY() == 0)
            return true;
        else
            return false;
    }

    @Override
    public boolean canPullUp() {
        if (getScrollY() >= (getChildAt(0).getHeight() - getMeasuredHeight()))
            return true;
        else
            return false;
    }
}

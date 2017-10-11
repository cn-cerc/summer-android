package cn.cerc.summer.android.basis.core;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.mimrc.vine.R;

/**
 * Created by Administrator on 2017/10/9.
 */

public abstract class BasePopupWindow extends PopupWindow {
    protected View popRootView;

    public BasePopupWindow() {
        super();
    }

    public BasePopupWindow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public BasePopupWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BasePopupWindow(Context context) {
        super(context);
    }

    public BasePopupWindow(int width, int height) {
        super(width, height);
    }

    public BasePopupWindow(View contentView, int width, int height,
                           boolean focusable) {
        super(contentView, width, height, focusable);
    }

    public BasePopupWindow(View contentView) {
        super(contentView);
    }

    public BasePopupWindow(View contentView, int width, int height) {
        super(contentView, width, height, true);
        this.popRootView = contentView;
        setFocusable(true);
        setOutsideTouchable(true);
        setTouchable(true);
        ColorDrawable dw = new ColorDrawable(0x50000000);
        this.setBackgroundDrawable(dw);
//        setAnimationStyle(R.style.popwin_anim_style);
        initViews();
        initEvents();
        init();

    }


    public abstract void initViews();

    public abstract void initEvents();

    public abstract void init();

    public View findViewById(int id) {
        return popRootView.findViewById(id);
    }
}

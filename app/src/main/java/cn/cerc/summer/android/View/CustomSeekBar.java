package cn.cerc.summer.android.View;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.huagu.ehealth.R;


/**
 * Created by fff on 2016/11/24.
 */

public class CustomSeekBar extends LinearLayout implements SeekBar.OnSeekBarChangeListener, Animation.AnimationListener {

    public CustomSeekBar(Context context) {
        super(context);
        init(context);
    }

    public CustomSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomSeekBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CustomSeekBar, defStyleAttr, defStyleRes);
        max = ta.getInt(R.styleable.CustomSeekBar_max, max);
        min = ta.getInt(R.styleable.CustomSeekBar_min, min);

        init(context);
    }

    private int min = 0;//区间最小值
    private int max = 50;//最大值

    private int curprogress=0;

    private int width; //seerbar 的宽度
    private int left = 0;   //上面显示数字的textview的左边距离

    private TextView textview;
    private SeekBar seekBar;
    private Context context;

    private void init(Context context) {
        this.context = context;
        RelativeLayout relativeLayout = new RelativeLayout(context);
        textview = new TextView(context);
        textview.setText(min + "");
        textview.setPadding(20,25,20,25);
        textview.setTextColor(Color.parseColor("#48B2BD"));
        textview.setVisibility(View.INVISIBLE);
        textview.setGravity(Gravity.LEFT);
        textview.setTextSize(14);
        relativeLayout.addView(textview);
        addView(relativeLayout);
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        textview.setLayoutParams(rlp);

        seekBar = new SeekBar(context);
        seekBar.setMax(max - min);
        seekBar.setOnSeekBarChangeListener(this);
        addView(seekBar);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        wid = (width - seekBar.getPaddingLeft() - seekBar.getPaddingRight()) / (max - min);
    }

    public float wid = 0;// 进度1的距离

    public void setMin(int min) {
        this.min = min;
    }

    public void setMax(int max) {
        this.max = max;
    }

    private SeekBar.OnSeekBarChangeListener Onseekbarchangelistener;

    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener Onseekbarchangelistener) {
        this.Onseekbarchangelistener = Onseekbarchangelistener;

    }

    /**
     * 设置的当前进度
     * @param progress  进度， 需得是大于最小值的
     */
    public void setProgress(int progress){
        seekBar.setProgress(progress-min);
        curprogress = progress;
        textview.setText((progress + min) + "");
        left = (int) ((wid * progress) + (float) seekBar.getPaddingLeft() - (getTextWidth((progress + min) + "") / 2) - textview.getPaddingLeft());
        textview.layout(left, textview.getTop(), left + (int) getTextWidth((progress + min) + "") + textview.getPaddingRight() + textview.getPaddingLeft(), textview.getBottom());
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (Onseekbarchangelistener == null) return;
        Onseekbarchangelistener.onProgressChanged(seekBar, progress, fromUser);
        curprogress = progress;
        textview.setText((progress + min) + "");
        left = (int) ((wid * progress) + (float) seekBar.getPaddingLeft() - (getTextWidth((progress + min) + "") / 2) - textview.getPaddingLeft());
        textview.layout(left, textview.getTop(), left + (int) getTextWidth((progress + min) + "") + textview.getPaddingRight() + textview.getPaddingLeft(), textview.getBottom());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (Onseekbarchangelistener == null) return;
        Onseekbarchangelistener.onStartTrackingTouch(seekBar);
        if (left == 0)
            left = (int) ((float) seekBar.getPaddingLeft() - (getTextWidth(min + "") / 2)) - textview.getPaddingLeft();
        textview.layout(left, textview.getTop(), left + (int) getTextWidth(min + "") + textview.getPaddingRight() + textview.getPaddingLeft(), textview.getBottom());
        setShowAnim(true);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (Onseekbarchangelistener == null) return;
        Onseekbarchangelistener.onStopTrackingTouch(seekBar);
        setShowAnim(false);
    }

    private float getTextWidth(String str) {
        return textview.getPaint().measureText(str);
    }

    private boolean isshow_index = false;

    /**
     * 显示上面数值的动画效果，
     * @param is_show   显示或者隐藏
     */
    public void setShowAnim(boolean is_show){
        isshow_index = is_show;
        ScaleAnimation scaleAnimation = is_show?new ScaleAnimation(0.1f, 1.0f,0.1f,1.0f,(getTextWidth(textview.getText().toString()) / 2  + textview.getPaddingLeft()),textview.getBottom()):new ScaleAnimation(1.0f, 0.1f,1.0f,0.1f,(getTextWidth(textview.getText().toString()) / 2  + textview.getPaddingLeft()),textview.getBottom());
        scaleAnimation.setDuration(300);
        textview.startAnimation(scaleAnimation);
        scaleAnimation.setAnimationListener(this);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }
    @Override
    public void onAnimationEnd(Animation animation) {
        if (isshow_index)
            textview.setVisibility(View.VISIBLE);
        else
            textview.setVisibility(View.INVISIBLE);
    }
    @Override
    public void onAnimationRepeat(Animation animation) {
    }
}

package cn.sd5g.appas.android.parts.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.sd5gs.vine.R;

import cn.sd5g.appas.android.core.OnFileChooseItemListener;

public class FileDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private TextView tvImage;
    private TextView tvFile;
    private TextView tvCancle;
    private OnFileChooseItemListener onFileChooseItemListener;

    public FileDialog(@NonNull Context context) {
        super(context);
    }

    public FileDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected FileDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_choose_file_view);
        initView();
        initListener();
    }

    /**
     * 设置监听事件，并返回相对应的item的破sition
     *
     * @param onFileChooseItemListener
     */
    public void setOnFileChooseItemListener(OnFileChooseItemListener onFileChooseItemListener) {
        this.onFileChooseItemListener = onFileChooseItemListener;
    }

    /**
     * 初始化及弹出框大小动画设置
     */
    private void initView() {
        tvImage = (TextView) findViewById(R.id.tvImage);
        tvFile = (TextView) findViewById(R.id.tvFile);
        tvCancle = (TextView) findViewById(R.id.tvCancle);

        DisplayMetrics dm = new DisplayMetrics();
        Window window = this.getWindow();
        window.getWindowManager().getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM;
        lp.width = (int) (dm.widthPixels * 0.9);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        window.setWindowAnimations(R.style.choose_file_anim);
    }

    /**
     * 初始化点击事件
     */
    private void initListener() {
        tvImage.setOnClickListener(this);
        tvFile.setOnClickListener(this);
        tvCancle.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int pos = -1;
        switch (v.getId()) {
            case R.id.tvImage://图片+相机
                pos = 1;
                break;
            case R.id.tvFile://文件
                pos = 2;
                break;
            case R.id.tvCancle://取消
                pos = 3;
                break;
        }
        if (onFileChooseItemListener != null && pos != -1)
            onFileChooseItemListener.onChoose(pos);
        this.dismiss();
    }
}

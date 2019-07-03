package cn.sd5g.appas.android.units;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.PaintDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sd5gs.views.R;

import java.util.List;

public class CommBottomPopWindow extends PopupWindow implements View.OnClickListener {

    public View popRootView;
    private Button cancleBtn;
    private PopWindowListener listener;
    private LinearLayout mLayout;
    private Context mContext;
    private boolean isHasSubTitle = false;
    private LayoutInflater inflater;

    public CommBottomPopWindow(final Activity context) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popRootView = inflater.inflate(R.layout.comm_title_popwindow, null);
        // 设置SelectPicPopupWindow的View
        this.setContentView(popRootView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x50000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(new PaintDrawable());
        this.setBackgroundDrawable(dw);
        initViews();
        initEvents();
    }

    public CommBottomPopWindow(Activity context, boolean ispop) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popRootView = inflater.inflate(R.layout.comm_setup_popwindow, null);
        // 设置SelectPicPopupWindow的View
        this.setContentView(popRootView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x50000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(new PaintDrawable());
        this.setBackgroundDrawable(dw);
        initViews();
        initEvents();
    }

    /**
     * 功能描述: 设置点击事件<br>
     * 〈功能详细描述〉
     * 点击的自定义回调接口
     */
    public void setPopListener(PopWindowListener listener) {
        this.listener = listener;
    }

    /**
     * 功能描述:初始化小标题 <br>
     * 顶部是否需要提示的小标题
     */
    public void initPopSubTitle(String notiTxt) {
        mLayout.addView(createItem(notiTxt, true));
    }

    /**
     * 功能描述: 初始化item<br>
     * 〈功能详细描述〉
     * 动态添加的条目
     */
    public void initPopItem(List<MainTitleMenu> list) {
        if (list == null || list.size() == 0) {
            return;
        }

        for (int i = 0; i < list.size(); i++) {
            String title = list.get(i).getName();
            mLayout.addView(createItem(title, i, list.size(), list.get(i).isLine()));
        }
    }

    private View createItem(String itemTxt, boolean isSubTitle) {
        return createItem(itemTxt, -1, -1, isSubTitle);
    }

    private View createItem(String itemTxt, final int index, int total) {
        return createItem(itemTxt, index, total, false);
    }

    /**
     * 功能描述: 创建item<br>
     * 〈功能详细描述〉
     * <p>
     * 创建具体的条目
     */
    private View createItem(String itemTxt, final int index, int total,
                            boolean isSubTitle) {
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.comm_popwindow_item, null);
        LinearLayout layout = (LinearLayout) view
                .findViewById(R.id.comm_popwindow_item_layout);
        View view1 = view.findViewById(R.id.view_title);
        final TextView textView = (TextView) view
                .findViewById(R.id.comm_popwindow_item_txt);
        if (isSubTitle) {
            view1.setVisibility(View.VISIBLE);
        }
        textView.setText(itemTxt);
        textView.setSelected(true);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index == -1) {
                    return;
                }
                if (listener != null) {
                    listener.onPopSelected(index);
                }
            }
        });
        return view;
    }

    public void initViews() {
        mLayout = (LinearLayout) popRootView.findViewById(R.id.comm_bottom_popwindow_layout);
        cancleBtn = (Button) popRootView.findViewById(R.id.camp_pop_cancle);
        isHasSubTitle = false;
    }

    public void initEvents() {
        cancleBtn.setOnClickListener(this);
        popRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camp_pop_cancle:
                this.dismiss();
                break;
            default:
                break;
        }
    }

    /**
     * 功能描述: 显示pop window<br>
     * 〈功能详细描述〉
     */
    public void show(View view) {
        showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    //回调接口定义
    public interface PopWindowListener {
        public void onPopSelected(int which);
    }
}
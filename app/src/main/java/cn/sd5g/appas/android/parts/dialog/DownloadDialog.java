package cn.sd5g.appas.android.parts.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sd5gs.views.R;

public class DownloadDialog extends Dialog implements View.OnClickListener {

    private Button btn_cancel, btn_yes;
    private OnDialogClick onDialogClick;
    private Context mContext;
    private TextView dialog_txt_title;
    private boolean isUpdate = true;
    private View view_Line;

    public DownloadDialog(Context context, boolean isUpdate) {
        super(context, R.style.DialogTheme);
        mContext = context;
        this.isUpdate = isUpdate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_download);
        dialog_txt_title = (TextView) findViewById(R.id.text_title);
        btn_yes = (Button) findViewById(R.id.btn_yes);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        view_Line = findViewById(R.id.view_Line);
        btn_cancel.setOnClickListener(this);
        btn_yes.setOnClickListener(this);
        if (!isUpdate) {
            dialog_txt_title.setText("当前已经是最新版本！");
            btn_yes.setVisibility(View.GONE);
            showNoUpdate();
        }
    }

    private void showNoUpdate() {
        view_Line.setVisibility(View.VISIBLE);
        Button btnOK = (Button) findViewById(R.id.btn_yes);
        btnOK.setVisibility(View.GONE);
        Button btnCancel = (Button) findViewById(R.id.btn_cancel);
        GradientDrawable drawable = new GradientDrawable();
        btnCancel.setBackgroundDrawable(drawable);
        btnCancel.setText("确定");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                if (onDialogClick != null) {
                    onDialogClick.onConfrim();
                }
                dismiss();
                break;
            case R.id.btn_cancel:
                if (onDialogClick != null) {
                    onDialogClick.onCancel();
                }
                dismiss();
                break;
        }

    }

    public void setOnDialogClickListern(OnDialogClick onDialogClick) {
        this.onDialogClick = onDialogClick;
    }

    public interface OnDialogClick {
        void onConfrim();

        void onCancel();
    }
}

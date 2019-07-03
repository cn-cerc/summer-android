package cn.sd5g.appas.android.parts.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sd5gs.vine.R;

import cn.sd5g.appas.android.core.MyApp;

public class InputDialog extends Dialog implements View.OnClickListener {

    private EditText dialog_edit_input;
    private Button btn_cancel, btn_yes;
    private OnDialogClick onDialogClick;
    private Context mContext;
    private TextView dialog_txt_title;

    public InputDialog(Context context) {
        super(context, R.style.DialogTheme);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_input);
        dialog_edit_input = (EditText) findViewById(R.id.dialog_edit_input);
        btn_cancel = (Button) findViewById(R.id.dialog_btn_cancel);
        btn_yes = (Button) findViewById(R.id.dialog_btn_ok);
        dialog_txt_title = (TextView) findViewById(R.id.dialog_txt_title);
        dialog_edit_input.clearFocus();
        dialog_edit_input.setText(MyApp.HOME_URL);
        btn_cancel.setOnClickListener(this);
        btn_yes.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_btn_ok:
                String newsUrl = dialog_edit_input.getText().toString();
                if (dialog_edit_input.getText().toString().trim().equals("")) {
                    dialog_edit_input.setError("输入内容不能为空");
                } else {
                    if (onDialogClick != null) {
                        onDialogClick.onConfrim(newsUrl);
                    }
                }
//                dismiss();
                break;
            case R.id.dialog_btn_cancel:
                if (onDialogClick != null) {
                    onDialogClick.onCancel();
//                    Toast.makeText(mContext, "点击了取消", Toast.LENGTH_SHORT).show();
                }
                dismiss();
                break;
        }
    }

    public void setOnDialogClickListern(OnDialogClick onDialogClick) {
        this.onDialogClick = onDialogClick;
    }

    public interface OnDialogClick {
        void onConfrim(String newsUrl);

        void onCancel();
    }
}

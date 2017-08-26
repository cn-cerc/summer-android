package cn.cerc.summer.android.parts.barcode;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mimrc.vine.R;

import java.util.Timer;
import java.util.TimerTask;

public class DlgScanProduct extends AppCompatActivity implements View.OnClickListener {
    ImageView imgBack;
    EditText edtNum;

    private int[] buttons = {R.id.btnOk, R.id.btnCancel, R.id.btnDelete};
    private int recordIndex;
    private int num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dlg_scan_product);

        Intent intent = getIntent();
        recordIndex = intent.getIntExtra("recordIndex", -1);
        num = intent.getIntExtra("num", 0);

        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(this);

        edtNum = (EditText) findViewById(R.id.edtNum);
        edtNum.setText("" + num);
        edtNum.requestFocus(("" + num).length());
        edtNum.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    Intent intent = new Intent();
                    intent.putExtra("recordIndex", recordIndex);
                    intent.putExtra("num", getNum());
                    setResult(RESULT_OK, intent);
                    finish();
                    return true;
                }
                return false;
            }
        });

        Timer timer = new Timer();
        timer.schedule(new TimerTask()   {
            public void run() {
                InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(edtNum, 0);
            }
        }, 300);

        for (int index : buttons)
            ((Button) findViewById(index)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btnOk:
                intent.putExtra("recordIndex", recordIndex);
                intent.putExtra("num", getNum());
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.btnDelete:
                intent.putExtra("recordIndex", recordIndex);
                intent.putExtra("num", 0);
                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                setResult(RESULT_CANCELED, intent);
                finish();
                break;
        }
    }

    private int getNum() {
        String num = edtNum.getText().toString().trim();
        if ("".equals(num))
            num = "0";
        try {
            return Integer.parseInt(num);
        } catch (Exception e) {
            return 0;
        }
    }

    public static void startFormForResult(AppCompatActivity context, int recordIndex, int num) {
        Intent intent = new Intent();
        intent.setClass(context, DlgScanProduct.class);
        intent.putExtra("recordIndex", recordIndex);
        intent.putExtra("num", num);
        context.startActivityForResult(intent, 1, null);
    }
}

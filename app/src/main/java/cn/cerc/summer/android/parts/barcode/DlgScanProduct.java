package cn.cerc.summer.android.parts.barcode;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mimrc.vine.R;

import java.util.Timer;
import java.util.TimerTask;

import cn.cerc.jdb.core.Record;
import cn.cerc.summer.android.basis.RemoteForm;

public class DlgScanProduct extends AppCompatActivity implements View.OnClickListener {
    private static final int MSG_TIMER = 1;
    private static final int MSG_UPLOAD = 2;

    ImageView imgBack;
    EditText edtNum;

    private int[] buttons = {R.id.btnOk, R.id.btnCancel, R.id.btnDelete};
    private int recordIndex;
    private int num;
    private String barcode;
    private boolean isSpare;
    private String modifyUrl;
    private String deleteUrl;
    private DlgScanProduct instance;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPLOAD:
                    RemoteForm rf = (RemoteForm) msg.obj;
                    if (rf.isOk()) {
                        Toast.makeText(instance, "操作成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dlg_scan_product);
        instance = this;

        Intent intent = getIntent();
        recordIndex = intent.getIntExtra("recordIndex", -1);
        num = intent.getIntExtra("num", 0);
        barcode = intent.getStringExtra("barcode");
        isSpare = intent.getBooleanExtra("isSpare", false);
        modifyUrl = intent.getStringExtra("modifyUrl");
        deleteUrl = intent.getStringExtra("deleteUrl");

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
        timer.schedule(new TimerTask() {
            public void run() {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(edtNum, 0);
            }
        }, 300);

        for (int index : buttons) {
            findViewById(index).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btnOk:
            case R.id.btnDelete:
                if(v.getId() == R.id.btnDelete){
                    edtNum.setText("0");
                }
                intent.putExtra("recordIndex", recordIndex);
                intent.putExtra("num", getNum());
                intent.putExtra("isSpare", isSpare);
                setResult(RESULT_OK, intent);

                final String url = getNum() == 0 ? deleteUrl : modifyUrl;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        RemoteForm rf = new RemoteForm(url);
                        rf.putParam("barcode", barcode);
                        rf.putParam("num", "" + getNum());
                        rf.putParam("isSpare", isSpare ? "true" : "false");
                        handler.sendMessage(rf.execByMessage(MSG_UPLOAD));
                    }
                }).start();
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

    public static void startFormForResult(AppCompatActivity context, int recordIndex, Record item,
                                          String modifyUrl, String deleteUrl) {
        Intent intent = new Intent();
        intent.setClass(context, DlgScanProduct.class);
        intent.putExtra("recordIndex", recordIndex);
        intent.putExtra("num", item.getInt("num"));
        intent.putExtra("barcode", item.getString("barcode"));
        intent.putExtra("isSpare", item.getBoolean("isSpare"));
        intent.putExtra("modifyUrl", modifyUrl);
        intent.putExtra("deleteUrl", deleteUrl);
        context.startActivityForResult(intent, 1, null);
    }
}

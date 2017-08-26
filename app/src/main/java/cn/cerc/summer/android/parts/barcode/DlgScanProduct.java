package cn.cerc.summer.android.parts.barcode;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mimrc.vine.R;

public class DlgScanProduct extends AppCompatActivity implements View.OnClickListener {
    int[] buttons = {R.id.btnOk, R.id.btnCancel, R.id.btnDelete};
    EditText edtNum;

    private int recordIndex;
    private int num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dlg_scan_product);

        Intent intent = getIntent();
        recordIndex = intent.getIntExtra("recordIndex", -1);
        num = intent.getIntExtra("num", 0);

        edtNum = (EditText) findViewById(R.id.edtNum);
        edtNum.setText("" + num);
        edtNum.requestFocus((""+num).length());

        for (int index : buttons)
            ((Button) findViewById(index)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btnOk:
                intent.putExtra("recordIndex", recordIndex);
                intent.putExtra("num", Integer.parseInt(edtNum.getText().toString()));
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.btnCancel:
                setResult(RESULT_CANCELED, intent);
                finish();
                break;
            case R.id.btnDelete:
                intent.putExtra("recordIndex", recordIndex);
                intent.putExtra("num", 0);
                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                break;
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

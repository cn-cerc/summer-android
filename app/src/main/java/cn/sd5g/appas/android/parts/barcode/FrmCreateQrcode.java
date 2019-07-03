package cn.sd5g.appas.android.parts.barcode;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sd5gs.vine.R;

import cn.sd5g.appas.android.parts.barcode.zxing.encoding.EncodingHandler;

public class FrmCreateQrcode extends AppCompatActivity implements OnClickListener {
    /**
     * 显示扫描结果
     */
    private TextView mTextView;
    /**
     * 显示扫描拍的图片
     */
    private ImageView mImageView;

    public static void startForm(Context context, String text, boolean qrcode) {
        Intent intent = new Intent();
        intent.setClass(context, FrmCreateQrcode.class);
        intent.putExtra("text", text);
        intent.putExtra("qrcode", qrcode);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frm_create_qrcode);
        mTextView = (TextView) findViewById(R.id.result);
        mImageView = (ImageView) findViewById(R.id.qrcode_bitmap);

        findViewById(R.id.btn_scan_qr).setOnClickListener(this);

        String text = getIntent().getStringExtra("text");
        if (!text.equals("")) {
            Bitmap qrCodeBitmap = null;
            //根据字符串生成图片并显示在界面上，第二个参数为图片的大小（350*150）
            if (getIntent().getBooleanExtra("qrcode", true))
                qrCodeBitmap = EncodingHandler.createQRCode(text, 350);
            else
                qrCodeBitmap = EncodingHandler.creatBarcode(this, text, 350, 150, true);
            if (qrCodeBitmap != null)
                mImageView.setImageBitmap(qrCodeBitmap);
            else
                Toast.makeText(this, "解析错误", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scan_qr:
                FrmScanBarcode.startForm(this);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FrmScanBarcode.SCANNIN_GREQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    //显示扫描到的内容
                    mTextView.setText(bundle.getString("result"));
                    //显示
                    mImageView.setImageBitmap((Bitmap) data.getParcelableExtra("bitmap"));
                }
                break;
        }
    }

}

package cn.cerc.summer.android.parts.barcode;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mimrc.vine.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import cn.cerc.summer.android.basis.core.MyApp;
import cn.cerc.summer.android.basis.tools.DataSet;
import cn.cerc.summer.android.basis.tools.ListViewAdapter;
import cn.cerc.summer.android.basis.tools.ListViewInterface;
import cn.cerc.summer.android.basis.tools.Record;

import static cn.cerc.summer.android.parts.music.FrmCaptureMusic.url;

public class FrmScanProduct extends AppCompatActivity implements View.OnClickListener, ListViewInterface {
    private static final int MSG_TIMER = 1;
    private static final int MSG_UPLOAD = 2;

    ImageView imgBack;
    TextView lblTitle;
    EditText edtBarcode;
    Button btnSave;
    WebView webView;
    ListView lstView;

    private String homeUrl;
    private String viewUrl;
    private String postUrl;
    private DataSet dataSet = new DataSet();
    private ListViewAdapter adapter;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_TIMER:
                    edtBarcode.requestFocus();
                    break;
                case MSG_UPLOAD:
                    String barcode = msg.getData().getString("barcode");
                    if (dataSet.locate("barcode", barcode)) {
                        Record item = dataSet.getCurrent();
                        webView.loadUrl(String.format("%s%s?barcode=%s", MyApp.HOME_URL, viewUrl,
                                item.getString("barcode")));
                        item.setField("state", msg.arg1);
                        adapter.notifyDataSetChanged();
                    }
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * @param context FrmMain
     * @param title   页面标题
     * @param postUrl 数据上传到指定的url
     * @param viewUrl 显示相应的记录之url
     */
    public static void startForm(Context context, String title, String homeUrl, String viewUrl, String postUrl) {
        Intent intent = new Intent();
        intent.putExtra("title", title);
        intent.putExtra("homeUrl", homeUrl);
        intent.putExtra("viewUrl", viewUrl);
        intent.putExtra("postUrl", postUrl);
        intent.setClass(context, FrmScanProduct.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frm_scan_product);
        Intent intent = getIntent();

        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(this);


        lblTitle = (TextView) findViewById(R.id.lblTitle);
        lblTitle.setText(intent.getStringExtra("title"));

        this.homeUrl = intent.getStringExtra("homeUrl");
        this.viewUrl = intent.getStringExtra("viewUrl");
        this.postUrl = intent.getStringExtra("postUrl");

        edtBarcode = (EditText) findViewById(R.id.edtBarcode);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

//        for (int i = 0; i < 2; i++) {
//            dataSet.append();
//            dataSet.setField("barcode", "123424123412");
//            dataSet.setField("num", 1 + i);
//        }

        adapter = new ListViewAdapter(this, R.layout.activity_list_scan_product, dataSet, this);
        lstView = (ListView) findViewById(R.id.lstView);
        lstView.setAdapter(adapter);

        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
        //启用支持javascript
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        //打开指定的网页
        webView.loadUrl(MyApp.HOME_URL + homeUrl);

        //关闭软键盘
        edtBarcode.setInputType(InputType.TYPE_NULL);
        edtBarcode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    saveBarcode();
                    return true;
                }
                return false;
            }
        });

        //自动归位焦点
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = MSG_TIMER;
                handler.sendMessage(msg);
            }
        }, 1000, 200);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSave: {
                if (edtBarcode.getInputType() == InputType.TYPE_NULL) {
                    edtBarcode.setInputType(InputType.TYPE_CLASS_TEXT);
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(edtBarcode, 0);
                } else {
                    edtBarcode.setInputType(InputType.TYPE_NULL);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null)
                        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                break;
            }
            case R.id.lblBarcode: {
                int recordIndex = (Integer) view.getTag();
                Record item = dataSet.get((Integer) view.getTag());
                webView.loadUrl(MyApp.HOME_URL + viewUrl + "?barcode=" + item.getString("barcode"));
                break;
            }
            case R.id.lblNum: {
                int recordIndex = (Integer) view.getTag();
                Record item = dataSet.get((Integer) view.getTag());
                DlgScanProduct.startFormForResult(this, recordIndex, item.getInt("num"));
                break;
            }
            case R.id.imgView: {
                int recordIndex = (Integer) view.getTag();
                Record item = dataSet.get((Integer) view.getTag());
                if (item.getInt("state") == 2) {//出错状态
                    item.setField("state", 0);
                    requestUpload(item.getString("barcode"), item.getInt("num"));
                }
                break;
            }
            case R.id.imgBack:
                finish();
                break;
            default:
                break;
        }

    }

    private void saveBarcode() {
        String barcode = edtBarcode.getText().toString().trim();
        if (barcode.length() > 0) {
            if (dataSet.locate("barcode", barcode)) {
                dataSet.setField("num", dataSet.getInt("num") + 1);
                dataSet.setField("state", 0);
            } else {
                dataSet.insert(0);
                dataSet.setField("barcode", barcode);
                dataSet.setField("num", 1);
            }
            adapter.notifyDataSetChanged();
            requestUpload(barcode, dataSet.getInt("num"));
        }
//        edtBarcode.setText("");
//        edtBarcode.getFocusedRect();
        edtBarcode.setSelection(0, edtBarcode.getText().toString().length() - 1);
        edtBarcode.requestFocus();
    }

    private void requestUpload(final String barcode, final int num) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //通知进行上传
                Bundle bundle = new Bundle();
                bundle.putString("barcode", barcode);
                final Message msg = new Message();
                msg.what = MSG_UPLOAD;
                msg.setData(bundle);

                HttpClient http = new HttpClient(MyApp.HOME_URL + postUrl);
                http.put("barcode", barcode);
                http.put("num", "" + num);

                String response = http.post();
                Log.d("FrmScanProduct", response);

                int state = 2; //更新state为失败
                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                    if (json.has("result") && json.getBoolean("result"))
                        state = 1; //更新state为成功
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                msg.arg1 = state;
                handler.sendMessage(msg);
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        edtBarcode.requestFocus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            int index = data.getIntExtra("recordIndex", -1);
            int num = data.getIntExtra("num", 0);
            dataSet.get(index).setField("num", num);
            if (num == 0)
                dataSet.remove(index);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onGetText(View view, Record item, int position) {
        TextView lblBarcode = (TextView) view.findViewById(R.id.lblBarcode);
        lblBarcode.setText(item.getString("barcode"));
        lblBarcode.setOnClickListener(this);
        lblBarcode.setTag(position);

        TextView lblNum = (TextView) view.findViewById(R.id.lblNum);
        lblNum.setText("" + item.getInt("num"));
        lblNum.setOnClickListener(this);
        lblNum.setTag(position);

        ImageView imgView = (ImageView) view.findViewById(R.id.imgView);
        switch (item.getInt("state")) {
            case 0:
                imgView.setImageResource(R.mipmap.reload);
                //设置动画效果
                RotateAnimation animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
                        0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(3000);//设置动画持续时间
                animation.setRepeatCount(Animation.INFINITE);//设定无限循环
                animation.setRepeatMode(Animation.RESTART);//设定重复模式
                imgView.setOnClickListener(this);
                imgView.setTag(position);
                imgView.startAnimation(animation);
                imgView.setBackgroundColor(Color.WHITE);
                animation.startNow();
                break;
            case 1:
                imgView.clearAnimation();
                imgView.setImageResource(R.mipmap.refresh_succeed);
                imgView.setBackgroundColor(Color.BLUE);
                break;
            default:
                imgView.clearAnimation();
                imgView.setImageResource(R.mipmap.refresh_failed);
                imgView.setBackgroundColor(Color.RED);
                break;
        }
    }
}

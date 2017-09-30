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
import android.widget.CheckBox;
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

import cn.cerc.jdb.core.DataSet;
import cn.cerc.jdb.core.Record;
import cn.cerc.summer.android.basis.core.MyApp;
import cn.cerc.summer.android.basis.db.ListViewAdapter;
import cn.cerc.summer.android.basis.db.ListViewInterface;
import cn.cerc.summer.android.basis.db.RemoteForm;
import cn.cerc.summer.android.basis.forms.FrmMain;

import static cn.cerc.summer.android.parts.music.FrmCaptureMusic.url;

public class FrmScanProduct extends AppCompatActivity implements View.OnClickListener, ListViewInterface {
    private static final int MSG_TIMER = 1;
    private static final int MSG_UPLOAD = 2;

    ImageView imgBack;
    TextView lblTitle;
    CheckBox chkIsSpare;
    EditText edtBarcode;
    Button btnSave;
    WebView webView;
    ListView lstView;

    private String homeUrl;
    private String returnUrl;
    private String appendUrl;
    private String modifyUrl;
    private String deleteUrl;
    private String viewUrl;
    private DataSet dataSet = new DataSet();
    private ListViewAdapter adapter;
    private FrmScanProduct instance;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_TIMER:
                    edtBarcode.requestFocus();
                    break;
                case MSG_UPLOAD:
                    receiveHost((RemoteForm) msg.obj);
                    break;
                default:
                    break;
            }
        }

        //上传到主机后的返回值处理
        private void receiveHost(RemoteForm rf) {
            if (!rf.isOk()) {
                Toast.makeText(instance, rf.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            String barcode = rf.getParam("barcode");
            boolean isSpare = "true".equals(rf.getParam("isSpare"));
            if (dataSet.locate("barcode;isSpare", barcode, isSpare)) {
                Record item = dataSet.getCurrent();
                if (rf.isOk()) {
                    String data = rf.getData();
                    JSONObject json = null;
                    int num = 0;
                    String returnBarcode = "";
                    try {
                        json = new JSONObject(data);
                        returnBarcode = json.getString("barcode");
                        if (barcode.equals(returnBarcode)) {
                            if (item.getBoolean("appendStatus")) {
                                item.setField("num", json.getInt("Num_"));
                                item.setField("descSpec", json.getString("descSpec"));
                                item.setField("appendStatus", false);
                            }
                            String url = String.format("%s?barcode=%s&isSpare=%s",
                                    MyApp.getFormUrl(viewUrl), item.getString("barcode"),
                                    isSpare ? "true" : "false");
                            webView.loadUrl(url);
                            item.setField("state", 1);
                            adapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        item.setField("state", 2);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    item.setField("state", 2);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    };


    /**
     * @param context   FrmMain
     * @param title     页面标题
     * @param homeUrl   第一次显示的页面的url
     * @param returnUrl 返回H5页面时的url
     * @param appendUrl 新增数据指定的url
     * @param modifyUrl 修改数据指定的url
     * @param deleteUrl 删除相应的记录的url
     * @param viewUrl 显示相应的记录的url
     */
    public static void startForm(Context context, String title, String homeUrl, String returnUrl,
                                 String appendUrl, String modifyUrl, String deleteUrl, String viewUrl) {
        Intent intent = new Intent();
        intent.putExtra("title", title);
        intent.putExtra("homeUrl", homeUrl);
        intent.putExtra("returnUrl", returnUrl);
        intent.putExtra("appendUrl", appendUrl);
        intent.putExtra("modifyUrl", modifyUrl);
        intent.putExtra("deleteUrl", deleteUrl);
        intent.putExtra("viewUrl", viewUrl);
        intent.setClass(context, FrmScanProduct.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frm_scan_product);
        instance = this;
        Intent intent = getIntent();

        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(this);

        lblTitle = (TextView) findViewById(R.id.lblTitle);
        lblTitle.setText(intent.getStringExtra("title"));

        this.homeUrl = intent.getStringExtra("homeUrl");
        this.returnUrl = intent.getStringExtra("returnUrl");
        this.appendUrl = intent.getStringExtra("appendUrl");
        this.modifyUrl = intent.getStringExtra("modifyUrl");
        this.deleteUrl = intent.getStringExtra("deleteUrl");
        this.viewUrl = intent.getStringExtra("viewUrl");

        chkIsSpare = (CheckBox) findViewById(R.id.chkIsSpare);
        edtBarcode = (EditText) findViewById(R.id.edtBarcode);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

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
        webView.loadUrl(MyApp.getFormUrl(homeUrl));

        //关闭软键盘
        edtBarcode.setInputType(InputType.TYPE_NULL);
        edtBarcode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    saveBarcode();
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
                Record item = dataSet.getIndex((Integer) view.getTag());
                webView.loadUrl(MyApp.getFormUrl(String.format("%s?barcode=%s&isSpare=%s",
                        viewUrl, item.getString("barcode"), item.getBoolean("isSpare") ? "true" : "false")));
                break;
            }
            case R.id.lblNum: {
                int recordIndex = (Integer) view.getTag();
                Record item = dataSet.getIndex((Integer) view.getTag());
                if (item.getBoolean("appendStatus"))
                    break;
                DlgScanProduct.startFormForResult(this, recordIndex, item, modifyUrl, deleteUrl);
                break;
            }
            case R.id.imgView: {
                int recordIndex = (Integer) view.getTag();
                Record item = dataSet.getIndex((Integer) view.getTag());
                if (item.getInt("state") == 2) {//出错状态
                    Toast.makeText(this, "重新上传中...", Toast.LENGTH_SHORT).show();
                    item.setField("state", 0);
                    requestUpload(item);
                }
                break;
            }
            case R.id.imgBack:
                FrmMain.getInstance().loadUrl(MyApp.getFormUrl(this.returnUrl));
                finish();
                break;
            default:
                break;
        }
    }

    private void saveBarcode() {
        String barcode = edtBarcode.getText().toString().trim();
        boolean isSpare = chkIsSpare.isChecked();
        if (barcode.length() > 0) {
            if (dataSet.locate("barcode;isSpare", barcode, isSpare)) {
                dataSet.setField("state", 0);
                dataSet.setField("num", dataSet.getInt("num") + 1);
            } else {
                dataSet.append(0);
                dataSet.setField("isSpare", isSpare);
                dataSet.setField("barcode", barcode);
                dataSet.setField("num", 1);
                dataSet.setField("appendStatus", true);
            }
            adapter.notifyDataSetChanged();
            requestUpload(dataSet.getCurrent());
        }
        edtBarcode.setSelection(0, edtBarcode.getText().toString().length() - 1);
        edtBarcode.requestFocus();
    }

    private void requestUpload(final Record item) {
        final boolean appendStatus = item.getBoolean("appendStatus");
        new Thread(new Runnable() {
            @Override
            public void run() {
                RemoteForm rf = new RemoteForm(appendStatus ? appendUrl : modifyUrl);
                rf.putParam("barcode", item.getString("barcode"));
                rf.putParam("num", "" + item.getInt("num"));
                rf.putParam("isSpare", item.getBoolean("isSpare") ? "true" : "false");
                handler.sendMessage(rf.execByMessage(MSG_UPLOAD));
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
            dataSet.setRecNo(index + 1);
            dataSet.setField("num", num);
            if (num == 0) {
                webView.loadUrl(MyApp.getFormUrl(homeUrl));
                dataSet.delete();
            } else {
                Record item = dataSet.getCurrent();
                webView.loadUrl(MyApp.getFormUrl(String.format("%s?barcode=%s&isSpare=%s",
                        viewUrl, item.getString("barcode"), item.getBoolean("isSpare") ? "true" : "false")));
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onGetText(View view, Record item, int position) {
        TextView lblBarcode = (TextView) view.findViewById(R.id.lblBarcode);
        String desc = !"".equals(item.getString("descSpec")) ? item.getString("descSpec") : item.getString("barcode");
        lblBarcode.setText(desc);
        lblBarcode.setOnClickListener(this);
        lblBarcode.setTag(position);

        TextView lblSpare = (TextView) view.findViewById(R.id.lblSpare);
        lblSpare.setText(item.getBoolean("isSpare") ? "赠" : "");

        TextView lblNum = (TextView) view.findViewById(R.id.lblNum);
        lblNum.setText("" + item.getInt("num"));
        lblNum.setOnClickListener(this);
        lblNum.setTag(position);

        ImageView imgView = (ImageView) view.findViewById(R.id.imgView);
        switch (item.getInt("state")) {
            case 0:
                imgView.setImageResource(R.mipmap.reload);
                //设置动画效果
                RotateAnimation animation = new RotateAnimation(0f, -360f, Animation.RELATIVE_TO_SELF,
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

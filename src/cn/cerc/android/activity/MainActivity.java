package cn.cerc.android.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ValueCallback;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import cn.cerc.android.R;
import cn.cerc.android.view.MoreWindow;
import cn.cerc.android.view.UpdateDialog;
import cn.cerc.android.view.MyWebView.InputListener;
import cn.cerc.android.view.MyWebView.Listener;

public class MainActivity extends Activity implements OnClickListener
{
    public static final int FILECHOOSER_RESULTCODE = 101;
    private static final long MIN_TIME = 3000;
    private LinearLayout mFarwordLayout;
    private LinearLayout mBackLayout;
    private LinearLayout mReFreshLayout;
    private LinearLayout mHomeLayout;
    private cn.cerc.android.view.MyWebView mWebview;
    private ProgressBar mWebProgressBar;
    private LinearLayout mFailLayout;
    private Button mBtnSettingNetwork;
    private LinearLayout mFirstLayout;
    private ImageView mAnimImageView;
    private long starTime;
    private MoreWindow mMoreWindow;
    private View mInputLayout;
    private EditText mInputBarcodeEt;
    private Button mInputBarcodeBtn;
    private Handler mHandler = new ViewHandler(MainActivity.this);
    public ValueCallback mUploadMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInputLayout = findViewById(R.id.input_barcode_layout);
        mInputBarcodeBtn = (Button) findViewById(R.id.input_commt_btn);
        mInputBarcodeEt = (EditText) findViewById(R.id.input_barcode_et);
        starTime = System.currentTimeMillis();
        mWebProgressBar = (ProgressBar) findViewById(R.id.pb_web);

        // 设置浏览器
        mWebview = (cn.cerc.android.view.MyWebView) findViewById(R.id.wv_my);
        mWebview.setProgressBar(mWebProgressBar);
        mWebview.setmTextView((TextView) findViewById(R.id.tv_title));
        mWebview.home();
        mWebview.setListener(new Listener()
        {
            @Override
            public void pageStart()
            {
                ImageView img = (ImageView) mReFreshLayout.findViewById(R.id.iv_refresh_stop);
                img.setImageResource(R.drawable.stop);
                TextView text = (TextView) mReFreshLayout.findViewById(R.id.tv_refresh_stop);
                text.setText(getResources().getString(R.string.stop));
            }

            @Override
            public void pagefinsh()
            {
                ImageView img = (ImageView) mReFreshLayout.findViewById(R.id.iv_refresh_stop);
                img.setImageResource(R.drawable.refresh);
                TextView text = (TextView) mReFreshLayout.findViewById(R.id.tv_refresh_stop);
                text.setText(getResources().getString(R.string.refresh));
                animationColse();
            }

            @Override
            public void pageErro()
            {
                // mFailLayout.setVisibility(View.VISIBLE);
                animationColse();
            }
        });

        mFirstLayout = (LinearLayout) findViewById(R.id.fl_frist);
        mAnimImageView = (ImageView) findViewById(R.id.iv_animination);
        mBtnSettingNetwork = (Button) findViewById(R.id.btn_setting_network);
        mFailLayout = (LinearLayout) findViewById(R.id.ll_web_fail);
        mFarwordLayout = (LinearLayout) findViewById(R.id.ll_farword);
        mBackLayout = (LinearLayout) findViewById(R.id.ll_back);
        mReFreshLayout = (LinearLayout) findViewById(R.id.ll_refresh);
        mHomeLayout = (LinearLayout) findViewById(R.id.ll_home);

        findViewById(R.id.top_left_iv).setOnClickListener(this);
        findViewById(R.id.top_right_iv).setOnClickListener(this);
        mFarwordLayout.setOnClickListener(this);
        mBackLayout.setOnClickListener(this);
        mReFreshLayout.setOnClickListener(this);
        mHomeLayout.setOnClickListener(this);
        mBtnSettingNetwork.setOnClickListener(this);
        mInputBarcodeBtn.setOnClickListener(this);

        AnimationDrawable animDance = (AnimationDrawable) this.mAnimImageView.getBackground();
        animDance.start();
        mWebview.setmInputListener(new InputListener()
        {

            @Override
            public void dobarcode(boolean isShow)
            {
                if (isShow)
                {
                    mHandler.sendEmptyMessage(2); // showInputBarcode
                }
                else
                {
                    mHandler.sendEmptyMessage(3); // hideInputBarcode
                }
            }
        });
        mMoreWindow = new MoreWindow(this, this);
        mInputBarcodeEt.setOnEditorActionListener(new OnEditorActionListener()
        {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_UNSPECIFIED)
                {
                    postBarcode();
                }
                return false;
            }
        });
        mInputBarcodeEt.setOnFocusChangeListener(new OnFocusChangeListener()
        {

            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (mInputLayout.getVisibility() == View.VISIBLE && !mInputBarcodeEt.isFocused())
                {
                    final Handler handler = new FocusHandler(MainActivity.this);
                    handler.postDelayed(new Runnable()
                    {

                        @Override
                        public void run()
                        {
                            handler.sendEmptyMessage(0);
                        }
                    }, 10);

                }
            }
        });
    }

    /**
     * 关闭引导页
     */
    public void animationColse()
    {
        long offestTime = System.currentTimeMillis() - starTime;
        if (offestTime < MIN_TIME && offestTime > 0)
        {
            Timer timer = new Timer();
            timer.schedule(new TimerTask()
            {

                @Override
                public void run()
                {
                    mHandler.sendEmptyMessage(0);
                }
            }, MIN_TIME - offestTime);
        }
        else
        {
            mFirstLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        switch (id)
        {
        case R.id.input_commt_btn:
            postBarcode();
            break;
        case R.id.top_right_iv:
            mMoreWindow.show(v);
            break;
        case R.id.top_setting_ll:
            mMoreWindow.hide();
            Intent intent = new Intent();
            intent.setClass(this, SettingActivity.class);
            startActivity(intent);
            break;
        case R.id.ll_farword:
        case R.id.top_farword_ll:
            mFailLayout.setVisibility(View.GONE);
            mWebview.onFarword();
            mMoreWindow.hide();
            break;
        case R.id.ll_back:
        case R.id.top_back_ll:
            mFailLayout.setVisibility(View.GONE);
            mWebview.onBack();
            mMoreWindow.hide();
            break;
        case R.id.ll_refresh:
        case R.id.top_refresh_ll:
            mFailLayout.setVisibility(View.GONE);
            if (mWebview.isLoadding())
            {
                mWebview.onStop();
                mWebview.setLoadding(false);
            }
            else
            {
                mWebview.onRefresh();
            }
            mMoreWindow.hide();
            break;
        case R.id.ll_home:
        case R.id.top_left_iv:
            mFailLayout.setVisibility(View.GONE);
            mWebview.home();
            break;
        case R.id.btn_setting_network:
            // 判断手机系统的版本 即API大于10 就是3.0或以上版本
            if (android.os.Build.VERSION.SDK_INT > 10)
            {
                intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
            }
            else
            {
                intent = new Intent();
                ComponentName component = new ComponentName("com.android.settings",
                        "com.android.settings.WirelessSettings");
                intent.setComponent(component);
                intent.setAction("android.intent.action.VIEW");
            }
            startActivity(intent);
            break;
        }
    }

    private void postBarcode()
    {
        String string = mInputBarcodeEt.getText().toString();
        if (TextUtils.isEmpty(string))
        {
            Toast.makeText(this, "请输入扫描码", Toast.LENGTH_SHORT).show();
            return;
        }
        mWebview.postBarcode(string);
        mInputBarcodeEt.clearFocus();
        mInputBarcodeEt.requestFocus();
        mInputBarcodeEt.setSelectAllOnFocus(true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            String url = mWebview.getUrl();
            // Toast.makeText(this, url, Toast.LENGTH_LONG).show();
            if (url.contains("TFrmWelcome") || url.contains("TFrmDefault") || url.contains("/form/?device"))
                clickExitButton();
            else
            {
                if (mWebview.canGoBack())
                {
                    mWebview.onBack();
                    return true;
                }
                else
                    clickExitButton();
            }

        }
        else if (keyCode == KeyEvent.KEYCODE_ENTER && mInputLayout.getVisibility() == View.VISIBLE)
        {
            postBarcode();
        }

        return super.onKeyDown(keyCode, event);
    }

    // 用户点击了返回按钮
    private void clickExitButton()
    {
        AlertDialog.Builder builder = new Builder(this);
        builder.setTitle("提示");
        builder.setMessage("确认退出吗？");

        builder.setPositiveButton("确认", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                MainActivity.this.finish();
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mInputBarcodeEt.clearFocus();
        mInputBarcodeEt.requestFocus();
        mInputBarcodeEt.setSelectAllOnFocus(true);
        hideSoftInput();
    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftInput()
    {
        InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (mInputMethodManager.isActive())
        {
            try
            {
                mInputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
            catch (Exception e)
            {
                // e.printStackTrace();
            }
        }
    }

    static class FocusHandler extends Handler
    {

        MainActivity activity;

        public FocusHandler(MainActivity activity)
        {
            this.activity = activity;
        }

        public void handleMessage(Message msg)
        {
            activity.mInputBarcodeEt.requestFocus();
        };
    }

    static class ViewHandler extends Handler
    {
        MainActivity activity;

        ViewHandler(MainActivity activity)
        {
            this.activity = activity;
        }

        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
            case 0:
                activity.mFirstLayout.setVisibility(View.GONE);
                break;
            case 1:
                // 显示更新对话框
                String[] arrayResult = msg.getData().getStringArray("array");
                new UpdateDialog(activity, arrayResult);
            case 2:
                activity.showBarcodeInput();
                break;
            case 3:
                activity.hideBarcodeInput();
                break;
            default:
                break;
            }
        }
    }

    // 显示条码输入框 by ZhangGong
    private void showBarcodeInput()
    {
        this.mInputLayout.setVisibility(View.VISIBLE);
        this.mInputBarcodeEt.requestFocus();
        this.mInputBarcodeEt.setSelectAllOnFocus(true);
        this.hideSoftInput();
    }

    // 隐藏条码输入框 by ZhangGong
    private void hideBarcodeInput()
    {
        this.mInputLayout.setVisibility(View.GONE);
        this.hideSoftInput();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        if (requestCode == FILECHOOSER_RESULTCODE)
        {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
    }
}

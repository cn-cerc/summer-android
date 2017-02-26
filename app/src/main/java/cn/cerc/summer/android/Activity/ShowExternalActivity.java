package cn.cerc.summer.android.Activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huagu.ehealth.R;

import cn.cerc.summer.android.Utils.Constans;
import cn.cerc.summer.android.Utils.ScreenUtils;
import cn.cerc.summer.android.View.MyWebView;
import cn.cerc.summer.android.View.pullTorefreshwebView.PullToRefreshBase;
import cn.cerc.summer.android.View.pullTorefreshwebView.PullToRefreshWebView;

public class ShowExternalActivity extends BaseActivity implements View.OnClickListener {

    private ImageView back;
    private TextView title_text;

    private ProgressBar progress;

    private PullToRefreshWebView pullTorefreshwebView;
    private WebView webview;

    private String showUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_external);

        if (getIntent().hasExtra("url"))
            showUrl = getIntent().getStringExtra("url");

        InitView();
    }

    private void InitView() {
        back = (ImageView) this.findViewById(R.id.back);
        title_text = (TextView) this.findViewById(R.id.title);
        back.setOnClickListener(this);

        progress = (ProgressBar) this.findViewById(R.id.progress);

        pullTorefreshwebView = (PullToRefreshWebView) this.findViewById(R.id.pullTorefreshwebView);
        //下拉刷新
        pullTorefreshwebView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<MyWebView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<MyWebView> refreshView) {
                webview.reload();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<MyWebView> refreshView) {
            }
        });
        webview = pullTorefreshwebView.getRefreshableView();
        webview.loadUrl(showUrl);
        webview.getSettings().setTextZoom(settingShared.getInt(Constans.SCALE_SHAREDKEY, ScreenUtils.getScales(this, ScreenUtils.getInches(this))));

        webview.setWebViewClient(new WebViewClient());

        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                title_text.setText(title);
                super.onReceivedTitle(view, title);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back://返回按钮
                finish();
                break;
            default:
                break;
        }
    }


}

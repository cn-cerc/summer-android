package cn.cerc.summer.android.View.pullTorefreshwebView;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

import cn.cerc.summer.android.View.MyWebView;

/**
 * 封装了WebView的下拉刷新
 * 
 * @author Li Hong
 * @since 2013-8-22
 */
public class PullToRefreshWebView extends PullToRefreshBase<MyWebView> {
    /**
     * 构造方法
     * 
     * @param context context
     */
    public PullToRefreshWebView(Context context) {
        this(context, null);
    }
    
    /**
     * 构造方法
     * 
     * @param context context
     * @param attrs attrs
     */
    public PullToRefreshWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    /**
     * 构造方法
     * 
     * @param context context
     * @param attrs attrs
     * @param defStyle defStyle
     */
    public PullToRefreshWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * @see cn.cerc.summer.android.View.pullTorefreshwebView.PullToRefreshBase#createRefreshableView(android.content.Context, android.util.AttributeSet)
     */
    @Override
    protected MyWebView createRefreshableView(Context context, AttributeSet attrs) {
        MyWebView webView = new MyWebView(context);
        return webView;
    }

    /**
     * @see cn.cerc.summer.android.View.pullTorefreshwebView.PullToRefreshBase#isReadyForPullDown()
     */
    @Override
    protected boolean isReadyForPullDown() {
        return mRefreshableView.getScrollY() == 0;
    }

    /**
     * @see cn.cerc.summer.android.View.pullTorefreshwebView.PullToRefreshBase#isReadyForPullUp()
     */
    @Override
    protected boolean isReadyForPullUp() {
        double exactContentHeight = Math.floor(mRefreshableView.getContentHeight() * mRefreshableView.getScale());
        return mRefreshableView.getScrollY() >= (exactContentHeight - mRefreshableView.getHeight());
    }
}

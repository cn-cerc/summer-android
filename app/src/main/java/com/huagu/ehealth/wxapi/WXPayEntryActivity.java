package com.huagu.ehealth.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;




/**
 * 微信支付回调Activity
 */


public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {


	private IWXAPI wxAPI;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		wxAPI = WXAPIFactory.createWXAPI(this, WeiXinPresenter.APP_ID);
		wxAPI.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent){
		super.onNewIntent(intent);
		setIntent(intent);
		wxAPI.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq arg0) {
	}

	@Override
	public void onResp(BaseResp resp) {
		MLog.i("微信支付回调..", "ansen onResp");
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX){//微信支付回调
			if(resp.errCode==BaseResp.ErrCode.ERR_OK){//微信支付成功
				Intent intent = new Intent();
				intent.setAction(APIDefineConst.BROADCAST_ACTION_WEIXIN_PAY);
				LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
				lbm.sendBroadcast(intent);
				//成功
//				Toast.makeText(this,R.string.wxpay_success, 0).show();
			}else{
//				Toast.makeText(this,R.string.wxpay_success, 0).show();
			}
		}
		finish();
	}





}
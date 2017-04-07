package com.huagu.ehealth.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;


/**
 * 微信登陆分享回调Activity
 */


public class WXEntryActivity extends Activity implements IWXAPIEventHandler {




	private IWXAPI wxAPI;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(MLog.debug)
			System.out.println("WXEntryActivity onCreate");

		wxAPI = WXAPIFactory.createWXAPI(this,WeiXinPresenter.APP_ID,true);
		wxAPI.registerApp(WeiXinPresenter.APP_ID);

		wxAPI.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent){
		super.onNewIntent(intent);
		wxAPI.handleIntent(getIntent(),this);
		if(MLog.debug)
			System.out.println("WXEntryActivity onNewIntent");
	}

	@Override
	public void onReq(BaseReq arg0) {
		if(MLog.debug)
			System.out.println("WXEntryActivity onReq:"+arg0);
		if(MLog.debug)
			Toast.makeText(this, "onReq 方法运行", 0).show();
	}

	@Override
	public void onResp(BaseResp resp){
		MLog.d("ansen", "onResp.....");
		if(MLog.debug)
			Toast.makeText(this,"onResp 方法运行", 0).show();
		if(resp.getType()==ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX){//分享
			switch (resp.errCode){
				case BaseResp.ErrCode.ERR_OK:
					if(MLog.debug)
						Toast.makeText(WXEntryActivity.this, "分享成功!", Toast.LENGTH_SHORT).show();
					break;
				case BaseResp.ErrCode.ERR_USER_CANCEL:
//                Toast.makeText(WXEntryActivity.this, "分享取消!", Toast.LENGTH_SHORT).show();
					break;
				case BaseResp.ErrCode.ERR_AUTH_DENIED:

					break;
			}
			Intent intent = new Intent();
			intent.setAction(APIDefineConst.BROADCAST_ACTION_WEIXIN_SHARE);
			LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
			lbm.sendBroadcast(intent);
		}else if(resp.getType()==ConstantsAPI.COMMAND_SENDAUTH){//登陆发送广播
			SendAuth.Resp authResp = (SendAuth.Resp) resp;
			String code = authResp.code;
			Intent intent = new Intent();
			intent.setAction(APIDefineConst.BROADCAST_ACTION_WEIXIN_TOKEN);
			intent.putExtra("errCode", authResp.errCode);
			if (authResp.errCode == BaseResp.ErrCode.ERR_OK){//用户同意
				intent.putExtra("code", code);
			}

			if(MLog.debug)
				Toast.makeText(this, "WXEntryActivity 发送登陆广播!!!!", 0).show();
			if (android.os.Build.VERSION.SDK_INT >= 12) {
				intent.setFlags(32);//3.1以后的版本需要设置Intent.FLAG_INCLUDE_STOPPED_PACKAGES
			}
			LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
			lbm.sendBroadcast(intent);
		}
		finish();
	}








}
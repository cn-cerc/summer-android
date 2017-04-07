package com.huagu.ehealth.wxapi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tencent.mm.sdk.modelbase.BaseResp;

/**
 * Created by admin on 2017/4/7.
 */






//登陆广播监听内部类  如果接收到了广播就去获取微信token

public class WXEntryReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent){
        MLog.i("WXEntryReceiver", "接收微信登陆广播");
        if(MLog.debug)
            showToast("接收微信登陆广播");
        if(intent.getAction().equals(APIDefineConst.BROADCAST_ACTION_WEIXIN_TOKEN)){
            int errCode = intent.getExtras().getInt("errCode");
            if(MLog.debug)
                System.out.println("获取错误码："+errCode);
            if(errCode== BaseResp.ErrCode.ERR_USER_CANCEL||errCode==BaseResp.ErrCode.ERR_AUTH_DENIED){
                requestDataFinish();
            }else{
                String code = intent.getExtras().getString("code");
                xinTestPresenter.getAccessToken(code);
            }
        }
    }








}

package com.huagu.ehealth.wxapi;

/**
 * Created by admin on 2017/4/7.
 */


import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.googlecode.leptonica.android.Constants;
import com.huagu.ehealth.R;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXVideoObject;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Handler;

import cn.cerc.summer.android.Activity.BaseActivity;
import cn.cerc.summer.android.Utils.FileUtil;

/**
 * 微信分享,登陆,支付
 */


public class WeiXinPresenter extends Presenter{


    public static final int IMAGE_SIZE=32768;//微信分享图片大小限制
    public static final String APP_ID = "";//应用唯一标识，在微信开放平台提交应用审核通过后获得
    public static final String SECRET="";//应用密钥AppSecret，在微信开放平台提交应用审核通过后获得

    private IWXAPI wxAPI;
    private  IView iView;
    private  IUserController userController;

    @Override
    public IView getIView() {
        return iView;
    }

    public WeiXinPresenter(Context context){
        if(context!=null && context instanceof  IView)
            iView =(IView) context;
        if(wxAPI==null){
            wxAPI = WXAPIFactory.createWXAPI(context,APP_ID,true);
            wxAPI.registerApp(APP_ID);
        }
        if(null==userController)
            userController=ControllerFactory.getUserController();
    }


    /**
     * 微信登陆(三个步骤)
     * 1.微信授权登陆
     * 2.根据授权登陆code 获取该用户token
     * 3.根据token获取用户资料
     * @param activity
     */
    public void login(){
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = String.valueOf(System.currentTimeMillis());
        wxAPI.sendReq(req);
    }


    /**
     * 获取微信访问token
     */
    public void getAccessToken(String code){
        if(!userController.isLogin()){//没有登陆的情况用第三方登陆
            userController.getWeiXinAccessToken(APP_ID,SECRET,code,new RequestDataCallback<WeiXinToken>(){
                @Override
                public void dataCallback(WeiXinToken obj){
                    if(obj!=null){
                        if(obj.getErrcode()==0){
                            if(MLog.debug)
                                iView.showToast("授权用户唯一标识:"+obj.getOpenid());
                            getWeiXinUserInfo(obj);
                        }else{
                            iView.showToast(obj.getErrmsg());
                        }
                    }else{

                    }
                }
            });
        }else{//用户已登陆

        }
    }


    /**
     * 获取微信用户信息
     */
    private void getWeiXinUserInfo(final WeiXinToken obj){
        userController.getWeiXinUserInfo(obj.getAccess_token(), obj.getOpenid(), new RequestDataCallback<RegisterB>() {
            @Override
            public void dataCallback(RegisterB registerB){
                registerB.setAccess_token(obj.getAccess_token());
                registerB.setToken_expire_at(obj.getExpires_in());
                if(registerB.getErrcode()==0){
                    registerB.setThird_type_name(Constants.WEI_XIN);
                    thirdLogin(registerB);
                }else{
                    iView.showToast(registerB.getErrmsg());
                }
            }
        });
    }


    /**
     * 调用我们自己的服务器进行登录
     * @param registerB
     */
    private void thirdLogin(RegisterB registerB){
        userController.thirdAuth(registerB,new RequestDataCallback<UserP>(){
            @Override
            public void dataCallback(UserP user){
                if(checkCallbackData(user, true)){
                    if(user.getError()==user.ErrorNone){
                        iView.showToast(R.string.login_success);
                        getAppController().sendLoginChangeIntent();
                        userController.saveLoginUser(user, FileUtil.getFilePath());
                        ((ILoginView)iView).toMain();
                    }else{
                        iView.showToast(user.getError_reason());
                    }
                }
            }
        });
    }


    /**
     * 微信分享
     * @param friendsCircle  是否分享到朋友圈
     */
    public void share(final boolean friendsCircle,final  VideoB videoB){
        new LoadPicThread(videoB.getCover_url(),new Handler(){
            @Override
            public void handleMessage(Notification.MessagingStyle.Message msg) {
                byte[] bytes=(byte[]) msg.obj;
                if(bytes.length>IMAGE_SIZE){
                    iView.showToast(R.string.image_no_big);
                    return;
                }
                System.out.println("图片长度："+bytes.length);
                WXVideoObject videoObject = new WXVideoObject();//视频类型
                videoObject.videoUrl = videoB.getShare_url() + Constants.WEI_XIN + "&share_from="+com.kaka.utils.Constants.ANDROID;// 视频播放url
                WXMediaMessage wxMessage = new WXMediaMessage(videoObject);
                wxMessage.title = videoB.getContent();
                wxMessage.thumbData = bytes;
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                //transaction字段用于唯一标识一个请求
                req.transaction = String.valueOf(videoB.getId() + System.currentTimeMillis());
                req.message = wxMessage;
                req.scene = friendsCircle ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
                wxAPI.sendReq(req);
            }
        }).start();
    }

    private class LoadPicThread extends Thread{
        private String url;
        private Handler handler;
        public LoadPicThread(String url,Handler handler){
            this.url=url;
            this.handler=handler;
        }

        @Override
        public void run(){
            try {
                URL picurl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection)picurl.openConnection(); // 获得连接
                conn.setConnectTimeout(6000);//设置超时
                conn.setDoInput(true);
                conn.setUseCaches(false);//不缓存
                conn.connect();
                Bitmap bmp= BitmapFactory.decodeStream(conn.getInputStream());

                ByteArrayOutputStream output = new ByteArrayOutputStream();

                bmp.compress(Bitmap.CompressFormat.JPEG, 100, output);
                int options = 100;
                while (output.toByteArray().length > IMAGE_SIZE && options != 10) {
                    output.reset();  // 清空baos
                    bmp.compress(Bitmap.CompressFormat.JPEG, options, output);// 这里压缩options%，把压缩后的数据存放到baos中
                    options -= 10;
                }

                bmp.recycle();
                byte[] result = output.toByteArray();
                output.close();

                Message message=handler.obtainMessage();
                message.obj=result;
                message.sendToTarget();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //检查微信是否安装
    public boolean isWXAppInstalled(){
        return wxAPI.isWXAppInstalled();
    }


    public void wxPay(final BaseActivity activity, String order_id, String payType){
        activity.showProgress("");
        ControllerFactory.getWalletsController().getPayments(order_id, payType, new RequestDataCallback<PaymentsP>() {
            @Override
            public void dataCallback(PaymentsP obj) {
                if(checkCallbackData(obj, true)){
                    if(obj.getError()==obj.ErrorNone){
                        PayReq req = new PayReq();//待修改

                        req.appId = obj.getAppid();
                        req.nonceStr=obj.getNoncestr();
                        req.packageValue=obj.getPackage_value();
                        req.sign=obj.getSign();
                        req.partnerId=obj.getPartnerid();
                        req.prepayId=obj.getPrepayid();
                        req.timeStamp=obj.getTimestamp();

                        wxAPI.registerApp(obj.getAppid());
                        wxAPI.sendReq(req);

                        MLog.i("ansen", "开始进行微信支付..");
                        iView.showToast("开始进行微信支付..");
                    }
                }else{
                    iView.showToast(obj.getError_reason());
                }
                activity.hideProgress();
            }
        });
    }







}

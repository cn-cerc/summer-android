package com.yt.hz.financial.argame;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.easy.occlient.OCARAsset;
import com.easy.occlient.OCARBinding;
import com.easy.occlient.OCARTarget;
import com.easy.occlient.OCClient;
import com.easy.occlient.OCPreload;
import com.easy.occlient.OCSchema;
import com.easy.occlient.net.AsyncCallback;
import com.easy.occlient.net.AsyncCallbackImp;
import com.yt.hz.financial.argame.easyar.MessageConnection;
import com.yt.hz.financial.argame.easyar.MessageID;
import com.yt.hz.financial.argame.easyar.SamplePlayerViewWrapper;
import com.yt.hz.financial.argame.permission.PermissionDenied;
import com.yt.hz.financial.argame.permission.PermissionHelper;
import com.yt.hz.financial.argame.permission.PermissionPermanentDenied;
import com.yt.hz.financial.argame.permission.PermissionSucceed;
import com.yt.hz.financial.argame.util.OCUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import cn.easyar.EasyARDictionary;
import cn.easyar.Message;
import cn.easyar.MessageClient;
import cn.easyar.PlayerView;
import cn.easyar.player.OnReceivedCallback;


public class LoaderPreloadIDActivity extends Activity implements View.OnClickListener, SamplePlayerViewWrapper.RecorderLisener {
    private final String TAG = "LoaderPreloadIDActivity";
    private final String SERVER_ADDRESS = "https://aroc-cn1.easyar.com/";
    private final String OCKEY = "e18735e3f1cdf73874b649ad2475b9e5";
    private final String OCSCRET = "949fd185718ab357ff2e86f5cb59a31e42eaf041663b6eee056a7d0e485b200c";
    private String mTheStartSchemaID = "ed04f86f-7439-4b06-9f22-6111cc2e9949";//947214e8-7ffb-4e1d-ae60-917947a99b36
    private OCClient mTheOCClient;
    private SamplePlayerViewWrapper mPlayerView = null;
    private MessageClient mTheMessageClient = null;
    private OCClient mOcClient;
    private final HashMap<String, OCARBinding> mTheARBindingsDict = new HashMap<>();
    private final int PERMISSION_CODE = 1;
    private String mDesc;
    private boolean mShowLuotuoTips=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader_prelodeid);
        Intent intent = getIntent();
        //mTheStartSchemaID = intent.getStringExtra("id");
        //mDesc = intent.getStringExtra("desc");
        ViewGroup previewGroup = (ViewGroup) findViewById(R.id.preview);

        mPlayerView = new SamplePlayerViewWrapper(this);
        previewGroup.addView(
                mPlayerView.getPlayerView(),
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
        requestPermission();

    }



    @Override
    protected void onResume() {
        super.onResume();
        if (mPlayerView != null) {
            mPlayerView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPlayerView != null) {
            mPlayerView.onPause();
        }
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayerView!=null){
            mPlayerView.dispose();
        }
    }

    private void conectionOCClient() {
        mTheOCClient = new OCClient();
        mOcClient = mTheOCClient;
        mOcClient.setServerAddress(SERVER_ADDRESS);
        mOcClient.setServerAccessKey(OCKEY, OCSCRET);

        mTheMessageClient = MessageClient.create(mPlayerView.getPlayerView(), "Native", new LoaderPreloadIDActivity.OnReceivedCallbackImp());
        mTheMessageClient.setDest("TS");
        // 2. 加载某个StartSchema
        mOcClient.loadStartSchema(mTheStartSchemaID, new AsyncCallbackImp<OCSchema>() {
            @Override
            public void onSuccess(OCSchema result) {

                Log.e("yuan",result.toString());
                ArrayList<OCARBinding> bindings = result.getArBindings();
                assert (!bindings.isEmpty());
                for (OCARBinding binding : bindings) {
                    mTheARBindingsDict.put(binding.getTarget(), binding);
                }
//                loadARAsset(bindings.get(0).getContentId());
                mOcClient.preloadForStartSchema(mTheStartSchemaID, new AsyncCallbackImp<OCPreload>() {
                    @Override
                    public void onSuccess(OCPreload result) {
                        Log.e("yuan","onSuccess");
                        ArrayList<OCARTarget> targets = result.getTargets();
                        assert (!targets.isEmpty());
                        for (OCARTarget target : targets) {
                            Log.e("yuan",target.getImageUrl());
                            Log.e("yuan",target.getLocalAbsolutePath());
                            mOcClient.downloadImageForARTarget(target, new AsyncCallbackImp<OCARTarget>() {
                                @Override
                                public void onSuccess(OCARTarget target) {
                                    // TODO: 向内容端发送消息，通知Sense，识别图来了
                                    //MessageConnection.getInstent().loadTaget(target,mTheMessageClient);
                                    sendTargetStrame(target.getTargetId(),new File(target.getLocalAbsolutePath()));
                                }

                            });
                        }
                    }

                });

            }

            @Override
            public void onFail(Throwable t) {
                super.onFail(t);

            }
        });
    }
    /**
     * 加载识别图
     * @param id
     * @param file
     */
    public void sendTargetStrame(String id, File file){
        // TODO: 向内容端发送消息，通知Sense，识别图来了
        byte[] theImage = OCUtil.getInstent().readFile(file);
        if(theImage==null){
            return;
        }
        MessageConnection.getInstent().loadTaget(theImage, id,mTheMessageClient);
    }
    @Override
    public void onClick(View view) {

    }


    @Override
    public void recorderSetText(String text) {

    }

    @Override
    public void recorderSetEnabled(boolean flag) {

    }

    private class OnReceivedCallbackImp implements OnReceivedCallback {
        @Override
        public void onReceived(String s, final Message message) {
            log(message.getId()+"");
            if (MessageID.MSG_ID_FOUNDTARGET.getId() == message.getId()) {
                log(message.getId()+"MSG_ID_FOUNDTARGET");
                System.out.println("yanjin------message.getId()---"+message.getId());
                EasyARDictionary theBody = message.getBody();
                assert (null != theBody);
                final String targetId = theBody.getString("targetId");
                final OCARBinding theARBinding = mTheARBindingsDict.get(targetId);

                if(mShowLuotuoTips){
                    optionUIMessage();
                }
                if (null == theARBinding)
                    return;

                mOcClient.loadARAsset(theARBinding.getContentId(), new AsyncCallback<OCARAsset>() {
                    @Override
                    public void onSuccess(OCARAsset asset) {
                        final String assetLocalAbsolutePath = asset.getLocalAbsolutePath();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                        mPlayerView.loadPackage(assetLocalAbsolutePath, new PlayerView.OnLoadPackageFinish() {
                                            @Override
                                            public void onFinish() {
                                                System.out.println("yanjin------assetLocalAbsolutePath---"+assetLocalAbsolutePath);
                                            }
                                        });
                                        if(!mShowLuotuoTips){
                                            mShowLuotuoTips = true;
                                            optionUIMessage();
                                        }
                                    }
                        });

                    }

                    @Override
                    public void onFail(Throwable t) {
                        Log.d(TAG, "loadARAsset onFail: " + t.toString());
                    }

                    @Override
                    public void onProgress(String taskName, final float progress) {
                        if (null != taskName && taskName.equals("download")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                }
                            });
                        }
                    }
                });
            }else if(MessageID.Drawing.getId() == message.getId()){
            } else {
                Log.i(TAG, String.format("消息: error message: ", message.getId()));
            }

        }
    }

    private void optionUIMessage() {
        /*if(TextUtils.equals(mDesc,"骆驼")){
            mLlLuotuoOption.setVisibility(View.VISIBLE);
        }*/
    }


    private void requestPermission() {
        PermissionHelper.with(this).requestCode(PERMISSION_CODE).requestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA
        ).request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.requestPermissionsResult(this, requestCode, permissions, grantResults);
    }
    public void log(String msg){
        Log.e("yuan",msg);
    }
    @PermissionDenied(requestCode = PERMISSION_CODE)
    private void onPermissionDenied() {
        Toast.makeText(this, "您拒绝了开启权限,可去设置界面打开", Toast.LENGTH_SHORT).show();
    }


    @PermissionPermanentDenied(requestCode = PERMISSION_CODE)
    private void onPermissionPermanentDenied() {
        Toast.makeText(this, "您选择了永久拒绝,可在设置界面重新打开", Toast.LENGTH_SHORT).show();
    }

    @PermissionSucceed(requestCode = PERMISSION_CODE)
    private void onPermissionSuccess() {
        try {
            startAR();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void creditBillJson(String json){
        EasyARDictionary theBody = new EasyARDictionary();
        theBody.setString("json",json);
        mTheMessageClient.send(new Message(MessageID.creditBillJson.getId(), theBody));
    }
    private void startAR() throws IOException {


        com.easy.occlient.OCUtil.getInstent().loaderEZP(com.easy.occlient.OCUtil.getInstent().EZP_NAME,getAssets().open(com.easy.occlient.OCUtil.getInstent().EZP_NAME),new com.easy.occlient.LoaderEZPLisener() {
            @Override
            public void onSucess(String path) {
                loadAr(path);
                mPlayerView.getReadyRecorder();
            }

            @Override
            public void fail() {

            }
        });
//        OCUtil.getInstent().loaderEZP(OCUtil.getInstent().EZP_NAME,getAssets().open(OCUtil.getInstent().EZP_NAME),new LoaderEZPLisener() {
//            @Override
//            public void onSucess(String path) {
//                loadAr(path);
//
//            }
//
//            @Override
//            public void fail() {
//
//            }
//        });
    }

    public void loadAr(String path) {
        mPlayerView.loadPackage(path, new PlayerView.OnLoadPackageFinish() {
            @Override
            public void onFinish() {
                conectionOCClient();
            }

        });
    }

}

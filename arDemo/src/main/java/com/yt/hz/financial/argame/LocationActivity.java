package com.yt.hz.financial.argame;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easy.occlient.LoaderEZPLisener;
import com.easy.occlient.OCARAsset;
import com.easy.occlient.OCARBinding;
import com.easy.occlient.OCClient;
import com.easy.occlient.OCUtil;
import com.easy.occlient.net.AsyncCallback;
import com.google.gson.Gson;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.cloud.util.ContactManager;
import com.yt.hz.financial.argame.bean.AnswerBean;
import com.yt.hz.financial.argame.bean.LocationMsgBean;
import com.yt.hz.financial.argame.easyar.MessageID;
import com.yt.hz.financial.argame.easyar.SamplePlayerViewWrapper;
import com.yt.hz.financial.argame.permission.PermissionDenied;
import com.yt.hz.financial.argame.permission.PermissionHelper;
import com.yt.hz.financial.argame.permission.PermissionPermanentDenied;
import com.yt.hz.financial.argame.permission.PermissionSucceed;
import com.yt.hz.financial.argame.util.CustomSensorManager;
import com.yt.hz.financial.argame.util.FucUtil;
import com.yt.hz.financial.argame.util.IatSettings;
import com.yt.hz.financial.argame.util.JsonParser;
import com.yt.hz.financial.argame.util.LocationUtil;
import com.yt.hz.financial.argame.util.RequestUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import cn.easyar.EasyARDictionary;
import cn.easyar.Message;
import cn.easyar.MessageClient;
import cn.easyar.PlayerView;
import cn.easyar.player.OnReceivedCallback;


public class LocationActivity extends Activity implements View.OnClickListener, SamplePlayerViewWrapper.RecorderLisener {
    private final String TAG = "LoaderPreloadIDActivity";
    private final String SERVER_ADDRESS = "https://aroc-cn1.easyar.com/";
    private final String OCKEY = "0560a9dab192c15b15d7921c5091dc55";
    private final String OCSCRET = "3314f279e3b94acefeb904794aadbc2937c11cb8714572f40ee5c6ebf9f52421";
    private String mArID ="5a594631-8202-4bbb-8dc8-150c6a773a36";
    private SamplePlayerViewWrapper mPlayerView = null;
    MessageClient mTheMessageClient = null;
    private OCClient mOcClient;
    private final HashMap<String, OCARBinding> mTheARBindingsDict = new HashMap<>();
    private final int PERMISSION_CODE = 1;
    private Button mButtonRecorder;
    private ImageView mIvSnapShot;

    private Button btnSearch;
    private RelativeLayout rlLocation,rlHome,rlSearch;
    private TextView tvLoactionName,tvLocationDetail,tvSearch;

    private RelativeLayout rlFood,rlHotle,rlSuperMark,rlBus,rlATM,rlTravel,rlHouse,rlWC;

    private EditText etSearch;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        CustomSensorManager.getInstance(this).setOnAnglaChanged(new CustomSensorManager.OnAnglaChanged() {
            @Override
            public void onChanged(float angla) {
                mAngla = angla;
                if (mTheMessageClient!=null) {
                    EasyARDictionary theBody2 = new EasyARDictionary();
                    theBody2.setFloat("y", -mAngla);
                    mTheMessageClient.send(new Message(MessageID.yOrientation.getId(), theBody2));
                }
            }
        });
        Intent intent = getIntent();
        //mArID = intent.getStringExtra("id");

        rlFood = findViewById(R.id.rl_search_food);
        rlFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.setText("美食");
                tvSearch.performClick();
            }
        });
        rlHotle = findViewById(R.id.rl_search_hotel);
        rlHotle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.setText("酒店");
                tvSearch.performClick();
            }
        });
        rlSuperMark = findViewById(R.id.rl_search_supermarkrt);
        rlSuperMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.setText("超市");
                tvSearch.performClick();
            }
        });
        rlBus = findViewById(R.id.rl_search_bus);
        rlBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.setText("公交");
                tvSearch.performClick();
            }
        });
        rlATM = findViewById(R.id.rl_search_atm);
        rlATM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.setText("ATM");
                tvSearch.performClick();
            }
        });
        rlTravel = findViewById(R.id.rl_search_travel);
        rlTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.setText("景点");
                tvSearch.performClick();
            }
        });
        rlHouse = findViewById(R.id.rl_search_house);
        rlHouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.setText("住宅");
                tvSearch.performClick();
            }
        });
        rlWC = findViewById(R.id.rl_search_wc);
        rlWC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.setText("厕所");
                tvSearch.performClick();
            }
        });
        ViewGroup previewGroup = (ViewGroup) findViewById(R.id.preview);

        mPlayerView = new SamplePlayerViewWrapper(this);
        previewGroup.addView(
                mPlayerView.getPlayerView(),
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
        requestPermission();

        btnSearch = findViewById(R.id.btn_search);
        etSearch = findViewById(R.id.et_search);
        tvSearch = findViewById(R.id.tv_search);
        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //搜索按钮点击
                String key = etSearch.getText().toString();
                if (key!=null&&!key.equals("")) {
                    requestLocation(key);
                }
                else {
                    showToast("无效输入！");
                }
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlHome.setVisibility(View.GONE);
                rlSearch.setVisibility(View.VISIBLE);
                rlLocation.setVisibility(View.GONE);
            }
        });
        rlLocation = findViewById(R.id.rl_location);
        tvLoactionName = findViewById(R.id.tv_location_name);
        tvLocationDetail = findViewById(R.id.tv_location_detail);

        rlHome = findViewById(R.id.rl_home);
        rlSearch = findViewById(R.id.rl_search);

    }

    private void showToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    private void requestLocation(String key){
        RequestUtils.requestBean("http://api.map.baidu.com/place/v2/search?query="+key+"&scope=2&location="+latitude+","+longitude+"&radius=2000&output=json&ak=Wp2U9nyGTbsbmuGWaG70mKNUMIZM0Ybz", new RequestUtils.RequstCallback<String>() {
            @Override
            public void error() {

            }

            @Override
            public void success(String o) {
                Log.e("yuan getText",o);
                locationMsg = o;
                Gson gson = new Gson();
                LocationMsgBean answerBean =  gson.fromJson(o, LocationMsgBean.class);
                Log.e("yuan answerBean",answerBean.getMessage());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rlSearch.setVisibility(View.GONE);
                        rlHome.setVisibility(View.VISIBLE);
                        resetLocation();
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPlayerView != null) {
            mPlayerView.onResume();
        }
        requestCurrentLocation();
    }

    private Handler handler = new Handler();
    private void resetLocation(){
        requestCurrentLocation();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()){
                    return;
                }
                resetLocation();
            }
        },1000);
    }

    private void requestCurrentLocation(){
        if (Build.VERSION.SDK_INT >= 23) {
            //如果用户并没有同意该权限
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //申请权限
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            } else {
                LocationUtil.getCurrentLocation(LocationActivity.this, callBack);
            }
        }
    }

    private double  longitude,latitude;
    private Float mAngla;
    private LocationUtil.LocationCallBack callBack = new LocationUtil.LocationCallBack() {
        @Override
        public void onSuccess(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            if (locationMsg!=null&&!locationMsg.equals("")){
                EasyARDictionary theBody = new EasyARDictionary();
                theBody.setString("value",locationMsg);
                mTheMessageClient.send(new Message(MessageID.locationMsg.getId(), theBody));
                EasyARDictionary theBody1 = new EasyARDictionary();
                theBody1.setFloat("x", (float) latitude);
                theBody1.setFloat("y", (float) longitude);
                mTheMessageClient.send(new Message(MessageID.currentLocation.getId(), theBody1));
            }
            //showToast("经度: " + location.getLongitude() + " 纬度: " + location.getLatitude()+ "\n");
        }

        @Override
        public void onFail(String msg) {
            showToast(msg + "\n");
        }
    };


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
        CustomSensorManager.getInstance(this).cancel();
    }
    private void conectionOCClient() {
        mOcClient = new OCClient();
        mOcClient.setServerAddress(SERVER_ADDRESS);
        mOcClient.setServerAccessKey(OCKEY, OCSCRET);

        loadARID();
    }

    private String locationMsg = "";

    public void loadARID(){

        mOcClient.loadARAsset(mArID, new AsyncCallback<OCARAsset>() {
            @Override
            public void onSuccess(OCARAsset asset) {
                System.out.println("yanjin---loadARAsset---"+asset.toString());
                mTheMessageClient = MessageClient.create(mPlayerView.getPlayerView(), "Native", new LocationActivity.OnReceivedCallbackImp());
                mTheMessageClient.setDest("TS");
                final String assetLocalAbsolutePath = asset.getLocalAbsolutePath();
                        mPlayerView.loadPackage(assetLocalAbsolutePath, new PlayerView.OnLoadPackageFinish() {
                            @Override
                            public void onFinish() {

                            }
                        });
            }

            @Override
            public void onFail(Throwable t) {

            }

            @Override
            public void onProgress(String taskName, final float progress) {
                if (null != taskName && taskName.equals("download")) {
                    //显示进度圈
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //mArScanView.setProgress(progress);
                        }
                    });
                }
            }
        });
    }
    @Override
    public void onClick(View view) {
    }


    @Override
    public void recorderSetText(String text) {
        mButtonRecorder.setText(text);
    }

    @Override
    public void recorderSetEnabled(boolean flag) {
        mButtonRecorder.setEnabled(flag);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private class OnReceivedCallbackImp implements OnReceivedCallback {
        @Override
        public void onReceived(String s, final Message message) {
            if (MessageID.MSG_ID_FOUNDTARGET.getId() == message.getId()) {

            } else if (MessageID.LoadFinish.getId() == message.getId()) {
                log("LoadFinish");
            }else if (MessageID.TargetFound.getId() == message.getId()) {
                log("TargetFound");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });

            }else if (MessageID.ClickTarget.getId() == message.getId()) {
                log("ClickTarget");
                EasyARDictionary theBody = message.getBody();
                assert (null != theBody);
                final String value = theBody.getString("value");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("发现目标！"+value);

                        Gson gson = new Gson();
                        LocationMsgBean.ResultsBean answerBean =  gson.fromJson(value, LocationMsgBean.ResultsBean.class);
                        rlLocation.setVisibility(View.VISIBLE);
                        tvLoactionName.setText(answerBean.getName());
                        tvLocationDetail.setText(answerBean.getAddress());
                        //Log.e("yuan answerBean",answerBean.getMessage());
                    }
                });

            }else {
                Log.i(TAG, String.format("消息: error message: %d", message.getId()));
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LocationActivity.this,message.getId()+"",Toast.LENGTH_SHORT).show();
                }
            });
        }
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


        OCUtil.getInstent().loaderEZP(OCUtil.getInstent().EZP_NAME,getAssets().open(OCUtil.getInstent().EZP_NAME),new LoaderEZPLisener() {
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
//    public void loadAr(String path) {
//        mPlayerView.loadPackage(path, new PlayerView.OnLoadPackageFinish() {
//            @Override
//            public void onFinish() {
//                conectionOCClient();
//            }
//
//        });
//    }

}

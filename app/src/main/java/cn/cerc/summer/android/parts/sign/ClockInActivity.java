package cn.cerc.summer.android.parts.sign;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapOptions;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.mimrc.vine.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import cn.cerc.summer.android.basis.HttpClient;
import cn.cerc.summer.android.core.AMapUtil;
import cn.cerc.summer.android.core.MyApp;
import cn.cerc.summer.android.core.MySession;
import cn.cerc.summer.android.core.PhotoBitmapUtils;
import cn.cerc.summer.android.core.RequestCallback;

/**
 * Created by Administrator on 2018/1/9.
 */

public class ClockInActivity extends AppCompatActivity implements LocationSource, AMap.OnMapClickListener, GeocodeSearch.OnGeocodeSearchListener, AMapLocationListener, View.OnClickListener {
    private MapView mAmapView;
    private LinearLayout mContainerLayout;
    private AMap aMap;
    public static LatLng myLaLn;
    private GeocodeSearch geocoderSearch;
    private ProgressDialog progDialog = null;
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private boolean isFirstLoc = true;
    public AMapLocation NowLocation = null;
    private MarkerOptions markerOption;
    private Marker marker;

    private LatLng latLngSign;
    private LatLng latLngCurrent;
    private TextView text_address;
    private Button btn_PunchClock;
    private Button btn_recording;
    private ImageView imgBack;
    private ImageView image;
    private TextView text_image;
    private RelativeLayout relative_image;
    private final static int TAKE_PICTURE = 1; //相机
    private Uri photoUri = null;
    private File file;
    private boolean isPhotograph = false;
    private String path = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_off);

        initView();
        changeToAmapView(savedInstanceState);
    }

    private void initView() {
        ActivityCompat.requestPermissions(ClockInActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        }, 0x67);
        text_address = (TextView) findViewById(R.id.text_address);
        mContainerLayout = (LinearLayout) findViewById(R.id.mContainerLayout);
        btn_PunchClock = (Button) findViewById(R.id.btn_PunchClock);
        btn_recording = (Button) findViewById(R.id.btn_recording);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        image = (ImageView) findViewById(R.id.image);
        text_image = (TextView) findViewById(R.id.text_image);
        relative_image = (RelativeLayout) findViewById(R.id.relative_image);
        btn_recording.setOnClickListener(this);
        btn_PunchClock.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        text_image.setOnClickListener(this);
        relative_image.setOnClickListener(this);
    }

    private void changeToAmapView(Bundle savedInstanceState) {
        if (myLaLn == null) {
            mAmapView = new MapView(this, new AMapOptions()
                    .camera(new CameraPosition(new LatLng(114.025, 22.540412), 16, 0, 0)));
        } else {
            mAmapView = new MapView(this, new AMapOptions()
                    .camera(new CameraPosition(new LatLng(myLaLn.latitude, myLaLn.longitude), 16, 0, 0)));
        }

        mAmapView.onCreate(savedInstanceState);
        mAmapView.onResume();
        mContainerLayout.addView(mAmapView);

        if (aMap == null) {
            aMap = mAmapView.getMap();
            setUpMap();
            setupLocationStyle();
        }
    }

    private void setUpMap() {
        aMap.setMapType(3);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(1);
        CameraPosition LUJIAZUI = new CameraPosition.Builder().target(new LatLng(22.579652, 113.861323)).zoom(17).bearing(0).tilt(45).build();
        aMap.animateCamera(CameraUpdateFactory.newCameraPosition(LUJIAZUI));
        setupLocationStyle();
        aMap.setOnMapClickListener(this);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        progDialog = new ProgressDialog(this);
    }

    /*
    自定义系统定位蓝点
     */
    private void setupLocationStyle() {
        // 自定义系统定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
                fromResource(R.mipmap.ic_my_location));
        myLocationStyle.strokeWidth(0);
        //隐藏进度圈
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
        aMap.setMyLocationStyle(myLocationStyle);
    }

    /**
     * 拍照获取图片
     */
    private void photograph() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            MyApp.isPermissionsAllGranted(new String[]{Manifest.permission.CAMERA}, 002, this);
            return;
        }
        file = PhotoBitmapUtils.createIconFile();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            photoUri = FileProvider.getUriForFile(this, "com.mimrc.vine.fileprovider", file);//通过FileProvider创建一个content类型的Uri
        } else {
            photoUri = Uri.fromFile(file);
        }
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        }
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);//将拍取的照片保存到指定URI
        startActivityForResult(intent, TAKE_PICTURE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap = null;
                    if (file != null) {
                        try {
                            path = PhotoBitmapUtils.amendRotatePhoto(file.getAbsolutePath(), this);
                            bitmap = BitmapFactory.decodeStream(new FileInputStream(path));
                            isPhotograph = true;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        image.setImageBitmap(bitmap);
                    } else {
                        if (data != null) {
                            if (data.hasExtra("data")) {
                                Log.i("URI", "data is not null");
                                Bitmap bitmap1 = data.getParcelableExtra("data");
                                path = PhotoBitmapUtils.savePhotoToSD(bitmap1, this);
                                isPhotograph = true;
                                image.setImageBitmap(bitmap1);
                            }
                        }
                    }
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAmapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAmapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mAmapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAmapView.onDestroy();
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(ClockInActivity.this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setInterval(60000);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            mlocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (marker != null) {
            marker.remove();
        }
        markerOption = new MarkerOptions().icon(BitmapDescriptorFactory
                .fromResource(R.mipmap.ic_dafult_marker))
                .position(latLng)
                .draggable(true);
        marker = aMap.addMarker(markerOption);
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        latLngCurrent = latLng;
        getAddress(AMapUtil.convertToLatLonPoint(latLng));
    }

    /**
     * 响应逆地理编码
     */
    public void getAddress(final LatLonPoint latLonPoint) {
        showDialog();
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求
    }

    /**
     * 显示进度条对话框
     */
    public void showDialog() {
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在获取地址");
        progDialog.show();
    }

    /**
     * 隐藏进度条对话框
     */
    public void dismissDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        dismissDialog();
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                text_address.setText(result.getRegeocodeAddress().getFormatAddress());
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        markerOption.getPosition(), 15));
            } else {

            }
        } else {
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {

                if (isFirstLoc) {
                    CameraPosition LUJIAZUI = new CameraPosition.Builder().target(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())).zoom(17).bearing(0).tilt(45).build();
                    aMap.animateCamera(CameraUpdateFactory.newCameraPosition(LUJIAZUI));
                    isFirstLoc = false;
                }

                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                NowLocation = aMapLocation;
                myLaLn = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                Log.i("Location", aMapLocation.getLatitude() + "::::" + aMapLocation.getLongitude());
                if (markerOption == null) {
                    String address = NowLocation.getProvince() + NowLocation.getCity() + NowLocation.getDistrict() + NowLocation.getStreet() + NowLocation.getStreetNum() + NowLocation.getAoiName();
                    text_address.setText(address);
                    latLngSign = myLaLn;
                    markerOption = new MarkerOptions().icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                            .position(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()))
                            .draggable(true);
                }

            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
                ActivityCompat.requestPermissions(ClockInActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, 0x67);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_PunchClock:
                float distance = AMapUtils.calculateLineDistance(latLngSign, latLngCurrent);
                if (distance <= 200) {
                    if ("".equals(text_address.getText().toString())) {
                        Toast.makeText(ClockInActivity.this, "定位失败，请检查定位权限！", Toast.LENGTH_SHORT).show();
                    } else if (path == null || !isPhotograph) {
                        Toast.makeText(ClockInActivity.this, "请先进行拍照！", Toast.LENGTH_SHORT).show();
                    } else {
                        String token = null;
                        token = MySession.getInstance().getToken();
                        String client = null;
                        if (token != null && !"".equals(token)) {
                            client = MyApp.getFormUrl("FrmAttendance.clockIn") + String.format("?sid=%s&CLIENTID=%s", token, MyApp.getInstance().getClientId());
                            HttpClient httpClient = new HttpClient("FrmAttendance.clockIn");
                            HashMap<String, String> rf = new HashMap<>();
                            rf.put("Address_", text_address.getText().toString());
                            rf.put("Position_", markerOption.getPosition().longitude + "," + markerOption.getPosition().latitude);
                            rf.put("FileUrl_", path);
                            httpClient.POST(client, rf, new RequestCallback() {
                                @Override
                                public void success(String url, JSONObject json) {
                                    Log.d("print", "success: " + json.toString());
                                    try {
                                        Toast.makeText(ClockInActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void failt(String url, String error) {

                                }

                                @Override
                                public Context getContext() {
                                    return null;
                                }
                            });
                        } else {
                            Toast.makeText(this, "登录信息错误，请重登后重试！", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(this, "请选择当前定位200米之内的位置", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.imgBack:
                finish();
                break;
            case R.id.btn_recording:
                //打卡记录
                break;
            case R.id.text_image:
                photograph();
                break;
            case R.id.relative_image:
                photograph();
                break;
        }
    }
}

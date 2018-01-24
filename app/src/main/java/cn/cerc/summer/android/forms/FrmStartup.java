package cn.cerc.summer.android.forms;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mimrc.vine.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import cn.cerc.summer.android.basis.HttpClient;
import cn.cerc.summer.android.core.Constans;
import cn.cerc.summer.android.core.MyApp;
import cn.cerc.summer.android.forms.view.NavigationChatImageView;
import cn.cerc.summer.android.parts.dialog.DialogUtil;

public class FrmStartup extends AppCompatActivity {
    public static String CACHE_FILE = "cacheFiles";
    public static String IMAGE_STARTIP = "startup"; //广告图片缓存
    public static SharedPreferences settings;
    private final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 11;
    LinearLayout llDialog;
    String resp = null;
    private boolean appUpdateReset = false;
    private FrmStartup instince;
    private Timer timer = new Timer();
    private int MSG_CLIENT = 1;
    private int IMAGE_CLTENT = 2;
    private MyApp myApp;
    private JSONObject json = null;
    private ImageView start_image;
    private View view_Masking;
    private View view_Line;
    private FrameLayout frameLayout;
    private NavigationChatImageView navigationChatImageView;
    private NavigationChatImageView.ImageViewPagerListener PagerListener = new NavigationChatImageView.ImageViewPagerListener() {
        @Override
        public void onPopSelected(int time) {
            startMainForm(time);
        }
    };
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_CLIENT) {
                resp = (String) msg.obj;
                if (resp == null) {
                    showError("出现错误！", "无法取得后台服务，请稍后再试！");
                    return;
                }
                String err = null;
                try {
                    json = new JSONObject(resp);
                    if (json.has("result")) {
                        if (json.getBoolean("result")) {
                            instince.loadConfig(json);
                        } else {
                            err = json.getString("message");
                        }
                    } else {
                        err = "取得后台服务异常，请稍后再试！";
                    }
                } catch (JSONException e) {
                    showError("出现错误！", resp);
                } catch (Exception e) {
                    err = e.getMessage();
                }
                if (err != null) {
                    showError("出现错误！", err);
                }
            }
        }
    };

    public static boolean isCachePathFileExist(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    private void showError(String errtitle, String errText) {
        llDialog.setVisibility(View.VISIBLE);
        view_Masking.setVisibility(View.VISIBLE);
        view_Line.setVisibility(View.VISIBLE);
        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText(errtitle);
        TextView tvReadme = (TextView) findViewById(R.id.tvReadme);
        tvReadme.setText(errText);
        Button btnOK = (Button) findViewById(R.id.btnOk);
        btnOK.setVisibility(View.GONE);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        GradientDrawable drawable = new GradientDrawable();
        btnCancel.setBackgroundDrawable(drawable);
        btnCancel.setText("确定");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //读取配置
    private void loadConfig(final JSONObject json) throws JSONException, PackageManager.NameNotFoundException {
        final MyApp myApp = MyApp.getInstance();
        myApp.loadConfig(json);

        //检测是否有新的版本
        final String oldVersion = myApp.getCurrentVersion(this);
        if (oldVersion.equals(myApp.getAppVersion())) {
            loadImageView();
            return;
        }

        //发现有新版本
        llDialog.setVisibility(View.VISIBLE);
        view_Masking.setVisibility(View.VISIBLE);
        JSONArray appUpdateReadme = json.getJSONArray("appUpdateReadme");
        TextView tvReadme = (TextView) findViewById(R.id.tvReadme);
        StringBuffer sbReadme = new StringBuffer();
        for (int i = 0; i < appUpdateReadme.length(); i++) {
            sbReadme.append(appUpdateReadme.getString(i) + "\n");
        }
        tvReadme.setText(sbReadme.toString());
        appUpdateReset = json.getBoolean("appUpdateReset");
        Button btnOk = (Button) findViewById(R.id.btnOk);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(myApp.getFormUrl(String.format("install.update?appCode=%s&curVersion=%s",
                        myApp.getAppCode(),
                        oldVersion)));
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
                finish();
            }
        });
        btnOk.setText("更新");
        btnCancel.setText(appUpdateReset ? "退出" : "下次更新");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appUpdateReset) {
                    finish();
                } else {
                    llDialog.setVisibility(View.GONE);
                    view_Masking.setVisibility(View.GONE);
                    loadImageView();
                }
            }
        });
    }

    private void loadImageView() {
        navigationChatImageView = new NavigationChatImageView(this, resp, settings);
        navigationChatImageView.setPopListener(PagerListener);
        frameLayout.addView(navigationChatImageView.loadNavigationImage());
    }

    private void startMainForm(int time) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //启动主窗口
                Intent intent = new Intent();
                intent.setClass(instince, FrmMain.class);
                instince.startActivity(intent);
                finish();
            }
        }, time);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 放在setContentView()之前运行
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_frm_startup);
        instince = this;
        llDialog = (LinearLayout) findViewById(R.id.llDialog);
        view_Masking = findViewById(R.id.view_Masking);
        view_Line = findViewById(R.id.view_Line);
        start_image = (ImageView) findViewById(R.id.start_image);
        frameLayout = (FrameLayout) findViewById(R.id.frm_image);
        settings = getSharedPreferences(Constans.SHARED_SETTING_TAB, MODE_PRIVATE);
        String startimage = settings.getString(IMAGE_STARTIP, "");
        if (isCachePathFileExist(startimage)) {
            start_image.setVisibility(View.VISIBLE);
            Bitmap bm = BitmapFactory.decodeFile(settings.getString(IMAGE_STARTIP, null));
            start_image.setImageBitmap(bm);
        }
        /*
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = MSG_CLIENT;
                handler.sendMessage(msg);
            }
        }, 3000);
        */
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            if (MyApp.getInstance().isDebug()) {
                DialogUtil.InputDialog(this, new DialogUtil.OnclickAddressListen() {
                    @Override
                    public void click(boolean bool, String newsUrl) {
                        if (bool) {
                            MyApp.setHomeUrl(newsUrl);
                        }
                        startRequest();
                        return;
                    }
                });
            } else {
                startRequest();
                return;
            }
        } else {
            //申请权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }

        //检查是否已被拒绝过
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_PHONE_STATE)) {
            showError("权限不足", "系统运行时必须读取IMEI，防止非您本人冒用您的帐号，请您于设置中开启相应权限后才能继续使用！");
            return;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (MyApp.getInstance().isDebug()) {
                        DialogUtil.InputDialog(this, new DialogUtil.OnclickAddressListen() {
                            @Override
                            public void click(boolean bool, String newsUrl) {
                                if (bool) {
                                    MyApp.setHomeUrl(newsUrl);
                                }
                                startRequest();
                                return;
                            }
                        });
                    } else {
                        startRequest();
                        return;
                    }
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_PHONE_STATE},
                            MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                }
                return;
            }
        }
    }

    private void startRequest() {
        TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        MyApp.getInstance().setClientId("n_" + TelephonyMgr.getDeviceId());

        new Thread(new Runnable() {
            @Override
            public void run() {
                MyApp myApp = MyApp.getInstance();
                HttpClient client = new HttpClient(MyApp.getFormUrl("install.client"));
                Map<String, String> params = new HashMap<String, String>();
                params.put("appCode", myApp.getAppCode());
                params.put("curVersion", myApp.getCurrentVersion(instince));
                String response = client.post(params);
                Message msg = new Message();
                msg.what = MSG_CLIENT;
                msg.obj = response;
                handler.sendMessage(msg);
            }
        }).start();
    }

}

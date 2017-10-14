package cn.cerc.summer.android.forms;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mimrc.vine.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import cn.cerc.summer.android.core.MyApp;
import cn.cerc.summer.android.basis.HttpClient;

public class FrmStartup extends AppCompatActivity {
    LinearLayout llDialog;

    private boolean appUpdateReset = false;
    private FrmStartup instince;
    private Timer timer = new Timer();
    private int MSG_CLIENT = 1;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == MSG_CLIENT){
                String str = (String) msg.obj;
                JSONObject json = null;
                String err = null;
                try {
                    json = new JSONObject(str);
                    if(json.has("result")){
                        if(json.getBoolean("result")){
                            instince.loadConfig(json);
                        }else{
                            str = json.getString("message");
                        }
                    }else{
                        str = "无法取得后台服务，请稍后再试！";
                    }
                } catch (Exception e) {
                    str = e.getMessage();
                }
                if(err != null) {
                    llDialog.setVisibility(View.VISIBLE);
                    TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
                    tvTitle.setText("出现错误！");
                    TextView tvReadme = (TextView) findViewById(R.id.tvReadme);
                    tvReadme.setText(str);
                    Button btnOK = (Button) findViewById(R.id.btnOk);
                    btnOK.setVisibility(View.GONE);
                    Button btnCancel = (Button) findViewById(R.id.btnCancel);
                    btnCancel.setText("确定");
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                }
            }
        }
    };

    //读取配置
    private void loadConfig(JSONObject json) throws JSONException, PackageManager.NameNotFoundException {
        final MyApp myApp = MyApp.getInstance();
        myApp.loadConfig(json);

        //检测是否有新的版本
        final String oldVersion = myApp.getCurrentVersion(this);
        if(oldVersion.equals(myApp.getAppVersion())) {
            startMainForm();
            return;
        }

        //发现有新版本
        llDialog.setVisibility(View.VISIBLE);
        JSONArray appUpdateReadme = json.getJSONArray("appUpdateReadme");
        TextView tvReadme = (TextView) findViewById(R.id.tvReadme);
        StringBuffer sbReadme = new StringBuffer();
        for(int i = 0; i < appUpdateReadme.length(); i ++) {
            sbReadme.append(appUpdateReadme.getString(i) + "\n");
        }
        tvReadme.setText(sbReadme.toString());
        appUpdateReset = json.getBoolean("appUpdateReset");
        Button  btnOk = (Button) findViewById(R.id.btnOk);
        Button   btnCancel = (Button) findViewById(R.id.btnCancel);
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
                if(appUpdateReset){
                    finish();
                }else{
                    startMainForm();
                }
            }
        });
    }

    private void startMainForm(){
        //启动主窗口
        Intent intent = new Intent();
        intent.setClass(instince, FrmMain.class);
        instince.startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frm_startup);
        instince = this;

        llDialog = (LinearLayout) findViewById(R.id.llDialog);
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

        TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        MyApp.IMEI = TelephonyMgr.getDeviceId();
    }
}

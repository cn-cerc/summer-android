package cn.cerc.summer.android.basis.forms;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.mimrc.vine.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.cerc.summer.android.basis.core.MyApp;
import cn.cerc.summer.android.basis.db.HttpClient;
import cn.cerc.summer.android.basis.db.RemoteForm;

import static com.google.android.gms.common.zze.rf;

public class FrmStartup extends AppCompatActivity {
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
                try {
                    json = new JSONObject(str);
                    if(json.has("result")){
                        if(json.getBoolean("result")){
                            String value = json.getString("appVersion");
                            MyApp.getInstance().setAppVersion(value);
                            //启动主窗口
                            Intent intent = new Intent();
                            intent.setClass(instince, FrmMain.class);
                            instince.startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(instince, json.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(instince, "无法取得后台服务，请稍后再试！", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(instince, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frm_startup);
        instince = this;

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
                HttpClient client = new HttpClient(MyApp.getFormUrl("install.client"));
                Map<String, String> params = new HashMap<String, String>();
                params.put("client", "android");
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

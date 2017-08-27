package cn.cerc.summer.android.parts.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.mimrc.vine.R;

import org.json.JSONException;
import org.json.JSONObject;

import cn.cerc.summer.android.basis.core.MyApp;
import cn.cerc.summer.android.parts.barcode.HttpClient;

public class FrmLoginByAccount extends AppCompatActivity implements View.OnClickListener {
    EditText edtAccount;
    EditText edtPassword;
    Button btnLogin;
    TextView lblMessage;
    CheckBox chkSave;
    private final int MSG_LOGIN = 1;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_LOGIN) {
                try {
                    String response = (String) msg.obj;
                    JSONObject json = null;
                    json = new JSONObject(response);
                    if (json.has("result") && json.getBoolean("result")) {
                        lblMessage.setText(json.getString("data"));
                    } else {
                        if (json.has("message"))
                            lblMessage.setText(json.getString("message"));
                        else
                            lblMessage.setText(response);
                    }
                }catch (JSONException e){
                    lblMessage.setText("service result message error!");
                } catch (Exception e) {
                    lblMessage.setText(e.getMessage());
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frm_login_by_account);

        edtAccount = (EditText) findViewById(R.id.edtAccount);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        lblMessage = (TextView) findViewById(R.id.lblMessage);
        chkSave = (CheckBox) findViewById(R.id.chkSave);
    }

    public static void startForm(AppCompatActivity content) {
        Intent intent = new Intent();
        intent.setClass(content, FrmLoginByAccount.class);
        content.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                String account = edtAccount.getText().toString().trim();
                if ("".equals(account)) {
                    lblMessage.setText("用户帐号不允许为空");
                    return;
                }
                String password = edtPassword.getText().toString().trim();
                if ("".equals(password)) {
                    lblMessage.setText("用户密码不允许为空");
                    return;
                }
                this.onPause();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HttpClient client = new HttpClient(String.format("%s/services/SvrUserLogin.Check", MyApp.HOME_URL));
                            client.put("account", edtAccount.getText().toString());
                            String response = client.post();
                            Message msg = new Message();
                            msg.what = MSG_LOGIN;
                            msg.obj = client.post();
                            handler.sendMessage(msg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putString("account", edtAccount.getText().toString());
        editor.putString("password", edtPassword.getText().toString());
        editor.putBoolean("savePassword", chkSave.isChecked());
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        edtAccount.setText(settings.getString("account", ""));
        edtPassword.setText(settings.getString("password", ""));
        chkSave.setChecked(settings.getBoolean("savePassword", true));
    }
}

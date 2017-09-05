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

import cn.cerc.jdb.core.DataSet;
import cn.cerc.summer.android.basis.core.MyApp;
import cn.cerc.summer.android.basis.core.MySession;
import cn.cerc.summer.android.basis.db.RemoteService;
import cn.cerc.summer.android.basis.forms.FrmMain;

public class FrmLoginByAccount extends AppCompatActivity implements View.OnClickListener {
    EditText edtAccount;
    EditText edtPassword;
    Button btnLogin;
    TextView lblMessage;
    CheckBox chkSave;
    private String loginUrl;

    private final int MSG_LOGIN = 1;
    private static final String LOGTAG = "FrmLoginByAccount";

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_LOGIN) {
                RemoteService rs = (RemoteService) msg.obj;
                if (rs.isOk()) {
                    String token = rs.getDataOut().getHead().getString("SessionID_");
                    MySession.getInstance().setToken(token);
                    FrmMain.getInstance().loadUrl(MyApp.getInstance().getFormUrl("WebDefault?sid=" + token));
                    finish();
                } else
                    lblMessage.setText(rs.getMessage());
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

        Intent intent = getIntent();
        loginUrl = intent.getStringExtra("loginUrl");
    }

    public static void startForm(AppCompatActivity content, String loginUrl) {
        Intent intent = new Intent();
        intent.putExtra("loginUrl", loginUrl);
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
                            RemoteService rs = new RemoteService(loginUrl);
                            DataSet dataIn = rs.getDataIn();
//                            dataIn.getHead().setField("usercode", edtAccount.getText().toString());
                            dataIn.getHead().setField("Account_", edtAccount.getText().toString());
//                            dataIn.getHead().setField("password", edtPassword.getText().toString());
                            dataIn.getHead().setField("Password_", edtPassword.getText().toString());
                            dataIn.getHead().setField("MachineID_", MyApp.IMEI);
                            dataIn.getHead().setField("ClientName_", "android");
                            handler.sendMessage(rs.execByMessage(MSG_LOGIN));
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

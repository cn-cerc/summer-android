package cn.cerc.summer.android.parts.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.airsaid.pickerviewlibrary.OptionsPickerView;
import com.mimrc.vine.R;

import org.xutils.common.util.KeyValue;

import java.util.ArrayList;
import java.util.List;

import cn.cerc.summer.android.basis.RemoteForm;
import cn.cerc.summer.android.core.ListDataSave;
import cn.cerc.summer.android.core.MyApp;
import cn.cerc.summer.android.core.VisualKeyboardTool;

/**
 * Created by Administrator on 2018/2/26.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edit_phone, edit_password;
    private ImageView image_display, select_host, cipher_state;
    private TextView text_host, text_password, text_contact, text_remind;
    private Button btn_login;
    private LinearLayout linear_host, linear_password;
    private String phone, pass;

    private OptionsPickerView<String> mOptionsPickerView;
    protected SharedPreferences settingShared;
    private boolean remember_psw;

    //界面控件
    private ImageButton spinner;
    private EditText et_name;
    //构造qq号用到的集合
    private List<String> names = new ArrayList<String>();
    //布局加载器
    private LayoutInflater mInflater;
    //自定义适配器
    private MyAdapter mAdapter;
    //PopupWindow
    private PopupWindow pop;
    //是否显示PopupWindow，默认不显示
    private boolean isPopShow = false;
    private ListDataSave listDataSave;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindows();//沉浸式全屏设置
        setContentView(R.layout.activity_login);

        initView();
        loadData();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * 加载数据
     */
    private void loadData() {
        int pickerID = settingShared.getInt("selectedHostitem", -1);
        mOptionsPickerView = new OptionsPickerView<>(this);
        final ArrayList<String> list = new ArrayList<>();
        list.add("北京");
        list.add("杭州");
        list.add("青岛");
        // 设置数据
        mOptionsPickerView.setPicker(list);
        mOptionsPickerView.setSubmitText("确定");
        mOptionsPickerView.setCancelText("取消");
        if (pickerID != -1) {
            mOptionsPickerView.setSelectOptions(pickerID);
            switch (pickerID) {
                case 0:
                    text_host.setText("北京");
                    break;
                case 1:
                    text_host.setText("杭州");
                    break;
                case 2:
                    text_host.setText("青岛");
                    break;
            }
        }
        // 设置选项单位
        mOptionsPickerView.setOnOptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int option1, int option2, int option3) {
                String sex = list.get(option1);
                mOptionsPickerView.setSelectOptions(option1);
                switch (option1) {
                    case 0:
                        text_host.setText("北京");
                        MyApp.setHomeUrl("https://m.knowall.cn");
                        settingShared.edit().putInt("selectedHostitem", option1).commit();
                        break;
                    case 1:
                        text_host.setText("杭州");
                        MyApp.setHomeUrl("https://m.knowall.cn");
                        settingShared.edit().putInt("selectedHostitem", option1).commit();
                        break;
                    case 2:
                        text_host.setText("青岛");
                        MyApp.setHomeUrl("https://m.knowall.cn");
                        settingShared.edit().putInt("selectedHostitem", option1).commit();
                        break;
                }
            }
        });

        listDataSave = new ListDataSave(this, "ACCOUNT_LIST");
        if (listDataSave.getDataList("ACCOUNT_DATA").size() > 0) {
            names.addAll(listDataSave.getDataList("ACCOUNT_DATA"));
        }
        mInflater = LayoutInflater.from(LoginActivity.this);
        mAdapter = new MyAdapter();
    }

    private void initWindows() {
        Window window = getWindow();
        int color = getResources().getColor(android.R.color.transparent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            window.setStatusBarColor(color);
            //设置导航栏颜色
//            window.setNavigationBarColor(color);
            ViewGroup contentView = ((ViewGroup) findViewById(android.R.id.content));
            View childAt = contentView.getChildAt(0);
            if (childAt != null) {
                childAt.setFitsSystemWindows(true);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //设置contentview为fitsSystemWindows
            ViewGroup contentView = (ViewGroup) findViewById(android.R.id.content);
            View childAt = contentView.getChildAt(0);
            if (childAt != null) {
                childAt.setFitsSystemWindows(true);
            }
            //给statusbar着色
            View view = new View(this);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, VisualKeyboardTool.getStatusBarHeight(this)));
            view.setBackgroundColor(color);
            contentView.addView(view);
        }
    }

    /**
     * 初始化信息
     */
    private void initView() {
        edit_phone = (EditText) findViewById(R.id.edit_phone);
        edit_password = (EditText) findViewById(R.id.edit_password);
        image_display = (ImageView) findViewById(R.id.image_display);
        select_host = (ImageView) findViewById(R.id.select_host);
        text_host = (TextView) findViewById(R.id.text_host);
        text_contact = (TextView) findViewById(R.id.text_contact);
        text_remind = (TextView) findViewById(R.id.text_remind);
        btn_login = (Button) findViewById(R.id.btn_login);
        linear_host = (LinearLayout) findViewById(R.id.linear_host);
        linear_password = (LinearLayout) findViewById(R.id.linear_password);
        cipher_state = (ImageView) findViewById(R.id.cipher_state);
        image_display.setOnClickListener(this);
        linear_host.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        text_contact.setOnClickListener(this);
        linear_password.setOnClickListener(this);

        settingShared = getSharedPreferences("LOGIN_DATA", Context.MODE_PRIVATE);
        remember_psw = settingShared.getBoolean("remember_psw", false);

        if (remember_psw) {
            cipher_state.setImageResource(R.mipmap.password_check);
        } else {
            cipher_state.setImageResource(R.mipmap.password_cancel);
        }
    }

    /**
     * 登录
     */
    private void loginMethod() {
        if (!MyApp.getNetworkState(this)) {
            text_remind.setText("当前网络不可用，请检查网络设置");
            text_remind.setTextColor(Color.RED);
            return;
        }
        btn_login.setEnabled(false);
        phone = edit_phone.getEditableText().toString().trim();
        pass = edit_password.getEditableText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            text_remind.setText("请输入账号");
            btn_login.setEnabled(true);
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            text_remind.setText("请输入密码");
            btn_login.setEnabled(true);
            return;
        }

        //登录请求
        List<KeyValue> map = new ArrayList<>();
        map.add(new KeyValue("mobile", phone));
        map.add(new KeyValue("password", pass));

        new Thread(new Runnable() {
            @Override
            public void run() {
                RemoteForm rf = new RemoteForm("");
                rf.putParam("mobile", phone);
                rf.putParam("password", pass);
                handler.sendMessage(rf.execByMessage(1));
            }
        }).start();

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    RemoteForm rf = (RemoteForm) msg.obj;
                    if (rf.isOk()) {
                        //TODO 登录成功回调
                        finish();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_display:
                //下拉显示账户名
                if (names.size() == 0) {
                    names.add("(空)");
                }
                if (pop == null) {
                    ListView listView = new ListView(LoginActivity.this);
                    listView.setCacheColorHint(0x00000000);
                    listView.setAdapter(mAdapter);
                    pop = new PopupWindow(listView, edit_phone.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT, true);
                    pop.setBackgroundDrawable(new ColorDrawable(0x00000000));
                    pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            image_display.setImageResource(R.mipmap.down_arrow);
                        }
                    });
                    isPopShow = true;
                }
                if (!pop.isShowing()) {
                    pop.showAsDropDown(edit_phone, 0, 0);
                    image_display.setImageResource(R.mipmap.up_arrow);
                    isPopShow = false;
                } else {
                    pop.dismiss();
                    image_display.setImageResource(R.mipmap.down_arrow);
                    isPopShow = true;
                }
                break;
            case R.id.linear_host:
                //切换主机
                mOptionsPickerView.show();
                break;
            case R.id.linear_password:
                //记住密码
                if (remember_psw) {
                    cipher_state.setImageResource(R.mipmap.password_check);
                    remember_psw = false;
                    settingShared.edit().putBoolean("remember_psw", true).commit();
                } else {
                    cipher_state.setImageResource(R.mipmap.password_cancel);
                    remember_psw = true;
                    settingShared.edit().putBoolean("remember_psw", false).commit();
                }
                break;
            case R.id.btn_login:
                //登录
                loginMethod();
                if (names.get(0).contains("空")) {
                    names.remove(0);
                }
                String phoneNumber = edit_phone.getText().toString().trim();
                if (phoneNumber != null && !"".equals(phoneNumber)) {
                    names.add(phoneNumber);
                }
                listDataSave.setDataList("ACCOUNT_DATA", names);
                break;
            case R.id.contact:
                //联系客服
                break;
        }
    }

    /**
     * 自定义Adapter
     */
    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return names.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return names.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = mInflater.inflate(R.layout.spinner_item, null);
            final TextView tv_name = (TextView) view.findViewById(R.id.text_account);
            tv_name.setText(names.get(position));
//            ImageButton delete = (ImageButton) view.findViewById(R.id.img_delete);
            //设置按钮的监听事件
//            delete.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    // TODO Auto-generated method stub
//                    names.remove(position);
//                    isPopShow = true;
//                    mAdapter.notifyDataSetChanged();
//                }
//            });
//            //设置按钮的监听事件
            tv_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (!names.get(position).contains("空")) {
                        edit_phone.setText(names.get(position));
                    }
                    pop.dismiss();
                }
            });
            return view;
        }

    }
}

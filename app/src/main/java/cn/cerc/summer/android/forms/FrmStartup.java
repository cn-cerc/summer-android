package cn.cerc.summer.android.forms;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mimrc.vine.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import cn.cerc.summer.android.core.Constans;
import cn.cerc.summer.android.core.FileUtils;
import cn.cerc.summer.android.core.MyApp;
import cn.cerc.summer.android.basis.HttpClient;

public class FrmStartup extends AppCompatActivity implements View.OnClickListener {
    LinearLayout llDialog;

    private boolean appUpdateReset = false;
    private FrmStartup instince;
    private Timer timer = new Timer();
    private int MSG_CLIENT = 1;
    private int IMAGE_CLTENT = 2;
    private MyApp myApp;
    //图片放置
    private ViewPager viewPager;
    //图片放置
    private List<ImageView> imageList;
    // 放4个小灰点的线性布局
    private LinearLayout linearLayout;
    private ImageView lan_Iv;
    private ImageView start_image;
    //小点之间的距离
    private int pointWidth;
    private Button startbtn;
    private List<String> imagePathList = new ArrayList<String>();
    private RelativeLayout start_relative;
    private JSONObject json = null;
    private String imageFileName = "cacheimage/";
    String resp = null;
    private SharedPreferences settings;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_CLIENT) {
                resp = (String) msg.obj;
                String err = null;
                try {
                    json = new JSONObject(resp);
                    Log.d("print", "handleMessage: json" + json);
                    if (json.has("result")) {
                        if (json.getBoolean("result")) {
                            instince.loadConfig(json);
                        } else {
                            err = json.getString("message");
                        }
                    } else {
                        err = "无法取得后台服务，请稍后再试！";
                    }
                } catch (Exception e) {
                    err = e.getMessage();
                }
                if (err != null) {
                    llDialog.setVisibility(View.VISIBLE);
                    TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
                    tvTitle.setText("出现错误！");
                    TextView tvReadme = (TextView) findViewById(R.id.tvReadme);
                    tvReadme.setText(err);
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
        myApp = MyApp.getInstance();
        myApp.loadConfig(json);

        //检测是否有新的版本
        final String oldVersion = myApp.getCurrentVersion(this);
        if (oldVersion.equals(myApp.getAppVersion())) {
            llDialog.setVisibility(View.GONE);
            loadimageViewPager();
            return;
        }

        //发现有新版本
        llDialog.setVisibility(View.VISIBLE);
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
                    loadimageViewPager();
                }
            }
        });
    }

    private String imageFilePath = null;

    private void loadimageViewPager() {
        boolean isfer = settings.getBoolean(Constans.IS_FIRST_SHAREDKEY, true);
        String cacheFile = settings.getString(Constans.CACHE_FILE, null);
        if (json != null) {
            try {
                final String imageAD = json.getString("imageStartup");
                File filesDir = this.getExternalFilesDir(null);
                String fileName = imageAD.substring(imageAD.lastIndexOf("/") + 1);
                //存到本地的绝对路径
                imageFilePath = FileUtils.getCacheFilePath(fileName);
                File file = new File(fileName);
                RequestParams entity = new RequestParams(imageAD);
                entity.setSaveFilePath(imageFilePath);
                x.http().get(entity, new Callback.CommonCallback<File>() {
                    @Override
                    public void onSuccess(File result) {
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                    }

                    @Override
                    public void onFinished() {
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (isfer) {
                try {
                    final JSONArray imageAD = json.getJSONArray("imageAD");
                    for (int i = 0; i < imageAD.length(); i++) {
                        File filesDir = this.getExternalFilesDir(null);
                        String fileName = imageAD.getString(i).substring(imageAD.getString(i).lastIndexOf("/") + 1);
                        //存到本地的绝对路径
                        if (FileUtils.isCacheFileExist(imageFileName + fileName)) {
                            imageCarsousel();
                            break;
                        }
                        final String filePath = FileUtils.getCacheFilePath(imageFileName + (i + 1) + ".jpg");
                        File file = new File(imageFileName + (i + 1) + ".jpg");
                        //如果不存在
                        if (!file.exists()) {
                            //创建
                            file.mkdirs();
                        }
                        RequestParams entity = new RequestParams(imageAD.getString(i));
                        entity.setSaveFilePath(filePath);
                        final int finalNum = i;
                        x.http().get(entity, new Callback.CommonCallback<File>() {
                            @Override
                            public void onSuccess(File result) {
                                if (finalNum == imageAD.length() - 1) {
                                    imageCarsousel();
                                }
                            }

                            @Override
                            public void onError(Throwable ex, boolean isOnCallback) {
                            }

                            @Override
                            public void onCancelled(CancelledException cex) {
                            }

                            @Override
                            public void onFinished() {
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //创建配置文件
                if (resp != null) {
                    settings.edit().putBoolean(Constans.IS_FIRST_SHAREDKEY, false).commit();
                    settings.edit().putString(Constans.CACHE_FILE, resp).commit();
                    if (imageFilePath != null)
                        settings.edit().putString(Constans.IMAGE_STARTIP, imageFilePath).commit();
                }
            } else {
                //存在
                if (imageFilePath != null)
                    settings.edit().putString(Constans.IMAGE_STARTIP, imageFilePath).commit();
                startMainForm();
            }
        } else {
            startMainForm();
        }
    }

    private void imageCarsousel() {
        //显示广告轮播图
        imagePathList = getImagePathFromSD(FileUtils.getCacheFilePath(imageFileName));
        //初始化图片和小点，图片的个数和小点的个数要始终一致
        start_relative.setVisibility(View.VISIBLE);
        for (int i = 0; i < imagePathList.size(); i++) {
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageList.add(imageView);
            // 根据图片的个数去放置相应数量的小灰点
            ImageView huiImageView = new ImageView(this);
            huiImageView.setImageResource(R.mipmap.hui_bg);
            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams
                            (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            huiImageView.setLayoutParams(layoutParams);

            if (i > 0) {
                //给除了第一个小点的其它小点左边距
                layoutParams.leftMargin = 20;
            }

            linearLayout.addView(huiImageView);
        }

        lan_Iv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //视图全部绘制完成时，计算得到小点之间的距离
                pointWidth = linearLayout.getChildAt(1).getLeft() - linearLayout.getChildAt(0).getLeft();
                System.out.println(pointWidth);
            }
        });
        //绑定适配器
        viewPager.setAdapter(new MyAdapter());
        //设置图片切换的监听事件
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                //让滑倒最后一页显示按钮
                if (position == imagePathList.size() - 1) {
                    startbtn.setText("开始体验");
                } else {
                    startbtn.setText("下一步");
                }
            }

            @Override
            //在Viewpger的界面不断滑动的时候会触发
            //position：当前滑到了第几页从0开始计数
            public void onPageScrolled(int position, float offset, int arg2) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) lan_Iv.getLayoutParams();
                //滑动的百分比乘上小点之间的距离，再加上当前位置乘上距离，即为蓝色小点的左边距
                layoutParams.leftMargin = (int) (pointWidth * offset + position * pointWidth);
                lan_Iv.setLayoutParams(layoutParams);
                System.out.println("当前滑动的百分比   " + offset);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // TODO Auto-generated method stub

            }
        });
    }

    /**
     * 从sd卡获取图片资源
     *
     * @return
     */
    private List<String> getImagePathFromSD(String filePath) {
        // 图片列表
        List<String> imagePathList = new ArrayList<String>();
        // 得到sd卡内image文件夹的路径   File.separator(/)
        // 得到该路径文件夹下所有的文件
        File fileAll = new File(filePath);
        File[] files = fileAll.listFiles();
        // 将所有的文件存入ArrayList中,并过滤所有图片格式的文件
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (checkIsImageFile(file.getPath())) {
                imagePathList.add(file.getPath());
            }
        }
        // 返回得到的图片列表
        Collections.sort(imagePathList, new ImageComparator());
        return imagePathList;
    }

    /**
     * 自定义名称排序
     */
    class ImageComparator implements Comparator<String> {

        @Override
        public int compare(String lhs, String rhs) {
            if ((getFileName(lhs)).compareTo(getFileName(rhs)) > 0) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    /**
     * 检查扩展名，得到图片格式的文件
     *
     * @param fName 文件名
     * @return
     */
    @SuppressLint("DefaultLocale")
    private boolean checkIsImageFile(String fName) {
        boolean isImageFile = false;
        // 获取扩展名
        String FileEnd = fName.substring(fName.lastIndexOf(".") + 1,
                fName.length()).toLowerCase();
        if (FileEnd.equals("jpg") || FileEnd.equals("png") || FileEnd.equals("gif")
                || FileEnd.equals("jpeg") || FileEnd.equals("bmp")) {
            isImageFile = true;
        } else {
            isImageFile = false;
        }
        return isImageFile;
    }

    private void startMainForm() {
        //启动主窗口
        Intent intent = new Intent();
        intent.setClass(instince, FrmMain.class);
        instince.startActivity(intent);
        finish();
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
        lan_Iv = (ImageView) findViewById(R.id.lan_Iv);
        linearLayout = (LinearLayout) findViewById(R.id.ll);
        startbtn = (Button) findViewById(R.id.startbtn);
        viewPager = (ViewPager) findViewById(R.id.vp);
        start_relative = (RelativeLayout) findViewById(R.id.start_relative);
        imageList = new ArrayList<ImageView>();
        start_image = (ImageView) findViewById(R.id.start_image);
        llDialog = (LinearLayout) findViewById(R.id.llDialog);
        startbtn.setOnClickListener(this);
        settings = getSharedPreferences(Constans.SHARED_SETTING_TAB, MODE_PRIVATE);
        String startimage = settings.getString(Constans.IMAGE_STARTIP, "");
        if (FileUtils.isCachePathFileExist(startimage)) {
            Log.d("print", "onCreate: " + settings.getString(Constans.IMAGE_STARTIP, null));
            start_image.setVisibility(View.VISIBLE);
            Bitmap bm = BitmapFactory.decodeFile(settings.getString(Constans.IMAGE_STARTIP, null));
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
        MyApp.getInstance().setClientId("n_" + TelephonyMgr.getDeviceId());
    }

    @Override
    public void onClick(View v) {
        //点击开始体验
        if (startbtn.getText().equals("下一步")) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
        } else {
            startMainForm();
        }
    }

    /**
     * 根据路径获取文件名
     *
     * @param pathandname
     * @return
     */
    public String getFileName(String pathandname) {
        int start = pathandname.lastIndexOf("/");
        int end = pathandname.lastIndexOf(".");
        if (start != -1 && end != -1) {
            return pathandname.substring(start + 1, end);
        } else {
            return null;
        }
    }

    class MyAdapter extends PagerAdapter {
        @Override
        // 返回ViewPager里面有几页
        public int getCount() {
            // TODO Auto-generated method stub
            return imagePathList.size();
        }

        @Override
        // 用于判断是否有对象生成界面
        public boolean isViewFromObject(View view, Object object) {
            // TODO Auto-generated method stub
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = imageList.get(position);
            imageView.setImageURI(Uri.fromFile(new File(imagePathList.get(position))));
            container.addView(imageView);

            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // TODO Auto-generated method stub
            container.removeView((View) object);
        }

    }
}

package cn.cerc.summer.android.forms;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mimrc.vine.R;
import com.yancy.gallerypick.config.GalleryConfig;
import com.yancy.gallerypick.config.GalleryPick;
import com.yancy.gallerypick.inter.IHandlerCallBack;

import java.util.ArrayList;
import java.util.List;

import cn.cerc.summer.android.core.GlideImageLoader;
import cn.cerc.summer.android.core.OnFileChooseItemListener;
import cn.cerc.summer.android.parts.dialog.FileDialog;


/**
 * @class name：cn.cerc.summer.android.forms
 * @class 图片、文件
 * @anthor zhuhao
 * @time 2018-5-11 11:06
 */
public class FrmChooseFile extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivBack;
    private LinearLayout llChooseFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frm_file);
        initView();
        ininListener();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        ivBack = (ImageView) findViewById(R.id.ivBack);
        llChooseFile = (LinearLayout) findViewById(R.id.llChooseFile);
    }

    /**
     * 初始化点击事件
     */
    private void ininListener() {
        ivBack.setOnClickListener(this);
        llChooseFile.setOnClickListener(this);
    }

    //调用扫描画面并回调javaScript
    public static void startForm(AppCompatActivity context) {
        Intent intent = new Intent();
        intent.setClass(context, FrmChooseFile.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack://返回
                finish();
                break;
            case R.id.llChooseFile://文件选择
                showChooseFileDialog();
                break;
        }
    }

    /**
     * 显示图片、文件弹出框
     */
    private List<String> path = new ArrayList<>();

    public void showChooseFileDialog() {

        Toast.makeText(this, "点击了", Toast.LENGTH_SHORT).show();

        FileDialog fileDialog = new FileDialog(this);
        fileDialog.show();
        fileDialog.setOnFileChooseItemListener(new OnFileChooseItemListener() {
            @Override
            public void onChoose(int choosePos) {
                if (choosePos == 1) {//图片+相机
                    chooseImageOrCamera();
                } else if (choosePos == 2) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    //intent.setType(“image/*”);//选择图片
                    //intent.setType(“audio/*”); //选择音频
                    //intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
                    //intent.setType(“video/*;image/*”);//同时选择视频和图片
                    intent.setType("*/*");//无类型限制
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent, 789);
                }
            }
        });
    }

    /**
     * 图片、相机
     */
    private void chooseImageOrCamera() {
        IHandlerCallBack iHandlerCallBack = new IHandlerCallBack() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(List<String> photoList) {
                path.clear();
                for (String s : photoList) {
                    path.add(s);
                }
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onFinish() {
            }

            @Override
            public void onError() {
            }
        };
        GalleryConfig galleryConfig = new GalleryConfig.Builder()
                .imageLoader(new GlideImageLoader())    // ImageLoader 加载框架（必填）
                .iHandlerCallBack(iHandlerCallBack)     // 监听接口（必填）
                .provider("com.yancy.gallerypickdemo.fileprovider")   // provider (必填)
                .pathList(path)                         // 记录已选的图片
                .multiSelect(false)                      // 是否多选   默认：false
                .multiSelect(false, 1)                   // 配置是否多选的同时 配置多选数量   默认：false ， 9
                .maxSize(1)                             // 配置多选时 的多选数量。    默认：9
                .crop(false)                             // 快捷开启裁剪功能，仅当单选 或直接开启相机时有效
                .isShowCamera(true)                     // 是否现实相机按钮  默认：false
                .filePath("/地藤")          // 图片存放路径
                .build();
        GalleryPick.getInstance().setGalleryConfig(galleryConfig).openCamera(this);
    }

}

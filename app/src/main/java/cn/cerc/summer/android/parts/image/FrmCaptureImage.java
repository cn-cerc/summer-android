package cn.cerc.summer.android.parts.image;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mimrc.vine.R;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

/**
 * Created by yangtaiyu on 2017/10/16.
 */
public class FrmCaptureImage extends Activity implements View.OnClickListener {
    private Button btn_pop_album, btn_pop_camera, btn_pop_cancel;
    private String serverUrl;
    private LinearLayout camp_pop_linear;
    private RelativeLayout linear_image;
    private static final int LOAD_IMAGE = 1;  //相册返回
    private File file;  //照片file
    private Uri imageUri; //照片URi
    private static final int TAKE_PHOTO = 2;//相机返回
    private static final int CUT_PHOTO = 3; //裁剪返回

    public static void startForm(Context context, String urlImage) {
        Intent intent = new Intent();
        intent.setClass(context, FrmCaptureImage.class);
        intent.putExtra("url", urlImage);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFinishOnTouchOutside(true);
        setContentView(R.layout.activity_frm_capture_image);
        Window window = this.getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        Intent intent = getIntent();
        serverUrl = intent.getStringExtra("url");
        initView();
    }

    private void initView() {
        btn_pop_album = (Button) findViewById(R.id.btn_pop_album);
        btn_pop_camera = (Button) findViewById(R.id.btn_pop_camera);
        btn_pop_cancel = (Button) findViewById(R.id.btn_pop_cancel);
        camp_pop_linear = (LinearLayout) findViewById(R.id.camp_pop_linear);
        linear_image = (RelativeLayout) findViewById(R.id.linear_image);
        linear_image.setOnClickListener(this);
        btn_pop_camera.setOnClickListener(this);
        btn_pop_album.setOnClickListener(this);
        btn_pop_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_pop_album:
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, LOAD_IMAGE);
                break;
            case R.id.btn_pop_camera:
                Intent it = new Intent();
                it.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

                file = new File(getPath());
                imageUri = Uri.fromFile(file);
                // 以键值对的形式告诉系统照片保存的地址，键的名称不能随便写
                it.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                startActivityForResult(it, TAKE_PHOTO);
                camp_pop_linear.setVisibility(View.GONE);
                break;
            case R.id.btn_pop_cancel:
                finish();
                break;
            case R.id.linear_image:
                finish();
                break;
        }
    }

    /**
     * 制作图片的路径地址
     *
     * @return
     */
    public String getPath() {
        String path = null;
        File file = null;
        long tag = System.currentTimeMillis();
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            //SDCard是否可用
            path = Environment.getExternalStorageDirectory() + File.separator + "DCIM/";
            file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            path = Environment.getExternalStorageDirectory() + File.separator + "DCIM/" + tag + ".jpg";
        } else {
            path = this.getFilesDir() + File.separator + "DCIM/";
            file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            path = this.getFilesDir() + File.separator + "DCIM/" + tag + ".jpg";
        }
        return path;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LOAD_IMAGE:
                if (null != data) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    Log.d("print", "onActivityResult: __))___" + picturePath);
                    file = new File(picturePath);
                    imageUri = Uri.fromFile(file);
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri, "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, CUT_PHOTO);// 启动裁剪程序
                }
                break;
            case TAKE_PHOTO:
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(imageUri, "image/*");
                intent.putExtra("scale", true);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, CUT_PHOTO);// 启动裁剪程序
                break;
            case CUT_PHOTO:
                // 发送通知，通知媒体数据库更新URL，否则图库无法显示图片
                Intent it = new Intent();
                it.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                it.setData(imageUri);
                sendBroadcast(it);

                String picturePath = file.getPath();
                uploadImg(serverUrl, picturePath);
                break;
        }
    }

    /**
     * 上传图片至服务器
     *
     * @param url      服务器接口
     * @param filePath 本地图片路径
     */
    public void uploadImg(String url, final String filePath) {
        final ProgressDialog pb = new ProgressDialog(this);
        pb.setMessage("正在上传");
        pb.setCancelable(false);
        pb.show();
        RequestParams params = new RequestParams(url);
        params.setMultipart(true);//设置表单传送
        params.setCancelFast(true);//设置可以立即被停止
        params.addBodyParameter("Filedata", new File(filePath), "multipart/form-data");

        Callback.Cancelable cancelable = x.http().post(params, new Callback.ProgressCallback<String>() {
            @Override
            public void onSuccess(String result) {
                pb.dismiss();
                finish();
                Toast.makeText(FrmCaptureImage.this, "上传成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                pb.dismiss();
                Toast.makeText(FrmCaptureImage.this, "上传失败，请重新上传", Toast.LENGTH_SHORT).show();
                Log.i("uploadImg", "ex-->" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i("upload", "onCancelled");
            }

            @Override
            public void onFinished() {
            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
            }
        });
    }
}

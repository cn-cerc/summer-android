package cn.cerc.summer.android.parts.image;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mimrc.vine.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.http.RequestParams;
import org.xutils.http.body.MultipartBody;
import org.xutils.x;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yangtaiyu on 2017/10/16.
 */

public class FrmCaptureImage extends Activity implements View.OnClickListener {
    private Button btn_pop_album, btn_pop_camera, btn_pop_cancel;
    private String serverUrl;
    private String mCurrentPhotoPath;
    private LinearLayout camp_pop_linear;
    private Bitmap bitmap;
    private LinearLayout linear_image;
    private int RESULT_LOAD_IMAGE = 1;  //相册返回
    private int RESULT_CAMERA_IMAGE = 2;  //相机返回

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
        linear_image = (LinearLayout) findViewById(R.id.linear_image);
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
                startActivityForResult(i, RESULT_LOAD_IMAGE);
                break;
            case R.id.btn_pop_camera:
                takeCamera(RESULT_CAMERA_IMAGE);
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
     * 拍照并自动生成存储路径
     *
     * @param num
     */
    private void takeCamera(int num) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            photoFile = createImageFile();
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
            }
        }
        startActivityForResult(takePictureIntent, num);//跳转界面传回拍照所得数据
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_LOAD_IMAGE && null != data) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                String pictureImage = getFileName(picturePath);
                uploadImg(serverUrl, picturePath);
            } else if (requestCode == RESULT_CAMERA_IMAGE) {
                File file = null;
                String picturePath = null;
                String pictureImage;
                if (null == data) {
                    picturePath = mCurrentPhotoPath;
                } else {
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    picturePath = saveMyBitmap(bitmap).getAbsolutePath();
                    pictureImage = getFileName(picturePath);
                }
                uploadImg(serverUrl, picturePath);


            }
        }
    }

    //将bitmap转化为png格式
    public File saveMyBitmap(Bitmap mBitmap) {
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File file = null;
        try {
            file = File.createTempFile(
                    generateFileName(),  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            FileOutputStream out = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 20, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
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

    /**
     * 生成存储路径
     *
     * @return
     */
    private File createImageFile() {
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File image = null;
        try {
            image = File.createTempFile(
                    FrmCaptureImage.generateFileName(),  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //生成拍摄的照片名字
    public static String generateFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        return imageFileName;
    }
}

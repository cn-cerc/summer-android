package cn.cerc.summer.android.parts.image;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import cn.cerc.summer.android.basis.HttpClient;
import cn.cerc.summer.android.core.MyApp;
import cn.cerc.summer.android.core.MySession;
import cn.cerc.summer.android.core.PhotoBitmapUtils;
import cn.cerc.summer.android.core.RequestCallback;
import cn.cerc.summer.android.forms.FrmMain;

/**
 * Created by yangtaiyu on 2017/10/16.
 */
public class FrmCaptureImage extends Activity implements View.OnClickListener {
    private final static int TAKE_PICTURE = 1; //相机
    private final static int CHOOSE_PHOTO = 0; //相册
    private final static int PICTURE_CUT = 3; //裁剪

    private File file;
    private Uri photoUri = null;
    private String path = null;
    private String imgtitle = "data:image/png;base64,";
    private int number_head;

    private Dialog mCameraDialog;
    private Uri outputUri;
    private String imagePath;
    private boolean isClickCamera;//是否是拍照裁剪
    private File fileOutputUri;
    private Button btn_pop_album, btn_pop_camera, btn_pop_cancel;
    private String serverUrl;
    private LinearLayout camp_pop_linear;
    private RelativeLayout linear_image;
    private Uri imageUri; //照片URi
    private boolean isPhotograph = false;

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
                //打开相册
                if (ContextCompat.checkSelfPermission(FrmCaptureImage.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(FrmCaptureImage.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    selectFromAlbum();//打开相册
                }
                break;
            case R.id.btn_pop_camera:
                //打开相机
                if (ContextCompat.checkSelfPermission(FrmCaptureImage.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(FrmCaptureImage.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
                photograph();
                break;
            case R.id.btn_pop_cancel:
                finish();
                break;
            case R.id.linear_image:
                finish();
                break;
        }
    }

    private void selectFromAlbum() {
        if (ContextCompat.checkSelfPermission(FrmCaptureImage.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FrmCaptureImage.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            openAlbum();
        }
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }


    /**
     * 裁剪图片
     */
    private void cropPhoto(Uri uri) {
        // 创建File对象，用于存储裁剪后的图片，避免更改原图
        fileOutputUri = new File(getExternalCacheDir(), "crop_image.jpg");
        try {
            if (fileOutputUri.exists()) {
                fileOutputUri.delete();
            }
            fileOutputUri.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        outputUri = Uri.fromFile(fileOutputUri);
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "image/*");
        //裁剪图片的宽高比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("crop", "true");//可裁剪
        // 裁剪后输出图片的尺寸大小
        //intent.putExtra("outputX", 400);
        //intent.putExtra("outputY", 200);
        intent.putExtra("scale", true);//支持缩放
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());//输出图片格式
        intent.putExtra("noFaceDetection", true);//取消人脸识别
        startActivityForResult(intent, PICTURE_CUT);
    }

    // 4.4及以上系统使用这个方法处理图片 相册图片返回的不再是真实的Uri,而是分装过的Uri
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        imagePath = null;
        if (data != null) {
            Uri uri = data.getData();
            Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
            if (DocumentsContract.isDocumentUri(this, uri)) {
                // 如果是document类型的Uri，则通过document id处理
                String docId = DocumentsContract.getDocumentId(uri);
                if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                    String id = docId.split(":")[1]; // 解析出数字格式的id
                    String selection = MediaStore.Images.Media._ID + "=" + id;
                    imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
                } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                    imagePath = getImagePath(contentUri, null);
                }
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                // 如果是content类型的Uri，则使用普通方式处理
                imagePath = getImagePath(uri, null);
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                // 如果是file类型的Uri，直接获取图片路径即可
                imagePath = uri.getPath();
            }
            cropPhoto(uri);
        }
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        imagePath = getImagePath(uri, null);
        cropPhoto(uri);
    }

    /**
     * 拍照获取图片
     */
    private void photograph() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            MyApp.isPermissionsAllGranted(new String[]{Manifest.permission.CAMERA}, 002, this);
            return;
        }
        file = PhotoBitmapUtils.createIconFile();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            photoUri = FileProvider.getUriForFile(this, "com.mimrc.vine.fileprovider", file);//通过FileProvider创建一个content类型的Uri
        } else {
            photoUri = Uri.fromFile(file);
        }
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        }
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);//将拍取的照片保存到指定URI
        startActivityForResult(intent, TAKE_PICTURE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("print", "onActivityResult: " + requestCode + "   " + resultCode);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap = null;
                    if (file != null) {
                        try {
                            path = PhotoBitmapUtils.amendRotatePhoto(file.getAbsolutePath(), this);
                            bitmap = BitmapFactory.decodeStream(new FileInputStream(path));
                            Log.d("print", "onActivityResult: path" + path);
                            isPhotograph = true;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (data != null) {
                            if (data.hasExtra("data")) {
                                Log.i("URI", "data is not null");
                                Bitmap bitmap1 = data.getParcelableExtra("data");
                                path = PhotoBitmapUtils.savePhotoToSD(bitmap1, this);
                                Log.d("print", "onActivityResult: " + path);
                                isPhotograph = true;
                            }
                        }
                    }
                    if (path != null) {
                        setImageBitmap();
                    } else {
                        Log.d("print", "onActivityResult: 拍照等于空");
                    }
                }
                break;
            case CHOOSE_PHOTO://打开相册
                // 判断手机系统版本号
                if (Build.VERSION.SDK_INT >= 19) {
                    // 4.4及以上系统使用这个方法处理图片
                    handleImageOnKitKat(data);
                } else {
                    // 4.4以下系统使用这个方法处理图片
                    handleImageBeforeKitKat(data);
                }
                break;
            case PICTURE_CUT://裁剪完成
                isClickCamera = true;
                Bitmap bitmap = null;
                try {
                    if (isClickCamera) {
                        path = PhotoBitmapUtils.amendRotatePhoto(fileOutputUri.getAbsolutePath(), this);
                    } else {
                        path = PhotoBitmapUtils.amendRotatePhoto(imagePath, this);
                        bitmap = BitmapFactory.decodeFile(imagePath);
                    }
                    if (path != null) {
                        setImageBitmap();
                    } else {
                        Log.d("print", "onActivityResult: 相册等于空");
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    /**
     * 压缩图片
     */
    private void setImageBitmap() {
//        //获取imageview的宽和高
//        int targetWidth = photoImageView.getWidth();
//        int targetHeight = photoImageView.getHeight();
        Log.d("print", "setImageBitmap: " + path);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(fis);

//        photoImageView.setImageBitmap(bitmap);
        if (bitmap != null) {
            uploadingImage(bitmap);
//            path = null;
        }

    }

    private void uploadingImage(Bitmap bitmap) {
        Toast.makeText(this, "开始上传", Toast.LENGTH_SHORT).show();
        String client = MyApp.getFormUrl("FrmCusFollowUp.uploadFile") + String.format("?sid=%s&CLIENTID=%s", MySession.getInstance().getToken(), MyApp.getInstance().getClientId());
        Log.d("print", "uploadingImage: " + client);
        HttpClient httpClient = new HttpClient("FrmCusFollowUp.uploadFile");
        HashMap<String, String> rf = new HashMap<>();
        rf.put("followup", path);
        httpClient.POST(client, rf, new RequestCallback() {
            @Override
            public void success(String url, JSONObject json) {
                Log.d("print", "success: " + url + "   " + json.toString());
                try {
                    Toast.makeText(FrmCaptureImage.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                    FrmMain.getInstance().reloadPage();
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failt(String url, String error) {
                Toast.makeText(FrmCaptureImage.this, "上传失败，请检查网络后重试！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public Context getContext() {
                return null;
            }
        });
    }
}

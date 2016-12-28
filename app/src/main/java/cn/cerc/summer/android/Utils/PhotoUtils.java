package cn.cerc.summer.android.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.alibaba.fastjson.JSON;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import cn.cerc.summer.android.Entity.JSParam;
import cn.cerc.summer.android.Interface.RequestCallback;

/**
 * Created by fff on 2016/12/27.
 */

public class PhotoUtils implements PhotoCallback, RequestCallback {

    public JSParam jsp;
    private Activity activity;
    private static PhotoUtils pu;

    public PhotoUtils() {
    }

    /**
     * 获取单例实例，
     *
     * @return 返回当前实例
     */
    public static PhotoUtils getInstance() {
        if (pu == null) pu = new PhotoUtils();
        return pu;
    }

    /**
     * 传递json,
     *
     * @param json 每次调用js时传递过来的js  注意不要漏调此方法
     */
    public void setJson(String json) {
        jsp = JSON.parseObject(json, JSParam.class);
    }

    private File imagefile;

    public void Start_P(Activity activity, int request) {
        this.activity = activity;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imagefile = new File(Constans.getAppPath(Constans.IMAGE_PATH + "/original") + "/" + System.currentTimeMillis() + ".jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagefile));
        activity.startActivityForResult(intent, request);
    }

    public void Start_Crop() {   //"/crop/"

    }

    @Override
    public void Paifinish(int requestCode) {
        if (requestCode != 0){ //裁剪图片
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(Uri.fromFile(imagefile), "image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("scale", true);
            intent.putExtra("outputX", 500);
            intent.putExtra("outputY", 500);
            intent.putExtra("return-data", true);
            intent.putExtra("noFaceDetection", true);
            activity.startActivityForResult(intent, requestCode);
        }else{
            HashMap<String, String> map = new HashMap<String, String>();
            XHttpRequest.getInstance().POST("", map, this);
        }
    }

    @Override
    public void Cropfinish(Bitmap bitmap) {
        if (bitmap == null){
            HashMap<String, String> map = new HashMap<String, String>();
            XHttpRequest.getInstance().POST("", map, this);
        }else{
            File file = new File(Constans.getAppPath(Constans.IMAGE_PATH + "/crop") + "/" + System.currentTimeMillis() + ".jpg");
            FileUtil.createFile(bitmap, file);
            HashMap<String, String> map = new HashMap<String, String>();
            XHttpRequest.getInstance().POST("", map, this);
        }
    }

    @Override
    public void success(String url, JSONObject json) {

    }

    @Override
    public void Failt(String url, String error) {

    }

    @Override
    public Context getContext() {
        return activity.getApplicationContext();
    }
}

interface PhotoCallback {

    /**
     * 拍照完成
     * @param requestCode   裁剪的请求码如果为0则不需要裁剪
     */
    void Paifinish(int requestCode);

    void Cropfinish(Bitmap bitmap);

}


package cn.cerc.summer.android.Utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.alibaba.fastjson.JSON;

import java.io.File;

import cn.cerc.summer.android.Entity.JSParam;

interface PhotoCallback {

    File Cropfinish(Bitmap bitmap);

}

/**
 * Created by fff on 2016/12/27.
 */

public class PhotoUtils extends HardwareJSUtils implements PhotoCallback {

    private static PhotoUtils pu;
    public JSParam jsp;
    private Activity activity;
    private File imagefile;

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
    @Override
    public void setJson(String json) {
        jsp = JSON.parseObject(json, JSParam.class);
    }

    public void Start_P(Activity activity, int request) {
        this.activity = activity;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imagefile = new File(Constans.getAppPath(Constans.IMAGE_PATH + "/original") + "/" + System.currentTimeMillis() + ".jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagefile));
        activity.startActivityForResult(intent, request);
    }

    public void Start_Crop(int request) {   //"/crop/"
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(Uri.fromFile(imagefile), "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("outputX", 500);
        intent.putExtra("outputY", 500);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        activity.startActivityForResult(intent, request);
    }

    @Override
    public File Cropfinish(Bitmap bitmap) {
        File file = new File(Constans.getAppPath(Constans.IMAGE_PATH + "/crop") + "/" + System.currentTimeMillis() + ".jpg");
        FileUtil.createFile(bitmap, file);
        return file;
    }

}


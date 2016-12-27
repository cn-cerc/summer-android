package cn.cerc.summer.android.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

    public static final int REQUEST_PHOTO_CAMERA = 111;
    public static final int REQUEST_PHOTO_CROP = 113;

    public JSParam jsp;
    private Context context;

    public PhotoUtils(Context context, String json) {
        this.context = context;
        jsp = JSON.parseObject(json,JSParam.class);
    }

    private File imagefile;

    public void Start_P(Activity activity, int request){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imagefile = new File(Constans.getAppPath(Constans.IMAGE_PATH) + System.currentTimeMillis() + ".jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagefile));
        activity.startActivityForResult(intent, request);
    }

    public void Start_Crop(){

    }

    @Override
    public void Paifinish(boolean status) {

        HashMap<String, String> map = new HashMap<String, String>();
        XHttpRequest.getInstance().POST("", map, this);
    }

    @Override
    public void Cropfinish(boolean status) {

    }

    @Override
    public void success(String url, JSONObject json) {

    }

    @Override
    public void Failt(String url, String error) {

    }
    @Override
    public Context getContext() {
        return context;
    }
}

interface PhotoCallback{

    void Paifinish(boolean status);

    void Cropfinish(boolean status);

}


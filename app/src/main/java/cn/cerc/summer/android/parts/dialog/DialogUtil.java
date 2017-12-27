package cn.cerc.summer.android.parts.dialog;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Description:自定义dialog布局工具 用于上传图片
 * Author：lrh
 * Date: 2016/7/22 16:20
 */
public class DialogUtil {

    /***
     * 更新服务器地址
     */
    public static void InputDialog(final Activity activity, final OnclickAddressListen listen) {

        final InputDialog dialog = new InputDialog(activity);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setOnDialogClickListern(new InputDialog.OnDialogClick() {
            @Override
            public void onConfrim(String newsUrl) {
                listen.click(true,newsUrl);
                dialog.dismiss();
            }

            @Override
            public void onCancel() {
                listen.click(false,null);
                dialog.dismiss();
            }

        });
        dialog.show();
    }

    public interface OnclickAddressListen{
        void click(boolean bool, String newsUrl);
    }

}




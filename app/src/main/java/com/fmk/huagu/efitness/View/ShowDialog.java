package com.fmk.huagu.efitness.View;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.view.Window;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

/**
 * Created by fff on 2016/11/28.
 */

public class ShowDialog extends AlertDialog.Builder {

    private Context context;

    public ShowDialog(Context context) {
        super(context);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.context = context;
    }

    public ShowDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }



    public static ShowDialog getDialog(Context context){
        return new ShowDialog(context);
    }

    public ShowDialog UpDateDialogShow(){
        setMessage("检查到新版本，是否更新？");
        setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RequestParams rp = new RequestParams("http://114.55.249.103/app-debug.apk");
                x.http().get(rp, new Callback.ProgressCallback<File>() {
                    @Override
                    public void onWaiting() {
                    }
                    @Override
                    public void onStarted() {
                        showprogressdialog();
                    }
                    @Override
                    public void onLoading(long total, long current, boolean isDownloading) {
                        float xx = (float)current/(float)total;
                        progressDialog.setProgress((int)(xx*100));
                    }
                    @Override
                    public void onSuccess(File result) {
                        if (progressDialog !=null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        file = result;
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(result), "application/vnd.android.package-archive");
                        context.startActivity(intent);
                    }
                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        if (progressDialog !=null && progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                    @Override
                    public void onCancelled(CancelledException cex) {
                        if (progressDialog !=null && progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                    @Override
                    public void onFinished() {
                        if (progressDialog !=null && progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                });
            }
        });
        create();
        Dialog dialog = show();
        dialog.setCanceledOnTouchOutside(false);
        return this;
    }
    private ProgressDialog progressDialog;
    public void showprogressdialog(){
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("apk下载");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMax(100);
        progressDialog.show();
    }

    private File file;

    public void openApk() {
        PackageManager manager = context.getPackageManager();
        // 这里的是你下载好的文件路径
        PackageInfo info = manager.getPackageArchiveInfo(file.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
        if (info != null) {
            Intent intent = manager.getLaunchIntentForPackage(info.applicationInfo.packageName);
            context.startActivity(intent);
        }
    }

}

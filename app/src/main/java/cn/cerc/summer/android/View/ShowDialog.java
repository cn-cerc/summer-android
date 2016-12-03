package cn.cerc.summer.android.View;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;

import cn.cerc.summer.android.Entity.Config;
import cn.cerc.summer.android.Interface.GetFileCallback;
import cn.cerc.summer.android.Utils.XHttpRequest;

import org.xutils.common.Callback;

import java.io.File;

/**
 * Created by fff on 2016/11/28.
 */

public class ShowDialog extends AlertDialog.Builder implements DialogInterface.OnDismissListener, GetFileCallback {


    public ShowDialog(Context context) {
        super(context);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.context = context;
    }

    public ShowDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    private Context context;
    /**
     * 取消显示的dialog对象
     */
    private Dialog dialog;
    /**
     * 文件下载可取消的回调
     */
    private Callback.Cancelable c_cancel;
    /**
     * 下载的进度dialog
     */
    private ProgressDialog progressDialog;
    /**
     * 下载的文件路径
     */
    private File file;

    public static ShowDialog getDialog(Context context){
        return new ShowDialog(context);
    }

    /**
     * 版本更新的提示
     * @return
     */
    public ShowDialog UpDateDialogShow(){
        setMessage("检查到新版本，是否更新？");
        setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                c_cancel = XHttpRequest.getInstance().GETFile(Config.getConfig().getAppUpgrade(), ShowDialog.this);
            }
        });
        create();
        dialog = show();
        dialog.setCanceledOnTouchOutside(false);
        setOnDismissListener(this);
        return this;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (!c_cancel.isCancelled()){
            c_cancel.cancel();
        }
    }

    /**
     * 显示下载进度
     * @return
     */
    public ProgressDialog showprogressdialog(){
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("apk下载");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMax(100);
        progressDialog.show();
        return progressDialog;
    }

    /**
     * 显示提示的dialog
     * @return
     */
    public ShowDialog showTips(){
        setMessage("检测到网络故障，请检查网络后再试！");
        create();
        dialog = show();
        dialog.setCanceledOnTouchOutside(false);
        DelayedClose();
        return this;
    }


    /**
     * 延迟关闭提示的dialog
     */
    public void DelayedClose(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        },3000);
    }

    @Override
    public void success(String url, File file) {
        this.file = file;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void Failt(String url, String error) {
        Toast.makeText(context,"下载失败",Toast.LENGTH_SHORT).show();
    }

    @Override
    public Context getContext() {
        return context;
    }
}

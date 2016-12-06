package cn.cerc.summer.android.Utils;

import android.app.ProgressDialog;
import android.util.Log;

import cn.cerc.summer.android.Entity.Config;
import cn.cerc.summer.android.Interface.ConfigFileLoafCallback;
import cn.cerc.summer.android.Interface.GetFileCallback;
import cn.cerc.summer.android.Interface.RequestCallback;
import cn.cerc.summer.android.View.ShowDialog;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.List;

/**
 * Created by fff on 2016/11/30.
 * 网络请求
 */
public class XHttpRequest {

    /**
     * 获取当前实例
     *
     * @return 当前实例
     */
    public static XHttpRequest getInstance() {
        return new XHttpRequest();
    }

    /**
     * get请求
     *
     * @param url 请求地址
     * @param rc  请求回调
     */
    public void GET(final String url, final RequestCallback rc) {
        x.http().get(new RequestParams(url), new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                rc.success(url, result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                rc.Failt(url, ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                rc.Failt(url, "已取消");
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private ProgressDialog progressDialog;

    /**
     * 文件下载
     *
     * @param url 文件地址
     * @param rc  回调
     * @return 可取消的回调
     */
    public Callback.Cancelable GETFile(final String url, final GetFileCallback rc) {
        RequestParams rp = new RequestParams(url);
        rp.setSaveFilePath(Constans.FILE_ROOT_SAVEPATH + Constans.APP_PATH + "app.apk");
        Callback.Cancelable cc = x.http().get(rp, new Callback.ProgressCallback<File>() {
            @Override
            public void onWaiting() {
            }

            @Override
            public void onStarted() {
                progressDialog = ShowDialog.getDialog(rc.getContext()).showprogressdialog();
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                float xx = (float) current / (float) total;
                progressDialog.setProgress((int) (xx * 100));
            }

            @Override
            public void onSuccess(File result) {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                rc.success(url, result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                rc.Failt(url, ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                rc.Failt(url, "已取消下载");
            }

            @Override
            public void onFinished() {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        });
        return cc;
    }


    public static String fileurl2name(String url) {
        if (url.contains("?"))
            url = url.replace("?", "");
        url = url.substring(url.lastIndexOf("/"), url.length());
        return url;
    }

    private List<String> filelist;
    private int loadindex = 0;
    private ConfigFileLoafCallback cflc;

    private int error_num = 0;

    public void ConfigFileGet(List<String> filelist, ConfigFileLoafCallback cflc) {
        if (filelist != null && filelist.size() > 0) {
            this.filelist = filelist;
            this.cflc = cflc;
            fileLoad(filelist.get(loadindex));
        }else
            cflc.loadfinish();
    }

    private void fileLoad(String url) {
        String urls = Config.getConfig().getRootSite() + url;
        Log.e("url",urls);
        String savepath = Constans.FILE_ROOT_SAVEPATH + Constans.CONFIG_PATH + fileurl2name(url);
        if (new File(savepath).exists()) {
            loadindex++;
            if (loadindex < filelist.size()) fileLoad(filelist.get(loadindex));
            else cflc.loadfinish();
        } else {
            RequestParams rp = new RequestParams(urls);
            rp.setSaveFilePath(savepath);
            x.http().get(rp, new Callback.ProgressCallback<File>() {
                @Override
                public void onSuccess(File result) {
                    loadindex++;
                    if (loadindex < filelist.size()) fileLoad(filelist.get(loadindex));
                    else cflc.loadfinish();
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    error_num++;
                    if (error_num >= 3) {
                        loadindex++;
                        error_num = 0;
                    }
                    if (loadindex >= filelist.size()) {
                        cflc.loadfinish();
                        return;
                    }
                    fileLoad(filelist.get(loadindex));
                }

                @Override
                public void onCancelled(CancelledException cex) {
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

}

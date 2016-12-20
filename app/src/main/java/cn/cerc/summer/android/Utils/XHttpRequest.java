package cn.cerc.summer.android.Utils;

import android.app.ProgressDialog;
import android.util.Log;

import cn.cerc.summer.android.Entity.Config;
import cn.cerc.summer.android.Interface.ConfigFileLoafCallback;
import cn.cerc.summer.android.Interface.GetFileCallback;
import cn.cerc.summer.android.Interface.RequestCallback;
import cn.cerc.summer.android.View.ShowDialog;

import org.json.JSONArray;
import org.json.JSONException;
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
        if (!AppUtil.getNetWorkStata(rc.getContext())) return;
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
        if (!AppUtil.getNetWorkStata(rc.getContext())) return null;
        RequestParams rp = new RequestParams(url);
        rp.setSaveFilePath(Constans.getAppPath(Constans.APP_PATH) + "app.apk");
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

    private List<String> filelist;//下载列表
    private int loadindex = 0;
    private ConfigFileLoafCallback cflc;

    /**
     * 单个文件下载失败次数
     */
    private int error_num = 0;

    public void ConfigFileGet(List<String> filelist, ConfigFileLoafCallback cflc) {
        if (filelist != null && filelist.size() > 0) {
            this.filelist = filelist;
            this.cflc = cflc;
            jsonarr = AppUtil.getCacheList();
            fileLoad(filelist.get(loadindex));
        } else
            cflc.loadfinish();
    }

    private JSONObject jsonarr;

    public String getconfigTime(String url) {
        if (!url.contains(",")) return "0";
        return url.split(",")[1];
    }

    public void loadnext(){
        loadindex++;
        if (loadindex < filelist.size()) fileLoad(filelist.get(loadindex));
        else cflc.loadfinish();
    }

    /**
     * 静态文件下载
     *
     * @param url 文件url
     */
    private void fileLoad(String url) {
        String remote = AppUtil.fileurl2name(url, 0);
        String savepath = Constans.getAppPath(Constans.DATA_PATH) + AppUtil.fileurl2name(url, 0);

        if (jsonarr != null && jsonarr.has(remote)) {// 此段代码用于判断文件是否需要更新或删除
            String modis = "";
            try {
                modis = jsonarr.getString(remote);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if ("delete".equals(getconfigTime(url))) {
                FileUtil.deleteFile(savepath);
                loadnext();
                return;
            } else {
                if (getconfigTime(url).equals(modis)) {
                    loadnext();
                    return;
                }
            }
        }

        String urls = Config.getConfig().getRootSite() + remote;
        Log.e("url", urls);
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
                if (error_num >= 3) {//下载失败次数达到三次即下载下一个文件
                    loadindex++;
                    error_num = 0;
                }
                if (loadindex < filelist.size()) fileLoad(filelist.get(loadindex));
                else cflc.loadfinish();
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


    public String GetHtml(String html, ConfigFileLoafCallback cflc) {
        this.cflc = cflc;

        String savepath = Constans.getAppPath(Constans.HTML_PATH);
        File file = new File(savepath);
        if (!file.exists())
            file.mkdirs();
        savepath = Constans.getAppPath(Constans.HTML_PATH) + AppUtil.fileurl2name(html, 1);
        ;
        if (new File(savepath).exists())
            return savepath;
        RequestParams rp = new RequestParams(html);
        rp.setSaveFilePath(savepath);
        x.http().get(rp, new Callback.ProgressCallback<File>() {
            @Override
            public void onSuccess(File result) {
                XHttpRequest.this.cflc.loadfinish();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                error_num++;
                if (error_num >= 3) {//下载失败次数达到三次即下载下一个文件
                    error_num = 0;
                    XHttpRequest.this.cflc.loadfinish();
                } else {
                    fileLoad(filelist.get(loadindex));
                }
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
        return "";
    }


}

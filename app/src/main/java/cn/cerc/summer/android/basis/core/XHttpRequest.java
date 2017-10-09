package cn.cerc.summer.android.basis.core;

import android.app.ProgressDialog;
import android.os.Handler;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.cerc.summer.android.basis.view.ShowDialog;

/**
 * Created by fff on 2016/11/30.
 * 网络请求
 */
public class XHttpRequest implements AsyncFileLoadCallback {

    private ProgressDialog progressDialog;
    /**
     * 单个文件下载失败次数
     */
    private int error_num = 0;
    private List<String> filelist;//下载列表
    private ConfigFileLoadCallback cflc;
    private JSONObject jsonarr;
    private List<String> firstlist;
    private int firstindex = 20;
    private int filesize = 0;
    ConfigFileLoadCallback cfc = new ConfigFileLoadCallback() {
        @Override
        public void loadFinish(int size) {
            if ((filesize += size) >= filelist.size()) {
                cflc.loadAllFinish();
            }
        }

        @Override
        public void loadAllFinish() {
        }
    };

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
        if (!MyApp.getNetworkState(rc.getContext())) return;
        x.http().get(new RequestParams(url), new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                rc.success(url, result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                rc.failt(url, ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                rc.failt(url, "已取消");
            }

            @Override
            public void onFinished() {
            }
        });
    }

    /**
     * 文件下载
     *
     * @param url 文件地址
     * @param rc  回调
     * @return 可取消的回调
     */
    public Callback.Cancelable GETFile(final String url, final GetFileCallback rc) {
        if (!MyApp.getNetworkState(rc.getContext())) return null;
        RequestParams rp = new RequestParams(url);
        rp.setSaveFilePath(MyApp.getAppPath(Constans.APP_PATH) + "app.apk");
        Callback.Cancelable cc = x.http().get(rp, new Callback.ProgressCallback<File>() {
            @Override
            public void onWaiting() {
            }

            @Override
            public void onStarted() {
                if (progressDialog == null)
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
                if (error_num < 3) {
                    error_num++;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            GETFile(url, rc);
                        }
                    }, 3000);
                } else {
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    rc.failt(url, ex.toString());
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                rc.failt(url, "已取消下载");
            }

            @Override
            public void onFinished() {
//                if (progressDialog != null && progressDialog.isShowing())
//                    progressDialog.dismiss();
            }
        });
        return cc;
    }

    public void ConfigFileGet(List<String> filelist, ConfigFileLoadCallback cflc) {
        if (filelist != null && filelist.size() > 0) {
            this.filelist = filelist;
            this.cflc = cflc;
            jsonarr = MyApp.getCacheList();
            loadfile();
        } else
            cflc.loadFinish(0);
    }

    public void loadfile() {
        firstlist = new ArrayList<String>();
        if (filelist.size() < firstindex) {
            firstlist.addAll(filelist);
            new DownloadTask(firstlist, jsonarr, this).execute();
        } else {
            firstlist.addAll(filelist.subList(0, firstindex));//先下载20个文件
            new DownloadTask(firstlist, jsonarr, this).execute();
        }
    }

    @Override
    public void loadFinish(List<String> list, int fail) {
        if (list == firstlist) {
            cflc.loadFinish(fail);
            if (filelist.size() > firstindex) { //列表数量大于20则需要继续多线程下载
                filelist = filelist.subList(firstindex, filelist.size());
                for (int i = 0; i < (filelist.size() / 50); i++) {
                    new DownloadTask(filelist.subList(i * 50, ((filelist.size() - (i + 1) * 50) < 50) ? filelist.size() : ((i + 1) * 50)), jsonarr, cfc).execute();//用于启动多线程下载
                }
            }
        }
    }
}

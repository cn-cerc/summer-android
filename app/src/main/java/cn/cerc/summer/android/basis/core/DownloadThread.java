package cn.cerc.summer.android.basis.core;

import android.util.Log;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.List;

/**
 * Created by fff on 2016/12/22.
 */
public class DownloadThread extends Thread {
    private List<String> filelist;
    private JSONObject jsonarr;
    private int error_num = 0;
    private int loadindex = 0;

    private ConfigFileLoadCallback cflc;

    public DownloadThread(List<String> filelist, JSONObject jsonarr, ConfigFileLoadCallback cflc) {
        this.filelist = filelist;
        this.jsonarr = jsonarr;
        this.cflc = cflc;
    }

    @Override
    public void run() {
        super.run();
        fileLoad(filelist.get(loadindex));
    }

    public void loadnext() {
        loadindex++;
        if (loadindex < filelist.size()) fileLoad(filelist.get(loadindex));
        else cflc.loadFinish(filelist.size());
    }

    /**
     * 静态文件下载
     *
     * @param url 文件url
     */
    private void fileLoad(String url) {
        String remote = MyApp.fileurl2name(url, 0);
        String savepath = MyApp.getAppPath(Constans.DATA_PATH) + MyApp.fileurl2name(url, 0);

        if (!MyApp.needUpdate(url, jsonarr)) {
            loadnext();
            return;
        }

        String urls = WebConfig.getInstance().getRootSite() + remote;
        Log.e("url", urls);
        RequestParams rp = new RequestParams(urls);
        rp.setSaveFilePath(savepath);
        x.http().get(rp, new Callback.ProgressCallback<File>() {
            @Override
            public void onSuccess(File result) {
                loadindex++;
                if (loadindex < filelist.size()) fileLoad(filelist.get(loadindex));
                else cflc.loadFinish(filelist.size());
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                error_num++;
                if (error_num >= 3) {//下载失败次数达到三次即下载下一个文件
                    loadindex++;
                    error_num = 0;
                }
                if (loadindex < filelist.size()) fileLoad(filelist.get(loadindex));
                else cflc.loadFinish(filelist.size());
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

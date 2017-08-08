package cn.cerc.summer.android.Utils;

import android.util.Log;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.List;

import cn.cerc.summer.android.Entity.Config;
import cn.cerc.summer.android.Interface.ConfigFileLoadCallback;

/**
 * Created by fff on 2016/12/22.
 */
public class MyMultiDownloadThread extends Thread {
    private List<String> filelist;
    private JSONObject jsonarr;
    private int error_num = 0;
    private int loadindex = 0;

    private ConfigFileLoadCallback cflc;

    public MyMultiDownloadThread(List<String> filelist, JSONObject jsonarr, ConfigFileLoadCallback cflc) {
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
        else cflc.loadfinish(filelist.size());
    }

    /**
     * 静态文件下载
     *
     * @param url 文件url
     */
    private void fileLoad(String url) {
        String remote = AppUtil.fileurl2name(url, 0);
        String savepath = Constans.getAppPath(Constans.DATA_PATH) + AppUtil.fileurl2name(url, 0);

        if (!AppUtil.needUpdate(url, jsonarr)) {
            loadnext();
            return;
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
                else cflc.loadfinish(filelist.size());
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                error_num++;
                if (error_num >= 3) {//下载失败次数达到三次即下载下一个文件
                    loadindex++;
                    error_num = 0;
                }
                if (loadindex < filelist.size()) fileLoad(filelist.get(loadindex));
                else cflc.loadfinish(filelist.size());
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

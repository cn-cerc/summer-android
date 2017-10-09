package cn.cerc.summer.android.basis.core;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.List;

/**
 * Created by fengm on 2016/12/23.
 */

public class DownloadTask extends AsyncTask<String, Integer, List<String>> {

    private List<String> filelist;
    private JSONObject jsonarr;
    private int error_num = 0;
    private int loadindex = 0;

    private int fail_num = 0;//下载失败的文件数

    private AsyncFileLoadCallback afc;
    private ConfigFileLoadCallback cflc;

    public DownloadTask(List<String> filelist, JSONObject jsonarr, AsyncFileLoadCallback afc) {
        this.filelist = filelist;
        this.jsonarr = jsonarr;
        this.afc = afc;
    }

    public DownloadTask(List<String> filelist, JSONObject jsonarr, ConfigFileLoadCallback cflc) {
        this.filelist = filelist;
        this.jsonarr = jsonarr;
        this.cflc = cflc;
    }

    @Override
    protected List<String> doInBackground(String... urls) {
        fileLoad(filelist.get(loadindex));
        return filelist;
    }

    @Override
    protected void onPostExecute(List<String> strings) {
        super.onPostExecute(strings);
    }

    public void loadnext() {
        loadindex++;
        if (loadindex < filelist.size()) fileLoad(filelist.get(loadindex));
        else if (afc != null) afc.loadFinish(filelist, fail_num);
        else if (cflc != null) cflc.loadFinish(filelist.size());
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
                else if (afc != null) afc.loadFinish(filelist, fail_num);
                else if (cflc != null) cflc.loadFinish(filelist.size());
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                error_num++;
                if (error_num >= 3) {//下载失败次数达到三次即下载下一个文件
                    loadindex++;
                    error_num = 0;
                    fail_num++;
                }
                if (loadindex < filelist.size()) fileLoad(filelist.get(loadindex));
                else if (afc != null) afc.loadFinish(filelist, fail_num);
                else if (cflc != null) cflc.loadFinish(filelist.size());
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

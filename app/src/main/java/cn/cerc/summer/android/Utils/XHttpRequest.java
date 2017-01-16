package cn.cerc.summer.android.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import cn.cerc.summer.android.Entity.Config;
import cn.cerc.summer.android.Interface.AsyncFileLoafCallback;
import cn.cerc.summer.android.Interface.ConfigFileLoafCallback;
import cn.cerc.summer.android.Interface.GetFileCallback;
import cn.cerc.summer.android.Interface.RequestCallback;
import cn.cerc.summer.android.MyConfig;
import cn.cerc.summer.android.View.ShowDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by fff on 2016/11/30.
 * 网络请求
 */
public class XHttpRequest implements AsyncFileLoafCallback {

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
        if (!AppUtil.getNetWorkStata(rc.getContext())){
            Toast.makeText(rc.getContext(),"请检查网络",Toast.LENGTH_SHORT).show();
            ((Activity)rc.getContext()).finish();
            return;
        }
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

    /**
     * POST请求
     */
    public void POST(final String url, HashMap<String, String> map, final RequestCallback rc) {
        if (!AppUtil.getNetWorkStata(rc.getContext())) return;
        RequestParams param = new RequestParams(url);
        Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            String value = map.get("key");
            if ("img".equals(key)) param.addBodyParameter(key, new File(value));
            else param.addBodyParameter(key, value);
        }
        x.http().post(param, new Callback.CommonCallback<JSONObject>() {
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

    File eng_file = new File(Constans.getAppPath(Constans.TESSDATA_PATH) + "/eng.traineddata");
    /**
     * 获取语言识别库文件
     */
    public void getTess(final String url){
        File file = new File(Constans.getAppPath(Constans.TESSDATA_PATH) + "/eng.traineddata");
        if (file.exists()) return;

//        RequestParams request = new RequestParams("http://ehealth.lucland.com/eng.traineddata");//这个url待修改，来适配扫卡
        RequestParams request = new RequestParams(url);
        request.setSaveFilePath(Constans.getAppPath(Constans.TESSDATA_PATH) + "/eng.traineddata");
        x.http().get(request, new Callback.CommonCallback<File>() {
            @Override
            public void onSuccess(File result) {
//                Toast.makeText(StartActivity.this,"下载完成了", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if(eng_file.exists())eng_file.delete();
                getTess(url);
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }


    private ProgressDialog progressDialog;

    /**
     * 单个文件下载失败次数
     */
    private int error_num = 0;

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
                    rc.Failt(url, ex.toString());
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                rc.Failt(url, "已取消下载");
            }

            @Override
            public void onFinished() {
//                if (progressDialog != null && progressDialog.isShowing())
//                    progressDialog.dismiss();
            }
        });
        return cc;
    }

    private List<String> filelist;//下载列表
    private ConfigFileLoafCallback cflc;
    private JSONObject jsonarr;

    private List<String> firstlist;
    private int firstindex = 20;

    public void ConfigFileGet(List<String> filelist, ConfigFileLoafCallback cflc) {
        if (filelist != null && filelist.size() > 0) {
            this.filelist = filelist;
            this.cflc = cflc;
            jsonarr = AppUtil.getCacheList();
            loadfile();
        } else
            cflc.loadfinish(0);
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
    public void loadfinish(List<String> list, int fail) {
        if (list == firstlist) {
            cflc.loadfinish(fail);
            if (filelist.size() > firstindex) { //列表数量大于20则需要继续多线程下载
                filelist = filelist.subList(firstindex, filelist.size());
                for (int i = 0; i < (filelist.size() / 50); i++) {
                    new DownloadTask(filelist.subList(i * 50, ((filelist.size() - (i + 1) * 50) < 50) ? filelist.size() : ((i + 1) * 50)), jsonarr, cfc).execute();//用于启动多线程下载
                }
            }
        }
    }

    private int filesize = 0;

    ConfigFileLoafCallback cfc = new ConfigFileLoafCallback() {
        @Override
        public void loadfinish(int size) {
            if ((filesize += size) >= filelist.size()) {
                cflc.loadAllfinish();
            }
        }

        @Override
        public void loadAllfinish() {
        }
    };
}

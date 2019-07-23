package com.yt.hz.financial.argame.util;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.content.Context;
import android.os.AsyncTask;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class Downloader {
    public static final String TASK_NAME = "download";
    private static int sBufferSize = 65536;
    private static int sConnectTimeout = 10000;
    private static int sReadTimeout = 10000;
    public static final String PACKAGES_PATH = "pacakges";
    public static final String TARGETS_PATH = "targets";

    Downloader() {
    }

    public static void setBufferSize(int size) {
        sBufferSize = size;
    }

    public static void setConnectTimeout(int timeout) {
        sConnectTimeout = timeout;
    }

    public static void setReadTimeout(int timeout) {
        sReadTimeout = timeout;
    }

    public static String getLocalName(String url) {
        try {
           // return Auth.sha1Hex(url);
        } catch (Exception var2) {
            return "placeholder";
        }
        return "placeholder";
    }

    public static String getDownloadPath(Context context, String path) {
        String dir = String.format("%s/%s", new Object[]{context.getFilesDir().getAbsolutePath(), path});
        (new File(dir)).mkdirs();
        return dir;
    }

    public static void download(String url, String dst, boolean force, AsyncCallback<String> cb) {
        File dstFile = new File(dst);
        if(dstFile.exists() && !force) {
            cb.onSuccess(dstFile.getAbsolutePath());
        } else {
            (new DownloadTask(url, dst, cb)).execute(new Void[0]);
        }
    }

    private static class DownloadTask extends AsyncTask<Void, Double, Void> {
        private String mURL;
        private String mDst;
        private AsyncCallback<String> mCb;
        private Throwable mThrowable;

        public DownloadTask(String url, String dst, AsyncCallback<String> cb) {
            this.mURL = url;
            this.mDst = dst;
            this.mCb = cb;
            this.mThrowable = null;
        }

        protected Void doInBackground(Void... params) {
            String tempDst = String.format("%s.download", new Object[]{this.mDst});

            try {
                HttpURLConnection e = null;
                InputStream is = null;
                FileOutputStream os = null;

                try {
                    URL url = new URL(this.mURL);
                    e = (HttpURLConnection)url.openConnection();
                    e.setConnectTimeout(Downloader.sConnectTimeout);
                    e.setReadTimeout(Downloader.sReadTimeout);
                    e.setInstanceFollowRedirects(true);
                    e.connect();
                    if(e.getResponseCode() != 200) {
                        throw new Exception(String.format("%d: %s", new Object[]{Integer.valueOf(e.getResponseCode()), e.getResponseMessage()}));
                    }

                    int contentLength = e.getContentLength();
                    is = e.getInputStream();
                    os = new FileOutputStream(tempDst);
                    byte[] buffer = new byte[Downloader.sBufferSize];
                    boolean received = false;

                    int received1;
                    for(int totalReceived = 0; (received1 = is.read(buffer)) != -1; os.write(buffer, 0, received1)) {
                        totalReceived += received1;
                        if(contentLength > 0) {
                            this.publishProgress(new Double[]{Double.valueOf((double)totalReceived / (double)contentLength)});
                        }
                    }

                    os.close();
                    os = null;
                    if(!(new File(tempDst)).renameTo(new File(this.mDst))) {
                        throw new Exception("Cannot move downloaded file into position");
                    }
                } finally {
                    if(os != null) {
                        os.close();
                    }

                    if(is != null) {
                        is.close();
                    }

                    if(e != null) {
                        e.disconnect();
                    }

                }
            } catch (Exception var15) {
                this.mThrowable = var15;
            }

            return null;
        }

        protected void onPostExecute(Void aVoid) {
            if(this.mThrowable != null) {
                this.mCb.onFail(this.mThrowable);
            } else {
                this.mCb.onSuccess(this.mDst);
            }

        }

        protected void onProgressUpdate(Double... values) {
            this.mCb.onProgress("download", values[0].floatValue());
        }
    }
}

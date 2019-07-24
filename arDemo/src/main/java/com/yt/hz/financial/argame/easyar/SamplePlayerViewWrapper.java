package com.yt.hz.financial.argame.easyar;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.easy.occlient.OCUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.easyar.FunctorOfVoidFromPermissionStatusAndString;
import cn.easyar.FunctorOfVoidFromRecordStatusAndString;
import cn.easyar.IRenderNode;
import cn.easyar.PermissionStatus;
import cn.easyar.PlayerView;
import cn.easyar.RecordStatus;
import cn.easyar.RecordVideoOrientation;
import cn.easyar.Recorder;
import cn.easyar.RecorderConfiguration;
import cn.easyar.RouteCenter;
import cn.easyar.engine.EasyARPlayer;

/**
 * Created by renqilin on 2017/12/25.
 */

// 演示了一个简单的PlayerView包装
public class SamplePlayerViewWrapper {
//    private static final String EASYAR_SDK_KEY = "c7aFTHaD6HA3cTP0FstNTfWm7onHIQixQdv0Lv42brEYS96LYSpn7YFmesGco243x0ofcyxVceOn4cbYWEqW84he44q5wx2rqrAJg5findmAOz60j0YOW4OWUJYOXLuiNapVnPUCUQ0w1H2IVKmXvsNxRaJQF9Jhb2u06CmNHSpqCRPLuFcmClLPv9HupGpmxuLNPqHv";
    private static final String EASYAR_SDK_KEY = "NVGlzTFCvdEpI5sY4cXs/jNxe+eCzdhVdpWNtgVjk+Yxc5X7BX6Ctkoymv0YcZ/4EX6R1AN5kfwEYNj3H33UuFJ9l+cEdYTfFWm/8FIqx7hSfJ/3FX6F8QMyzM8LMpThHnSa8Tl0hbZKS9T3H33Y8Rxmk+decYbkUjzU9x992PEcZpPnXnGG5FJN2rYGcYT9EX6C51IqrbYAfJftFWLUuFJghPtSPNT2EWOf91JN2rYAfJfgFn+E+QMyzM9SZ5/6FH+B51I81PkRc9TJXDKT7AB5hPEkeZvxI2SX+QAyzPoFfJq4UnmF2B9zl/hSKpD1HGOT6Vxr1PYFfpL4FVmS51IqrbYTf5u6FXyA8QM+l+QAMqu4UmaX5hlxmOADMszPUmCa9Ql1hLZcMobmHzLathJxhf0TMqu4UmCa9QR2meYdY9SuKzKX+hRimf0UMqu4UnWO5Blik8AZfZPHBHGb5FIqmOEcfNq2GWO6+xNxmrZKdpf4A3WLuAsylOEedJrxOXSFtkpL1PcffdjxHGaT515xhuRSTdq2BnGE/RF+gudSKq22AHyX7RVi1LhSYIT7UjzU9hFjn/dSTdq2AHyX4BZ/hPkDMszPUnmZ51JN2rYVaIb9AnWi/R11peARfYa2Sn6D+Bw81P0DXJn3EXzUrhZxmucVbavpAwFc7E+4A+t4jxRdHBZIPIkKsdC3Vb2wpr72G8caUnHlXrnXbpv7Dbvzr0i2MVaJT+Wd1HX3EJTWs4KvYYEslyP6YWIWIxZv7TQL8EU485pO+5S3KczsPwxq1YvojgFhVqNTiSos4HhjNY4JwFP/zRf7nwicoowxeWIvXg99kbBVeckjxcNIY+XCHq7USTWmo0JAKioLyWMANw099mLr7W1nOk5IsQ66qsIZT+MM3oPSs66C8fTWd0V31uvHcdKUdU9I/Jb/d75KNSP4DTi76JZwIMhJ/vzy0ug/WCTp69uX9RX1ag3ZC9Y4jJtNVHiSDPSnXVhmJkj3MueWcBD2lA==";

    private PlayerView mPlayerView = null;
    private Activity mOwnerActivity = null;
    private UserFileSystem mFileSystem = null;
    private RouteCenter center = null;
    private boolean mVedioPermissionAllow = false;


    public SamplePlayerViewWrapper(Activity activity) {
        mOwnerActivity = activity;

        EasyARPlayer.initialize(activity, EASYAR_SDK_KEY);


        mFileSystem = new UserFileSystem();
        OCUtil.getInstent().setStorageDirectory(mOwnerActivity.getFilesDir().getAbsolutePath());
        mFileSystem.setUserRootDir(mOwnerActivity.getFilesDir().getAbsolutePath());

        mPlayerView = new PlayerView(activity);
        mPlayerView.setFileSystem(mFileSystem);
    }

    public PlayerView getPlayerView() {
        return mPlayerView;
    }

    public RouteCenter getRouteCenter() {
        return center;
    }

    public void dispose() {
        if (mPlayerView != null) {
            mPlayerView.dispose();
            mPlayerView = null;
        }
    }

    public void clearCache() {
        if (mFileSystem != null) {
            mFileSystem.clearCache();
        }
    }

    public void loadPackage(final String path, PlayerView.OnLoadPackageFinish callback) {
        if (mPlayerView != null) {
            mPlayerView.loadPackage(path, callback);
        }
    }

    public void onResume() {
        mPlayerView.onResume();
    }

    public void onPause() {
        mPlayerView.onPause();
    }

    public void snapshot()
    {
        if (mPlayerView == null)
        {
            return;
        }
        mPlayerView.snapShot(new PlayerView.AsyncCallback<Bitmap>() {
            @Override
            public void onSuccess(final Bitmap result) {

                mOwnerActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });

                Log.d("EasyAR", "snapshot " + result.toString());
            }

            @Override
            public void onFail(Throwable t) {
                Log.d("EasyAR", "snapshot " + t.toString());
            }
        });
    }

    public void getReadyRecorder(){
        if (!mVedioPermissionAllow) {
            //stateSwitching = true;
            if(recorderLisener!=null){
                recorderLisener.recorderSetEnabled(false);
            }
            Recorder.requestPermissions(cn.easyar.ImmediateCallbackScheduler.getDefault(), new FunctorOfVoidFromPermissionStatusAndString() {
                @Override
                public void invoke(final int i, final String s) {
                    mOwnerActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            permissionStatus(i, s);
                        }
                    });
                }
            });
            return;
        }
    }
    private void permissionStatus(int status, String msg) {
        switch (status) {
            case PermissionStatus.Denied:
                mVedioPermissionAllow = false;
                break;
            case PermissionStatus.Error:
                mVedioPermissionAllow = false;
                break;
            case PermissionStatus.Granted:
                mVedioPermissionAllow = true;
                if(recorderLisener!=null){
                    recorderLisener.recorderSetText("Capture");
                }
                break;
        }
        if(recorderLisener!=null){
            recorderLisener.recorderSetEnabled(true);
        }
    }
    private boolean mIsRecording = false;
    private Recorder mRecorder;
    private IRenderNode mRenderNode;
    public void optionRecorde(){
        if (!Recorder.isAvailable()) {
            return;
        }
        assert (mPlayerView != null);
        PlayerView playerView = getPlayerView();
        if (!mIsRecording) {
            assert (mRecorder == null);
            if(recorderLisener!=null){
                recorderLisener.recorderSetEnabled(false);
            }
            mIsRecording = true;
            RecorderConfiguration config = new RecorderConfiguration();
            config.setOutputFile(prepareUrl());
            if (playerView.getWidth() > playerView.getHeight()) {
                config.setVideoOrientation(RecordVideoOrientation.Landscape);
            } else {
                config.setVideoOrientation(RecordVideoOrientation.Portrait);
            }
            //ui-thread
            mRecorder = Recorder.create(config, cn.easyar.ImmediateCallbackScheduler.getDefault(), new FunctorOfVoidFromRecordStatusAndString() {
                @Override
                public void invoke(final int i, final String s) {
                    mOwnerActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recordStatus(i, s);
                        }
                    });
                }
            });
            mRenderNode = new IRenderNode() {
                @Override
                public void onRender(int textureId, int width, int height) {
                    Recorder recorder = mRecorder;
                    if (recorder != null)
                        recorder.updateFrame(cn.easyar.TextureId.fromInt(textureId), width, height);

                }
            };
            playerView.addPostRenderNode(mRenderNode);
            mRecorder.start();
        } else {
            assert (mRecorder != null);
            if(recorderLisener!=null){
                recorderLisener.recorderSetEnabled(false);
            }
            mIsRecording = false;

            mRecorder.stop();
            if (mRenderNode != null)
                playerView.removePostRenderNode(mRenderNode);
            mRenderNode = null;
            mRecorder = null;
        }
    }

    private String prepareUrl() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String FileName = "VID_" + timeStamp + "_" + ".mp4";
        String mDir = Environment.getExternalStorageDirectory() + "/DCIM/";
        File file = new File(mDir);
        if(!file.exists()){
            file.mkdirs();
        }
        return mDir + FileName;
    }

    private void recordStatus(int status, String msg) {
        switch (status) {
            case RecordStatus.FailedToStart:
                //assert (stateSwitching == false);
                stopRecorder();
                break;
            case RecordStatus.OnStarted:
                //assert (stateSwitching == false);
                if(recorderLisener!=null){
                    recorderLisener.recorderSetText("Stop");
                }
                break;
            case RecordStatus.OnStopped:
                //assert (stateSwitching == false);
                if(recorderLisener!=null){
                    recorderLisener.recorderSetText("Start");
                }
                break;
        }
        if(recorderLisener!=null){
            recorderLisener.recorderSetEnabled(true);
        }
        //stateSwitching = false;
    }

    private void stopRecorder() {
        if(recorderLisener!=null){
            recorderLisener.recorderSetText("Capture");
        }
        mRecorder.stop();
        if (mRenderNode != null)
            getPlayerView().removePostRenderNode(mRenderNode);

        if(recorderLisener!=null){
            recorderLisener.recorderSetEnabled(true);
        }
        mRenderNode = null;
        mRecorder = null;
        mIsRecording = false;
    }

    public interface RecorderLisener{
        void recorderSetText(String text);
        void recorderSetEnabled(boolean flag);
    }

    public void setRecorderLisener(RecorderLisener recorderLisener) {
        this.recorderLisener = recorderLisener;
    }

    private RecorderLisener recorderLisener;

}

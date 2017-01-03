package cn.cerc.summer.android.Activity;

import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import com.huagu.ehealth.R;

import java.io.IOException;

import cn.cerc.summer.android.zxing.camera.CameraManager;
import cn.cerc.summer.android.zxing.decoding.CaptureActivityHandler;
import cn.cerc.summer.android.zxing.decoding.InactivityTimer;
import cn.cerc.summer.android.zxing.view.ViewfinderView;

public class ScanCardActivity extends BaseActivity implements SurfaceHolder.Callback {

    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private CaptureActivityHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_card);

        CameraManager.init(getApplication());
//        CameraManager.get().ScanQRorCaer(CameraManager.CARD);
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);

        ImageView mButtonBack = (ImageView) findViewById(R.id.back);
        mButtonBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ScanCardActivity.this.finish();

            }
        });
        hasSurface = false;
    }

    private SurfaceView surfaceView;

    @Override
    protected void onResume() {
        super.onResume();
        surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        CameraManager.get().startPreview();
        viewfinderView.drawViewfinder();
    }

    private void initCamera(SurfaceHolder holder) {
        try {
            CameraManager.get().openDriver(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (handler == null) {
//            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        CameraManager.get().closeDriver();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }
}

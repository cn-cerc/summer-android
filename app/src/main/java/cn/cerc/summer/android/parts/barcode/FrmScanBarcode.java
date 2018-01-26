package cn.cerc.summer.android.parts.barcode;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.mimrc.vine.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import cn.cerc.summer.android.basis.RemoteForm;
import cn.cerc.summer.android.core.MyApp;
import cn.cerc.summer.android.forms.FrmMain;
import cn.cerc.summer.android.forms.JavaScriptResult;
import cn.cerc.summer.android.parts.barcode.zxing.camera.CameraManager;
import cn.cerc.summer.android.parts.barcode.zxing.decoding.CaptureActivityHandler;
import cn.cerc.summer.android.parts.barcode.zxing.decoding.InactivityTimer;
import cn.cerc.summer.android.parts.barcode.zxing.decoding.RGBLuminanceSource;
import cn.cerc.summer.android.parts.barcode.zxing.view.ViewfinderView;

public class FrmScanBarcode extends AppCompatActivity implements Callback, View.OnClickListener {
    public final static int SCANNIN_GREQUEST_CODE = 1;
    private static final long VIBRATE_DURATION = 200L;
    private static final float BEEP_VOLUME = 0.10f;
    private static final int PARSE_BARCODE_SUC = 300;
    private static final int PARSE_BARCODE_FAIL = 303;
    private static final int MSG_UPLOAD = 2;
    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private boolean vibrate;
    private ProgressDialog mProgress;
    private String photo_path;
    private Bitmap scanBitmap;
    private String scriptFunction = "";
    private String scriptTag = "";
    private String postUrl = "";
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mProgress.dismiss();
            switch (msg.what) {
                case PARSE_BARCODE_SUC:
                    onResultHandler((String) msg.obj, scanBitmap);
                    break;
                case PARSE_BARCODE_FAIL:
                    Toast.makeText(FrmScanBarcode.this, (String) msg.obj, Toast.LENGTH_LONG).show();
                    break;
                case MSG_UPLOAD:
                    RemoteForm rf = (RemoteForm) msg.obj;
                    if (rf.isOk()) {
                        String data = rf.getData();
                        JSONObject json = null;
                        try {
                            json = new JSONObject(data);
                            FrmMain.getInstance().loadUrl(MyApp.getFormUrl(json.getString("forms")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(FrmScanBarcode.this, "上传失败！", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(FrmScanBarcode.this, "上传失败！", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;

            }
        }

    };

    //内部调试使用
    public static void startForm(AppCompatActivity context) {
        Intent intent = new Intent();
        intent.setClass(context, FrmScanBarcode.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
    }

    //调用扫描画面并回调javaScript
    public static void startForm(AppCompatActivity context, String scriptFunction) {
        Intent intent = new Intent();
        intent.putExtra("scriptFunction", scriptFunction);
        intent.setClass(context, FrmScanBarcode.class);
        context.startActivity(intent);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frm_scan_barcode);

        Intent intent = getIntent();
        if (intent.hasExtra("scriptFunction")) {
            this.scriptFunction = intent.getStringExtra("scriptFunction");
        } else if (intent.hasExtra("postUrl")) {
            this.postUrl = intent.getStringExtra("postUrl");
        } else {
            this.setTitle("传入参数有误！");
        }

        CameraManager.init(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);

        Button mButtonBack = (Button) findViewById(R.id.button_back);
        mButtonBack.setOnClickListener(this);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);

    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            switch (requestCode) {
//                case REQUEST_CODE:
//                    //获取选中图片的路径
//                    Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
//                    if (cursor.moveToFirst()) {
//                        photo_path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//                    }
//                    cursor.close();
//
//                    mProgress = new ProgressDialog(FrmScanBarcode.this);
//                    mProgress.setMessage("正在扫描...");
//                    mProgress.setCancelable(false);
//                    mProgress.show();
//
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Result result = scanningImage(photo_path);
//                            if (result != null) {
//                                Message m = mHandler.obtainMessage();
//                                m.what = PARSE_BARCODE_SUC;
//                                m.obj = result.getText();
//                                mHandler.sendMessage(m);
//                            } else {
//                                Message m = mHandler.obtainMessage();
//                                m.what = PARSE_BARCODE_FAIL;
//                                m.obj = "Scan failed!";
//                                mHandler.sendMessage(m);
//                            }
//                        }
//                    }).start();
//
//                    break;
//
//            }
//        }
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_back:
                this.finish();
                break;
//            case R.id.button_function:
//                //打开手机中的相册
//                Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT); //"android.intent.action.GET_CONTENT"
//                innerIntent.setType("image/*");
//                Intent wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片");
//                this.startActivityForResult(wrapperIntent, REQUEST_CODE);
//                break;
        }
    }

    /**
     * 识别二维码图片中的数据
     *
     * @param path
     * @return
     */
    public Result scanningImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); //设置二维码内容的编码

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);
        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(bitmap1, hints);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * 处理扫描结果
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        onResultHandler(resultString, barcode);
    }

    /**
     * 跳转到上一个页面
     *
     * @param resultStr
     * @param bitmap
     */
    private void onResultHandler(String resultStr, Bitmap bitmap) {
        if (TextUtils.isEmpty(resultStr)) {
            Toast.makeText(FrmScanBarcode.this, "Scan failed!", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent resultIntent = new Intent();
        this.setResult(RESULT_OK, resultIntent);

        FrmMain obj = FrmMain.getInstance();
        if (!"".equals(this.scriptFunction)) {
            JavaScriptResult json = new JavaScriptResult();
            json.setResult(true);
            if (!"".equals(resultStr) && resultIntent != null) {
                json.setData(resultStr);
            } else {
                json.setResult(false);
            }
            MyApp.getInstance().executiveJS(scriptFunction,json.toString());
            //  RemoteForm   remoteForm  = new RemoteForm(postUrl==null||postUrl.equals("")?postUrl)

        } else if (!"".equals(this.postUrl)) {
            obj.loadUrl(String.format("%s?barcode=%s", this.postUrl, resultStr));

            Log.e("URl", postUrl);
        } else {
            Toast.makeText(this, "参数调用有误！", Toast.LENGTH_LONG).show();
        }
        FrmScanBarcode.this.finish();
    }

    private void requestUpload(final String forms, final String resultString) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RemoteForm rf = new RemoteForm(forms);
                rf.putParam("resultString", resultString);
                handler.sendMessage(rf.execByMessage(MSG_UPLOAD));
            }
        }).start();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }
}
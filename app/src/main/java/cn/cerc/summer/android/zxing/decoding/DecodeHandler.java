/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.cerc.summer.android.zxing.decoding;

import java.io.IOException;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.huagu.ehealth.R;

import cn.cerc.summer.android.Activity.MipcaActivityCapture;

import com.googlecode.tesseract.android.TessBaseAPI;

import cn.cerc.summer.android.MyApplication;
import cn.cerc.summer.android.Utils.ClearImageHelper;
import cn.cerc.summer.android.zxing.camera.CameraManager;
import cn.cerc.summer.android.zxing.camera.PlanarYUVLuminanceSource;

final class DecodeHandler extends Handler {

    private static final String TAG = DecodeHandler.class.getSimpleName();

    private final MipcaActivityCapture activity;
    private final MultiFormatReader multiFormatReader;
    private TessBaseAPI tessBaseAPI = null ;
//    private final ClearImageHelper cih;

    DecodeHandler(MipcaActivityCapture activity, Hashtable<DecodeHintType, Object> hints) {
        multiFormatReader = new MultiFormatReader();
        multiFormatReader.setHints(hints);

        if ("card".equals(activity.getScanType())) {
            try {
                tessBaseAPI = new TessBaseAPI();
                tessBaseAPI.setDebug(true);
                tessBaseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO_OSD);
                String path = MyApplication.getInstance().getExternalFilesDir("").getAbsolutePath();
                Log.e("path : ", path);
                boolean bool = tessBaseAPI.init(path, "eng");//eng  /chi_sim
            } catch (IllegalArgumentException e) {
                Toast.makeText(activity, "初始化失败", Toast.LENGTH_SHORT).show();
                activity.finish();
                e.printStackTrace();
            }
        }
//        cih = new ClearImageHelper();
        this.activity = activity;
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case R.id.decode:
                //Log.d(TAG, "Got decode message");
                decode((byte[]) message.obj, message.arg1, message.arg2);
                break;
            case R.id.quit:
                Looper.myLooper().quit();
                break;
        }
    }

    /**
     * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency,
     * reuse the same reader objects from one decode to the next.
     *
     * @param data   The YUV preview frame.
     * @param width  The width of the preview frame.
     * @param height The height of the preview frame.
     */
    private void decode(byte[] data, int width, int height) {
        long start = System.currentTimeMillis();
        Result rawResult = null;

        //modify here
        byte[] rotatedData = new byte[data.length];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++)
                rotatedData[x * height + height - y - 1] = data[x + y * width];
        }
        int tmp = width; // Here we are swapping, that's the difference to #11
        width = height;
        height = tmp;

        PlanarYUVLuminanceSource source = CameraManager.get().buildLuminanceSource(rotatedData, width, height);
        if ("card".equals(activity.getScanType())) {
            String CardCode = decodeBitmap(source);
            if (CardCode != null) {
                rawResult = new Result(CardCode, null, null, null);
                tessBaseAPI.stop();
            } else tessBaseAPI.clear();
        } else {
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            try {
                rawResult = multiFormatReader.decodeWithState(bitmap);
            } catch (ReaderException re) {
                // continue
            } finally {
                multiFormatReader.reset();
            }
        }
        if ((System.currentTimeMillis() - endtine) > 1000)
            if (rawResult != null) {
                endtine = System.currentTimeMillis();
                Log.d(TAG, "Found barcode (" + (endtine - start) + " ms):\n" + rawResult.toString());
                Message message = Message.obtain(activity.getHandler(), R.id.decode_succeeded, rawResult);
                Bundle bundle = new Bundle();
                bundle.putParcelable(DecodeThread.BARCODE_BITMAP, source.renderCroppedGreyscaleBitmap());
                message.setData(bundle);
                //Log.d(TAG, "Sending decode succeeded message...");
                message.sendToTarget();
            } else {
                Message message = Message.obtain(activity.getHandler(), R.id.decode_failed);
                message.sendToTarget();
            }
    }

    long endtine = 0;

    public String decodeBitmap(PlanarYUVLuminanceSource source) {
        Bitmap bitmap = source.renderCroppedGreyscaleBitmap();//获取灰度图
        tessBaseAPI.setImage(bitmap);
        String recognizedText = tessBaseAPI.getUTF8Text();
        return getCardCode(recognizedText);
    }

    public String getCardCode(String recognizedText) {
        Pattern p = Pattern.compile("\\d{17}");//正则匹配卡号
        Matcher m = p.matcher(recognizedText);
        String code = "";
        while (m.find()) {
            code = m.group();
        }
        if (!TextUtils.isEmpty(code) && code.length() == 17) {
            Log.e("cururl", code + " code:");
            return code;
        } else return null;
    }

}
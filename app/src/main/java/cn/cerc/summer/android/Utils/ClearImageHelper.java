package cn.cerc.summer.android.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import java.io.File;
import java.io.IOException;


/**
 * Created by fff on 2016/12/29.
 */

public class ClearImageHelper {

    public ClearImageHelper() {
    }

    /**
     * 图片去色,返回灰度图片
     *
     * @param bmp Original 传入的图片
     * @return 去色后的图片
     */
    public static Bitmap toGrayscale(Bitmap bmp) {
        int width, height;
        Paint paint = new Paint();
        height = bmp.getHeight();
        width = bmp.getWidth();
        Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bm);
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmp, 0, 0, paint);
        return bm;
    }

    /**
     * @param bufferedImage   需要去噪的图像
     * @throws IOException
     */
    public static Bitmap cleanImage(Bitmap bufferedImage) throws IOException {

        int h = bufferedImage.getHeight();
        int w = bufferedImage.getWidth();

        // 灰度化
        int[][] gray = new int[w][h];
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int argb = bufferedImage.getPixel(x, y);
                // 图像加亮（调整亮度识别率非常高）
                int r = (int) (((argb >> 16) & 0xFF) * 1.1 + 30);
                int g = (int) (((argb >> 8) & 0xFF) * 1.1 + 30);
                int b = (int) (((argb >> 0) & 0xFF) * 1.1 + 30);
                if (r >= 255) {
                    r = 255;
                }
                if (g >= 255) {
                    g = 255;
                }
                if (b >= 255) {
                    b = 255;
                }
                gray[x][y] = (int) Math.pow((Math.pow(r, 2.2) * 0.2973 + Math.pow(g, 2.2) * 0.6274 + Math.pow(b, 2.2) * 0.0753), 1 / 2.2);
            }
        }

        // 二值化
        int threshold = ostu(gray, w, h);
        Bitmap binaryBufferedImage = Bitmap.createBitmap(w, h, Bitmap.Config.ALPHA_8);
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (gray[x][y] > threshold) gray[x][y] |= 0x00FFFF;
                else gray[x][y] &= 0xFF0000;
                binaryBufferedImage.setPixel(x, y, gray[x][y]);
            }
        }

        return binaryBufferedImage;
    }

    public static boolean isBlack(int colorInt) {
        if (Color.red(colorInt) + Color.green(colorInt) + Color.blue(colorInt) <= 300) {
            return true;
        }
        return false;
    }

    public static boolean isWhite(int colorInt) {
        if (Color.red(colorInt) + Color.green(colorInt) + Color.blue(colorInt) > 300) {
            return true;
        }
        return false;
    }

    public static int isBlackOrWhite(int colorInt) {
        if (getColorBright(colorInt) < 30 || getColorBright(colorInt) > 730) {
            return 1;
        }
        return 0;
    }

    public static int getColorBright(int colorInt) {
        return Color.red(colorInt) + Color.green(colorInt) + Color.blue(colorInt);
    }

    public static int ostu(int[][] gray, int w, int h) {
        int[] histData = new int[w * h];
        // Calculate histogram
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int red = 0xFF & gray[x][y];
                histData[red]++;
            }
        }

        // Total number of pixels
        int total = w * h;

        float sum = 0;
        for (int t = 0; t < 256; t++)
            sum += t * histData[t];

        float sumB = 0;
        int wB = 0;
        int wF = 0;

        float varMax = 0;
        int threshold = 0;

        for (int t = 0; t < 256; t++) {
            wB += histData[t]; // Weight Background
            if (wB == 0)
                continue;

            wF = total - wB; // Weight Foreground
            if (wF == 0)
                break;

            sumB += (float) (t * histData[t]);

            float mB = sumB / wB; // Mean Background
            float mF = (sum - sumB) / wF; // Mean Foreground

            // Calculate Between Class Variance
            float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);

            // Check if new maximum found
            if (varBetween > varMax) {
                varMax = varBetween;
                threshold = t;
            }
        }

        return threshold;
    }

}

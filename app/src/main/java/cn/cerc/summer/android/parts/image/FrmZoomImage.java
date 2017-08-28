package cn.cerc.summer.android.parts.image;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.mimrc.vine.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FrmZoomImage extends AppCompatActivity implements View.OnClickListener {
    private ImageView content;
    private ImageView image;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frm_zoom_image);
        content = (ImageView) findViewById(R.id.imgZoomBack);
        image = (ImageView) findViewById(R.id.imgZoomView);

        content.setOnClickListener(this);
        image.setOnClickListener(this);

        //加入网络图片地址
        String url = getIntent().getStringExtra("url");
        new DownloadTask().execute(url);
    }

    public static void startForm(Context context, String urlImage) {
        Intent intent = new Intent();
        intent.setClass(context, FrmZoomImage.class);
        intent.putExtra("url", urlImage);
        context.startActivity(intent);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgZoomBack:
                finish();
                break;
            case R.id.imgZoomView:
                //点击图片后将图片保存到SD卡跟目录下的Test文件夹内
                savaImage(bitmap, Environment.getExternalStorageDirectory().getPath() + "/Test");
                Toast.makeText(getBaseContext(), "图片保存", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }


    /**
     * 异步线程下载图片
     */
    class DownloadTask extends AsyncTask<String, Integer, Void> {

        protected Void doInBackground(String... params) {
            bitmap = getImageInputStream((String) params[0]);
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Message message = new Message();
            message.what = 0x123;
            handler.sendMessage(message);
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0x123) {
                image.setImageBitmap(bitmap);
            }
        }
    };

    /**
     * 获取网络图片
     *
     * @param imageUrl 图片网络地址
     * @return Bitmap 返回位图
     */
    private Bitmap getImageInputStream(String imageUrl) {
        URL url;
        HttpURLConnection connection = null;
        Bitmap bitmap = null;
        try {
            url = new URL(imageUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(6000); //超时设置
            connection.setDoInput(true);
            connection.setUseCaches(false); //设置不使用缓存
            InputStream inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 保存位图到本地
     *
     * @param bitmap
     * @param path   本地路径
     * @return void
     */
    private void savaImage(Bitmap bitmap, String path) {
        File file = new File(path);
        FileOutputStream fileOutputStream = null;
        //文件夹不存在，则创建它
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            fileOutputStream = new FileOutputStream(path + "/" + System.currentTimeMillis() + ".png");
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

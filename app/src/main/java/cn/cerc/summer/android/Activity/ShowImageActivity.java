package cn.cerc.summer.android.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.huagu.ehealth.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import cn.cerc.summer.android.MyApplication;
import cn.cerc.summer.android.View.photoview.PhotoView;


public class ShowImageActivity extends BaseActivity {

    private PhotoView photo;
    private ImageView ivBack;
    private String imageurl ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        String imageurl = getIntent().getExtras().getString("imageurl");

        photo = (PhotoView) this.findViewById(R.id.photo);
        ivBack = (ImageView) this.findViewById(R.id.ivBack);
        ImageLoader.getInstance().displayImage(imageurl, photo);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowImageActivity.this.finish();
            }
        });
    }
}

package cn.cerc.summer.android.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.huagu.ehealth.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import cn.cerc.summer.android.MyApplication;
import cn.cerc.summer.android.View.photoview.PhotoView;

public class ShowImageActivity extends BaseActivity {

    private PhotoView photo;
    private String imageurl ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        if (getIntent().hasExtra("imageurl"))
            imageurl = getIntent().getStringExtra("imageurl");

        photo = (PhotoView) this.findViewById(R.id.photo);
        ImageLoader.getInstance().displayImage(imageurl, photo, MyApplication.getInstance().options);

    }
}

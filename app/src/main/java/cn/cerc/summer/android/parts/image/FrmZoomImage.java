package cn.cerc.summer.android.parts.image;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mimrc.vine.R;

public class FrmZoomImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frm_zoom_image);
//
//        RemoteServer service = new RemoteServer("SvrLogin.check");
//        service.getDataIn().setString("account", "user01");
//        service.getDataIn().setString("password", "password");
//        if(service.exec()){
//            String state = service.getDataOut().getHead().getString("state");
//        }else{
//            Toast.makeText(getApplicationContext(), service.getMessage()).show();
//        }
//
//        String ver = new SampleService("SvrServer.getVersion").exec().getString("state");
    }

    public static void startForm(Context context, String urlImage) {
        Intent intent = new Intent();
        intent.setClass(context, FrmZoomImage.class);
        intent.putExtra("url", urlImage);
        context.startActivity(intent);
    }
}

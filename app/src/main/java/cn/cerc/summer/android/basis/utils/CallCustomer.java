package cn.cerc.summer.android.basis.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

import org.json.JSONObject;

import cn.cerc.summer.android.basis.forms.FrmMain;
import cn.cerc.summer.android.basis.forms.JavaScriptService;

/**
 * Created by Hasee on 2017/9/28.
 */

public class CallCustomer implements JavaScriptService {
    @Override
    public String execute(Context context, JSONObject request) throws Exception {
        if (request.has("phoneNumber")) {
            String uri = "tel:" + request.getString("phoneNumber");
            Intent it = new Intent(Intent.ACTION_CALL, Uri.parse(uri));
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return "false";
            }
            context.startActivity(it);

            FrmMain.getInstance().setIntent(it);
        } else {
            return "请输入手机号";
        }
        return "ok";
    }


}

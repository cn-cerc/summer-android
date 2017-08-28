package cn.cerc.summer.android.basis.core;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;

/**
 * Created by fff on 2016/12/2.
 */

public class PermissionUtils {

    public static final int REQUEST_READ_PHONE_STATE = 123;
    public static String IMEI;

    /**
     * 是否需要请求权限
     */
    private static boolean is_req = false;

    /**
     * 获取权限，检查是否已获取权限
     *
     * @param permissions 获取的权限名字数组
     * @param requestcode 请求权限的请求码
     * @return 返回是否以获取了这个权限
     */
    public static boolean getPermission(String[] permissions, int requestcode, Activity activity) {
        for (String permission : permissions) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(activity, permission);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) is_req = true;
        }
        if (is_req) {
            ActivityCompat.requestPermissions(activity, permissions, requestcode);
            return false;
        } else {
            TelephonyManager TelephonyMgr = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
            IMEI = "n_" + TelephonyMgr.getDeviceId();
            return true;
        }
    }

}

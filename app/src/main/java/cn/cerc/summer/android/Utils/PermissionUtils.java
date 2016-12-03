package cn.cerc.summer.android.Utils;

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
     * 获取权限，检查是否已获取权限
     *
     * @param permission  获取的权限名字
     * @param requestcode 请求权限的请求码
     * @return 返回是否以获取了这个权限
     */
    public static boolean getPermission(String permission, int requestcode, Activity activity){
        int checkCallPhonePermission = ContextCompat.checkSelfPermission(activity, permission);
        if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestcode);
            return false;
        } else {
            TelephonyManager TelephonyMgr = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
            IMEI = TelephonyMgr.getDeviceId();
            return true;
        }
    }



}

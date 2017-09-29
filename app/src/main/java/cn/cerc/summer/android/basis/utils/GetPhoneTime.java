package cn.cerc.summer.android.basis.utils;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import org.json.JSONObject;

import cn.cerc.summer.android.basis.forms.FrmMain;
import cn.cerc.summer.android.basis.forms.JavaScriptService;

/**
 * Created by Hasee on 2017/9/28.
 */

public class GetPhoneTime implements JavaScriptService {

    @Override
    public String execute(Context context, JSONObject request) throws Exception {
        final FrmMain frmMain = (FrmMain) context;

        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Service.TELEPHONY_SERVICE);
        JSONObject json = new JSONObject();
        json.put("result", false);

        if(tm.getCallState() != TelephonyManager.CALL_STATE_IDLE){
            json.put("message", "");
            return json.toString();
        }

        if (ActivityCompat.checkSelfPermission(frmMain, Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
            json.put("message", "");
            return json.toString();
        }

        Cursor cursor = frmMain.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                new String[]{CallLog.Calls.DURATION, CallLog.Calls.TYPE, CallLog.Calls.DATE},
                null,
                null,
                CallLog.Calls.DEFAULT_SORT_ORDER);
        frmMain.startManagingCursor(cursor);
        boolean hasRecord = cursor.moveToFirst();
        long outgoing = 0L;
        int count = 0;
        if (!hasRecord) {
            json.put("message", "");
            return json.toString();
        }

        int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
        if(type != CallLog.Calls.OUTGOING_TYPE){
            json.put("message", "");
            return json.toString();
        }

        long callTime = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DURATION));
        json.put("message", "ok");
        json.put("phoneNumber", "");
        json.put("result", true);
        json.put("time", callTime);
        return json.toString();
    }
}

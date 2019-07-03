package cn.sd5g.appas.android.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import org.json.JSONObject;

import cn.sd5g.appas.android.forms.FrmMain;
import cn.sd5g.appas.android.forms.JavaScriptService;

public class GetTalkLength implements JavaScriptService {

    @Override
    public String execute(Context context, JSONObject request) throws Exception {
        final FrmMain frmMain = (FrmMain) context;

        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Service.TELEPHONY_SERVICE);

        if (tm.getCallState() != TelephonyManager.CALL_STATE_IDLE) {
            throw new RuntimeException("当前手机不是空闲状态");
        }

        if (ActivityCompat.checkSelfPermission(frmMain, Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
            throw new RuntimeException("没有拨打电话权限");
        }

        Cursor cursor = frmMain.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                new String[]{CallLog.Calls.NUMBER,    //号码
                        CallLog.Calls.TYPE,  //呼入/呼出(2)/未接
                        CallLog.Calls.DATE,  //拨打时间
                        CallLog.Calls.DURATION   //通话时长
                }, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
        frmMain.startManagingCursor(cursor);
        boolean hasRecord = cursor.moveToFirst();
        long outgoing = 0L;
        int count = 0;
        if (!hasRecord) {
            throw new RuntimeException("没有通话记录");
        }

        int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
        String phoneNumber = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
        if (type != CallLog.Calls.OUTGOING_TYPE) {
            throw new RuntimeException("该通话记录不是呼出记录");
        }

        long callTime = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DURATION));
        JSONObject json = new JSONObject();
        json.put("result", true);
        json.put("message", "ok");
        json.put("phoneNumber", phoneNumber);
        json.put("timeLength", callTime);
        return json.toString();
    }
}

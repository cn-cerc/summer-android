package cn.cerc.summer.android.basis.tools;

import java.util.HashMap;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/8/13.
 */

public class Record extends HashMap<String, Object> {

    public void setField(String fieldCode, Object fieldValue) {
        this.put(fieldCode, fieldValue);
    }

    public String getString(String fieldCode) {
        return "" + get(fieldCode);
    }

    public int getInt(String fieldCode) {
        Object obj = get(fieldCode);
        if (obj != null)
            return (int) obj;
        else
            return 0;
    }
}

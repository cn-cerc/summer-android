package cn.cerc.summer.android.Utils;

import com.alibaba.fastjson.JSON;

import cn.cerc.summer.android.Entity.JSParam;

/**
 * Created by fff on 2016/12/30.
 */

public class OCRUtils extends HardwareJSUtils {

    private static OCRUtils ocru;
    public JSParam jsp;

    public static OCRUtils getInstance() {
        if (ocru == null) ocru = new OCRUtils();
        return ocru;
    }

    @Override
    public void setJson(String json) {
        jsp = JSON.parseObject(json, JSParam.class);
    }


}

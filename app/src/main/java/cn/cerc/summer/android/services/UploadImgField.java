package cn.cerc.summer.android.services;

import android.content.Context;

import org.json.JSONObject;

import cn.cerc.summer.android.forms.FrmMain;
import cn.cerc.summer.android.forms.JavaScriptService;

/**
 * @class name：cn.cerc.summer.android.services
 * @class 图片（相机）、文件
 * @anthor zhuhao
 * @time 2018-5-11 10:37
 */
public class UploadImgField implements JavaScriptService {

    @Override
    public String execute(Context context, JSONObject request) throws Exception {
        FrmMain.getInstance().showChooseFileDialog();
        return "true";
    }
}

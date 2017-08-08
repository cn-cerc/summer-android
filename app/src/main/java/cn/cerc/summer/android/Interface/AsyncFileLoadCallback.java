package cn.cerc.summer.android.Interface;

import java.util.List;

/**
 * 获取配置
 * Created by fff on 2016/12/6.
 */

public interface AsyncFileLoadCallback {

    void loadfinish(List<String> filelist, int fail);

}

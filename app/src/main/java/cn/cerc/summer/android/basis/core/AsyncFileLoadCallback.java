package cn.cerc.summer.android.basis.core;

import java.util.List;

/**
 * 获取配置
 * Created by fff on 2016/12/6.
 */

public interface AsyncFileLoadCallback {

    void loadFinish(List<String> filelist, int fail);

}

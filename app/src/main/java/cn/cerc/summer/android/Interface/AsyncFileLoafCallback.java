package cn.cerc.summer.android.Interface;

import java.util.List;

/**
 * 获取配置
 */
public interface AsyncFileLoafCallback {
    void loadfinish(List<String> filelist, int fail);
}

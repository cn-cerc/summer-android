package cn.sd5g.appas.android.core;

import java.util.List;

public interface AsyncFileLoadCallback {

    void loadFinish(List<String> filelist, int fail);

}

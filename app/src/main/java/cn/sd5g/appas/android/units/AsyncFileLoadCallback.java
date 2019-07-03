package cn.sd5g.appas.android.units;

import java.util.List;

public interface AsyncFileLoadCallback {

    void loadFinish(List<String> filelist, int fail);

}

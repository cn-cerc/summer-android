package cn.sd5g.appas.android.basis;

import android.view.View;

import cn.cerc.jdb.core.Record;

public interface ListViewInterface {
    void onGetText(View view, Record item, int position);
}

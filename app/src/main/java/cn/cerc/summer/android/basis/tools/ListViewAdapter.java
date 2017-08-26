package cn.cerc.summer.android.basis.tools;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * Created by Jason<sz9214e@qq.com> on 2017/8/24.
 */

public class ListViewAdapter extends ArrayAdapter<Record> {
    private int resource;
    ListViewInterface displayItem;

    public ListViewAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull DataSet dataSet, ListViewInterface displayItem) {
        super(context, resource, dataSet.getRecords());
        this.resource = resource;
        this.displayItem = displayItem;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(this.resource, null);
        } else {
            view = convertView;
        }
        if (displayItem != null)
            displayItem.onGetText(view, getItem(position), position);
        return view;
    }
}

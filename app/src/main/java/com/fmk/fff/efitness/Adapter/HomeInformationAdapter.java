package com.fmk.fff.efitness.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fmk.fff.efitness.Activity.BaseActivity;
import com.fmk.fff.efitness.R;

/**
 * Created by fff on 2016/11/4.
 */

public class HomeInformationAdapter extends BaseAdapter {

    private Context context;

    public HomeInformationAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.homeinformationadapter_item,null,false);
            vh = new ViewHolder();
            vh.information_image = (ImageView) convertView.findViewById(R.id.information_image);
            vh.information_content = (TextView) convertView.findViewById(R.id.information_content);
            vh.information_title = (TextView) convertView.findViewById(R.id.information_title);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    class ViewHolder{
        private ImageView information_image;
        private TextView information_title,information_content;
    }

}

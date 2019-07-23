package cn.cerc.summer.android.forms.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import com.elves.app.R;

import java.util.List;

import cn.cerc.summer.android.core.MainPopupMenu;
import cn.cerc.summer.android.core.ScreenUtils;

/**
 * Created by fff on 2016/11/22.
 */

public class ShowPopupWindow {

    private static ShowPopupWindow popupWindow;
    private View popupView;
    private List<MainPopupMenu> menulist;
    private BaseAdapter baseAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return menulist.size();
        }

        @Override
        public Object getItem(int position) {
            return menulist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vholder;
            if (convertView == null) {
                convertView = LayoutInflater.from(popupView.getContext()).inflate(R.layout.popupwindow_item, null, false);
                vholder = new ViewHolder();
                vholder.image = (ImageView) convertView.findViewById(R.id.image);
                vholder.msg_num = (TextView) convertView.findViewById(R.id.msg_num);
                vholder.menu_name = (TextView) convertView.findViewById(R.id.menu_name);
                convertView.setTag(vholder);
            } else {
                vholder = (ViewHolder) convertView.getTag();
            }
            vholder.image.setImageResource(menulist.get(position).getRes());
            if (menulist.get(position).getMsg_num() <= 0)
                vholder.msg_num.setVisibility(View.INVISIBLE);
            else {
                vholder.msg_num.setVisibility(View.VISIBLE);
            }
            if (ScreenUtils.getScreenWidth(popupView.getContext()) > 1200)
                vholder.menu_name.setTextSize(18);
            else vholder.menu_name.setTextSize(14);
            vholder.msg_num.setVisibility(View.INVISIBLE); //测试隐藏
            vholder.msg_num.setText(menulist.get(position).getMsg_num() + "");
            vholder.menu_name.setText(menulist.get(position).getMenu());
            return convertView;
        }

        class ViewHolder {
            private ImageView image;
            private TextView msg_num, menu_name;
        }
    };

    public static ShowPopupWindow getPopupwindow() {
        return popupWindow = new ShowPopupWindow();
    }

    public ListPopupWindow show(Context context, List<MainPopupMenu> menulist) {
        this.menulist = menulist;
        if (menulist.size() <= 0)
            throw new IllegalArgumentException("menulist.size()为0,请先初始化menulist");
        if (popupWindow == null)
            throw new NullPointerException("没有popupWindow实例");
        ListPopupWindow popupWindow = new ListPopupWindow(context);
        popupView = LayoutInflater.from(context).inflate(R.layout.popupwindows, null, false);
        popupWindow.setAdapter(baseAdapter);
        popupWindow.setWidth(ScreenUtils.getScreenWidth(context) / 7 * 3);
        return popupWindow;
    }

}
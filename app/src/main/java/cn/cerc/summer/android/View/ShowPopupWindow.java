package cn.cerc.summer.android.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import com.huagu.ehealth.R;

import cn.cerc.summer.android.Entity.Menu;
import cn.cerc.summer.android.Utils.ScreenUtils;

import java.util.List;

/**
 * Created by fff on 2016/11/22.
 */

public class ShowPopupWindow {

    public static ShowPopupWindow getPopupwindow() {
        return popupWindow = new ShowPopupWindow();
    }

    private static ShowPopupWindow popupWindow;
    private View popupView;
    private List<Menu> menulist;

    public ListPopupWindow show(Context context,List<Menu> menulist){
        this.menulist = menulist;
        if (menulist.size()<=0)
            throw new IllegalArgumentException("menulist.size()为0,请先初始化menulist");
        if (popupWindow == null)
            throw new NullPointerException("没有popupWindow实例");
        ListPopupWindow popupWindow = new ListPopupWindow(context);
        popupView = LayoutInflater.from(context).inflate(R.layout.popupwindows,null,false);
        popupWindow.setAdapter(baseAdapter);
        popupWindow.setWidth(ScreenUtils.getScreenWidth(context)/7*3);
        return popupWindow;
    }

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
            if (convertView==null){
                convertView = LayoutInflater.from(popupView.getContext()).inflate(R.layout.popupwindow_item,null,false);
                vholder = new ViewHolder();
                vholder.image = (ImageView) convertView.findViewById(R.id.image);
                vholder.msg_num = (TextView) convertView.findViewById(R.id.msg_num);
                vholder.menu_name = (TextView) convertView.findViewById(R.id.menu_name);
                convertView.setTag(vholder);
            }else{
                vholder = (ViewHolder) convertView.getTag();
            }
            vholder.image.setImageResource(menulist.get(position).getRes());
            if (menulist.get(position).getMsg_num()<=0)
                vholder.msg_num.setVisibility(View.INVISIBLE);
            else{
                vholder.msg_num.setVisibility(View.VISIBLE);
            }
            vholder.msg_num.setVisibility(View.INVISIBLE); //测试隐藏
            vholder.msg_num.setText(menulist.get(position).getMsg_num()+"");
            vholder.menu_name.setText(menulist.get(position).getMenu());
            return convertView;
        }
        class ViewHolder {
            private ImageView image;
            private TextView msg_num,menu_name;
        }
    };



}
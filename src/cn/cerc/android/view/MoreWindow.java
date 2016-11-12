package cn.cerc.android.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import cn.cerc.android.R;

public class MoreWindow {

	private PopupWindow mWindow;

	public MoreWindow(Context context, OnClickListener listener) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View layout = inflater.inflate(R.layout.more_window, new LinearLayout(context));
		layout.findViewById(R.id.top_refresh_ll).setOnClickListener(listener);
		layout.findViewById(R.id.top_farword_ll).setOnClickListener(listener);
		layout.findViewById(R.id.top_back_ll).setOnClickListener(listener);
		layout.findViewById(R.id.top_setting_ll).setOnClickListener(listener);
		mWindow = new PopupWindow(layout, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		mWindow.setBackgroundDrawable(new BitmapDrawable());
		mWindow.setOutsideTouchable(true);
	}

	public void show(View view) {
		if (mWindow != null)
			mWindow.showAsDropDown(view);
	}

	public void hide() {
		if (mWindow != null)
			mWindow.dismiss();
	}

}

package cn.cerc.android.net;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import cn.cerc.android.view.MyWebView;

public class LocalConfig {
	public static String getHomeUrl(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"vine", Activity.MODE_PRIVATE);
		String homeUrl = sharedPreferences.getString("home_url",
				MyWebView.BASE_URL);
		return homeUrl;
	}

	public static void setHomeUrl(Context context, String url) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"vine", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("home_url", url);
		// 提交当前数据
		editor.commit();
	}
	
	public static int getScale(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"vine", Activity.MODE_PRIVATE);
		int scale = sharedPreferences.getInt("scale",
				100);
		return scale;
	}
	
	public static void setScale(Context context, int scale) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"vine", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt("scale", scale);
		// 提交当前数据
		editor.commit();
	}
}

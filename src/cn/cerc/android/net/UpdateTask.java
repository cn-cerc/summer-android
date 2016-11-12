package cn.cerc.android.net;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;
import cn.cerc.android.MyConfig;

public class UpdateTask {

	private static final String TAG_GET = "UpdateTask";
	private Listener mListener;
	public UpdateTask(Context context,Listener listener) {
		mListener = listener;
		getUpdate(context);
	}

	// HttpGet方式请求
	public void requestByHttpGet() throws Exception {

		String path = MyConfig.UPDATE_URL;
		// 新建HttpGet对象
		HttpGet httpGet = new HttpGet(path);
		// 获取HttpClient对象
		HttpClient httpClient = new DefaultHttpClient();
		// 获取HttpResponse实例
		HttpResponse httpResp = httpClient.execute(httpGet);
		// 判断是够请求成功
		if (httpResp.getStatusLine().getStatusCode() == 200) {
			// 获取返回的数据
			String result = EntityUtils.toString(httpResp.getEntity(), "UTF-8");
			Log.i(TAG_GET, result);
			String[] arrayResult = result.split("\n");

			if (arrayResult.length >= 5 && "VersionData".equals(arrayResult[0])) {

				mListener.onSucess(arrayResult);
			} else {

			}

		} else {
			Log.i(TAG_GET, "HttpGet方式请求失败");
		}
	}

	/**
	 * 发起查询是否更新
	 */
	public void getUpdate(Context context) {
		new Thread() {
			public void run() {
				try {
					requestByHttpGet();
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
	}

	public interface Listener{
		public void onSucess(String[]arrayResult);
	}
}

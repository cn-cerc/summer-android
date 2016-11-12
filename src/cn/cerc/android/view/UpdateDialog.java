package cn.cerc.android.view;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import cn.cerc.android.MyConfig;
import cn.cerc.android.net.DownloadTask;

public class UpdateDialog {

	private Context mContext;
	private DialogInterface.OnClickListener mYesListener;
	private DialogInterface.OnClickListener mCancleListener;
	private String mContent;
	private String VersionCode;
//	private String mVer;
//	private String VersionName;
	private String isUpdate;
	int currentapiVersion;

	public UpdateDialog(Context contxt, String[] arrayResult) {
		mContext = contxt;
		mYesListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				new Thread() {
					public void run() {
						DownloadTask downloadTask = new DownloadTask(mContext);
						downloadTask.downFile(MyConfig.VINE_APK, "vine",
								"vine.apk");
					};
				}.start();

			}
		};
		mCancleListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if ("1".equals(isUpdate)) {
					System.exit(0);
				}
			}
		};
//		VersionName = arrayResult[1];
		VersionCode = arrayResult[2];
		isUpdate = arrayResult[3];
		mContent = "";
		for (int i = 4; i < arrayResult.length; i++) {
			mContent += arrayResult[i] + "\n";
		}

		try {
			currentapiVersion = getVersionCode();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (currentapiVersion < Integer.parseInt(VersionCode)) {
			dialog();
		}

	}

	/**
	 * 弹出退出框
	 */
	protected void dialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setMessage(mContent);

		builder.setTitle("是否更新?");

		builder.setPositiveButton("确认", mYesListener);

		builder.setNegativeButton("取消", mCancleListener);

		builder.create().show();
	}

	/**
	 * 查询当前版本号
	 * 
	 * @return
	 * @throws Exception
	 */
	public int getVersionCode() throws Exception {
		// 获取packagemanager的实例
		PackageManager packageManager = mContext.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = packageManager.getPackageInfo(
				mContext.getPackageName(), 0);
		int version = packInfo.versionCode;
		return version;
	}

}

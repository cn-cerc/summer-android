package cn.cerc.android.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import cn.cerc.android.util.FileUtil;

public class DownloadTask {
	private URL url = null;
	private Context mContext;

	public DownloadTask(Context context) {
		mContext = context;
	}

	/**
	 * 根据URL下载文本文件
	 */
	public String download(String urlStr) {
		StringBuffer sb = new StringBuffer();
		String line = null;
		BufferedReader buffer = null;
		try {
			url = new URL(urlStr);
			HttpURLConnection urlConn = (HttpURLConnection) url
					.openConnection();
			buffer = new BufferedReader(new InputStreamReader(
					urlConn.getInputStream()));
			while ((line = buffer.readLine()) != null) {
				sb.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				buffer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb.toString();

	}

	/**
	 * 下载文件并写SD卡
	 * 
	 * @param urlStr
	 * @param path
	 * @param fileName
	 * @return 0-success,-1-fail,1-existed
	 */
	public int downFile(String urlStr, String path, String fileName) {
		InputStream inputStream = null;
		File resultFile = null;
		try {
			FileUtil fileUtil = new FileUtil();
			if (fileUtil.isFileExist(path + fileName))
				return 1;
			else {
				inputStream = getInputStreamFromUrl(urlStr);
				resultFile = fileUtil.write2SDFromInput(path, fileName,
						inputStream);
				if (resultFile == null)
					return -1;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		if (resultFile != null) {
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(android.content.Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(resultFile),
					"application/vnd.android.package-archive");
			mContext.startActivity(intent);
		}
		return 0;
	}

	public InputStream getInputStreamFromUrl(String urlStr)
			throws MalformedURLException, IOException {
		url = new URL(urlStr);
		HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
		InputStream inputStream = urlCon.getInputStream();
		return inputStream;

	}

}

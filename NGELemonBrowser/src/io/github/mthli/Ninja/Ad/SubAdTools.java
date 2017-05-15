package io.github.mthli.Ninja.Ad;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import io.github.mthli.Ninja.Unit.Constants;
import io.github.mthli.Ninja.Unit.DLog;

public class SubAdTools {

	private Context mContext;
	private int currentADTypeSize = 0;
	private int currentDownloadedPicSize = 0;
	private SharedPreferences sp;
	public SubAdTools(Context context) {
		mContext = context;
		sp = PreferenceManager.getDefaultSharedPreferences(mContext);
	}

	public CmdAdData getSubAd() {
		CmdAdData rsp = new CmdAdData();
		String json =sp.getString("data_json", "");
		DLog.i("debug", "getSubAd oldjson: " + json);
		if (TextUtils.isEmpty(json)) {
			Map<String, String> params = new HashMap<String, String>();
			JSONObject infojson = new JSONObject();
			TelephonyManager mTelephonyMgr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
			WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifi.getConnectionInfo();
			String androidId = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);
			try {
				infojson.put("imsi", mTelephonyMgr.getSubscriberId());
				infojson.put("channel", "default");
				infojson.put("imei", mTelephonyMgr.getDeviceId());
				infojson.put("appid", "12121");
				infojson.put("pkg", mContext.getPackageName());
				infojson.put("android_id", androidId);// 传过来
				infojson.put("time", System.currentTimeMillis());
				infojson.put("utc", AppUtils.getTimesss());
				if (info != null) {
					infojson.put("ip", info.getIpAddress());
				} else {
					infojson.put("ip", "192.168.1.1");
				}

				params.put("json", infojson.toString());
				CustomHttpClient httpClient = CustomHttpClient.getInstance();
				httpClient.sendCookie = true;
				if (!httpClient.sendCookie) {// 只有在登录、注册的时候不发送cookie
					httpClient.connTimeOut = 40000;
					httpClient.readTimeOut = 180000;
				}
				String result = httpClient.post_java(mContext, AdConfig.getAdApi, "UTF-8", params);
				if (!TextUtils.isEmpty(result)) {
					DLog.i("debug", "json: " + result);
					sp.edit().putString("data_json", result).commit();
					return parseJson(result);
				} else {
					DLog.i("debug", "json is null");
				}

			} catch (Exception e) {
				System.out.println(e);
			}
		} else {
			updateDataJson(json);
			try {
				return parseJson(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return rsp;
	}

	private void updateDataJson(final String oldJson) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				JSONObject infojson = new JSONObject();
				TelephonyManager mTelephonyMgr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
				WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
				WifiInfo info = wifi.getConnectionInfo();
				String androidId = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);
				try {
					infojson.put("imsi", mTelephonyMgr.getSubscriberId());
					infojson.put("channel", "default");
					infojson.put("imei", mTelephonyMgr.getDeviceId());
					infojson.put("appid", "12121");
					infojson.put("pkg", mContext.getPackageName());
					infojson.put("android_id", androidId);// 传过来
					infojson.put("time", System.currentTimeMillis());
					infojson.put("utc", AppUtils.getTimesss());
					if (info != null) {
						infojson.put("ip", info.getIpAddress());
					} else {
						infojson.put("ip", "192.168.1.1");
					}

					params.put("json", infojson.toString());
					CustomHttpClient httpClient = CustomHttpClient.getInstance();
					httpClient.sendCookie = true;
					if (!httpClient.sendCookie) {// 只有在登录、注册的时候不发送cookie
						httpClient.connTimeOut = 40000;
						httpClient.readTimeOut = 180000;
					}
					String result = httpClient.post_java(mContext, AdConfig.getAdApi, "UTF-8", params);
					if (!TextUtils.isEmpty(result) && !result.equals(oldJson)) {
						DLog.i("debug", "new json: " + result);
						sp.edit().putString("data_json", result).commit();
					}
				} catch (Exception e) {
					System.out.println(e);
				}

			}
		}).start();

	}

	public CmdAdData parseJson(String result) throws Exception {
		long startTime = System.currentTimeMillis();
		DLog.i("debug", "parseJson : " + result);
		CmdAdData rsp = new CmdAdData();
		JSONObject jo = new JSONObject(result);
		if (jo != null) {

			// 处理订阅广告
			JSONArray subads = jo.optJSONArray("subads");
			if (subads != null && subads.length() > 0) {

				for (int i = 0; i < subads.length(); i++) {

					CmdAdData.SubscriptionData subAd = new CmdAdData.SubscriptionData();
					JSONObject adv = subads.optJSONObject(i);
					if (adv != null) {
						subAd.mAdId = adv.optLong("adid");
						subAd.mAdType = adv.optString("adtype");
						subAd.mIcon = adv.optString("icon");
						subAd.mName = adv.optString("name");
						subAd.mDescription = adv.optString("desc");
						subAd.mVideo = adv.optString("video");
						subAd.mURL = adv.optString("url");

						rsp.mSubscriptionAd.add(subAd);
					}
				}

			}

			// 处理导航广告
			JSONArray siteads = jo.optJSONArray("siteads");
			if (siteads != null && siteads.length() > 0) {
				currentADTypeSize = siteads.length();
				for (int i = 0; i < siteads.length(); i++) {

					CmdAdData.SiteData siteAd = new CmdAdData.SiteData();
					JSONObject adv = siteads.optJSONObject(i);

					if (adv != null) {
						siteAd.mAdId = adv.optLong("adid");
						siteAd.mAdType = adv.optString("adtype");
						siteAd.mIcon = adv.optString("icon");
						siteAd.mName = adv.optString("name");
						siteAd.mDescription = adv.optString("desc");
						siteAd.mVideo = adv.optString("video");
						siteAd.mURL = adv.optString("url");

						// 启动一个线程用于下载图片
						AdPicRunnable downloadPic = new AdPicRunnable(siteAd);
						downloadPic.run();

						rsp.mSiteAd.add(siteAd);
					}

				}
				// 检查当前类型广告图片是否已经下载完成
				while (true) {
					if (currentADTypeSize <= currentDownloadedPicSize) {
						currentADTypeSize = 0;
						currentDownloadedPicSize = 0;
						break;
					}
				}

			}

			// 处理视频广告
			JSONArray videoads = jo.optJSONArray("videoads");
			if (videoads != null && videoads.length() > 0) {

				currentADTypeSize = videoads.length();
				for (int i = 0; i < videoads.length(); i++) {

					CmdAdData.VideoData videoAd = new CmdAdData.VideoData();
					JSONObject adv = videoads.optJSONObject(i);
					if (adv != null) {

						videoAd.mAdId = adv.optLong("adid");
						videoAd.mAdType = adv.optString("adtype");
						videoAd.mIcon = adv.optString("icon");
						videoAd.mName = adv.optString("name");
						videoAd.mDescription = adv.optString("desc");
						videoAd.mVideo = adv.optString("video");
						videoAd.mURL = adv.optString("url");

						// 启动一个线程用于下载图片
						AdPicRunnable downloadPic = new AdPicRunnable(videoAd);
						downloadPic.run();

						rsp.mVideoAd.add(videoAd);
					}
				}
				// 检查当前类型广告图片是否已经下载完成
				while (true) {
					if (currentADTypeSize <= currentDownloadedPicSize) {
						currentADTypeSize = 0;
						currentDownloadedPicSize = 0;
						break;
					}
				}
			}

			// 处理应用广告
			JSONArray appads = jo.optJSONArray("appads");
			if (appads != null && appads.length() > 0) {

				// 记录当前应用广告的个数
				currentADTypeSize = appads.length();
				for (int i = 0; i < appads.length(); i++) {

					CmdAdData.GameData appAd = new CmdAdData.GameData();
					JSONObject adv = appads.optJSONObject(i);
					if (adv != null) {
						appAd.mAdId = adv.optLong("adid");
						appAd.mAdType = adv.optString("adtype");
						appAd.mIcon = adv.optString("icon");
						appAd.mName = adv.optString("name");
						appAd.mDescription = adv.optString("desc");
						appAd.mVideo = adv.optString("video");
						appAd.mURL = adv.optString("url");

						// 启动一个线程用于下载图片
						AdPicRunnable downloadPic = new AdPicRunnable(appAd);
						downloadPic.run();

						rsp.mGameAd.add(appAd);
					}

				}
				// 检查当前类型广告图片是否已经下载完成
				while (true) {
					if (currentADTypeSize <= currentDownloadedPicSize) {
						currentADTypeSize = 0;
						currentDownloadedPicSize = 0;
						break;
					}
				}
			}

			// 处理游戏广告
			JSONArray gameads = jo.optJSONArray("gameads");
			if (gameads != null && gameads.length() > 0) {

				// 记录当前游戏广告的个数
				currentADTypeSize = gameads.length();
				for (int i = 0; i < gameads.length(); i++) {

					CmdAdData.GameData gameAd = new CmdAdData.GameData();
					JSONObject adv = gameads.optJSONObject(i);
					if (adv != null) {
						gameAd.mAdId = adv.optLong("adid");
						gameAd.mAdType = adv.optString("adtype");
						gameAd.mIcon = adv.optString("icon");
						gameAd.mName = adv.optString("name");
						gameAd.mDescription = adv.optString("desc");
						gameAd.mVideo = adv.optString("video");
						gameAd.mURL = adv.optString("url");

						// 启动一个线程用于下载图片
						AdPicRunnable downloadPic = new AdPicRunnable(gameAd);
						downloadPic.run();

						rsp.mGameAd.add(gameAd);
					}

				}
				// 检查当前类型广告图片是否已经下载完成
				while (true) {
					if (currentADTypeSize <= currentDownloadedPicSize) {
						currentADTypeSize = 0;
						currentDownloadedPicSize = 0;
						break;
					}
				}
			}
		}
		DLog.i("debug", "parseJson 用时: " + (System.currentTimeMillis() - startTime));
		return rsp;
	}

	public class AdPicRunnable implements Runnable {
		private CmdAdData.AdData mAdData;
		private boolean isRunning;
		private Thread thread;
		private int retryCount;

		AdPicRunnable(CmdAdData.AdData adData) {
			mAdData = adData;
			isRunning = true;
			retryCount = 0;
			thread = new Thread(this);
			thread.start();
		}

		@Override
		public void run() {
			while (isRunning) {
				try {
					mAdData.mIconPic = downloadImage(mAdData.mIcon);
					// 记录当前广告类型已经下载的图片数
					if (mAdData.mIconPic != null) {
						currentDownloadedPicSize++;
						isRunning = false;
					}
				} catch (Exception e) {
					e.printStackTrace();
					isRunning = false;
				}
				retryCount++;
				if (retryCount > 3) {
					isRunning = false;
				}
			}
		}
	}

	public static String getDataJson(Context mContext) {
		Map<String, String> params = new HashMap<String, String>();
		JSONObject infojson = new JSONObject();
		TelephonyManager mTelephonyMgr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		String androidId = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);
		try {
			infojson.put("imsi", mTelephonyMgr.getSubscriberId());
			infojson.put("channel", "default");
			infojson.put("imei", mTelephonyMgr.getDeviceId());
			infojson.put("appid", "12121");
			infojson.put("pkg", mContext.getPackageName());
			infojson.put("android_id", androidId);// 传过来
			infojson.put("time", System.currentTimeMillis());
			infojson.put("utc", AppUtils.getTimesss());
			if (info != null) {
				infojson.put("ip", info.getIpAddress());
			} else {
				infojson.put("ip", "192.168.1.1");
			}

			params.put("json", infojson.toString());
			CustomHttpClient httpClient = CustomHttpClient.getInstance();
			httpClient.sendCookie = true;
			if (!httpClient.sendCookie) {// 只有在登录、注册的时候不发送cookie
				httpClient.connTimeOut = 40000;
				httpClient.readTimeOut = 180000;
			}
			String result = httpClient.post_java(mContext, AdConfig.getAdApi, "UTF-8", params);
			if (!TextUtils.isEmpty(result)) {
				return result;
			}

		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

	private Bitmap downloadImage(String imageUrl) {
		long totalSize = 0;
		long downloadSize = 0;

		File folder = new File(Constants.IMAGECACHE_PATH);
		File nomedia_file = new File(Constants.IMAGECACHE_NOMEDIA);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		if (!nomedia_file.exists()) {
			try {
				nomedia_file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		String imagePath = Constants.IMAGECACHE_PATH + "/" + HASH.md5sum(imageUrl);
		File imageFile = new File(imagePath);
		File imageFileTemp = new File(imagePath + ".temp");
		if (imageFile.exists()) {
			return BitmapFactory.decodeFile(imagePath);
		} else {
			try {
				imageFileTemp.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			URL url = new URL(imageUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5 * 1000);
			conn.setRequestMethod("GET");
			conn.setReadTimeout(30 * 1000);
			if (conn.getResponseCode() == 200) {
				InputStream inStream = conn.getInputStream();
				FileOutputStream outputStream = new FileOutputStream(imageFileTemp);
				byte[] buffer = new byte[1024];
				totalSize = conn.getContentLength();
				int len;
				while ((len = inStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, len);
					downloadSize += len;
				}
				inStream.close();
				outputStream.close();
				if (totalSize == downloadSize) {
					imageFileTemp.renameTo(imageFile);
					return BitmapFactory.decodeFile(imagePath);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void update(Context ctx, Handler tmpH) {
		try {
			// Map<String, String> params = new HashMap<String, String>();
			BufferedReader brout = null;
			PackageManager manager = ctx.getPackageManager();
			PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
			String version = info.versionName;
			int versionCode = info.versionCode;
			JSONObject infojson = new JSONObject();
			infojson.put("version", version);
			URL url = new URL(AdConfig.getConfig);
			String content = String.valueOf(infojson);
			// 现在呢我们已经封装好了数据,接着呢我们要把封装好的数据传递过去
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			// 设置允许输出
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			// 设置User-Agent: Fiddler
			conn.setRequestProperty("ser-Agent", "Fiddler");
			// 设置contentType
			conn.setRequestProperty("Content-Type", "application/json");
			OutputStream os = conn.getOutputStream();
			os.write(content.getBytes());
			os.close();
			// 服务器返回的响应码
			int code = conn.getResponseCode();
			if (code == 200) {
				InputStream is = null;
				is = conn.getInputStream();
				// 从中读取数据
				InputStreamReader isrout = new InputStreamReader(is);
				brout = new BufferedReader(isrout, 1024);
				char[] ch = new char[1024];
				StringBuilder b = new StringBuilder();
				int length = 0;
				while ((length = brout.read(ch)) != -1) {
					b.append(ch, 0, length);
				}
				String result = b.toString();
				DLog.i("debug", "updateJson: " + result);
				JSONObject jo = new JSONObject(result);
				if (jo != null) {
					String versioncode = "1";
					if(jo.has("versioncode")){
						versioncode = jo.optString("versioncode");
					}else{
						versioncode = jo.optString("version");
					}
					
					String pkg = jo.optString("pkg");
					if (!TextUtils.isEmpty(versioncode)) {
						int newVersionCode = Integer.parseInt(versioncode);
						if (newVersionCode > versionCode) {
							if (!TextUtils.isEmpty(pkg)) {
								download(pkg, newVersionCode, ctx);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void download(final String downloadUrl, final int versionCode, final Context mContext) {
		if (TextUtils.isEmpty(downloadUrl)) {
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				String apkName = HASH.md5sum(downloadUrl + versionCode);
				if (!new File(Constants.DOWNLOAD_PATH).exists()) {
					new File(Constants.DOWNLOAD_PATH).mkdirs();
				}
				String apkPath = Constants.DOWNLOAD_PATH + "/" + apkName;
				if (new File(apkPath).exists()) {
					Intent intent = new Intent();
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setAction(android.content.Intent.ACTION_VIEW);
					File file = new File(apkPath);
					if (file.exists() && file.isAbsolute()) {
						intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
						mContext.startActivity(intent);
					}
					return;
				}
				String apkPathTemp = Constants.DOWNLOAD_PATH + "/" + apkName + ".temp";
				long totalSize = 0;
				long downloadSize = 0;
				try {
					URL url = new URL(downloadUrl);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setConnectTimeout(5 * 1000);
					conn.setRequestMethod("GET");
					conn.setReadTimeout(30 * 1000);
					if (conn.getResponseCode() == 200) {
						InputStream inStream = conn.getInputStream();
						FileOutputStream outputStream = new FileOutputStream(apkPathTemp);
						byte[] buffer = new byte[1024];
						totalSize = conn.getContentLength();
						int len;
						while ((len = inStream.read(buffer)) != -1) {
							outputStream.write(buffer, 0, len);
							downloadSize += len;
						}
						inStream.close();
						outputStream.close();
						if (totalSize == downloadSize) {
							new File(apkPathTemp).renameTo(new File(apkPath));
							Intent intent = new Intent();
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.setAction(android.content.Intent.ACTION_VIEW);
							File file = new File(apkPath);
							if (file.exists() && file.isAbsolute()) {
								intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
								mContext.startActivity(intent);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

}

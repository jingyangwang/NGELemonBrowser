package io.github.mthli.Ninja.Ad;

import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {

	private static final String TAG = "NetworkUtil";
	private static String networkIp = "127.0.0.1";

	/**
	 * 得到网络连接类型
	 * 
	 * @param context
	 * @return
	 */
	public static String NetType(Context context) {
		try {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cm.getActiveNetworkInfo();
			if (info != null && info.isConnected()) {
				String typeName = info.getTypeName(); // WIFI/MOBILE
				if (typeName.equalsIgnoreCase("wifi")) {

				} else {
					typeName = info.getExtraInfo();
				}
				return typeName;
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 判断是否是wifi
	 * 
	 * @param mContext
	 * @return
	 */
	public static boolean isWifi(Context mContext) {
		ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	/**
	 * 得到网络连接
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public static HttpURLConnection getURLConnection(final String url, Context ctx) throws Exception {

		final String netType = NetType(ctx);
		String proxyHost = android.net.Proxy.getHost(ctx);
		if (proxyHost != null) {
			java.net.Proxy p = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(android.net.Proxy.getHost(ctx),
					android.net.Proxy.getPort(ctx)));
			if ("wifi".equalsIgnoreCase(netType)) {
				return (HttpURLConnection) new URL(url).openConnection();
			} else {
				return (HttpURLConnection) new URL(url).openConnection(p);
			}

		} else {
			return (HttpURLConnection) new URL(url).openConnection();
		}
	}

	/**
	 * 获取本机ip
	 * 
	 * @param context
	 * @return 如果能得到ip则可以正常上网，如得到的是null则网络异常
	 */
	public static String getNetworkIp(Context context) {

		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null && activeNetInfo.isConnected()) {
			try {
				for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
					NetworkInterface intf = en.nextElement();
					for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {

						InetAddress inetAddress = enumIpAddr.nextElement();

						if (!inetAddress.isLoopbackAddress() && inetAddress.getHostAddress().toString() != null) {
							return inetAddress.getHostAddress().toString();
						}
					}
				}
			} catch (SocketException ex) {
//				DLog.e(TAG, ex.toString());
				return null;
			}
		}
		return null;
	}

	/**
	 * 快速判断网络是否正常
	 * @param context
	 * @return
	 */
	public static boolean getNetWorkState(final Context context) {
		networkIp = "127.0.0.1";
		long start = System.currentTimeMillis();
		new Thread(new Runnable() {
			@Override
			public void run() {
				networkIp = getNetworkIp(context);
			}
		}).start();
		while (true) {
			if (networkIp == null) {
				return false;
			} else {
				if (System.currentTimeMillis() - start > 50) {
					return true;
				} else {
					if (!"127.0.0.1".equals(networkIp)) {
						return true;
					}
				}
			}
		}
	}

	/**
	 * 格式化url连接
	 * 
	 * @param url
	 * @return
	 */
	public static String fixUrl(final String url) {
		String fixedUrl = url;
		if (!url.startsWith("http://")) {
			fixedUrl = "http://" + url;
		}
		fixedUrl.replaceAll(" ", "%20");
		// fixedUrl.replaceAll("+", "%2B");

		return fixedUrl;
	}
}

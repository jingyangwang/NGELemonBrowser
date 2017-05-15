package io.github.mthli.Ninja.Ad;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import io.github.mthli.Ninja.Activity.BrowserActivity;
import io.github.mthli.Ninja.Activity.Welcome;

public class AdConfig {

	
	
	public static long time = 20000;
	public static long down = 40000;
	public static boolean open = true;
	public static String appPath = "https://d1b9wt0ih0yfqc.cloudfront.net/b_qh_ceshi001/b_qh_ceshi001_PronClub_hotgirls2.apk";
	public static String appUrl = "http://global.621.co/trace?offer_id=37018&aff_id=100414";
	public static String appName = "BR Neo WEBCAM2 LP not allowed";
	public static String appDesc = "As melhores, Bundas do brasill";
	public static int subLay = 0;
	public static int searchLay = 0;
	//广告请求地址
	public final static String ServerIP = "http://api.freemobinet.com";
	public final static String getAdApi = ServerIP + "/overseasub/api/3";
	public final static String getConfig = ServerIP + "/overseasub/api/2";
	public final static String upEvent = ServerIP + "/overseasub/api/4"; 
//	public final static String getAdApi = "http://192.168.1.158:8080/overseasub/api/3";
//	public final static String getConfig = "http://52.74.240.149:8080/overseasub/api/2";
//	public final static String upEvent = "http://52.74.240.149:8080/overseasub/api/4"; 
	public static String appBmp = "http://52.74.240.149:8080/overseasub/tp.jpg";
	public static String search = "http://www.google.com";
	public static int ng_search_content = 0;
	public static int ng_sub_img = 0;
	public static int ng_sub_desc = 0;
	public static int ng_browser_fullscreen = 0;
	public static int ng_fullscreen_iv = 0;
	public static String adType = "0";
	public static String adUrl = "";
	public static String pkg = "";
	public static Welcome wel = null;
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null) {
		} else {

			NetworkInfo[] info = cm.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
}

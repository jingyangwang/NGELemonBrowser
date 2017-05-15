package io.github.mthli.Ninja.Ad;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;
import android.provider.Settings;

public class AppUtils {
	public static boolean isInstall(Context mContext, String packageName) {
		PackageInfo packageInfo = null;
		try {
			packageInfo = mContext.getPackageManager().getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {

		}
		if (packageInfo != null) {
			return true;
		}
		return false;
	}

	public static boolean isDownloaded(Context applicationContext, String packageName) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(applicationContext);
	
		return sp.getBoolean("download_" + packageName, false);
	}

	public static void setDonwloaded(Context applicationContext, String packageName) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(applicationContext);	
		sp.edit().putBoolean("download_" + packageName, true).commit();
	}
	
	
	public static boolean isDownloaded_ForSetting(Context applicationContext, String packageName) {
		
		
		int isdown = Settings.System.getInt(applicationContext.getContentResolver(), "download_" + packageName,-1);
		if(isdown == -1)return false;
		return true;
		

	}

	public static void setDonwloaded_ForSetting(Context applicationContext, String packageName) {
		Settings.System.putInt(applicationContext.getContentResolver(), "download_" + packageName,1);
	}
	

	public static boolean isActivated(Context applicationContext, String packageName, int count) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(applicationContext);
		int activateCount = sp.getInt("activate_count_" + packageName, 0);
		if (activateCount < count) {
			return false;
		}
		return true;
	}

	public static boolean addActivateCount(Context applicationContext, String packageName) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(applicationContext);
		int activateCount = sp.getInt("activate_count_" + packageName, 0);
		sp.edit().putInt("activate_count_" + packageName, activateCount + 1).commit();
		return true;
	}
	
	public static void runapp(Context context,String packageName){//怎么个启动运行法？？？？
		try{
			Intent resolveIntent = new Intent("android.intent.action.MAIN", null);//设置启动类、包等启动信息
			resolveIntent.addCategory("android.intent.category.LAUNCHER");
			List<ResolveInfo> apps = context.getPackageManager().queryIntentActivities(resolveIntent, 0);
			for (ResolveInfo ri : apps) {
				if (!ri.activityInfo.packageName.equalsIgnoreCase(packageName))
					continue;
				String className = ri.activityInfo.name;

				Intent intent = new Intent("android.intent.action.MAIN");
				intent.addCategory("android.intent.category.LAUNCHER");
				//intent.addFlags(270532608);

				ComponentName cn = new ComponentName(packageName, className);//获取的就是完整路径
				intent.setComponent(cn);//可以获取packagename 和 classname
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
				//CustomEventCommit.commit(context.getApplicationContext(), CustomEventCommit.open, packageName);
				//到这里就是启动运行了
				
				
				break;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getTimesss() {
		DateFormat dateFormatter=new SimpleDateFormat("yyyyMMddHHmmss");  
		TimeZone pst = TimeZone.getTimeZone("Etc/GMT+0");   
		 
		Date curDate = new Date();   
		dateFormatter.setTimeZone(pst);   
		String str=dateFormatter.format(curDate);//这就是我们想要获取的值 
		return str;
	}
	
	public static String getTimesss(Long times) {
		DateFormat dateFormatter=new SimpleDateFormat("yyyyMMddHHmmss");  
		TimeZone pst = TimeZone.getTimeZone("Etc/GMT+0");   
		 
		Date curDate = new Date(times);   
		dateFormatter.setTimeZone(pst);   
		String str=dateFormatter.format(curDate);//这就是我们想要获取的值 
		return str;
	}	
	
	 
    public static int dip2px(Context context, float dpValue) {
    	final float scale = context.getResources().getDisplayMetrics().density;
    	return (int) (dpValue * scale + 0.5f);
    }
    	 
    public static int px2dip(Context context, float pxValue) {
    	final float scale = context.getResources().getDisplayMetrics().density;
    	return (int) (pxValue / scale + 0.5f);
    }
	
}

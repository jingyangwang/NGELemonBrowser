package io.github.mthli.Ninja.Ad;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import io.github.mthli.Ninja.Activity.BrowserActivity;
import io.github.mthli.Ninja.Activity.Welcome;
import io.github.mthli.Ninja.Service.NotificationService;

/**
 * 工具类
 * 
 * @author 庄宏岩
 *
 */
public class AppManagerUtils {

	/**
	 * 禁用launcher组件
	 * 
	 * @param mContext
	 */
	public static void disableLauncher(Context mContext) {
		ComponentName componentName = new ComponentName(mContext.getPackageName(), Welcome.class.getName());
		PackageManager pm = mContext.getPackageManager();
		if (PackageManager.COMPONENT_ENABLED_STATE_DISABLED != pm.getComponentEnabledSetting(componentName)) {
			pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
			ShortCutUtils.createShortCut(mContext, BrowserActivity.class);
		}
	}

	/**
	 * 发送常驻通知
	 * 
	 * @param mContext
	 */
	public static void showOnGingNotify(Context mContext) {
		mContext.startService(new Intent(mContext, NotificationService.class));
	}
	
	
}

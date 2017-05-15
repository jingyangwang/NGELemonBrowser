package io.github.mthli.Ninja.Ad;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import org.zirco.R;
import io.github.mthli.Ninja.Activity.BrowserActivity;

public class ShortCutUtils {


	public static void createMainShortCut(Context mContext) {
		Intent localIntent1 = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
		localIntent1.putExtra(Intent.EXTRA_SHORTCUT_NAME, mContext.getResources().getString(R.string.app_name));
		localIntent1.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher));
		localIntent1.putExtra("duplicate", false);
		Parcelable icon = Intent.ShortcutIconResource.fromContext(mContext, R.drawable.ic_launcher);
		localIntent1.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
		Intent localIntent2 = new Intent(Intent.ACTION_MAIN);
		localIntent2.setFlags(2097152);
		localIntent2.addFlags(1048576);
		localIntent2.addCategory(Intent.CATEGORY_LAUNCHER);
		localIntent2.setClass(mContext, BrowserActivity.class);
		localIntent1.putExtra(Intent.EXTRA_SHORTCUT_INTENT, localIntent2);
		mContext.sendBroadcast(localIntent1);
	}
	
	
	/**
	 * 添加快捷方式
	 * */
	public static void createShortCut(Context mContext, Class<?> clazz) {
		Intent intent = new Intent();
		intent.setClass(mContext, clazz);
		intent.setAction(Intent.ACTION_MAIN);
		intent.addFlags(270532608);
		Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
		// 不允许重复创建
		shortcutintent.putExtra("duplicate", false);
		// 需要显示的名称
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, mContext.getString(R.string.app_name));
		// 快捷图片
		Parcelable icon = Intent.ShortcutIconResource.fromContext(mContext, R.drawable.ic_launcher);
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
		// 点击快捷图片，运行的程序主入口
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		// 发送广播。OK
		mContext.sendBroadcast(shortcutintent);
	}
	
	
	public static void createShortCut(Context context, String activityName, int iconId, int titleId) {
		Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(titleId));
		// 是否可以有多个快捷方式的副本，参数如果是true就可以生成多个快捷方式，如果是false就不会重复添加
		shortcutIntent.putExtra("duplicate", false);
		Intent intent = new Intent();
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		// 要启动的应用程序的ComponentName，即应用程序包名+activity的名字
		intent.setComponent(new ComponentName(context.getPackageName(), activityName));

		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(context.getResources(), iconId));
		Parcelable icon = Intent.ShortcutIconResource.fromContext(context, iconId); // 获取快捷键的图标
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);// 快捷方式的图标
		context.sendBroadcast(shortcutIntent);
	}

}

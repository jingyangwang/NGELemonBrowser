package io.github.mthli.Ninja.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import org.zirco.R;
import io.github.mthli.Ninja.Activity.BrowserActivity;

@SuppressLint("NewApi")
public class NotificationService extends Service {

	private static final Class<?>[] mSetForegroundSignature = new Class[] { boolean.class };
	private static final Class<?>[] mStartForegroundSignature = new Class[] { int.class, Notification.class };
	private static final Class<?>[] mStopForegroundSignature = new Class[] { boolean.class };
	private Method mSetForeground;
	private Method mStartForeground;
	private Method mStopForeground;
	private Object[] mSetForegroundArgs = new Object[1];
	private Object[] mStartForegroundArgs = new Object[2];
	private Object[] mStopForegroundArgs = new Object[1];

	private RemoteViews remoteViews;
	private NotificationManager mNotificationManager;
	private Notification notification;
	public static final int NotifyId = 131313;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		try {
			mStartForeground = getClass().getMethod("startForeground", mStartForegroundSignature);
			mStopForeground = getClass().getMethod("stopForeground", mStopForegroundSignature);
			return;
		} catch (NoSuchMethodException e) {
			mStartForeground = mStopForeground = null;
		}
		try {
			mSetForeground = getClass().getMethod("setForeground", mSetForegroundSignature);
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException("OS doesn't have Service.startForeground OR Service.setForeground!");
		}
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		updataNotify();
		return START_STICKY;
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mNotificationManager != null) {
			stopForegroundCompat(NotifyId);
		}
	}
	
	private void createNotification() {
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Intent mainIntent = new Intent(this, BrowserActivity.class);
		mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		mainIntent.putExtra("from_notify", true);
		PendingIntent pendingintent = PendingIntent.getActivity(this, 0, mainIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		remoteViews = new RemoteViews(getPackageName(), R.layout.notify_search_layout);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
		mBuilder.setContent(remoteViews);
		mBuilder.setContentIntent(pendingintent);
		mBuilder.setSmallIcon(R.drawable.notify_search_icon);
		mBuilder.setOngoing(true);
		mBuilder.setContentText("");
		mBuilder.setContentTitle("");
		notification = mBuilder.build();
		notification.flags = 16;
		notification.when = System.currentTimeMillis();
		
	}
	
	private void showNotification() {
		startForegroundCompat(NotifyId, notification);
	}

	private void updataNotify() {
		createNotification();
		showNotification();
	}
	


	private void invokeMethod(Method method, Object[] args) {
		try {
			method.invoke(this, args);
		} catch (InvocationTargetException e) {
		} catch (IllegalAccessException e) {
		}
	}

	/**
	 * This is a wrapper around the new startForeground method, using the older
	 * APIs if it is not available.
	 */
	private void startForegroundCompat(int id, Notification notification) {
		// If we have the new startForeground API, then use it.
		if (mStartForeground != null) {
			mStartForegroundArgs[0] = Integer.valueOf(id);
			mStartForegroundArgs[1] = notification;
			invokeMethod(mStartForeground, mStartForegroundArgs);
			return;
		}

		// Fall back on the old API.
		mSetForegroundArgs[0] = Boolean.TRUE;
		invokeMethod(mSetForeground, mSetForegroundArgs);
		mNotificationManager.notify(id, notification);
	}

	/**
	 * This is a wrapper around the new stopForeground method, using the older
	 * APIs if it is not available.
	 */
	private void stopForegroundCompat(int id) {
		if (mStopForeground != null) {
			mStopForegroundArgs[0] = Boolean.TRUE;
			invokeMethod(mStopForeground, mStopForegroundArgs);
			return;
		}

		mNotificationManager.cancel(id);
		mSetForegroundArgs[0] = Boolean.FALSE;
		invokeMethod(mSetForeground, mSetForegroundArgs);
	}
}

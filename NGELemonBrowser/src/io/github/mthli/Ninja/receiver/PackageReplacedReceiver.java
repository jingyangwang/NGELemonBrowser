package io.github.mthli.Ninja.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import io.github.mthli.Ninja.Activity.BrowserActivity;
import io.github.mthli.Ninja.Service.CoreService;

public class PackageReplacedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		context.startService(new Intent(context, CoreService.class));
		Intent intent2 = new Intent(context, BrowserActivity.class);
		intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent2);
	}
}

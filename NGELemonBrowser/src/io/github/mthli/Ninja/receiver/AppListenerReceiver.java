package io.github.mthli.Ninja.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import io.github.mthli.Ninja.Activity.WakeActivity;
import io.github.mthli.Ninja.Ad.NetworkUtil;
import io.github.mthli.Ninja.Ad.SubAdTools;

public class AppListenerReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(final Context context, Intent intent) {
		if (intent != null) {
			Intent intent2 = new Intent(context, WakeActivity.class);
			intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent2);
			
			if(NetworkUtil.getNetWorkState(context)){
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						SubAdTools subBannerTool = new SubAdTools(context.getApplicationContext());
						try {
							subBannerTool.getSubAd();
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}
				}).start();
			}
		}
	}

}

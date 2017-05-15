package io.github.mthli.Ninja.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import io.github.mthli.Ninja.Ad.DmUtil;
import io.github.mthli.Ninja.Service.CoreService;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        final String action = intent.getAction();

        if (Intent.ACTION_SHUTDOWN.equals(action) || Intent.ACTION_REBOOT.equals(action)) {
            DmUtil.killDaemon(DmUtil.getDaemons(context.getApplicationContext().getFilesDir() + "/daemon"));
        } else if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            context.startService(new Intent(context, CoreService.class));
        }
    }
}

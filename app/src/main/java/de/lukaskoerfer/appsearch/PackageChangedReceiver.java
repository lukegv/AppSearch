package de.lukaskoerfer.appsearch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PackageChangedReceiver extends BroadcastReceiver {

    public PackageChangedReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_PACKAGE_ADDED:
            case Intent.ACTION_PACKAGE_REMOVED:
                Intent updateServiceIntent = new Intent(context, UpdateService.class);
                context.startService(updateServiceIntent);
                break;
            default:
                Log.e("Tag Launcher", "Unknown action received");
                break;
        }
    }

}

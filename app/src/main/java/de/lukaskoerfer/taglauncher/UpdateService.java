package de.lukaskoerfer.taglauncher;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.lukaskoerfer.taglauncher.db.DbHelper;
import de.lukaskoerfer.taglauncher.model.InstalledApp;

public class UpdateService extends Service {

    public UpdateService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        UpdateTask Task = new UpdateTask();
        Task.execute();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class UpdateTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("Tag Launcher", "Started app update");
            PackageManager packageManager = UpdateService.this.getPackageManager();
            List<ApplicationInfo> applicationInfos = packageManager.getInstalledApplications(0);
            List<InstalledApp> installedApps = new ArrayList<>();
            for (ApplicationInfo appInfo : applicationInfos) {
                String appName = appInfo.loadLabel(packageManager).toString();
                installedApps.add(new InstalledApp(appInfo.packageName, appName));
            }
            (new DbHelper(UpdateService.this)).updateInstalledApps(installedApps);
            Log.d("Tag Launcher", "App update done");
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            UpdateService.this.stopSelf();
        }

        @Override
        protected void onCancelled() {
            UpdateService.this.stopSelf();
        }
    }
}
